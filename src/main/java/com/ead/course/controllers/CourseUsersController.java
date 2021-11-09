package com.ead.course.controllers;


import com.ead.course.client.AuthUserClient;
import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUsersController {


    @Autowired
    AuthUserClient curseClient;

    @Autowired
    CourseService courseService;

    @Autowired
    CourseUserService courseUserService;

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Page<UserDto>> getAllUsersByCourse(@PageableDefault(size = 10, page = 0,sort = "userId", direction = Sort.Direction.ASC) Pageable page,
                                                             @PathVariable(value = "courseId")UUID courseId){

        return ResponseEntity.status(HttpStatus.OK).body(curseClient.getAllUsersByCourse(courseId,page));
    }

    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "courseId")UUID courseId,

                                                               @RequestBody @Valid SubscriptionDto subscriptionDto){
        ResponseEntity<UserDto> responseUser;
        Optional<CourseModel> courseModelOpt = courseService.findById(courseId);

        if(courseModelOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        CourseModel courseModel = courseModelOpt.get();
        UUID userId = subscriptionDto.getUserId();
        if(courseUserService.existsByCourseAndUserId(courseModel, userId)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error : User already exists!");
        }

        try {
            responseUser = curseClient.getOneUserById(userId);
            if(UserStatus.BLOCKED.equals(responseUser.getBody().getUserStatus())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error : User is blocked!");
            }
        } catch (HttpStatusCodeException e) {
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error : User not found!");
            }
            return ResponseEntity.status(e.getStatusCode()).body("Error : " + e.getStatusText());
        }

        CourseUserModel courseUserModel = courseUserService.saveAndSendSubscriptionUserInCourse(courseModel.convertToCourseUserModel(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(courseUserModel);
    }
}
