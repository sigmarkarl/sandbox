package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CazyParse {
	public static void cazyParse( File f, Map<String,Set<String>> cazymap, Set<String> allcazy, Map<String,Map<String,Integer>> specmap ) throws IOException {
		String spec = null;
		FileReader fr = new FileReader( f );
		BufferedReader br = new BufferedReader( fr );
		String line = br.readLine();
		while( line != null ) {
			if( line.startsWith("Query=") || line.startsWith("Query:") ) {
				String next = br.readLine();
				while( next != null && !next.startsWith("Length") && !next.startsWith("Description:") ) {
					line += next;
					next = br.readLine();
				}
				
				int i = line.lastIndexOf('[');
				if( i != -1 ) {
					String cont = line.substring( i+1, line.indexOf(']',i+1) );
					if( cont.contains("=") ) {
						spec = "YL1";
					} else {
						spec = cont.substring(0, cont.indexOf('_', cont.indexOf('_')+1));
					}
				} else spec = f.getName();
			} else if( line.startsWith(">") ) {
				int i = line.indexOf('|');
				if( i != -1 ) {
					String id = line.substring( i+1, line.indexOf('|',i+1) );
					for( String key : cazymap.keySet() ) {
						Set<String> set = cazymap.get(key);
						if( set.contains(id) ) {
							allcazy.add( key );
							
							Map<String,Integer>	ctmap;
							if( specmap.containsKey( spec ) ) {
								ctmap = specmap.get( spec );
							} else {
								ctmap = new HashMap<String,Integer>();
								specmap.put(spec, ctmap);
							}
							
							if( ctmap.containsKey(key) ) {
								ctmap.put(key, ctmap.get(key)+1 );
							} else ctmap.put(key, 1);
							
							//break;
						}
					}
				} else {
					String id = line.substring( 2 ).trim();
					if( cazymap.size() == 0 ) {
						int idx = id.indexOf('.');
						if( idx == -1 ) idx = id.length();
						id = id.substring(0,idx);
						allcazy.add( id );
						
						Map<String,Integer>	ctmap;
						if( specmap.containsKey( spec ) ) {
							ctmap = specmap.get( spec );
						} else {
							ctmap = new HashMap<String,Integer>();
							specmap.put(spec, ctmap);
						}
						
						if( ctmap.containsKey(id) ) {
							ctmap.put(id, ctmap.get(id)+1 );
						} else ctmap.put(id, 1);
					} else {
						for( String key : cazymap.keySet() ) {
							Set<String> set = cazymap.get(key);
							if( set.contains(id) ) {
								allcazy.add( key );
								
								Map<String,Integer>	ctmap;
								if( specmap.containsKey( spec ) ) {
									ctmap = specmap.get( spec );
								} else {
									ctmap = new HashMap<String,Integer>();
									specmap.put(spec, ctmap);
								}
								
								if( ctmap.containsKey(key) ) {
									ctmap.put(key, ctmap.get(key)+1 );
								} else ctmap.put(key, 1);
								
								//break;
							}
						}
					}
				}
			}
			
			line = br.readLine();
		}
		br.close();
	}
	
	public static void main(String[] args) {
		File f = new File("/home/sigmar/cazy/");
		File[] ff = f.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		});
		
		Map<String,Set<String>>	cazymap = new HashMap<String,Set<String>>();
		try {
			if( ff != null ) for( File tf : ff ) {
				String fname = tf.getName();
				String id = fname.substring( 0, fname.length()-4 );
				Set<String> cazyset = new HashSet<String>();
				cazymap.put(id, cazyset);
				
				FileReader fr = new FileReader( tf );
				BufferedReader br = new BufferedReader( fr );
				String line = br.readLine();
				while( line != null ) {
					cazyset.add( line );
					
					line = br.readLine();
				}
				br.close();
			}
			
			Set<String>						allcazy = new TreeSet<String>();
			Map<String,Map<String,Integer>>	specmap = new HashMap<String,Map<String,Integer>>();	
			
			f = new File("/Users/sigmar/cazy");
			cazyParse( f, cazymap, allcazy, specmap );
			//f = new File("/home/sigmar/meta/metacazy567.blastout");
			//cazyParse( f, cazymap, allcazy, specmap );
			
			FileWriter fw = new FileWriter("/Users/sigmar/cazy.res");
			for( String cazyid : allcazy ) {
				fw.write("\t"+cazyid);
			}
			//fw.write("\n");
			for( String spc : specmap.keySet() ) {
				fw.write("\n"+spc);
				Map<String,Integer> cmap = specmap.get(spc);
				for( String cazyid : allcazy ) {
					if( cmap.containsKey(cazyid) ) {
						fw.write("\t"+cmap.get(cazyid));
					} else {
						fw.write("\t0");
					}
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		/*for( int i = 1; i <= 16; i++ ) {
			Set<String>	gids = new TreeSet<String>();
			int start = 0;
			boolean done = false;
			while( true ) {
				boolean first = true;
				try {
					String urlstr = "http://www.cazy.org/AA"+i+"_all.html";
					if( start > 0 ) {
						urlstr += "?debut_PRINC="+start+"#pagination_PRINC";
					}
					URL url = new URL( urlstr );
					InputStream is = url.openStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader( isr );
					String line = br.readLine();
					while( line != null ) {
						int k = line.indexOf("protein&val=");
						while( k != -1 ) {
							int u = line.indexOf('"', k+1);
							String gid = line.substring(k+12, u);
							if( !gids.add( gid ) && first ) {
								done = true;
								break;
							}
							first = false;
							k = line.indexOf("protein&val=", u+1);
						}
						line = br.readLine();
					}
					br.close();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if( done || first ) break;
				
				start += 1000;
			}
			
			try {
				FileWriter fw = new FileWriter("/home/sigmar/cazy/AA"+i+".txt" );
				for( String gid : gids ) fw.write( gid + "\n" );
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//break;
		}*/
	}
}
