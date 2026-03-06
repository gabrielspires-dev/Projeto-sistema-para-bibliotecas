package org.example.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.entities.Student;

public class StudentRepository {
    private ArrayList<Student> students = new ArrayList<>();

    public void add(Student student) {
        students.add(student);
    }

    public void removeById(int id) {
        students.removeIf(student -> student.getId() == id);
    }

    public List<Student> getAll() {
        return Collections.unmodifiableList(students);
    }
}

