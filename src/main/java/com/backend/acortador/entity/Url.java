package com.backend.acortador.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Url {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idUrl;
	@Column(nullable = false)
	private String url;
	@Column(nullable = false, unique = true, length = 10)
	private String shortCode;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime expirationDate;
	private int accessCount;
}
