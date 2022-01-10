/*
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
package de.thoughtgang.camel.xtokenize.testcase;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.builder.Namespaces;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelTestSupportTest extends CamelTestSupport {
    
    private static String xml;
    
    private static final Logger LOG = LoggerFactory.getLogger(CamelTestSupportTest.class);
    
    @BeforeAll
    public static void init() throws IOException, URISyntaxException {
        
        xml = new String(Files.readAllBytes(Paths.get(CamelTestSupportTest.class.getClassLoader().getResource("test.xml").toURI())));
        
        
    }

    @Test
    public void test() throws Exception {
        
        getMockEndpoint("mock:result").expectedMessageCount(3);
        getMockEndpoint("mock:result").expectedMessagesMatches(exchange -> exchange.getIn().getBody(String.class).contains("<Level2preceding>Included</Level2preceding>"),
                                                               exchange -> exchange.getIn().getBody(String.class).contains("<Level2following>Not Included</Level2following>"));

        template.sendBody("direct:start", xml);

        assertMockEndpointsSatisfied();
    }



    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .split().xtokenize("/root/Level1/Level2/data", 'w',  new Namespaces("ns1", ""))
                        .log("${body}")
                        .to("mock:result");
            }
        };
    }
}