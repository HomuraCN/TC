package utils;


import java.util.BitSet;
import java.util.Map;

public class Context {
    private Map<Integer, BitSet> objs;
    private Map<Integer, BitSet> attrs;
    private int objs_size;
    private int attrs_size;

    public Map<Integer, BitSet> getObjs() {
        return objs;
    }

    public void setObjs(Map<Integer, BitSet> objs) {
        this.objs = objs;
    }

    public Map<Integer, BitSet> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<Integer, BitSet> attrs) {
        this.attrs = attrs;
    }

    public int getObjs_size() {
        return objs_size;
    }

    public void setObjs_size(int objs_size) {
        this.objs_size = objs_size;
    }

    public int getAttrs_size() {
        return attrs_size;
    }

    public void setAttrs_size(int attrs_size) {
        this.attrs_size = attrs_size;
    }

    public Context() {
    }

    @Override
    public String toString() {
        return "Context{" +
                "objs=" + objs +
                ", attrs=" + attrs +
                ", objs_size=" + objs_size +
                ", attrs_size=" + attrs_size +
                '}';
    }
}



