package com.example.demo.services;

import com.example.demo.models.Course;
import com.example.demo.repositories.CourseRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;

    /**
     * Retrieve all courses from the database.
     * @return a list of courses
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    /**
     * Retrieve a course by its id.
     * @param id the id of the course to retrieve
     * @return an Optional containing the found course or empty if not found
     */
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }
    /**
     * Add a new course to the database.
     * @param course the course to add
     */
    @Transactional
    public void addCourse(Course course) {
        courseRepository.save(course);
    }
    /**
     * Update an existing course.
     * @param course the course with updated information
     */
    public void updateCourse(@NotNull Course course) {
        Course existingCourse = courseRepository.findById(course.getId())
                .orElseThrow(() -> new IllegalStateException("Course with ID " +
                        course.getId() + " does not exist."));
        existingCourse.setTeacherName(course.getTeacherName());
        existingCourse.setAddress(course.getAddress());
        existingCourse.setStartDate(course.getStartDate());
        courseRepository.save(existingCourse);
    }
    /**
     * Delete a course by its id.
     * @param id the id of the course to delete
     */
    public void deleteCourseById(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new IllegalStateException("Course with ID " + id + " does not exist.");
        }
        courseRepository.deleteById(id);
    }

    /**
     * Search for courses by name or other criteria.
     * @param query the search query
     * @return a list of courses matching the query
     */
    public List<Course> searchCourses(String query) {
        return courseRepository.findByNameContainingIgnoreCaseOrTeacherNameContainingIgnoreCaseOrAddressContainingIgnoreCase(query, query, query);
    }
}