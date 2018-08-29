package utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Prints out ASM {@link MethodNode}s using its instructions. <br>
 * 
 * @author Bibl
 * 
 */
public class ASMPrinter {

	/** The MethodNode to print **/
	protected MethodNode mNode;

	protected HashMap<LabelNode, Integer> labels;

	/**
	 * Creates a new printer
	 * 
	 * @param mNode
	 *            MethodNode to use
	 */
	public ASMPrinter(MethodNode mNode) {
		this.mNode = mNode;
		this.labels = new HashMap<LabelNode, Integer>();
	}

	/**
	 * Creates the print
	 * 
	 * @return The print as an ArrayList
	 */
	public ArrayList<String> getPrint() {
		ArrayList<String> info = new ArrayList<String>();
		ListIterator<?> it = mNode.instructions.iterator();
		while (it.hasNext()) {
			AbstractInsnNode ain = (AbstractInsnNode) it.next();
			String line = "";
			if (ain instanceof VarInsnNode) {
				line = printVarInsnNode((VarInsnNode) ain, it);
			} else if (ain instanceof IntInsnNode) {
				line = printIntInsnNode((IntInsnNode) ain, it);
			} else if (ain instanceof FieldInsnNode) {
				line = printFieldInsnNode((FieldInsnNode) ain, it);
			} else if (ain instanceof MethodInsnNode) {
				line = printMethodInsnNode((MethodInsnNode) ain, it);
			} else if (ain instanceof LdcInsnNode) {
				line = printLdcInsnNode((LdcInsnNode) ain, it);
			} else if (ain instanceof InsnNode) {
				line = printInsnNode((InsnNode) ain, it);
			} else if (ain instanceof JumpInsnNode) {
				line = printJumpInsnNode((JumpInsnNode) ain, it);
			} else if (ain instanceof LineNumberNode) {
				line = printLineNumberNode((LineNumberNode) ain, it);
			} else if (ain instanceof LabelNode) {
				line = printLabelnode((LabelNode) ain);
			} else if (ain instanceof TypeInsnNode) {
				line = printTypeInsnNode((TypeInsnNode) ain);
			} else if (ain instanceof FrameNode) {
				line = "";
			} else if (ain instanceof IincInsnNode) {
				printIincInsnNode((IincInsnNode) ain);
			} else {
				line += "UNKNOWN-NODE: " + nameOpcode(ain.getOpcode()) + " "
						+ ain.toString();
			}
			if (!line.equals(""))
				info.add(line);
		}
		return info;
	}

	protected String printVarInsnNode(VarInsnNode vin, ListIterator<?> it) {
		return nameOpcode(vin.getOpcode()) + " " + vin.var;
	}

	protected String printIntInsnNode(IntInsnNode iin, ListIterator<?> it) {
		return nameOpcode(iin.getOpcode()) + " " + iin.operand;
	}

	protected String printFieldInsnNode(FieldInsnNode fin, ListIterator<?> it) {
		return nameOpcode(fin.getOpcode()) + " " + fin.owner + " " + fin.name
				+ ":" + fin.desc;
	}

	protected String printMethodInsnNode(MethodInsnNode min, ListIterator<?> it) {
		return nameOpcode(min.getOpcode()) + " " + min.owner + " " + min.name
				+ ":" + min.desc;
	}

	protected String printLdcInsnNode(LdcInsnNode ldc, ListIterator<?> it) {
		return nameOpcode(ldc.getOpcode()) + " " + ldc.cst + " ("
				+ ldc.cst.getClass().getCanonicalName() + ")";
	}

	protected String printInsnNode(InsnNode in, ListIterator<?> it) {
		return nameOpcode(in.getOpcode());
	}

	protected String printJumpInsnNode(JumpInsnNode jin, ListIterator<?> it) {
		String line = nameOpcode(jin.getOpcode()) + " L"
				+ resolveLabel(jin.label);
		return line;
	}

	protected String printLineNumberNode(LineNumberNode lin, ListIterator<?> it) {
		return "";
	}

	protected String printLabelnode(LabelNode label) {
		return "L" + resolveLabel(label);
	}

	protected String printTypeInsnNode(TypeInsnNode tin) {
		return nameOpcode(tin.getOpcode()) + " " + tin.desc;
	}

	protected String printIincInsnNode(IincInsnNode iin) {
		return nameOpcode(iin.getOpcode()) + " " + iin.var + " " + iin.incr;
	}

	protected String nameOpcode(int opcode) {
		return "    " + OpcodeInfo.OPCODES.get(opcode).toLowerCase();
	}

	protected int resolveLabel(LabelNode label) {
		if (labels.containsKey(label)) {
			return labels.get(label);
		} else {
			int newLabelIndex = labels.size() + 1;
			labels.put(label, newLabelIndex);
			return newLabelIndex;
		}
	}

	/**
	 * Creates the print
	 * 
	 * @return The print as a string array
	 */
	public String[] getPrints() {
		ArrayList<String> lines = getPrint();
		return lines.toArray(new String[lines.size()]);
	}

	public static String[] getPrints(MethodNode m) {
		ArrayList<String> lines = new ArrayList<String>();
		for (String s : new ASMPrinter(m).getPrints())
			if (s.toCharArray().length > 0 && isDesiredInstruction(s) && s.toCharArray()[0] != 'L')
				lines.add(removeIndent(s));
		return lines.toArray(new String[] {});
	}

	public static boolean isDesiredInstruction(String s) {
		return s.contains("push") || s.contains("field") || s.contains("load") || s.contains("static");
	}
	
	public static String removeIndent(String in) {
		String out = "";
		boolean go = false;
		for (char c : in.toCharArray()) {
			if (!go) {
				if (Character.isAlphabetic(c)) {
					out += c;
					go = true;
				}
			} else {
				out += c;
			}
		}
		return out;
	}

	/**
	 * Static method to print
	 * 
	 * @param lines
	 *            To print
	 */
	public static void consolePrint(String[] lines) {
		for (String line : lines) {
			System.out.println(line);
		}
	}

	/**
	 * Prints out the MethodNode
	 * 
	 * @param m
	 *            MethodNode to print
	 */
	public static void consolePrint(MethodNode m) {
		consolePrint(new ASMPrinter(m).getPrints());
	}
}