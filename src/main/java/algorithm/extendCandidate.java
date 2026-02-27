package algorithm;

import utils.concept.Concept;

import java.util.*;

import static utils.util.intersection;

public class extendCandidate {

    public static Map<BitSet,Set<BitSet>> extendCandidateExe(Tradic tradic, Queue<Concept> concepts) {
        HashMap<Integer, BitSet> map = new HashMap<>();
        Map<BitSet,Set<BitSet>> res=new HashMap<>();


        for(Concept concept:concepts){
            if(concept.getIntent().isEmpty()|| concept.getExtent().isEmpty()){
                continue;
            }
            for(int i=1;i<=tradic.getZ();i++){
                map.put(i,new BitSet());
            }
            BitSet intent=concept.getIntent();
            for(int i=intent.nextSetBit(0);i>=0;i=intent.nextSetBit(i+1)){
                if(i%tradic.getZ()==0){
                    map.get(tradic.getZ()).set(i/tradic.getZ());
                }else{
                    map.get(i% tradic.getZ()).set((i/tradic.getZ())+1);
                }
            }
            Set<BitSet> set=new HashSet<>();
            for(int i=1;i<=tradic.getZ();i++){
                if(map.get(i).cardinality()!=0){
                    set.add(map.get(i));
                }
            }
            Set<Set<BitSet>> powerSet = powerSet(set);
            for(Set<BitSet> power:powerSet){
                if(power.isEmpty()){
                    continue;
                }
                BitSet temp = new BitSet();
                temp=power.iterator().next();
                for(BitSet bitset:power){
                    temp=intersection(temp,bitset);
                }
                Set<BitSet> set1 = res.getOrDefault(concept.getExtent(), new HashSet<>());
                if(!temp.isEmpty()){
                    set1.add(temp);
                    res.put(concept.getExtent(), set1);
                }

            }
            map.clear();
        }
        return res;
    }


    public static  Set<Set<BitSet>> powerSet(Set<BitSet> originalSet) {
        Set<Set<BitSet>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }
        List<BitSet> list = new ArrayList<>(originalSet);
        BitSet head = list.get(0);
        Set<BitSet> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<BitSet> set : powerSet(rest)) {
            Set<BitSet> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }
}
