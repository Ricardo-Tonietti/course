package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;

import com.ead.course.specifications.SpecificationTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@Api(value = "API REST Course")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    public static final String MODULENOTFOUND = "Module not found for this course!";
    public static final String COURSENOTFOUND = "Course not found!";

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private CourseService courseService;

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModule(@PathVariable(value = "courseId") UUID courseId,
                                            @RequestBody @Valid ModuleDto moduleDto){

        log.debug("POST saveModule moduleDto received {} ", moduleDto.toString());

        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(COURSENOTFOUND);
        }

        var moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDto,moduleModel);
        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        moduleModel.setCourse(courseModelOptional.get());
        log.debug("POST saveModule moduleId saved {} ", moduleModel.getDescription());
        log.info("Module saved successfully moduleId {} ", moduleModel.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.save(moduleModel));
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable(value = "courseId")UUID courseId,
                                               @PathVariable(value = "moduleId")UUID moduleId){
        log.debug("DELETE deleteModule moduleId received {} ", moduleId);

        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if(!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MODULENOTFOUND);
        }
        moduleService.delete(moduleModelOptional.get());
        log.debug("DELETE deleteModule moduleId deleted {} ", moduleId);
        log.info("Module deleted successfully moduleId {} ", moduleId);
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully");
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    @ApiOperation(value = "Return update Module")
    public ResponseEntity<Object> updateModule( @PathVariable(value = "courseId")UUID courseId,
                                                @PathVariable(value = "moduleId")UUID moduleId,
                                                @RequestBody @Valid ModuleDto moduleDto){
        log.debug("PUT updateModule moduleDto received {} ", moduleDto.toString());

        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if(!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MODULENOTFOUND);
        }
        var moduleModel = moduleModelOptional.get();
        moduleModel.setTitle(moduleDto.getTitle());
        moduleModel.setDescription(moduleDto.getDescription());
        log.debug("PUT updateModule moduleId saved {} ", moduleModel.getModuleId());
        log.info("Module updated successfully moduleId {} ", moduleModel.getModuleId());
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.save(moduleModel));
    }
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/courses/{courseId}/modules")
    @ApiOperation(value = "Return List of Modules")
    public ResponseEntity<Page<ModuleModel>> getAllModules(@PathVariable(value = "courseId")UUID courseId,
                                                           SpecificationTemplate.ModuleSpec spec,
                                                           @PageableDefault(page = 0, size = 5, sort = "moduleId",
                                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(moduleService.findAllByCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable));
    }
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    @ApiOperation(value = "Return one Course")
    public ResponseEntity<Object> getOneModule (@PathVariable(value = "courseId")UUID courseId,
                                                @PathVariable(value = "moduleId")UUID moduleId){
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if(!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MODULENOTFOUND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelOptional.get());
    }
}
