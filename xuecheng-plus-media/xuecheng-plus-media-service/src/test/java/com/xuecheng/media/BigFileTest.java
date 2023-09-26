package com.xuecheng.media;


import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author: zhourx
 * @Description:
 * @Date: 2023/9/24
 */

public class BigFileTest {

    //测试文件分块方法
    @Test
    public void testChunk() throws IOException {
        File sourceFile = new File("E:\\B23Dld-v0.9.5.8-win_64\\Downloads\\1.mp4");
        String chunkPath = "d:/chunk/";
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        //分块大小
        long chunkSize = 1024 * 1024 * 5;
        //分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        System.out.println("分块总数："+chunkNum);
        //缓冲区大小
        byte[] b = new byte[1024];
        //使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        //分块
        for (int i = 0; i < chunkNum; i++) {
            //创建分块文件
            File file = new File(chunkPath + i);
            if(file.exists()){
                file.delete();
            }
            boolean newFile = file.createNewFile();
            if (newFile) {
                //向分块文件中写数据
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }
                raf_write.close();
                System.out.println("完成分块"+i);
            }

        }
        raf_read.close();
    }

    //将分块进行合并
    @Test
    public void testMerge1() throws IOException {
        //块文件目录
        File chunkFolder = new File("d:\\chunk");
        //源文件
        File souceFile = new File("E:\\B23Dld-v0.9.5.8-win_64\\Downloads\\1.mp4");
        //合并后的文件
        File mergeFile = new File("d:\\chunk\\1.mp4");
        //取出所有分块文件
        File[] listFiles = chunkFolder.listFiles();
        //将数组转为list
        List<File> filesList = Arrays.asList(listFiles);
        Collections.sort(filesList, (o1, o2) -> Integer.parseInt(o1.getName())-Integer.parseInt(o2.getName()));
        //向和并文件写的流
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");
        //缓存区
        byte[] bytes = new byte[1024];
        //遍历分块文件，向合并的文件写
        for (File file : filesList) {
            //度分块文件的流
            RandomAccessFile raf_r = new RandomAccessFile(file, "r");
            int len=-1;
            while ((len=raf_r.read(bytes))!=-1){
                raf_rw.write(bytes,0,len);
            }
            raf_r.close();
        }
        raf_rw.close();
        //合并文件后对文件进行校验
        FileInputStream fileInputStream_merge  = new FileInputStream(mergeFile);
        FileInputStream fileInputStream_source  = new FileInputStream(souceFile);
        String md5_merge = DigestUtils.md5DigestAsHex(fileInputStream_merge);
        String md5_source = DigestUtils.md5DigestAsHex(fileInputStream_source);
        if (md5_source.equals(md5_merge)){
            System.out.println("文件合并成功");
        }

    }


}
