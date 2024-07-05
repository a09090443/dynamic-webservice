
package com.company.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>getCompanyResponse complex type �� Java ���O.
 *
 * <p>�U�C���n���q�|���w�����O���]�t���w�����e.
 *
 * <pre>
 * &lt;complexType name="getCompanyResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="companyResponse" type="{http://service.other.com}companyResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCompanyResponse", propOrder = {
    "companyResponse"
})
public class GetCompanyResponse {

    @XmlElement(namespace = "")
    protected CompanyResponse companyResponse;

    /**
     * ���o companyResponse �S�ʪ���.
     *
     * @return
     *     possible object is
     *     {@link CompanyResponse }
     *
     */
    public CompanyResponse getCompanyResponse() {
        return companyResponse;
    }

    /**
     * �]�w companyResponse �S�ʪ���.
     *
     * @param value
     *     allowed object is
     *     {@link CompanyResponse }
     *
     */
    public void setCompanyResponse(CompanyResponse value) {
        this.companyResponse = value;
    }

}
