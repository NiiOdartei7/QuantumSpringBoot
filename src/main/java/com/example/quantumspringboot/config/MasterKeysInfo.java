package com.example.quantumspringboot.config;

import com.cosmian.jna.covercrypt.structs.Policy;
import lombok.AllArgsConstructor;
import lombok.Data;

public record MasterKeysInfo(
        String privateMasterKeyId,
        String publicMasterKeyId,
        byte[] privateKeyBytes,
        byte[] publicKeyBytes,

        Policy policy
) {}
