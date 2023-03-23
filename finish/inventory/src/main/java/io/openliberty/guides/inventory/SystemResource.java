// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.inventory;

import java.util.List;
import java.util.logging.Logger;

import io.openliberty.guides.system.model.CQMessage;
import io.openliberty.guides.system.model.SystemData;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/systems")
public class SystemResource {

    private static Logger logger = Logger.getLogger(SystemResource.class.getName());

    @Inject
    Inventory inventory;

    @Inject
    @JMSConnectionFactory("CommandQueueConnectionFactory")
    private JMSContext jmsContext;

    @Resource(lookup = "jms/CommandQueue")
    private Queue commandQueue;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SystemData> listContents() {
        return inventory.getSystems();
    }

    @GET
    @Path("/{hostname}")
    @Produces(MediaType.APPLICATION_JSON)
    public SystemData getSystem(@PathParam("hostname") String hostname) {
        return inventory.getSystem(hostname);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSystem(
        @QueryParam("hostname") String hostname,
        @QueryParam("osName") String osName,
        @QueryParam("javaVersion") String javaVersion,
        @QueryParam("heapSize") Long heapSize) {

        SystemData system = inventory.getSystem(hostname);
        if (system != null) {
            return fail(hostname + " already exists.");
        }
        SystemData s = new SystemData(hostname, osName, javaVersion, heapSize);
        sendMessageToCommandQueue("add", s);
        return success("submitted to add " + hostname + ".");
    }

    @PUT
    @Path("/{hostname}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSystem(
        @PathParam("hostname") String hostname,
        @QueryParam("osName") String osName,
        @QueryParam("javaVersion") String javaVersion,
        @QueryParam("heapSize") Long heapSize) {

        SystemData system = inventory.getSystem(hostname);
        if (system == null) {
            return fail(hostname + " does not exists.");
        }
        SystemData s = new SystemData(hostname, osName, javaVersion, heapSize);
        s.setId(system.getId());
        sendMessageToCommandQueue("update", s);
        return success("submitted to update " + hostname + ".");
    }

    @DELETE
    @Path("/{hostname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSystem(@PathParam("hostname") String hostname) {

        SystemData system = inventory.getSystem(hostname);
        if (system == null) {
            return fail(hostname + " does not exists.");
        }
        sendMessageToCommandQueue("remove", system);
        return success("submitted to remove " + hostname + ".");
    }

    private Response success(String message) {
        return Response.ok("{ \"ok\" : \"" + message + "\" }").build();
    }

    private Response fail(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity("{ \"error\" : \"" + message + "\" }")
                       .build();
    }

    private void sendMessageToCommandQueue(String action, SystemData system) {
        JMSProducer producer = jmsContext.createProducer();
        if (commandQueue == null) {
            logger.warning("CommandQueue is null.");
        } else {
            String message = new CQMessage(action, system).toString();
            producer.send(commandQueue, message);
            logger.info("Sent message to CommandQueue: " + message);
        }
    }
}
