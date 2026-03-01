package algorithm;

import java.util.BitSet;
import java.util.Objects;

public class TriadicConcept {
    public BitSet extent;
    public BitSet intent;
    public BitSet modus;

    public TriadicConcept(BitSet extent, BitSet intent, BitSet modus) {
        this.extent = (BitSet) extent.clone();
        this.intent = (BitSet) intent.clone();
        this.modus = (BitSet) modus.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TriadicConcept that = (TriadicConcept) o;
        return extent.equals(that.extent) && intent.equals(that.intent) && modus.equals(that.modus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extent, intent, modus);
    }

    @Override
    public String toString() {
        return "TriadicConcept{" + "extent=" + extent + ", intent=" + intent + ", modus=" + modus + '}';
    }
}