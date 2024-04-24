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
package io.openliberty.guides.inventory;

import io.openliberty.guides.system.model.SystemData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/systems")
public class SystemsResource {

    @Inject
    CommandQueueMessageProducer producer;
    
    @Inject
    Inventory inventory;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSystem(SystemData s) {
        String hostname = s.getHostname();
        SystemData system = inventory.getSystem(hostname);
        if (system != null) {
            return fail(hostname + " already exists.");
        }
        producer.sendMessage("add", s);
        return success("submitted to add " + s.getHostname() + ".");
    }

    @PUT
    @Path("/{hostname}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSystem(
        @PathParam("hostname") String hostname,
        SystemData s) {

        SystemData system = inventory.getSystem(hostname);
        if (system == null) {
            return fail(hostname + " does not exists.");
        }
        system.setOsName(s.getOsName());
        system.setJavaVersion(s.getJavaVersion());
        system.setHeapSize(s.getHeapSize());
        producer.sendMessage("update", system);
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
        producer.sendMessage("remove", system);
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

}
