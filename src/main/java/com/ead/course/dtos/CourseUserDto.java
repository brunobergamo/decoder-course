package com.ead.course.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CourseUserDto {

    private UUID userId;
    @NotNull
    private UUID courseId;

}
