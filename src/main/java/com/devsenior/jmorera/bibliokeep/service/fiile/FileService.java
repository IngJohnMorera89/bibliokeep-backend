package com.devsenior.jmorera.bibliokeep.service.fiile;

import org.springframework.web.multipart.MultipartFile;

import com.devsenior.jmorera.bibliokeep.model.dto.file.UploadResponse;

public interface FileService {

    UploadResponse upload(MultipartFile file);

}
