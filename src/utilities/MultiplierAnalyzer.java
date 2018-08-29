package utilities;



import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.AALOAD;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.IMUL;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC_W;


public class MultiplierAnalyzer {

	public static ClassGen[] pool = null;
	
	public MultiplierAnalyzer(String file) {
		pool = this.loadClasses(new File(file));
	}
	
	public int getMultiplier(String clazz, String field) {
		HashMap<String, Integer> multipliers = new HashMap<String, Integer>();
		for (ClassGen cg : pool) {
		//	if (cg.getClassName().equals(clazz)) {
				ConstantPoolGen cpg = cg.getConstantPool();
				for (Method m : cg.getMethods()) {
					if (m.isAbstract() || m.isNative())
						continue;
					try {
						InstructionList il = new InstructionList(m.getCode()
								.getCode());
						InstructionHandle curr = il.getStart();
						while (!curr.equals(il.getEnd())) {
							if (curr.getInstruction() instanceof GETFIELD
									|| curr.getInstruction() instanceof GETSTATIC) {
								FieldInstruction fi = (FieldInstruction) curr
										.getInstruction();
								if (fi.getClassName(cpg).equals(clazz)
										&& fi.getFieldName(cpg).equals(field)) {
									InstructionHandle mulIns = curr;
									for (int i = 0; i < ((curr.getInstruction() instanceof GETSTATIC) ? 2
											: 5); ++i) {
										if (mulIns.getInstruction() instanceof IMUL)
											break;
										if (mulIns.getNext() == null)
											break;
										mulIns = mulIns.getNext();
									}
									if (!(mulIns.getInstruction() instanceof IMUL))
										break;
									InstructionHandle multi = mulIns;
									for (int i = 0; i < ((curr.getInstruction() instanceof GETSTATIC) ? 2
											: 5); ++i) {
										if (multi.getInstruction() instanceof LDC
												|| multi.getInstruction() instanceof LDC_W)
											break;
										if (multi.getPrev() == null)
											break;
										multi = multi.getPrev();
										if (multi.getInstruction() instanceof AALOAD) {
											multi = multi.getPrev();
											multi = multi.getPrev();
											multi = multi.getPrev();
										}
									}
									if (!(multi.getInstruction() instanceof LDC)
											&& !(multi.getInstruction() instanceof LDC_W))
										break;
									if (multi.getInstruction() instanceof LDC) {
										int temp = (Integer) ((LDC) multi
												.getInstruction())
												.getValue(cpg);
										if (temp != 0) {
											if (multipliers.containsKey(temp
													+ "")) {
												multipliers.put(
														temp + "",
														multipliers.get(temp
																+ "") + 1);
											} else {
												multipliers.put(temp + "", 1);
											}
										}
									}
								}
							}
							curr = curr.getNext();
						}
					} catch (Exception e) {
					}
				}
			//}
		}

		int max = 0;
		int multi = 1;

		for (String s : multipliers.keySet()) {
			if (multipliers.get(s) > max) {
				multi = Integer.parseInt(s);
				max = multipliers.get(s);
			}
		}
		return multi;
	}
	
	
	public ClassGen[] loadClasses(File fileJar) {
		try {
			LinkedList<ClassGen> classes = new LinkedList<ClassGen>();
			JarFile file = new JarFile(fileJar);
			Enumeration<JarEntry> entries = file.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().endsWith(".class")) {
					ClassGen cG = new ClassGen(new ClassParser(
							file.getInputStream(entry), entry.getName()
									.replaceAll(".class", "")).parse());
					classes.add(cG);
				}
			}
			return classes.toArray(new ClassGen[classes.size()]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
