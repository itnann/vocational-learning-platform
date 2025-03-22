package com.yaojiuye.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.media.mapper.MediaFilesMapper;
import com.yaojiuye.media.model.dto.QueryMediaParamsDto;
import com.yaojiuye.media.model.dto.UploadFileParamsDto;
import com.yaojiuye.media.model.dto.UploadFileResultDto;
import com.yaojiuye.media.model.po.MediaFiles;
import com.yaojiuye.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author itnan
 * @version 1.0
 * @description
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConfigurationProperties(prefix = "minio.bucket")
@Data//ConfigurationProperties需要反射注入
public class MediaFileServiceImpl implements MediaFileService {

    private final MinioClient minioClient;

    private final MediaFilesMapper mediaFilesMapper;

    //@Value("${minio.bucket.files}")
    private String files;
    //@Value("${minio.bucket.videofiles}")
    private String videofiles;

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
        MediaFileService proxy = (MediaFileService)AopContext.currentProxy();
        MediaFiles mediaFiles = proxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, files, objectName);
        //准备返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    /**
     * @description 将文件信息添加到文件表
     * @param companyId  机构id
     * @param fileMd5  文件md5值
     * @param uploadFileParamsDto  上传文件的信息
     * @param bucket  桶
     * @param objectName 对象名称
     * @return com.yaojiuye.media.model.po.MediaFiles
     * @author itnan
     */
    @Transactional//只有代理对象才有rollback命令,实例对象的事务注解是没有用的 非事务方法调用同类的一个事务方法,事务失效
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName){
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
                log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
                GlobalException.cast("保存文件信息失败");
            }
            log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());

        }
        return mediaFiles;

    }

    private String getMimeType(String extension){
        if(extension==null)
            extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    private String getDefaultFolderPath(){
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
     * @description 将文件写入minIO
     * @param localFilePath  文件地址
     * @param bucket  桶
     * @param objectName 对象名称
     * @return void
     * @author itnan
     */
    public boolean addMediaFilesToMinIO(String localFilePath,String mimeType,String bucket, String objectName) {
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket(bucket)//存储桶
                    .object(objectName)//文件名称可放在子目录下
                    .filename(localFilePath)//本地文件路径 ,临时文件传过来的
                    .contentType(mimeType)//设置媒体文件类型
                    .build();
            minioClient.uploadObject(testbucket);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}",bucket,objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}",bucket,objectName,e.getMessage(),e);
            GlobalException.cast("上传文件到文件系统失败");
        }
        return false;
    }
}
