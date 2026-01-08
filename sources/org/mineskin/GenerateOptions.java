/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.gson.JsonObject
 */
package org.mineskin;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.mineskin.data.Variant;
import org.mineskin.data.Visibility;

public class GenerateOptions {
    private String name;
    private Variant variant;
    private Visibility visibility;
    private String cape;

    private GenerateOptions() {
    }

    public static GenerateOptions create() {
        return new GenerateOptions();
    }

    public GenerateOptions name(String name) {
        this.name = name;
        return this;
    }

    public GenerateOptions variant(Variant variant) {
        this.variant = variant;
        return this;
    }

    public GenerateOptions visibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    public GenerateOptions cape(UUID cape) {
        this.cape = cape.toString();
        return this;
    }

    public GenerateOptions cape(String cape) {
        this.cape = cape;
        return this;
    }

    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (!Strings.isNullOrEmpty((String)this.name)) {
            json.addProperty("name", this.name);
        }
        if (this.variant != null && this.variant != Variant.AUTO) {
            json.addProperty("variant", this.variant.getName());
        }
        if (this.visibility != null) {
            json.addProperty("visibility", this.visibility.getName());
        }
        if (this.cape != null) {
            json.addProperty("cape", this.cape);
        }
        return json;
    }

    protected Map<String, String> toMap() {
        HashMap<String, String> data = new HashMap<String, String>();
        this.addTo(data);
        return data;
    }

    protected void addTo(Map<String, String> data) {
        if (!Strings.isNullOrEmpty((String)this.name)) {
            data.put("name", this.name);
        }
        if (this.variant != null && this.variant != Variant.AUTO) {
            data.put("variant", this.variant.getName());
        }
        if (this.visibility != null) {
            data.put("visibility", this.visibility.getName());
        }
        if (this.cape != null) {
            data.put("cape", this.cape);
        }
    }

    public String getName() {
        return this.name;
    }
}

