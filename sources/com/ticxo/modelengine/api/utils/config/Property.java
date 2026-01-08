/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 */
package com.ticxo.modelengine.api.utils.config;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.generator.BaseItemEnum;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import org.bukkit.configuration.ConfigurationSection;

public interface Property {
    public String getPath();

    public Object getDef();

    default public int getInt() {
        return ModelEngineAPI.getAPI().getConfigManager().getInt(this);
    }

    default public int getIntOrIfNeg(Supplier<Integer> supplier) {
        int i = this.getInt();
        return i < 0 ? supplier.get() : i;
    }

    default public double getDouble() {
        return ModelEngineAPI.getAPI().getConfigManager().getDouble(this);
    }

    default public String getString() {
        return ModelEngineAPI.getAPI().getConfigManager().getString(this);
    }

    default public boolean getBoolean() {
        return ModelEngineAPI.getAPI().getConfigManager().getBoolean(this);
    }

    default public List<String> getStringList() {
        return ModelEngineAPI.getAPI().getConfigManager().getStringList(this);
    }

    default public ConfigurationSection getSection() {
        return ModelEngineAPI.getAPI().getConfigManager().getConfigSection(this.getPath());
    }

    default public ConfigurationSection getSection(String path) {
        return ModelEngineAPI.getAPI().getConfigManager().getConfigSection(this.getPath() + "." + path);
    }

    default public BaseItemEnum getBaseItem() {
        return BaseItemEnum.get(this.getString().toUpperCase(Locale.ENGLISH));
    }

    default public Set<BaseItemEnum> getBaseItems() {
        HashSet<BaseItemEnum> set = new HashSet<BaseItemEnum>();
        for (String s : this.getStringList()) {
            BaseItemEnum item = BaseItemEnum.get(s.toUpperCase(Locale.ENGLISH));
            if (item == null) continue;
            set.add(item);
        }
        return set;
    }

    default public CullType getCullType() {
        return CullType.get(this.getString().toUpperCase(Locale.ENGLISH));
    }
}

