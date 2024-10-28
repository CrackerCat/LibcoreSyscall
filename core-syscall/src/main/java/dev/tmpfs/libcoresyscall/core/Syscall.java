package dev.tmpfs.libcoresyscall.core;


import android.system.ErrnoException;
import android.system.OsConstants;

import androidx.annotation.NonNull;

import dev.tmpfs.libcoresyscall.core.impl.NativeBridge;
import dev.tmpfs.libcoresyscall.core.impl.trampoline.ISyscallNumberTable;
import dev.tmpfs.libcoresyscall.core.impl.trampoline.CommonSyscallNumberTables;

public class Syscall {

    static {
        initializeOnce();
    }

    private Syscall() {
        throw new AssertionError("no instances");
    }

    private static void initializeOnce() {
        NativeBridge.initializeOnce();
    }

    /**
     * Check if the result is an error.
     *
     * @param result the result returned by a raw syscall
     * @return true if the result is an error
     */
    public static boolean isError(long result) {
        // [-4095, -1] is error
        return result < 0 && result >= -4095;
    }

    /**
     * Get the result or throw an exception if the result is an error.
     *
     * @param result the result returned by a raw syscall
     * @param name   the name of the syscall
     * @return the result
     * @throws ErrnoException if the result is an error
     */
    public static long getResultOrThrow(long result, @NonNull String name) throws ErrnoException {
        if (isError(result)) {
            throw new ErrnoException(name, (int) -result);
        }
        return result;
    }

    /**
     * Check the result or throw an exception if the result is an error.
     *
     * @param result the result returned by a raw syscall
     * @param name   the name of the syscall
     * @throws ErrnoException if the result is an error
     */
    public static void checkResultOrThrow(long result, @NonNull String name) throws ErrnoException {
        if (isError(result)) {
            throw new ErrnoException(name, (int) -result);
        }
    }

    /**
     * Call the specified syscall with the specified arguments without checking the result.
     *
     * @param number the syscall number
     * @param args   the arguments of the syscall, support up to 6 arguments
     * @return the result of the syscall, error can be checked by {@link #isError(long)}
     */
    public static long syscallNoCheck(long number, long... args) {
        if (args.length > 6) {
            throw new IllegalArgumentException("Too many arguments: " + args.length);
        }
        long[] args6 = new long[6];
        System.arraycopy(args, 0, args6, 0, args.length);
        return NativeBridge.nativeSyscall((int) number, args6[0], args6[1], args6[2], args6[3], args6[4], args6[5]);
    }

    /**
     * Call the specified syscall with the specified arguments and check the result.
     *
     * @param number the syscall number
     * @param args   the arguments of the syscall, support up to 6 arguments
     * @return the result of the syscall
     * @throws ErrnoException if the result is an error
     */
    public static long syscall(long number, long... args) throws ErrnoException {
        if (args.length > 6) {
            throw new IllegalArgumentException("Too many arguments: " + args.length);
        }
        long result = syscallNoCheck(number, args);
        return getResultOrThrow(result, "syscall-" + number);
    }

    /**
     * Call the specified syscall with the specified arguments and retry if the result is EINTR.
     *
     * @param number the syscall number
     * @param args   the arguments of the syscall, support up to 6 arguments
     * @return the result of the syscall
     * @throws ErrnoException if the result is an error
     */
    public static long syscallTempFailureRetry(long number, long... args) throws ErrnoException {
        if (args.length > 6) {
            throw new IllegalArgumentException("Too many arguments: " + args.length);
        }
        long result;
        do {
            result = syscallNoCheck(number, args);
        } while (result == -OsConstants.EINTR);
        return getResultOrThrow(result, "syscall-" + number);
    }

    /**
     * The wrapper of the syscall `mprotect`.
     *
     * @param address the start address of the memory range
     * @param size    the size of the memory range
     * @param prot    the new protection of the memory range
     * @throws ErrnoException if the syscall fails
     */
    public static void mprotect(long address, long size, int prot) throws ErrnoException {
        ISyscallNumberTable table = CommonSyscallNumberTables.get();
        long result = NativeBridge.nativeSyscall(table.__NR_mprotect(), address, size, prot, 0, 0, 0);
        checkResultOrThrow(result, "mprotect");
    }

}
