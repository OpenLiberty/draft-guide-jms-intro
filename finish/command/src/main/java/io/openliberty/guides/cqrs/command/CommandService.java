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

import io.openliberty.guides.system.model.CQMessage;
import io.openliberty.guides.system.model.SystemData;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class CommandService {

    private static Logger logger = Logger.getLogger(CommandService.class.getName());

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    @Inject
    @JMSConnectionFactory("QueryQueueConnectionFactory")
    private JMSContext jmsContext;

    @Resource(lookup = "jms/QueryQueue")
    private Queue queryQueue;

    public void add(SystemData system) {
        em.persist(system);
        sendMessageToQueryQueue("add", system);
    }

    public void update(SystemData system) {
        em.merge(system);
        sendMessageToQueryQueue("update", system);
    }

    public void remove(SystemData system) {
        String hostname = system.getHostname();
        SystemData s = getSystem(hostname);
        if (s == null) {
            logger.warning(hostname + " does not exists." );
        } else {
            em.remove(s);
        }
        sendMessageToQueryQueue("remove", system);
    }

    public SystemData getSystem(String hostname) {
        List<SystemData> systems =
            em.createNamedQuery("SystemData.findSystem", SystemData.class)
              .setParameter("hostname", hostname)
              .getResultList();
        return systems == null || systems.isEmpty() ? null : systems.get(0);
    }

    private void sendMessageToQueryQueue(String action, SystemData system) {
        JMSProducer producer = jmsContext.createProducer();
        if (queryQueue == null) {
            logger.warning("QueryQueue is null.");
        } else {
            String message = new CQMessage(action, system).toString();
            producer.send(queryQueue, message);
            logger.info("Sent message to QueryQueue: " + message);
        }
    }

}
