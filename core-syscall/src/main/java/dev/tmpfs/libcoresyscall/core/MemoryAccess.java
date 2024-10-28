package dev.tmpfs.libcoresyscall.core;

import dev.tmpfs.libcoresyscall.core.impl.NativeBridge;
import libcore.io.Memory;

public class MemoryAccess {

    private MemoryAccess() {
        throw new AssertionError("no instances");
    }

    public static long getPageSize() {
        return NativeBridge.getPageSize();
    }

    // Since libcore.io.Memory is not part of public API, so we place a wrapper here.

    public static void peekByteArray(long address, byte[] dst, int offset, int byteCount) {
        Memory.peekByteArray(address, dst, offset, byteCount);
    }

    public static void pokeByteArray(long address, byte[] src, int offset, int byteCount) {
        Memory.pokeByteArray(address, src, offset, byteCount);
    }

    public static long peekLong(long address, boolean swap) {
        return Memory.peekLong(address, swap);
    }

    public static void pokeLong(long address, long value, boolean swap) {
        Memory.pokeLong(address, value, swap);
    }

    public static long peekLong(long address) {
        return Memory.peekLong(address, false);
    }

    public static void pokeLong(long address, long value) {
        Memory.pokeLong(address, value, false);
    }

    public static int peekInt(long address, boolean swap) {
        return Memory.peekInt(address, swap);
    }

    public static int peekInt(long address) {
        return Memory.peekInt(address, false);
    }

    public static void pokeInt(long address, int value, boolean swap) {
        Memory.pokeInt(address, value, swap);
    }

    public static void pokeInt(long address, int value) {
        Memory.pokeInt(address, value, false);
    }

    public static short peekByte(long address) {
        return Memory.peekByte(address);
    }

}
