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

import java.util.List;

import org.eclipse.persistence.config.QueryHints;

import io.openliberty.guides.system.model.SystemData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class QueryService {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public List<SystemData> getSystems() {
        return em.createNamedQuery("SystemData.findAll", SystemData.class)
                 .setHint(QueryHints.REFRESH, true)
                 .getResultList();
    }

    public SystemData getSystem(String hostname) {
        List<SystemData> systems =
            em.createNamedQuery("SystemData.findSystem", SystemData.class)
              .setParameter("hostname", hostname)
              .setHint(QueryHints.REFRESH, true)
              .getResultList();
        return systems == null || systems.isEmpty() ? null : systems.get(0);
    }
}
