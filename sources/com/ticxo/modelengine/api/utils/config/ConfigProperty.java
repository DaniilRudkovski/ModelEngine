/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.MemoryConfiguration
 *  org.jetbrains.annotations.Nullable
 */
package com.ticxo.modelengine.api.utils.config;

import com.ticxo.modelengine.api.utils.config.Property;
import java.util.List;
import lombok.Generated;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.Nullable;

public enum ConfigProperty implements Property
{
    ENGINE("Model-Engine"),
    GENERATOR("Model-Generator"),
    OPTIMIZATION("Network-Optimization"),
    DEFAULT_NAMES(ENGINE, "Default-Animations"),
    SCRIPT_WARNING(ENGINE, "Print-Script-Warnings", false),
    USE_STATE_MACHINE(ENGINE, "Use-State-Machine", false),
    ENGINE_THREADS(ENGINE, "Engine-Threads", 4),
    MAX_ENGINE_THREADS(ENGINE, "Max-Engine-Threads", 10),
    SYNC_CLIENT_TICK_END(ENGINE, "Sync-Client-Tick-End", true),
    LOAD_ON_WORLD_LOAD(ENGINE, "Load-On-World-Load", false),
    UNLOAD_ON_WORLD_UNLOAD(ENGINE, "Unload-On-World-Unload", false),
    MANUAL_TRANSLUCENCY_ORDER(ENGINE, "Manual-Translucency-Order", false),
    DEFAULT_PRIORITY(ENGINE, "Default-Animation-Priority", 0),
    CLIENT_SYNC_INTERVAL(ENGINE, "Client-Sync-Interval", 60000),
    LOWER_SCALE_LIMIT(ENGINE, "Lower-Scale-Limit", 0.001),
    FORCE_CMD(ENGINE, "Force-Custom-Model-Data", false),
    EMPTY_ZERO(ENGINE, "Process-Empty-Timeline", false),
    RENDER_FIRE(ENGINE, "Render-Fire-On-Model", true),
    MINESKIN_API_KEY(ENGINE, "MineSkin-API-Key", ""),
    USER_LIMB(ENGINE, "User-Limb"),
    EAGER_GENERATE(USER_LIMB, "Eager-Generate-Skins", true),
    DEFAULT_CLASSIC_SKIN(USER_LIMB, "Default-Classic-Skin", "Steve"),
    DEFAULT_SLIM_SKIN(USER_LIMB, "Default-Slim-Skin", "Alex"),
    DEFAULT_SKINS(USER_LIMB, "Default-Skins", ConfigProperty.defaultSkins()),
    DELETE_OLD(GENERATOR, "Delete-Old", true),
    LATE_REGISTER(GENERATOR, "Register-Post-Server", true),
    LATE_ASSETS(GENERATOR, "Assets-Post-Server", true),
    LATE_ZIPPING(GENERATOR, "Compile-Post-Server", true),
    ERROR(GENERATOR, "Enable-Error", true),
    DEBUG_LEVEL(GENERATOR, "Debug-Level", 1),
    NAMESPACE(GENERATOR, "Namespace", "modelengine"),
    ZIP(GENERATOR, "Create-Zip", true),
    ATLAS(GENERATOR, "Create-Atlas", true),
    SHADER(GENERATOR, "Create-Shader", true),
    MCMETA(GENERATOR, "Create-MC-META", true),
    ITEM_MODEL(GENERATOR, "Item-Model", "LEATHER_HORSE_ARMOR"),
    ITEM_MODELS(GENERATOR, "Item-Models", List.of()),
    MAX_GENERATOR_THREADS(GENERATOR, "Max-Generator-Threads", 10),
    BUNDLE_EVERYTHING(OPTIMIZATION, "Bundle-Everything", false),
    BUNDLE_SIZE(OPTIMIZATION, "Bundle-Size", 512),
    OVERRIDE_MOVE_TO(OPTIMIZATION, "Override-Move-To-Method", true),
    CULL_INTERVAL(OPTIMIZATION, "Cull-Interval", 4),
    CULLING_THREADS(OPTIMIZATION, "Culling-Threads", 4),
    MAX_CULLING_THREADS(OPTIMIZATION, "Max-Culling-Threads", 10),
    VERTICAL_CULL(OPTIMIZATION, "Vertical-Render-Distance"),
    VERTICAL_CULL_ENABLE(VERTICAL_CULL, "Enabled", true),
    VERTICAL_CULL_DISTANCE(VERTICAL_CULL, "Vertical-Render-Distance", 32),
    VERTICAL_CULL_TYPE(VERTICAL_CULL, "Cull-Type", "CULLED"),
    BACKWARDS_CULL(OPTIMIZATION, "Skip-Models-Behind-Viewer"),
    BACKWARDS_CULL_ENABLED(BACKWARDS_CULL, "Enabled", true),
    BACKWARDS_CULL_ANGLE(BACKWARDS_CULL, "View-Angle", 180),
    BACKWARDS_CULL_IGNORE_RADIUS(BACKWARDS_CULL, "Force-Render-Radius", 5),
    BACKWARDS_CULL_TYPE(BACKWARDS_CULL, "Cull-Type", "MOVEMENT_ONLY"),
    BLOCK_CULL(OPTIMIZATION, "Skip-Blocked-Models"),
    BLOCK_CULL_ENABLE(BLOCK_CULL, "Enabled", true),
    BLOCK_CULL_IGNORE_RADIUS(BLOCK_CULL, "Force-Render-Radius", 5),
    BLOCK_CULL_TYPE(BLOCK_CULL, "Cull-Type", "CULLED"),
    BLOCK_CULL_IGNORE_SIZE(BLOCK_CULL, "Force-Render-Size"),
    BLOCK_CULL_USE_PAPER_CLIP(BLOCK_CULL, "Use-Paper-Clip-Method", false),
    BLOCK_CULL_IGNORE_SIZE_WIDTH(BLOCK_CULL_IGNORE_SIZE, "Width", 32),
    BLOCK_CULL_IGNORE_SIZE_HEIGHT(BLOCK_CULL_IGNORE_SIZE, "Height", 32),
    ANIMATION_LOD(OPTIMIZATION, "Reduce-Update-When-Far"),
    ANIMATION_LOD_ENABLED(ANIMATION_LOD, "Enabled", true),
    ANIMATION_LOD_DEFAULT(ANIMATION_LOD, "Active-By-Default", true),
    ANIMATION_LOD_FALLOFF_LENGTH(ANIMATION_LOD, "Falloff-Length", 8),
    ANIMATION_LOD_FALLOFF_MULTIPLIER(ANIMATION_LOD, "Falloff-Multiplier", 0.85),
    ANIMATION_LOD_IGNORE_DISTANCE(ANIMATION_LOD, "Full-Update-Distance", 16),
    ANIMATION_LOD_IGNORE_MULTIPLIER(ANIMATION_LOD, "Full-Update-Distance-Multiplier-By-Size", 1),
    ANIMATION_LOD_MAX_RATE_MULTIPLIER(ANIMATION_LOD, "Max-Rate-Multiplier", 1),
    ANIMATION_LOD_MIN_RATE_MULTIPLIER(ANIMATION_LOD, "Min-Rate-Multiplier", 0.05);

