package com.yaojiuye.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.base.model.RestResponse;
import com.yaojiuye.media.mapper.MediaFilesMapper;
import com.yaojiuye.media.mapper.MediaProcessMapper;
import com.yaojiuye.media.model.dto.QueryMediaParamsDto;
import com.yaojiuye.media.model.dto.UploadFileParamsDto;
import com.yaojiuye.media.model.dto.UploadFileResultDto;
import com.yaojiuye.media.model.po.MediaFiles;
import com.yaojiuye.media.model.po.MediaProcess;
import com.yaojiuye.media.service.MediaFileService;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author itnan
 * @version 1.0
 * @description minio的默认分块大戏为5mb,springboot种tomcat的默认最大请求体为1mb,所以这里需要设分块大小为5mb
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConfigurationProperties(prefix = "minio.bucket")
@Data//ConfigurationProperties需要反射注入
public class MediaFileServiceImpl implements MediaFileService {

    private final MinioClient minioClient;

    private final MediaFilesMapper mediaFilesMapper;

    private final MediaProcessMapper mediaProcessMapper;

    //@Value("${minio.bucket.files}")
    private String files;
    //@Value("${minio.bucket.videofiles}")
    private String videofiles;

    @Qualifier("ClearChunkFilesTaskExecutor")//指定bean的具体名称
    private final Executor taskExecutor;
    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = pageParams.toMpPage();
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        //po转po
        PageResult<MediaFiles> mediaListResult = PageResult.po2Po(pageResult);
        return mediaListResult;

    }


    /**
     * 上传文件
     *
     * @param companyId           机构id
     * @param uploadFileParamsDto 上传文件信息
     * @param localFilePath       文件磁盘路径
     * @return 文件信息
     */
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        //文件名
        String filename = uploadFileParamsDto.getFilename();
        //拿到扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //根据扩展名取出mimeType
        String mimeType = getMimeType(extension);
        // 将文件上传到minio
        String fileMd5 = getFileMd5(new File(localFilePath));//临时文件为了获取文件的md5值
        String objectName = getDefaultFolderPath() + fileMd5 + extension;
        boolean result = addMediaFilesToMinIO(localFilePath, mimeType, files, objectName);
        if (!result) {
            GlobalException.cast("上传文件失败");
        }

        //将文件信息保存到数据库
        MediaFileService proxy = (MediaFileService) AopContext.currentProxy();
        MediaFiles mediaFiles = proxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, files, objectName);
        //准备返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    /**
     * @param companyId           机构id
     * @param fileMd5             文件md5值
     * @param uploadFileParamsDto 上传文件的信息
     * @param bucket              桶
     * @param objectName          对象名称
     * @return com.yaojiuye.media.model.po.MediaFiles
     * @description 将文件信息添加到文件表
     * @author itnan
     */
    @Transactional//只有代理对象才有rollback命令,实例对象的事务注解是没有用的 非事务方法调用同类的一个事务方法,事务失效
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);//数据库没有查到为null
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert <= 0) {
                log.error("保存文件信息到数据库失败,{}", mediaFiles.toString());
                GlobalException.cast("保存文件信息失败");
            }
            //添加到待处理任务表 事务方法调用非事务方法受事务控制
            addWaitingTask(mediaFiles);
            log.debug("保存文件信息到数据库成功,{}", mediaFiles.toString());


        }
        return mediaFiles;
    }

    /**
     * 添加待处理任务
     * @param mediaFiles 媒资文件信息
     */
    private void addWaitingTask(MediaFiles mediaFiles){
        String filename = mediaFiles.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        String mimeType = getMimeType(extension);
        //如果是avi视频添加到视频待处理表
        if(mimeType.equals("video/x-msvideo")){
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles,mediaProcess, "id", "url");
            mediaProcess.setStatus("1");//状态是未未处理
            mediaProcess.setCreateDate(LocalDateTime.now());
            mediaProcess.setFailCount(0);//失败次数默认为0
            mediaProcessMapper.insert(mediaProcess);
        }
    }

    private String getMimeType(String extension) {
        if (extension == null)
            extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//未知流的类型
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    private String getDefaultFolderPath() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/";
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

    /**
     * @param localFilePath 文件地址
     * @param bucket        桶
     * @param objectName    对象名称
     * @return void
     * @description 将文件写入minIO
     * @author itnan
     */
    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket(bucket)//存储桶
                    .object(objectName)//文件名称可放在子目录下
                    .filename(localFilePath)//本地文件路径 ,临时文件传过来的
                    .contentType(mimeType)//设置媒体文件类型
                    .build();
            minioClient.uploadObject(testbucket);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}", bucket, objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}", bucket, objectName, e.getMessage(), e);
            GlobalException.cast("上传文件到文件系统失败");
        }
        return false;
    }

    /**
     * @param fileMd5 文件的md5
     * @return com.yaojiuye.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @description 检查文件是否存在
     * @author itnan
     */
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //数据库存在minio可能不存在,但是数据库不存在,minio一定不存在
        //先查询数据库
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //桶
            String bucket = mediaFiles.getBucket();
            //存储目录
            String filePath = mediaFiles.getFilePath();
            //文件流
            InputStream stream = null;
            try {
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build());
                if (stream != null) {
                    //文件已存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //文件不存在
        return RestResponse.success(false);
    }

    /**
     * @param fileMd5    文件的md5
     * @param chunkIndex 分块序号
     * @return com.yaojiuye.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @description 检查分块是否存在
     * @author itnan
     */
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        //得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        //文件流
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(videofiles)
                            .object(chunkFilePath)
                            .build());
            if (fileInputStream != null) {
                //分块已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //分块未存在
        return RestResponse.success(false);
    }

    //得到分块文件的目录

    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    /**
     * @param fileMd5            文件md5
     * @param chunk              分块序号
     * @param localChunkFilePath 分块文件本地路径
     * @return com.yaojiuye.base.model.RestResponse
     * @description 上传分块
     * @author itnan
     */
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {
        //将分块文件上传到minio
        String mimeType = getMimeType(null);
        String objectName = getChunkFileFolderPath(fileMd5) + chunk;
        boolean b = addMediaFilesToMinIO(localChunkFilePath, mimeType, videofiles, objectName);
        if (!b) {
            //上传失败
            return RestResponse.validfail(false, "上传分块文件出错");
        }
        return RestResponse.success(true);
    }

    /**
     * @param companyId           机构id
     * @param fileMd5             文件md5
     * @param chunkTotal          分块总和
     * @param uploadFileParamsDto 文件信息
     * @return com.yaojiuye.base.model.RestResponse
     * @description 合并分块
     * @author itnan
     */
    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //找到分块文件调用minio的sdk进行合并
        List<ComposeSource> composeSourceList = Stream.iterate(0, i -> ++i)//只能++i,不能i++ Stream.iterate(initial value, next value) 返回next value一定是++i
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(videofiles)
                        .object(getChunkFileFolderPath(fileMd5) + i)
                        .build())
                .collect(Collectors.toList());
        String extension = uploadFileParamsDto.getFilename().substring(uploadFileParamsDto.getFilename().lastIndexOf("."));
        String objectName = getFilePathByMd5(fileMd5, extension);
        //报错 size 1048576 must be greater than 5242880, minio的默认分块大小为5mb
        try {
            ObjectWriteResponse response = minioClient.composeObject(ComposeObjectArgs.builder()
                    .bucket(videofiles)
                    .object(objectName)//合并后文件所在的目录
                    .sources(composeSourceList)
                    .build());
            log.debug("合并文件成功,response={}", objectName);
        } catch (Exception e) {
            log.error("合并文件失败,bucketName:{},objectName:{},错误:{}", videofiles, objectName, e.getMessage());
            return RestResponse.validfail(false, "合并文件失败。");

        }
        //校验合并后的文件和源文件是否一致,将视频上传成功 ,需要将文件临时下载到本地
        File minioFile = downloadFileFromMinIO(videofiles, objectName);
        if (minioFile == null) {
            log.debug("下载合并后文件失败,path:{}", videofiles + "/" + objectName);
            return RestResponse.validfail(false, "下载合并后文件失败。");
        }
        try (FileInputStream newFileInputStream = new FileInputStream(minioFile)) {
            String md5Hex = DigestUtils.md5Hex(newFileInputStream);
            if (!StringUtils.equals(md5Hex, fileMd5)) {
                log.error("合并文件校验md5值不一致,原始文件md5:{},合并文件md5:{}", fileMd5, md5Hex);
                return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
            }
            //文件大小
            uploadFileParamsDto.setFileSize(minioFile.length());
        } catch (Exception e) {
            return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
        }
        //将文件信息入库
        MediaFileService proxy = (MediaFileService) AopContext.currentProxy();
        proxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, videofiles, objectName);
        //开启一个独立的线程去清理分块文件
        //clearChunkFiles(getChunkFileFolderPath(fileMd5), chunkTotal);
        //taskExecutor.execute(new ClearChunkFilesTask(getChunkFileFolderPath(fileMd5), chunkTotal));
        taskExecutor.execute(() -> clearChunkFiles(getChunkFileFolderPath(fileMd5), chunkTotal));


        return RestResponse.success(true);
    }

    /**
     * 得到合并后的文件的地址
     *
     * @param fileMd5 文件id即md5值
     * @param fileExt 文件扩展名
     * @return
     */
    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

    /**
     * 从minio下载文件
     *
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    public File downloadFileFromMinIO(String bucket, String objectName) {
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
           InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //创建临时文件
            minioFile = File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 创建一个线程让他取处理 清除分块文件
     * @param chunkFileFolderPath 分块文件路径
     * @param chunkTotal 分块文件总数
     */
    private void clearChunkFiles(String chunkFileFolderPath,int chunkTotal) {

        try {
            // TODO 断点续传后分块无法删除
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket(videofiles).objects(deleteObjects).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            //需要遍历一下才算真正的删除
            for (Result<DeleteError> r : results) {
                try {
                    DeleteError deleteError = r.get();
                    if (deleteError != null) {
                        log.error("清除分块文件失败,objectname:{}", deleteError.objectName());
                    }
                } catch (Exception e) {
                    log.error("清除分块文件时发生异常", e);
                }
            }
            log.info("清除分块文件成功,chunkFileFolderPath:{}", chunkFileFolderPath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清除分块文件失败,chunkFileFolderPath:{}", chunkFileFolderPath, e);
        }
    }

   /* private class ClearChunkFilesTask implements Runnable {
        private String chunkFileFolderPath;
        private int chunkTotal;

        public ClearChunkFilesTask(String chunkFileFolderPath, int chunkTotal) {
            this.chunkFileFolderPath = chunkFileFolderPath;
            this.chunkTotal = chunkTotal;
        }

        @Override
        public void run() {
            clearChunkFiles(chunkFileFolderPath, chunkTotal);
        }
    }*/
}
