package com.ead.course.repositories;

import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseUserRepository extends JpaRepository<CourseUserModel, UUID> {

    boolean existsByCourseAndUserId(CourseModel courseModel, UUID userId);

    @Query(value = " select * from tb_courses_users where course_course_id = :courseId", nativeQuery = true)
    List<CourseUserModel> findAllCourseUserIntoCourse(@Param("courseId") UUID courseId);

    void deleteAllByUserId(UUID userId);

    boolean existsByUserId(java.util.UUID userId);
}
