/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.jsoup.helper;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface RequestAuthenticator {
    public @Nullable PasswordAuthentication authenticate(Context var1);

    public static class Context {
        private final URL url;
        private final Authenticator.RequestorType type;
        private final String realm;

        Context(URL url, Authenticator.RequestorType type, String realm) {
            this.url = url;
            this.type = type;
            this.realm = realm;
        }

        public URL url() {
            return this.url;
        }

        public Authenticator.RequestorType type() {
            return this.type;
        }

        public String realm() {
            return this.realm;
        }

        public boolean isProxy() {
            return this.type == Authenticator.RequestorType.PROXY;
        }

        public boolean isServer() {
            return this.type == Authenticator.RequestorType.SERVER;
        }

        public PasswordAuthentication credentials(String username, String password) {
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }
}

