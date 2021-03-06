/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.servicemix.examples.cxf;

import java.io.Closeable;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.camel.example.reportincident.InputReportIncident;
import org.apache.camel.example.reportincident.ReportIncidentEndpoint;
import org.apache.camel.example.reportincident.ReportIncidentEndpointService;
import org.apache.camel.example.reportincident.OutputReportIncident;
import org.apache.cxf.ws.security.wss4j.DefaultCryptoCoverageChecker;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;

public final class Client {
   
    private static QName portName = new QName("http://reportincident.example.camel.apache.org", "ReportIncidentService"); 

    private Client() {
    }

    public static void main(String args[]) throws Exception {
        try {
            Bus bus = BusFactory.getDefaultBus();
            Map<String, Object> outProps = new HashMap<String, Object>();
            outProps.put("action", "Signature");

            outProps.put("user", "clientx509v1");
            outProps.put("passwordCallbackClass", "org.apache.servicemix.examples.cxf.UTPasswordCallback");
            outProps.put("signaturePropFile", "etc/Client_Sign.properties");
            outProps.put("signatureKeyIdentifier", "DirectReference");
            outProps.put("signatureParts",
                         "{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");

            bus.getOutInterceptors().add(new LoggingOutInterceptor());
            bus.getOutInterceptors().add(new WSS4JOutInterceptor(outProps));

            Map<String, Object> inProps = new HashMap<String, Object>();
            inProps.put("action", "Signature");
            inProps.put("signaturePropFile", "etc/Client_Encrypt.properties");
            inProps.put("signatureKeyIdentifier", "DirectReference");

            bus.getInInterceptors().add(new LoggingInInterceptor());
            bus.getInInterceptors().add(new WSS4JInInterceptor(inProps));

            // Check to make sure that the SOAP Body was signed
            DefaultCryptoCoverageChecker coverageChecker = new DefaultCryptoCoverageChecker();
            coverageChecker.setSignBody(true);
            //coverageChecker.setSignTimestamp(true);
            //coverageChecker.setEncryptBody(true);
            bus.getInInterceptors().add(coverageChecker);

            ReportIncidentEndpointService service = new ReportIncidentEndpointService();
            String hostname = System.getProperty("hostname");
            if (hostname == null) {
                hostname = "localhost";
            }
            String serverPort = System.getProperty("port");
            if (serverPort == null) {
                serverPort = "9001";
            }
            service.addPort(portName, "http://reportincident.example.camel.apache.org", "http://" + hostname + ":" + (new Integer(serverPort)).intValue() + "/incident");
            //service.addPort(portName, "http://reportincident.example.camel.apache.org", "http://localhost:9090/incident");
            ReportIncidentEndpoint port = service.getReportIncidentService();
 
            InputReportIncident input = new InputReportIncident();
            input.setIncidentId("123");
            input.setIncidentDate("2008-08-18");
            input.setGivenName("Claus");
            input.setFamilyName("Ibsen");
            input.setSummary("Bla");
            input.setDetails("Bla bla");
            input.setEmail("davsclaus@apache.org");
            input.setPhone("0045 2962 7576");
  
            OutputReportIncident out = port.reportIncident(input);

            System.out.println("received back: " + out.getCode());

            if (port instanceof Closeable) {
                ((Closeable)port).close();
            }

            bus.shutdown(true);

        } catch (UndeclaredThrowableException ex) {
            ex.getUndeclaredThrowable().printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
