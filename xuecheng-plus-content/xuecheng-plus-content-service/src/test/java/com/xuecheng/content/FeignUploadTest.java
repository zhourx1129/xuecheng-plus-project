package com.xuecheng.content;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @Author: zhourx
 * @Description:    测试远程调用
 * @Date: 2023/10/23
 */
@SpringBootTest
public class FeignUploadTest {
    @Autowired
    MediaServiceClient mediaServiceClient;
    @Test
    public void test() throws IOException {
        //将file转成multiplefile
        File file = new File("E:\\data\\120.html");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        mediaServiceClient.uploadFile(multipartFile,"course/2.html");
    }
}
