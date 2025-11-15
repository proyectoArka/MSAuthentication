package com.arka.MSAuthentication.infrastructure.adapters.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_id_seq", allocationSize = 1)
    private Long id;

    /*
    * CREATE SEQUENCE users_id_seq
    INCREMENT BY 1
    START WITH 1
    NO CYCLE;
    *
    * CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1;*/

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    private String direction;

    @Column(nullable = false)
    private String phone;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

