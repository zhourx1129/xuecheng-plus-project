package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
 * @Author: zhourx
 * @Description:    任务处理
 * @Date: 2023/10/7
 */
public interface MediaFileProcessService {
    public boolean startTask(long id);
    /**
     * 获取媒体进程列表
     * @param shardIndex 碎片索引
     * @param shardTotal 总计
     * @param count      计数
     * @return {@link List}<{@link MediaProcess}>
     */
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     * 保存进程完成状态
     *
     * @param taskId   任务id
     * @param status   地位
     * @param fileId   文件id
     * @param url      url
     * @param errorMsg 错误消息
     */
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}
