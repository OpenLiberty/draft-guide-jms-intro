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
    MessageProducer producer;

    public void add(SystemData system) {
        em.persist(system);
        producer.sendMessage("add", system);
    }

    public void update(SystemData system) {
        SystemData updated = em.merge(system);
        producer.sendMessage("update", system);
        logger.info("Before: " + system.toString());
        logger.info("updated: " + updated.toString());
    }

    public void remove(SystemData system) {
        String hostname = system.getHostname();
        SystemData s = getSystem(hostname);
        if (s == null) {
            logger.warning(hostname + " does not exists." );
        } else {
            em.remove(s);
        }
        producer.sendMessage("remove", system);
    }

    public SystemData getSystem(String hostname) {
        List<SystemData> systems =
            em.createNamedQuery("SystemData.findSystem", SystemData.class)
              .setParameter("hostname", hostname)
              .getResultList();
        return systems == null || systems.isEmpty() ? null : systems.get(0);
    }
}
