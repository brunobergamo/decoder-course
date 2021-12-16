package com.ead.course.services.impl;

import com.ead.course.client.AuthUserClient;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.services.CourseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CourseUserServiceImpl implements CourseUserService {

    @Autowired
    CourseUserRepository courseUserRepository;

    @Autowired
    AuthUserClient authUserClient;

    @Override
    public boolean existsByCourseAndUserId(CourseModel courseModel, UUID userId) {

        return courseUserRepository.existsByCourseAndUserId(courseModel,userId);
    }

    @Override
    public CourseUserModel save(CourseUserModel courseUserModel) {
        return courseUserRepository.save(courseUserModel);
    }

    @Override
    @Transactional
    public CourseUserModel saveAndSendSubscriptionUserInCourse(CourseUserModel courseUserModel) {

        courseUserModel = courseUserRepository.save(courseUserModel);
        authUserClient.postSubscriptionUserInCourse(courseUserModel.getCourse().getCourseId(),courseUserModel.getUserId());
        return courseUserModel;
    }

    @Override
    public void deleteCourseUserByUserId(UUID userId) {
        List<CourseUserModel> courseUserModelByUserId = courseUserRepository.findCourseUserModelByUserId(userId);
        courseUserModelByUserId.stream().forEach(a-> courseUserRepository.delete(a));
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return courseUserRepository.existsByUserId(userId);
    }
}
