package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author: zhourx
 * @Description: 课程计划信息模型类
 * @Date: 2023/9/20
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {
    //与媒资关联的信息
    private TeachplanMedia teachplanMedia;
    //小涨节信息
    private List<TeachplanDto> teachPlanTreeNodes;
}
