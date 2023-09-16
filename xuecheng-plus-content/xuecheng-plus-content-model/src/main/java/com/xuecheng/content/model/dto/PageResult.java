package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: zhourx
 * @Description: 分页查询结果模型类
 * @Date: 2023/9/15
 */
@Data
public class PageResult<T> implements Serializable {
    //数据列表
    private List<T> items;
    //总记录数
    private Long counts;
    //当前页码
    private Long page;
    //每页记录数
    private Long pageSize;

    public PageResult(List<CourseBase> items, long pages, long size) {
    }

    public PageResult(List<T> items, Long counts, Long page, Long pageSize) {
        this.items = items;
        this.counts = counts;
        this.page = page;
        this.pageSize = pageSize;
    }
}
