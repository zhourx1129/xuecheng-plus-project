package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.activation.MimeType;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @description
 * @author Mr.M
 * @date 2022/9/10 8:58
 * @version 1.0
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

 @Autowired
 MediaFileService currencyProxy;
  @Autowired
 MediaFilesMapper mediaFilesMapper;
  @Autowired
  MinioClient minioClient;
  //存储普通文件
  @Value("${minio.bucket.files}")
  private String bucket_mediafiles;
  //存储视频文件
  @Value("${minio.bucket.videofiles}")
  private String bucket_video;

 //获取文件默认存储目录路径 年/月/日
 private String getDefaultFolderPath() {
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  String folder = sdf.format(new Date()).replace("-", "/")+"/";
  return folder;
 }
 //获取文件的md5
 private String getFileMd5(File file) {
  try (FileInputStream fileInputStream = new FileInputStream(file)) {
   String fileMd5 = DigestUtils.md5Hex(fileInputStream);
   return fileMd5;
  } catch (Exception e) {
   e.printStackTrace();
   return null;
  }
 }



 @Override
 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

  //构建查询条件对象
  LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
  
  //分页对象
  Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
  // 查询数据内容获得结果
  Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
  // 获取数据列表
  List<MediaFiles> list = pageResult.getRecords();
  // 获取数据总数
  long total = pageResult.getTotal();
  // 构建结果集
  PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
  return mediaListResult;

 }

 //根据扩展名获取mineType
 private String getMimeType(String extension){
  if (extension == null) {
   extension="";
  }
  //根据扩展名取出mimeType
  ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
  String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
  if (extensionMatch != null) {
   mimeType = extensionMatch.getMimeType();
  }
  return mimeType;
 }

 /**
  * 将文件上传到minio
  *
  * @param localFilePath 本地文件路径
  * @param mimeType      媒体类型
  * @param bucket        桶
  * @param objectName    对象名称
  * @return boolean
  */
 public boolean addMediaFilesToMinIO(String localFilePath,String mimeType,String bucket, String objectName){
  //上传文件的参数信息
  try {
   UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
           .bucket(bucket)
           .filename(localFilePath)
           .object(objectName)
           .contentType(mimeType)
           .build();
   minioClient.uploadObject(uploadObjectArgs);
   log.info("上传文件到minio成功");
   return true;
  } catch (Exception e) {
    e.printStackTrace();
    log.error("上传文件出错,bucket:{},objectName:{},错误信息:{}",bucket,objectName,e.getMessage());
  }
  return false;
 }

 /**
  * @description 将文件信息添加到文件表
  * @param companyId  机构id
  * @param fileMd5  文件md5值
  * @param uploadFileParamsDto  上传文件的信息
  * @param bucket  桶
  * @param objectName 对象名称
  * @return com.xuecheng.media.model.po.MediaFiles
  * @author Mr.M
  * @date 2022/10/12 21:22
  */
 @Transactional
 public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName) {
  MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
  if (mediaFiles == null) {
   mediaFiles = new MediaFiles();
   BeanUtils.copyProperties(uploadFileParamsDto,mediaFiles);
   //文件id
   mediaFiles.setId(fileMd5);
   //机构id
   mediaFiles.setCompanyId(companyId);
   //桶
   mediaFiles.setBucket(bucket);
   //file_path
   mediaFiles.setFilePath(objectName);
   //file_id
   mediaFiles.setFileId(fileMd5);
   //url
   mediaFiles.setUrl("/"+bucket+"/"+objectName);
   //上传时间
   mediaFiles.setCreateDate(LocalDateTime.now());
   //状态
   mediaFiles.setStatus("1");
   //审核状态
   mediaFiles.setAuditStatus("002003");
   //插入数据库
   int insert = mediaFilesMapper.insert(mediaFiles);
   if (insert<=0){
    log.debug("向数据库保存文件信息失败，bucket:{},objectName:{}",bucket,objectName);
    return null;
   }
  }
  return mediaFiles;
 }
  //@Transactional
 @Override
 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
  //文件名
  String filename = uploadFileParamsDto.getFilename();
  //先得到扩展名
  String extension = filename.substring(filename.lastIndexOf("."));
  //得到mimetype
  String mimeType = getMimeType(extension);
  //子目录
  String defaultFolderPath = getDefaultFolderPath();
  //文件的md5
  String fileMd5 = getFileMd5(new File(localFilePath));
  String objectName = defaultFolderPath+fileMd5+extension;
  //上传文件到minio
  boolean result = addMediaFilesToMinIO(localFilePath, mimeType, bucket_mediafiles, objectName);
  if (!result) {
   XueChengPlusException.cast("上传文件失败");
  }
  //将文件信息保存到数据库
   MediaFiles mediaFiles = currencyProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_mediafiles, objectName);
  if (mediaFiles==null){
   XueChengPlusException.cast("文件上传失败");
  }
   UploadFileResultDto fileParamsDto = new UploadFileResultDto();
   BeanUtils.copyProperties(mediaFiles,fileParamsDto);
   return fileParamsDto;
 }
}
