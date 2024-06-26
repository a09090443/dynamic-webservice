package com.dynamicwebservice.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class WebServiceRequest {

    private String beanName;

    private String publishUrl;

    private String classPath;

    private String jarFileId;

    private String jarFileName;

    private Boolean isActive;
}
