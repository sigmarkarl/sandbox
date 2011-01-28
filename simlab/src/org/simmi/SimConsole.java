package org.simmi;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Writer;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class SimConsole extends JScrollPane {
    PipedInputStream piOut;
    PipedOutputStream poOut;
    JTextArea textArea = new JTextArea();
    int last = 0;

    boolean caret = true;
    public SimConsole( final Writer w ) throws IOException {
    	//super("Console",true,true,true,true);
    	
        // Set up System.out
        piOut = new PipedInputStream();
        poOut = new PipedOutputStream(piOut);
        System.setOut(new PrintStream(poOut, true));

        // Set up System.err
        //piErr = new PipedInputStream();
        //poErr = new PipedOutputStream(piErr);
        //System.setErr(new PrintStream(poErr, true));

        // Add a scrolling text area
        textArea.setEditable(true);
        textArea.setRows(20);
        textArea.setColumns(50);
        
        textArea.addKeyListener( new KeyAdapter() {
        	public void keyTyped( KeyEvent e ) {
        		try {
        			int current = textArea.getCaretPosition();
        			
        			if( 10 == (int)e.getKeyChar() ) {
        				String text = textArea.getText(last, current-last);
        				w.append( text );
        				//w.flush();
        				last = current;
        			}
        			
        			if( current < last ) last = current;
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
        	}
        });
        
        textArea.getDocument().addDocumentListener( new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {}
			
			@Override
			public void insertUpdate(DocumentEvent e) {}
			
			@Override
			public void changedUpdate(DocumentEvent e) {}
		});
        textArea.addCaretListener( new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {}
		});
        
        this.setViewportView( textArea );
        //getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        //pack();
        //setVisible(true);

        // Create reader threads
        new ReaderThread(piOut).start();
        //new ReaderThread(piErr).start();
    }

    class ReaderThread extends Thread {
        PipedInputStream pi;

        ReaderThread(PipedInputStream pi) {
            this.pi = pi;
        }

        public void run() {
            final byte[] buf = new byte[1024];
            try {
                while (true) {
                    final int len = pi.read(buf);
                    if (len == -1) {
                        break;
                    }
                    final String str = new String(buf, 0, len);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            textArea.append( str );

                            // Make sure the last line is always visible
                            textArea.setCaretPosition(textArea.getDocument().getLength());
                            last = textArea.getCaretPosition();

                            // Keep the text area down to a certain character size
                            /*int idealSize = 1000;
                            int maxExcess = 500;
                            int excess = textArea.getDocument().getLength() - idealSize;
                            if (excess >= maxExcess) {
                                textArea.replaceRange("", 0, excess);
                            }*/
                        }
                    });
                }
            } catch (IOException e) {}
        }
    }
    
    public static void main( String[] args ) {
    	try {
    		JFrame	frame = new JFrame();
    		
    		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.add( new SimConsole( null ) );
			
			frame.setVisible( true );
		} catch (IOException e) { 
			e.printStackTrace();
		}
    }
}
