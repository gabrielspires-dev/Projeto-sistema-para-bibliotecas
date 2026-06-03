package org.example.repositories;

import org.example.entities.Librarian;
import java.util.List;

public interface LibrarianDAO {
    void add(Librarian librarian);
    void removeById(int id);
    void update(Librarian librarian);
    Librarian getById(int id);
    List<Librarian> getAll();
}
