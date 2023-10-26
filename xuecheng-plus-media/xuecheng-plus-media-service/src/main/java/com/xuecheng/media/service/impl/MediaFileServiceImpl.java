package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.activation.MimeType;
import javax.management.ObjectName;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

 @Autowired
 MediaFileService currencyProxy;
  @Autowired
 MediaFilesMapper mediaFilesMapper;
  @Autowired
  MinioClient minioClient;
  @Autowired
 MediaProcessMapper mediaProcessMapper;
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
 public MediaFiles getMediaFiles(String  mediaId) {
  MediaFiles mediaFiles = mediaFilesMapper.selectById(mediaId);
  return mediaFiles;
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
  //记录待处理的任务
  addWaitTask(mediaFiles);
  //向mediaProcess插入记录
  return mediaFiles;
 }

 /**
  * 添加待处理任务
  * @param mediaFiles 媒体文件
  */
 public void addWaitTask(MediaFiles mediaFiles){
  //获取文件的mimetype
  String filename = mediaFiles.getFilename();
  String extension = filename.substring(filename.lastIndexOf("."));
  String mimeType = getMimeType(extension);
  //通过mimetype判断如果是avi视频才写入待处理任务
  if (mimeType.equals("video/x-msvideo")){
   MediaProcess mediaProcess = new MediaProcess();
   BeanUtils.copyProperties(mediaFiles,mediaProcess);
   //状态是未处理
   mediaProcess.setStatus("1");
   //上传时间
   mediaProcess.setCreateDate(LocalDateTime.now());
   //失败此数
   mediaProcess.setFailCount(0);
   mediaProcess.setUrl(null);
    mediaProcessMapper.insert(mediaProcess);
  }}

 /**
  * 检查文件是否存在
  *
  * @param fileMd5 文件md5
  * @return {@link RestResponse}<{@link Boolean}>
  */
 @Override
 public RestResponse<Boolean> checkFile(String fileMd5) {
  //先查询数据库
  MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
  if (mediaFiles != null) {
   //桶
   String bucket = mediaFiles.getBucket();
   //objectName
   String filePath = mediaFiles.getFilePath();
   //如果数据库存在再查询minio
   GetObjectArgs getObjectArgs = GetObjectArgs
           .builder()
           .bucket(bucket)
           .object(filePath)
           .build();
   try {
    FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
    if (inputStream!=null)
     //文件已经存在
     return RestResponse.success(true);
   } catch (Exception e) {
    e.printStackTrace();
   }
  }
  //文件不存在
  return RestResponse.success(false);
 }


 //得到分块文件目录
 private String getChunkFileFolderPath(String fileMd5){
  return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+"chunk"+"/";
 }

 //得到合并后的文件名
 private String getFilePathByMD5(String fileMd5,String fileExt){
  return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+fileMd5+fileExt;
 }

 /**
  * 清除分块文件
  * @param chunkFileFolderPath 区块文件文件夹路径
  * @param chunkTotal          区块合计
  */
 private void clearChunkFiles(String chunkFileFolderPath,int chunkTotal){
  Iterable<DeleteObject> objects = Stream.iterate(0, i -> ++i)
          .limit(chunkTotal)
          .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
          .collect(Collectors.toList());
  RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
          .bucket(bucket_video)
          .objects(objects)
          .build();
  Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
  //真正的删除
 results.forEach(f-> {
  try {
   DeleteError deleteError = f.get();
  } catch (Exception e) {
   e.printStackTrace();
  }
 });
 }
 /**
  * 检查分块文件是否存在
  *
  * @param fileMd5    文件md5
  * @return {@link RestResponse}<{@link Boolean}>
  */
 @Override
 public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
  //分块存储的路径是:md5前两位为两个目录，chunk存储分块文件
  String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
   //如果数据库存在再查询minio
   GetObjectArgs getObjectArgs = GetObjectArgs
           .builder()
           .bucket(bucket_video)
           .object(chunkFileFolderPath+chunkIndex)
           .build();
   try {
    FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
    if (inputStream!=null)
     //文件已经存在
     return RestResponse.success(true);
   } catch (Exception e) {
    e.printStackTrace();
   }
  //文件不存在
  return RestResponse.success(false);
 }

 @Override
 public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {
  String chunkFilePath = getChunkFileFolderPath(fileMd5) + chunk;
  //获取mimetype
  String mimeType = getMimeType(null);
  //将分块文件上传到minio
  boolean isSuccees = addMediaFilesToMinIO(localChunkFilePath, mimeType, bucket_video, chunkFilePath);
  if (!isSuccees) {
   log.debug("上传分块文件失败:{}", chunkFilePath);
   //上传失败
    return RestResponse.validfail(false,"上传失败");
  }
  //上传成功
  return RestResponse.success(true);
 }

 @Override
 public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
  //分块文件目录
  String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
  //=============找到所有的分块文件
  List<ComposeSource> sources = Stream.iterate(0, i -> ++i)
          .limit(chunkTotal)
          .map(i -> ComposeSource.builder()
                  .bucket(bucket_video)
                  .object(chunkFileFolderPath.concat(Integer.toString(i)))
                  .build())
          .collect(Collectors.toList());
  String filename = uploadFileParamsDto.getFilename();
  String extension = filename.substring(filename.lastIndexOf("."));
  //=============调用minio的sdk进行合并
  String objectName = getFilePathByMD5(fileMd5, extension);
  ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder().bucket(bucket_video).object(objectName).sources(sources).build();
  try {
   minioClient.composeObject(composeObjectArgs);
  } catch (Exception e) {
   e.printStackTrace();
   log.error("合并文件出错,bucket:{},object:{},错误信息:{}",bucket_video,objectName,e.getMessage());
   return RestResponse.validfail(false,"合并文件异常");
  }
  //===============验证合并后的文件与源文件是否一致，一致视频才上传成功
 //先下载合并后的文件
  File file = downloadFileFromMinio(bucket_video, objectName);

  try(FileInputStream fileInputStream = new FileInputStream(file);) {
   //计算合并后文件的md5
   String mergeFile_md5 = DigestUtils.md5Hex(fileInputStream);
   //比较原始的md5值和合并后的md5值
   if (!mergeFile_md5.equals(fileMd5)){
    log.error("校验合并md5值不一致，原始文件:{},合并后的文件:{}",fileMd5,mergeFile_md5);
    return RestResponse.validfail(false,"文件校验失败");
   }
   //文件大小
   uploadFileParamsDto.setFileSize(file.length());
  }catch (Exception e){
   return RestResponse.validfail(false,"文件校验失败");
  }
  //============将文件信息入库
  MediaFiles mediaFiles = currencyProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_video, objectName);
  if (mediaFiles == null) {
   return RestResponse.validfail(false,"文件入库失败");
  }

  //===============清理分块文件
  clearChunkFiles(chunkFileFolderPath,chunkTotal);
  return RestResponse.success(true);
 }
 /**
  * 从minio下载文件
  * @param bucket     桶
  * @param objectName 对象名称
  * @return {@link File}
  */
 public File downloadFileFromMinio(String bucket,String objectName){
  //临时文件
  File minioFile = null;
  FileOutputStream outputStream = null;
  try {
   GetObjectResponse stream = minioClient.getObject(GetObjectArgs
           .builder()
           .bucket(bucket)
           .object(objectName)
           .build());
   //创建临时文件
   minioFile = File.createTempFile("minio", ".merge");
   outputStream = new FileOutputStream(minioFile);
   IOUtils.copy(stream,outputStream);
   return minioFile;
  } catch (Exception e) {
   e.printStackTrace();
  }finally {
   if (outputStream!=null){
    try {
     outputStream.close();
    } catch (IOException e) {
     e.printStackTrace();
    }
   }
  }

  return null;
 }

 //@Transactional
 @Override
 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath,String objectName) {
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
  if(StringUtils.isEmpty(objectName)){
       objectName = defaultFolderPath+fileMd5+extension;
  }
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
