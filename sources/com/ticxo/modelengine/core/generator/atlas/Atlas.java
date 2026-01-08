/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.ticxo.modelengine.core.generator.atlas;

import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class Atlas {
    private final List<Source> sources = new ArrayList<Source>();

    @Generated
    public List<Source> getSources() {
        return this.sources;
    }

    public static class Filter
    extends Source {
        public final ResourceLocation pattern;

        public Filter(ResourceLocation pattern) {
            super("filter");
            this.pattern = pattern;
        }
    }

    public static class Directory
    extends Source {
        public final String source;
        public final String prefix;

        public Directory(String location) {
            super("directory");
            this.source = location;
            this.prefix = location + "/";
        }
    }

    public static class Single
    extends Source {
        public final String resource;
        public final String sprite;

        public Single(String location) {
            super("single");
            this.resource = location;
            this.sprite = location;
        }
    }

    public static class Source {
        public final String type;

        @Generated
        public Source(String type) {
            this.type = type;
        }
    }
}

