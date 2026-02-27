package algorithm;

import java.util.BitSet;
import java.util.Map;

public class Tradic {

    private Map<Integer, BitSet> objsAndAttrs_Attr;
    private Map<Integer, BitSet> objsAndAttrs_Obj;
    private Map<Integer, BitSet> attrsAndCondi_Attr;
    private Map<Integer, BitSet> attrsAndCondi_Obj;
    private Map<Integer, BitSet> objsAndCondi_Attr;
    private Map<Integer, BitSet> objsAndCondi_Obj;
    private Map<Integer, BitSet> CondiAndobjs_Obj;
    private Map<Integer, BitSet> CondiAndobjs_Attr;
    private int objsAndAttrs_AttrSize;
    private int x;
    private int attrsAndCondi_AttrSize;
    private int y;
    private int objsAndCondi_AttrSize;
    private int z;

    public Tradic() {
    }


    public Map<Integer, BitSet> getObjsAndAttrs_Attr() {
        return objsAndAttrs_Attr;
    }

    public void setObjsAndAttrs_Attr(Map<Integer, BitSet> objsAndAttrs_Attr) {
        this.objsAndAttrs_Attr = objsAndAttrs_Attr;
    }

    public Map<Integer, BitSet> getObjsAndAttrs_Obj() {
        return objsAndAttrs_Obj;
    }

    public void setObjsAndAttrs_Obj(Map<Integer, BitSet> objsAndAttrs_Obj) {
        this.objsAndAttrs_Obj = objsAndAttrs_Obj;
    }

    public Map<Integer, BitSet> getAttrsAndCondi_Attr() {
        return attrsAndCondi_Attr;
    }

    public void setAttrsAndCondi_Attr(Map<Integer, BitSet> attrsAndCondi_Attr) {
        this.attrsAndCondi_Attr = attrsAndCondi_Attr;
    }

    public Map<Integer, BitSet> getAttrsAndCondi_Obj() {
        return attrsAndCondi_Obj;
    }

    public void setAttrsAndCondi_Obj(Map<Integer, BitSet> attrsAndCondi_Obj) {
        this.attrsAndCondi_Obj = attrsAndCondi_Obj;
    }

    public Map<Integer, BitSet> getObjsAndCondi_Attr() {
        return objsAndCondi_Attr;
    }

    public void setObjsAndCondi_Attr(Map<Integer, BitSet> objsAndCondi_Attr) {
        this.objsAndCondi_Attr = objsAndCondi_Attr;
    }

    public Map<Integer, BitSet> getObjsAndCondi_Obj() {
        return objsAndCondi_Obj;
    }

    public void setObjsAndCondi_Obj(Map<Integer, BitSet> objsAndCondi_Obj) {
        this.objsAndCondi_Obj = objsAndCondi_Obj;
    }

    public int getObjsAndAttrs_AttrSize() {
        return objsAndAttrs_AttrSize;
    }

    public void setObjsAndAttrs_AttrSize(int objsAndAttrs_AttrSize) {
        this.objsAndAttrs_AttrSize = objsAndAttrs_AttrSize;
    }



    public int getAttrsAndCondi_AttrSize() {
        return attrsAndCondi_AttrSize;
    }

    public void setAttrsAndCondi_AttrSize(int attrsAndCondi_AttrSize) {
        this.attrsAndCondi_AttrSize = attrsAndCondi_AttrSize;
    }



    public int getObjsAndCondi_AttrSize() {
        return objsAndCondi_AttrSize;
    }

    public void setObjsAndCondi_AttrSize(int objsAndCondi_AttrSize) {
        this.objsAndCondi_AttrSize = objsAndCondi_AttrSize;
    }

    public Map<Integer, BitSet> getCondiAndobjs_Obj() {
        return CondiAndobjs_Obj;
    }

    public void setCondiAndobjs_Obj(Map<Integer, BitSet> condiAndobjs_Obj) {
        CondiAndobjs_Obj = condiAndobjs_Obj;
    }

    public Map<Integer, BitSet> getCondiAndobjs_Attr() {
        return CondiAndobjs_Attr;
    }

    public void setCondiAndobjs_Attr(Map<Integer, BitSet> condiAndobjs_Attr) {
        CondiAndobjs_Attr = condiAndobjs_Attr;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}