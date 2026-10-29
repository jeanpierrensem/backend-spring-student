package com.dev.backendspringstudents;

import com.dev.backendspringstudents.entity.Payment;
import com.dev.backendspringstudents.entity.Student;
import com.dev.backendspringstudents.enums.PaymentStatus;
import com.dev.backendspringstudents.enums.PaymentType;
import com.dev.backendspringstudents.repository.PaymentRepository;
import com.dev.backendspringstudents.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class BackendSpringStudentsApplication {

    StudentRepository studentRepository;
    PaymentRepository paymentRepository;

    public BackendSpringStudentsApplication(StudentRepository studentRepository, PaymentRepository paymentRepository) {
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendSpringStudentsApplication.class, args);
    }

    @Bean
    CommandLineRunner start(){
        return args ->{
            studentRepository.save(Student.builder().id("11").firstname("Mohamed").code(UUID.randomUUID().toString()).email("Mohamed@gmail.com").build());
            studentRepository.save(Student.builder().id("22").firstname("Youssuf").code(UUID.randomUUID().toString()).email("Mohamed@gmail.com").build());
            studentRepository.save(Student.builder().id("33").firstname("Mohamed").code(UUID.randomUUID().toString()).email("Mohamed@gmail.com").build());

            List<Student> studentList = studentRepository.findAll();
            PaymentType[] values = PaymentType.values();
            int index = 0;
            for (Student student : studentList){
                for (int i=0; i<10; i++){
                     index = (int)(Math.random()*values.length-1) +0;
                    Payment payment = Payment.builder()
                            .status(PaymentStatus.CREATED)
                            .type(values[index])
                            .date(new Date())
                            .amount(100 + Math.random() * 1000)
                            .file(UUID.randomUUID().toString().concat(".pdf"))
                            .student(student)
                            .build();
                    paymentRepository.save(payment);
                }
            }

        };
    }
}
