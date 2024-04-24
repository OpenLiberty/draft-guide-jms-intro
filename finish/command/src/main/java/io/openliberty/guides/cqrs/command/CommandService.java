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
package io.openliberty.guides.cqrs.command;

import java.util.List;
import java.util.logging.Logger;

import io.openliberty.guides.system.model.SystemData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class CommandService {

    private static Logger logger = Logger.getLogger(CommandService.class.getName());

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    @Inject
    RefreshQueueMessageProducer producer;

    public void add(SystemData system) {
        String hostname = system.getHostname();
        if (getSystem(hostname) == null) {
            em.persist(system);
            producer.sendMessage("add", system);
        } else {
            logger.warning(hostname + " exists." );
        }
    }

    public void update(SystemData system) {
        String hostname = system.getHostname();
        SystemData s = getSystem(hostname);
        if (s == null) {
            logger.warning(hostname + " does not exists to update." );
        } else {
            SystemData updated = em.merge(system);
            producer.sendMessage("update", updated);
        }
    }

    public void remove(SystemData system) {
        String hostname = system.getHostname();
        SystemData s = getSystem(hostname);
        if (s == null) {
            logger.warning(hostname + " does not exists to remove." );
        } else {
            em.remove(s);
        }
        producer.sendMessage("remove", system);
    }

    private SystemData getSystem(String hostname) {
        List<SystemData> systems =
            em.createNamedQuery("SystemData.findSystem", SystemData.class)
              .setParameter("hostname", hostname)
              .getResultList();
        return systems == null || systems.isEmpty() ? null : systems.get(0);
    }
}
