package com.dev.backendspringstudents.web;
import com.dev.backendspringstudents.dto.NewPaymentDto;
import com.dev.backendspringstudents.entity.Payment;
import com.dev.backendspringstudents.entity.Student;
import com.dev.backendspringstudents.enums.PaymentStatus;
import com.dev.backendspringstudents.repository.PaymentRepository;
import com.dev.backendspringstudents.repository.StudentRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
public class StudentController {
    private StudentRepository studentRepository ;
    private PaymentRepository paymentRepository ;

    public StudentController(StudentRepository studentRepository, PaymentRepository paymentRepository) {
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/students")
    List<Student> getStudents(){
       return studentRepository.findAll();
    }
    @GetMapping("/studentsByCode/{code}")
    Student getStudentByCode(@PathVariable String code){
        return  studentRepository.findByCode(code);
    }
    @GetMapping("/students/{id}")
    Student getStudentById(@PathVariable String id){
        return studentRepository.findById(id).get();
    }

    @GetMapping("/students/{code}/payments")
    List<Payment> findPaymentsByStudentCode(@PathVariable  String code){
        return paymentRepository.findByStudentCode(code);
    }
    @PostMapping(value = "/payments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Payment savePayment(@RequestParam ("file") MultipartFile file, NewPaymentDto newPaymentDto ) throws IOException {

        Student student = studentRepository.findByCode(newPaymentDto.getStudentCode());

        String paymentId = UUID.randomUUID().toString();
        Path path = Paths.get(System.getProperty("user.home"), "students-app-files", "payments");

        if(!Files.exists(path)){
             Files.createDirectories(path);
        }
        Path filePath = Paths.get(System.getProperty("user.home"), "students-app-files", "payments", paymentId.concat(".pdf"));
        Files.copy(file.getInputStream(), filePath);

        Payment payment = Payment.builder()
                .student(student)
                .date(newPaymentDto.getDate())
                .type(newPaymentDto.getType())
                .status(PaymentStatus.CREATED)
                .amount(newPaymentDto.getAmount())
                .file(filePath.toUri().toString())
                .build();
        return paymentRepository.save(payment);

    }

    @GetMapping(value = "/paymentFile/{paymentId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] getPaymentFile(@PathVariable  Long paymentId) throws IOException {
        Payment payment = paymentRepository.findById(paymentId).get();
        String filePath = payment.getFile();
        System.out.println(filePath);
        return Files.readAllBytes(Path.of(URI.create(filePath)));
    }

    @GetMapping("/payments")
    List<Payment> getAllPayments(){
        return paymentRepository.findAll();
    }

}
