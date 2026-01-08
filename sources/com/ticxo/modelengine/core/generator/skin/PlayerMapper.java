/*
 * Decompiled with CFR 0.152.
 */
package com.ticxo.modelengine.core.generator.skin;

import com.ticxo.modelengine.api.generator.skin.LimbData;
import com.ticxo.modelengine.core.generator.skin.FaceMapper;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;

public class PlayerMapper {
    private final Map<String, LimbData> limbData = new HashMap<String, LimbData>();
    private final Map<String, BufferedImage> textureReference = new HashMap<String, BufferedImage>();
    private final Map<String, Set<FaceMapper>> faceMappers = new HashMap<String, Set<FaceMapper>>();

    public void registerLimbData(LimbData data) {
        this.limbData.put(data.limbType(), data);
    }

    public LimbData getLimbData(String data) {
        return this.limbData.get(data);
    }

    public void registerTextureSource(String name, BufferedImage texture) {
        this.textureReference.put(name, texture);
    }

    public void registerFaceMapper(String name, FaceMapper mapper) {
        this.faceMappers.computeIfAbsent(name, s -> new HashSet()).add(mapper);
    }

    public Map<String, byte[]> generateAll(Map<String, BufferedImage> placeholderReference) {
        HashMap<String, byte[]> map = new HashMap<String, byte[]>();
        for (String key : this.faceMappers.keySet()) {
            try {
                byte[] data = this.generatePartTexture(key, placeholderReference);
                if (data.length <= 0) continue;
                map.put(key, data);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public byte[] generatePartTexture(String part, Map<String, BufferedImage> placeholderReference) throws IOException {
        Set<FaceMapper> mappers = this.faceMappers.get(part);
        if (mappers == null || mappers.isEmpty()) {
            return new byte[0];
        }
        BufferedImage image = new BufferedImage(64, 64, 2);
        for (FaceMapper mapper : mappers) {
            BufferedImage texture = placeholderReference.get(mapper.sourceTexture());
            if (texture == null && (texture = this.textureReference.get(mapper.sourceTexture())) == null) continue;
            BufferedImage finalTexture = texture;
            mapper.to().forPixel((x, y) -> {
                float[] source = mapper.getSourcePixel(x.intValue(), y.intValue());
                int xx = (int)(source[0] * ((float)finalTexture.getWidth() / 64.0f));
                int yy = (int)(source[1] * ((float)finalTexture.getHeight() / 64.0f));
                image.setRGB((int)x, (int)y, finalTexture.getRGB(xx, yy));
            });
        }
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage)image, "png", byteStream);
        return byteStream.toByteArray();
    }
}

