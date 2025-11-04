package com.arka.MSAuthentication.infrastructure.adapters.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions") // Tabla de permisos
@Getter
@Setter
@NoArgsConstructor
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code; // Ej: "usuarios:crear_admin", "productos:crear"

    @Column(length = 255)
    private String description;

    // Relación ManyToMany con RoleEntity
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<RoleEntity> roles = new HashSet<>();

}
