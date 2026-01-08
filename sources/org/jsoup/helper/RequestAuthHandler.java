/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jsoup.helper.RequestAuthHandler
 */
package org.jsoup.helper;

import java.net.HttpURLConnection;
import org.jsoup.helper.AuthenticationHandler;
import org.jsoup.helper.RequestAuthenticator;

class RequestAuthHandler
implements AuthenticationHandler.AuthShim {
    @Override
    public void enable(RequestAuthenticator auth, HttpURLConnection con) {
        AuthenticationHandler authenticator = new AuthenticationHandler(auth);
        con.setAuthenticator(authenticator);
    }

    @Override
    public void remove() {
    }

    @Override
    public AuthenticationHandler get(AuthenticationHandler helper) {
        return helper;
    }
}

