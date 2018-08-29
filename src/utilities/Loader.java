package utilities;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

public class Loader implements AppletStub {

	private static final HashMap<String, String> params = new HashMap<String, String>();
	final String baseLink = "http://oldschool70.runescape.com/";
	public Applet loader;
	public ClassLoader clientClassLoader = null;
	public Applet getApplet(int rev) {
		try {
			parseParameters();
			downloadFile(getUrl(), "Loader" + rev + ".jar");
			clientClassLoader = new URLClassLoader(
					new URL[] { new File("Loader" + rev + ".jar").toURL() });

			Class<?> clientClass = clientClassLoader.loadClass("client");
			loader = (Applet) clientClass.newInstance();
			loader.setStub(this);

			JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.loader;
	}

	public void appletResize(int width, int height) {
	}

	public final URL getCodeBase() {
		try {
			return new URL(baseLink);
		} catch (Exception e) {
			return null;
		}
	}

	public final URL getDocumentBase() {
		try {
			return new URL(baseLink);
		} catch (Exception e) {
			return null;
		}
	}

	public final String getParameter(String name) {
		return params.get(name);
	}

	public final AppletContext getAppletContext() {
		return null;
	}

	public static void main(String[] args) {
		try {
			new Loader();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void parseParameters() {
		try {
			URL rsserver = new URL(baseLink);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					rsserver.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains("app") && inputLine.contains("write")) {
					addParam("<app");
					addParam("let ");
				} else if (inputLine.contains("scriptsrc")
						|| inputLine.contains("ie6")) {
				} else if (inputLine.contains("document.write")) {
					inputLine = inputLine.replaceAll("document.write", "")
							.replaceAll("<param name=\"", "")
							.replaceAll("\">'", "\"").replaceAll("'", "")
							.replaceAll("\\(", "").replaceAll("\\)", "")
							.replaceAll("\"", "").replaceAll(" ", "")
							.replaceAll(";", "").replaceAll("value", "");
					String[] splitted = inputLine.split("=");
					if (splitted.length == 1) {
						addParam(splitted[0]);
					} else if (splitted.length == 2) {
						addParam(splitted[0], splitted[1]);
					} else if (splitted.length == 3) {
						addParam(splitted[0], splitted[1] + splitted[2]);
					}
				}
			}
			in.close();
		} catch (Exception e) {
			return;
		}
	}

	void addParam(final String str1) {
		addParam(str1, "");
	}

	void addParam(final String str1, final String str2) {
		params.put(str1, str2);
	}

	String getUrl() throws Exception {
		return baseLink + params.get("archive");
	}

	void downloadFile(final String url, String name) {
		try {
			BufferedInputStream in = new java.io.BufferedInputStream(new URL(
					url).openStream());
			FileOutputStream fos = new java.io.FileOutputStream(name);
			BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
			byte[] data = new byte[1024];
			int x = 0;
			while ((x = in.read(data, 0, 1024)) >= 0) {
				bout.write(data, 0, x);
			}
			bout.close();
			in.close();
		} catch (Exception e) {
			return;
		}
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

}