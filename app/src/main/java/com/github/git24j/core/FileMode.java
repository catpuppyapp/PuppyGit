package com.github.git24j.core;

/** Valid modes for index and tree entries. */
public enum FileMode implements IBitEnum {
    UNREADABLE(0000000),
    TREE(0040000),
    BLOB(0100644),
    BLOB_EXECUTABLE(0100755),
    LINK(0120000),
    COMMIT(0160000);
    private final int _bit;

    FileMode(int bit) {
        _bit = bit;
    }

    @Override
    public int getBit() {
        return _bit;
    }
}
