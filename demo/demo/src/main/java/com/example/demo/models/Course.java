package com.example.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên khóa học không được bỏ trống!")
    private String name;

    @NotBlank(message = "Tên giáo viên không được bỏ trống!")
    @Length(min = 2,max=100)
    @Column(name = "teacher_name")
    private String teacherName;

    @NotBlank(message = "Địa điểm không được bỏ trống!")
    private String address;

    @NotNull(message = "Ngày bắt đầu là bắt buộc!")
    @Future(message = "Ngày bắt đầu phải lớn hơn ngày hiện tại!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date")
    private LocalDate startDate;
}