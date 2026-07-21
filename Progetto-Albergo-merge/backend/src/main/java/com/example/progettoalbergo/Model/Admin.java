package com.example.progettoalbergo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "admin")
public class Admin {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idadmin")
    private Long idAdmin;

    private String username,email,password;
    
    public Admin(Long idAdmin, String username, String email, String password) {
    	setIdAdmin(idAdmin);
		setUsername(username);
		setEmail(email);
		setPassword(password);
	}
	
	public Admin() {
		
	}

	public Long getIdAdmin() {
		return idAdmin;
	}

	public void setIdAdmin(Long idAdmin) {
		this.idAdmin = idAdmin;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
