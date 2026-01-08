/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Pair
 */
package com.ticxo.modelengine.api.generator.parser;

import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.generator.assets.ModelAssets;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import it.unimi.dsi.fastutil.Pair;
import java.io.File;

public interface ModelParser {
    public boolean validateFile(File var1);

    public Pair<ModelBlueprint, ModelAssets> generate(File var1, ErrorCollector var2) throws Exception;
}

