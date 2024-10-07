package com.hyperrealm.kiwi.logging;

import lombok.Getter;

@Getter
public enum Types {

    INFO("INFO"), STATUS("STATUS"), WARNING("WARNING"), ERROR("ERROR");

    private final String type;

    Types(String type) {
        this.type = type;
    }

}
