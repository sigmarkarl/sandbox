package org.simmi.signed;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class NativeRun {
	public Container			cnt = null;
	
	public Path checkProdigalInstall( Path dir, List<Path> urls ) throws IOException, URISyntaxException {
		Path check1 = dir.resolve( "prodigal.v2_60.windows.exe" );
		Path check2 = dir.resolve( "prodigal.v2_60.linux" );
		Path check;
		if( !Files.exists(check1) && !Files.exists(check2) ) {
			check = installProdigal( dir, urls );
		} else check = Files.exists(check1) ? check1 : check2;
		
		return check;
	}
	
	public void checkInstall( File dir ) throws IOException {
		File check1 = new File( "/opt/ncbi-blast-2.2.28+/bin/blastp" );
		File check2 = new File( "c:\\\\Program files\\NCBI\\blast-2.2.28+\\bin\\blastp.exe" );
		if( !check1.exists() && !check2.exists() ) {
			File f = installBlast( dir );
		}
	}
	
	public File installBlast( final File homedir ) throws IOException {
		final URL url = new URL("ftp://ftp.ncbi.nlm.nih.gov/blast/executables/blast+/2.2.28/ncbi-blast-2.2.28+-win32.exe");
		String fileurl = url.getFile();
		String[] split = fileurl.split("/");
		
		final File f = new File( homedir, split[split.length-1] );
		final ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		final byte[] bb = new byte[100000];
		if( !f.exists() ) {
			final JDialog		dialog = new JDialog();
			final JProgressBar	pbar = new JProgressBar();
			Runnable run = new Runnable() {
				boolean interrupted = false;
				
				public void run() {
					dialog.addWindowListener( new WindowListener() {
						@Override
						public void windowOpened(WindowEvent e) {}
						
						@Override
						public void windowIconified(WindowEvent e) {}
						
						@Override
						public void windowDeiconified(WindowEvent e) {}
						
						@Override
						public void windowDeactivated(WindowEvent e) {}
						
						@Override
						public void windowClosing(WindowEvent e) {}
						
						@Override
						public void windowClosed(WindowEvent e) {
							interrupted = true;
						}
						
						@Override
						public void windowActivated(WindowEvent e) {}
					});
					dialog.setVisible( true );

					try {
						InputStream is = url.openStream();
						int r = is.read(bb);
						while( r > 0 && !interrupted ) {
							baos.write( bb, 0, r );
							r = is.read( bb );
						}
						is.close();
						//f.mkdirs();
						
						if( !interrupted ) {
							FileOutputStream fos = new FileOutputStream( f );
							fos.write( baos.toByteArray() );
							fos.close();
							baos.close();
							
							byte[] bb = new byte[100000];
							
							String path = f.getAbsolutePath();
							//String[] cmds = new String[] { "wine", path };
							ProcessBuilder pb = new ProcessBuilder( path );
							pb.directory( homedir );
							Process p = pb.start();
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							is = p.getInputStream();
							r = is.read(bb);
							while( r > 0 ) {
								baos.write( bb, 0, r );
								r = is.read( bb );
							}
							is.close();
							
							is = p.getErrorStream();
							r = is.read(bb);
							while( r > 0 ) {
								baos.write( bb, 0, r );
								r = is.read( bb );
							}
							is.close();
							
							System.out.println( "erm " + baos.toString() );
							baos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			runProcess( "Downloading blast...", run, dialog, pbar );
		}
		
		return f;
	}
	
	public String runProcessBuilder( String title, @SuppressWarnings("rawtypes") final List commandsList, final Runnable run, final Object[] cont ) throws IOException {
		//System.err.println( pb.toString() );
		//pb.directory( dir );
		
		final JDialog	dialog = new JDialog();
		dialog.setTitle( title );
		dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		dialog.setSize(400, 300);
		
		JComponent comp = new JComponent() {};
		comp.setLayout( new BorderLayout() );
		
		final JTextArea		ta = new JTextArea();
		for( Object commands : commandsList ) {
			if( commands instanceof List ) {
				for( Object cmd : (List)commands ) {
					ta.append(cmd+" ");
				}
				ta.append("\n");
			} else {
				ta.append(commands+" ");
			}
		}
		ta.append("\n");
	
		ta.setEditable( false );
		final JScrollPane	sp = new JScrollPane( ta );
		final JProgressBar	pbar = new JProgressBar();
		
		dialog.add( comp );
		comp.add( pbar, BorderLayout.NORTH );
		comp.add( sp, BorderLayout.CENTER );
		pbar.setIndeterminate( true );
		
		System.err.println( "about to run" );
		for( Object commands : commandsList ) {
			if( commands instanceof List ) {
				for( Object c : (List)commands ) {
					System.err.print( c+" " );
				}
				System.err.println();
			} else {
				System.err.print( commands+" " );
			}
		}
		System.err.println();
		
		Runnable runnable = new Runnable() {
			boolean interupted = false;
			
			@Override
			public void run() {
				try {
					Path input = null;
					Path output = null;
					Path workingdir = null;
					for( Object commands : commandsList ) {
						if( commands instanceof Path[] ) {
							Path[] pp = (Path[])commands;
							input = pp[0];
							output = pp[1];
							workingdir = pp[2];
						} else {
							boolean blist = commands instanceof List;
							List<String> lcmd = blist ? (List)commands : commandsList;
							for( String s : lcmd ) {
								System.err.println( s );
							}
							ProcessBuilder pb = new ProcessBuilder( lcmd );
							//pb.environment().putAll( System.getenv() );
							System.err.println( pb.environment() );
							if( workingdir != null ) {
								//System.err.println( "blblblbl " + workingdir.toFile() );
								pb.directory( workingdir.toFile() );
							}
							pb.redirectErrorStream( true );
							final Process p = pb.start();
							dialog.addWindowListener( new WindowListener() {
								
								@Override
								public void windowOpened(WindowEvent e) {}
								
								@Override
								public void windowIconified(WindowEvent e) {}
								
								@Override
								public void windowDeiconified(WindowEvent e) {}
								
								@Override
								public void windowDeactivated(WindowEvent e) {}
								
								@Override
								public void windowClosing(WindowEvent e) {}
								
								@Override
								public void windowClosed(WindowEvent e) {
									if( p != null ) {
										interupted = true;
										p.destroy();
										//tt.interrupt();
									}
								}
								
								@Override
								public void windowActivated(WindowEvent e) {}
							});
							dialog.setVisible( true );
							
							if( input != null ) {
								final Path inp = input;
								new Thread() {
									public void run() {
										try {
											OutputStream os = p.getOutputStream();
											//os.write( "simmi".getBytes() );
											Files.copy(inp, os);
											os.close();
										} catch( Exception e ) {
											e.printStackTrace();
										}
									}
								}.start();
							}
							
							if( output != null ) {
								try {
									InputStream is = p.getInputStream();
									Files.copy(is, output, StandardCopyOption.REPLACE_EXISTING);
									is.close();
								} catch( Exception e ) {
									e.printStackTrace();
								}
							} else {
								try {
									InputStream is = p.getInputStream();
									BufferedReader br = new BufferedReader( new InputStreamReader(is) );
									String line = br.readLine();
									while( line != null ) {
										String str = line + "\n";
										ta.append( str );
										
										line = br.readLine();
									}
									br.close();
									is.close();
								} catch( Exception e ) {
									e.printStackTrace();
								}
							}
							
							input = null;
							output = null;
							workingdir = null;
							
							if( !blist ) break;
						}
					}
					
					/*System.err.println("hereok");
					
					is = p.getErrorStream();
					br = new BufferedReader( new InputStreamReader(is) );
					
					line = br.readLine();
					while( line != null ) {
						String str = line + "\n";
						ta.append( str );
						
						System.err.println("hereerm " + line);
						
						line = br.readLine();
					}
					br.close();
					is.close();
					
					System.err.println("here");*/
					
					String result = ta.getText().trim();
					if( run != null ) {
						cont[0] = interupted ? null : ""; 
						cont[1] = result;
						cont[2] = new Date( System.currentTimeMillis() ).toString();
						run.run();
					}
					
					pbar.setIndeterminate( false );
					pbar.setEnabled( false );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		final Thread trd = new Thread( runnable );
		trd.start();
		
		//dialog.setVisible( false );
		return "";
	}
	
	public static String fixPath( String path ) {
		String[] split = path.split("\\\\");
		String res = "";
		for( String s : split ) {
			if( s == split[0] ) res += s;
			else if( s.contains(" ") ) {
				s = s.replace(" ", "");
				if( s.length() > 8 ) {
					res += "\\"+s.substring(0, 6)+"~1";
				} else {
					res += "\\"+s;
				}
			} else {
				res += "\\"+s;
			}
		}
		
		return res;
	}
	
	public Path installProdigal( final Path homedir, final List<Path> urls ) throws IOException, URISyntaxException {
		final Path url = Paths.get( new URL("http://prodigal.googlecode.com/files/prodigal.v2_60.windows.exe").toURI() );
		String fileurl = url.getFileName().toString();
		String[] split = fileurl.split("/");
		
		final Path f = homedir.resolve( split[split.length-1] );
		if( !Files.exists(f) ) {
			final JDialog dialog;
			Window window = SwingUtilities.windowForComponent(cnt);
			if( window != null ) dialog = new JDialog( window );
			else dialog = new JDialog();
			final JProgressBar	pbar = new JProgressBar();
			
			Runnable run = new Runnable() {
				boolean interrupted = false;
				
				public void run() {
					dialog.addWindowListener( new WindowListener() {
						@Override
						public void windowOpened(WindowEvent e) {}
						
						@Override
						public void windowIconified(WindowEvent e) {}
						
						@Override
						public void windowDeiconified(WindowEvent e) {}
						
						@Override
						public void windowDeactivated(WindowEvent e) {}
						
						@Override
						public void windowClosing(WindowEvent e) {}
						
						@Override
						public void windowClosed(WindowEvent e) {
							interrupted = true;
						}
						
						@Override
						public void windowActivated(WindowEvent e) {}
					});
					dialog.setVisible( true );
					
					try {
						/*byte[] bb = new byte[100000];
						ByteArrayOutputStream	baos = new ByteArrayOutputStream();
						InputStream is = url.openStream();
						int r = is.read(bb);
						while( r > 0 ) {
							baos.write( bb, 0, r );
							r = is.read( bb );
						}
						is.close();
						//f.mkdirs();
						FileOutputStream fos = new FileOutputStream( f );
						fos.write( baos.toByteArray() );
						fos.close();
						baos.close();*/
						
						Files.copy( url, f );
						
						// ermermerm doProdigal(homedir, NativeRun.this.cnt, f, urls);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			};
			runProcess( "Downloading Prodigal ...", run, dialog, pbar );
			
			/*JProgressBar pb = new JProgressBar();
			pb.setIndeterminate( true );
			
			dialog.setTitle("Downloading Prodigal ...");
			dialog.add( pb );
			dialog.setVisible( true );
			
			InputStream is = url.openStream();
			int r = is.read(bb);
			while( r > 0 ) {
				baos.write( bb, 0, r );
				r = is.read( bb );
			}
			is.close();
			//f.mkdirs();
			FileOutputStream fos = new FileOutputStream( f );
			fos.write( baos.toByteArray() );
			fos.close();
			baos.close();
			
			dialog.setVisible( false );*/
			
			return null;
		}
		
		return f;
	}
	
	public void runProcess( String title, Runnable run, JDialog dialog, JProgressBar pbar ) {
		dialog.setTitle( title );
		dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		dialog.setSize(400, 300);
		
		JComponent comp = new JComponent() {};
		comp.setLayout( new BorderLayout() );
		
		final JTextArea		ta = new JTextArea();
		ta.setEditable( false );
		final JScrollPane	sp = new JScrollPane( ta );
		
		dialog.add( comp );
		comp.add( pbar, BorderLayout.NORTH );
		comp.add( sp, BorderLayout.CENTER );
		pbar.setIndeterminate( true );
		
		Thread thread = new Thread( run );
		thread.start();
	}
}
