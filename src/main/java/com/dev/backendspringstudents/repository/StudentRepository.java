package com.dev.backendspringstudents.repository;

import com.dev.backendspringstudents.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, String> {
    Student findByCode(String code);
}
