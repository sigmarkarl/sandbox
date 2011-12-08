package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

public class JoinFasta extends JApplet {
	public static void createConcatFsa( String[] names, File dir, File savefile ) throws IOException {
		FileWriter	fw = new FileWriter( savefile );
		for( String name : names ) {
			File 			f = new File( dir, name );
			BufferedReader 	br = new BufferedReader( new FileReader( f ) );
			String line = br.readLine();
			while( line != null ) {
				if( line.startsWith(">") ) fw.write( ">"+name+"_"+line.substring(1)+"\n" );
				else fw.write( line+"\n" );
				
				line = br.readLine();
			}
			br.close();
		}
		fw.close();
	}
	
	static {
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
	}
	
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
		
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select fasta files to join");
		fc.addChoosableFileFilter( new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String filename = pathname.getName();
				if( pathname.isDirectory() || filename.endsWith(".fa") || filename.endsWith(".fsa") || filename.endsWith(".fna") || filename.endsWith(".fasta") || filename.endsWith(".fas") ) return true;
				return false;
			}

			@Override
			public String getDescription() {
				return "Fasta files (*.fsa, *.fna, *.fa, *.fas, *.fasta)";
			}
		});
		fc.setMultiSelectionEnabled( true );
		if( fc.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
			File dir = fc.getCurrentDirectory();
			File[] ff = fc.getSelectedFiles();
			List<String>	lstr = new ArrayList<String>();
			for( File f : ff ) {
				lstr.add( f.getName() );
			}
			
			fc.setDialogTitle("Save joined fasta file");
			fc.setSelectedFile( new File("") );
			//fc.setSelectedFile(null);
			//fc.cancelSelection();
			fc.setMultiSelectionEnabled( false );
			if( fc.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
				try {
					createConcatFsa( lstr.toArray(new String[0]), dir, fc.getSelectedFile() );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
