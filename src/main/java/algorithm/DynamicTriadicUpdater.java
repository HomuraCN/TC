package algorithm;

import utils.Context;
import utils.concept.Concept;
import java.util.*;
import static utils.util.*; // 假设里面有 intersection, isEqual 等方法

public class DynamicTriadicUpdater {

    /**
     * 判断原三元背景中是否存在三元关系 (i, j, k)
     */
    private static boolean hasRelation(Tradic tradic, int i, int j, int k) {
        int key = (i - 1) * tradic.getY() + j;
        BitSet set = tradic.getObjsAndAttrs_Attr().get(key);
        return set != null && set.get(k);
    }

    /**
     * 定理1：筛选旧概念 (判断旧概念是否在新增 (x,y,z) 后依然成立)
     * 注意：传入的 tradic 必须是【更新前】的旧三元背景
     */
    public static boolean isPreservedByTheorem1(TriadicConcept c, Tradic oldTradic, int x, int y, int z) {
        int X_size = oldTradic.getX();
        int Y_size = oldTradic.getY();
        int Z_size = oldTradic.getZ();

        // 条件 (1): 若 x ∈ A1，则存在 y_j ∉ A2 且 z_k ∉ A3，使得 (x, y_j, z_k) ∉ Y
        if (c.extent.get(x)) {
            boolean found = false;
            for (int j = 1; j <= Y_size; j++) {
                if (!c.intent.get(j)) {
                    for (int k = 1; k <= Z_size; k++) {
                        if (!c.modus.get(k)) {
                            if (!hasRelation(oldTradic, x, j, k)) {
                                found = true; break;
                            }
                        }
                    }
                }
                if (found) break;
            }
            if (!found) return false;
        }

        // 条件 (2): 若 y ∈ A2，则存在 x_i ∉ A1 且 z_k ∉ A3，使得 (x_i, y, z_k) ∉ Y
        if (c.intent.get(y)) {
            boolean found = false;
            for (int i = 1; i <= X_size; i++) {
                if (!c.extent.get(i)) {
                    for (int k = 1; k <= Z_size; k++) {
                        if (!c.modus.get(k)) {
                            if (!hasRelation(oldTradic, i, y, k)) {
                                found = true; break;
                            }
                        }
                    }
                }
                if (found) break;
            }
            if (!found) return false;
        }

        // 条件 (3): 若 z ∈ A3，则存在 x_i ∉ A1 且 y_j ∉ A2，使得 (x_i, y_j, z) ∉ Y
        if (c.modus.get(z)) {
            boolean found = false;
            for (int i = 1; i <= X_size; i++) {
                if (!c.extent.get(i)) {
                    for (int j = 1; j <= Y_size; j++) {
                        if (!c.intent.get(j)) {
                            if (!hasRelation(oldTradic, i, j, z)) {
                                found = true; break;
                            }
                        }
                    }
                }
                if (found) break;
            }
            if (!found) return false;
        }

        return true;
    }

    /**
     * 将新增的三元组 (x, y, z) 更新到 Tradic 结构中，生成 K+
     */
    public static void addRelationToTradic(Tradic tradic, int x, int y, int z) {
        // 更新 objsAndAttrs_Attr: key = (i-1)*y+j
        int key1 = (x - 1) * tradic.getY() + y;
        tradic.getObjsAndAttrs_Attr().get(key1).set(z);

        // 更新 attrsAndCondi_Attr: key = j*z+k+1，实际代码逻辑中是 (j-1)*z+k
        int key2 = (y - 1) * tradic.getZ() + z;
        tradic.getAttrsAndCondi_Attr().get(key2).set(x);

        // 更新 objsAndCondi: key = (i-1)*z+k (如有用到也一并更新)
        int key3 = (x - 1) * tradic.getZ() + z;
        tradic.getObjsAndCondi_Attr().get(key3).set(y);
    }

