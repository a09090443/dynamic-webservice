package com.dynamicwebservice.model;

import lombok.Data;

@Data
public class ControllerModel {
    public String id;
    public String publishUri;
    public String classPath;
    public Boolean isActive;
    public String jarFileId;
    public String jarFileName;
    public String fileStatus;
}
