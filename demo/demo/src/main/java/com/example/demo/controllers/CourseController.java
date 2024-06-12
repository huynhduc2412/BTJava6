package com.example.demo.controllers;

import com.example.demo.models.Course;
import com.example.demo.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    @Autowired
    private final CourseService courseService;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses/course-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("course", new Course());
        return "courses/add-course";
    }

    @PostMapping("/add")
    public String addCourse(@Valid Course course, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "courses/add-course";
        }
        courseService.addCourse(course);
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course id: " + id));
        model.addAttribute("course", course);
        return "courses/update-course";
    }

    @PostMapping("/edit/{id}")
    public String updateCourse(@PathVariable Long id, @Valid Course course, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "courses/update-course";
        }
        courseService.updateCourse(course);
        return "redirect:/courses";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourseById(id);
        return "redirect:/courses";
    }

    @GetMapping("/search")
    public String searchCourses(@RequestParam(name = "query", required = false) String query, Model model) {
        List<Course> courses;
        if (query != null && !query.isEmpty()) {
            courses = courseService.searchCourses(query);
            model.addAttribute("query", query);
        } else {
            courses = courseService.getAllCourses();
        }
        model.addAttribute("courses", courses);
        return "courses/search-course";
    }
}