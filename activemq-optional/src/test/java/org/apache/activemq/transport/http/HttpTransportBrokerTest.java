/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.http;

import java.net.Socket;
import java.net.URI;
import java.net.URL;

import javax.net.SocketFactory;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transport.TransportBrokerTestSupport;
import org.apache.activemq.util.Wait;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpTransportBrokerTest extends TransportBrokerTestSupport {

    private static final Log LOG = LogFactory.getLog(HttpTransportBrokerTest.class);
    protected String getBindLocation() {
        return "http://localhost:8081";
    }

    protected void setUp() throws Exception {
        maxWait = 2000;
        super.setUp();
        waitForJettySocketToAccept(getBindLocation());
    }
    
    private void waitForJettySocketToAccept(String bindLocation) throws Exception {
        final URL url = new URL(bindLocation);
        assertTrue("Jetty endpoint is available", Wait.waitFor(new Wait.Condition() {

            public boolean isSatisified() throws Exception {
                boolean canConnect = false;
                try {
                    Socket socket = SocketFactory.getDefault().createSocket(url.getHost(), url.getPort());
                    socket.close();
                    canConnect = true;
                } catch (Exception e) {
                    LOG.warn("verify jettty available, failed to connect to " + url + e);
                }
                return canConnect;
            }}, 60 * 1000));
        
    }

    protected BrokerService createBroker() throws Exception {
		BrokerService broker = BrokerFactory.createBroker(new URI("broker:()/localhost?persistent=false&useJmx=false"));
		connector = broker.addConnector(getBindLocation());
		return broker;
	}

	protected void tearDown() throws Exception {
        super.tearDown();
        // Give the jetty server enough time to shutdown before starting another one
        Thread.sleep(100);
    }

    public static Test suite() {
        return suite(HttpTransportBrokerTest.class);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

}
