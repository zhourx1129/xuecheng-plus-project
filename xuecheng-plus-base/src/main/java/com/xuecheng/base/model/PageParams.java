package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;



/**
 * @Author: zhourx
 * @Description: 分页查询分页参数
 * @Date: 2023/9/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageParams {
    //当前页码
    @ApiModelProperty("页码")
    private Long pageNo=1L;
    //每页显示记录数
    @ApiModelProperty("每页记录数")
    private Long pageSize=10L;
}
