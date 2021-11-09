package com.ead.course.services.impl;

import com.ead.course.models.LessonModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    LessonRepository lessonRepository;

    @Override
    public void save(LessonModel model) {
        lessonRepository.save(model);
    }

    @Override
    public Optional<LessonModel> findByLessonIdAndModule(UUID lessonId, UUID moduleId) {
        return lessonRepository.findByLessonIdAndModule_ModuleId(lessonId,moduleId);
    }

    @Override
    public void delete(LessonModel lessonModel) {
        lessonRepository.delete(lessonModel);
    }

    @Override
    public List<LessonModel> findAllLessonsByModule(UUID moduleId) {
        return lessonRepository.findByModule_ModuleId(moduleId);
    }

    @Override
    public Page<LessonModel> findAllLessonsByModule(Specification<LessonModel> spec, Pageable pageable) {
        return lessonRepository.findAll(spec,pageable);
    }
}
