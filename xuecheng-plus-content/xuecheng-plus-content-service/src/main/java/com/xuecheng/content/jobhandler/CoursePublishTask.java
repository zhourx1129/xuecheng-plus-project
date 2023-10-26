package com.xuecheng.content.jobhandler;

import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: zhourx
 * @Description:    课程发布的任务类
 * @Date: 2023/10/22
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        //分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        //调用抽象类的方法来执行任务
        process(shardIndex,shardTotal,"course_publish",30,60);
    }


        //执行课程发布任务的逻辑，如果此方法抛出异常说明任务执行失败
    @Override
    public boolean execute(MqMessage mqMessage) {
        //从mqmessage拿到课程id
        long courseId = Long.parseLong(mqMessage.getBusinessKey1());
        //课程静态化上传到mino
        generateCourseHtml(mqMessage,courseId);
        //向es写索引数据
        saveCourseIndex(mqMessage,courseId);
        //向redis写缓存

        //返回true表示任务完成
        return false;
    }
    //生成课程静态化页面并上传至文件系统
    private void generateCourseHtml(MqMessage mqMessage,long courseId){
        //消息id
        Long messageId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //做任务幂等性处理

        //查询数据库，取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(messageId);
        //任务已完成
        if (stageOne>0){
            log.debug("课程静态化任务完成，无需处理");
            return;
        }
        //开始进行课程静态化
        int i = 1/0;
        //任务处理完成写任务状态为完成
        mqMessageService.completedStageOne(messageId);
    }
    //将课程信息缓存至redis
    public void saveCourseCache(MqMessage mqMessage,long courseId){

    }
    //保存课程索引信息
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        //任务id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //取出第二个阶段状态
        int stageTwo = mqMessageService.getStageTwo(taskId);
        //任务的幂等性处理
        if (stageTwo>0){
            log.debug("课程索引信息已写入，无需执行");
            return;
        }
        //查询课程信息,调用搜索服务添加索引。。。。
        //完成本阶段任务
        mqMessageService.completedStageTwo(taskId);
    }

}
