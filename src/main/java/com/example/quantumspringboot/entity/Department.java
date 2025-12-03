package com.example.quantumspringboot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Department {

    @Column
    private String name;

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public Department(String name){
        this.name = name;

    }
}
