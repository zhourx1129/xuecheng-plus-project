package com.xuecheng.content.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    public PageResult() {
    }

    public PageResult(List<T> items, Long counts, Long page, Long pageSize) {
        this.items = items;
        this.counts = counts;
        this.page = page;
        this.pageSize = pageSize;
    }
}
