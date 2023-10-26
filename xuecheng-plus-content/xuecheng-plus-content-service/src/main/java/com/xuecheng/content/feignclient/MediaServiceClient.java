package com.xuecheng.content.feignclient;

import com.xuecheng.content.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: zhourx
 * @Description:    远程调用媒资服务的接口
 * @Date: 2023/10/23
 */
@Component
@FeignClient(value = "media-api",configuration = {MultipartSupportConfig.class})
public interface MediaServiceClient {
    @RequestMapping(value = "/media/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestPart("filedata") MultipartFile upload,
                      @RequestParam(value = "objectName",required=false) String objectName)throws IOException;

}
