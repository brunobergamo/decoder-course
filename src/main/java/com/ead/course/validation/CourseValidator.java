package com.ead.course.validation;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;

@Component
public class CourseValidator implements Validator {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Autowired
    UserService userService;

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
         Optional<UserModel> userModelOptional = userService.findById(userId);
         if(userModelOptional.isEmpty()){
             errors.rejectValue("userInstructor", "userInstructornotFound", "User instructor not found");
         }

         if(userModelOptional.isPresent() && userModelOptional.get().getUserType().equals(UserType.STUDENT.toString())){
             errors.rejectValue("userInstructor", "userInstructorError", "User must be instructor or admin");
         }
     }
}
