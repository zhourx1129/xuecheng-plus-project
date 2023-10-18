package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.util.List;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
  * @author Mr.M
  * @date 2022/9/10 8:57
 */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);
    /**
     * 检查文件是否存在
     * @param fileMd5 文件md5
     * @return {@link RestResponse}<{@link Boolean}>
     */
    public RestResponse<Boolean> checkFile(String fileMd5);

    MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucketMediafiles, String objectName);

    /**
  * 上传文件
  *
  * @param companyId           机构id
  * @param uploadFileParamsDto 文件信息
  * @param localFilePath       本地文件路径
  * @return {@link UploadFileResultDto}
  */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);


    /**
     * 检查分块是否存在
     *
     * @param fileMd5    文件md5
     * @param chunkIndex 区块索引
     * @return {@link RestResponse}<{@link Boolean}>
     */
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

   /**
    * 上传区块
    * @param fileMd5            文件md5
    * @param chunk              分块序号
    * @param localChunkFilePath 本地分块文件路径
    * @return {@link RestResponse}
    */
   public RestResponse uploadChunk(String fileMd5,int chunk,String localChunkFilePath);

    /**
     *  合并分块
     *
     * @param companyId           机构id
     * @param fileMd5             文件md5
     * @param chunkTotal          分块合计
     * @param uploadFileParamsDto 文件信息
     * @return {@link RestResponse}
     */
    public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);
    public File downloadFileFromMinio(String bucket, String objectName);

    /**
     * 将文件上传到minio
     *
     * @param localFilePath 本地文件路径
     * @param mimeType      媒体类型
     * @param bucket        桶
     * @param objectName    对象名称
     * @return boolean
     */
    public boolean addMediaFilesToMinIO(String localFilePath,String mimeType,String bucket, String objectName);
}
