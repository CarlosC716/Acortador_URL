package com.backend.acortador.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.acortador.entity.Url;

@Repository
public interface IUrlRepository extends JpaRepository<Url, Integer> {

	@Modifying(clearAutomatically = true)
	@Query("""
			UPDATE Url u
			SET u.accessCount = u.accessCount + 1
			WHERE u.shortCode = :code
			""")
	void incrementAccessCount(@Param("code") String code);

	Url findUrlByShortCode(@Param("code") String code);
}
