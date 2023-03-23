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
package io.openliberty.guides.cqrs.query;

import java.util.logging.Logger;

import io.openliberty.guides.system.model.CQMessage;
import io.openliberty.guides.system.model.SystemData;
import jakarta.annotation.Resource;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;

@MessageDriven(mappedName="jms/QueryQueue")
public class QueryListener implements MessageListener {

    private static Logger logger = Logger.getLogger(QueryListener.class.getName());

    @Inject
    QueryService queryService;

    @Inject
    @JMSConnectionFactory("CacheQueueConnectionFactory")
    private JMSContext jmsContext;
    
    @Resource(lookup = "jms/CacheQueue")
    private Queue cacheQueue;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage tm = (TextMessage) message;
                logger.info("QueryQueue received message: "  + tm.getText());
                CQMessage writeMessage = CQMessage.fromJson(tm.getText());
                SystemData system = writeMessage.getSystemData();
                String action = writeMessage.getAction();
                if (action.equalsIgnoreCase("remove")) {
                    sendMessageToCacheQueue(action, system);
                } else {
                    String hostname = system.getHostname();
                    SystemData s = queryService.getSystem(hostname);
                    sendMessageToCacheQueue(action, s);
                }
            } else {
                logger.warning("QueryQueue received a non-text message: " + message);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendMessageToCacheQueue(String action, SystemData system) {
        JMSProducer producer = jmsContext.createProducer();
        if (cacheQueue == null) {
            logger.warning("CacheQueue is null");
        } else {
            String message = new CQMessage(action, system).toString();
            producer.send(cacheQueue, message);
            logger.info("Sent message to CacheQueue: " + message);
        }
    }
}