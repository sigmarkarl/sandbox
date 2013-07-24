package org.simmi.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GBK2AminoFasta {
	public static class Anno {
		public Anno( String type ) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}
		
		String 			name;
		String			id;
		String			spec;
		String 			type;
		StringBuilder	contig;
		int				start;
		int				stop;
		boolean 		comp;
	};
	
	public static void handleText( String filename, Map<String,StringBuilder> filetextmap, Map<String,URI> annoset, Writer allout ) throws IOException {
		List<Anno>	annolist = new ArrayList<Anno>();
		for( String tag : filetextmap.keySet() ) {
			StringBuilder filetext = filetextmap.get( tag );
			int ind = filetext.indexOf("\n");
			String line = null;
			if( ind > 0 ) line = filetext.substring(0, ind);
			
			Anno		anno = null;
			
			//int k = filename.indexOf('.');
			//if( k == -1 ) k = filename.length();
			String spec = tag; //filename.substring(0, k);
			
			StringBuilder	strbuf = new StringBuilder();		
			while( line != null ) {
				String trimline = line.trim();
				//String[] split = trimline.split("[\t ]+");
				
				String banno = null;
				for( String annostr : annoset.keySet() ) {
					if( trimline.startsWith( annostr+"  " ) ) {
						banno = annostr;
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
				if( banno != null ) { //|| trimline.startsWith("gene ") ) {
					anno = new Anno( banno );
					anno.contig = strbuf;
					
					anno.spec = spec;
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
			
			int k = filetext.indexOf("\n", ind+1);
			line = null;
			if( k > 0 ) line = filetext.substring(ind+1, k);
			ind = k;
			while( line != null ) {
				strbuf.append( line.replaceAll("[\t 1234567890/]+", "") );
				
				k = filetext.indexOf("\n", ind+1);
				line = null;
				if( k > 0 ) line = filetext.substring(ind+1, k);
				ind = k;
			}
			
			allout.write( ">" + spec + "\n" );
			for( int i = 0; i < strbuf.length(); i+= 70 ) {
				allout.write( strbuf.substring(i, Math.min( strbuf.length(), i+70) ) + "\n" );
			}
		}
			
		Map<URI,Writer>	urifile = new HashMap<URI,Writer>();
		for( Anno ao : annolist ) {
			StringBuilder	strbuf = ao.contig;
			URI uri = annoset.get( ao.getType() );
			
			Writer out;
			if( !urifile.containsKey( uri ) ) {
				Writer fw = null;//new FileWriter( new File( uri ) );
				urifile.put( uri, fw );
				
				out = fw;
			} else {
				out = urifile.get( uri );
			}
			boolean amino = ao.getType().contains("CDS");
			
			String end = amino ? " # " + ao.start + " # " + ao.stop + " # " + (ao.comp ? "-1" : "1") + " #\n" : "\n";
			out.write( ">"+ao.id + " " + ao.name + " [" + ao.spec + "]" + end );
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
						String second = Sequence.revcom.get( first );
						String str = Sequence.amimap.get( second );
						if( str != null ) {
							if( str.equals("0") || str.equals("1") ) break;
							else out.write( str );//+ " " + t + " " );
							if( (++t % 60) == 0 ) out.write("\n");
						}
					}
				} else {
					for( int i = 0; i < val.length(); i+=3 ) {
						//ami += 
						String first = val.substring( i, Math.min(val.length(), i+3) ).toUpperCase();
						String str = Sequence.amimap.get( first );
						if( str != null ) {
							if( str.equals("0") || str.equals("1") ) break;
							else out.write( str );//+ " " + t + " " );
							if( (++t % 60) == 0 ) out.write("\n");
						}
					}
				}
			} else {
				if( ao.comp ) {
					for( int i = ao.stop-1; i >= ao.start; i-- ) {
						char c = strbuf.charAt(i);
						Character bigc = Sequence.rc.get( Character.toUpperCase( c ) );
						if( bigc == null ) System.err.println( "blerr " + c );
						out.write( bigc != null ? bigc : c );
						if( (++t % 60) == 0 ) out.write("\n");
					}
				} else {
					for( int i = ao.start; i < ao.stop; i+=60 ) {
						int start = i;
						int stop = Math.min( ao.stop, i+60 );
						String str = strbuf.substring( start, stop );
						out.write( str.toUpperCase() + (str.length() == 60 ? "\n" : "") );
					}
				}
			}
			out.write("\n");
			//if( c++ > 10 ) break;
		}
		
		for( URI uri : urifile.keySet() ) {
			Writer w = urifile.get( uri );
			w.close();
		}
		allout.close();
			//String encoded = encode( sb.toString() );
			//String dataurl = "data:text/plain;base64,"+encoded;
			//console( "dataurl length: "+dataurl.length() );
			//ta.setText( sb.toString() );
			//Window.Location.assign( dataurl );
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
		//ftpExtract();
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
				//Arrays.asList( annoarray )
				Map<String,URI>	map = new HashMap<String,URI>();
				map.put( "tRNA", null );
				map.put( "rRNA", null );
				
				StringWriter sb = new StringWriter();
				//handleText( fstr, filetext, map, null );
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
