package com.dynamicwebservice.dto;

import lombok.Data;

@Data
public class MockResponseResponseDTO {

    private String id;

    private String publishUrl;

    private String method;

    private String condition;

    private String responseContent;

    private Boolean isActive;
}
