package com.dev.backendspringstudents.web;
import com.dev.backendspringstudents.entity.Payment;
import com.dev.backendspringstudents.entity.Student;
import com.dev.backendspringstudents.enums.PaymentStatus;
import com.dev.backendspringstudents.enums.PaymentType;
import com.dev.backendspringstudents.repository.PaymentRepository;
import com.dev.backendspringstudents.repository.StudentRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
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
    Payment savePayment(@RequestParam MultipartFile file, double amount, PaymentType type, LocalDate date, String studentCode) throws IOException {

        Student student = studentRepository.findByCode(studentCode);

        String paymentId = UUID.randomUUID().toString();
        Path path = Paths.get(System.getProperty("user.home"), "students-app-files", "payments");

        if(!Files.exists(path)){
             Files.createDirectories(path);
        }
        Path filePath = Paths.get(System.getProperty("user.home"), "students-app-files", "payments", paymentId.concat(".pdf"));
        Files.copy(file.getInputStream(), filePath);

        Payment payment = Payment.builder()
                .student(student)
                .date(date)
                .type(type)
                .status(PaymentStatus.CREATED)
                .amount(amount)
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

}
