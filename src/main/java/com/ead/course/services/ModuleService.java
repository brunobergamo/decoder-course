package com.ead.course.services;

import com.ead.course.models.ModuleModel;
import com.ead.course.speficication.SpecificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleService {

    public void delete(ModuleModel moduleModel);

    ModuleModel save(ModuleModel model);

    Optional<ModuleModel> findById(UUID moduleId);

    Optional<ModuleModel> findByModuleIdAndCourse(UUID moduleId, UUID courseId);

    List<ModuleModel> findAllModulesByCourse(UUID courseId);

    Page<ModuleModel> findAllModulesByCourse(Specification<ModuleModel> and, Pageable pageable);
}
