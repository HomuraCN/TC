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
     * 定理1
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
            if (expands) return false; // 旧概念失效
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

        return true; // 没有发生扩张，旧概念保留
    }

    /**
     * 将新增的三元组 (x, y, z) 更新到 Tradic 结构中，生成 K+
     */
    public static void addRelationToTradic(Tradic tradic, int x, int y, int z) {
        // 1. 更新 objsAndAttrs (正向与反向): key = (i-1)*y+j
        int key1 = (x - 1) * tradic.getY() + y;
        if (tradic.getObjsAndAttrs_Attr() != null) {
            if (tradic.getObjsAndAttrs_Attr().get(key1) == null) {
                tradic.getObjsAndAttrs_Attr().put(key1, new BitSet());
            }
            tradic.getObjsAndAttrs_Attr().get(key1).set(z);
        }
        if (tradic.getObjsAndAttrs_Obj() != null) {
            if (tradic.getObjsAndAttrs_Obj().get(z) == null) {
                tradic.getObjsAndAttrs_Obj().put(z, new BitSet());
            }
            tradic.getObjsAndAttrs_Obj().get(z).set(key1);
        }

        // 2. 更新 attrsAndCondi (正向与反向): key = (j-1)*z+k
        int key2 = (y - 1) * tradic.getZ() + z;
        if (tradic.getAttrsAndCondi_Attr() != null) {
            if (tradic.getAttrsAndCondi_Attr().get(key2) == null) {
                tradic.getAttrsAndCondi_Attr().put(key2, new BitSet());
            }
            tradic.getAttrsAndCondi_Attr().get(key2).set(x);
        }
        if (tradic.getAttrsAndCondi_Obj() != null) {
            if (tradic.getAttrsAndCondi_Obj().get(x) == null) {
                tradic.getAttrsAndCondi_Obj().put(x, new BitSet());
            }
            tradic.getAttrsAndCondi_Obj().get(x).set(key2);
        }

        // 3. 更新 objsAndCondi (正向与反向): key = (i-1)*z+k
        int key3 = (x - 1) * tradic.getZ() + z;
        if (tradic.getObjsAndCondi_Attr() != null) {
            if (tradic.getObjsAndCondi_Attr().get(key3) == null) {
                tradic.getObjsAndCondi_Attr().put(key3, new BitSet());
            }
            tradic.getObjsAndCondi_Attr().get(key3).set(y);
        }
        if (tradic.getObjsAndCondi_Obj() != null) {
            if (tradic.getObjsAndCondi_Obj().get(y) == null) {
                tradic.getObjsAndCondi_Obj().put(y, new BitSet());
            }
            tradic.getObjsAndCondi_Obj().get(y).set(key3);
        }

        // 4. 专门为定理 2_2 维护 CondiAndobjs 字典 (条件在前，对象在后)
        int key4 = (z - 1) * tradic.getX() + x;
        if (tradic.getCondiAndobjs_Attr() != null) {
            if (tradic.getCondiAndobjs_Attr().get(key4) == null) {
                tradic.getCondiAndobjs_Attr().put(key4, new BitSet());
            }
            tradic.getCondiAndobjs_Attr().get(key4).set(y);
        }
        if (tradic.getCondiAndobjs_Obj() != null) {
            if (tradic.getCondiAndobjs_Obj().get(y) == null) {
                tradic.getCondiAndobjs_Obj().put(y, new BitSet());
            }
            tradic.getCondiAndobjs_Obj().get(y).set(key4);
        }
    }

    /**
     * 定理2
     */
    public static Set<TriadicConcept> generateByTheorem2(Tradic newTradic, Context newContext, int x, int y, int z) {
        Set<TriadicConcept> newConcepts = new HashSet<>();

        Concept initialConcept = new Concept();
        initialConcept.setExtent(makeSet(newContext.getObjs_size()));
        initialConcept.setIntent(new BitSet());
        Map<Integer, BitSet> nj = new HashMap<>();
        Queue<Concept> res = new LinkedList<>();
        InClose3.inClose3_exe(newContext, initialConcept, 1, nj, res);

        // 计算 RIGHT = {(a2, a3)}^(1)'
        int yzKey = (y - 1) * newTradic.getZ() + z;
        BitSet rightBoundary = newTradic.getAttrsAndCondi_Attr().get(yzKey);
        if (rightBoundary == null) rightBoundary = new BitSet();

        Queue<Concept> filteredRes = new LinkedList<>();
        for (Concept c : res) {
            BitSet X = c.getExtent();

            // 1. LEFT ⊆ X：
            // X.get(x) 等价于 {a1}^(1)'(1)' ⊆ X
            boolean satisfiesLeft = X.get(x);

            // 2. X ⊆ RIGHT：
            // 集合差集运算 X \ RIGHT 如为空，则 X ⊆ RIGHT
            BitSet temp = (BitSet) X.clone();
            temp.andNot(rightBoundary);
            boolean satisfiesRight = temp.isEmpty();

            // 3. LEFT ⊆ X ⊆ RIGHT
            if (satisfiesLeft && satisfiesRight) {
                filteredRes.add(c);
            }
        }

        Map<BitSet, Set<BitSet>> candidatesMap = extendCandidate.extendCandidateExe(newTradic, filteredRes);

        for (Map.Entry<BitSet, Set<BitSet>> entry : candidatesMap.entrySet()) {
            BitSet extentX = entry.getKey();
            Set<BitSet> candidateA = entry.getValue();

            for (BitSet intent : candidateA) {
                // 使用 boolean 标志位来判断是否是首次赋值，而不是 cardinality()
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
                            modusZ.and(temp); // 原生 AND 求交集
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
     * 定理2_2
     */
    public static Set<TriadicConcept> generateByTheorem2_2(Tradic newTradic, Context contextK2, int x, int y, int z) {
        Set<TriadicConcept> newConcepts = new HashSet<>();
        Concept initialConcept = new Concept();
        initialConcept.setExtent(makeSet(contextK2.getObjs_size()));
        initialConcept.setIntent(makeSet(0));
        Map<Integer, BitSet> nj = new HashMap<>();
        Queue<Concept> res = new LinkedList<>();
        InClose3.inClose3_exe(contextK2, initialConcept, 1, nj, res);

        int xzKey = (x - 1) * newTradic.getZ() + z;
        BitSet rightBoundary = newTradic.getObjsAndCondi_Attr().get(xzKey);
        if (rightBoundary == null) rightBoundary = new BitSet();

        Queue<Concept> filteredRes = new LinkedList<>();
        for (Concept c : res) {
            BitSet X = c.getExtent();

            boolean satisfiesLeft = X.get(y);

            BitSet temp = (BitSet) X.clone();
            temp.andNot(rightBoundary);
            boolean satisfiesRight = temp.isEmpty();

            if (satisfiesLeft && satisfiesRight) {
                filteredRes.add(c);
            }
        }

        Map<BitSet, Set<BitSet>> candidatesMap = extendCandidate2.extendCandidateExe(newTradic, filteredRes);

        for (Map.Entry<BitSet, Set<BitSet>> entry : candidatesMap.entrySet()) {
            BitSet intentX = entry.getKey();
            Set<BitSet> candidateA = entry.getValue();

            for (BitSet modusA : candidateA) {
                BitSet extentE = new BitSet();
                boolean isFirstE = true;
                for (int i = intentX.nextSetBit(0); i >= 0; i = intentX.nextSetBit(i + 1)) {
                    for (int j = modusA.nextSetBit(0); j >= 0; j = modusA.nextSetBit(j + 1)) {
                        int num = (i - 1) * newTradic.getZ() + j;
                        BitSet temp = newTradic.getAttrsAndCondi_Attr().get(num);
                        if (temp == null) temp = new BitSet();

                        if (isFirstE) {
                            extentE = (BitSet) temp.clone();
                            isFirstE = false;
                        } else {
                            extentE.and(temp);
                        }
                    }
                }

                BitSet derivedIntentQ = new BitSet();
                boolean isFirstQ = true;
                if (!isFirstE) {
                    for (int j = extentE.nextSetBit(0); j >= 0; j = extentE.nextSetBit(j + 1)) {
                        for (int i = modusA.nextSetBit(0); i >= 0; i = modusA.nextSetBit(i + 1)) {
                            int num = (j - 1) * newTradic.getZ() + i;
                            BitSet temp = newTradic.getObjsAndCondi_Attr().get(num);
                            if (temp == null) temp = new BitSet();

                            if (isFirstQ) {
                                derivedIntentQ = (BitSet) temp.clone();
                                isFirstQ = false;
                            } else {
                                derivedIntentQ.and(temp);
                            }
                        }
                    }
                }

                if (!isFirstQ && isEqual(derivedIntentQ, intentX)) {
                    if (extentE.get(x) && intentX.get(y) && modusA.get(z)) {
                        newConcepts.add(new TriadicConcept(extentE, intentX, modusA));
                    }
                }
            }
        }
        return newConcepts;
    }

    /**
     * 定理2_3
     */
    public static Set<TriadicConcept> generateByTheorem2_3(Tradic newTradic, Context contextK3, int x, int y, int z) {
        Set<TriadicConcept> newConcepts = new HashSet<>();
        Concept initialConcept = new Concept();
        initialConcept.setExtent(makeSet(contextK3.getObjs_size()));
        initialConcept.setIntent(makeSet(0));
        Map<Integer, BitSet> nj = new HashMap<>();
        Queue<Concept> res = new LinkedList<>();
        InClose3.inClose3_exe(contextK3, initialConcept, 1, nj, res);

        int xyKey = (x - 1) * newTradic.getY() + y;
        BitSet rightBoundary = newTradic.getObjsAndAttrs_Attr().get(xyKey);
        if (rightBoundary == null) rightBoundary = new BitSet();

        Queue<Concept> filteredRes = new LinkedList<>();
        for (Concept c : res) {
            BitSet X = c.getExtent();

            boolean satisfiesLeft = X.get(z);

            BitSet temp = (BitSet) X.clone();
            temp.andNot(rightBoundary);
            boolean satisfiesRight = temp.isEmpty();

            if (satisfiesLeft && satisfiesRight) {
                filteredRes.add(c);
            }
        }

        Map<BitSet, Set<BitSet>> candidatesMap = extendCandidate1.extendCandidateExe(newTradic, filteredRes);

        for (Map.Entry<BitSet, Set<BitSet>> entry : candidatesMap.entrySet()) {
            BitSet modusX = entry.getKey();
            Set<BitSet> candidateA = entry.getValue();

            for (BitSet extentA : candidateA) {
                BitSet intentI = new BitSet();
                boolean isFirstI = true;
                for (int j = extentA.nextSetBit(0); j >= 0; j = extentA.nextSetBit(j + 1)) {
                    for (int i = modusX.nextSetBit(0); i >= 0; i = modusX.nextSetBit(i + 1)) {
                        int num = (j - 1) * newTradic.getZ() + i;
                        BitSet temp = newTradic.getObjsAndCondi_Attr().get(num);
                        if (temp == null) temp = new BitSet();

                        if (isFirstI) {
                            intentI = (BitSet) temp.clone();
                            isFirstI = false;
                        } else {
                            intentI.and(temp);
                        }
                    }
                }

                BitSet derivedModusQ = new BitSet();
                boolean isFirstQ = true;
                if (!isFirstI) {
                    for (int i = extentA.nextSetBit(0); i >= 0; i = extentA.nextSetBit(i + 1)) {
                        for (int j = intentI.nextSetBit(0); j >= 0; j = intentI.nextSetBit(j + 1)) {
                            int num = (i - 1) * newTradic.getY() + j;
                            BitSet temp = newTradic.getObjsAndAttrs_Attr().get(num);
                            if (temp == null) temp = new BitSet();

                            if (isFirstQ) {
                                derivedModusQ = (BitSet) temp.clone();
                                isFirstQ = false;
                            } else {
                                derivedModusQ.and(temp);
                            }
                        }
                    }
                }

                if (!isFirstQ && isEqual(derivedModusQ, modusX)) {
                    if (extentA.get(x) && intentI.get(y) && modusX.get(z)) {
                        newConcepts.add(new TriadicConcept(extentA, intentI, modusX));
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
     * 定理4：针对新增属性的三元组 (newX, y_new, newZ)，进行概念更新
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

        // 2. 判断 |EC'(a1)| 与 |EC(a1)| 是否相等
        // 换了个比较容易懂的写法，和之前的等价
        boolean isSizeEqual = true;
        for (int k = 1; k <= oldTradic.getZ(); k++) {
            if (k == newZ) continue; // 排除 B_{a3}^{a1} 自身 (对应公式的 \ {B_a3^a1})

            int key = (newX - 1) * oldTradic.getZ() + k;
            BitSet E = oldTradic.getObjsAndCondi_Attr().get(key);

            if (E != null && !E.isEmpty()) {
                // 判断 B_{a3}^{a1} 是否是 E 的子集
                BitSet temp = (BitSet) B_a3_a1.clone();
                temp.and(E);
                if (temp.equals(B_a3_a1)) {
                    // 存在 E 使得 B ⊆ E，此时 |EC'| = |EC| + 1，直接 break
                    isSizeEqual = false;
                    break;
                }
            }
        }

//        boolean hasA1Concept = false;
        boolean generatedNew = false; // 标记是否已经在遍历中生成了包含新属性的新概念

        // 3. 遍历旧概念，执行定理4的 判定与更新
        for (TriadicConcept oldC : oldConcepts) {
            if (oldC.extent.isEmpty() || oldC.modus.isEmpty()) {
                TriadicConcept updatedC = new TriadicConcept(oldC.extent, oldC.intent, oldC.modus);
                updatedC.intent.set(y_new);
                newConcepts.add(updatedC);
                continue;
            }

            boolean isOnlyA1 = (oldC.extent.cardinality() == 1 && oldC.extent.get(newX));

            if (isOnlyA1) {
                if (oldC.modus.get(newZ) && oldC.intent.equals(B_a3_a1)) {
                    // 若 A2 == B_a3_a1
                    // 产生一个仅在 newZ 条件下包含新属性的新概念
                    BitSet newModus = new BitSet();
                    newModus.set(newZ);
                    TriadicConcept updatedC = new TriadicConcept(oldC.extent, oldC.intent, newModus);
                    updatedC.intent.set(y_new);
                    newConcepts.add(updatedC);

                    generatedNew = true;

                    // 如果原概念在其他条件下依然存活(cardinality > 1)，必须保留旧概念
                    if (oldC.modus.cardinality() > 1) {
                        newConcepts.add(new TriadicConcept(oldC.extent, oldC.intent, oldC.modus));
                    }
                } else {
                    newConcepts.add(new TriadicConcept(oldC.extent, oldC.intent, oldC.modus));
                }
            } else {
                // A1 != {a1} 的概念不受影响，直接保留
                newConcepts.add(new TriadicConcept(oldC.extent, oldC.intent, oldC.modus));
            }
        }

        if (!generatedNew) {
            BitSet ext = new BitSet(); ext.set(newX);
            BitSet intt = (BitSet) B_a3_a1.clone(); intt.set(y_new);
            BitSet mod = new BitSet(); mod.set(newZ);
            newConcepts.add(new TriadicConcept(ext, intt, mod));
        }

        return newConcepts;
    }
}