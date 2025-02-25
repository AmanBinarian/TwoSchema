package com.aman.twoschema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aman.twoschema.entity.CourseDetail;
import com.aman.twoschema.repository.CourseDetailRepository;

import java.util.List;

@Service
public class CourseDetailService {
	
    @Autowired
    private CourseDetailRepository courseDetailRepository;

    public List<CourseDetail> getAllCourses() {
        return courseDetailRepository.findAll();
    }

    public CourseDetail saveCourse(CourseDetail courseDetail) {
        return courseDetailRepository.save(courseDetail);
    }

    public void deleteCourse(int id) {
        courseDetailRepository.deleteById(id);
    }
}
