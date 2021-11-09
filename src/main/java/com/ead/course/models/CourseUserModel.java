package com.ead.course.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_COURSES_USERS",uniqueConstraints = { @UniqueConstraint(columnNames = { "course_id", "userId" }) })
public class CourseUserModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "course_id")
    private CourseModel course;

    @Column(nullable = false)
    private UUID userId;

    public CourseUserModel(CourseModel courseModel, UUID userId) {
        this.userId = userId;
        this.course = courseModel;
    }

    public CourseUserModel(UUID id, CourseModel course, UUID userId) {
        this.id = id;
        this.course = course;
        this.userId = userId;
    }

    public CourseUserModel() {
    }

    public CourseModel getCourse() {
        return course;
    }

    public void setCourse(CourseModel course) {
        this.course = course;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


}
