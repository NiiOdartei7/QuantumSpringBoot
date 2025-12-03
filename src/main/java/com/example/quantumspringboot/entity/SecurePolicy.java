package com.example.quantumspringboot.entity;

import com.cosmian.jna.covercrypt.structs.Policy;
import com.cosmian.jna.covercrypt.structs.PolicyAxis;
import com.cosmian.jna.covercrypt.structs.PolicyAxisAttribute;
import com.cosmian.utils.CloudproofException;

public class SecurePolicy {
    public static Policy createPolicy() throws CloudproofException {


        return new Policy(
                new PolicyAxis[]{
                        new PolicyAxis("Department",
                                new PolicyAxisAttribute[]{
                                        new PolicyAxisAttribute("Artificial Intelligence", true),
                                        new PolicyAxisAttribute("Robotics", true)},
                                false

                        ),
                        new PolicyAxis("Clearance",
                                new PolicyAxisAttribute[]{
                                        new PolicyAxisAttribute("HIGH", true),
                                        new PolicyAxisAttribute("LOW", true)},
                                true
                        ),

                }
        );
    }
}
