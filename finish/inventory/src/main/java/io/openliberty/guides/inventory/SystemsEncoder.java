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

import java.util.List;

import io.openliberty.guides.system.model.SystemData;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;

public class SystemsEncoder implements Encoder.Text<List<SystemData>> {

    @Override
    public String encode(List<SystemData> systems) throws EncodeException {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (SystemData s : systems) {
            builder.add(
                Json.createObjectBuilder()
                    .add("id", s.getId())
                    .add("hostname", s.getHostname())
                    .add("osName", s.getOsName())
                    .add("javaVersion", s.getJavaVersion())
                    .add("heapSize", s.getHeapSize())
            );
        }
        return builder.build().toString();
    }
}
