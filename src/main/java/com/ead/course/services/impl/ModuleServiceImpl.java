package com.ead.course.services.impl;

import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.ModuleService;
import com.ead.course.speficication.SpecificationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Transactional
    @Override
    public void delete(ModuleModel moduleModel){
        List<LessonModel> lessonModelList = lessonRepository.findByModule_ModuleId(moduleModel.getModuleId());
        if(!lessonModelList.isEmpty()){
            lessonRepository.deleteAll(lessonModelList);
        }
        moduleRepository.delete(moduleModel);
    }

    @Override
    public ModuleModel save(ModuleModel model) {
        return moduleRepository.save(model);
    }

    @Override
    public Optional<ModuleModel> findById(UUID moduleId) {
        return moduleRepository.findById(moduleId);
    }

    @Override
    public Optional<ModuleModel> findByModuleIdAndCourse(UUID moduleId, UUID courseId) {
        return moduleRepository.findByModuleIdAndCourse_CourseId(moduleId,courseId);
    }

    @Override
    public List<ModuleModel> findAllModulesByCourse(UUID courseId) {
        return null;
    }

    @Override
    public Page<ModuleModel> findAllModulesByCourse(Specification<ModuleModel> spec, Pageable pageable) {
        return moduleRepository.findAll(spec,pageable);
    }
}
