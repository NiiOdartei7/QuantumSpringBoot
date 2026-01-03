package com.example.quantumspringboot.config;

import com.cosmian.jna.covercrypt.CoverCrypt;
import com.cosmian.jna.covercrypt.structs.MasterKeys;
import com.cosmian.jna.covercrypt.structs.Policy;
import com.cosmian.rest.abe.KmsClient;
import com.cosmian.utils.CloudproofException;
import com.example.quantumspringboot.entity.SecurePolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@ConfigurationProperties(prefix = "cosmian.kms")
public class KmsConfig {
    private String url;
    private String apiToken;


    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getApiToken() { return apiToken; }
    public void setApiToken(String apiToken) { this.apiToken = apiToken; }






    @Bean
    public NativeMasterKeys masterKeysInfo() throws CloudproofException {
        Policy policy = SecurePolicy.createPolicy();
        MasterKeys masterKeys = CoverCrypt.generateMasterKeys(policy);

        return new NativeMasterKeys(
                masterKeys,
                policy
        );
    }

}
