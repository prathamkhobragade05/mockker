package com.projects.mockker.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="Users")
public class UserModel {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
    private Long id;

    @Column(name="email", nullable = false, unique = true)
    private String email;
    
    @Column(name="user_name")
    String name;
    
    @Column()
	String password;

    @Column()
	String phone;
	
	public UserModel(Long id, String name, String email, String phone,String password) {
		this.id=id;
		this.email=email;
		this.name=name;
		this.phone=phone;
		this.password=password;
	}
	public UserModel() {}
	
	// Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name= name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone= phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
	
}
