/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.harmony.umbrella.asm;

import java.io.IOException;
import java.io.InputStream;

/**
 * A Java class parser to make a {@link ClassVisitor} visit an existing class.
 * This class parses a byte array conforming to the Java class file format and
 * calls the appropriate visit methods of a given class visitor for each field,
 * method and bytecode instruction encountered.
 * 
 * @author Eric Bruneton
 * @author Eugene Kuleshov
 */
public class ClassReader {

    /**
     * The class to be parsed. <i>The content of this array must not be
     * modified. This field is intended for {@link Attribute} sub classes, and
     * is normally not needed by class generators or adapters.</i>
     */
    public final byte[] b;

    /**
     * The start index of each constant pool item in {@link #b b}, plus one. The
     * one byte offset skips the constant pool item tag that indicates its type.
     */
    private final int[] items;

    /**
     * The String objects corresponding to the CONSTANT_Utf8 items. This cache
     * avoids multiple parsing of a given CONSTANT_Utf8 constant pool item,
     * which GREATLY improves performances (by a factor 2 to 3). This caching
     * strategy could be extended to all constant pool items, but its benefit
     * would not be so great for these items (because they are much less
     * expensive to parse than CONSTANT_Utf8 items).
     */
    private final String[] strings;

    /**
     * Maximum length of the strings contained in the constant pool of the
     * class.
     */
    private final int maxStringLength;

    /**
     * Start index of the class header information (access, name...) in
     * {@link #b b}.
     */
    public final int header;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructs a new {@link ClassReader} object.
     * 
     * @param b
     *            the bytecode of the class to be read.
     */
    public ClassReader(final byte[] b) {
        this(b, 0, b.length);
    }

    /**
     * Constructs a new {@link ClassReader} object.
     * 
     * @param is
     *            an input stream from which to read the class.
     * @throws IOException
     *             if a problem occurs during reading.
     */
    public ClassReader(final InputStream is) throws IOException {
        this(readClass(is, false));
    }
    
    protected ClassReader(final InputStream is, boolean close) throws IOException {
        this(readClass(is, close));
    }

    /**
     * Constructs a new {@link ClassReader} object.
     * 
     * @param name
     *            the binary qualified name of the class to be read.
     * @throws IOException
     *             if an exception occurs during reading.
     */
    public ClassReader(final String name) throws IOException {
        this(readClass(ClassLoader.getSystemResourceAsStream(name.replace('.', '/') + ".class"), true));
    }

    /**
     * Constructs a new {@link ClassReader} object.
     * 
     * @param b
     *            the bytecode of the class to be read.
     * @param off
     *            the start offset of the class data.
     * @param len
     *            the length of the class data.
     */
    public ClassReader(final byte[] b, final int off, final int len) {
        this.b = b;
        // checks the class version
        if (readShort(off + 6) > Opcodes.V1_8) {
            throw new IllegalArgumentException();
        }
        // parses the constant pool
        items = new int[readUnsignedShort(off + 8)];
        int n = items.length;
        strings = new String[n];
        int max = 0;
        int index = off + 10;
        for (int i = 1; i < n; ++i) {
            items[i] = index + 1;
            int size;
            switch (b[index]) {
            case ClassWriter.FIELD:
            case ClassWriter.METH:
            case ClassWriter.IMETH:
            case ClassWriter.INT:
            case ClassWriter.FLOAT:
            case ClassWriter.NAME_TYPE:
            case ClassWriter.INDY:
                size = 5;
                break;
            case ClassWriter.LONG:
            case ClassWriter.DOUBLE:
                size = 9;
                ++i;
                break;
            case ClassWriter.UTF8:
                size = 3 + readUnsignedShort(index + 1);
                if (size > max) {
                    max = size;
                }
                break;
            case ClassWriter.HANDLE:
                size = 4;
                break;
            // case ClassWriter.CLASS:
            // case ClassWriter.STR:
            // case ClassWriter.MTYPE
            default:
                size = 3;
                break;
            }
            index += size;
        }
        maxStringLength = max;
        // the class header information starts just after the constant pool
        header = index;
    }

