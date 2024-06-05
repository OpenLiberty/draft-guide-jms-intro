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
package io.openliberty.guides.system.health;

import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class SystemReadinessCheck implements HealthCheck {

    private static Logger logger = Logger.getLogger(
        SystemReadinessCheck.class.getName());

    @Inject
    @ConfigProperty(name = "mp.messaging.connector.liberty-kafka.bootstrap.servers")
    String kafkaServer;

    @Override
    public HealthCheckResponse call() {
        boolean up = isReady();
        return HealthCheckResponse.named(
            this.getClass().getSimpleName()).status(up).build();
    }

    private boolean isReady() {
        return true;
    }
}
