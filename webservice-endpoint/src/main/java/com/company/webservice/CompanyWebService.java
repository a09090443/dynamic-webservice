package com.company.webservice;

import com.company.dto.CompanyRequest;
import com.company.dto.CompanyResponse;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;

@WebService(targetNamespace = "http://service.other.com", name = "CompanyWebService")
public interface CompanyWebService {

    @WebMethod
    @RequestWrapper(localName = "getCompany", targetNamespace = "http://service.other.com")
    @ResponseWrapper(localName = "getCompanyResponse", targetNamespace = "http://service.other.com")
    @WebResult(name = "CompanyRequest", targetNamespace = "http://service.other.com")
    CompanyResponse getCompany(@WebParam(name = "CompanyRequest", targetNamespace = "http://service.other.com") CompanyRequest request);
}
