package com.xuecheng.content.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: zhourx
 * @Description: 课程查询条件模型类
 * @Date: 2023/9/15
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QueryCourseParamsDto {
    //课程审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;
}
