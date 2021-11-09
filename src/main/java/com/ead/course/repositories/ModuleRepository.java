package com.ead.course.repositories;

import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<ModuleModel, UUID> , JpaSpecificationExecutor<ModuleModel> {


    Optional<ModuleModel> findByModuleIdAndCourse_CourseId(UUID moduleId, UUID courseId);

    List<ModuleModel> findByCourse_CourseId(UUID courseId);

    @Query(value = "select * from tb_modules where course_id = :courseId", nativeQuery = true)
    List<ModuleModel> findAllModulesIntoCourse(@Param("courseId") UUID courseId);

//    @EntityGraph(attributePaths = {"course"})
//    ModuleModel findByTitle(String title);


}
