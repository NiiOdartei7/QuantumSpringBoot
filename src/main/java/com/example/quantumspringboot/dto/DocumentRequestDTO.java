package com.example.quantumspringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class DocumentRequestDTO {

    private UUID documentID;
    private UUID user_id;
}
