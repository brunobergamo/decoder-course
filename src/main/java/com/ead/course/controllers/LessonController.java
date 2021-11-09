package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import com.ead.course.speficication.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
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
public class LessonController {

    @Autowired
    LessonService lessonService;

    @Autowired
    ModuleService moduleService;

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                              @RequestBody @Valid LessonDto lessonDto){
        log.debug("POST saveLesson lessonDto received {} ", lessonDto.toString());
        Optional<ModuleModel> moduleModelOptional =moduleService.findById(moduleId);
        if(moduleModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }

        LessonModel model = new LessonModel();
        BeanUtils.copyProperties(lessonDto,model);
        LocalDateTime utc = LocalDateTime.now(ZoneId.of("UTC"));

        model.setCreationDate(utc);
        model.setModule(moduleModelOptional.get());

        lessonService.save(model);
        log.debug("POST saveLesson lessonId saved {} ", model.getLessonId());
        log.info("Lesson saved successfully lessonId {} ", model.getLessonId());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable(value = "moduleId")UUID moduleId,
                                               @PathVariable(value = "lessonId")UUID lessonId){
        log.debug("DELETE deleteLesson lessonId received {} ", lessonId);
        Optional<LessonModel> lessonModelOptional = lessonService.findByLessonIdAndModule(lessonId,moduleId);
        if(lessonModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found");
        }
        lessonService.delete(lessonModelOptional.get());
        log.debug("DELETE deleteLesson lessonId deleted {} ", lessonId);
        log.info("Lesson deleted successfully lessonId {} ", lessonId);
        return ResponseEntity.status(HttpStatus.OK).body("Delete successfully");
    }

    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable(value = "moduleId")UUID moduleId,
                                               @PathVariable(value = "lessonId")UUID lessonId,
                                               @RequestBody @Valid LessonDto lessonDto){

        log.debug("PUT updateLesson lessonDto received {} ", lessonDto.toString());
        Optional<LessonModel> lessonModelOptional = lessonService.findByLessonIdAndModule(lessonId,moduleId);
        if(lessonModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found");
        }

        var lessonModel = lessonModelOptional.get();
        lessonModel.setDescription(lessonDto.getDescription());
        lessonModel.setTitle(lessonDto.getTitle());
        lessonModel.setVideoUrl(lessonDto.getVideoUrl());
        lessonService.save(lessonModel);
        log.debug("PUT updateLesson lessonId saved {} ", lessonModel.getLessonId());
        log.info("Lesson updated successfully lessonId {} ", lessonModel.getLessonId());
        return ResponseEntity.ok().body(lessonModel);
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Page<LessonModel>> getAllLessons(@PathVariable(value = "moduleId")UUID moduleId,
                                                           SpecificationTemplate.LessonSpec spec,
                                                           @PageableDefault(page = 0, size = 10, sort = "lessonId",
                                                                   direction = Sort.Direction.ASC) Pageable pageable){
        Page<LessonModel> all = lessonService.findAllLessonsByModule(SpecificationTemplate.lessonModuleId(moduleId).and(spec),pageable);
        return ResponseEntity.ok().body(all);
    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getLesson(@PathVariable(value = "moduleId")UUID moduleId,
                                            @PathVariable(value = "lessonId")UUID lessonId){
        Optional<LessonModel> lessonModelOptional = lessonService.findByLessonIdAndModule(lessonId,moduleId);
        if(lessonModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found");
        }
        return ResponseEntity.ok().body(lessonModelOptional.get());
    }
}
