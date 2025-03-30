package com.yaojiuye.content.controller;

import com.yaojiuye.content.model.dto.BindTeachplanMediaDto;
import com.yaojiuye.content.model.dto.SaveTeachplanDto;
import com.yaojiuye.content.model.dto.TeachplanDto;
import com.yaojiuye.content.service.ITeachplanMediaService;
import com.yaojiuye.content.service.ITeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author itnan
 * @ClassName TeachplanController
 * @Description 课程计划编辑接口
 * @Date 2025/3/15 12:37
 * @Version V1.0
 */
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
@RestController
@RequiredArgsConstructor
public class TeachplanController {

    private final ITeachplanService teachplanService;

    private final ITeachplanMediaService teachplanMediaService;

    /**
     * 根据课程id查询课程计划
     * @param courseId
     * @return java.util.List<com.yaojiuye.content.model.dto.TeachplanDto>
     */
    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {
        return teachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    /**
     * 课程计划删除
     * @param id
     */
    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable Long id){
        teachplanService.deleteTeachplan(id);
    }

    @ApiOperation("课程计划向上排序")
    @PostMapping("/teachplan/moveup/{id}")
    public void moveup(@PathVariable Long id){
        teachplanService.moveup(id);
    }

    @ApiOperation("课程计划向下排序")
    @PostMapping("/teachplan/movedown/{id}")
    public void movedown(@PathVariable Long id){
        teachplanService.movedown(id);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
            teachplanMediaService.associationMedia(bindTeachplanMediaDto);
    }

    @ApiOperation("课程计划和媒资信息绑定解绑")
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public void unassociationMedia(@PathVariable("teachPlanId") Long teachPlanId,@PathVariable("mediaId") String mediaId){
        teachplanMediaService.unassociationMedia(teachPlanId,mediaId);
    }

}
