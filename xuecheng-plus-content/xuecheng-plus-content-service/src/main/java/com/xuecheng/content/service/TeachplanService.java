package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @Author: zhourx
 * @Description:
 * @Date: 2023/9/20
 */
public interface TeachplanService {
    /**
     * 查询课程计划树型结构
     * @param courseId 课程id
     * @return List<TeachplanDto>
     */
    public List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 保存课程计划
     * @param teachplanDto 课程计划信息
     */
    public void saveTeachplan(SaveTeachplanDto teachplanDto);

    /**
     * @description 教学计划绑定媒资
     */
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

}
