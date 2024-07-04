package com.dynamicwebservice.dto;

import lombok.Data;

@Data
public class MockResponseRequest {

    private String id;

    private String publishUrl;

    private String method;

    private String condition;

    private String responseContent;

    private Boolean status;
}
