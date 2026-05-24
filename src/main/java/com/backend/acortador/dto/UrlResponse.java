package com.backend.acortador.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlResponse {
	private Integer idUrl;
	private String url;
	private String shortCode;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime expirationDate;
	private int accessCount;
}
