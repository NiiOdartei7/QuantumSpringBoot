package com.example.quantumspringboot.entity;

import com.cosmian.jna.covercrypt.structs.AccessPolicy;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="user_details")
public class User implements UserDetails {

    @ManyToOne
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    @Column
    private String email;

    @Column
    private String password;
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;

    @Column
    private Clearance clearance;

    @Column
    private String userAccessPolicy;

    @Column
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Document> documents = new ArrayList<>();


    public User(String email, String password, Role role){
        this.email = email;
        this.password = password;
        this.role = role;

    }

    public void definePolicy(){

        setUserAccessPolicy("Department::" + this.department.getName() + " && " + "Clearance::" + this.clearance);

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {

        return email;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
