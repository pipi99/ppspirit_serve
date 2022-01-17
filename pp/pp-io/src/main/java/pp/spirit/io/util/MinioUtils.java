package pp.spirit.io.util;

import com.google.common.collect.Lists;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pp.spirit.io.pojo.PPFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.io.util
 * @Description: minio 工具类
 * @date 2021.4.21  16:28
 * @email 453826286@qq.com
 */
@Component
public class MinioUtils {
    @Autowired
    private IoProp minioProp;

    private MinioClient client() {
        return MinioClient.builder()
                        .endpoint(minioProp.getEndpoint())
                        .credentials(minioProp.getAccessKey(), minioProp.getSecretKey())
                        .build();
    }


    /**
     * @Title: createBucket
     * @Description: (创建bucket)
     * [bucketName] 桶名
     */
    public void createBucket(String bucketName) throws Exception {
        boolean found =client().bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            client().makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());
        }
    }

    /**
     * @Author: LiV
     * @Date: 2021.2.3 14:11
     * @Description: 上传多个文件
     **/
    public List<PPFile> batchUpload(List<MultipartFile> files,String bucketName) throws Exception {
        List<PPFile> fileupload = Lists.newArrayList();
        for (int i = 0; i < files.size(); i++) {
            fileupload.add(this.uploadFile(files.get(i),bucketName));
        }
        return fileupload;
    }
    /**
     * @Title: uploadFile
     * @Description: (获取上传文件信息上传文件)
     * [file 上传文件（MultipartFile）, bucketName 桶名]
     */
    public PPFile uploadFile(MultipartFile file, String bucketName) throws Exception {
        //判断文件是否为空
        if (null == file || 0 == file.getSize()) {
            return null;
        }
        //判断存储桶是否存在  不存在则创建
        createBucket(bucketName);

        //数据库对象
        PPFile fileData = new PPFile();

        //设置文件所属模块
        fileData.setApp("default");
        fileData.setModel("default");

        // 设置文件名称
        fileData.setFileName(file.getOriginalFilename());
        fileData.setFileSize(file.getSize());
        fileData.setUploadTime(new Date());
        fileData.setFileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));


        //判断存储桶是否存在  不存在则创建
        createBucket(bucketName);

        //新的文件名 = 存储桶文件名_时间戳.后缀名
        String fileName = bucketName + "_" +getFileId();

        //实际id
        fileData.setFileId(fileName.replaceAll("/","_"));

        fileData.setMimeType(URLConnection.guessContentTypeFromName(file.getOriginalFilename()));
        //开始上传
        client().putObject(
                PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                        file.getInputStream(), -1, 10485760)
                        .contentType(file.getContentType())
                        .build());

        return fileData;
    }

    //存储id
    private String getFileId(){
        //年月时间路径
        String yyyyMM = DateUtil.getDate("yyyyMM");

        // 创建文件ID
        return yyyyMM+"/"+ UUID.randomUUID().toString();
    }

    //查询id
    private String getRealFileId(String fileId){
        return fileId.replaceAll("_","/").replaceFirst("/","_");
    }


    /**
     * @Title: getAllBuckets
     * @Description: (获取全部bucket)
     
     * []
     */
    public List<Bucket> getAllBuckets() throws Exception {
        return client().listBuckets();
    }

    /**
     * @param bucketName bucket名称
     * @Title: getBucket
     * @Description: (根据bucketName获取信息)
     
     * [bucketName] 桶名
     */
    public Optional<Bucket> getBucket(String bucketName) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        return client().listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
    }

    /**
     * @param bucketName bucket名称
     * @Title: removeBucket
     * @Description: (根据bucketName删除信息)
     
     * [bucketName] 桶名
     */
    public void removeBucket(String bucketName) throws Exception {
        client().removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @param expires    过期时间 <=7
     * @return url
     * @Title: getObjectURL
     * @Description: (获取 ⽂ 件外链)
     
     * [bucketName 桶名, objectName 文件名, expires 时间<=7]
     */
    public String getObjectUrl(String bucketName, String objectName, Integer expires) throws Exception {
        return client().getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(getRealFileId(objectName))
                        .expiry(expires)
                        .build());
    }

    /**
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @return ⼆进制流
     * @Title: getObject
     * @Description: (获取文件)
     
     * [bucketName 桶名, objectName 文件名]
     */
    public InputStream getObject(String bucketName, String objectName) throws Exception {
        return client().getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(getRealFileId(objectName))
                        .build());
    }

    /**
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @param stream     ⽂件流
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     * @Title: putObject
     * @Description: (上传文件)
     
     * [bucketName 桶名, objectName 文件名, stream ⽂件流]
     */
    public void putObject(String bucketName, String objectName, InputStream stream) throws
            Exception {
        client().putObject(
                PutObjectArgs.builder().bucket(bucketName).object(getRealFileId(objectName)).stream(
                        stream,  -1, 10485760)
                        .contentType("application/octet-stream")
                        .build());
    }

    /**
     * 上传⽂件
     *
     * @param bucketName  bucket名称
     * @param objectName  ⽂件名称
     * @param stream      ⽂件流
     * @param size        ⼤⼩
     * @param contextType 类型
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     * @Title: putObject
     * @Description: $(文件流上传文件)
     
     * [bucketName, objectName, stream, size, contextType]
     */
    public void putObject(String bucketName, String objectName, InputStream stream, long
            size, String contextType) throws Exception {
        client().putObject(
                PutObjectArgs.builder().bucket(bucketName).object(getRealFileId(objectName)).stream(
                        stream,  size, -1)
                        .contentType(contextType)
                        .build());
    }

    /**
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#statObject
     * @Title: getObjectInfo
     * @Description: (获取文件信息)
     
     * [bucketName, objectName]
     * @return
     */
    public StatObjectResponse getObjectInfo(String bucketName, String objectName) throws Exception {
        return client().statObject(
                StatObjectArgs.builder().bucket(bucketName).object(getRealFileId(objectName)).build());
    }

    /**
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @throws Exception https://docs.minio.io/cn/java-client-apireference.html#removeObject
     * @Title: removeObject
     * @Description: (删除文件)

     * [bucketName, objectName]
     */
    public void remove(String bucketName, String objectName) throws Exception {
        this.removeObject(bucketName,objectName);
    }
    /**
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @throws Exception https://docs.minio.io/cn/java-client-apireference.html#removeObject
     * @Title: removeObject
     * @Description: (删除文件)
     
     * [bucketName, objectName]
     */
    public void removeObject(String bucketName, String objectName) throws Exception {
        client().removeObject(
                RemoveObjectArgs.builder().bucket(bucketName).object(getRealFileId(objectName)).build());
    }
}
