package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: zhourx
 * @Description:
 * @Date: 2023/9/16
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
        //子节点
        List<CourseCategoryTreeDto> childrenTreeNodes;
}
