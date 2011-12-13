package org.simmi.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TreeUtil {
	Node currentNode = null;
	
	public Node getNode() {
		return currentNode;
	}
	
	public class Node {
		String 		name;
		String		meta;
		private double		h;
		private double		h2;
		String		color;
		List<Node>	nodes;
		int			leaves = 0;
		
		public Node() {
			nodes = new ArrayList<Node>();
		}
		
		public double geth2() {
			return h2;
		}
		
		public double geth() {
			return h;
		}
		
		public String toString() {
			String str = "";
			if( nodes.size() > 0 ) {
				str += "(";
				int i = 0;
				for( i = 0; i < nodes.size()-1; i++ ) {
					str += nodes.get(i)+",";
				}
				str += nodes.get(i)+")";
			}
			
			if( name != null && name.length() > 0 ) str += name;
			if( meta != null && meta.length() > 0 ) str += meta;
			
			//if( h > 0.0 )
				str += ":"+h;
			//else str += ":0.0";
			
			return str;
		}
		
		public List<Node> getNodes() {
			return nodes;
		}
		
		public void setName( String newname ) {
			this.name = newname;
		}
		
		public String getName() {
			return name;
		}
		
		public String getMeta() {
			return meta;
		}
		
		public int countLeaves() {
			int total = 0;
			for( Node node : nodes ) {
				total += node.countLeaves();
			}	
			return Math.max( 1, total );
		}
		
		public int countMaxHeight() {
			int val = 0;
			for( Node node : nodes ) {
				val = Math.max( val, node.countMaxHeight() );
			}
			return val+1;
		}
	}
	
	public Node findNode( Node old, Node node ) {
		for( Node n : old.nodes ) {
			if( n == node ) return old;
			else {
				Node ret = findNode( n, node );
				if( ret != null ) return n;
			}
		}
		return null;
	}
	
	public Node findNode( Node old, double val ) {
		for( Node n : old.nodes ) {
			if( n.h2 == val ) return n;
			else {
				Node ret = findNode( n, val );
				//if( ret != null ) 
				return ret;
			}
		}
		return null;
	}
	
	public void getlevel( Map<Integer,Set<Node>> map, Node n, int l ) {
		Set<Node> set;
		if( map.containsKey( l ) ) {
			set = map.get( l );
		} else {
			set = new HashSet<Node>();
			map.put( l, set );
		}
		set.addAll( n.nodes );
		for( Node node : n.nodes ) {
			getlevel( map, node, l+1 );
		}
	}
	
	public void propnull( Node n ) {
		for( Node node : n.nodes ) {
			propnull( node );
		}
		if( n.h < 0.002 ) n.h = 0.6;
	}
	
	public void extractMetaRecursive( Node node, Map<String,Map<String,String>> mapmap ) {
		extractMeta( node, mapmap );
		for( Node subnode : node.nodes ) {
			extractMetaRecursive(subnode, mapmap);
		}
	}
	
	public TreeUtil( String str, boolean inverse, Map<String,Map<String,String>> mapmap ) {
		super();
		loc = 0;
		//System.err.println( str );
		Node resultnode = parseTreeRecursive( str, inverse );
		extractMetaRecursive( resultnode, mapmap );
		this.currentNode = resultnode;
	}
	
	public double reroot( Node oldnode, Node newnode ) {
		for( Node res : oldnode.nodes ) {
			double b;
			if( res == newnode ) b = res.h;
			else b = reroot( res, newnode );
			
			if( b != -1 ) {
				res.nodes.add( oldnode );
				double tmph = oldnode.h;
				//res.h = oldnode.h;
				oldnode.h = b;
				oldnode.nodes.remove( res );
				return tmph;
			}
		}
		
		return -1;
	}
	
	public double getminh2() {
		return minh2;
	}
	
	public double getmaxh2() {
		return maxh2;
	}
	
	public double getminh() {
		return minh;
	}
	
	public double getmaxh() {
		return maxh;
	}
	
	public double getdiff() {
		return maxh-minh;
	}
	
	public double getdiff2() {
		return maxh2-minh2;
	}
	
	public static void maintree( String[] args ) {		
		String imgType = "png";
		int x = 1024;
		int y = 1024*16;
		boolean	equalHeight = false;
		boolean inverse = false;
		boolean show = false;
		boolean help = false;
		boolean vertical = false;
		String export = null;
		String coords = null;
		String metafile = null;
		
		Map<String,Map<String,String>>	mapmap = new HashMap<String,Map<String,String>>();
		
		char[] cbuf = new char[4096];
		StringBuilder sb = new StringBuilder();
			
		String str = sb.toString().replaceAll("[\r\n]+", "");
		TreeUtil treeutil = new TreeUtil( str, inverse, null );
	}
	
	public void extractMeta( Node node, Map<String,Map<String,String>> mapmap ) {
		node.name = node.name.replaceAll("'", "");
		int ki = node.name.indexOf(',');
		if( ki != -1 ) {
			String[] metasplit = node.name.split(",");
			String meta = metasplit[ metasplit.length-1 ].trim();
			if( meta.contains(":") ) {
				String[] msplit = meta.split(":");
				node.meta = meta.contains("awai") || meta.contains("ellow") ? msplit[1] : msplit[0];
			}
			node.name = metasplit[1];
		}
		
		String mapname = node.name;
		int ik = node.name.indexOf('.');
		if( ik != -1 ) {
			mapname = node.name.substring(0, ik);
		}
		if( mapmap != null && mapmap.containsKey( mapname ) ) {
			Map<String,String>	keyval = mapmap.get( mapname );
			if( keyval.containsKey("country") ) {
				String meta = keyval.get("country");
				int i = meta.indexOf(':');
				if( i != -1 ) meta = meta.substring(0, i);
				node.meta = meta;
			}
			
			if( keyval.containsKey("full_name") ) {
				String tax = keyval.get("full_name");
				int i = tax.indexOf(':');
				if( i != -1 ) tax = tax.substring(0, i);
				node.name = tax;
			}
		}
	}
	
	double minh = Double.MAX_VALUE;
	double maxh = 0.0;
	double minh2 = Double.MAX_VALUE;
	double maxh2 = 0.0;
	int loc;
	private Node parseTreeRecursive( String str, boolean inverse ) {
		Node ret = new Node();
		Node node = null;
		while( loc < str.length()-1 && str.charAt(loc) != ')' ) {
			loc++;
			char c = str.charAt(loc);
			if( c == '(' ) {
				node = parseTreeRecursive(str, inverse);
				//if( node.nodes.size() == 1573 ) System.err.println( node );
				if( inverse ) {
					node.nodes.add( ret );
					node.leaves++;
				} else {
					ret.nodes.add( node );
					ret.leaves += node.leaves;
				}
			} else {
				node = new Node();
				int end = loc+1;
				char n = str.charAt(end);
				
				if( c == '\'' ) {
					while( end < str.length()-1 && n != '\'' ) {
						n = str.charAt(++end);
					}
				}
				while( end < str.length()-1 && n != ',' && n != ')' ) {
					n = str.charAt(++end);
				}
				
				String code = str.substring( loc, end );
				if( code.contains(":") ) {
					String[] split;
					int i = code.lastIndexOf("'");
					if( i > 0 ) {
						split = code.substring(i, code.length()).split(":");
						node.name = code.substring(0, i+1);
					} else {
						split = code.split(":");
						node.name = split[0];
					}
					//extractMeta( node, mapmap );
					
					if( split.length > 2 ) {
						String color = split[2].substring(1);
						int r = Integer.parseInt( color.substring(0, 2), 16 );
						int g = Integer.parseInt( color.substring(2, 4), 16 );
						int b = Integer.parseInt( color.substring(4, 6), 16 );
						node.color = "rgb( "+r+","+g+","+b+")"; //new Color( r,g,b );
					} else node.color = null;
					
					String dstr = split[1].trim();
					String dstr2 = "0";
					if( dstr.contains("[") ) {
						int start = split[1].indexOf('[');
						int stop = split[1].indexOf(']');
						dstr2 = dstr.substring( start+1, stop );
						dstr = dstr.substring( 0, start );
					}
					node.h = Double.parseDouble( dstr );
					node.h2 = Double.parseDouble( dstr2 );
					
					if( node.h < minh ) minh = node.h;
					if( node.h > maxh ) maxh = node.h;
					
					if( node.h2 < minh2 ) minh2 = node.h2;
					if( node.h2 > maxh2 ) maxh2 = node.h2;
				} else {
					node.name = code.replaceAll("'", "");;
				}
				loc = end;
				
				if( inverse ) {
					node.nodes.add( ret );
					node.leaves++;
				} else {
					ret.nodes.add( node );
					ret.leaves++;
				}
			}
		}
		
		Node use = inverse ? node : ret;
		List<Node> checklist = use.nodes;
		String metacheck = null;
		boolean dual = true;
		for( Node n : checklist ) {
			if( n.meta != null ) {
				if( metacheck == null ) metacheck = n.meta;
				else if( !n.meta.equals(metacheck) ) dual = false;
			}
		}
		
		if( dual ) {
			for( Node n : checklist ) {
				if( n.nodes != null && n.nodes.size() > 0 ) n.meta = null;
			}
			use.meta = metacheck;
		} else use.meta = "";
		//System.err.println("setting: "+metacheck + use.nodes);
		
		if( loc < str.length()-1 ) {
			loc++;
			int end = loc;
			char n = str.charAt(end);
			while( end < str.length()-1 && n != ',' && n != ';' && n != ')' ) {
				n = str.charAt(++end);
			}
			String code = str.substring( loc, end );
			if( code.contains(":") ) {
				String[] split = code.split(":");
				if( split.length > 0 ) {
					ret.name = split[0].replaceAll("'", "");
					if( split.length > 2 ) {
						String color = split[2].substring(1);
						int r = Integer.parseInt( color.substring(0, 2), 16 );
						int g = Integer.parseInt( color.substring(2, 4), 16 );
						int b = Integer.parseInt( color.substring(4, 6), 16 );
						ret.color = "rgb( "+r+","+g+","+b+")"; //new Color( r,g,b );
					} else ret.color = null;
					String dstr = split[1].trim();
					String dstr2 = "0";
					if( dstr.contains("[") ) {
						int start = split[1].indexOf('[');
						int stop = split[1].indexOf(']');
						dstr2 = dstr.substring( start+1, stop );
						dstr = dstr.substring( 0, start );
					}
					try {
						ret.h = Double.parseDouble( dstr );
						ret.h2 = Double.parseDouble( dstr2 );
					} catch( Exception e ) {}
					if( ret.h < minh ) minh = ret.h;
					if( ret.h > maxh ) maxh = ret.h;
					if( ret.h2 < minh2 ) minh2 = ret.h2;
					if( ret.h2 > maxh2 ) maxh2 = ret.h2;
				} else {
					System.out.println( str );
				}
			} else {
				ret.name = code.replaceAll("'", "");
			}
			loc = end;
		}
		
		/*if( use.leaves == 1573 ) {
			try {
				FileWriter fw = new FileWriter("/home/sigmar/tree"+(cnt++)+".ntree");
				fw.write( use.toString() );
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		return use;
	}
	int cnt = 0;
}
