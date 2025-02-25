package com.aman.twoschema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aman.twoschema.entity.CourseDetail;


public interface CourseDetailRepository extends JpaRepository<CourseDetail, Integer> {
}
