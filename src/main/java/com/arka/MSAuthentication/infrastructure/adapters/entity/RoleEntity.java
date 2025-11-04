package com.arka.MSAuthentication.infrastructure.adapters.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Roles")
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nameRole;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List <UserEntity> users = new ArrayList<>();

    // Rol con Permisos
    @ManyToMany(fetch = FetchType.EAGER) // Importante: Cargar los permisos junto con el Rol
    @JoinTable(
            name = "rol_permiso", // Nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "role_id"), // Columna que referencia a Roles
            inverseJoinColumns = @JoinColumn(name = "permission_id") // Columna que referencia a Permissions
    )
    private Set<PermissionEntity> permissions = new HashSet<>(); // Usar Set para evitar duplicados

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
