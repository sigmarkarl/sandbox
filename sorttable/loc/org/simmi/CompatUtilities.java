package org.simmi;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Window;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class CompatUtilities {
	static String lof = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public static SortOrder UNSORTED = SortOrder.UNSORTED;
	
	public static void updateLof() {
		try {
			UIManager.setLookAndFeel(lof);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	public static Window getWindowAncestor( Component comp ) {
		return SwingUtilities.getWindowAncestor( comp );
	}

	public static String getCharsetString(byte[] buffer, int i, int read, Charset cs) {
		return new String(buffer, i, read, cs);
	}
	
	public static void browse( URL url ) throws IOException, URISyntaxException {
		browse( url.toURI() );
	}
	
	public static void browse( URI uri ) throws IOException {
		Desktop.getDesktop().browse( uri );
	}
	
	public static String getDateString( Calendar cal, boolean val ) {
		if( val ) return cal.get(Calendar.DAY_OF_MONTH) + ". "+ cal.getDisplayName( Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
		else return cal.getDisplayName( Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
	}

	public static String[] copyOfRange(String[] dayfood, int i, int min) {
		return Arrays.copyOfRange(dayfood, i, min);
	}
}
