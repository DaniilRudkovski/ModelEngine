/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  lombok.Generated
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.ticxo.modelengine.core.generator.skin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchDeserializer;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.generator.skin.LimbData;
import com.ticxo.modelengine.api.generator.skin.SkinSplitter;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.generator.skin.FaceMapper;
import com.ticxo.modelengine.core.generator.skin.PlayerMapper;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.Generated;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SkinSplitterImpl
implements SkinSplitter {
    private static final String PLACEHOLDER_SKIN = "<placeholder.skin>";
    private final Gson gson = new GsonBuilder().registerTypeAdapter(BlockbenchModel.class, (Object)new BlockbenchDeserializer()).create();
    private final File internalSteveMapped;
    private final File internalAlexMapped;
    private PlayerMapper steveMapper;
    private PlayerMapper alexMapper;

    public void copyMapped(ModelEngineAPI plugin) {
        TFile.copyResource(plugin, this.internalSteveMapped, "internal/steve_mapped.bbmodel");
        TFile.copyResource(plugin, this.internalAlexMapped, "internal/alex_mapped.bbmodel");
    }

    @Override
    public void reloadMapping() {
        try {
            this.steveMapper = this.createSkinMaps((BlockbenchModel)this.gson.fromJson((Reader)new FileReader(this.internalSteveMapped), BlockbenchModel.class));
            this.alexMapper = this.createSkinMaps((BlockbenchModel)this.gson.fromJson((Reader)new FileReader(this.internalAlexMapped), BlockbenchModel.class));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PlayerMapper createSkinMaps(BlockbenchModel model) {
        PlayerMapper playerTemplate = new PlayerMapper();
        HashMap<Integer, String> texMap = new HashMap<Integer, String>();
        for (Map.Entry<Integer, BlockbenchModel.Texture> entry : model.getTextures().entrySet()) {
            BlockbenchModel.Texture texture = entry.getValue();
            if (!texture.getName().equals(PLACEHOLDER_SKIN)) {
                BufferedImage image = TFile.toImage(texture.getSource());
                playerTemplate.registerTextureSource(texture.getName(), image);
            }
            texMap.put(entry.getKey(), texture.getName());
        }
        Map<String, BlockbenchModel.Group> groups = model.getFlatOutlinerByName();
        for (Map.Entry<String, BlockbenchModel.Group> entry : groups.entrySet()) {
            List<BlockbenchModel.Cube> cubes;
            String boneId = entry.getKey();
            if (boneId.equals("head")) {
                playerTemplate.registerLimbData(new LimbData(boneId, new Vector3f(0.0f, 0.5f, 0.0f), new Vector3f(1.0f), true));
                continue;
            }
            BlockbenchModel.Group group = entry.getValue();
            if (group.getElement().isEmpty() || (cubes = group.getElement().stream().map(uuid -> model.getElements().get(uuid)).map(element -> {
                BlockbenchModel.Cube cube;
                return element instanceof BlockbenchModel.Cube ? (cube = (BlockbenchModel.Cube)element) : null;
            }).filter(Objects::nonNull).toList()).isEmpty()) continue;
            Vector3f origin = new Vector3f(0.0f);
            Vector3f scale = new Vector3f(1.0f);
            for (BlockbenchModel.Cube cube : cubes) {
                boolean hatLayer = cube.getName().startsWith("hat_");
                if (!hatLayer) {
                    origin.set(cube.centerX(), cube.maxY(), cube.centerZ()).sub((Vector3fc)TMath.wrap(group.getOrigin())).mul(-0.0625f, 0.0625f, -0.0625f);
                    scale.set(cube.width(), cube.height(), cube.depth()).mul(0.125f);
                }
                for (Map.Entry<String, BlockbenchModel.Face> faceEntry : cube.getFaces().entrySet()) {
                    String texName = (String)texMap.get(faceEntry.getValue().getTexture());
                    if (texName == null) continue;
                    FaceMapper.Face face = FaceMapper.Face.getFace(faceEntry.getKey(), hatLayer);
                    Float[] uv = faceEntry.getValue().getUv();
                    FaceMapper faceMapper = new FaceMapper(texName, new float[]{uv[0].floatValue(), uv[1].floatValue(), uv[2].floatValue(), uv[3].floatValue()}, face);
                    playerTemplate.registerFaceMapper(boneId, faceMapper);
                }
            }
            playerTemplate.registerLimbData(new LimbData(boneId, origin, scale, false));
        }
        return playerTemplate;
    }

    @Override
    public LimbData getLimb(String id, boolean slim) {
        return (slim ? this.alexMapper : this.steveMapper).getLimbData(id);
    }

    @Override
    public Map<String, byte[]> splitSkin(byte[] skinData, boolean slim) {
        try {
            BufferedImage skinImage = ImageIO.read(new ByteArrayInputStream(skinData));
            HashMap<String, BufferedImage> placeholder = new HashMap<String, BufferedImage>();
            placeholder.put(PLACEHOLDER_SKIN, skinImage);
            return (slim ? this.alexMapper : this.steveMapper).generateAll(placeholder);
        }
        catch (IOException e) {
            e.printStackTrace();
            return new HashMap<String, byte[]>();
        }
    }

    @Generated
    public SkinSplitterImpl(File internalSteveMapped, File internalAlexMapped) {
        this.internalSteveMapped = internalSteveMapped;
        this.internalAlexMapped = internalAlexMapped;
    }
}

