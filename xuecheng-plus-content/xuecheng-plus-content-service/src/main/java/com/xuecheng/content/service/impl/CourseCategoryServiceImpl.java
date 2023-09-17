package com.xuecheng.content.service.impl;


import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: zhourx
 * @Description:
 * @Date: 2023/9/17
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //1.调用mapper递归查询出分类信息
        List<CourseCategoryTreeDto> treeNodes = courseCategoryMapper.selectTreeNodes(id);

        //2.找到每个节点的子节点，最终将其封装成List<CourseCategoryTreeDto>
        //现将list转为map，key就是节点的id，value就是List<CourseCategoryTreeDto>对象，目的就是为了方便从map获取节点,filter(item->!id.equals(item.getId()))的作用是排除根节点
        Map<String, CourseCategoryTreeDto> mapTemp = treeNodes.stream().filter(item->!id.equals(item.getId())).collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
        //定义一个list作为最终返回的list
        List<CourseCategoryTreeDto> courseCategoryTreeDtoList = new ArrayList<>();
        //从头遍历List<CourseCategoryTreeDto>，一边遍历一边找子节点放在父节点的childrenTreeNodes
        treeNodes.stream().filter(item->!id.equals(item.getId())).forEach(item-> {
            //向list写入元素
            if (item.getParentid().equals(id)){
                courseCategoryTreeDtoList.add(item);
            }
            //找到节点的父节点
            CourseCategoryTreeDto courseCategoryParent = mapTemp.get(item.getParentid());
            if (courseCategoryParent!=null){
                if (courseCategoryParent.getChildrenTreeNodes()==null){
                    //如果该父节点的子节点属性为空，则要new一个集合，因为要向该集合中放一个她的子节点
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                //找到每个几点的子节点放到父节点的childrenTreeNodes属性中
                courseCategoryParent.getChildrenTreeNodes().add(item);
            }
        });
        return courseCategoryTreeDtoList;
    }
}
