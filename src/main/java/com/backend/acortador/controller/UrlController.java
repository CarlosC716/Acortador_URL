package com.backend.acortador.controller;

import java.net.URI;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.acortador.dto.UrlRequest;
import com.backend.acortador.dto.UrlResponse;
import com.backend.acortador.service.UrlService;

@RestController
@RequestMapping("/api")
public class UrlController {
	@Autowired
	private UrlService service;
	
	@GetMapping("/{code}")
	public ResponseEntity<?> findUrl(@PathVariable String code){
		UrlResponse u = service.findUrl(code);	
		if(u == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		if(u.getExpirationDate().isBefore(LocalDateTime.now())) {
			return ResponseEntity.status((HttpStatus.GONE)).build();
		}
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(u.getUrl())).build();
	}
	@GetMapping("/{code}/stats")
	public ResponseEntity<UrlResponse> stats(@PathVariable String code){
		UrlResponse u = service.getStats(code);	
		if(u == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(u);
	}
	@PostMapping("/create")
	public ResponseEntity<UrlResponse> createCode(@RequestParam("url") String url){
		UrlResponse resultado = service.create(url);
		if(resultado == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
	}
	@PatchMapping("/update")
	public ResponseEntity<UrlResponse> updateUrl(@RequestBody UrlRequest url) {
		UrlResponse u = service.update(url);
		if(u == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(u);
	}
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteCode(@RequestParam("url") String url){
		Boolean result = service.delete(url);
		if(!result) {
			return ResponseEntity.notFound().build();
			
		}
		return ResponseEntity.noContent().build();
	}
}