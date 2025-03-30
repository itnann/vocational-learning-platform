package com.yaojiuye.media.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yaojiuye.base.utils.Mp4VideoUtil;
import com.yaojiuye.media.model.po.MediaProcess;
import com.yaojiuye.media.service.MediaFileProcessService;
import com.yaojiuye.media.service.MediaFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 视频处理任务类
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VideoTask {

    //private static Logger logger = LoggerFactory.getLogger(VideoTask.class); 日志记录器 @Slf4j
    private final MediaFileProcessService mediaFileProcessService;

    private final MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpeg_path;

    /**
     * 2、分片广播任务
     */
    @XxlJob("videoJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        //确定cpu的核数
        int cpuCore = Runtime.getRuntime().availableProcessors();

        //查询出待处理的任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, cpuCore);
        //创建线程池
        int size = mediaProcessList.size();
        log.debug("取到的任务总数: {}", size);
        if (size <= 0) {
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        //使用计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        //一个任务对应一个线程
        mediaProcessList.forEach(mediaProcess -> {
            executorService.execute(() -> {
                try{
                //文件的id就是md5值
                String fileId = mediaProcess.getFileId();
                //获取任务id
                long taskId = mediaProcess.getId();
                //开始处理任务
                boolean startTask = mediaFileProcessService.startTask(taskId);
                if (!startTask) {
                    log.debug("抢占任务失败,任务Id:{}", taskId);
                    return;
                }
                //执行视频的转码
                //源avi视频的路径
                String bucket = mediaProcess.getBucket();
                String objectName = mediaProcess.getFilePath();
                File file = mediaFileService.downloadFileFromMinIO(bucket, objectName);
                if (file == null) {
                    log.debug("下载视频出错,文件路径: {}", bucket.concat(objectName));
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载视频到本地失败");
                    return;
                }
                //下载到本地服务器的文件路径
                String video_path = file.getAbsolutePath();

                //转换后mp4文件的名称
                String mp4_name = fileId.concat(".mp4");
                //转换后mp4文件的路径 mp4_path+mp4_name
                File mp4File = null;
                try {
                    mp4File = File.createTempFile("minio", ".mp4");
                } catch (IOException e) {
                    log.debug("创建临时文件失败", e.getMessage());
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件异常");
                }
                //等到mp4_file所在目录
                String mp4_path = mp4File.getParent() + "/";
                //创建工具类对象
                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4_path);
                //开始视频转换，成功将返回success
                String result = videoUtil.generateMp4();
                if (!result.equals("success")) {
                    log.debug("视频文件转换失败,视频文件路径:{}", mp4_path);
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, result);
                    return;
                }
                String minio_path = objectName.replace(objectName.substring(objectName.lastIndexOf(".")), ".mp4");
                boolean uploadFile = mediaFileService.addMediaFilesToMinIO(mp4_path + mp4_name, "video/mp4", bucket, minio_path);
                if (!uploadFile) {
                    log.debug("上传文件失败,文件路径:{}", mp4_path + mp4_name);
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "上传文件失败");
                }
                mediaFileProcessService.saveProcessFinishStatus(taskId, "2", fileId, bucket + "/" + minio_path, null);

            }finally {
                    countDownLatch.countDown();
                }
            });
        });
        //阻塞最多等待30分钟
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

}