    /**
     * Returns the class's access flags (see {@link Opcodes}). This value may
     * not reflect Deprecated and Synthetic flags when bytecode is before 1.5
     * and those flags are represented by attributes.
     * 
     * @return the class access flags
     * 
     * @see ClassVisitor#visit(int, int, String, String, String, String[])
     */
    public int getAccess() {
        return readUnsignedShort(header);
    }

    /**
     * Returns the internal name of the class (see
     * {@link Type#getInternalName() getInternalName}).
     * 
     * @return the internal class name
     * 
     * @see ClassVisitor#visit(int, int, String, String, String, String[])
     */
    public String getClassName() {
        return readClass(header + 2, new char[maxStringLength]);
    }

    /**
     * Returns the internal of name of the super class (see
     * {@link Type#getInternalName() getInternalName}). For interfaces, the
     * super class is {@link Object}.
     * 
     * @return the internal name of super class, or <tt>null</tt> for
     *         {@link Object} class.
     * 
     * @see ClassVisitor#visit(int, int, String, String, String, String[])
     */
    public String getSuperName() {
        return readClass(header + 4, new char[maxStringLength]);
    }

    /**
     * Returns the internal names of the class's interfaces (see
     * {@link Type#getInternalName() getInternalName}).
     * 
     * @return the array of internal names for all implemented interfaces or
     *         <tt>null</tt>.
     * 
     * @see ClassVisitor#visit(int, int, String, String, String, String[])
     */
    public String[] getInterfaces() {
        int index = header + 6;
        int n = readUnsignedShort(index);
        String[] interfaces = new String[n];
        if (n > 0) {
            char[] buf = new char[maxStringLength];
            for (int i = 0; i < n; ++i) {
                index += 2;
                interfaces[i] = readClass(index, buf);
            }
        }
        return interfaces;
    }

    /**
     * Reads the bytecode of a class.
     * 
     * @param is
     *            an input stream from which to read the class.
     * @param close
     *            true to close the input stream after reading.
     * @return the bytecode read from the given input stream.
     * @throws IOException
     *             if a problem occurs during reading.
     */
    private static byte[] readClass(final InputStream is, boolean close) throws IOException {
        if (is == null) {
            throw new IOException("Class not found");
        }
        try {
            byte[] b = new byte[is.available()];
            int len = 0;
            while (true) {
                int n = is.read(b, len, b.length - len);
                if (n == -1) {
                    if (len < b.length) {
                        byte[] c = new byte[len];
                        System.arraycopy(b, 0, c, 0, len);
                        b = c;
                    }
                    return b;
                }
                len += n;
                if (len == b.length) {
                    int last = is.read();
                    if (last < 0) {
                        return b;
                    }
                    byte[] c = new byte[b.length + 1000];
                    System.arraycopy(b, 0, c, 0, len);
                    c[len++] = (byte) last;
                    b = c;
                }
            }
        } finally {
            if (close) {
                is.close();
            }
        }
    }

    // ------------------------------------------------------------------------
    // Utility methods: low level parsing
    // ------------------------------------------------------------------------

    /**
     * Returns the maximum length of the strings contained in the constant pool
     * of the class.
     * 
     * @return the maximum length of the strings contained in the constant pool
     *         of the class.
     */
    public int getMaxStringLength() {
        return maxStringLength;
    }

    /**
     * Reads a byte value in {@link #b b}. <i>This method is intended for
     * {@link Attribute} sub classes, and is normally not needed by class
     * generators or adapters.</i>
     * 
     * @param index
     *            the start index of the value to be read in {@link #b b}.
     * @return the read value.
     */
    public int readByte(final int index) {
        return b[index] & 0xFF;
    }

