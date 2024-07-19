package com.dynamicwebservice.dto;

import lombok.Data;

@Data
public class WebServiceRequestDTO {
    private String id;

    private String beanName;

    private String publishUri;

    private String classPath;

    private String jarFileId;

    private String jarFileName;

    private Boolean isActive;
}
