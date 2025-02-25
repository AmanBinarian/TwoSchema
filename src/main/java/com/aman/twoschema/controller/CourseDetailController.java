package com.aman.twoschema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.aman.twoschema.entity.CourseDetail;
import com.aman.twoschema.service.CourseDetailService;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseDetailController {
    @Autowired
    private CourseDetailService courseDetailService;

    @GetMapping("/all")
    public List<CourseDetail> getAllCourses() {
        return courseDetailService.getAllCourses();
    }

    @PostMapping("/add")
    public CourseDetail addCourse(@RequestBody CourseDetail courseDetail) {
        return courseDetailService.saveCourse(courseDetail);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCourse(@PathVariable int id) {
        courseDetailService.deleteCourse(id);
    }
}
