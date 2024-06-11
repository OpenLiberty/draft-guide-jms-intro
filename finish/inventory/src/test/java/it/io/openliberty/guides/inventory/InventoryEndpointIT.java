// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
// tag::testClass[]
package it.io.openliberty.guides.inventory;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryEndpointIT {

    private static String port;
    private static String baseUrl;
    private static String hostname;

    private Client client;

    private final String INVENTORY_SYSTEMS = "inventory/systems";

    @BeforeAll
    public static void oneTimeSetup() {
        port = System.getProperty("http.port");
        baseUrl = "http://localhost:" + port + "/";
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void teardown() {
        client.close();
    }


    // tag::tests[]
    @Test
    @Order(1)
    // tag::testNonEmpty[]
    public void testNonEmpty() {
        Response response = this.getResponse(baseUrl + INVENTORY_SYSTEMS);
        this.assertResponse(baseUrl, response);

        JsonArray systems = response.readEntity(JsonArray.class);

        boolean hostnameExists = false;
        boolean loadAverageExists = false;
        for (int n = 0; n < systems.size(); n++) {
            hostnameExists = systems.getJsonObject(n).get("hostname").toString().isEmpty();
            loadAverageExists = systems.getJsonObject(n).get("systemLoad").toString().isEmpty();

            assertFalse(hostnameExists, "A host was registered, but it was empty");
            assertFalse(loadAverageExists, "A load average was registered, but it was empty");
            if (!hostnameExists && !loadAverageExists) {
                String host = systems.getJsonObject(n).get("hostname").toString();
                hostname = host.substring(1, host.length() - 1);
                break;
            }
        }

        response.close();
    }
    // end::testNonEmpty[]

    // tag::testValue[]
    @Test
    @Order(2)
    public void testValue() {
        assertNotNull(hostname, "Hostname should be set by the first test.");

        Response response = this.getResponse(baseUrl + INVENTORY_SYSTEMS + "/" + hostname);
        this.assertResponse(baseUrl, response);

        JsonObject system = response.readEntity(JsonObject.class);

        String responseHostname = system.getString("hostname");
        Boolean loadAverageExists = system.get("systemLoad").toString().isEmpty();

        assertEquals(hostname, responseHostname, "Hostname should match the one from the TestNonEmpty");
        assertFalse(loadAverageExists, "A Load Average should not be empty");

        response.close();
    }
    // end::testValue[]

    @Test
    @Order(3)
    // tag::testUnknown[]
    public void testUnknown() {
        Response badResponse = client
                .target(baseUrl + INVENTORY_SYSTEMS + "/" + "badhostname")
                .request(MediaType.APPLICATION_JSON).get();

        assertEquals(404, badResponse.getStatus(),
                "BadResponse expected status: 404. Response code not as expected.");

        String stringObj = badResponse.readEntity(String.class);
        assertTrue(stringObj.contains("hostname does not exist."),
                "badhostname is not a valid host but it didn't raise an error");

        badResponse.close();
    }
    // end::testUnknown[]
    // end::tests[]

    private Response getResponse(String url) {
        return client.target(url).request().get();
    }

    private void assertResponse(String url, Response response) {
        assertEquals(200, response.getStatus(), "Incorrect response code from " + url);
    }

}
