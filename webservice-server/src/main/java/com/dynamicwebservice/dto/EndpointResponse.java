package com.dynamicwebservice.dto;

import lombok.Data;

@Data
public class EndpointResponse {
    public String publishUrl;
    public String beanName;
    public String classPath;
    public Boolean isActive;
}
