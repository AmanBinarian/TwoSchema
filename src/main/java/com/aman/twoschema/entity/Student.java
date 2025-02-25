package com.aman.twoschema.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Student {
    @Id
    private int rollNo;
    private String name;
    private String fatherName;
    private String motherName;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseDetail> courses;


    // Getters and Setters
	public int getRollNo() {
		return rollNo;
	}

	public void setRollNo(int rollNo) {
		this.rollNo = rollNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}
	public List<CourseDetail> getCourses() {
		return courses;
	}

	public void setCourses(List<CourseDetail> courses) {
		this.courses = courses;
	}

}
