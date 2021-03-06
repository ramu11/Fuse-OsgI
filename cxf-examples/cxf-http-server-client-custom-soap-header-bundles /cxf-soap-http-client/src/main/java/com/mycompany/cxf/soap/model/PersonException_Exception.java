
package com.mycompany.cxf.soap.model;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.0.4.redhat-621107
 * 2016-09-22T19:08:32.060+05:30
 * Generated source version: 3.0.4.redhat-621107
 */

@WebFault(name = "PersonException", targetNamespace = "http://model.soap.cxf.mycompany.com/")
public class PersonException_Exception extends Exception {
    
    private com.mycompany.cxf.soap.model.PersonException personException;

    public PersonException_Exception() {
        super();
    }
    
    public PersonException_Exception(String message) {
        super(message);
    }
    
    public PersonException_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public PersonException_Exception(String message, com.mycompany.cxf.soap.model.PersonException personException) {
        super(message);
        this.personException = personException;
    }

    public PersonException_Exception(String message, com.mycompany.cxf.soap.model.PersonException personException, Throwable cause) {
        super(message, cause);
        this.personException = personException;
    }

    public com.mycompany.cxf.soap.model.PersonException getFaultInfo() {
        return this.personException;
    }
}