    /**
     * 定理2：在更新后的背景上，局部生成包含新增三元组的新概念
     * 注意：传入的 newTradic 必须是【更新后】的三元背景
     */
    public static Set<TriadicConcept> generateByTheorem2(Tradic newTradic, Context newContext, int x, int y, int z) {
        Set<TriadicConcept> newConcepts = new HashSet<>();

        // 1. 获取新背景 K+^(1) 的全部形式概念
        Concept initialConcept = new Concept();
        initialConcept.setExtent(makeSet(newContext.getObjs_size()));
        initialConcept.setIntent(new BitSet());
        Map<Integer, BitSet> nj = new HashMap<>();
        Queue<Concept> res = new LinkedList<>();
        InClose3.inClose3_exe(newContext, initialConcept, 1, nj, res);

        // 2. 定理2优化：只保留外延包含对象 x 的形式概念
        Queue<Concept> filteredRes = new LinkedList<>();
        for (Concept c : res) {
            if (c.getExtent().get(x)) {
                filteredRes.add(c);
            }
        }

        // 3. 计算外延待选集
        Map<BitSet, Set<BitSet>> candidatesMap = extendCandidate.extendCandidateExe(newTradic, filteredRes);

        // 4. 验证与生成三元概念 (完全复用 Main.java 的检验逻辑)
        for (Map.Entry<BitSet, Set<BitSet>> entry : candidatesMap.entrySet()) {
            BitSet extentX = entry.getKey();
            Set<BitSet> candidateA = entry.getValue();

            for (BitSet intent : candidateA) {
                // 求 Z = (X × A)^(3)
                BitSet modusZ = new BitSet();
                for (int i = extentX.nextSetBit(0); i >= 0; i = extentX.nextSetBit(i + 1)) {
                    for (int j = intent.nextSetBit(0); j >= 0; j = intent.nextSetBit(j + 1)) {
                        int num = (i - 1) * newTradic.getY() + j;
                        if (modusZ.cardinality() == 0) {
                            modusZ = (BitSet) newTradic.getObjsAndAttrs_Attr().get(num).clone();
                        } else {
                            BitSet temp = newTradic.getObjsAndAttrs_Attr().get(num);
                            modusZ = intersection(temp, modusZ);
                        }
                    }
                }

                // 求 X' = (A × Z)^(1)
                BitSet derivedExtent = new BitSet();
                for (int j = intent.nextSetBit(0); j >= 0; j = intent.nextSetBit(j + 1)) {
                    for (int k = modusZ.nextSetBit(0); k >= 0; k = modusZ.nextSetBit(k + 1)) {
                        int num = (j - 1) * newTradic.getZ() + k;
                        if (derivedExtent.cardinality() == 0) {
                            derivedExtent = (BitSet) newTradic.getAttrsAndCondi_Attr().get(num).clone();
                        } else {
                            BitSet temp = newTradic.getAttrsAndCondi_Attr().get(num);
                            derivedExtent = intersection(temp, derivedExtent);
                        }
                    }
                }

                // 验证 X' == X
                if (isEqual(derivedExtent, extentX)) {
                    // 【非常关键】定理2指明，所有因新增而变化或生成的三元概念，必定包含该新增点
                    if (extentX.get(x) && intent.get(y) && modusZ.get(z)) {
                        newConcepts.add(new TriadicConcept(extentX, intent, modusZ));
                    }
                }
            }
        }
        return newConcepts;
    }

