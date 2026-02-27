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
     * 定理1：修复了原论文表述漏洞，真正实现 O(1) 的局部扩张校验
     */
    public static boolean isPreservedByTheorem1(TriadicConcept c, Tradic oldTradic, int x, int y, int z) {
        // 条件 (1): 若 x ∉ A1，则检查 A1 是否会因为新增 (x,y,z) 而发生扩张
        if (!c.extent.get(x)) {
            boolean expands = true; // 假设会扩张
            for (int j = c.intent.nextSetBit(0); j >= 0; j = c.intent.nextSetBit(j + 1)) {
                for (int k = c.modus.nextSetBit(0); k >= 0; k = c.modus.nextSetBit(k + 1)) {
                    // Y_new = Y_old U {(x,y,z)}
                    boolean inYnew = (j == y && k == z) || hasRelation(oldTradic, x, j, k);
                    if (!inYnew) {
                        expands = false; // 找到了一个不在 Y_new 中的关系，说明 x 填不满 A2 x A3，A1 不会扩张
                        break;
                    }
                }
                if (!expands) break;
            }
            if (expands) return false; // 如果 x 能填满，说明 A1 扩张了，旧概念失效！
        }

        // 条件 (2): 若 y ∉ A2，则检查 A2 是否会扩张
        if (!c.intent.get(y)) {
            boolean expands = true;
            for (int i = c.extent.nextSetBit(0); i >= 0; i = c.extent.nextSetBit(i + 1)) {
                for (int k = c.modus.nextSetBit(0); k >= 0; k = c.modus.nextSetBit(k + 1)) {
                    boolean inYnew = (i == x && k == z) || hasRelation(oldTradic, i, y, k);
                    if (!inYnew) {
                        expands = false;
                        break;
                    }
                }
                if (!expands) break;
            }
            if (expands) return false;
        }

        // 条件 (3): 若 z ∉ A3，则检查 A3 是否会扩张
        if (!c.modus.get(z)) {
            boolean expands = true;
            for (int i = c.extent.nextSetBit(0); i >= 0; i = c.extent.nextSetBit(i + 1)) {
                for (int j = c.intent.nextSetBit(0); j >= 0; j = c.intent.nextSetBit(j + 1)) {
                    boolean inYnew = (i == x && j == y) || hasRelation(oldTradic, i, j, z);
                    if (!inYnew) {
                        expands = false;
                        break;
                    }
                }
                if (!expands) break;
            }
            if (expands) return false;
        }

        return true; // 没有任何维度发生扩张，旧概念完美保留
    }

    /**
     * 将新增的三元组 (x, y, z) 更新到 Tradic 结构中，生成 K+
     */
    public static void addRelationToTradic(Tradic tradic, int x, int y, int z) {
        // 1. 更新 objsAndAttrs_Attr: key = (i-1)*y+j
        int key1 = (x - 1) * tradic.getY() + y;
        if (tradic.getObjsAndAttrs_Attr().get(key1) == null) {
            tradic.getObjsAndAttrs_Attr().put(key1, new BitSet());
        }
        tradic.getObjsAndAttrs_Attr().get(key1).set(z);

        // 2. 更新 attrsAndCondi_Attr: key = (j-1)*z+k
        int key2 = (y - 1) * tradic.getZ() + z;
        if (tradic.getAttrsAndCondi_Attr().get(key2) == null) {
            tradic.getAttrsAndCondi_Attr().put(key2, new BitSet());
        }
        tradic.getAttrsAndCondi_Attr().get(key2).set(x);

        // 3. 【关键修复】：同步更新 attrsAndCondi_Obj (反向映射)
        // InClose3 极度依赖此 Map，不更新会导致形式概念计算缺失
        if (tradic.getAttrsAndCondi_Obj().get(x) == null) {
            tradic.getAttrsAndCondi_Obj().put(x, new BitSet());
        }
        tradic.getAttrsAndCondi_Obj().get(x).set(key2);

        // 4. 更新 objsAndCondi_Attr: key = (i-1)*z+k
        int key3 = (x - 1) * tradic.getZ() + z;
        if (tradic.getObjsAndCondi_Attr().get(key3) == null) {
            tradic.getObjsAndCondi_Attr().put(key3, new BitSet());
        }
        tradic.getObjsAndCondi_Attr().get(key3).set(y);
    }

    /**
     * 定理2：修复了 cardinality == 0 带来的空集覆盖 Bug
     */
    public static Set<TriadicConcept> generateByTheorem2(Tradic newTradic, Context newContext, int x, int y, int z) {
        Set<TriadicConcept> newConcepts = new HashSet<>();

        Concept initialConcept = new Concept();
        initialConcept.setExtent(makeSet(newContext.getObjs_size()));
        initialConcept.setIntent(new BitSet());
        Map<Integer, BitSet> nj = new HashMap<>();
        Queue<Concept> res = new LinkedList<>();
        InClose3.inClose3_exe(newContext, initialConcept, 1, nj, res);

        Queue<Concept> filteredRes = new LinkedList<>();
        for (Concept c : res) {
            if (c.getExtent().get(x)) {
                filteredRes.add(c);
            }
        }

        Map<BitSet, Set<BitSet>> candidatesMap = extendCandidate.extendCandidateExe(newTradic, filteredRes);

        for (Map.Entry<BitSet, Set<BitSet>> entry : candidatesMap.entrySet()) {
            BitSet extentX = entry.getKey();
            Set<BitSet> candidateA = entry.getValue();

            for (BitSet intent : candidateA) {
                // 修复 Bug：使用 boolean 标志位来判断是否是首次赋值，而不是 cardinality()
                BitSet modusZ = new BitSet();
                boolean isFirstZ = true;
                for (int i = extentX.nextSetBit(0); i >= 0; i = extentX.nextSetBit(i + 1)) {
                    for (int j = intent.nextSetBit(0); j >= 0; j = intent.nextSetBit(j + 1)) {
                        int num = (i - 1) * newTradic.getY() + j;
                        BitSet temp = newTradic.getObjsAndAttrs_Attr().get(num);
                        if (temp == null) temp = new BitSet();

                        if (isFirstZ) {
                            modusZ = (BitSet) temp.clone();
                            isFirstZ = false;
                        } else {
                            modusZ.and(temp); // 直接利用原生的高效 AND 操作求交集
                        }
                    }
                }

                BitSet derivedExtent = new BitSet();
                boolean isFirstX = true;
                for (int j = intent.nextSetBit(0); j >= 0; j = intent.nextSetBit(j + 1)) {
                    for (int k = modusZ.nextSetBit(0); k >= 0; k = modusZ.nextSetBit(k + 1)) {
                        int num = (j - 1) * newTradic.getZ() + k;
                        BitSet temp = newTradic.getAttrsAndCondi_Attr().get(num);
                        if (temp == null) temp = new BitSet();

                        if (isFirstX) {
                            derivedExtent = (BitSet) temp.clone();
                            isFirstX = false;
                        } else {
                            derivedExtent.and(temp);
                        }
                    }
                }

                // isFirstX 为 false 代表有发生过交集计算，避免空集判断异常
                if (!isFirstX && isEqual(derivedExtent, extentX)) {
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