package com.backend.acortador.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.acortador.entity.Url;

@Repository
public interface IUrlRepository extends JpaRepository<Url, Integer> {

}
