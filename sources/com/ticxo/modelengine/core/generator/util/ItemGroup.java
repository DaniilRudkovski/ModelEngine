/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 */
package com.ticxo.modelengine.core.generator.util;

import com.ticxo.modelengine.api.generator.assets.JavaDisplay;
import com.ticxo.modelengine.api.generator.assets.JavaItemModel;
import com.ticxo.modelengine.api.generator.assets.ModelAssets;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.math.Direction;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.generator.processed.ProcessedBone;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

public record ItemGroup(int debug, Quaterniond displayQuaternion, Vector3d displayRotation, List<ProcessedBone.Cube> cubes) {
    public JavaItemModel toJavaItemModel(String name, BlockbenchModel model, ModelAssets assets) {
        JavaItemModel javaItem = new JavaItemModel();
        javaItem.setName(name);
        for (ProcessedBone.Cube cube : this.cubes) {
            JavaItemModel.JavaElement element = new JavaItemModel.JavaElement();
            element.from(TMath.unwrap(cube.getFrom()), cube.getInflate());
            element.to(TMath.unwrap(cube.getTo()), cube.getInflate());
            element.setRotation(cube.rotation());
            for (Map.Entry<Direction, ProcessedBone.Face> faceEntry : cube.getFaces().entrySet()) {
                int id;
                Direction dir = faceEntry.getKey();
                ProcessedBone.Face bbFace = faceEntry.getValue();
                if (bbFace.isEmpty() || (id = bbFace.texture()) >= assets.getTextures().size()) continue;
                BlockbenchModel.Texture tex = model.getTextures().get(id);
                JavaItemModel.JavaElement.Face face = new JavaItemModel.JavaElement.Face();
                face.setRotation(bbFace.uv().rotation());
                face.uv(MiscUtils.or(tex.getUv_width(), model.getResolution().getWidth()), MiscUtils.or(tex.getUv_height(), model.getResolution().getHeight()), new float[]{bbFace.uv().u1(), bbFace.uv().v1(), bbFace.uv().u2(), bbFace.uv().v2()});
                face.setTexture("#" + id);
                element.getFaces().put(dir.name().toLowerCase(Locale.ENGLISH), face);
                Map<String, String> textures = javaItem.getTextures();
                textures.computeIfAbsent(String.valueOf(id), s -> assets.getTextures().get(id).getPath().toString());
                if (textures.size() != 1) continue;
                textures.computeIfAbsent("particle", s -> "#" + id);
            }
            javaItem.addElement(element);
        }
        javaItem.setDisplay(JavaDisplay.THIRDPERSON_RIGHTHAND, JavaDisplay.Transform.ROTATION, (float)this.displayRotation.x, (float)this.displayRotation.y, (float)this.displayRotation.z);
        javaItem.setDisplay(JavaDisplay.FIRSTPERSON_RIGHTHAND, JavaDisplay.Transform.ROTATION, (float)this.displayRotation.x, (float)this.displayRotation.y, (float)this.displayRotation.z);
        javaItem.setDisplay(JavaDisplay.GROUND, JavaDisplay.Transform.ROTATION, (float)this.displayRotation.x, (float)this.displayRotation.y, (float)this.displayRotation.z);
        javaItem.setDisplay(JavaDisplay.HEAD, JavaDisplay.Transform.ROTATION, (float)this.displayRotation.x, (float)this.displayRotation.y, (float)this.displayRotation.z);
        javaItem.setDisplay(JavaDisplay.FIXED, JavaDisplay.Transform.ROTATION, (float)this.displayRotation.x, (float)this.displayRotation.y, (float)this.displayRotation.z);
        Vector3d guiRotation = TMath.toEulerXYZ(TMath.fromEulerXYZ(new Vector3d(30.0, 225.0, 0.0)).mul((Quaterniondc)this.displayQuaternion));
        javaItem.setDisplay(JavaDisplay.GUI, JavaDisplay.Transform.ROTATION, (float)guiRotation.x, (float)guiRotation.y, (float)guiRotation.z);
        javaItem.setDisplay(JavaDisplay.THIRDPERSON_LEFTHAND, JavaDisplay.Transform.ROTATION, (float)this.displayRotation.x, (float)(-this.displayRotation.y), (float)(-this.displayRotation.z));
        javaItem.setDisplay(JavaDisplay.FIRSTPERSON_LEFTHAND, JavaDisplay.Transform.ROTATION, (float)this.displayRotation.x, (float)(-this.displayRotation.y), (float)(-this.displayRotation.z));
        return javaItem;
    }

    @Override
    public String toString() {
        return "ItemGroup{debug=" + this.debug + ", displayRotation=" + this.displayRotation.toString(NumberFormat.getInstance()) + ", cubes=" + this.cubes + "}";
    }
}

