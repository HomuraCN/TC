package utils;

import algorithm.InClose3;
import algorithm.Tradic;
import algorithm.TriadicConcept;
import algorithm.extendCandidate;
import utils.Context;
import utils.concept.Concept;
import java.util.*;
import static utils.util.*;

public class TriadicConceptGenerator {

    /**
     * 获取指定三元背景下的所有三元概念
     */
    public static List<TriadicConcept> getAllTriadicConcepts(Tradic tradic, Context context) {
        List<TriadicConcept> allConcepts = new ArrayList<>();

        // 1. 获取诱导形式背景 K^(1) 的所有形式概念
        Concept initialConcept = new Concept();
        initialConcept.setExtent(makeSet(context.getObjs_size()));
        initialConcept.setIntent(new BitSet());
        Map<Integer, BitSet> nj = new HashMap<>();
        Queue<Concept> res = new LinkedList<>();
        InClose3.inClose3_exe(context, initialConcept, 1, nj, res);

        // 2. 计算外延待选集
        Map<BitSet, Set<BitSet>> candidatesMap = extendCandidate.extendCandidateExe(tradic, res);

        // 3. 遍历待选集，验证并生成三元概念
        for (Map.Entry<BitSet, Set<BitSet>> entry : candidatesMap.entrySet()) {
            BitSet extentX = entry.getKey();
            Set<BitSet> candidateA = entry.getValue();

            for (BitSet intent : candidateA) {
                // 计算 Z = (X × A)^(3)
                BitSet modusZ = new BitSet();
                for (int i = extentX.nextSetBit(0); i >= 0; i = extentX.nextSetBit(i + 1)) {
                    for (int j = intent.nextSetBit(0); j >= 0; j = intent.nextSetBit(j + 1)) {
                        int num = (i - 1) * tradic.getY() + j;
                        if (modusZ.cardinality() == 0) {
                            modusZ = (BitSet) tradic.getObjsAndAttrs_Attr().get(num).clone();
                        } else {
                            BitSet temp = tradic.getObjsAndAttrs_Attr().get(num);
                            modusZ = intersection(temp, modusZ);
                        }
                    }
                }

                // 计算 X' = (A × Z)^(1)
                BitSet derivedExtent = new BitSet();
                for (int j = intent.nextSetBit(0); j >= 0; j = intent.nextSetBit(j + 1)) {
                    for (int k = modusZ.nextSetBit(0); k >= 0; k = modusZ.nextSetBit(k + 1)) {
                        int num = (j - 1) * tradic.getZ() + k;
                        if (derivedExtent.cardinality() == 0) {
                            derivedExtent = (BitSet) tradic.getAttrsAndCondi_Attr().get(num).clone();
                        } else {
                            BitSet temp = tradic.getAttrsAndCondi_Attr().get(num);
                            derivedExtent = intersection(temp, derivedExtent);
                        }
                    }
                }

                // 验证 X' == X
                if (isEqual(derivedExtent, extentX)) {
                    allConcepts.add(new TriadicConcept(extentX, intent, modusZ));
                }
            }
        }
        return allConcepts;
    }
}