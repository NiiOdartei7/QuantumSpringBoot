package com.example.quantumspringboot.config;

import com.cosmian.jna.covercrypt.structs.MasterKeys;
import com.cosmian.jna.covercrypt.structs.Policy;

public record NativeMasterKeys(
        MasterKeys masterKeys,

        Policy policy
) {

}
