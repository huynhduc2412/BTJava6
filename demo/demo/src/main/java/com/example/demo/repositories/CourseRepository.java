package com.example.demo.repositories;

import com.example.demo.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByNameContainingIgnoreCaseOrTeacherNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String teacherName, String address);

    @Query("SELECT c FROM Course c " +
            "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.teacherName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Course> searchCourses(@Param("searchTerm") String searchTerm);
}
