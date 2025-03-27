package com.yaojiuye.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author itnan
 * @ClassName BigFileTest
 * @Description 大文件分块和合并
 * @Date 2025/3/22 16:15
 * @Version V1.0
 */
public class BigFileTest {

    /**
     * 测试大文件分块
     * @throws Exception
     */
    @Test
    public void testChunk() throws Exception {
        File sourceFile = new File("G:/java进阶/2、黑马程序员Java项目《学成在线》企业级开发实战/学成在线项目—视频/day01 项目介绍&环境搭建/Day1-00.项目导学.mp4");
        String ChunkPath = "G:/java进阶/2、黑马程序员Java项目《学成在线》企业级开发实战/学成在线项目—视频/day01 项目介绍&环境搭建/chunk/";
        int chunkSize = 1024 * 1024 * 1; // 1MB
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        //向原始文件读的流
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        for(int i = 0; i < chunkNum; i++){
            File chunkFile = new File(ChunkPath + i);
            //向合块写的流
            RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw");
            byte[] buffer = new byte[1024];
            int len = -1;
            while((len = raf_read.read(buffer)) != -1){
                raf_write.write(buffer, 0, len);
                //该块读完后结束循环
                if(chunkFile.length() >=  chunkSize){
                    break;
                }
            }
            raf_write.close();
        }
        raf_read.close();
    }

    @Test
    public void testMerge() throws Exception {
        File sourceFile = new File("G:/java进阶/2、黑马程序员Java项目《学成在线》企业级开发实战/学成在线项目—视频/day01 项目介绍&环境搭建/Day1-00.项目导学.mp4");
        String mergeFilePath = "G:/java进阶/2、黑马程序员Java项目《学成在线》企业级开发实战/学成在线项目—视频/day01 项目介绍&环境搭建/Day1-00.项目导学_2.mp4";
        File mergeFile = new File(mergeFilePath);
        String ChunkPath = "G:/java进阶/2、黑马程序员Java项目《学成在线》企业级开发实战/学成在线项目—视频/day01 项目介绍&环境搭建/chunk/";
        //一块一块读合并到一起 ,先排序 从0开始
        File file = new File(ChunkPath);
        File[] files = file.listFiles();
        List<File> fileList = Arrays.asList(files);
//        Collections.sort(fileList,(o1, o2) -> {
//            // o1-o2升序，o2-o1降序 0 -? 需要返回升序
//            return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
//        });
        // o1-o2升序，o2-o1降序 0 -? 需要返回升序
        Collections.sort(fileList,(o1, o2) -> Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName()));
        //向合并块写的流
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        fileList.forEach(file1 -> {
            byte[] buffer = new byte[1024];
            try {
                //向合并块读的流
                RandomAccessFile raf_read = new RandomAccessFile(file1, "r");
                int len = -1;
                while((len = raf_read.read(buffer)) != -1){
                    raf_write.write(buffer, 0, len);
                }
                raf_read.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        raf_write.close();
        String source = DigestUtils.md5Hex(new FileInputStream(sourceFile));
        String merge = DigestUtils.md5Hex(new FileInputStream(mergeFile));
        Assertions.assertEquals(source,merge);
    }
}
