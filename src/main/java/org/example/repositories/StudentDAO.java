package org.example.repositories;

import org.example.entities.Student;
import java.util.List;

public interface StudentDAO {
    boolean add(Student student);
    void removeById(int id);
    void update(Student student);
    Student getById(int id);
    List<Student> getAll();
}
