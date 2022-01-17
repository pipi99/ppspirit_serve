package pp.spirit.io.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.io.pojo.PPFile;
import pp.spirit.io.pojo.PPFileQuery;
import pp.spirit.io.service.FileService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @author LiV
 * @Title:
 * @Package com.pp.api.io.util
 * @Description: 文件上传下载
 * @date 2021.4.22  09:05
 * @email 453826286@qq.com
 */
@Component
public class FileUpDownUtil {

    @Value("${pp.io.temp-dir}")
    private String tempDir;

    @Autowired
    private FileService fileService ;

    @Autowired
    private IoProp minioProp;

    @Autowired
    private FilesIO filesIO;

    @Autowired
    private MinioUtils minioUtils;

    private static FileUpDownUtil fileUpDownUtil;

    @PostConstruct
    public void  init(){
        fileUpDownUtil = this;
    }

    public static PPFile upload(MultipartFile file) throws Exception {
        PPFile fileData = null;
        if(fileUpDownUtil.minioProp.getType().equals("minio")){
            fileData = fileUpDownUtil.minioUtils.uploadFile(file,fileUpDownUtil.minioProp.getDefaultBucket());
        }else{
            fileData = fileUpDownUtil.filesIO.upload(file);
        }

        //文件信息入库
        fileUpDownUtil.fileService.save(fileData);
        return fileData;
    }

    public static List<PPFile> batchUpload(List<MultipartFile> files) throws Exception {
        List<PPFile> fileDatas = null;
        if(fileUpDownUtil.minioProp.getType().equals("minio")){
            fileDatas = fileUpDownUtil.minioUtils.batchUpload(files,fileUpDownUtil.minioProp.getDefaultBucket());
        }else{
            fileDatas = fileUpDownUtil.filesIO.batchUpload(files);
        }
        //文件信息入库
        fileUpDownUtil.fileService.getRepository().saveAll(fileDatas);

        return fileDatas;
    }

