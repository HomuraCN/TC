package algorithm;

import java.io.FileReader;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class File2 {
    public static Tradic readFileToTradic(String fileName) throws Exception {
        Tradic res = new Tradic();
        Scanner sc = new Scanner(new FileReader(fileName));
        String[] s=sc.nextLine().split(" ");
        int x=Integer.parseInt(s[0]);
        int y=Integer.parseInt(s[1]);
        int z=Integer.parseInt(s[2]);
        res.setObjsAndAttrs_AttrSize(x*y);
        res.setX(x);
        res.setY(y);
        res.setZ(z);
//        res.setObjsAndAttrs_ObjSize(z);
        res.setAttrsAndCondi_AttrSize(y*z);
//        res.setAttrsAndCondi_ObjSize(x);
        res.setObjsAndCondi_AttrSize(x*z);
//        res.setObjsAndCondi_ObjSize(y);


        int[][][] tradic =new int[x][y][z];
        for(int i=0;i<x;i++){
            String[] temp=sc.nextLine().split(" ");
            int t=0;
            for(int k=0;k<z;k++){
                for(int j=0;j<y;j++){
                    tradic[i][j][k]=Integer.parseInt(temp[t++]);
                }
            }
        }
        Map<Integer, BitSet> objsAndAttrs_Attr=new HashMap<>();
        Map<Integer, BitSet> objsAndAttrs_Obj=new HashMap<>();
        int objsAndAttrsNum=1;
        //objsAndAttrsNums的值为下(i-1)*y+j （i是从1开始的对象序列，j是从1开始的属性序列。
        // y是属性个数）
        for(int i=0;i<x;i++){
            for(int j=0;j<y;j++){
                BitSet set = new BitSet();
                for(int k=0;k<z;k++){
                    if(tradic[i][j][k]==1){
                        set.set(k+1);
                    }
                }
                objsAndAttrs_Attr.put(objsAndAttrsNum++,set);
            }
        }
        for(int i=0;i<z;i++){
            BitSet set=new BitSet();
            for(int j=1;j<=x*y;j++){
                if(objsAndAttrs_Attr.get(j).get(i+1)){
                    set.set(j);
                }
            }
            objsAndAttrs_Obj.put(i+1,set);
        }

        // attrsAndCondiNum的值是j*z+k+1
        int attrsAndCondiNum=1;
        Map<Integer,BitSet> attrsAndCondi_Attr=new HashMap<>();
        Map<Integer,BitSet> attrsAndCondi_Obj=new HashMap<>();
        for(int j=0;j<y;j++){
            for(int k=0;k<z;k++){
                BitSet set = new BitSet();
                for(int i=0;i<x;i++){
                    if(tradic[i][j][k]==1){
                        set.set(i+1);
                    }
                }
                attrsAndCondi_Attr.put(attrsAndCondiNum++,set);
            }
        }
        for(int i=0;i<x;i++){
            BitSet set = new BitSet();
            for(int j=1;j<=y*z;j++){
                if(attrsAndCondi_Attr.get(j).get(i+1)){
                    set.set(j);
                }
            }
            attrsAndCondi_Obj.put(i+1,set);
        }

        Map<Integer, BitSet> objsAndCondi_Attr=new HashMap<>();
        Map<Integer, BitSet> objsAndCondi_Obj=new HashMap<>();
        int objsAndCondiNum=1;

        //objsAndCondiNums的值为下(i-1)*z+j （i是从1开始的对象序列，j是从1开始的属性序列。
        // z是条件个数）
        for(int i=0;i<x;i++){
            for(int k=0;k<z;k++){
                BitSet set = new BitSet();
                for(int j=0;j<y;j++){
                    if(tradic[i][j][k]==1){
                        set.set(j+1);
                    }
                }
                objsAndCondi_Attr.put(objsAndCondiNum++,set);
            }
        }
        for(int i=0;i<z;i++){
            BitSet set=new BitSet();
            for(int k=1;k<=x*z;k++){
                if(objsAndCondi_Attr.get(k).get(i+1)){
                    set.set(k);
                }
            }
            objsAndCondi_Obj.put(i+1,set);
        }

        Map<Integer, BitSet> CondiAndobjs_Attr=new HashMap<>();
        Map<Integer, BitSet> CondiAndobjs_Obj=new HashMap<>();
        int CondiAndobjsNum=1;
        //oCondiAndobjsNum的值为下(i-1)*y+j （i是从1开始的条件序列，j是从1开始的对象序列。
        // y是属性个数）
        for(int i=0;i<z;i++){
            for(int j=0;j<x;j++){
                BitSet set = new BitSet();
                for(int k=0;k<y;k++){
                    if(tradic[j][k][i]==1){
                        set.set(k+1);
                    }
                }
                CondiAndobjs_Attr.put(CondiAndobjsNum++,set);
            }
        }
        for(int i=0;i<y;i++){
            BitSet set=new BitSet();
            for(int j=1;j<=z*x;j++){
                if(CondiAndobjs_Attr.get(j).get(i+1)){
                    set.set(j);
                }
            }
            CondiAndobjs_Obj.put(i+1,set);
        }


//        Map<Integer,BitSet> objsAndCondi=new HashMap<>();
        //i*z+k

//        for(int i=0;i<x;i++){
//            for(int k=0;k<z;k++){
//                BitSet set = new BitSet();
//                for(int j=0;j<y;j++){
//                    if(tradic[i][j][k]==1){
//                        set.set(j+1);
//                    }
//                }
//                objsAndCondi.put(objsAndCondiNum++,set);
//            }
//        }

        res.setObjsAndAttrs_Attr(objsAndAttrs_Attr);
        res.setObjsAndAttrs_Obj(objsAndAttrs_Obj);
//        res.getObjsAndAttrs_Obj(o)
        res.setAttrsAndCondi_Attr(attrsAndCondi_Attr);
        res.setAttrsAndCondi_Obj(attrsAndCondi_Obj);
//        res.setObjsAndCondi_Attr(objsAndCondi);
        res.setObjsAndCondi_Attr(objsAndCondi_Attr);
        res.setObjsAndCondi_Obj(objsAndCondi_Obj);
        res.setCondiAndobjs_Attr(CondiAndobjs_Attr);
        res.setCondiAndobjs_Obj(CondiAndobjs_Obj);
        return res;
    }


}
