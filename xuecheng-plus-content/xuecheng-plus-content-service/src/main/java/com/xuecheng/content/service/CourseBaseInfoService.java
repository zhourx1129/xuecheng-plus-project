package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * @Author: zhourx
 * @Description: 课程信息管理接口
 * @Date: 2023/9/16
 */
public interface CourseBaseInfoService {

    /**
     * 查询分页查询
     *
     * @param pageParams 分页查询参数
     * @param dto        查询条件
     * @return 查询结果
     */
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto dto);
}
