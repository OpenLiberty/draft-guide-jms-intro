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
import io.openliberty.guides.system.model.SystemData;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

@Stateless
public class RefreshQueueMessageProducer {

      private static Logger logger = Logger.getLogger(RefreshQueueMessageProducer.class.getName());

      @Inject
      @JMSConnectionFactory("RefreshQueueConnectionFactory")
      JMSContext context;

      @Resource(lookup = "jms/RefreshQueue")
      Queue queue;

      public void sendMessage(String action, SystemData system) {
          String message = new CQMessage(action, system).toString();
          context.createProducer().send(queue, message);
          logger.info("Sent message to RefreshQueue: " + message);
      }

}