    /**
     * Reads an unsigned short value in {@link #b b}. <i>This method is intended
     * for {@link Attribute} sub classes, and is normally not needed by class
     * generators or adapters.</i>
     * 
     * @param index
     *            the start index of the value to be read in {@link #b b}.
     * @return the read value.
     */
    public int readUnsignedShort(final int index) {
        byte[] b = this.b;
        return ((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF);
    }

    /**
     * Reads a signed short value in {@link #b b}. <i>This method is intended
     * for {@link Attribute} sub classes, and is normally not needed by class
     * generators or adapters.</i>
     * 
     * @param index
     *            the start index of the value to be read in {@link #b b}.
     * @return the read value.
     */
    public short readShort(final int index) {
        byte[] b = this.b;
        return (short) (((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF));
    }

    /**
     * Reads a signed int value in {@link #b b}. <i>This method is intended for
     * {@link Attribute} sub classes, and is normally not needed by class
     * generators or adapters.</i>
     * 
     * @param index
     *            the start index of the value to be read in {@link #b b}.
     * @return the read value.
     */
    public int readInt(final int index) {
        byte[] b = this.b;
        return ((b[index] & 0xFF) << 24) | ((b[index + 1] & 0xFF) << 16) | ((b[index + 2] & 0xFF) << 8) | (b[index + 3] & 0xFF);
    }

    /**
     * Reads a signed long value in {@link #b b}. <i>This method is intended for
     * {@link Attribute} sub classes, and is normally not needed by class
     * generators or adapters.</i>
     * 
     * @param index
     *            the start index of the value to be read in {@link #b b}.
     * @return the read value.
     */
    public long readLong(final int index) {
        long l1 = readInt(index);
        long l0 = readInt(index + 4) & 0xFFFFFFFFL;
        return (l1 << 32) | l0;
    }

    /**
     * Reads an UTF8 string constant pool item in {@link #b b}. <i>This method
     * is intended for {@link Attribute} sub classes, and is normally not needed
     * by class generators or adapters.</i>
     * 
     * @param index
     *            the start index of an unsigned short value in {@link #b b},
     *            whose value is the index of an UTF8 constant pool item.
     * @param buf
     *            buffer to be used to read the item. This buffer must be
     *            sufficiently large. It is not automatically resized.
     * @return the String corresponding to the specified UTF8 item.
     */
    public String readUTF8(int index, final char[] buf) {
        int item = readUnsignedShort(index);
        if (index == 0 || item == 0) {
            return null;
        }
        String s = strings[item];
        if (s != null) {
            return s;
        }
        index = items[item];
        return strings[item] = readUTF(index + 2, readUnsignedShort(index), buf);
    }

    /**
     * Reads UTF8 string in {@link #b b}.
     * 
     * @param index
     *            start offset of the UTF8 string to be read.
     * @param utfLen
     *            length of the UTF8 string to be read.
     * @param buf
     *            buffer to be used to read the string. This buffer must be
     *            sufficiently large. It is not automatically resized.
     * @return the String corresponding to the specified UTF8 string.
     */
    private String readUTF(int index, final int utfLen, final char[] buf) {
        int endIndex = index + utfLen;
        byte[] b = this.b;
        int strLen = 0;
        int c;
        int st = 0;
        char cc = 0;
        while (index < endIndex) {
            c = b[index++];
            switch (st) {
            case 0:
                c = c & 0xFF;
                if (c < 0x80) { // 0xxxxxxx
                    buf[strLen++] = (char) c;
                } else if (c < 0xE0 && c > 0xBF) { // 110x xxxx 10xx xxxx
                    cc = (char) (c & 0x1F);
                    st = 1;
                } else { // 1110 xxxx 10xx xxxx 10xx xxxx
                    cc = (char) (c & 0x0F);
                    st = 2;
                }
                break;

            case 1: // byte 2 of 2-byte char or byte 3 of 3-byte char
                buf[strLen++] = (char) ((cc << 6) | (c & 0x3F));
                st = 0;
                break;

            case 2: // byte 2 of 3-byte char
                cc = (char) ((cc << 6) | (c & 0x3F));
                st = 1;
                break;
            }
        }
        return new String(buf, 0, strLen);
    }

    /**
     * Reads a class constant pool item in {@link #b b}. <i>This method is
     * intended for {@link Attribute} sub classes, and is normally not needed by
     * class generators or adapters.</i>
     * 
     * @param index
     *            the start index of an unsigned short value in {@link #b b},
     *            whose value is the index of a class constant pool item.
     * @param buf
     *            buffer to be used to read the item. This buffer must be
     *            sufficiently large. It is not automatically resized.
     * @return the String corresponding to the specified class item.
     */
    public String readClass(final int index, final char[] buf) {
        // computes the start index of the CONSTANT_Class item in b
        // and reads the CONSTANT_Utf8 item designated by
        // the first two bytes of this CONSTANT_Class item
        return readUTF8(items[readUnsignedShort(index)], buf);
    }

}
