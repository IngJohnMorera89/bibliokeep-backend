package com.devsenior.jmorera.bibliokeep.model.dto.file;

public record UploadResponse(
        String filename,
        String url,
        Long size) {

}
