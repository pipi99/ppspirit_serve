package pp.spirit.io.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.io.pojo.PPFile;

import java.io.*;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.io
 * @Description: 文件存储工具包
 * @date 2021.2.3  11:00
 * @email 453826286@qq.com
 */
@Slf4j
@Component
public class FilesIO {
    /**
     * @Author: LiV
     * @Date: 2021.2.3 14:11
     * @Description: 上传单个文件
     **/
    public PPFile upload(MultipartFile file) throws IOException {
       return this.upload(file,"default","default");
    }
    /**
     * @Author: LiV
     * @Date: 2021.2.3 14:11
     * @Description: 上传单个文件
     **/
    public  PPFile upload(MultipartFile file,String app,String module) throws IOException {

        //数据库对象
        PPFile fileData = new PPFile();
        if (file != null && 0 != file.getSize()) {

            //设置文件所属模块
            fileData.setApp(app);
            fileData.setModel(module);

            // 设置文件名称
            fileData.setFileName(file.getOriginalFilename());
            fileData.setFileSize(file.getSize());
            fileData.setUploadTime(new Date());
            fileData.setFileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
            fileData.setMimeType(URLConnection.guessContentTypeFromName(file.getOriginalFilename()));
            String fileId = getFileId();
            fileData.setFileId(fileId.replaceAll("/","_"));

            //文件的存储路径
            File dir = new File(ContextUtils.getSession().getServletContext().getRealPath("/")+"spfiles/"+fileId.split("/")[0]);
            if(!dir.exists()){
                dir.mkdirs();
            }

            //文件对象
            File uploadFile = new File(ContextUtils.getSession().getServletContext().getRealPath("/")+"spfiles/"+fileId);


            // 文件保存
            file.transferTo(uploadFile);

        } else {
            return null;
        }
        return fileData;
    }

    private String getFileId(){
        //年月时间路径
        String yyyyMM = DateUtil.getDate("yyyyMM");

        // 创建文件ID
        return yyyyMM+"/"+UUID.randomUUID().toString();
    }
    //查询id
    private String getRealFileId(String fileId){
        return fileId.replaceAll("_","/");
    }
    /**
     * @Author: LiV
     * @Date: 2021.2.3 14:11
     * @Description: 上传多个文件
     **/
    public List<PPFile> batchUpload(List<MultipartFile> files) throws IOException {
        return this.batchUpload(files,"default","default");
    }

    /**
     * @Author: LiV
     * @Date: 2021.2.3 14:11
     * @Description: 上传多个文件
     **/
    public List<PPFile> batchUpload(List<MultipartFile> files,String app,String module) throws IOException {
        List<PPFile> fileupload = Lists.newArrayList();
        for (int i = 0; i < files.size(); i++) {
            fileupload.add(this.upload(files.get(i),app,module));
        }
        return fileupload;
    }

    /**
     * @Author: LiV
     * @Date: 2021.2.4 09:30
     * @Description: 获取文件
     **/
    public  InputStream getFile(String fileId) throws UnsupportedEncodingException {
            // 实现文件下载
        BufferedInputStream bis = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            bis = new BufferedInputStream(new FileInputStream(new File(ContextUtils.getSession().getServletContext().getRealPath("/")+"spfiles/"+getRealFileId( fileId))));
        } catch (Exception e) {
            log.debug(e.getMessage(),e);
        }
        return bis;
    }


    /**
     * @Author: LiV
     * @Date: 2021.2.4 11:04
     * @Description: 删除文件
     **/
    public  String getFilePath(String fileId){
       return ContextUtils.getSession().getServletContext().getRealPath("/")+"spfiles/" + getRealFileId( fileId);
    }

    /**
     * @Author: LiV
     * @Date: 2021.2.4 11:04
     * @Description: 删除文件
     **/
    public  void remove(String fileId){
        //删除磁盘目录
        File file = new File(ContextUtils.getSession().getServletContext().getRealPath("/")+"spfiles/"+getRealFileId( fileId));
        if(file.exists()){
            file.delete();
        }
    }
}
