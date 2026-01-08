/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.api.generator.parser.blockbench;

import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;

public interface BlockbenchBehaviorParser {
    public void processModel(ErrorCollector var1, BlockbenchModel var2, ModelBlueprint var3);

    public void processBone(ErrorCollector var1, BlockbenchModel var2, BlockbenchModel.Group var3, BlueprintBone var4);
}

