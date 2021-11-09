package com.ead.course.services;

import com.ead.course.models.LessonModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonService {
    void save(LessonModel model);

    Optional<LessonModel> findByLessonIdAndModule(UUID lessonId, UUID moduleId);

    void delete(LessonModel lessonModel);

    List<LessonModel> findAllLessonsByModule(UUID moduleId);

    Page<LessonModel> findAllLessonsByModule(Specification<LessonModel> and, Pageable pageable);
}
