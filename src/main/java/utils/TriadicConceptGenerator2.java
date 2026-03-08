package utils;

import algorithm.*;
import utils.concept.Concept;
import java.util.*;
import static utils.util.*;

public class TriadicConceptGenerator2 {

    public static List<TriadicConcept> getAllTriadicConcepts(Tradic tradic, Context contextK2) {
        List<TriadicConcept> allConcepts = new ArrayList<>();

        Concept initialConcept = new Concept();
        initialConcept.setExtent(makeSet(contextK2.getObjs_size()));
        initialConcept.setIntent(new BitSet());
        Map<Integer, BitSet> nj = new HashMap<>();
        Queue<Concept> res = new LinkedList<>();
        InClose3.inClose3_exe(contextK2, initialConcept, 1, nj, res);

        Map<BitSet, Set<BitSet>> candidatesMap = extendCandidate2.extendCandidateExe(tradic, res);

        for (Map.Entry<BitSet, Set<BitSet>> entry : candidatesMap.entrySet()) {
            BitSet intentX = entry.getKey();
            Set<BitSet> candidateA = entry.getValue();

            for (BitSet modusA : candidateA) {
                BitSet extentE = new BitSet();
                boolean isFirstE = true;
                for (int i = intentX.nextSetBit(0); i >= 0; i = intentX.nextSetBit(i + 1)) {
                    for (int j = modusA.nextSetBit(0); j >= 0; j = modusA.nextSetBit(j + 1)) {
                        int num = (i - 1) * tradic.getZ() + j;
                        BitSet temp = tradic.getAttrsAndCondi_Attr().get(num);
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
                            int num = (j - 1) * tradic.getZ() + i;
                            BitSet temp = tradic.getObjsAndCondi_Attr().get(num);
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
                    allConcepts.add(new TriadicConcept(extentE, intentX, modusA));
                }
            }
        }
        return allConcepts;
    }
}