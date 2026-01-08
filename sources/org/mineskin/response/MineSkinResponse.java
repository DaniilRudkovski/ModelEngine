/*
 * Decompiled with CFR 0.152.
 */
package org.mineskin.response;

import java.util.List;
import java.util.Optional;
import org.mineskin.data.Breadcrumbed;
import org.mineskin.data.CodeAndMessage;

public interface MineSkinResponse<T>
extends Breadcrumbed {
    public boolean isSuccess();

    public int getStatus();

    public List<CodeAndMessage> getMessages();

    public Optional<CodeAndMessage> getFirstMessage();

    public List<CodeAndMessage> getErrors();

    public boolean hasErrors();

    public Optional<CodeAndMessage> getFirstError();

    public Optional<CodeAndMessage> getErrorOrMessage();

    public List<CodeAndMessage> getWarnings();

    public Optional<CodeAndMessage> getFirstWarning();

    public String getServer();

    @Override
    public String getBreadcrumb();

    public T getBody();
}

