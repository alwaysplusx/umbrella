package com.harmony.umbrella.asm1;

import java.io.FileOutputStream;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.harmony.umbrella.util.IOUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ClassCreateTest {

    private static final byte[] C = new byte[] { //
            -54, -2, -70, -66, 0, 0, 0, 52, 0, 18, 7, 0, 2, 1, 0, 30, 99, 111, 109, 47, 104, 97, 114, 109, 111, 110, 121, 47, 117, 109, 98, 114, 101, 108, 108,
            97, 47, 97, 115, 109, 47, 72, 101, 108, 108, 111, 7, 0, 4, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0,
            6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 10, 0, 3, 0, 9, 12, 0, 5, 0, 6, 1, 0, 15, 76, 105, 110, 101, 78,
            117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4,
            116, 104, 105, 115, 1, 0, 32, 76, 99, 111, 109, 47, 104, 97, 114, 109, 111, 110, 121, 47, 117, 109, 98, 114, 101, 108, 108, 97, 47, 97, 115, 109,
            47, 72, 101, 108, 108, 111, 59, 1, 0, 5, 115, 97, 121, 72, 105, 1, 0, 20, 40, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114,
            105, 110, 103, 59, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 10, 72, 101, 108, 108, 111, 46, 106, 97, 118, 97, 0, 33, 0, 1, 0,
            3, 0, 0, 0, 0, 0, 2, 0, 1, 0, 5, 0, 6, 0, 1, 0, 7, 0, 0, 0, 47, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 8, -79, 0, 0, 0, 2, 0, 10, 0, 0, 0, 6, 0, 1, 0,
            0, 0, 6, 0, 11, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 12, 0, 13, 0, 0, 0, 1, 0, 14, 0, 15, 0, 1, 0, 7, 0, 0, 0, 44, 0, 1, 0, 1, 0, 0, 0, 2, 1, -80, 0,
            0, 0, 2, 0, 10, 0, 0, 0, 6, 0, 1, 0, 0, 0, 9, 0, 11, 0, 0, 0, 12, 0, 1, 0, 0, 0, 2, 0, 12, 0, 13, 0, 0, 0, 1, 0, 16, 0, 0, 0, 2, 0, 17 };

    @Test
    public void testRead() {
        new ClassReader(C).accept(new ClassVisitor(Opcodes.ASM5) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                System.out.println(name + ", " + desc);
                return super.visitMethod(access, name, desc, signature, exceptions);
            }

        }, ClassReader.SKIP_DEBUG);
    }

    public static void main(String[] args) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        System.out.println(Type.getType(String.class).getDescriptor());

        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, "Hello", null, "java/lang/Object", null);

        MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "sayHi", "()Ljava/lang/String", null, null);

        mw.visitParameter("name", Opcodes.ACC_MANDATED);
        mw.visitInsn(Opcodes.NULL);
        mw.visitInsn(Opcodes.RETURN);
        mw.visitEnd();

        IOUtils.write(cw.toByteArray(), new FileOutputStream("./target/Hello.class"));
    }

}
