package com.company.webservice;

import com.company.dto.CompanyRequest;
import com.company.dto.CompanyResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;

import javax.xml.transform.TransformerException;
import java.io.IOException;

@WebService(targetNamespace = "http://service.other.com", name = "CompanyWebService")
public interface CompanyWebService {

    @WebMethod
    @RequestWrapper(localName = "getCompany", targetNamespace = "http://service.other.com", className = "com.company.dto.GetCompany")
    @ResponseWrapper(localName = "getCompanyResponse", targetNamespace = "http://service.other.com", className = "com.company.dto.GetCompanyResponse")
    @WebResult(name = "companyResponse", targetNamespace = "")
    CompanyResponse getCompany(@WebParam(name = "CompanyRequest", targetNamespace = "http://service.other.com") CompanyRequest request) throws IOException, SOAPException, TransformerException;
}