    /**
     * @Author: LiV
     * @Date: 2021.2.4 09:30
     * @Description: 预览文件
     **/
    public static void previewMedia(String fileId) throws Exception {
        PPFileQuery query = new PPFileQuery();
        query.setFileId(fileId);
        List<PPFile> list = fileUpDownUtil.fileService.list(query.getQueryWrapper());

        if (list.size()>0) {
            PPFile ppFile = list.get(0);
            HttpServletRequest request = ContextUtils.getRequest();
            HttpServletResponse response = ContextUtils.getResponse();

            //删除历史,创建今天
            String todayPath = fileUpDownUtil.handlerDir(fileUpDownUtil.tempDir+"/video-preview");
            File tempFile = new File(todayPath+"/"+ppFile.getFileName());
            if(!tempFile.exists()){
                tempFile.createNewFile();
                // 实现文件下载
                byte[] buffer = new byte[1024];
                BufferedInputStream bis = null;
                OutputStream os = null;
                try {
                    if(fileUpDownUtil.minioProp.getType().equals("minio")){
                        bis = new BufferedInputStream(fileUpDownUtil.minioUtils.getObject(fileUpDownUtil.minioProp.getDefaultBucket(),fileId));
                    }else{
                        bis = new BufferedInputStream(fileUpDownUtil.filesIO.getFile(fileId));
                    }
                    os = new FileOutputStream(tempFile);
                    int i = -1;
                    while ((i=bis.read(buffer)) != -1) {
                        os.write(buffer, 0, i);
                    }
                    os.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            //清空缓存
            response.resetBuffer();
            //获取响应的输出流
            OutputStream outputStream = null;
            RandomAccessFile targetFile = null;
            try {
                outputStream = response.getOutputStream();
                //创建随机读取文件对象
                targetFile = new RandomAccessFile(tempFile, "r");
                long fileLength = targetFile.length();
                //获取从那个字节开始读取文件
                String rangeString = request.getHeader("Range");
                if (rangeString != null) {//如果rangeString不为空，证明是播放视频发来的请求
                    long range = Long.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
                    //设置内容类型
                    response.setHeader("Content-Type", "video/mp4");
                    //设置此次相应返回的数据长度
                    response.setHeader("Content-Length", String.valueOf(fileLength - range));
                    //设置此次相应返回的数据范围
                    response.setHeader("Content-Range", "bytes "+range+"-"+(fileLength-1)+"/"+fileLength);
                    //返回码需要为206，而不是200
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    //设定文件读取开始位置（以字节为单位）
                    targetFile.seek(range);
                }else {
                    //设置响应头，把文件名字设置好
                    response.setHeader("Content-Disposition", "attachment; filename=" +ppFile.getFileName());
                    //设置文件长度
                    response.setHeader("Content-Length", String.valueOf(fileLength));
                    //解决编码问题
                    response.setHeader("Content-Type","application/octet-stream");

                }
                byte[] cache = new byte[1024 * 300];
                int flag;
                while ((flag = targetFile.read(cache))!=-1){
                    outputStream.write(cache, 0, flag);
                }
                outputStream.flush();
            } catch (Exception e) {
//                e.printStackTrace();
            } finally {
//                if (outputStream != null) {
//                    try {
//                        outputStream.close();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
                if (targetFile != null) {
                    try {
                        targetFile.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    private synchronized String handlerDir(String previewPath) throws Exception {
        File file  = new File(previewPath);
        String today = DateUtil.getDate("yyyy-MM-dd");

        String todayPath = previewPath+"/"+today;
        //创建今天的目录
        //检查目录
        this.createDir(todayPath);

        File[]  hisFiles = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return dir.isDirectory()&&(!today.equals(name));
            }
        });
        if(hisFiles!=null){
            for (int i = 0; i <hisFiles.length ; i++) {
                File his = hisFiles[i];
                FileUtils.forceDelete(his);
            }
        }
        return todayPath;
    }
    private synchronized void createDir(String previewPath){
        File file  = new File(previewPath);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    /**
     * @Author: LiV
     * @Date: 2021.2.4 09:30
     * @Description: 预览文件
     **/
    public static void preview(String fileId) throws UnsupportedEncodingException {

        download(fileId,null,"inline");
    }

    /**
     * @Author: LiV
     * @Date: 2021.2.4 09:30
     * @Description: 预览文件
     **/
    public static void download(String fileId) throws UnsupportedEncodingException {
        download(fileId,"application/octet-stream","attachment");
    }

    public static void download(String fileId,String mimeType,String Disposition) throws UnsupportedEncodingException {
        //获取文件路径
        PPFileQuery query = new PPFileQuery();
        query.setFileId(fileId);
        List<PPFile> list = fileUpDownUtil.fileService.list(query.getQueryWrapper());
        if (list.size()>0) {
            PPFile file  = list.get(0);

            HttpServletResponse response = ContextUtils.getResponse();
            HttpServletRequest request = ContextUtils.getRequest();

            if(mimeType == null){
                mimeType = file.getMimeType();
            }
            response.setHeader("Content-Type",mimeType);
            // 获取 user-agent 请求头
            String agent = request.getHeader("user-agent");
            // 下载文件能正常显示中文,响应头打开方式,inline/attachment为附件下载
            response.setHeader("Content-Disposition", ""+Disposition+";filename=" + getFileName(agent, file.getFileName()));
            response.addHeader("Content-Length", "" + file.getFileSize());
            response.setHeader("Last-Modified",new Date().toString());



            // 实现文件下载
            byte[] buffer = new byte[1024];
            BufferedInputStream bis = null;
            OutputStream os = null;
            try {
                if(fileUpDownUtil.minioProp.getType().equals("minio")){
                    bis = new BufferedInputStream(fileUpDownUtil.minioUtils.getObject(fileUpDownUtil.minioProp.getDefaultBucket(),fileId));
                }else{
                    bis = new BufferedInputStream(fileUpDownUtil.filesIO.getFile(fileId));
                }
                os = response.getOutputStream();
                int i = -1;
                while ((i=bis.read(buffer)) != -1) {
                    os.write(buffer, 0, i);
                }
                os.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                if (os != null) {
//                    try {
//                        os.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }
    }

    /**
     * @Author: LiV
     * @Date: 2021.4.28 21:00
     * @Description:  图片缩放
     **/
    public static void imageThumb(String fileId,int with,int height,float outputQuality) throws IOException {
        //获取文件路径
        PPFile file = FileUpDownUtil.getFile(fileId);
        ByteArrayInputStream in = new ByteArrayInputStream(file.getFileData());
        ImageUtil.imgThumb(in,ContextUtils.getResponse().getOutputStream(),with,height, outputQuality);
    }

    /**
     * @Author: LiV
     * @Date: 2021.4.28 21:00
     * @Description:  图片缩放
     **/
    public static void imageScale(String fileId,double scale,float outputQuality) throws IOException {
        //获取文件路径
        PPFile file = FileUpDownUtil.getFile(fileId);
        ByteArrayInputStream in = new ByteArrayInputStream(file.getFileData());
        ImageUtil.imgScale(in,ContextUtils.getResponse().getOutputStream(),scale, outputQuality);
    }

    private static String getFileName(String agent, String filename) throws UnsupportedEncodingException {
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            Base64 base64Encoder = new Base64();
            // 火狐浏览器
            filename = "=?utf-8?B?" + base64Encoder.encode(filename.getBytes("utf-8")) + "?=";
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    public static PPFile getFile(String fileId) throws UnsupportedEncodingException {
        PPFile file = null;
        PPFileQuery query = new PPFileQuery();
        query.setFileId(fileId);
        List<PPFile> list = fileUpDownUtil.fileService.list(query.getQueryWrapper());

        if (list.size()>0) {
            file = list.get(0);
            // 实现文件下载
            byte[] buffer = new byte[1024];
            BufferedInputStream bis = null;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                if(fileUpDownUtil.minioProp.getType().equals("minio")){
                    bis = new BufferedInputStream(fileUpDownUtil.minioUtils.getObject(fileUpDownUtil.minioProp.getDefaultBucket(),fileId));
                }else{
                    bis = new BufferedInputStream(fileUpDownUtil.filesIO.getFile(fileId));
                }
                int i = -1;
                while ((i=bis.read(buffer)) != -1) {
                    os.write(buffer, 0, i);
                }
                os.flush();
                file.setFileData(os.toByteArray());
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return file;
    }
    /**
     * @Author: LiV
     * @Date: 2021.2.4 11:04
     * @Description: 批量删除文件
     **/
    public static void removeBatch(List<String> fileIds) throws Exception {
        for (int i = 0; i < fileIds.size(); i++) {
            FileUpDownUtil.remove(fileIds.get(i));
        }
    }

    public static void remove(String fileId) throws Exception {
        //删除数据库数据
        PPFileQuery query = new PPFileQuery();
        query.setFileId(fileId);
        fileUpDownUtil.fileService.remove(query.getQueryWrapper());

        if(fileUpDownUtil.minioProp.getType().equals("minio")){
            fileUpDownUtil.minioUtils.remove(fileUpDownUtil.minioProp.getDefaultBucket(),fileId);
        }else{
            fileUpDownUtil.filesIO.remove(fileId);
        }
    }
}
