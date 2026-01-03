package com.example.quantumspringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class DBDocumentDTO {
    private String filename;
    private byte[] content;
    private String status;
    private String url;
    private UUID id;


    public byte[] getContent() {
        return content;
    }
}
