package com.yaojiuye.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaojiuye.media.mapper.MediaFilesMapper;
import com.yaojiuye.media.mapper.MediaProcessHistoryMapper;
import com.yaojiuye.media.mapper.MediaProcessMapper;
import com.yaojiuye.media.model.po.MediaFiles;
import com.yaojiuye.media.model.po.MediaProcess;
import com.yaojiuye.media.model.po.MediaProcessHistory;
import com.yaojiuye.media.service.MediaFileProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author itnan
 * @ClassName MediaFileProcessServiceImpl
 * @Description 媒资文件处理业务方法
 * @Date 2025/3/29 17:01
 * @Version V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    private final MediaProcessMapper mediaProcessMapper;

    private final MediaFilesMapper mediaFilesMapper;

    private final MediaProcessHistoryMapper mediaProcessHistoryMapper;


    /**
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count      获取记录数
     * @return java.util.List<com.yaojiuye.media.model.po.MediaProcess>
     * @description 获取待处理任务
     * @author itnan
     */
    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        return mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    //实现如下
    public boolean startTask(long id) {
        int result = mediaProcessMapper.startTask(id);
        //三元运算表达式
        return result <= 0 ? false : true;
    }

    /**
     * @param taskId   任务id
     * @param status   任务状态
     * @param fileId   文件id
     * @param url      url
     * @param errorMsg 错误信息
     * @return void
     * @description 保存任务结果
     * @author itnan
     */
    @Transactional
    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if(mediaProcess == null)
                return;
        LambdaQueryWrapper<MediaProcess> queryWrapperById = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
        //任务处理失败
        if(status.equals("3")){
            MediaProcess mediaProcess_u = new MediaProcess();
            mediaProcess_u.setStatus("3");
            mediaProcess_u.setErrormsg(errorMsg);
            mediaProcess_u.setFailCount(mediaProcess.getFailCount()+1);
            mediaProcessMapper.update(mediaProcess_u,queryWrapperById);
            log.debug("更新任务处理状态为失败，任务信息:{}",mediaProcess_u);
            return ;
        }
        //任务处理成功 先跟新media_files
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        mediaFiles.setUrl(url);
        mediaFilesMapper.updateById(mediaFiles);
        //处理成功，更新url和状态
        mediaProcess.setUrl(url);
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcessMapper.updateById(mediaProcess);

        //添加到历史记录
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        //删除mediaProcess
        mediaProcessMapper.deleteById(mediaProcess.getId());
    }
}
