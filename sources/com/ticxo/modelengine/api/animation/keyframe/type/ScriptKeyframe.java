/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.api.animation.keyframe.type;

import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class ScriptKeyframe
extends AbstractKeyframe<List<Script>> {
    private final List<Script> script = new ArrayList<Script>();

    @Override
    public List<Script> getValue(int index, IAnimationProperty property) {
        return this.script;
    }

    @Generated
    public List<Script> getScript() {
        return this.script;
    }

    public record Script(String reader, String script) {
        public static Script from(String full) {
            String[] split = full.split(":", 2);
            if (split.length <= 1) {
                return new Script("meg", split[0]);
            }
            return new Script(split[0], split[1]);
        }
    }
}

