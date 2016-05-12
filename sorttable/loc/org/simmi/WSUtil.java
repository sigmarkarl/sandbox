package org.simmi;

import java.awt.Component;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.jnlp.ClipboardService;
import javax.jnlp.ServiceManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class WSUtil {
	static SortTable	sorttable;
	static class CopyAction extends AbstractAction {
        public CopyAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }
 
        public void actionPerformed(ActionEvent e) {
            copyData((Component)e.getSource());
        }
    }

	static boolean grabFocus = false;
	public static void copyData(Component source) {
        //TableModel model = sorttable.table.getModel();
 
        String s = sorttable.makeCopyString();
        if (s==null || s.trim().length()==0) {
            JOptionPane.showMessageDialog(null, "There is no data selected!");
        } else {
            StringSelection selection = new StringSelection(s);
            ((ClipboardService)clipboardService).setContents( selection );
        }
        
        if (grabFocus) {
            source.requestFocus();
        }
	}
	
	public static Object clipboardService;
	public static void clipboard( SortTable sorttable ) {
		System.err.println( "clipity clipclip .........................................................................................................................." );
		WSUtil.sorttable = sorttable;
		try {
	    	clipboardService = ServiceManager.lookup("javax.jnlp.ClipboardService");
	    	Action action = new CopyAction( "Copy", null, "Copy data", new Integer(KeyEvent.VK_CONTROL+KeyEvent.VK_C) );
            sorttable.table.getActionMap().put( "copy", action );
            grabFocus = true;
	    } catch (Exception e) { 
	    	e.printStackTrace();
	    	System.err.println("Copy services not available.  Copy using 'Ctrl-c'.");
	    }
	}
}
