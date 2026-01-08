/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Pair
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  lombok.Generated
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.ticxo.modelengine.core.generator.util;

import com.ticxo.modelengine.api.utils.math.Axis;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.generator.processed.ProcessedBone;
import com.ticxo.modelengine.core.generator.util.HashedVector3d;
import com.ticxo.modelengine.core.generator.util.IllegalRotationSolver;
import com.ticxo.modelengine.core.generator.util.ItemGroup;
import com.ticxo.modelengine.core.generator.util.PlaneGroup;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import lombok.Generated;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RotationSolver {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(RotationSolver.class);
    private static final List<Vector3d> CARDINAL_AXES = new ArrayList<Vector3d>(){
        {
            this.add(new Vector3d(1.0, 0.0, 0.0));
            this.add(new Vector3d(0.0, 1.0, 0.0));
            this.add(new Vector3d(0.0, 0.0, 1.0));
            this.add(new Vector3d(-1.0, 0.0, 0.0));
            this.add(new Vector3d(0.0, -1.0, 0.0));
            this.add(new Vector3d(0.0, 0.0, -1.0));
        }
    };
    private final Int2ObjectMap<ProcessedBone.Cube> cubes = new Int2ObjectOpenHashMap();
    private final Int2ObjectMap<List<Vector3d>> axes = new Int2ObjectOpenHashMap();

    public static void solve(Collection<ItemGroup> result, Collection<ProcessedBone.Cube> cubes) {
        RotationSolver solver = new RotationSolver();
        solver.initialize(result, cubes);
        solver.solve(result);
    }

    private void initialize(Collection<ItemGroup> result, Collection<ProcessedBone.Cube> collection) {
        int cubeId = 0;
        ArrayList<ProcessedBone.Cube> list = new ArrayList<ProcessedBone.Cube>();
        for (ProcessedBone.Cube cube : collection) {
            if (RotationSolver.isLegal(cube)) {
                list.add(IllegalRotationSolver.solve(cube));
                continue;
            }
            this.cubes.put(cubeId++, (Object)cube);
        }
        if (!list.isEmpty()) {
            result.add(new ItemGroup(-1, new Quaterniond(), new Vector3d(), list));
        }
    }

    private void solve(Collection<ItemGroup> result) {
        Map<Vector3d, IntLinkedOpenHashSet> axisGroup = this.groupByAxis();
        List<PlaneGroup> moduloGroup = this.groupByModulo(axisGroup);
        Map<PlaneGroup, Set<PlaneGroup>> planeGroup = this.combineGroup(moduloGroup);
        this.fixGroups(result, planeGroup);
    }

    private void simpleConvert(Collection<ItemGroup> result, List<PlaneGroup> moduloGroup) {
        for (PlaneGroup plane : moduloGroup) {
            ArrayList<ProcessedBone.Cube> list = new ArrayList<ProcessedBone.Cube>();
            this.rotateCubes(list, plane.cubes(), plane.invOrigin());
            result.add(new ItemGroup(0, new Quaterniond((Quaterniondc)plane.origin()), TMath.toEulerXYZ(plane.origin()), list));
        }
    }

    private List<Vector3d> toAxes(int cubeId) {
        ProcessedBone.Cube cube = (ProcessedBone.Cube)this.cubes.get(cubeId);
        Vector3d px = TMath.fixVector(new HashedVector3d(Axis.X, 1.0, 0.0, 0.0).rotate((Quaterniondc)cube.getQuaternion()));
        Vector3d py = TMath.fixVector(new HashedVector3d(Axis.Y, 0.0, 1.0, 0.0).rotate((Quaterniondc)cube.getQuaternion()));
        Vector3d pz = TMath.fixVector(new HashedVector3d(Axis.Z, 0.0, 0.0, 1.0).rotate((Quaterniondc)cube.getQuaternion()));
        return Arrays.asList(px, py, pz);
    }

    private Map<Vector3d, IntLinkedOpenHashSet> groupByAxis() {
        Object2ObjectLinkedOpenHashMap map = new Object2ObjectLinkedOpenHashMap();
        IntIterator intIterator = this.cubes.keySet().iterator();
        while (intIterator.hasNext()) {
            int key = (Integer)intIterator.next();
            List<Vector3d> axes = this.toAxes(key);
            this.axes.put(key, axes);
            for (Vector3d axis : axes) {
                ((IntLinkedOpenHashSet)map.computeIfAbsent((Object)axis, vector3d -> new IntLinkedOpenHashSet())).add(key);
            }
        }
        return RotationSolver.fetch(map);
    }

    private List<PlaneGroup> groupByModulo(Map<Vector3d, IntLinkedOpenHashSet> byAxis) {
        Object2ObjectLinkedOpenHashMap temp = new Object2ObjectLinkedOpenHashMap();
        ArrayList<PlaneGroup> result = new ArrayList<PlaneGroup>();
        for (Map.Entry<Vector3d, IntLinkedOpenHashSet> entry : byAxis.entrySet()) {
            Quaterniond toOrigin = null;
            IntListIterator intListIterator = entry.getValue().iterator();
            while (intListIterator.hasNext()) {
                double angle;
                int cubeId = (Integer)intListIterator.next();
                ProcessedBone.Cube cube = (ProcessedBone.Cube)this.cubes.get(cubeId);
                if (toOrigin == null) {
                    toOrigin = cube.getQuaternion().invert(new Quaterniond());
                    angle = 0.0;
                } else {
                    Quaterniond localQ = toOrigin.mul((Quaterniondc)cube.getQuaternion(), new Quaterniond());
                    Vector3d lqVec = new Vector3d(localQ.x, localQ.y, localQ.z);
                    double sign = Math.signum(lqVec.dot((Vector3dc)entry.getKey()));
                    angle = sign * 2.0 * Math.acos(localQ.w) * 57.29577951308232;
                }
                int groupId = RotationSolver.getGroupId(angle);
                ((IntSet)((Int2ObjectMap)temp.computeIfAbsent((Object)entry.getKey(), vector3d -> new Int2ObjectLinkedOpenHashMap())).computeIfAbsent(groupId, integer -> new IntLinkedOpenHashSet())).add(cubeId);
            }
        }
        for (Map.Entry group : temp.entrySet()) {
            for (Int2ObjectMap.Entry subGroup : ((Int2ObjectMap)group.getValue()).int2ObjectEntrySet()) {
                int firstCubeId = ((IntSet)subGroup.getValue()).iterator().nextInt();
                Quaterniond rotation = ((ProcessedBone.Cube)this.cubes.get(firstCubeId)).getQuaternion();
                result.add(new PlaneGroup(new Vector3d((Vector3dc)group.getKey()), subGroup.getIntKey(), new Quaterniond((Quaterniondc)rotation), rotation.invert(new Quaterniond()), (IntSet)subGroup.getValue()));
            }
        }
        return result;
    }

    private Map<PlaneGroup, Set<PlaneGroup>> combineGroup(List<PlaneGroup> planes) {
        Object2ObjectLinkedOpenHashMap map = new Object2ObjectLinkedOpenHashMap();
        for (int a = 0; a < planes.size(); ++a) {
            PlaneGroup groupA = planes.get(a);
            Set set = (Set)map.computeIfAbsent((Object)groupA, planeGroup -> new ObjectLinkedOpenHashSet());
            set.add(groupA);
            block1: for (int b = a + 1; b < planes.size(); ++b) {
                PlaneGroup groupB = planes.get(b);
                if (TMath.isSimilar(groupA.axis().dot((Vector3dc)groupB.axis()), -1.0)) {
                    List axesA = (List)this.axes.get(groupA.cubes().iterator().nextInt());
                    List axesB = (List)this.axes.get(groupB.cubes().iterator().nextInt());
                    for (int i = 0; i < 3; ++i) {
                        Vector3d axisB;
                        Vector3d axisA = (Vector3d)axesA.get(i);
                        if (RotationSolver.getGroupId(Math.acos(axisA.dot((Vector3dc)(axisB = (Vector3d)axesB.get(i)))) * 57.29577951308232) != 0) continue block1;
                    }
                } else {
                    for (PlaneGroup g : set) {
                        double dot = g.axis().dot((Vector3dc)groupB.axis());
                        if (!TMath.isSimilar(dot, 0.0) && !TMath.isSimilar(dot, -1.0)) continue block1;
                        Vector3d vA = groupB.axis().rotate((Quaterniondc)g.invOrigin(), new Vector3d());
                        Vector3d vB = g.axis().rotate((Quaterniondc)groupB.invOrigin(), new Vector3d());
                        if (RotationSolver.isGroupable(vA) && RotationSolver.isGroupable(vB)) continue;
                        continue block1;
                    }
                }
                set.add(groupB);
            }
        }
        return RotationSolver.fetch(map);
    }

    private void fixGroups(Collection<ItemGroup> result, Map<PlaneGroup, Set<PlaneGroup>> planeGroupMap) {
        for (Map.Entry<PlaneGroup, Set<PlaneGroup>> entry : planeGroupMap.entrySet()) {
            Set<PlaneGroup> planeGroups = entry.getValue();
            if (planeGroups.size() < 2) {
                PlaneGroup group = planeGroups.iterator().next();
                result.add(new ItemGroup(0, new Quaterniond((Quaterniondc)group.origin()), TMath.toEulerXYZ(group.origin()), this.rotateCubes(new ArrayList<ProcessedBone.Cube>(), group.cubes(), group.invOrigin())));
                continue;
            }
            Iterator<PlaneGroup> iterator = planeGroups.iterator();
            PlaneGroup groupA = iterator.next();
            Vector3d axisA = groupA.axis();
            Vector3d axisB = iterator.next().axis();
            while (TMath.isSimilar(Math.abs(axisA.dot((Vector3dc)axisB)), 1.0) && iterator.hasNext()) {
                axisB = iterator.next().axis();
            }
            if (TMath.isSimilar(Math.abs(axisA.dot((Vector3dc)axisB)), 1.0)) {
                ArrayList<ProcessedBone.Cube> list = new ArrayList<ProcessedBone.Cube>();
                for (PlaneGroup planeGroup : planeGroups) {
                    this.rotateCubes(list, planeGroup.cubes(), groupA.invOrigin());
                }
                result.add(new ItemGroup(0, new Quaterniond((Quaterniondc)groupA.origin()), TMath.toEulerXYZ(groupA.origin()), list));
                continue;
            }
            Vector3d cardinalA = RotationSolver.getClosestCardinal(axisA);
            Quaterniond aToCardinal = axisA.rotationTo((Vector3dc)cardinalA, new Quaterniond());
            Vector3d axisBPrime = axisB.rotate((Quaterniondc)aToCardinal, new Vector3d());
            Vector3d cardinalB = RotationSolver.getClosestCardinal(axisBPrime);
            Quaterniond bPrimeToCardinal = axisBPrime.rotationTo((Vector3dc)cardinalB, new Quaterniond());
            Quaterniond invRoot = bPrimeToCardinal.mul((Quaterniondc)aToCardinal);
            Quaterniond rootRotation = invRoot.invert(new Quaterniond());
            Vector3d rootEuler = TMath.fixEuler(TMath.toEulerXYZ(rootRotation));
            ArrayList<ProcessedBone.Cube> list = new ArrayList<ProcessedBone.Cube>();
            for (PlaneGroup planeGroup : planeGroups) {
                this.rotateCubes(list, planeGroup.cubes(), invRoot);
            }
            result.add(new ItemGroup(1, rootRotation, rootEuler, list));
        }
    }

    private List<ProcessedBone.Cube> rotateCubes(List<ProcessedBone.Cube> result, IntSet cubeIds, Quaterniond quaterniond) {
        IntIterator intIterator = cubeIds.iterator();
        while (intIterator.hasNext()) {
            int cubeId = (Integer)intIterator.next();
            ProcessedBone.Cube cube = (ProcessedBone.Cube)this.cubes.get(cubeId);
            cube.rotate(quaterniond);
            result.add(IllegalRotationSolver.solve(cube));
        }
        return result;
    }

    private static <T, R extends Collection<S>, S> Map<T, R> fetch(Map<T, R> map) {
        Pair largest;
        PriorityQueue<Pair> queue = new PriorityQueue<Pair>(Comparator.comparing(pair -> ((Collection)pair.second()).size()).reversed());
        map.forEach((t, rs) -> queue.add(Pair.of((Object)t, (Object)rs)));
        Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap();
        while (!queue.isEmpty() && !((Collection)(largest = queue.poll()).second()).isEmpty()) {
            queue.clear();
            map.remove(largest.first());
            result.put(largest.first(), (Object)((Collection)largest.second()));
            map.forEach((t, rs) -> {
                rs.removeAll((Collection)largest.second());
                queue.add(Pair.of((Object)t, (Object)rs));
            });
        }
        return result;
    }

    private static int getGroupId(double angle) {
        while (angle < 0.0) {
            angle += 360.0;
        }
        int intAngle = (int)Math.round(angle * 10000.0);
        return intAngle % 225000;
    }

    private static boolean isGroupable(Vector3d vec) {
        for (Axis axis : Axis.values()) {
            if (RotationSolver.getGroupId(Math.acos(vec.dot((Vector3dc)axis.getVector())) * (double)57.29578f) == 0) continue;
            return false;
        }
        return true;
    }

    private static Vector3d getClosestCardinal(Vector3d vec) {
        double dot = -1.0;
        Vector3d cardinal = null;
        for (Vector3d axis : CARDINAL_AXES) {
            double d = vec.dot((Vector3dc)axis);
            if (!(d > dot)) continue;
            dot = d;
            cardinal = axis;
        }
        return cardinal;
    }

    private static boolean isLegal(ProcessedBone.Cube cube) {
        return RotationSolver.legalId(cube.getRotation().x) + RotationSolver.legalId(cube.getRotation().y) + RotationSolver.legalId(cube.getRotation().z) < 2;
    }

    private static int legalId(double angle) {
        if (TMath.isInterval(angle, 90.0)) {
            return 0;
        }
        if (TMath.isInterval(angle, 22.5)) {
            return 1;
        }
        return 999;
    }

    private RotationSolver() {
    }
}

