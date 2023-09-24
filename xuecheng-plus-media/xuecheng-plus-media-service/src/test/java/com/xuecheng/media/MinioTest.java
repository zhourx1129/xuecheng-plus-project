package com.xuecheng.media;


import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: zhourx
 * @Description:    测试minio的sdk
 * @Date: 2023/9/22
 */
public class MinioTest {
    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void test_upload() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //上传文件信息
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("testbucket") //确定桶
                .filename("C:\\Users\\zhour\\Pictures\\195054-1694692254c27f.jpg") //指定本地文件路径
                .object("test/01/1.jpg")   //对象名
                .build();
        //上传文件
        minioClient.uploadObject(uploadObjectArgs);
    }

    //删除文件
    @Test
    public void test_delete() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //上传文件信息
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket("testbucket") //确定桶
                .object("1.jpg")
                .build();
        //删除文件
        minioClient.removeObject(removeObjectArgs);
    }
    //查询文件
    @Test
    public void test_getFile() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, ServerException, InvalidKeyException {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket")
                .object("test/01/1.jpg")
                .build();
        FilterInputStream getObjectResponse = minioClient.getObject(getObjectArgs);
        FileOutputStream outputStream = new FileOutputStream(new File("D:/1.jpg"));
        IOUtils.copy(getObjectResponse,outputStream);
        String source_md5 = DigestUtils.md5DigestAsHex(getObjectResponse);
        String local_md5 = DigestUtils.md5DigestAsHex(new FileInputStream("d:/1.jpg"));
        if (local_md5.equals(source_md5)) {
            System.out.println("下载成功");
        }

    }
}
