package com.ead.course.validation;

import com.ead.course.client.AuthUserClient;
import com.ead.course.dtos.CourseDTO;
import com.ead.course.dtos.UserDto;
import com.ead.course.enums.UserType;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.UUID;

@Component
public class CourseValidator implements Validator {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Autowired
    AuthUserClient authUserClient;


    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

        CourseDTO courseDto = (CourseDTO)  target;
        validator.validate(courseDto,errors);

        if(!errors.hasErrors()){
            validateUserInstructor(courseDto.getUserInstructor(),errors);
        }

     }

     private void validateUserInstructor(UUID userId, Errors errors){
        try {
            ResponseEntity<UserDto> responseUserInstructor = authUserClient.getOneUserById(userId);
            if(responseUserInstructor.getBody().getUserType().equals(UserType.STUDENT)){
                errors.rejectValue("userInstructor", "userInstructorError", "User must be instructor or admin");
            }
        }
        catch(HttpStatusCodeException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                errors.rejectValue("userInstructor", "userInstructornotFound", "User instructor not found");
            }
        }
     }
}
