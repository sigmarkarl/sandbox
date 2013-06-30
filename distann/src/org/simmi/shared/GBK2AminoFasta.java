package org.simmi.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

public class GBK2AminoFasta {
	public static class Anno {
		String 	name;
		String	id;
		String	spec;
		int		start;
		int		stop;
		boolean comp;
	};
	
	static Map<String,String>	amimap = new HashMap<String,String>();
	static Map<String,String>	revcom = new HashMap<String,String>();
	static Map<Character,Character>	rc = new HashMap<Character,Character>();
	static {
		amimap.put("TTT","F");
		amimap.put("TTC","F");
		amimap.put("TTA","L");
		amimap.put("TTG","L");
		amimap.put("TCT","S");
		amimap.put("TCC","S");
		amimap.put("TCA","S");
		amimap.put("TCG","S");
		amimap.put("TAT","Y");
		amimap.put("TAC","Y");
		amimap.put("TAA","1");
		amimap.put("TAG","0");
		amimap.put("TGT","C");
		amimap.put("TGC","C");
		amimap.put("TGA","0");
		amimap.put("TGG","W");
		amimap.put("CTT","L");
		amimap.put("CTC","L");
		amimap.put("CTA","L");
		amimap.put("CTG","L");
		amimap.put("CCT","P");
		amimap.put("CCC","P");
		amimap.put("CCA","P");
		amimap.put("CCG","P");
		amimap.put("CAT","H");
		amimap.put("CAC","H");
		amimap.put("CAA","Q");
		amimap.put("CAG","Q");
		amimap.put("CGT","R");
		amimap.put("CGC","R");
		amimap.put("CGA","R");
		amimap.put("CGG","R");
		amimap.put("ATT","I");
		amimap.put("ATC","I");
		amimap.put("ATA","I");
		amimap.put("ATG","M");
		amimap.put("ACT","T");
		amimap.put("ACC","T");
		amimap.put("ACA","T");
		amimap.put("ACG","T");
		amimap.put("AAT","N");
		amimap.put("AAC","N");
		amimap.put("AAA","K");
		amimap.put("AAG","K");
		amimap.put("AGT","S");
		amimap.put("AGC","S");
		amimap.put("AGA","R");
		amimap.put("AGG","R");
		amimap.put("GTT","V");
		amimap.put("GTC","V");
		amimap.put("GTA","V");
		amimap.put("GTG","V");
		amimap.put("GCT","A");
		amimap.put("GCC","A");
		amimap.put("GCA","A");
		amimap.put("GCG","A");
		amimap.put("GAT","D");
		amimap.put("GAC","D");
		amimap.put("GAA","E");
		amimap.put("GAG","E");
		amimap.put("GGT","G");
		amimap.put("GGC","G");
		amimap.put("GGA","G");
		amimap.put("GGG","G");
		
		revcom.put("TTT","AAA");
		revcom.put("TTC","GAA");
		revcom.put("TTA","TAA");
		revcom.put("TTG","CAA");
		revcom.put("TCT","AGA");
		revcom.put("TCC","GGA");
		revcom.put("TCA","TGA");
		revcom.put("TCG","CGA");
		revcom.put("TAT","ATA");
		revcom.put("TAC","GTA");
		revcom.put("TAA","TTA");
		revcom.put("TAG","CTA");
		revcom.put("TGT","ACA");
		revcom.put("TGC","GCA");
		revcom.put("TGA","TCA");
		revcom.put("TGG","CCA");
		revcom.put("CTT","AAG");
		revcom.put("CTC","GAG");
		revcom.put("CTA","TAG");
		revcom.put("CTG","CAG");
		revcom.put("CCT","AGG");
		revcom.put("CCC","GGG");
		revcom.put("CCA","TGG");
		revcom.put("CCG","CGG");
		revcom.put("CAT","ATG");
		revcom.put("CAC","GTG");
		revcom.put("CAA","TTG");
		revcom.put("CAG","CTG");
		revcom.put("CGT","ACG");
		revcom.put("CGC","GCG");
		revcom.put("CGA","TCG");
		revcom.put("CGG","CCG");
		revcom.put("ATT","AAT");
		revcom.put("ATC","GAT");
		revcom.put("ATA","TAT");
		revcom.put("ATG","CAT");
		revcom.put("ACT","AGT");
		revcom.put("ACC","GGT");
		revcom.put("ACA","TGT");
		revcom.put("ACG","CGT");
		revcom.put("AAT","ATT");
		revcom.put("AAC","GTT");
		revcom.put("AAA","TTT");
		revcom.put("AAG","CTT");
		revcom.put("AGT","ACT");
		revcom.put("AGC","GCT");
		revcom.put("AGA","TCT");
		revcom.put("AGG","CCT");
		revcom.put("GTT","AAC");
		revcom.put("GTC","GAC");
		revcom.put("GTA","TAC");
		revcom.put("GTG","CAC");
		revcom.put("GCT","AGC");
		revcom.put("GCC","GGC");
		revcom.put("GCA","TGC");
		revcom.put("GCG","CGC");
		revcom.put("GAT","ATC");
		revcom.put("GAC","GTC");
		revcom.put("GAA","TTC");
		revcom.put("GAG","CTC");
		revcom.put("GGT","ACC");
		revcom.put("GGC","GCC");
		revcom.put("GGA","TCC");
		revcom.put("GGG","CCC");
		
		rc.put('A', 'T');
		rc.put('C', 'G');
		rc.put('G', 'C');
		rc.put('T', 'A');
	}
	
