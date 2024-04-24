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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import io.openliberty.guides.system.model.SystemData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint(value = "/boardcast", encoders = { SystemsEncoder.class })
public class BoardcastService {

      private static Logger logger = Logger.getLogger(BoardcastService.class.getName());

      private static Set<Session> sessions = new HashSet<>();
    
      public static void refreshAllSessions(List<SystemData> systems) {
          for (Session session : sessions) {
              try {
                  session.getBasicRemote().sendObject(systems);
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
      }
      
      @Inject
      Inventory inventory;
      
      @OnOpen
      public void onOpen(Session session) {
          logger.info("Boardcast service connected to session: " + session.getId());
          sessions.add(session);
      }

      @OnMessage
      public void onMessage(String option, Session session) {
          logger.info("Boardcast service received message \"" + option + "\" "
                  + "from session: " + session.getId());
          if (option.equalsIgnoreCase("refresh")) {
              try {
                  session.getBasicRemote().sendObject(inventory.getSystems());
              } catch (Exception e) {
                  e.printStackTrace();
              }
          } else {
              logger.warning("Unknown option: " + option);
          }
      }

      @OnClose
      public void onClose(Session session, CloseReason closeReason) {
          logger.info("Session " + session.getId()
                      + " was closed with reason " + closeReason.getCloseCode());
          sessions.remove(session);
      }

      @OnError
      public void onError(Session session, Throwable throwable) {
          logger.info("WebSocket error for " + session.getId() + " "
                      + throwable.getMessage());
      }
}
