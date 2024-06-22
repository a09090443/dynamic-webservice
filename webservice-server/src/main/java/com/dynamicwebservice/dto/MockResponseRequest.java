package com.dynamicwebservice.dto;

import lombok.Data;

@Data
public class MockResponseRequest {

    private String publishUrl;

    private String method;

    private String condition;

    private String responseContent;

    private Boolean status;
}
