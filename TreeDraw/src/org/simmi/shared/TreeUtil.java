package org.simmi.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TreeUtil {
	public Node currentNode = null;
	
	public void neighborJoin( double[] corrarr, int len, List<String> corrInd ) {
		List<Node> nodes = new ArrayList<Node>();
		for( int i = 0; i < len; i++ ) {
			nodes.add( new Node( corrInd.get(i) ) );
		}
		
		double[] dmat = new double[len*len];
		double[] u = new double[len];
		System.arraycopy(corrarr, 0, dmat, 0, len*len);
		while( len > 2 ) {
			//System.err.println( "trying " + len + " size is " + nodes.size() );
			for ( int i = 0; i < len; i++ ) {
				u[i] = 0;
				for ( int j = 0; j < len; j++ ) {
					if( i != j ) {
						u[i] += dmat[i*len+j];
					}
				}
				u[i] /= len-2;
			}
			
			int imin = 0;
			int jmin = 0;
			double dmin = Double.MAX_VALUE;
			for ( int i = 0; i < len; i++ ) {
				for ( int j = 0; j < len; j++ ) {
					if( i != j ) {
						double val = dmat[i*len+j] - u[i] - u[j];
						//if( dmat[i*len+j] < 50 ) System.err.println("euff " + val + " " + i + " " + j + "  " + dmat[i*len+j] );
						if( val < dmin ) {
							dmin = val;
							imin = i;
							jmin = j;
						}
					}
				}
			}
			
			//System.err.println( dmat[imin*len+jmin] );
			double vi = (dmat[imin*len+jmin]+u[imin]-u[jmin])/2.0;
			double vj = (dmat[imin*len+jmin]+u[jmin]-u[imin])/2.0;
			Node parnode = new Node();
			parnode.addNode( nodes.get(imin), vi );
			parnode.addNode( nodes.get(jmin), vj );
			
			if( imin > jmin ) {
				nodes.remove(imin);
				nodes.remove(jmin);
			} else {
				nodes.remove(jmin);
				nodes.remove(imin);
			}
			nodes.add( parnode );
			
			double[] dmatmp = new double[(len-1)*(len-1)];
			int k = 0;
			for( int i = 0; i < len; i++ ) {
				if( i != imin && i != jmin ) {
					for( int j = 0; j < len; j++ ) {
						if( j != imin && j != jmin ) {
							dmatmp[k++] = dmat[i*len+j];
						}
					}
					k++;
				}
			}
			k = 0;
			for( int i = 0; i < len; i++ ) {
				if( i != imin && i != jmin ) {
					dmatmp[((k++) + 1)*(len-1)-1] = (dmat[imin*len+i] + dmat[jmin*len+i] - dmat[imin*len+jmin])/2.0;
				}
			}
			k = 0;
			for( int i = 0; i < len; i++ ) {
				if( i != imin && i != jmin ) {
					dmatmp[(len-2)*(len-1)+(k++)] = (dmat[i*len+imin] + dmat[i*len+jmin] - dmat[jmin*len+imin])/2.0;
				}
			}
			len--;
			dmat = dmatmp;
			
			System.err.println( "size is " + nodes.size() );
		}
		
		Node parnode = new Node();
		parnode.addNode( nodes.get(0), dmat[1] );
		parnode.addNode( nodes.get(1), dmat[3] );
		nodes.clear();
		
		parnode.countLeaves();
		currentNode = parnode;
	}
	
	public Node getNode() {
		return currentNode;
	}
	
	public void setNode( Node node ) {
		currentNode = node;
	}
	
	public class Node {
		String 		name;
		String		meta;
		int			metacount;
		private double		h;
		private double		h2;
		String		color;
		List<Node>	nodes;
		int			leaves = 0;
		Node		parent;
		
		double		canvasx;
		double		canvasy;
		
		String		collapsed = null;
		
		public boolean isCollapsed() {
			return collapsed != null;
		}
		
		public String getCollapsedString() {
			return collapsed;
		}
		
		public void setCollapsed( String collapsed ) {
			this.collapsed = collapsed;
		}
		
		public Node() {
			nodes = new ArrayList<Node>();
			metacount = 0;
		}
		
		public Node( String name ) {
			this();
			this.name = name;
		}
		
		public void setCanvasLoc( double x, double y ) {
			canvasx = x;
			canvasy = y;
		}
		
		public double getCanvasX() {
			return canvasx;
		}
		
		public double getCanvasY() {
			return canvasy;
		}
		
		public double geth2() {
			return h2;
		}
		
		public double geth() {
			return h;
		}
		
		public void seth( double h ) {
			this.h = h;
		}
		
		public void seth2( double h2 ) {
			this.h2 = h2;
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
			
			if( meta != null && meta.length() > 0 ) {
				//System.err.println("muuu " + meta);
				if( name != null && name.length() > 0 ) str += "'"+name+";"+meta+"'";
				else str += "'"+meta+"'";
			} else if( name != null && name.length() > 0 ) str += name;
			
			//if( h > 0.0 )
			str += ":"+h;
			if( color != null && color.length() > 0 ) str += ":"+color;
			//else str += ":0.0";
			
			return str;
		}
		
		public List<Node> getNodes() {
			return nodes;
		}
		
		public void addNode( Node node, double h ) {
			nodes.add( node );
			node.h = h;
		}
		
		public void removeNode( Node node ) {
			nodes.remove( node );
			
			if( nodes.size() == 1 ) {
				if( this.getParent().getNodes().remove( this ) ) {
					Node thenode = nodes.get(0);
					thenode.seth( thenode.geth() + this.geth() );
					this.getParent().getNodes().add( thenode );
				}
			}
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
		
		public void setMeta( String newmeta ) {
			this.meta = newmeta;
		}
		
		public String getColor() {
			return color;
		}
		
		public int countLeaves() {
			int total = 0;
			if( !isCollapsed() && nodes != null && nodes.size() > 0 ) {
				for( Node node : nodes ) {
					total += node.countLeaves();
				}
			} else total = 1;
			leaves = total;
			
			return total;
		}
		
		public int getLeavesCount() {
			return leaves;
		}
		
		public int countMaxHeight() {
			int val = 0;
			for( Node node : nodes ) {
				val = Math.max( val, node.countMaxHeight() );
			}
			return val+1;
		}
		
		public double getHeight() {
			double h = this.geth();
			double d = h + ((parent != null) ? parent.getHeight() : 0.0);
			//console( h + " total " + d );
			return d;
		}
		
		public double getMaxHeight() {
			double max = 0.0;
			for( Node n : nodes ) {
				double nmax = n.getMaxHeight();
				if( nmax > max ) max = nmax;
			}
			return geth()+max;
		}
		
		public Node getParent() {
			return parent;
		}
		
		public void setParent( Node parent ) {
			this.parent = parent;
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
	
	public void extractMetaRecursive( Node node, Map<String,Map<String,String>> mapmap, Set<String> collapset, boolean collapse ) {
		if( node.name != null ) extractMeta( node, mapmap );
		
		List<Node> checklist = node.nodes;
		for( Node subnode : checklist ) {
			extractMetaRecursive(subnode, mapmap, collapset, collapse);
		}
		
		if( mapmap != null && mapmap.size() > 0 && checklist.size() > 0 ) {
			String metacheck = null;
			boolean dual = true;
			String partial = "";
			for( Node n : checklist ) {
				if( n.meta != null ) {
					String nmeta = null;
					if( n.name != null && n.name.length() > 0 ) {
						nmeta = n.name.substring(7).trim();
						
						/*if( n.name.startsWith("T.ign") ) {
							System.err.println();
						}*/
					}
					
					if( n.meta.contains(";") || (n.nodes != null && n.nodes.size() > 0) ) {
						String[] split = n.meta.split(";");
						if( split.length > 2 ) {
							String[] msp = split[split.length-1].split(":");
							String val = null;
							if( msp.length > 1 ) {
								val = (msp[1].contains("awai") || msp[1].contains("ibet") || msp[1].contains("ellow")) ? msp[1].split(" ")[0] : msp[0];
							} else {
								val = msp[0];
							}
														
							if( nmeta == null ) nmeta = val;
							else nmeta += "-"+val;
						} else if( nmeta == null ) nmeta = split[0];
							
						/*String[] lsp = nmeta.split("-");
						if( lsp.length > 1 ) {
							String[] msp = lsp[1].split(":");
							if( msp.length > 1 ) {
								nmeta = lsp[0] + "-" + ((msp.length > 1 && (nmeta.contains("awai") || nmeta.contains("ibet") || nmeta.contains("ellow"))) ? msp[1].split(" ")[0] : msp[0]);
							}
						} else {
							String[] msp = nmeta.split(":");
							if( msp.length > 1 ) {
								nmeta = (msp.length > 1 && (nmeta.contains("awai") || nmeta.contains("ibet") || nmeta.contains("ellow"))) ? msp[1].split(" ")[0] : msp[0];
							}
						}*/
						//}
					}
									
					if( nmeta != null ) {
						//if( nmeta.contains("oshimai") ) System.err.println( nmeta + "  " + metacheck );
						
						if( metacheck == null ) {
							metacheck = nmeta;
						} else if( nmeta.length() == 0 || metacheck.length() == 0 ) {
							//System.err.println( "buuuu " + nmeta + "  " + metacheck);
							dual = false;
						} else {
							if( !collapse ) { 
								if( (!nmeta.contains(metacheck) && !metacheck.contains(nmeta)) ) {
									String[] split1 = nmeta.split("-");
									String[] split2 = metacheck.split("-");
									
									String cont = null;
									if( split1.length > 1 || split2.length > 1 ) {
										Set<String>	s1 = new HashSet<String>( Arrays.asList(split1) );
										Set<String> s2 = new HashSet<String>( Arrays.asList(split2) );
										
										for( String str : s1 ) {
											if( s2.contains( str ) ) {
												cont = str+"-";
												break;
											}
										}
									}
									
									if( cont != null ) {
										metacheck = cont;
										partial = cont;
									} else dual = false;
								} else {
									if( nmeta.length() > metacheck.length() ) {
										metacheck = collapset.contains(metacheck) ? nmeta : metacheck;
									} else {
										metacheck = collapset.contains(nmeta) ? metacheck : nmeta;
									}
									partial = metacheck;
								}
							} else {
								if( (!nmeta.contains(metacheck) || !metacheck.contains(nmeta)) ) {
									dual = false;
								}
							}
						}
					}
				}
			}
			
			if( dual ) {
				//if( metacheck.contains("oshimai") ) System.err.println("dual "+metacheck);
				for( Node n : checklist ) {
					if( n.nodes != null && n.nodes.size() > 0 ) {
						//if(n.meta != null) System.err.println("delete meta" + n.meta);
						if( partial.length() > 0 ) {
							if( n.meta != null && partial.length() >= n.meta.length() ) {
								n.meta = null;
							}
							//System.err.println( "meta " + n.meta );
							//n.meta = n.meta.replace(partial, "");
							//n.meta = n.meta.replace("-", "")	;
						} else {
							n.meta = null;
						}
					}
				}
				//String[] msp = metacheck.split(":");
				//node.meta = (msp.length > 1 && (metacheck.contains("awai") || metacheck.contains("ibet") || metacheck.contains("ellow"))) ? msp[1].split(" ")[0] : msp[0];
				node.meta = metacheck;
			} else node.meta = partial;
		}
	}
	
	public Set<Node> includeNodes( Node n, Set<String> include ) {
		Set<Node>	ret = null;
		if( include.contains( n.name ) ) {
			ret = new HashSet<Node>();
			ret.add( n );
		}
		for( Node sn : n.nodes ) {
			Set<Node>	ns = includeNodes( sn, include );
			if( ns != null ) {
				if( ret == null ) ret = ns;
				else ret.addAll( ns );
			}
		}
		
		return ret;
	}
	
	public void includeAlready( Node n, Set<Node> include ) {
		if( n.parent != null && !include.contains(n.parent) ) {
			/*if( n.parent.name != null || n.parent.name.length() > 0 ) {
				System.err.println( "erm " + n.parent.name );
			}*/
			include.add( n.parent );
			includeAlready( n.parent, include );
		}
	}
	
	public void deleteNotContaining( Node n, Set<Node> ns ) {
		n.nodes.retainAll( ns );
		for( Node sn : n.nodes ) {
			deleteNotContaining(sn, ns);
		}
		if( n.nodes.size() == 1 ) {
			Node nn = n.nodes.get(0);
			//if( nn.nodes.size() > 0 ) {
				n.name = nn.name;
				n.meta = nn.meta;
				n.h += nn.h;
				n.nodes = nn.nodes;
			//} 
			/*else if( nn.name == null || nn.name.length() == 0 ) {
				n.nodes.clear();
				n.nodes = null;
			}*/
		}
	}
	
	public void markColor( Node node, Map<String,String> colormap ) {
		if( colormap.containsKey(node.meta) ) node.color = colormap.get(node.meta);
		for( Node n : node.nodes ) {
			markColor(n, colormap);
		}
	}
	
	public TreeUtil() {
		super();
	}
	
	public void clearParentNames( Node node ) {
		if( node.getNodes() != null && node.getNodes().size() > 0 ) {
			node.setName("");
			for( Node n : node.getNodes() ) {
				clearParentNames( n );
			}
		}
	}
	
	public TreeUtil( String str, boolean inverse, Set<String> include, Map<String,Map<String,String>> mapmap, boolean collapse, Set<String> collapset, Map<String,String> colormap, boolean clearParentNodes ) {
		super();
		loc = 0;
		//System.err.println( str );
		if( str != null && str.length() > 0 ) {
			Node resultnode = parseTreeRecursive( str, inverse );
			
			if( clearParentNodes ) {
				clearParentNames( resultnode );
			}
			
			if( include == null ) {
				include = new HashSet<String>();
				String inc = str.substring( loc+1 ).trim();
				if( inc.length() > 0 && !inc.startsWith("(") ) {
					String[] split = inc.split(",");
					for( String sp : split ) {
						include.add( sp.trim() );
					}
				}
			}
			
			if( include.size() > 0 ) {
				Set<Node> sn = includeNodes( resultnode, include );
				Set<Node> cloneset = new HashSet<Node>( sn );
				for( Node n : sn ) {
					includeAlready( n, cloneset );
				}
				
				deleteNotContaining( resultnode, cloneset );
				resultnode.h = 0.0;
				
				/*for( Node n : cloneset ) {
					if( n.name != null && n.name.trim().length() > 0 ) System.err.println( "nnnnnnnn " + n.name );
				}*/
			}
			
			extractMetaRecursive( resultnode, mapmap, collapset, collapse );
			if( colormap != null ) {
				markColor( resultnode, colormap );
			}
			if( collapse ) {
				collapseTree( resultnode, collapset, false );
			}
			
			this.currentNode = resultnode;
		} /*else {
			System.err.println( str );
		}*/
	}
	
	public void collapseTreeAdvanced( Node node, Collection<String> collapset ) {
		if( node.nodes != null && node.nodes.size() > 0 ) {
			for( Node n : node.nodes ) {
				collapseTreeAdvanced( n, collapset );
			}
			
			String test = null;
			boolean collapse = node.nodes.size() > 1;
			for( Node n : node.nodes ) {
				String nname = n.getName() != null ? n.getName() : "";
				if( collapset == null || collapset.isEmpty() ) {
					if( test == null ) {
						test = nname;
					} else if( test.length() == 0 || nname.length() == 0 || !(nname.contains(test) || test.contains(nname)) ) {
						collapse = false;
						break;
					}
				} else {
					if( test == null ) {
						for( String s : collapset ) {
							if( nname.contains(s) ) {
								test = s;
								break;
							}
						}
						
						if( test == null ) {
							test = "";
						}
					} else if( !nname.contains(test) ) {
						collapse = false;
						break;
					}
				}
			}
			
			if( collapse && (collapset == null || collapset.contains(test)) ) {
				node.nodes.clear();
				//node.nodes = null;
				node.setName( test );
			}
		}
	}
	
	public void collapseTreeSimple( Node node, Set<String> collapset ) {
		if( node.nodes != null && node.nodes.size() > 0 ) {
			boolean check = false;
			for( String s : collapset ) {
				if( node.meta != null && node.meta.contains(s) ) {
					check = true;
					break;
				}
			}
			if( check ) {
				node.name = node.meta;
				node.meta = Integer.toString( node.countLeaves() );
				node.nodes.clear();
				//node.nodes = null;
			} else {
				for( Node n : node.nodes ) {
					collapseTreeSimple( n, collapset );
				}
			}
		}
	}
	
	public boolean collapseTree( Node node, Set<String> collapset, boolean delete ) {
		boolean ret = false;
		
		if( node.nodes != null && node.nodes.size() > 0 ) {
			//Set<Node>	delset = null;
			//if( delete ) delset = new HashSet<Node>();
			
			boolean any = false;
			for( Node n : node.nodes ) {
				if( collapseTree( n, collapset, delete ) ) any = true;
				//else if( delset != null ) delset.add( n );
			}
			
			//if( delset != null ) node.nodes.removeAll( delset );
			
			if( any ) ret = true;
			else {
				if( node.meta != null && node.meta.length() > 0 ) {
					node.name = node.meta;
					node.meta = Integer.toString( node.countLeaves() );
					node.nodes.clear();
					ret = true;
				}
			}
		}
		
		return ret;
	}
	
	public double reroot( Node oldnode, Node newnode ) {
		for( Node res : oldnode.nodes ) {
			double b;
			if( res == newnode ) b = res.h;
			else b = reroot( res, newnode );
			
			if( b != -1 ) {
				res.nodes.add( oldnode );
				oldnode.parent = res;
				
				double tmph = oldnode.h;
				//res.h = oldnode.h;
				oldnode.h = b;
				oldnode.nodes.remove( res );
				
				currentNode = newnode;
				currentNode.countLeaves();
				
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
		TreeUtil treeutil = new TreeUtil( str, inverse, null, null, false, null, null, false );
	}
	
	int metacount = 0;
	public void extractMeta( Node node, Map<String,Map<String,String>> mapmap ) {
		node.name = node.name.replaceAll("'", "");
		
		int ki = node.name.indexOf(';');
		if( ki != -1 ) {
			//String[] metasplit = node.name.split(";");
			node.meta = node.name.substring(ki+1).trim();
			node.name = node.name.substring(0,ki).trim();
			
			/*int ct = 1;
			String meta = metasplit[ ct ].trim();
			while( !meta.contains(":") && ct < metasplit.length-1 ) {
				meta = metasplit[ ++ct ];
			}
			
			String[] msplit = meta.split(":");
			node.meta = meta.contains("awai") || meta.contains("ellow") ? msplit[1].split(" ")[0].trim() : msplit[0].trim();
			metacount++;
			
			/*for( String meta : metasplit ) {
				if( meta.contains("name:") ) {
					node.name = meta.substring(5).trim();
				} else if( meta.contains("country:") ) {
					String[] msplit = meta.substring(8).trim().split(":");
					node.meta = meta.contains("awai") || meta.contains("ellow") ? msplit[1].trim() : msplit[0].trim();
					metacount++;
				}
			}*/
		}
		
		if( mapmap != null ) {
			String mapname = node.name;
			/*int ik = mapname.indexOf('.');
			if( ik != -1 ) {
				mapname = mapname.substring(0, ik);
			}*/

			if( mapmap.containsKey( mapname ) ) {
				Map<String,String>	keyval = mapmap.get( mapname );
				
				for( String key : keyval.keySet() ) {
					String meta = keyval.get(key);
					
					if( key.equals("name") ) {
						node.name = meta.trim();
					} else if( node.meta == null || node.meta.length() == 0 ) {
						node.meta = meta;
					} else {
						node.meta += ";" + meta;
						//node.meta += meta;
					}
				}
				/*if( keyval.containsKey("country") ) {
					String meta = keyval.get("country");
					//int i = meta.indexOf(':');
					//if( i != -1 ) meta = meta.substring(0, i);
					node.meta = meta;
				}
				
				if( keyval.containsKey("full_name") ) {
					String tax = keyval.get("full_name");
					int i = tax.indexOf(':');
					if( i != -1 ) tax = tax.substring(0, i);
					node.name = tax;
				}*/
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
					ret.parent = node;
					node.leaves++;
				} else {
					ret.nodes.add( node );
					node.parent = ret;
					//if( ret.name != null && ret.name.length() > 0 ) System.err.println("fokk you too");
					ret.leaves += node.leaves;
				}
			} else {
				node = new Node();
				int end = loc+1;
				char n = str.charAt(end);
				
				int si = 0;
				if( c == '\'' ) {
					while( end < str.length()-1 && n != '\'' ) {
						n = str.charAt(++end);
					}
					si = end-loc-1;
					//String code = str.substring( loc, end );
					//node.name = code.replaceAll("'", "");
					//loc = end+1;
				}
				
				boolean outsvig = true;
				//while( end < str.length()-1 && n != ',' && n != ')' ) {
				while( end < str.length()-1 && (n != ',' && n != ')' || !outsvig) ) {
					n = str.charAt(++end);
					if( outsvig && n == '(' ) {
						outsvig = false;
						n = str.charAt(++end);
					} else if( !outsvig && n == ')' ) {
						outsvig = true;
						n = str.charAt(++end);
					}
					
					//end++;
				}
				
				String code = str.substring( loc, end );
				int ci = code.indexOf(":", si);
				if( ci != -1 ) {
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
						String color = split[2].substring(0);
						if( color.contains("rgb") ) {
							try {
								int co = color.indexOf('(');
								int ce = color.indexOf(')', co+1);
								String[] csplit = color.substring(co+1, ce).split(",");
								int r = Integer.parseInt( csplit[0].trim() );
								int g = Integer.parseInt( csplit[1].trim() );
								int b = Integer.parseInt( csplit[2].trim() );
								node.color = "rgb("+r+","+g+","+b+")"; //new Color( r,g,b );
							} catch( Exception e ) {
								
							}
						} else {
							try {
								int r = Integer.parseInt( color.substring(0, 2), 16 );
								int g = Integer.parseInt( color.substring(2, 4), 16 );
								int b = Integer.parseInt( color.substring(4, 6), 16 );
								node.color = "rgb("+r+","+g+","+b+")"; //new Color( r,g,b );
							} catch( Exception e ) {
								
							}
						}
					} else node.color = null;
					
					String dstr = split[1].trim();
					String dstr2 = "0";
					if( dstr.contains("[") ) {
						int start = split[1].indexOf('[');
						int stop = split[1].indexOf(']');
						dstr2 = dstr.substring( start+1, stop );
						dstr = dstr.substring( 0, start );
					}
					
					try {
						node.h = Double.parseDouble( dstr );
						node.h2 = Double.parseDouble( dstr2 );
					} catch( Exception e ) {
						System.err.println();
					}
					
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
					ret.parent = node;
					node.leaves++;
				} else {
					ret.nodes.add( node );
					//if( ret.name != null && ret.name.length() > 0 ) System.err.println("fokk");
					node.parent = ret;
					ret.leaves++;
				}
			}
		}
		
		Node use = inverse ? node : ret;
		
		
		/*List<Node> checklist = use.nodes;
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
		} else use.meta = "";*/
		
		//System.err.println("setting: "+metacheck + use.nodes);
		
		if( loc < str.length()-1 ) {
			loc++;
			int end = loc;
			char n = str.charAt(end);
			
			int si = 0;
			if( n == '\'' ) {
				n = str.charAt(++end);
				while( end < str.length()-1 && n != '\'' ) {
					n = str.charAt(++end);
				}
				si = end-loc-1;
				//String code = str.substring( loc, end );
				//node.name = code.replaceAll("'", "");
				//loc = end+1;
			}
			
			while( end < str.length()-1 && n != ',' && n != ';' && n != ')' ) {
				n = str.charAt(++end);
			}
			String code = str.substring( loc, end );
			int ci = code.indexOf(":", si);
			if( ci != -1 ) {
				String[] split;
				int i = code.lastIndexOf("'");
				if( i > 0 ) {
					split = code.substring(i, code.length()).split(":");
					ret.name = code.substring(0, i+1);
				} else {
					split = code.split(":");
					ret.name = split.length > 0 ? split[0] : "";
				}
				
				//String[] split = code.split(":");
				if( split.length > 2 ) {
					String color = split[2].substring(0);
					try {
						int r = Integer.parseInt( color.substring(0, 2), 16 );
						int g = Integer.parseInt( color.substring(2, 4), 16 );
						int b = Integer.parseInt( color.substring(4, 6), 16 );
						ret.color = "rgb("+r+","+g+","+b+")"; //new Color( r,g,b );
					} catch( Exception e ) {
						
					}
				} else ret.color = null;
				String dstr = split.length > 1 ? split[1].trim() : "0";
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