	public static StringBuilder handleText( String filename, StringBuilder filetext, boolean amino, Collection<String> annoset ) throws IOException {
		int ind = filetext.indexOf("\n");
		String line = null;
		if( ind > 0 ) line = filetext.substring(0, ind);
		
		Anno		anno = null;
		List<Anno>	annolist = new ArrayList<Anno>();
		
		StringBuilder	sb = new StringBuilder();
		while( line != null ) {
			String trimline = line.trim();
			//String[] split = trimline.split("[\t ]+");
			
			boolean banno = false;
			for( String annostr : annoset ) {
				if( trimline.startsWith( annostr+"  " ) ) {
					banno = true;
					break;
				}
			}
			if( trimline.startsWith("CDS  ") || trimline.startsWith("tRNA  ") || trimline.startsWith("rRNA  ") || trimline.startsWith("mRNA  ") || trimline.startsWith("misc_feature  ") ) {
				if( anno != null ) {
					if( anno.id == null || anno.id.length() == 0 ) anno.id = anno.comp ? "comp("+anno.start+".."+anno.stop+")" : anno.start+".."+anno.stop;
					annolist.add( anno );
				}
				anno = null;
			}
			if( banno ) { //|| trimline.startsWith("gene ") ) {
				anno = new Anno();
				anno.spec = filename;
				String[] split = trimline.split("[\t ]+");
				if( split.length > 1 ) {
					if( split[1].startsWith("compl") ) {
						int iof = split[1].indexOf(")");
						String substr = split[1].substring(11, iof);
						String[] nsplit = substr.split("\\.\\.");
						//if( !nsplit[0].startsWith("join")  ) {
						char c = nsplit[0].charAt(0);
						char c2 = nsplit[1].charAt(0);
						if( c >= '0' && c <= '9' && c2 >= '0' && c2 <= '9' ) {
							anno.start = Integer.parseInt( nsplit[0] );
							anno.stop = Integer.parseInt( nsplit[1] );
							anno.comp = true;
						} else {
							System.err.println( nsplit[0] + " n " + nsplit[1] );
							anno = null;
						}
					} else {
						String[] nsplit = split[1].split("\\.\\.");
						if( nsplit.length > 1 ) {
							char c = nsplit[0].charAt(0);
							char c2 = nsplit[1].charAt(0);
							if( c >= '0' && c <= '9' && c2 >= '0' && c2 <= '9' ) {
								anno.start = Integer.parseInt( nsplit[0] );
								anno.stop = Integer.parseInt( nsplit[1] );
								anno.comp = false;
							} else {
								System.err.println( nsplit[0] + " n " + nsplit[1] );
								anno = null;
							}
						} else {
							System.err.println("nono2");
						}
					}
				} else {
					System.err.println("nono");
				}
			} else if( trimline.startsWith("/product") ) {
				if( anno != null ) {
					anno.name = trimline.substring(10,trimline.length()-1);
					//annolist.add( anno );
					//anno = null;
				}
			} else if( trimline.startsWith("/protein_id") ) {
				if( anno != null ) {
					anno.id = trimline.substring(13,trimline.length()-1);
					//annolist.add( anno );
					//anno = null;
				}
			} else if( trimline.startsWith("ORIGIN") ) {
				break;
			}
			
			int k = filetext.indexOf("\n", ind+1);
			line = null;
			if( k > 0 ) line = filetext.substring(ind+1, k);
			ind = k;
		}
		
		StringBuilder	strbuf = new StringBuilder();
		int k = filetext.indexOf("\n", ind+1);
		line = null;
		if( k > 0 ) line = filetext.substring(ind+1, k);
		ind = k;
		while( line != null ) {
			strbuf.append( line.replaceAll("[\t 1234567890]+", "") );
			
			k = filetext.indexOf("\n", ind+1);
			line = null;
			if( k > 0 ) line = filetext.substring(ind+1, k);
			ind = k;
		}
		
		for( Anno ao : annolist ) {
			sb.append( ">"+ao.id + " " + ao.name + " [" + ao.spec + "]\n" );
			//strbuf.
			
			//System.err.println(val);
			//String	ami = "";
			
			int t = 0;
			if( amino ) {
				String 	val = strbuf.substring( Math.max(0, ao.start-1), ao.stop );
				if( ao.comp ) {
					for( int i = val.length()-3; i >= 0; i-=3 ) {
						//ami += 
						String first = val.substring(i, i+3).toUpperCase();
						String second = revcom.get( first );
						String str = amimap.get( second );
						if( str != null ) {
							if( str.equals("0") || str.equals("1") ) break;
							else sb.append( str );//+ " " + t + " " );
							if( (++t % 60) == 0 ) sb.append("\n");
						}
					}
				} else {
					for( int i = 0; i < val.length(); i+=3 ) {
						//ami += 
						String first = val.substring( i, Math.min(val.length(), i+3) ).toUpperCase();
						String str = amimap.get( first );
						if( str != null ) {
							if( str.equals("0") || str.equals("1") ) break;
							else sb.append( str );//+ " " + t + " " );
							if( (++t % 60) == 0 ) sb.append("\n");
						}
					}
				}
			} else {
				if( ao.comp ) {
					for( int i = ao.stop-1; i >= ao.start; i-- ) {
						sb.append( rc.get( Character.toUpperCase( strbuf.charAt(i) ) ) );
						if( (++t % 60) == 0 ) sb.append("\n");
					}
				} else {
					for( int i = ao.start; i < ao.stop; i+=60 ) {
						int start = i;
						int stop = Math.min( ao.stop, i+60 );
						String str = strbuf.substring( start, stop );
						sb.append( str.toUpperCase() + (str.length() == 60 ? "\n" : "") );
					}
				}
			}
			sb.append("\n");
			//if( c++ > 10 ) break;
		}
		//String encoded = encode( sb.toString() );
		//String dataurl = "data:text/plain;base64,"+encoded;
		//console( "dataurl length: "+dataurl.length() );
		//ta.setText( sb.toString() );
		//Window.Location.assign( dataurl );
		
		return sb;
	}
	
