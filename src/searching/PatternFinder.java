package searching;

import java.util.ArrayList;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import utilities.ASMPrinter;
import utilities.ClassNode;
import utilities.Tools;
import core.Viewer;

public class PatternFinder {

	public static ArrayList<ArrayList<String>> getPatternsFor(String className,
			String fieldName, MethodNode mn) {
		ArrayList<ArrayList<String>> patterns = new ArrayList<ArrayList<String>>();
		String[] bc = PatternFinder.getByteCode(mn);
		for (int i = 0; i < bc.length; i++) {
			String insn = bc[i];
			if (PatternFinder.isFieldInsn(insn)) {
				String[] split = insn.replace("put", "").replace("get", "")
						.replace("static", "").replace("field", "").split(":");
				String c = split[0].split(" ")[1].replace(" ", "");
				String f = split[0].split(" ")[2].replace(" ", "");
				if (c.equals(className) && f.equals(fieldName)) {
					for (int length = 5; length > 0; length--) {
						if (bc.length > i + length && i - length > 0) {
							ArrayList<String> current = new ArrayList<String>();
							for (int j = i - length; j < i + length; j++) {
								String cs = bc[j];
								if (cs.contains("bipush")
										|| cs.contains("sipush"))
									cs += "      #######################################################";
								current.add(cs);
							}
							patterns.add(current);
							break;
						} else {
							// Tools.log(bc.length + " !> " + (i + length)
							// + " || " + (i - length) + " !> 0");
						}
					}
				}
			}
		}
		return patterns;
	}

	public static boolean methodContainsPattern(String[] pattern, MethodNode mn) {
		String[] bc = getByteCode(mn);
		for (int i = 0; i < bc.length; i++) {
			if (bc.length > i + pattern.length) {
				int count = 0;
				for (int j = 0; j < pattern.length; j++)
					if (!bc[i + j].contains(pattern[j]))
						count++;
				if (count == 0)
					return true;
			}
		}
		return false;
	}

	public static String[] getByteCode(MethodNode mn) {
		String[] strings = ASMPrinter.getPrints(mn);
		ArrayList<String> list = new ArrayList<String>();
		for (String s : strings) {
			if (!(s.toCharArray()[0] == 'L') && !s.contains("goto")
					&& !s.contains("ldc") && !s.contains("imul")) {
				String current = "";
				boolean foundChar = false;
				for (char c : s.toCharArray()) {
					if (c != ' ' && !foundChar) {
						foundChar = true;
						current += c;
					} else if (foundChar) {
						current += c;
					}
				}
				list.add(current);
			}
		}
		return list.toArray(new String[] {});
	}

	public static boolean isFieldInsn(String insn) {
		return insn.contains("putfield") || insn.contains("getfield")
				|| insn.contains("getstatic") || insn.contains("putstatic");
	}

}
