package com.yaojiuye.media.service;

import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.base.model.RestResponse;
import com.yaojiuye.media.model.dto.QueryMediaParamsDto;
import com.yaojiuye.media.model.dto.UploadFileParamsDto;
import com.yaojiuye.media.model.dto.UploadFileResultDto;
import com.yaojiuye.media.model.po.MediaFiles;

/**
 * @author itnan
 * @version 1.0
 * @description 媒资文件管理业务类
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.yaojiuye.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author itnan
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     * @param companyId 机构id
     * @param uploadFileParamsDto 上传文件信息
     * @param localFilePath 文件磁盘路径
     * @return 文件信息
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);

    /**
     * 将文件信息添加到数据库
     * @param companyId
     * @param fileMd5
     * @param uploadFileParamsDto
     * @param bucket
     * @param objectName
     * @return
     */
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName);


    /**
     * @description 检查文件是否存在
     * @param fileMd5 文件的md5
     * @return com.yaojiuye.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @author itnan
     */
    public RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * @description 检查分块是否存在
     * @param fileMd5  文件的md5
     * @param chunkIndex  分块序号
     * @return com.yaojiuye.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @author itnan
     */
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * @description 上传分块
     * @param fileMd5  文件md5
     * @param chunk  分块序号
     * @param localChunkFilePath  分块文件本地路径
     * @return com.yaojiuye.base.model.RestResponse
     * @author itnan
     */
    public RestResponse uploadChunk(String fileMd5,int chunk,String localChunkFilePath);

    /**
     * @description 合并分块
     * @param companyId  机构id
     * @param fileMd5  文件md5
     * @param chunkTotal 分块总和
     * @param uploadFileParamsDto 文件信息
     * @return com.yaojiuye.base.model.RestResponse
     * @author itnan
     */
    public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);
}
