package com.aman.twoschema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.aman.twoschema.entity.Student;
import com.aman.twoschema.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/all")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }
    
    @GetMapping("/details/{rollno}")
    public Student getStudentDetails(@PathVariable int rollno) {
        return studentService.getStudentByRollNo(rollno);
    }


    @PostMapping("/add")
    public Student addStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    @DeleteMapping("/delete/{rollno}")
    public void deleteStudent(@PathVariable int rollno) {
        studentService.deleteStudent(rollno);
    }
}

