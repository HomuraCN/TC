package utils;

import algorithm.*;
import utils.concept.Concept;
import java.util.*;
import static utils.util.*;

public class TriadicConceptGenerator1 {

    public static List<TriadicConcept> getAllTriadicConcepts(Tradic tradic, Context contextK3) {
        List<TriadicConcept> allConcepts = new ArrayList<>();

        Concept initialConcept = new Concept();
        initialConcept.setExtent(makeSet(contextK3.getObjs_size()));
        initialConcept.setIntent(new BitSet());
        Map<Integer, BitSet> nj = new HashMap<>();
        Queue<Concept> res = new LinkedList<>();
        InClose3.inClose3_exe(contextK3, initialConcept, 1, nj, res);

        Map<BitSet, Set<BitSet>> candidatesMap = extendCandidate1.extendCandidateExe(tradic, res);

        for (Map.Entry<BitSet, Set<BitSet>> entry : candidatesMap.entrySet()) {
            BitSet modusX = entry.getKey();
            Set<BitSet> candidateA = entry.getValue();

            for (BitSet extentA : candidateA) {
                BitSet intentI = new BitSet();
                boolean isFirstI = true;
                for (int j = extentA.nextSetBit(0); j >= 0; j = extentA.nextSetBit(j + 1)) {
                    for (int i = modusX.nextSetBit(0); i >= 0; i = modusX.nextSetBit(i + 1)) {
                        int num = (j - 1) * tradic.getZ() + i;
                        BitSet temp = tradic.getObjsAndCondi_Attr().get(num);
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
                            int num = (i - 1) * tradic.getY() + j;
                            BitSet temp = tradic.getObjsAndAttrs_Attr().get(num);
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
                    allConcepts.add(new TriadicConcept(extentA, intentI, modusX));
                }
            }
        }
        return allConcepts;
    }
}