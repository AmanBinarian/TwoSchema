package com.aman.twoschema.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.aman.twoschema.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
