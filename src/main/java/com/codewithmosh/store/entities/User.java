package com.codewithmosh.store.entities;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;


//    @Override
//    public String toString() {
//        return getClass().getSimpleName() + "(" +
//                "id = " + id + ", " +
//                "name = " + name + ", " +
//                "email = " + email + ")";
//    }
}
