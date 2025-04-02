package com.yaojiuye.media.api;

import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.base.model.RestResponse;
import com.yaojiuye.media.model.po.MediaFiles;
import com.yaojiuye.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author itnan
 * @ClassName MediaOpenController
 * @Description 媒资文件管理接口
 * @Date 2025/4/2 10:18
 * @Version V1.0
 */

@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
public class MediaOpenController {

    private final MediaFileService mediaFileService;

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){

        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        if(mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())){
            GlobalException.cast("视频还没有转码处理");
        }
        return RestResponse.success(mediaFiles.getUrl());

    }

}
