package org.simmi;

import java.awt.Window;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SimCard extends JApplet {
	public void init() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		Window window = SwingUtilities.windowForComponent(this);
		if (window instanceof JFrame) {
			JFrame frame = (JFrame) window;
			if (!frame.isResizable())
				frame.setResizable(true);
		}

		/*
		 * ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		 * List<ScriptEngineFactory> scriptEngineFactories =
		 * scriptEngineManager.getEngineFactories(); for( ScriptEngineFactory
		 * scriptEngineFactory : scriptEngineFactories ) { System.err.println(
		 * scriptEngineFactory.getEngineName() ); }
		 */

		final PipedWriter pw = new PipedWriter();
		
			/*SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					
				}
			});*/
			
		try {
			SimConsole sc = new SimConsole(pw);
			SimCard.this.add( sc );
			Simlab simlab = new Simlab();
			simlab.init();
			simlab.welcome();
			simlab.jinit();
			final ScriptEngine engine = simlab.getScriptEngine();
			final PipedReader pr = new PipedReader(pw);
			new Thread() {
				public void run() {
					try {
						engine.eval(pr);
					} catch (ScriptException e) {
						e.printStackTrace();
					}
				}
			}.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
