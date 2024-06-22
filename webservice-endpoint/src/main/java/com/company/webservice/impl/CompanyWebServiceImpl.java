package com.company.webservice.impl;

import com.company.dto.CompanyRequest;
import com.company.dto.CompanyResponse;
import com.company.dto.Employee;
import com.company.webservice.CompanyWebService;
import jakarta.jws.WebService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@WebService(serviceName = "CompanyWebService",//對外發布的服務名
        targetNamespace = "http://service.other.com",//指定你想要的名稱空間，通常使用使用包名反轉
        endpointInterface = "com.company.webservice.CompanyWebService")//服務接口全路徑, 指定做SEI（Service EndPoint Interface）服務端點接口
@Component
public class CompanyWebServiceImpl implements CompanyWebService {

    @Override
    public CompanyResponse getCompany(CompanyRequest request) {
        CompanyResponse response = new CompanyResponse();
        response.setName(request.getName());
        response.setTaxId(request.getTaxId());
        response.setAddress("Test Address");
        Employee employee = new Employee();
        employee.setName("Test Employee");
        employee.setGrander("G");
        response.getEmployees().getEmployee().add(employee);
        return response;
    }
}
