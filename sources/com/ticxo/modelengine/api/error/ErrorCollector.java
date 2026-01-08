/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.error;

import com.ticxo.modelengine.api.error.IError;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class ErrorCollector {
    private final String fileName;
    private final List<IError> errorList = new ArrayList<IError>();

    public void collect(IError error) {
        this.errorList.add(error);
    }

    public void logAll() {
        TLogger.log();
        TLogger.log(LogColor.CYAN + "Importing " + this.fileName + ".");
        this.errorList.forEach(IError::log);
    }

    public boolean hasError() {
        return !this.errorList.isEmpty();
    }

    @Generated
    public ErrorCollector(String fileName) {
        this.fileName = fileName;
    }

    @Generated
    public String getFileName() {
        return this.fileName;
    }

    @Generated
    public List<IError> getErrorList() {
        return this.errorList;
    }
}

