package com.dynamicwebservice.dto;

import lombok.Data;

@Data
public class EndpointDTO {
    private String id;

    private String publishUri;

    private String beanName;

    private String jarFileId;

    private String jarFileName;

    private String classPath;

    private Boolean isActive;

}
