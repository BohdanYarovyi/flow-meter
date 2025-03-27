package com.yarovyi.flowmeter.domain.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "t_roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "c_name")
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<Account> accounts;

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
