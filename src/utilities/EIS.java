package utilities;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.List;
import java.util.ArrayList;


public final class EIS {

	private final AbstractInsnNode[] nodes;

	public final List<AbstractInsnNode[]> matches = new ArrayList<>();

	public int index = -1;

	public EIS(final AbstractInsnNode[] nodes) {
		this.nodes = nodes;
	}

	public EIS(final AbstractInsnNode[] nodes, final String pattern) {
		this(nodes);
		setWithPattern(pattern);
	}

	private boolean contains(final Object[] objects, final Object object) {
		for (final Object o : objects) {
			if (o.equals(object)) {
				return true;
			}
		}
		return false;
	}

	public void setWithPattern(final String pattern) {
		index = -1;
		matches.clear();
		final String[] opstrings = pattern.split(" ");
		final List<AbstractInsnNode> current = new ArrayList<>();
		final List<Boolean> cachedBools = new ArrayList<>();
		final List<Integer[]> cachedCodes = new ArrayList<>();
		for (final String opstring : opstrings) {
			cachedBools.add(opstring.contains("!"));
			cachedCodes.add(ASMUtil.getOpcodesFrom(opstring));
		}
		searcher:
		for (int i = 0; i < nodes.length; i++) {
			if (contains(cachedCodes.get(0), nodes[i].getOpcode())) {
				current.add(nodes[i]);
				for (int j = 1; j < opstrings.length; j++) {
					if (i + j >= nodes.length) {
						current.clear();
						break searcher;
					} else {
						if (opstrings[j].equals("*")) {
							current.add(nodes[i + j]);
						} else {
							final boolean ignore = cachedBools.get(j);
							final int opcode = nodes[i + j].getOpcode();
							final boolean contains = contains(cachedCodes.get(j), opcode);
							if (!ignore && contains || ignore && !contains) {
								current.add(nodes[i + j]);
							} else {
								current.clear();
								continue searcher;
							}
						}
					}
				}
				matches.add(current.toArray(new AbstractInsnNode[current.size()]));
				current.clear();
			}
		}
	}

	public AbstractInsnNode[] previous() {
		try {
			return matches.get(index - 1);
		} catch (final Exception e) {
			return null;
		}
	}

	public AbstractInsnNode[] next() {
		index++;
		try {
			return matches.get(index);
		} catch (final Exception e) {
			return null;
		}
	}

	public AbstractInsnNode[] getNodesAt(final int index) {
		try {
			return matches.get(index);
		} catch (final Exception e) {
			return null;
		}
	}

	public int found() {
		return matches.size();
	}
}