
package com.company.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>employee complex type �� Java ���O.
 *
 * <p>�U�C���n���q�|���w�����O���]�t���w�����e.
 *
 * <pre>
 * &lt;complexType name="employee"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{employee}grander" minOccurs="0"/&gt;
 *         &lt;element ref="{employee}name" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "employee", propOrder = {
    "grander",
    "name"
})
public class Employee {

    @XmlElement(namespace = "employee")
    protected String grander;
    @XmlElement(namespace = "employee")
    protected String name;

    /**
     * ���o grander �S�ʪ���.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGrander() {
        return grander;
    }

    /**
     * �]�w grander �S�ʪ���.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGrander(String value) {
        this.grander = value;
    }

    /**
     * ���o name �S�ʪ���.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * �]�w name �S�ʪ���.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

}
