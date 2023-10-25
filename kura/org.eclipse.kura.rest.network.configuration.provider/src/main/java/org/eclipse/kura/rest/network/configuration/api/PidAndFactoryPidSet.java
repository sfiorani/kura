/*******************************************************************************
 * Copyright (c) 2021 Eurotech and/or its affiliates and others
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *  Eurotech
 *******************************************************************************/
package org.eclipse.kura.rest.network.configuration.api;

import java.util.Set;

public class PidAndFactoryPidSet {

    private final Set<PidAndFactoryPid> components;

    public PidAndFactoryPidSet(final Set<PidAndFactoryPid> components) {
        this.components = components;
    }

    public Set<PidAndFactoryPid> getComponents() {
        return components;
    }
}
