package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

/**
 * @Author: zhourx
 * @Description:    课程发布相关的接口
 * @Date: 2023/10/20
 */
public interface CoursePublishService {
    /**
     * 获取课程预览信息
     * @param courseId 课程id
     * @return {@link CoursePreviewDto}
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 提交审核
     * @param companyId 公司id
     * @param courseId  课程id
     */
    public void commitAudit(Long companyId,Long courseId);

    /**
     * 课程发布接口
     * @param companyId 机构id
     * @param courseId  课程id
     */
    public void publish(Long companyId,Long courseId);
}
