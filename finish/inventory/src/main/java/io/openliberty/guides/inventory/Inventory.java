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

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.openliberty.guides.inventory.client.QueryClient;
import io.openliberty.guides.system.model.SystemData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Inventory {

    @Inject
    @RestClient
    QueryClient queryClient;

    public List<SystemData> getSystems() {
        return queryClient.getSystems();
    }

    public SystemData getSystem(String hostname) {
        return queryClient.getSystem(hostname);
    }
    
}