    /**
     * 当新增三元组包含【全新属性】时，扩容并重构 Tradic 矩阵
     * @param oldTradic 旧三元背景
     * @param newX 新增关系的对象
     * @param newZ 新增关系的条件
     * @return 扩容后的新三元背景 K+
     */
    public static Tradic expandTradicContext(Tradic oldTradic, int newX, int newZ) {
        int X = oldTradic.getX();
        int Y_old = oldTradic.getY();
        int Z = oldTradic.getZ();
        int Y_new = Y_old + 1; // 属性维度扩张 + 1

        Tradic newT = new Tradic();
        newT.setX(X);
        newT.setY(Y_new);
        newT.setZ(Z);
        newT.setObjsAndAttrs_AttrSize(X * Y_new);
        newT.setAttrsAndCondi_AttrSize(Y_new * Z);
        newT.setObjsAndCondi_AttrSize(X * Z);

        // 1. 重构 objsAndAttrs_Attr: key = (i-1) * Y_new + j
        Map<Integer, BitSet> oaa_Attr = new HashMap<>();
        int num = 1;
        for (int i = 1; i <= X; i++) {
            for (int j = 1; j <= Y_new; j++) {
                if (j <= Y_old) {
                    int oldKey = (i - 1) * Y_old + j;
                    oaa_Attr.put(num++, (BitSet) oldTradic.getObjsAndAttrs_Attr().get(oldKey).clone());
                } else {
                    BitSet bs = new BitSet();
                    // 新属性只在指定的对象和条件处有值
                    if (i == newX) bs.set(newZ);
                    oaa_Attr.put(num++, bs);
                }
            }
        }
        newT.setObjsAndAttrs_Attr(oaa_Attr);

        // 2. 重构 attrsAndCondi_Attr: key = (j-1) * Z + k
        Map<Integer, BitSet> aca_Attr = new HashMap<>();
        num = 1;
        for (int j = 1; j <= Y_new; j++) {
            for (int k = 1; k <= Z; k++) {
                if (j <= Y_old) {
                    int oldKey = (j - 1) * Z + k;
                    aca_Attr.put(num++, (BitSet) oldTradic.getAttrsAndCondi_Attr().get(oldKey).clone());
                } else {
                    BitSet bs = new BitSet();
                    if (k == newZ) bs.set(newX);
                    aca_Attr.put(num++, bs);
                }
            }
        }
        newT.setAttrsAndCondi_Attr(aca_Attr);

        // 重构 attrsAndCondi_Obj (反向映射)
        Map<Integer, BitSet> aca_Obj = new HashMap<>();
        for (int i = 1; i <= X; i++) {
            BitSet bs = new BitSet();
            for (int j = 1; j <= Y_new * Z; j++) {
                if (aca_Attr.get(j).get(i)) bs.set(j);
            }
            aca_Obj.put(i, bs);
        }
        newT.setAttrsAndCondi_Obj(aca_Obj);

        // 3. 重构 objsAndCondi_Attr: key = (i-1) * Z + k (该 Map 的 key 算法与 y 无关，只需追加值)
        Map<Integer, BitSet> oca_Attr = new HashMap<>();
        num = 1;
        for (int i = 1; i <= X; i++) {
            for (int k = 1; k <= Z; k++) {
                int oldKey = (i - 1) * Z + k;
                BitSet bs = (BitSet) oldTradic.getObjsAndCondi_Attr().get(oldKey).clone();
                if (i == newX && k == newZ) {
                    bs.set(Y_new); // 将新属性加进去
                }
                oca_Attr.put(num++, bs);
            }
        }
        newT.setObjsAndCondi_Attr(oca_Attr);

        return newT;
    }

