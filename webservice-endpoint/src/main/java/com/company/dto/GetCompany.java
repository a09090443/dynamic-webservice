
package com.company.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>getCompany complex type �� Java ���O.
 *
 * <p>�U�C���n���q�|���w�����O���]�t���w�����e.
 *
 * <pre>
 * &lt;complexType name="getCompany"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CompanyRequest" type="{http://service.other.com}companyRequest" minOccurs="0" form="qualified"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCompany", propOrder = {
    "companyRequest"
})
public class GetCompany {

    @XmlElement(name = "CompanyRequest")
    @JacksonXmlProperty(localName = "CompanyRequest")
    protected CompanyRequest companyRequest;

    /**
     * ���o companyRequest �S�ʪ���.
     *
     * @return
     *     possible object is
     *     {@link CompanyRequest }
     *
     */
    public CompanyRequest getCompanyRequest() {
        return companyRequest;
    }

    /**
     * �]�w companyRequest �S�ʪ���.
     *
     * @param value
     *     allowed object is
     *     {@link CompanyRequest }
     *
     */
    public void setCompanyRequest(CompanyRequest value) {
        this.companyRequest = value;
    }

}
