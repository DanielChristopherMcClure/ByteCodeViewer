package utilities;

import org.objectweb.asm.Opcodes;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.AbstractInsnNode;

public class EMS {

	private Multiplier multiplier = null;

	public EMS(final ClassNode cn, final FieldNode fn) {
		if (fn == null)
			System.out.println("Field Null");
		final String fieldInsn = (fn.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC ? "getstatic"
				: "getfield";
		final String[] patterns = new String[] { "ldc " + fieldInsn + " *mul",
				fieldInsn + " *mul putstatic|putfield",
				"ldc aload " + fieldInsn };
		final int[] fields = { 1, 0, 2 };
		final int[] ldcs = { 0, 1, 0 };
		final int[] muls = { 2, 1, -1 };
		searcher: for (final MethodNode mn : (MethodNode[]) cn.methods
				.toArray(new MethodNode[cn.methods.size()])) {
			final EIS eis = new EIS(mn.instructions.toArray());
			for (int i = 0; i < patterns.length; i++) {
				eis.setWithPattern(patterns[i]);
				if (eis.found() < 1) {
					continue;
				}
				AbstractInsnNode[] match;
				while ((match = eis.next()) != null) {
					if (((FieldInsnNode) match[fields[i]]).name.equals(fn.name)) {
						if (match[ldcs[i]] instanceof LdcInsnNode) {
							final Object cst = ((LdcInsnNode) match[ldcs[i]]).cst;
							if (muls[i] == -1) {
								if (cst instanceof Integer)
									multiplier = new Multiplier((Integer) cst);
							} else {
								final int opcode = match[muls[i]].getOpcode();
								if (opcode == Opcodes.IMUL) {
									multiplier = new Multiplier((Integer) cst);
								} else if (opcode == Opcodes.DMUL) {
									multiplier = new Multiplier((Double) cst);
								} else if (opcode == Opcodes.FMUL) {
									multiplier = new Multiplier((Float) cst);
								} else if (opcode == Opcodes.LMUL) {
									multiplier = new Multiplier((Long) cst);
								} else {
									multiplier = new Multiplier((Integer) cst);
								}
							}
						}
						break searcher;
					}
				}
			}
		}
	}

	public EMS(final ClassNode cn, final FieldInsnNode fin) {
		this(cn, ASMUtil.getField(cn, fin));
	}

	public Multiplier getMultiplier() {
		return multiplier != null ? multiplier : null;
	}
}
