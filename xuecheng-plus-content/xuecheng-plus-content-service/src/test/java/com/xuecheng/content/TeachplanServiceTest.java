package com.xuecheng.content;

import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author: zhourx
 * @Description:    课程计划
 * @Date: 2023/9/20
 */
@SpringBootTest
public class TeachplanServiceTest {
    @Autowired
    TeachplanService teachplanService;

    @Test
    public void testSelectTreeNodes(){
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(117l);
        System.out.println(teachplanTree);
    }
}
