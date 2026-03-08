package utils;

import algorithm.InClose3;
import algorithm.Tradic;
import algorithm.TriadicConcept;
import algorithm.extendCandidate2;
import utils.concept.Concept;
import java.util.*;
import static utils.util.*;

public class TriadicConceptGenerator2 {

    public static List<TriadicConcept> getAllTriadicConcepts(Tradic tradic, Context ignoredContext) {
        List<TriadicConcept> allConcepts = new ArrayList<>();

        // 完完全全按照 Main2.java 构建 K^(2) 的 Context
        Context context = new Context();
        context.setAttrs(tradic.getCondiAndobjs_Attr());
        context.setObjs(tradic.getCondiAndobjs_Obj());
        context.setObjs_size(tradic.getY());
        context.setAttrs_size(tradic.getObjsAndCondi_AttrSize());

        Concept concept = new Concept();
        concept.setExtent(makeSet(context.getObjs_size()));
        concept.setIntent(makeSet(0));
        Map<Integer, BitSet> nj = new HashMap<>();
        Queue<Concept> res = new LinkedList<>();
        InClose3.inClose3_exe(context, concept, 1, nj, res);

        // 完完全全复刻 Main2.java 的派生与验证逻辑
        Map<BitSet, Set<BitSet>> bitSetSetMap = extendCandidate2.extendCandidateExe(tradic, res);
        for(Map.Entry<BitSet, Set<BitSet>> entry : bitSetSetMap.entrySet()){
            BitSet key = entry.getKey();
            Set<BitSet> set = entry.getValue();
            for(BitSet value : set){
                BitSet setTemp = new BitSet();
                for(int i = key.nextSetBit(0); i >= 0; i = key.nextSetBit(i + 1)){
                    for(int j = value.nextSetBit(0); j >= 0; j = value.nextSetBit(j + 1)){
                        int num = (i - 1) * tradic.getZ() + j;
                        if(setTemp.cardinality() == 0){
                            setTemp = tradic.getAttrsAndCondi_Attr().get(num);
                        } else {
                            BitSet temp = tradic.getAttrsAndCondi_Attr().get(num);
                            setTemp = intersection(temp, setTemp);
                        }
                    }
                }
                BitSet setTemp1 = new BitSet();
                for(int j = setTemp.nextSetBit(0); j >= 0; j = setTemp.nextSetBit(j + 1)){
                    for(int i = value.nextSetBit(0); i >= 0; i = value.nextSetBit(i + 1)){
                        int num = (j - 1) * tradic.getZ() + i;
                        if(setTemp1.cardinality() == 0){
                            setTemp1 = tradic.getObjsAndCondi_Attr().get(num);
                        } else {
                            BitSet temp = tradic.getObjsAndCondi_Attr().get(num);
                            setTemp1 = intersection(temp, setTemp1);
                        }
                    }
                }
                if(isEqual(setTemp1, key)){
                    // 对应 Main2: setTemp是外延, key是内涵, value是方式
                    // 同样添加 .clone() 进行防御性拷贝
                    allConcepts.add(new TriadicConcept((BitSet)setTemp.clone(), (BitSet)key.clone(), (BitSet)value.clone()));
                }
            }
        }
        return allConcepts;
    }
}