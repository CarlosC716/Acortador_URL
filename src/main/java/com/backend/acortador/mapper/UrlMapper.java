package com.backend.acortador.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.backend.acortador.dto.UrlResponse;
import com.backend.acortador.entity.Url;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UrlMapper {
	UrlResponse toResponse(Url url);
}