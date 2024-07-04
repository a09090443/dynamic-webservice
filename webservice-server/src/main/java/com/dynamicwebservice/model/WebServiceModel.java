package com.dynamicwebservice.model;

import lombok.Data;

@Data
public class WebServiceModel {
    public String id;
    public String publishUrl;
    public String beanName;
    public String classPath;
    public Boolean isActive;
    public String jarFileId;
    public String jarFileName;
    public String fileStatus;
}
