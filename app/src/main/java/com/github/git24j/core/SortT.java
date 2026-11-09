package com.github.git24j.core;

/** Flags to specify the sorting which a revwalk should perform. */
public enum SortT implements IBitEnum {
    /**
     * Sort the output with the same default method from `git`: reverse chronological order. This is
     * the default sorting for new walkers.
     */
    NONE(0),

    /**
     * Sort the repository contents in topological order (no parents before all of its children are
     * shown); this sorting mode can be combined with time sorting to produce `git`'s
     * `--date-order``.
     */
    TOPOLOGICAL(1 << 0),

    /**
     * Sort the repository contents by commit time; this sorting mode can be combined with
     * topological sorting.
     */
    TIME(1 << 1),

    /**
     * Iterate through the repository contents in reverse order; this sorting mode can be combined
     * with any of the above.
     */
    REVERSE(1 << 2);

    private final int _bit;

    SortT(int bit) {
        _bit = bit;
    }

    @Override
    public int getBit() {
        return _bit;
    }
}
