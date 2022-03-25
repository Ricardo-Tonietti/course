package com.ead.course.controllers;

import com.ead.course.dtos.CourseDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
@Api(value = "API REST Course")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    public static final String COURSENOTFOUND = "Course not found!";

    @Autowired
    private CourseService courseService;

    @PostMapping
    @ApiOperation(value = "Return created of Course")
    public ResponseEntity<Object> saveCourse(@RequestBody @Valid CourseDto courseDto){
        var courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDto,courseModel);
        courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseService.save(courseModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(courseModel);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable(value = "courseId")UUID courseId){
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(COURSENOTFOUND);
        }
        courseService.delete(courseModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully");
    }

    @PutMapping("/{courseId}")
    @ApiOperation(value = "Return update Course")
    public ResponseEntity<Object> updateCourse(@PathVariable(value = "courseId")UUID courseId,
                                               @RequestBody @Valid CourseDto courseDto ){
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(COURSENOTFOUND);
        }
        var courseModel = courseModelOptional.get();
        BeanUtils.copyProperties(courseDto,courseModel);
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseService.save(courseModel);

        return ResponseEntity.status(HttpStatus.OK).body(courseModel);
    }

    @GetMapping
    @ApiOperation(value = "Return List of Course")
    public ResponseEntity<List<CourseModel>> getAllCourses(){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findAll());
    }

    @GetMapping("/{courseId}")
    @ApiOperation(value = "Return one Course")
    public ResponseEntity<Object> getOneCourse (@PathVariable(value = "courseId")UUID courseId){
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(COURSENOTFOUND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(courseModelOptional.get());
    }

}