    private final String path;
    private final Object def;

    private ConfigProperty(String path) {
        this(path, null);
    }

    private ConfigProperty(ConfigProperty scope, String path) {
        this(scope.getPath() + "." + path, null);
    }

    private ConfigProperty(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    private ConfigProperty(@Nullable ConfigProperty scope, String path, Object def) {
        this(scope.getPath() + "." + path, def);
    }

    private static ConfigurationSection defaultSkins() {
        MemoryConfiguration section = new MemoryConfiguration();
        MemoryConfiguration steve = new MemoryConfiguration();
        steve.set("base64", (Object)"iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAMAAACdt4HsAAAAdVBMVEUAAAAKvLwAzMwmGgokGAgrHg0zJBE/KhW3g2uzeV5SPYn///+qclmbY0mQWT8Af38AaGhVVVWUYD52SzOBUzmPXj5JJRBCHQp3QjVqQDA0JRIoKCg3Nzc/Pz9KSko6MYlBNZtGOqUDenoFiIgElZUApKQAr6/wvakZAAAAAXRSTlMAQObYZgAAAolJREFUeNrt1l1rHucZReFrj/whu5hSCCQtlOTE/f+/Jz4q9Cu0YIhLcFVpVg+FsOCVehi8jmZgWOzZz33DM4CXlum3gH95GgeAzQZVeL4gTm6Cbp4vqFkD8HwBazPY8wWbMq9utu3mNZ5fotVezbzOE3kBEFbaZuc8kb00NTMUbWJp678Xf2GV7RRtx1TDQQ6XBNvsmL2+2vHq1TftmMPIyAWujtN2cl274ua2jpVpZneXEjjo7XW1q53V9ds4ODO5xIuhvGHvfLI3aixauig415uuO2+vl9+cncfsFw25zL650fXn687jqnXuP68/X3+eV3zE7y6u9eB73MlfAcfbTf3yR8CfAX+if8S/H5/EAbAxj5LN48tULvEBOh8V1AageMTXe2YHAOwHbZxrzPkSR3+ffr8TR2JDzE/4Fj8CDgEwDsW+q+9GsR07hhg2CsALBgMo2v5wNxXnQXMeGQVW7gUAyKI2m6KDsJ8Au3++F5RZO+kKNQjQcLLWgjwUjBXLltFgWWMUUlviocBgNoxNGgMjSxiYAA7zgLFo2hgIENiDU8gQCzDOmViGFAsEuBcQSDCothhpJaDRA8E5fHqH2nTbYm5fHLo1V0u3B7DAuheoeScRYabjjjuzs17cHVaTrTXmK78m9swP34d9oK/dfeXSIH2PW/MXwPvxN/bJlxw8zlYAcEyeI6gNgA/O8P8neN8xe1IHP2gTzegjvhUDfuRygmwEs2GE4mkCDIAzm2R4yAuPsIdR9k8AvMc+3L9+2UEjo4WP0FpgP19O0MzCsqxIoMsdDBvYcQyGmO0ZJRoYCKjLJWY0BAhYwGUBCgkh8MRdOKt+ruqMwAB2OcEX94U1TPbYJP0PkyyAI1S6cSIAAAAASUVORK5CYII=");
        section.set("Steve", (Object)steve);
        MemoryConfiguration alex = new MemoryConfiguration();
        alex.set("base64", (Object)"iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAMAAACdt4HsAAAAZlBMVEUAAAD////v2r/r0LDvu7HfxKLUt4/zqFiUyJKMvorrmD+GuYTljT97snjegS5xq26UlmuAglpvb29lZWV8Vz51UDhYWFhyTjZPT09sRi5kQSwjYiQrVChcOyc/Pz82NjYYOBYoKCgm8xoJAAAAAXRSTlMAQObYZgAAAwZJREFUeNrtlu1W6jAQRWtjWiaZtCC9/YgC8v4vec9Mg6Uur6b8ve6S0Lo8O2NYmClusOMGF7Nr8HKu2IoDTYPk44K6lvBciePNAm7mlTGz3G2voAaYZg9vFzCCjtnZqoHjAYHDJjAM1tqqgmR7Bahf9tFaA6zND6JwXRpxxosZIkz4uQzOWFmTrJPOUgdjOP1hzu7flmNrJe4wtCBoMgR1LVE1mNIYNbAurpafN5N5Xto8vZbl65PhWg0aRiU/C2Rpa0x5w/LHnjQwZVTgGk755+eyNMbWXDeukQIwMjaRm6qyJjmMtTAi1ziHF0OQQwsul+u1SJBnZsTbRJEgIqzxD8H1erksAt80MLTtCawFpvpScDqtKzhUYpA8yKgAv7Wq4GCripmT4PsKPFGIQxdjbFsPCPiDsbY6tAvBU2K+KRZiCHHquimC0ylGCoH8vjSmVEG4xb0Mmb2MYmGYpgH5boBA7iAIPuxL5CUtT0Q72u0wy6SDioVOosh3k5ggQIZgQB4iDITlOqwoFiaJwzJAcMJARhOShUCX3AGjWMWYYqHv+zghHntwPPaoWQJh58mLRe5FUSpQ6BenWDgi1B81rCAki0oIgiDvQRRlMkh+JdDgi2b78zieEUKEWglRG4gQp+8EEupTeOz78f39/U32n1oKb3iAQ642CWQfPlWAUAr3MtoQ2jck4XmTe7wL7ykPrFkJRkm9HCUuopF8CN6TXsGTEAjvt2+7Gu4Fl/N5xN+PMs6XCwZJLv4BER480MzHFsyfwi9ZpDNSYH7IIKcDPKB4iHSwPk7tVo974ZDYZ1VQ/PKfg9Nsddg6ouWfyCMNh2OqzBbBCdxXUDMq2Cq4r6CmDRVEoRvm4z4Q8OwowdmCbgDThEq8Jwg8pb4gSzANU5cY4l1ngCtDMM0NR4yYwfS5LzgoewXPXwuQ1bZDsIJJ2OJHPvcL49wQJEyGQI+5pV/o9Sj8IKcCSd31C5sFEhrv+gVrzAbBul9QINiwB+t+QdHWLBmyPgXtF8CtX5CO4LsK/gL4WlU0/HKH/gAAAABJRU5ErkJggg==");
        alex.set("slim", (Object)true);
        section.set("Alex", (Object)alex);
        return section;
    }

    @Override
    @Generated
    public String getPath() {
        return this.path;
    }

    @Override
    @Generated
    public Object getDef() {
        return this.def;
    }
}

