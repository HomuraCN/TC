package utils.concept;

import java.util.BitSet;

public class Concept {
    private BitSet extent;
    private BitSet intent;
    private int id=0;

    public Concept() {
    }

    @Override
    public String toString() {
        return "Concept{" +
                "extent=" + extent +
                ", intent=" + intent +
                ", id=" + id +
                '}';
    }

    public BitSet getExtent() {
        return extent;
    }

    public void setExtent(BitSet extent) {
        this.extent = extent;
    }

    public BitSet getIntent() {
        return intent;
    }

    public void setIntent(BitSet intent) {
        this.intent = intent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Concept(BitSet extent, BitSet intent) {
        this.extent = extent;
        this.intent = intent;
    }

    public Concept(BitSet extent, BitSet intent, int id) {
        this.extent = extent;
        this.intent = intent;
        this.id = id;
    }
}
