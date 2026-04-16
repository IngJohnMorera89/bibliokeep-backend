package com.devsenior.jmorera.bibliokeep.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devsenior.jmorera.bibliokeep.model.dto.file.UploadResponse;
import com.devsenior.jmorera.bibliokeep.service.fiile.FileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public UploadResponse upload(@RequestParam("file") MultipartFile file) {
        return fileService.upload(file);

    }

}