	public static void ftpExtract() {
		String basesave = "/home/sigmar/ftpncbi/";
		String ftpsite = "ftp.ncbi.nih.gov";
		FTPClient ftp = new FTPClient();
		try {
			ftp.connect( ftpsite );
			ftp.login("anonymous", "anonymous");
			
			String subdir = "/genomes/Bacteria/";
			ftp.cwd( subdir );
			FTPFile[] files = ftp.listFiles();
			for( FTPFile ftpfile : files ) {
				if( ftpfile.isDirectory() ) {
					String fname = ftpfile.getName();
					if( fname.startsWith("Thermus") || fname.startsWith("Meiothermus") || fname.startsWith("Marinithermus") || fname.startsWith("Oceanithermus") ) {
						System.err.println( "up "+fname );
						if( !ftp.isConnected() ) {
							ftp.connect("ftp.ncbi.nih.gov");
							ftp.login("anonymous", "anonymous");
						}
						ftp.cwd( subdir+fname );
						FTPFile[] newfiles = ftp.listFiles();
						int cnt = 1;
						for( FTPFile newftpfile : newfiles ) {
							String newfname = newftpfile.getName();
							System.err.println("trying " + newfname + " in " + fname);
							if( newfname.endsWith(".gbk") ) {
								//System.err.println("in " + fname);
								long size = newftpfile.getSize();
								String fwname;
								if( size > 3000000 ) fwname = fname+"_"+newfname;//+".gbk";
								else fwname = fname+"_p"+(cnt++)+"_"+newfname;//+".gbk";
								//if( size > 1500000 ) fwname = fname+".fna";
								//else fwname = fname+"_p"+(cnt++)+".fna";
								
								//FileWriter fw = new FileWriter( basesave+fwname );
								URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
								InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
								BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
								String line = br.readLine();
								while( line != null ) {
									//fw.write( line + "\n" );
									line = br.readLine();
								}
								is.close();
								//ftp.completePendingCommand();
								//fw.close();
								System.err.println("done " + fname);
							}
						}
					}
				}
			}
			
			subdir = "/genomes/Bacteria_DRAFT/";
			FileSystemManager fsManager = VFS.getManager();
			//FileObject ftpconn = fsManager.resolveFile( "ftp://anonymous:anonymous@"+ftpsite+ftppath );
			//FileObject[] files2 = ftpconn.getChildren();
			
			//for( FileObject ftpfile : files2 ) {
			//	String fname = ftpfile.getName().getBaseName();
			byte[] bb = new byte[30000000];
			ftp.cwd( subdir );
			FTPFile[] files2 = ftp.listFiles();
			for( FTPFile ftpfile : files2 ) {
				if( ftpfile.isDirectory() ) {
					String fname = ftpfile.getName();
					if( fname.startsWith("Thermus") || fname.startsWith("Meiothermus") || fname.startsWith("Marinithermus") || fname.startsWith("Oceanithermus") ) {
						//System.err.println( "erm "+fname );
						ftp.cwd( subdir+fname );
						FTPFile[] newfiles = ftp.listFiles();
						//FileObject[] newfiles = ftpfile.getChildren();
						//amint cnt = 1;
						//for( FileObject newftpfile : newfiles ) {
						for( FTPFile newftpfile : newfiles ) {
							//String newfname = newftpfile.getName().getBaseName();
							String newfname = newftpfile.getName();
							if( newfname.endsWith("scaffold.gbk.tgz") ) {
								//long size = newftpfile.getSize();
								//String fwname = "";
								//if( size > 1500000 ) fwname = fname+".fna";
								//else fwname = fname+"_p"+(cnt++)+".fna";
								
								//InputStream is = newftpfile.getContent().getInputStream();
								URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
								InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
								System.err.println( "trying "+newfname + (is == null ? "critical" : "success" ) );
								//InputStream gis = is;
								GZIPInputStream gis = new GZIPInputStream( is );
								
								String filename = basesave+fname.substring(0,fname.length()-4)+".tar";
								if( gis != null ) {
									int r = gis.read(bb);
									//appengine out: FileOutputStream fos = new FileOutputStream( filename );
									while( r > 0 ) {
										System.err.println( "reading " + r );
										//fos.write( bb, 0, r );
										
										r = gis.read(bb);
									}
									//gis.close();
									gis.close();
									//fos.close();
								}
								
								//FileSystemManager fsManager = VFS.getManager();
								FileObject jarFile = fsManager.resolveFile( "tar://"+filename );

								// List the children of the Jar file
								FileObject[] children = jarFile.getChildren();
								//System.out.println( "Children of " + jarFile.getName().getURI() );
								int contig = 1;
								for ( int i = 0; i < children.length; i++ ) {
									FileObject child = children[i];
									//appengine out: FileOutputStream fos = new FileOutputStream( basesave+fname.substring(0,fname.length())+"_contig"+(contig++)+"_"+child.getName().getBaseName() );
									FileContent fc = child.getContent();
									InputStream sis = fc.getInputStream();
									int r = sis.read( bb );
									int total = r;
									
									while( r != -1 ) {
										//fos.write( bb, 0, r );
										r = sis.read( bb );
										total += r;
									}
									//fos.close();
								}
								
								//FileInputStream	fis = new FileInputStream( filename );
								
								/*TarInputStream tais = new TarInputStream( fis );
								TarEntry te = tais.getNextEntry();
								int contig = 1;
								// (int)te.getSize() ];
								
								while( te != null ) {
									//size = te.getSize();
									//if( size > 0 ) {
										byte[] bb = new byte[ 2048 ];
										
										FileOutputStream fos = new FileOutputStream( "/home/sigmar/ftpncbi/"+fname.substring(0,fname.length())+"_contig"+(contig++)+".gbk" );
										int r = tais.read( bb );
										int total = r;
										//fis.write( bb, 0, r );
										//System.err.println( te.getName() + "  " + total + "  " + size );
										
										while( r != -1 ) {
											fos.write( bb, 0, r );
											//System.err.println( te.getName() + "  " + total + "  " + size );
											r = tais.read( bb );
											total += r;
										}
										//IOUtils.copy(tis, fis, (int)ae.getSize());
										fos.close();
									//}
									te = tais.getNextEntry();
								}
								tais.close();
								fis.close();*/
								//ftp.completePendingCommand();
							}
						}
					}
				}
			}
			
			subdir = "/genomes/Viruses/";
			ftp.cwd( subdir );
			FTPFile[] files3 = ftp.listFiles();
			for( FTPFile ftpfile : files3 ) {
				if( ftpfile.isDirectory() ) {
					String fname = ftpfile.getName();
					if( fname.startsWith("Thermus") ) {//fname.startsWith("Thermus") || fname.startsWith("Meiothermus") || fname.startsWith("Marinithermus") || fname.startsWith("Oceanithermus") ) {
						if( !ftp.isConnected() ) {
							ftp.connect("ftp.ncbi.nih.gov");
							ftp.login("anonymous", "anonymous");
						}
						ftp.cwd( subdir+fname );
						FTPFile[] newfiles = ftp.listFiles();
						int cnt = 1;
						for( FTPFile newftpfile : newfiles ) {
							String newfname = newftpfile.getName();
							System.err.println("trying " + newfname + " in " + fname);
							if( newfname.endsWith(".gbk") ) {
								System.err.println("in " + fname);
								long size = newftpfile.getSize();
								String fwname = fname+"_"+newfname;
								//if( size > 3000000 ) fwname = fname+".gbk";
								//else fwname = fname+"_p"+(cnt++)+".gbk";
								//if( size > 1500000 ) fwname = fname+".fna";
								//else fwname = fname+"_p"+(cnt++)+".fna";
								
								// appengine out: FileWriter fw = new FileWriter( basesave+fwname );
								URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
								InputStream is = url.openStream(); //ftp.retrieveFileStream( newfname );
								BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
								String line = br.readLine();
								while( line != null ) {
									//fw.write( line + "\n" );
									line = br.readLine();
								}
								is.close();
								//ftp.completePendingCommand();
								//fw.close();
								System.err.println("done " + fname);
							}
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		/*try {
			File f = new File("/home/sigmar/ami57/ami.gb");
			FileReader fr = new FileReader( f );
			BufferedReader br = new BufferedReader( fr );
			StringBuilder filetext = new StringBuilder();
			String line = br.readLine();
			while( line != null ) {
				filetext.append( line+"\n" );
				line = br.readLine();
			}
			br.close();
			fr.close();
			
			StringBuilder sb = handleText("filename.aa", filetext.toString());
			FileWriter fw = new FileWriter("/home/sigmar/filename.aa");
			fw.write( sb.toString() );
			fw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		ftpExtract();
/* catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String basesave = "/home/sigmar/ftpncbi/";
		//ftpExtract( basesave );
		
		try {
			File file = new File( basesave );
			File[] ff = file.listFiles();
			for( File f : ff ) {
				FileReader fr = new FileReader( f );
				BufferedReader br = new BufferedReader( fr );
				StringBuilder filetext = new StringBuilder();
				String line = br.readLine();
				while( line != null ) {
					filetext.append( line+"\n" );
					line = br.readLine();
				}
				br.close();
				fr.close();
				
				String fname = f.getName();
				String fstr = fname.substring(0, fname.length()-4);
				System.err.println( "about to "+fstr );
				
				boolean amino = false;
				String[] annoarray = {"tRNA", "rRNA"};//{"CDS", "tRNA", "rRNA", "mRNA"};
				StringBuilder sb = handleText( fstr, filetext, amino, Arrays.asList( annoarray ) );
				// appengine out: FileWriter fw = new FileWriter( "/home/sigmar/ncbiaas/nn2/"+fstr+(amino ? ".aa" : ".nn") );
				//fw.write( sb.toString() );
				//fw.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
