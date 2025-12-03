package com.example.quantumspringboot.service;

import com.cosmian.rest.abe.KmsClient;
import com.cosmian.jna.covercrypt.structs.Policy;
import com.cosmian.rest.kmip.objects.PrivateKey;
import com.cosmian.rest.kmip.objects.PublicKey;
import com.example.quantumspringboot.entity.SecurePolicy;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.cosmian.utils.CloudproofException;


@Service
@Data
public class MasterKeyService {
    private final KmsClient kmsClient;
    private String privateKeyId;
    private String publicKeyId;
    private Policy policy;

    public MasterKeyService(KmsClient kmsClient) {
        System.setProperty("RUST_LOG", "debug,cosmian_kms=debug,cosmian_sdk=debug");

        this.kmsClient = kmsClient;
    }

    public synchronized void initKeysIfAbsent() throws CloudproofException {
        if (privateKeyId == null || publicKeyId == null) {
            Policy policy = SecurePolicy.createPolicy();
            setPolicy(policy);
            System.out.println("Creating key pair with KMS...");

            String[] ids = kmsClient.createCoverCryptMasterKeyPair(policy);
            privateKeyId = ids[0];
            publicKeyId = ids[1];
            PrivateKey privateMasterKey = kmsClient.retrieveCoverCryptPrivateMasterKey(privateKeyId);
            PublicKey publicMasterKey = kmsClient.retrieveCoverCryptPublicMasterKey(publicKeyId);
        }
    }

    public String getPrivateKeyId() { return privateKeyId; }
    public String getPublicKeyId() { return publicKeyId; }
}

