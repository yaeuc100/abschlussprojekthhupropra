package de.hhu.propra.application.services;

import de.hhu.propra.application.repositories.StudentRepository;

public class StudentService {
    private final StudentRepository studentRepository;
    private final Anw

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
}
