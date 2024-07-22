package com.dynamicwebservice.dto;

import lombok.Data;

@Data
public class ControllerResponseDTO {

    private String id;

    private String publishUri;

    private String jarFileId;

    private String jarFileName;

    private String classPath;

    private Boolean isActive;
}