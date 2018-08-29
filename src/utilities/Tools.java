package utilities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class Tools {

	public static boolean isFieldInsn(String insn) {
		return insn.contains("putfield") || insn.contains("getfield")
				|| insn.contains("getstatic") || insn.contains("putstatic");
	}

	public static int getUse(String field, ClassNode cn) {
		int amt = 0;
		for (FieldInsnNode fin : Tools.getFinListFromClass(cn))
			if (fin.owner.equals(cn.name) && fin.name.equals(field))
				amt++;
		return amt;
	}

	public static boolean areEqual(String[] one, String[] two) {
		if (one.length == two.length)
			for (int i = 0; i < one.length; i++)
				if (!one[i].equals(two[i]))
					return false;
				else
					return false;
		return true;
	}

	public static String getFirstword(String sentence) {
		String it = "";
		boolean start = false;
		for (char c : sentence.toCharArray()) {
			if (c == ' ' && !start) {

			} else if (c != ' ') {
				start = true;
				it += c;
			}
			if (c == ' ' && start)
				break;
		}
		// Tools.log(sentence + " --> " + it);
		return it;
	}

	public static void logBytecode(ClassNode node) {
		ArrayList<String> bc1 = new ArrayList<String>();
		for (MethodNode mn : Tools.getMethodList(node)) {
			bc1.add(" --------------------------  METHOD NAME: " + mn.name
					+ "  " + mn.desc + "  " + mn.access + "  "
					+ "  ------------------------------------- ");
			for (String s : ASMPrinter.getPrints(mn))
				if (!(s.contains("L") && s.length() <= 4))
					bc1.add(s);
		}
		for (String s : bc1)
			Tools.log(s.replace("  ", ""));
		System.exit(0);
	}

	public static String[] getByteCode(ClassNode node) {
		ArrayList<String> lines = new ArrayList<String>();
		for (MethodNode mn : Tools.getMethodList(node))
			for (String s : ASMPrinter.getPrints(mn))
				if (!(s.contains("L") && s.length() <= 4))
					lines.add(s);
		return lines.toArray(new String[] {});
	}

	public static int compareByteCode(String[] one, ClassNode two) {
		int dif = 0;
		ArrayList<String> bc1 = new ArrayList<String>();
		ArrayList<String> bc2 = new ArrayList<String>();
		for (String s : one)
			bc1.add(s);
		for (MethodNode mn : Tools.getMethodList(two))
			for (String s : ASMPrinter.getPrints(mn))
				bc2.add(s);
		for (String current : bc1) {
			int count = 0;
			int count2 = 0;
			for (String s : bc1)
				if (s.equals(current))
					count++;
			for (String s : bc2)
				if (s.equals(current))
					count2++;
			dif += Math.abs(count - count2);
		}

		for (int i = 0; i < bc1.size(); i++) {
			if (i + 5 < bc1.size()) {
				String[] pattern = new String[] { bc1.get(i), bc1.get(i + 1),
						bc1.get(i + 2), bc1.get(i + 3), bc1.get(i + 4),
						bc1.get(i + 5) };
				int count1 = countPattern(one, pattern);
				int count2 = 0;
				for (MethodNode mn : Tools.getMethodList(two))
					if (Tools.methodContainsPartial(pattern, mn))
						count2++;
				dif += Math.abs(count1 - count2);
			}
		}

		return dif;
	}

	public static int countPattern(String[] array, String[] pattern) {
		int count = 0;
		for (int i = 0; i < array.length; i++) {
			int off = 0;
			for (int j = 0; j < pattern.length; j++) {
				if (i + j < array.length) {
					if (!pattern[j].contains(array[i + j]))
						off++;
				}
			}
			if (off == 0)
				count++;
		}
		return count;
	}

	public static void printClass(ClassNode cn) {
		for (MethodNode mn : Tools.getMethodList(cn))
			ASMPrinter.consolePrint(mn);
	}

	/**
	 * Uses String.equals
	 * 
	 * @param seq
	 *            Sequence of Strings
	 * @param mn
	 *            MethodNode
	 * @return returns boolean whether the method contains the sequence
	 */
	public static boolean methodContainsExact(String[] seq, MethodNode mn) {
		String[] prints = new ASMPrinter(mn).getPrints();
		for (int i = 0; i < prints.length; i++) {
			if (prints.length > i + seq.length) {
				int count = 0;
				for (int j = 0; j < seq.length; j++) {
					if (!prints[i + j].equals(seq[j]))
						count++;
				}
				if (count == 0)
					return true;
			}
		}
		return false;
	}

	/**
	 * Uses String.contains rather than String.equals
	 * 
	 * @param seq
	 *            Sequence of Strings
	 * @param mn
	 *            MethodNode
	 * @return returns boolean whether the method contains the sequence
	 */
	public static boolean methodContainsPartial(String[] seq, MethodNode mn) {
		ArrayList<String> bc = new ArrayList<String>();
		for (String s : ASMPrinter.getPrints(mn))
			if (!(s.contains("L") && s.length() <= 4))
				bc.add(s);
		String[] prints = bc.toArray(new String[] {});
		for (int i = 0; i < prints.length; i++) {
			if (prints.length > i + seq.length) {
				int count = 0;
				for (int j = 0; j < seq.length; j++) {
					if (!prints[i + j].contains(seq[j]))
						count++;
				}
				if (count == 0)
					return true;
			}
		}
		return false;
	}

	public static String opcodeToString(int op) {
		return OpcodeInfo.OPCODES.get(op);
	}

	public static void sleep(int amt) {
		try {
			Thread.sleep(amt);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sleep(int one, int two) {
		Tools.sleep(Tools.random(one, two));
	}

	public static int random(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt(max - min + 1) + min;
		return randomNum;
	}

	public static ArrayList<Integer> toArrayList(int[] array) {
		ArrayList<Integer> ints = new ArrayList<Integer>();
		for (int i : array)
			ints.add(i);
		return ints;
	}

	public static Image getImage(String loc) {
		File f = new File(loc);
		if (f.exists()) {
			return Toolkit.getDefaultToolkit().getImage(loc);
		} else {
			log("File Doesnt Exist: " + loc);
			return null;
		}
	}

	public static void log(Object o) {
		System.out.println(o.toString());
	}

	public static void drawSharpText(String text, int x, int y, Color c,
			Color shade, Graphics g) {
		g.setColor(shade);
		g.drawString(text, x + 1, y + 1);
		g.setColor(c);
		g.drawString(text, x, y);
	}

	public static boolean acceptableChar(int key) {
		int[] accept = { 81, 87, 69, 82, 84, 89, 85, 73, 79, 80, 65, 83, 68,
				70, 71, 72, 74, 75, 76, 90, 88, 67, 86, 66, 78, 77, 49, 50, 51,
				52, 53, 54, 55, 56, 57, 48, 32 };
		for (int i : accept)
			if (i == key)
				return true;
		return false;
	}

	public static void writeFile(String[] lines, File f) {
		try {
			FileWriter outFile = new FileWriter(f);
			PrintWriter out = new PrintWriter(outFile);
			for (String s : lines) {
				out.println(s);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] getTextFrom(File f) {
		ArrayList<String> lines = new ArrayList<String>();
		try {
			if (f.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(
						f.getPath()));
				String line = br.readLine();
				while (line != null) {
					line = br.readLine();
					lines.add(line);
				}
				return lines.toArray(new String[] {});
			} else {
				Tools.log("File Doesn't Exist: " + f.getPath());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getUrlSource(String url) throws IOException {
		URL page = new URL(url);
		// URLConnection yc = page.openConnection();
		// yc.setRequestProperty("Cookie", "foo=bar");
		HttpURLConnection httpcon = (HttpURLConnection) page.openConnection();
		httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				httpcon.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuilder a = new StringBuilder();
		while ((inputLine = in.readLine()) != null)
			a.append(inputLine);
		in.close();
		return a.toString();
	}

	public static ArrayList<FieldNode> getFieldList(ClassNode cn) {
		ArrayList<FieldNode> nodes = new ArrayList<FieldNode>();
		Iterator<FieldNode> fIt = cn.fields.iterator();
		while (fIt.hasNext())
			nodes.add(fIt.next());
		return nodes;
	}

	public static ArrayList<MethodNode> getMethodList(ClassNode cn) {
		ArrayList<MethodNode> nodes = new ArrayList<MethodNode>();
		Iterator<MethodNode> fIt = cn.methods.iterator();
		while (fIt.hasNext())
			nodes.add(fIt.next());
		return nodes;
	}

	public static ArrayList<AbstractInsnNode> getAinList(MethodNode mn) {
		ArrayList<AbstractInsnNode> list = new ArrayList<AbstractInsnNode>();
		Iterator<AbstractInsnNode> inIt = mn.instructions.iterator();
		while (inIt.hasNext())
			list.add(inIt.next());
		return list;
	}

	public static ArrayList<FieldInsnNode> getFinList(MethodNode mn) {
		ArrayList<FieldInsnNode> list = new ArrayList<FieldInsnNode>();
		for (AbstractInsnNode ain : Tools.getAinList(mn)) {
			if (ain instanceof FieldInsnNode) {
				list.add(((FieldInsnNode) ain));
			}
		}
		return list;
	}

	public static ArrayList<FieldInsnNode> getFinListFromClass(ClassNode cn) {
		ArrayList<FieldInsnNode> fins = new ArrayList<FieldInsnNode>();
		for (MethodNode mn : Tools.getMethodList(cn)) {
			for (FieldInsnNode fin : Tools.getFinList(mn))
				fins.add(fin);
		}
		return fins;
	}

	public static int getClassImplements(String s, ClassNode[] pool) {
		int count = 0;
		for (ClassNode cn : pool)
			if (cn.superName.equals(s))
				count++;
		return count;
	}

	public static void stopProgram() {
		Tools.log("");
		Tools.log("Program Executed");
		System.exit(0);
	}

}
