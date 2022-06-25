package com.ead.course.controllers;


import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import com.ead.course.speficication.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUsersController {

    @Autowired
    CourseService courseService;

    @Autowired
    UserService courseUserService;


    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Object> getAllUsersByCourse(SpecificationTemplate.UserSpec spec,
            @PageableDefault(size = 10, page = 0,sort = "userId", direction = Sort.Direction.ASC) Pageable page,
                                                      @PathVariable(value = "courseId")UUID courseId){
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(courseModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(courseUserService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec),page));
    }

    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "courseId")UUID courseId,

                                                               @RequestBody @Valid SubscriptionDto subscriptionDto){
        Optional<CourseModel> courseModelOpt = courseService.findById(courseId);

        if(courseModelOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        if(courseService.existsByCourseAndUser(courseId,subscriptionDto.getUserId())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: subscripton already exists!");
        }
        Optional<UserModel> userModelOptional = courseUserService.findById(subscriptionDto.getUserId());
        if(userModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if(UserStatus.BLOCKED.toString().equals(userModelOptional.get().getUserStatus())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Useris blocked");
        }

        courseService.saveSubscriptionUserInCourseAndSendNotification(courseModelOpt.get(),userModelOptional.get());

        return ResponseEntity.status(HttpStatus.CREATED).body("Subscription created");
    }
}
