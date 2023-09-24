package com.xuecheng.media;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * @Author: zhourx
 * @Description:
 * @Date: 2023/9/24
 */
public class BigFileTest {
    //测试文件分块方法
    @Test
    public void testChunk() throws FileNotFoundException {
        //源文件
        File sourceFile = new File("E:\\B23Dld-v0.9.5.8-win_64\\Downloads\\1.mp4");
        //分块文件存储路径
        String chunkFilePath = "D:/";
        //分块文件大小
        int chunkSize = 1024 * 1024 * 1;
        //分块文件个数
        int chunkNum = (int) Math.ceil(sourceFile.length()*1.0/chunkSize);
        //使用流从源文件中读取数据，向分块文件中写入数据
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");
        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePath + i);
            //分块文件写入流
            RandomAccessFile raf_rw = new RandomAccessFile(sourceFile, "rw");
        }
    }

    //将分块进行合并
    @Test
    public void testMerge(){

    }
}
