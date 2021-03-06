package core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import searching.PatternFinder;
import utilities.ASMPrinter;
import utilities.ClassNode;
import utilities.JarUtil;
import utilities.Loader;
import utilities.Tools;

/**
 *
 * @author Dan
 */
public class Viewer extends javax.swing.JFrame implements ActionListener,
		ListSelectionListener {

	JTextField SEARCHFIELD = new JTextField();
	JButton SEARCHFORFIELD = new JButton();

	public static HashMap<String, ClassNode> nodes = null;

	int rev = Integer.parseInt(JOptionPane
			.showInputDialog("Enter Current Rev: "));
	String file = new File("Loader" + rev + ".jar").getAbsolutePath();
	String currentClass = null;

	DefaultListModel insn = new DefaultListModel();
	DefaultListModel classes = new DefaultListModel();
	String last_search = "";
	int search_index = 0;

	/**
	 * Creates new form ViewerUI
	 * 
	 * @throws IOException
	 */
	public Viewer() throws IOException {
		this.download(rev, file);
		nodes = JarUtil.loadClasses(file);
		initComponents();
		this.populateClassList();
		this.setVisible(true);
		this.setTitle("Tronic ByteCode Viewer");
	}

	public void download(int rev, String file) {
		if (!new File(file).exists()) {
			Tools.log("Downloading Loader #" + rev + ".....");
			new Loader().getApplet(rev);
		}
	}

	public void populateClassList() {
		classes.clear();
		List<String> cns = new ArrayList<String>();
		for (ClassNode cn : nodes.values())
			cns.add(cn.name);
		Collections.sort(cns);
		for (String s : cns)
			classes.addElement(s);
		CLASSLIST.setModel(classes);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		CLASSLIST = new javax.swing.JList();
		jScrollPane2 = new javax.swing.JScrollPane();
		INSNLIST = new javax.swing.JList();
		SEARCHITEM = new javax.swing.JTextField();
		NEXT = new javax.swing.JButton();

		CLASSLIST.addListSelectionListener(this);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jScrollPane1.setViewportView(CLASSLIST);

		jScrollPane2.setViewportView(INSNLIST);

		NEXT.setText("Next");
		NEXT.addActionListener(this);

		SEARCHFORFIELD.setText("Search for field");
		SEARCHFORFIELD.addActionListener(this);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										284,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jScrollPane2)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		SEARCHITEM,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		147,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		NEXT)
																.addGap(0,
																		586,
																		Short.MAX_VALUE)
																.addComponent(
																		SEARCHFIELD,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		147,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		SEARCHFORFIELD,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		147,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jScrollPane1)
												.addGroup(
														layout.createSequentialGroup()
																.addContainerGap()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.BASELINE)
																				.addComponent(
																						SEARCHITEM,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(
																						NEXT)
																				.addComponent(
																						SEARCHITEM)
																				.addComponent(
																						SEARCHFIELD)
																				.addComponent(
																						SEARCHFORFIELD))
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jScrollPane2,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		685,
																		Short.MAX_VALUE)))
								.addContainerGap()));

		pack();
	}// </editor-fold>

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed"
		// desc=" Look and feel setting code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase
		 * /tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(Viewer.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(Viewer.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(Viewer.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(Viewer.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Viewer().setVisible(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	// Variables declaration - do not modify
	private javax.swing.JList CLASSLIST;
	private javax.swing.JList INSNLIST;
	private javax.swing.JButton NEXT;
	private javax.swing.JTextField SEARCHITEM;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;

	// End of variables declaration

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

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Next":
			ArrayList<Integer> indexs = new ArrayList<Integer>();
			String search = SEARCHITEM.getText();
			Tools.log("Search: " + search);
			if (search.equals(last_search)) {
				search_index++;
			} else {
				search_index = 0;
			}
			last_search = search;
			for (int i = 0; i < insn.size(); i++) {
				if (insn.getElementAt(i).toString().contains(search)) {
					indexs.add(i);
				}
			}
			if (search_index < indexs.size()) {
				if (search_index > 0
						&& indexs.get(search_index) + 15 < insn.size()) {
					INSNLIST.setSelectedIndex(indexs.get(search_index));
					INSNLIST.ensureIndexIsVisible(indexs.get(search_index) + 15);
					this.setTitle("Viewing Seach Item " + search_index + "/"
							+ indexs.size());
				} else {
					INSNLIST.setSelectedIndex(indexs.get(search_index));
					INSNLIST.ensureIndexIsVisible(indexs.get(search_index));
					this.setTitle("Viewing Seach Item " + search_index + "/"
							+ indexs.size());
				}
			} else {
				if (indexs.size() > 0) {
					search_index = 0;
					INSNLIST.setSelectedIndex(indexs.get(search_index));
					INSNLIST.ensureIndexIsVisible(indexs.get(search_index));
					this.setTitle("Viewing Seach Item " + search_index + "/"
							+ indexs.size());
				}
			}
			break;
		case "Search for field":
			String cn = SEARCHFIELD.getText().split(" ")[0];
			String fn = SEARCHFIELD.getText().split(" ")[1];
			if (cn != null && fn != null && insn.size() > 0
					&& currentClass != null) {
				Tools.log("Searching for " + cn + "." + fn);
				ArrayList<ArrayList<String>> allPatterns = new ArrayList<ArrayList<String>>();
				for (MethodNode mn : Tools.getMethodList(nodes
						.get(currentClass))) {
					ArrayList<ArrayList<String>> patterns = PatternFinder
							.getPatternsFor(cn, fn, mn);
					for (ArrayList<String> s : patterns)
						if (s != null && s.size() > 0)
							allPatterns.add(s);
				}
				new PatternsUI(allPatterns);
			}
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (CLASSLIST.getSelectedValue() != null) {
			String clazz = classes.getElementAt(CLASSLIST.getSelectedIndex())
					.toString();
			Tools.log("Selected Class: " + clazz);
			ClassNode cn = nodes.get(clazz);
			currentClass = cn.name;
			if (cn != null) {
				this.setTitle("Tronic ByteCode Viewer  -   Viewing Class: "
						+ cn.name);
				insn.clear();
				insn.addElement("Class: " + cn.name + "    Super: "
						+ cn.superName + "    Sig: " + cn.signature
						+ "    Access: " + cn.access);
				insn.addElement(" ----------------- FIELDS: ");
				for (FieldNode fn : Tools.getFieldList(cn))
					insn.addElement(fn.name
							+ "   Desc: "
							+ fn.desc
							+ "    Access: "
							+ fn.access
							+ "   Static: "
							+ Modifier.isStatic(fn.access)
							+ "   Use: "
							+ Tools.getUse(fn.name, cn));
				for (MethodNode mn : Tools.getMethodList(cn)) {
					insn.addElement(" -------------------------  ( Method: "
							+ mn.name + "   Desc: " + mn.desc + "    Access: "
							+ mn.access + "    Static: "
							+ Modifier.isStatic(mn.access) + " )   ");
					for (String s : this.getByteCode(mn)) {
						if (s.contains("bipush") || s.contains("sipush")) {
							insn.addElement(s
									+ "                   ################################### "
									+ s
									+ " ##############################################");
						} else {
							insn.addElement(s);
						}
					}
				}
				INSNLIST.setModel(insn);
			}
		}

	}
}
