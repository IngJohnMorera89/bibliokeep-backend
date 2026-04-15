package com.devsenior.jmorera.bibliokeep.mapper;

import java.util.List;

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
            expression = "java(request.status() != null ? request.status() : com.devsenior.jmorera.bibliokeep.model.enums.BookStatus.DESEADO)"
    )
    @Mapping(target = "authors", source = "authors")
    // AQUÍ ESTÁ LA CORRECCIÓN:
    // Si tu CreateBookRequest tiene un nombre distinto a 'thumbnail', mapealo así:
    // @Mapping(target = "thumbnail", source = "thumbnailUrl") 
    Book toEntity(CreateBookRequest request);

    BookPreviewDto toPreviewDto(Book book);

    BookDto toDto(Book book);

    default List<String> map(List<String> authors) {
        return authors == null ? List.of() : List.copyOf(authors);
    }
}
