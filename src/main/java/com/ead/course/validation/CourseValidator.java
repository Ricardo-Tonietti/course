package com.ead.course.validation;

import com.ead.course.configs.security.AuthenticationCurrentUserService;
import com.ead.course.dtos.CourseDto;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
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
    private UserService userService;

    @Autowired
    private AuthenticationCurrentUserService authenticationCurrentUserService;
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {

        CourseDto courseDto = (CourseDto) o;
        validator.validate(courseDto, errors);

        if(!errors.hasErrors()){
            validateUserInstructor(courseDto.getUserInstructor(), errors );
        }
    }

    private void validateUserInstructor(UUID userInstructor, Errors errors){
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();

        if(currentUserId.equals(userInstructor)){
            Optional<UserModel> userModelOptional = userService.findById(userInstructor);
            if(!userModelOptional.isPresent()){
                errors.rejectValue("UserInstructor", "responseUserInstructor", "Instructor Not Found!");
            }
            if(userModelOptional.get().getUserType().equals(UserType.STUDENT.toString())){
                errors.rejectValue("UserInstructor", "responseUserInstructor", "User must be INSTRUCTOR or ADMIN");
            }
        }else{
            throw new AccessDeniedException("Forbidden");
        }

    }
}
