package com.example.quantumspringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocumentResponseDTO {
    private UUID id;
    private String status;

    private String url;

    private String filename;
    private byte[] content;
}
