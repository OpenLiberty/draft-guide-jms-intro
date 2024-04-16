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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.openliberty.guides.system.model.SystemData;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Inventory {

    private List<SystemData> systems = Collections.synchronizedList(new ArrayList<>());

    public List<SystemData> getSystems() {
        return systems;
    }

    public SystemData getSystem(String hostname) {
        for (SystemData s : systems) {
            if (s.getHostname().equalsIgnoreCase(hostname)) {
                return s;
            }
        }
        return null;
    }

    public void add(SystemData s) throws Exception {
        for (SystemData system : systems) {
            if (system.getHostname().equalsIgnoreCase(s.getHostname())) {
                systems.remove(system);
            }
        }
        systems.add(s);
    }

    public void update(SystemData s) throws Exception {
        for (SystemData system : systems) {
            if (system.getHostname().equalsIgnoreCase(s.getHostname())) {
                system.setOsName(s.getOsName());
                system.setJavaVersion(s.getJavaVersion());
                system.setHeapSize(s.getHeapSize());
            }
        }
    }

    public void remove(SystemData s) throws Exception {
    	systems.remove(s);
    }

}
