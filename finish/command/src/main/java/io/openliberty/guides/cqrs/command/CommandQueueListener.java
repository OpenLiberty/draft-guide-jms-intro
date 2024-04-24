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

import java.util.logging.Logger;

import io.openliberty.guides.system.model.CQMessage;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@MessageDriven(mappedName="jms/CommandQueue")
public class CommandQueueListener implements MessageListener {

    private static Logger logger = Logger.getLogger(CommandQueueListener.class.getName());

    @Inject
    CommandService commandService;

    @Override
    public void onMessage(Message message) {

        try {
            if (message instanceof TextMessage) {
                TextMessage tm = (TextMessage) message;
                logger.info("CommandQueue received message: " + tm.getText());
                CQMessage cqMessage = CQMessage.fromJson(tm.getText());
                String action = cqMessage.getAction();
                if (action.equalsIgnoreCase("add")) {
                    commandService.add(cqMessage.getSystemData());
                } else if (action.equalsIgnoreCase("update")) {
                    commandService.update(cqMessage.getSystemData());
                } else if (action.equalsIgnoreCase("remove")) {
                    commandService.remove(cqMessage.getSystemData());
                } else {
                    logger.warning("Unknown CommandQueue action: " + action);
                }
            } else {
                logger.warning("CommandQueue received a non-text message: " + message);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
