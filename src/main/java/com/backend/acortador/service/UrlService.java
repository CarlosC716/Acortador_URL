package com.backend.acortador.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.acortador.dto.UrlRequest;
import com.backend.acortador.dto.UrlResponse;
import com.backend.acortador.entity.Url;
import com.backend.acortador.mapper.UrlMapper;
import com.backend.acortador.repository.IUrlRepository;

import jakarta.transaction.Transactional;

@Service
public class UrlService {
	@Autowired
	private IUrlRepository _repo;
	@Autowired
	private UrlMapper _mapper;
	
	@Transactional
	public UrlResponse findUrl(String code) {
		_repo.incrementAccessCount(code);
		Url u = _repo.findUrlByShortCode(code);
		if (u == null) {
			return null;
		}

		return _mapper.toResponse(u);
	}

	public UrlResponse getStats(String code) {
		Url u = _repo.findUrlByShortCode(code);
		return _mapper.toResponse(u);

	};

	public UrlResponse create(String url) {
		Url u = new Url();

		String code = UUID.randomUUID().toString().substring(0, 6);

		u.setUrl(url);
		u.setShortCode(code);
		u.setCreatedAt(LocalDateTime.now());
		u.setExpirationDate(LocalDateTime.now().plusDays(10));
		Url saved = _repo.save(u);

		return _mapper.toResponse(saved);
	}

	public UrlResponse update(UrlRequest url) {
		Url u = _repo.findUrlByShortCode(url.getShortCode());

		if (u == null) {
			return null;
		}

		u.setUrl(url.getUrl()); 
		u.setUpdatedAt(LocalDateTime.now());
		u.setExpirationDate(LocalDateTime.now().plusDays(30));
		Url saved = _repo.save(u);

		return _mapper.toResponse(saved);
	}

	public Boolean delete(String url) {
		Url u = _repo.findUrlByShortCode(url);
		if (u != null) {
			_repo.delete(u);
			return true;
		}
		return false;
	}
}
