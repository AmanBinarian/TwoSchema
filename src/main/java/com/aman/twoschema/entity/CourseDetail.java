package com.aman.twoschema.entity;

import jakarta.persistence.*;

@Entity
public class CourseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String courseName;
    private int price;

    // Add rollno to the body as a foreign key
    private int rollno;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rollno", insertable = false, updatable = false) // Maintain the foreign key relationship
    private Student student;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRollno() {
        return rollno;
    }

    public void setRollno(int rollno) {
        this.rollno = rollno;
    }

}
