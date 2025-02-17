/*******************************************************************************
 * Copyright (c) 2011, 2020 Eurotech and/or its affiliates and others
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *  Eurotech
 ******************************************************************************/
package org.eclipse.kura.configuration;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @noextend This class is not intended to be subclassed by clients.
 */
@ProviderType
public class Password {

    private char[] passwordVal;
    private InputStream passwordValStream;

    public Password(String password) {
        super();
        if (password != null) {
            this.passwordVal = password.toCharArray();
            this.passwordValStream = new ByteArrayInputStream(password.getBytes());
        }
    }

    public Password(char[] password) {
        super();
        this.passwordVal = password;
        this.passwordValStream = new ByteArrayInputStream(new String(password).getBytes());
    }

    public Password(InputStream password) {
        super();
        this.passwordValStream = password;
        this.passwordVal = new BufferedReader(new InputStreamReader(password, StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n")).toCharArray();
    }

    public char[] getPassword() {
        return this.passwordVal;
    }

    public InputStream getPasswordStream() {
        return this.passwordValStream;
    }

    @Override
    public String toString() {
        return new String(this.passwordVal);
    }
}
