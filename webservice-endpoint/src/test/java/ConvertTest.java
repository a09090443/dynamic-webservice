import com.company.dto.GetCompany;
import com.company.dto.GetCompanyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipe.util.SoapUtil;
import com.zipe.util.XmlUtil;
import jakarta.xml.soap.SOAPException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.transform.TransformerException;
import java.io.IOException;

class ConvertTest {
    @Test
    void xmlToResponse() throws SOAPException, IOException, TransformerException {
        String soapResXml = SoapUtil.getFromSoapXml(mockXml(), "ns4:getCompanyResponse");
        GetCompanyResponse response = XmlUtil.xmlToBean(soapResXml, GetCompanyResponse.class);

        Assertions.assertNotNull(response.getCompanyResponse());
    }

    //    @Test
    void requestToJson() throws IOException, SOAPException, TransformerException {
        String soapResXml = SoapUtil.getFromSoapXml(mockRequestJson(), "ser:getCompany");
        GetCompany request = XmlUtil.xmlToBean(soapResXml, GetCompany.class);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request.getCompanyRequest());
        System.out.println(jsonString);
    }

    private String mockXml() {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><ns4:getCompanyResponse xmlns:ns4="http://service.other.com" xmlns:ns2="employee" xmlns:ns3="company"><companyResponse><ns3:address>Test Address</ns3:address><employees><employee><ns2:grander>G</ns2:grander><ns2:name>Test Employee</ns2:name></employee></employees><ns3:name>Gary</ns3:name><ns3:taxId>883028</ns3:taxId></companyResponse></ns4:getCompanyResponse></soap:Body></soap:Envelope>
                                """;
    }

    private String mockRequestJson() {
        return """
                 <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.other.com" xmlns:emp="employee" xmlns:com="company">
                    <soapenv:Header/>
                    <soapenv:Body>
                       <ser:getCompany>
                          <ser:CompanyRequest>
                             <com:name>Gary</com:name>
                             <com:taxId>123456789</com:taxId>
                          </ser:CompanyRequest>
                       </ser:getCompany>
                    </soapenv:Body>
                 </soapenv:Envelope>
                """;
    }
}
