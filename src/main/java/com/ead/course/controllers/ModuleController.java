package com.ead.course.controllers;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.speficication.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.connector.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    @Autowired
    ModuleService moduleService;

    @Autowired
    CourseService courseService;

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModules(@PathVariable(value = "courseId") UUID courseId,
                                              @RequestBody  @Valid ModuleDto moduleDto){

        log.debug("POST saveModule moduleDto received {} ", moduleDto.toString());
        Optional<CourseModel> courseModelOptional =courseService.findById(courseId);
        if(courseModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        ModuleModel model = new ModuleModel();
        BeanUtils.copyProperties(moduleDto,model);
        LocalDateTime utc = LocalDateTime.now(ZoneId.of("UTC"));

        model.setCreationDate(utc);
        model.setCourse(courseModelOptional.get());

        moduleService.save(model);
        log.debug("POST saveModule moduleId saved {} ", model.getModuleId());
        log.info("Module saved successfully moduleId {} ", model.getModuleId());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable(value = "courseId")UUID courseId,
                                               @PathVariable(value = "moduleId")UUID moduleId){
        log.debug("DELETE deleteModule moduleId received {} ", moduleId);
        Optional<ModuleModel> moduleModelOptional = moduleService.findByModuleIdAndCourse(moduleId,courseId);
        if(moduleModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }
        moduleService.delete(moduleModelOptional.get());
        log.debug("DELETE deleteModule moduleId deleted {} ", moduleId);
        log.info("Module deleted successfully moduleId {} ", moduleId);
        return ResponseEntity.status(HttpStatus.OK).body("Delete successfully");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModule(@PathVariable(value = "courseId")UUID courseId,
                                               @PathVariable(value = "moduleId")UUID moduleId,
                                               @RequestBody @Valid ModuleDto moduleDto){

        log.debug("PUT updateModule moduleDto received {} ", moduleDto.toString());
        Optional<ModuleModel> moduleModelOptional = moduleService.findByModuleIdAndCourse(moduleId,courseId);
        if(moduleModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }

        ModuleModel moduleModel = moduleModelOptional.get();
        moduleModel.setDescription(moduleDto.getDescription());
        moduleModel.setTitle(moduleDto.getTitle());
        moduleService.save(moduleModel);
        log.debug("PUT updateModule moduleId saved {} ", moduleModel.getModuleId());
        log.info("Module updated successfully moduleId {} ", moduleModel.getModuleId());
        return ResponseEntity.ok().body(moduleModel);
    }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleModel>> getAllModules(@PathVariable(value = "courseId")UUID courseId,
                                                           SpecificationTemplate.ModuleSpec spec,
                                                           @PageableDefault(page = 0, size = 10, sort = "moduleId",
                                                                   direction = Sort.Direction.ASC) Pageable pageable){
        Page<ModuleModel> all = moduleService.findAllModulesByCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec),pageable);
        return ResponseEntity.ok().body(all);
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> getModule(@PathVariable(value = "courseId")UUID courseId,
                                            @PathVariable(value = "moduleId")UUID moduleId){
        Optional<ModuleModel> moduleModelOptional = moduleService.findByModuleIdAndCourse(moduleId,courseId);

        if(moduleModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }
        return ResponseEntity.ok().body(moduleModelOptional);
    }
}
