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
import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryEndpointIT {

    private static String port;
    private static String baseUrl;

    private Client client;

    private final String INVENTORY_SYSTEMS = "inventory/systems";

    @BeforeAll
    public static void oneTimeSetup() {
        port = System.getProperty("http.port");
        baseUrl = "http://localhost:" + port + "/";
    }

    @BeforeEach
    public void setup() {
        System.out.println("client setting up");
        client = ClientBuilder.newClient();
        System.out.println("Client setup complete: " + (client != null));
    }

    @AfterEach
    public void teardown() {
        client.close();
    }


    // tag::tests[]
    @Test
    @Order(1)
    // tag::testHostRegistration[]
    public void testInventorySystem() {
        Response response = this.getResponse(baseUrl + INVENTORY_SYSTEMS);
        this.assertResponse(baseUrl, response);
        System.out.println("***: " + response.readEntity(String.class));

        JsonObject obj = response.readEntity(JsonObject.class);
        JsonArray systems = obj.getJsonArray("systems");

        boolean localhostExists = false;
        for (int n = 0; n < systems.size(); n++) {
            localhostExists = systems.getJsonObject(n).get("hostname").toString()
                    .contains("localhost");
            if (localhostExists) {
                break;
            }
        }
        assertTrue(localhostExists, "A host was registered, but it was not localhost");

        response.close();
    }
    // end::testHostRegistration[]
    /**
     * <p>
     * Returns response information from the specified URL.
     * </p>
     *
     * @param url - target URL.
     * @return Response object with the response from the specified URL.
     */
    // end::javadoc[]
    private Response getResponse(String url) {
        return client.target(url).request().get();
    }

    // tag::javadoc[]
    /**
     * <p>
     * Asserts that the given URL has the correct response code of 200.
     * </p>
     *
     * @param url      - target URL.
     * @param response - response received from the target URL.
     */
    // end::javadoc[]
    private void assertResponse(String url, Response response) {
        assertEquals(200, response.getStatus(), "Incorrect response code from " + url);
    }

}
