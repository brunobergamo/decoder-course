package com.ead.course.controllers;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.speficication.SpecificationTemplate;
import com.ead.course.validation.CourseValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    @Autowired
    CourseService courseService;

    @Autowired
    CourseValidator courseValidator;

    @PostMapping
    public ResponseEntity<Object> saveCourse(@RequestBody CourseDTO courseDto, Errors error){

        courseValidator.validate(courseDto,error);
        if(error.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getAllErrors());
        }
        log.debug("POST saveCourse courseDto received {} ", courseDto.toString());
        LocalDateTime utf = LocalDateTime.now(ZoneId.of("UTC"));

        CourseModel courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDto,courseModel);
        courseModel.setCreationDate(utf);
        courseModel.setLastUpdateDate(utf);
        courseService.save(courseModel);
        log.debug("POST saveCourse courseId saved {} ", courseModel.getCourseId());
        log.info("Course saved successfully courseId {} ", courseModel.getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(courseModel);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable(value = "courseId")UUID courseId){

        log.debug("DELETE deleteCourse courseId received {} ", courseId);
        Optional<CourseModel> courseModelOpt = courseService.findById(courseId);

        if(courseModelOpt.isPresent()){
            courseService.delete(courseModelOpt.get());
            return ResponseEntity.ok().body("Deleted successfully");
        }
        log.debug("DELETE deleteCourse courseId deleted {} ", courseId);
        log.info("Course deleted successfully courseId {} ", courseId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Object> UpdateCourse(@PathVariable(value = "courseId")UUID courseId,
                                               @RequestBody @Valid CourseDTO courseDto){

        log.debug("PUT updateCourse courseDto received {} ", courseDto.toString());
        Optional<CourseModel> courseModelOpt = courseService.findById(courseId);

        if(courseModelOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        CourseModel courseModel = courseModelOpt.get();
        LocalDateTime utf = LocalDateTime.now(ZoneId.of("UTC"));
        courseModel.setLastUpdateDate(utf);
        courseModel.setDescription(courseDto.getDescription());
        courseModel.setImageUrl(courseDto.getImageUrl());
        courseModel.setCourseStatus(courseDto.getCourseStatus());
        courseModel.setCourseLevel(courseDto.getCourseLevel());
        courseService.save(courseModel);
        log.debug("PUT updateCourse courseId saved {} ", courseModel.getCourseId());
        log.info("Course updated successfully courseId {} ", courseModel.getCourseId());
        return ResponseEntity.ok().body(courseModel);
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllCourses(SpecificationTemplate.CourseSpec spec,
                                                           @PageableDefault(page = 0, size = 10, sort = "courseId",
                                                                   direction = Sort.Direction.ASC)Pageable pageable,
                                                           @RequestParam(required = false) UUID userId){
        Specification specification = spec;
        if(userId !=null){
            specification = SpecificationTemplate.courseUsers(userId).and(spec);
        }
        return ResponseEntity.ok().body(courseService.findAll(specification,pageable));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Object> getCourse(@PathVariable(value = "courseId")UUID courseId){
        Optional<CourseModel> courseModelOpt = courseService.findById(courseId);

        if(courseModelOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        return ResponseEntity.ok().body(courseModelOpt);
    }

}
