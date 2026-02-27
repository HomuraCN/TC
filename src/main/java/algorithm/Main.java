package algorithm;

import utils.Context;
import utils.concept.Concept;

import java.util.*;

import static utils.util.*;

public class Main {
    public static long count=1;
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        Tradic tradic = File.readFileToTradic("D:\\H\\Code\\Java\\TC\\src\\main\\java\\datasets\\context.txt");
        Context context=new Context();
        context.setAttrs(tradic.getAttrsAndCondi_Attr());
        context.setObjs(tradic.getAttrsAndCondi_Obj());
        context.setObjs_size(tradic.getX());
        context.setAttrs_size(tradic.getAttrsAndCondi_AttrSize());

        Concept concept=new Concept();
        concept.setExtent(makeSet(context.getObjs_size()));
        concept.setIntent(makeSet(0));
        Map<Integer, BitSet> nj=new HashMap<>();
        Queue<Concept> res=new LinkedList<>();
        InClose3.inClose3_exe(context,concept,1,nj,res);

        Map<BitSet, Set<BitSet>> bitSetSetMap = extendCandidate.extendCandidateExe(tradic, res);
        for(Map.Entry<BitSet, Set<BitSet>> entry : bitSetSetMap.entrySet()){
            BitSet key=entry.getKey();
            Set<BitSet> set=entry.getValue();
            for(BitSet value : set){
                BitSet setTemp=new BitSet();
                for(int i=key.nextSetBit(0);i>=0;i=key.nextSetBit(i+1)){
                    for(int j=value.nextSetBit(0);j>=0;j=value.nextSetBit(j+1)){
                        int num=(i-1)*tradic.getY()+j;
                        if(setTemp.cardinality()==0){
                            setTemp=tradic.getObjsAndAttrs_Attr().get(num);
                        }else {
                            BitSet temp = tradic.getObjsAndAttrs_Attr().get(num);
                            setTemp=intersection(temp,setTemp);
                        }
                    }
                }
                BitSet setTemp1=new BitSet();
                for(int i=value.nextSetBit(0);i>=0;i=value.nextSetBit(i+1)){
                    for(int j=setTemp.nextSetBit(0);j>=0;j=setTemp.nextSetBit(j+1)){
                        int num=(i-1)*tradic.getZ()+j;
                        if(setTemp1.cardinality()==0){
                            setTemp1=tradic.getAttrsAndCondi_Attr().get(num);
                        }else{
                            BitSet temp=tradic.getAttrsAndCondi_Attr().get(num);
                            setTemp1=intersection(temp,setTemp1);
                        }
                    }
                }
                if(isEqual(setTemp1,key)){
                    System.out.println(key+" "+value+" "+setTemp + " id:" +(count++));
                }
            }
        }
        System.out.println("消耗时间"+(System.currentTimeMillis()-start));
    }
}
