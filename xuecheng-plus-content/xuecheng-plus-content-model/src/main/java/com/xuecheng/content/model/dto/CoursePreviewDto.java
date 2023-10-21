package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CoursePublish;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: zhourx
 * @Description:    课程预览模型类
 * @Date: 2023/10/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CoursePreviewDto {
    private CourseBaseInfoDto courseBase;
    //课程计划信息
    private List<TeachplanDto> teachplans;
    //课程师资信息
}
