
package com.company.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>companyResponse complex type �� Java ���O.
 *
 * <p>�U�C���n���q�|���w�����O���]�t���w�����e.
 *
 * <pre>
 * &lt;complexType name="companyResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{company}address" minOccurs="0"/&gt;
 *         &lt;element name="employees" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="employee" type="{http://service.other.com}employee" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element ref="{company}name" minOccurs="0"/&gt;
 *         &lt;element ref="{company}taxId" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "companyResponse", propOrder = {
    "address",
    "employees",
    "name",
    "taxId"
})
public class CompanyResponse {

    @XmlElement
    protected String address;
    @XmlElement(name = "employees", namespace = "")
    protected Employees employees = new Employees();
    @XmlElement(namespace = "company")
    protected String name;
    @XmlElement(namespace = "company")
    protected String taxId;

    /**
     * ���o address �S�ʪ���.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAddress() {
        return address;
    }

    /**
     * �]�w address �S�ʪ���.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * ���o employees �S�ʪ���.
     *
     * @return
     *     possible object is
     *     {@link Employees }
     *
     */
    public Employees getEmployees() {
        return employees;
    }

    /**
     * �]�w employees �S�ʪ���.
     *
     * @param value
     *     allowed object is
     *     {@link Employees }
     *
     */
    public void setEmployees(Employees value) {
        this.employees = value;
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

    /**
     * ���o taxId �S�ʪ���.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTaxId() {
        return taxId;
    }

    /**
     * �]�w taxId �S�ʪ���.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTaxId(String value) {
        this.taxId = value;
    }


    /**
     * <p>anonymous complex type �� Java ���O.
     *
     * <p>�U�C���n���q�|���w�����O���]�t���w�����e.
     *
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="employee" type="{http://service.other.com}employee" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "employee"
    })
    public static class Employees {

        @XmlElement(name="employee", namespace = "", nillable = true)
        @JacksonXmlProperty(localName = "employee", namespace = "")
        @JacksonXmlElementWrapper(useWrapping = false)
        protected List<Employee> employee;

        /**
         * Gets the value of the employee property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the employee property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEmployee().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Employee }
         *
         *
         */
        public List<Employee> getEmployee() {
            if (employee == null) {
                employee = new ArrayList<Employee>();
            }
            return this.employee;
        }

    }

}