    /**
     * 定理4：针对新增了【全新属性】的三元组 (newX, y_new, newZ)，进行概念更新
     * 利用推论1直接判定 |EC'| 与 |EC|，完全规避全局候选集的重新计算。
     */
    public static Set<TriadicConcept> generateByTheorem4(List<TriadicConcept> oldConcepts, Tradic oldTradic, int newX, int newZ) {
        Set<TriadicConcept> newConcepts = new HashSet<>();
        int Y_old = oldTradic.getY();
        int y_new = Y_old + 1; // 新属性 a2 的索引

        // 1. 获取 B_{a3}^{a1} (即在旧背景中，对象 newX 在条件 newZ 下的所有属性)
        int ocaKey = (newX - 1) * oldTradic.getZ() + newZ;
        BitSet B_a3_a1 = new BitSet();
        if (oldTradic.getObjsAndCondi_Attr().get(ocaKey) != null) {
            B_a3_a1 = (BitSet) oldTradic.getObjsAndCondi_Attr().get(ocaKey).clone();
        }

        // 2. 利用推论1直接计算 |EC'(a1)| 与 |EC(a1)| 是否相等
        // 2.1 提取对象 newX 在所有条件下的属性集合，放入集合 S
        Set<BitSet> S = new HashSet<>();
        for (int k = 1; k <= oldTradic.getZ(); k++) {
            int key = (newX - 1) * oldTradic.getZ() + k;
            BitSet Bk = oldTradic.getObjsAndCondi_Attr().get(key);
            if (Bk != null && !Bk.isEmpty()) {
                S.add(Bk);
            }
        }

        // 2.2 快速求 S 的幂集交集，得到旧的外延待选集 EC(a1)
        Set<BitSet> EC_a1 = new HashSet<>();
        Set<Set<BitSet>> powerSet = extendCandidate.powerSet(S);
        for (Set<BitSet> subset : powerSet) {
            if (subset.isEmpty()) continue;
            BitSet intersection = (BitSet) subset.iterator().next().clone();
            for (BitSet bs : subset) {
                intersection.and(bs);
            }
            if (!intersection.isEmpty()) {
                EC_a1.add(intersection);
            }
        }

        // 2.3 判断 EC(a1) 中是否包含 B_{a3}^{a1} 的超集
        int delta = 0;
        for (BitSet A : EC_a1) {
            BitSet temp = (BitSet) A.clone();
            temp.and(B_a3_a1);
            if (temp.equals(B_a3_a1)) { // 判定条件：B_{a3}^{a1} ⊆ A
                delta++;
            }
        }
        boolean isSizeEqual = (delta == 0); // 判断 |EC'(a1)| 是否等于 |EC(a1)|

        boolean hasA1Concept = false; // 用于标记原背景中是否存在外延为 {a1} 的概念

        // 3. 遍历旧概念，执行定理4的 O(1) 判定与更新
        for (TriadicConcept oldC : oldConcepts) {
            // 补充处理：对空外延或空方式的极限概念，内涵必定包含所有属性，需补充新属性 (参考论文2.2节文字描述)
            if (oldC.extent.isEmpty() || oldC.modus.isEmpty()) {
                TriadicConcept updatedC = new TriadicConcept(oldC.extent, oldC.intent, oldC.modus);
                updatedC.intent.set(y_new);
                newConcepts.add(updatedC);
                continue;
            }

            boolean isOnlyA1 = (oldC.extent.cardinality() == 1 && oldC.extent.get(newX));

            if (!isOnlyA1) {
                // 定理4(1): 若 A1 != {a1}，保持不变
                newConcepts.add(new TriadicConcept(oldC.extent, oldC.intent, oldC.modus));
            } else {
                hasA1Concept = true;
                // 定理4(2): 若 A1 == {a1}
                if (!oldC.modus.get(newZ)) {
                    // a3 不在 A3 中，保持不变
                    newConcepts.add(new TriadicConcept(oldC.extent, oldC.intent, oldC.modus));
                } else {
                    // a3 在 A3 中
                    if (oldC.intent.equals(B_a3_a1)) {
                        // 若 A2 == B_{a3}^{a1}，内涵更新增加 a2
                        TriadicConcept updatedC = new TriadicConcept(oldC.extent, oldC.intent, oldC.modus);
                        updatedC.intent.set(y_new);
                        newConcepts.add(updatedC);
                    } else {
                        // 若 A2 != B_{a3}^{a1}
                        if (isSizeEqual) {
                            // |EC'(a1)| == |EC(a1)|，仅保持旧概念
                            newConcepts.add(new TriadicConcept(oldC.extent, oldC.intent, oldC.modus));
                        } else {
                            // |EC'(a1)| != |EC(a1)|，保留旧概念，并生成新概念
                            newConcepts.add(new TriadicConcept(oldC.extent, oldC.intent, oldC.modus));

                            // 依据推论1，严格校验 B_{a3}^{a1} ⊆ A2，只有满足条件的 A2 才能生成含 a2 的新概念
                            BitSet checkInclude = (BitSet) oldC.intent.clone();
                            checkInclude.and(B_a3_a1);
                            if (checkInclude.equals(B_a3_a1)) {
                                TriadicConcept updatedC = new TriadicConcept(oldC.extent, oldC.intent, oldC.modus);
                                updatedC.intent.set(y_new);
                                newConcepts.add(updatedC);
                            }
                        }
                    }
                }
            }
        }

        // 定理4(3): 若原三元背景中根本不存在外延为 {a1} 的概念，则基于新增点直接生成一个
        if (!hasA1Concept) {
            BitSet ext = new BitSet(); ext.set(newX);
            BitSet intt = (BitSet) B_a3_a1.clone(); intt.set(y_new);
            BitSet mod = new BitSet(); mod.set(newZ);
            newConcepts.add(new TriadicConcept(ext, intt, mod));
        }

        return newConcepts;
    }
}