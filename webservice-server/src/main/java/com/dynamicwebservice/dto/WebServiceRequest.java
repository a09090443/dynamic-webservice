package com.dynamicwebservice.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class WebServiceRequest {

    public String beanName;

    public String fileId;

    public String publishUrl;

    public String classPath;

    private MultipartFile file;
}
