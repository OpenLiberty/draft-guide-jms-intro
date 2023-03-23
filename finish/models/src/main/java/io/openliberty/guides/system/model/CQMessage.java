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
package io.openliberty.guides.system.model;

import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class CQMessage {

    private static final Jsonb jsonb = JsonbBuilder.create();

    private String action;
    private SystemData systemData;

    public CQMessage() {
        this.action = "";
        this.systemData = null;
    }

    public CQMessage(String type, SystemData systemData) {
        this.action = type;
        this.systemData = systemData;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public SystemData getSystemData() {
        return systemData;
    }

    public void setSystemData(SystemData systemData) {
        this.systemData = systemData;
    }

    public String toString() {
        return jsonb.toJson(this);
    }

    public static CQMessage fromJson(String jsonStr) {
        JsonObject jObj = jsonb.fromJson(jsonStr, JsonObject.class);
        return new CQMessage(
                       jObj.getString("action"),
                       new SystemData(jObj.getJsonObject("systemData"))
                   );
    }

}
