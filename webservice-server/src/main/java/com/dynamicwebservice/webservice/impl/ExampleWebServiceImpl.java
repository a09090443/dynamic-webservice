package com.dynamicwebservice.webservice.impl;

import com.dynamicwebservice.webservice.ExampleWebService;
import jakarta.jws.WebService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@WebService(serviceName = "ExampleWebService",//對外發布的服務名
        targetNamespace = "http://service.example.com/",//指定你想要的名稱空間，通常使用使用包名反轉
        endpointInterface = "com.dynamicwebservice.webservice.ExampleWebService")
@Component
public class ExampleWebServiceImpl implements ExampleWebService {

    @Override
    public String sayHello() {
        return "Hello, World!";
    }
}
