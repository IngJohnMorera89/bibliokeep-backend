package com.devsenior.jmorera.bibliokeep.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.devsenior.jmorera.bibliokeep.model.dto.books.BookDto;
import com.devsenior.jmorera.bibliokeep.model.dto.books.BookPreviewDto;
import com.devsenior.jmorera.bibliokeep.model.dto.books.CreateBookRequest;
import com.devsenior.jmorera.bibliokeep.model.entity.Book;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

	@Mapping(
			target = "status",
			expression = "java(request.status() != null ? request.status() : BookStatus.DESEADO)"
	)
	Book toEntity(CreateBookRequest request);

	BookPreviewDto toPreviewDto(Book book);

	BookDto toDto(Book book);
}

