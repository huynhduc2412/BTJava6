package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer role_id;
    @Column
    private String name;
}
