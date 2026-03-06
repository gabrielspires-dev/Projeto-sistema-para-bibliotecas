package org.example.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.entities.Librarian;

public class LibrarianRepository {
    private ArrayList<Librarian> librarians = new ArrayList<>();

    public void add(Librarian librarian) {
        librarians.add(librarian);
    }

    public void removeById(int id) {
        librarians.removeIf(librarian -> librarian.getId() == id);
    }

    public Librarian getById(int id) {
        for (Librarian l : librarians) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }

    public List<Librarian> getAll() {
        return Collections.unmodifiableList(librarians);
    }
}