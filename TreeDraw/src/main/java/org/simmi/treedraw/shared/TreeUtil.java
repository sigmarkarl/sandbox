package org.simmi.treedraw.shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TreeUtil {
	private Node currentNode = null;
	String treelabel = null;
	
	public Node removeRoot( Node n ) {
		Node ret;
		List<Node> ln = n.getNodes();
		Node n1 = ln.get( 0 );
		Node n2 = ln.get( 1 );
		if( n1.getNodes() != null & n1.getNodes().size() > 0 ) {
			n1.addNode( n2, n1.geth()+n2.geth() );
			n1.setParent( null );
			ret = n1;
		} else {
			n2.addNode( n1, n2.geth()+n2.geth() );
			n2.setParent( null );
			ret = n2 ;
		}			
		ret.countLeaves();
		
		return ret;
	}
	
	public Node getParent( Node root, Set<String> leaveNames ) {
		Set<String> currentLeaveNames = root.getLeaveNames();
		if( currentLeaveNames.size() >= leaveNames.size() ) {
			//System.err.println( currentLeaveNames );
			if( currentLeaveNames.equals( leaveNames ) ) return root;
			else {
				for( Node n : root.getNodes() ) {
					Node par = getParent(n, leaveNames);
					if( par != null ) return par;
				}
			}
		}
			
		return null;
	}
	
	public String getSelectString( Node n, boolean meta ) {
		String ret = "";
		if( n.isLeaf() ) {
			if( n.isSelected() ) ret += n.toStringWoLengths(); //meta ? (n.getMeta() != null ? n.getMeta() : n.getName()) : n.getName();
		} else for( Node nn : n.getNodes() ) {
			String selstr = getSelectString( nn, meta );
			if( selstr.length() > 0 ) {
				if( ret.length() == 0 ) ret += getSelectString( nn, meta );
				else ret += ","+getSelectString( nn, meta );
			}
		}
		return ret;
	}
	
	public void reduceParentSize( Node n ) {
		List<Node> nodes = n.getNodes();
		if( nodes != null && nodes.size() > 0 ) {
			for( Node node : nodes) {
				reduceParentSize( node );
			}
			if( n.getFontSize() != -1.0 && n.getFontSize() != 0.0 ) n.setFontSize( n.getFontSize()*0.8 );
			else n.setFontSize( 0.8 );
		}
	}
	
	public void propogateSelection( Set<String> selset, Node node ) {
		List<Node> nodes = node.getNodes();
		if( nodes != null ) {
			for( Node n : nodes ) {
				propogateSelection(selset, n);
			}
		}
		if( node.isLeaf() && (selset.contains( node.toStringWoLengths() ) || selset.contains( node.getName() )) ) node.setSelected( true );
		//else node.setSelected( false );
	}
	
	public void invertSelectionRecursive( Node root ) {
		root.setSelected( !root.isSelected() );
		if( root.getNodes() != null ) for( Node n : root.getNodes() ) {
			invertSelectionRecursive( n );
		}
	}
	
	public boolean isChildSelected( Node n ) {
		if( n.isSelected() ) return true;
		
		List<Node> nodes = n.getNodes();
		if( nodes != null ) {
			for( Node node : nodes ) {
				if( isChildSelected(node) ) return true;
			}
		}
		
		return false;
	}
	
	public boolean retainSelection( Node n ) {
		if( isChildSelected( n ) ) {
			List<Node> nodes = n.getNodes();
			if( nodes != null ) {
				Node rem = null;
				List<Node> copy = new ArrayList<Node>(nodes);
				for( Node node : copy ) {
					if( retainSelection( node ) ) {
						rem = node;
					}
				}
				if( rem != null ) {
					rem.getParent().removeNode( rem );
				}
			}
			return false;
		} else {
			return true;
		}
	}
	
	public void setTreeLabel( String label ) {
		this.treelabel = label;
	}
	
	public String getTreeLabel() {
		return this.treelabel;
	}
	
	public boolean isRooted() {
		return currentNode.getNodes().size() == 2;
	}
	
	public void propogateCompare( Node n ) {
		if( n.getNodes().size() > 0 ) {
			n.comp++;
			for( Node nn : n.getNodes() ) {
				propogateCompare( nn );
			}
		}
	}	
	
	public void appendCompare( Node n ) {
		if( n.getNodes().size() > 0 ) {
			n.name = ""+n.comp;
			for( Node nn : n.getNodes() ) {
				appendCompare( nn );
			}
		}
	}
	
	public Node findNode( Node root, String subtree ) {
		Node ret = null;
		
		String rn = root.toStringWoLengths();
		if( rn.equals(subtree) ) ret = root;
		else if( rn.length() > subtree.length() ) {
			for( Node n : root.getNodes() ) {
				Node nn = findNode( n, subtree );
				if( nn != null ) {
					ret = nn;
					break;
				}
			}
		}
		
		return ret;
	}
	
	public void compareTrees( String ns1, Node n1, Node n2 ) {
		if( n2.getNodes().size() > 1 ) {
			String ns2 = n2.toStringWoLengths();
		
			if( ns1.contains(ns2) ) {
				Node n = findNode( n1, ns2 );
				propogateCompare( n );
			} else {
				for( Node n : n2.getNodes() ) {
					compareTrees( ns1, n1, n );
				}
			}
		}
	}
	
	public void arrange( Node root, Comparator<Node> comparator ) {
		List<Node> nodes = root.getNodes();
		if( nodes != null ) {
			for( Node n : nodes ) {
				arrange( n, comparator );
			}
			Collections.sort( nodes, comparator );
		}
	}
	
	public double[][] lms( double[] distmat, List<String> corrInd, Node toptree ) {
		int len = corrInd.size();
		List<Node> nodes = this.getLeaves( toptree );
		int c = 0;
		for( String s : corrInd ) {
			int i = c;
			while( !s.equals( nodes.get(i).getName() ) ) i++;
			
			Node tnode = nodes.get(c);
			nodes.set( c, nodes.get(i) );
			nodes.set( i, tnode );
			
			c++;
		}
		
		List<Double> lad = new ArrayList<>();
		for( int y = 0; y < corrInd.size()-1; y++ ) {
			for( int x = y+1; x < corrInd.size(); x++ ) {
				lad.add( distmat[y*corrInd.size()+x] );
			}
		}
		double[] d = new double[ lad.size() ];
		int count = 0;
		for( double dval : lad ) {
			d[count++] = dval;
		}
		
		int nodecount = toptree.countSubnodes();
		double[][] X = new double[ lad.size() ][ nodecount ];
		for( int k = 0; k < nodecount; k++ ) {
			for( int i = 0; i < lad.size(); i++ ) {
				
			}
		}
		
		return X;
	}
	
	public Node neighborJoin( double[] corrarr, List<String> corrInd, Node guideTree, boolean rootTree, boolean parseName ) {
		Node retnode = new Node();
		try {
			List<Node> nodes;
			int len = corrInd.size();
			if( guideTree != null ) {
				nodes = this.getLeaves( guideTree );
				int c = 0;
				for( String s : corrInd ) {
					int i = c;
					while( !s.equals( nodes.get(i).getName() ) ) i++;
					
					Node tnode = nodes.get(c);
					nodes.set( c, nodes.get(i) );
					nodes.set( i, tnode );
					
					c++;
				}
			} else {
				nodes = new ArrayList<Node>();
				for( String name : corrInd ) {
					Node n = new Node( name, parseName );
					nodes.add( n );
				}
			}
			
			double[] dmat = corrarr; //new double[len*len];
			double[] u = new double[len];
			//System.arraycopy(corrarr, 0, dmat, 0, len*len);
			while( len > 2 ) {
				//System.err.println( "trying " + len + " size is " + nodes.size() );
				for ( int i = 0; i < len; i++ ) {
					u[i] = 0;
					for ( int j = 0; j < len; j++ ) {
						if( i != j ) {
							double dval = dmat[i*len+j];
							if( Double.isNaN( dval ) ) {
								System.err.println("erm");
							}
							u[i] += dval;
						}
					}
					u[i] /= len-2;
				}
				
				int imin = 0;
				int jmin = 1;
				double dmin = Double.MAX_VALUE;
				
				if( guideTree == null ) {
					for ( int i = 0; i < len-1; i++ ) {
						for ( int j = i+1; j < len; j++ ) {
							//if( i != j ) {
								double val = dmat[i*len+j] - u[i] - u[j];
								//if( dmat[i*len+j] < 50 ) System.err.println("euff " + val + " " + i + " " + j + "  " + dmat[i*len+j] );
								if( val < dmin ) {
									dmin = val;
									imin = i;
									jmin = j;
								}
							//}
						}
					}
				} else {
					for ( int i = 0; i < len-1; i++ ) {
						for ( int j = i+1; j < len; j++ ) {
							Node iparent = nodes.get( i ).getParent();
							Node jparent = nodes.get( j ).getParent();
							if( iparent == jparent ) {
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
				}
				
				//System.err.println( dmat[imin*len+jmin] );
				double vi = (dmat[imin*len+jmin]+u[imin]-u[jmin])/2.0;
				double vj = (dmat[imin*len+jmin]+u[jmin]-u[imin])/2.0;
				
				Node parnode;
				Node nodi = nodes.get( imin );
				Node nodj = nodes.get( jmin );
				if( guideTree == null ) {
					parnode = new Node();
					parnode.addNode( nodi, vi );
					parnode.addNode( nodj, vj );
				} else {
					parnode = nodi.getParent();
					nodi.seth( vi );
					nodj.seth( vj );
				}
				
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
				//boolean done = false;
				for( int i = 0; i < len; i++ ) {
					if( i != imin && i != jmin ) {
						for( int j = 0; j < len; j++ ) {
							if( j != imin && j != jmin ) {
								/*if( k >= dmatmp.length ) {
									System.err.println();
								}*/
								/*if( k >= dmatmp.length ) {
									System.err.println("ok");
								}*/
								dmatmp[k] = dmat[i*len+j];
								k++;
							}
						}
						
						k++;
						
						//done = true;
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
				
				//System.err.println( "size is " + nodes.size() );
			}
			
			if( rootTree ) {
				retnode.addNode( nodes.get(0), dmat[1] );
				retnode.addNode( nodes.get(1), dmat[2] );
			} else {
				retnode = nodes.get(0);
				retnode.seth(0);
				retnode.setParent( null );
				retnode.addNode( nodes.get(1), dmat[1]+dmat[2] );
			}
			nodes.clear();
		} catch( Exception e ) {
			e.printStackTrace();
			//console( e.getMessage() );
		}
		
		retnode.countLeaves();
		return retnode;
	}
	
	public Node getNode() {
		return currentNode;
	}
	
	public void setNode( Node node ) {
		currentNode = node;
	}
	
	public void grisj( Node startNode ) {
		List<Node> lnodes = startNode.getNodes();
		if( lnodes != null ) {
			List<Node>	kvislist = new ArrayList<Node>();
			for( Node n : lnodes ) {
				if( !n.isLeaf() ) {
					kvislist.add( n );
				}
			}
			
			if( kvislist.size() == 0 ) {
				Node longestNode = null;
				double h = -1.0;
				for( Node n : lnodes ) {
					if( n.geth() > h ) {
						h = n.geth();
						longestNode = n;
					}
				}
				if( longestNode != null ) startNode.removeNode( longestNode );
			} else {
				for( Node n : kvislist ) {
					grisj( n );
				}
			}
		}
	}
	
	public static class NodeSet implements Comparable<NodeSet> {
		public NodeSet( Set<String> nodes ) {
			this.nodes = nodes;
			//this.count = count;
		}
		
		Set<String>					nodes;
		Map<String,List<Double>>	leaveHeightMap = new HashMap<String,List<Double>>();
		//Map<String,List<Double>>	leaveHeightMap = new HashMap<String,List<Double>>();
		List<Double> 				count = new ArrayList<Double>();
		List<Double> 				boots = new ArrayList<Double>();
		
		public int getCount() {
			return count.size();
		}
		
		public Set<String> getNodes() {
			return nodes;
		}
		
		public void addLeaveHeight( String name, double h ) {
			List<Double>	leaveHeights;
			if( leaveHeightMap.containsKey(name) ) {
				leaveHeights = leaveHeightMap.get( name );
			} else {
				leaveHeights = new ArrayList<Double>();
				leaveHeightMap.put(name, leaveHeights);
			}
			leaveHeights.add( h );
		}
		
		public double getAverageLeaveHeight( String name ) {
			if( leaveHeightMap.containsKey( name ) ) {
				List<Double> dlist = leaveHeightMap.get( name );
				
				double avg = 0.0;
				
				for( double d : dlist ) {
					avg += d;
				}
				
				avg /= dlist.size();
				return avg;
			}
			return -1.0;
		}
		
		public void addHeight( double h ) {
			//if( count == null ) count = new ArrayList<Double>();
			count.add( h );
		}
		
		public void addBootstrap( double h ) {
			//if( count == null ) count = new ArrayList<Double>();
			boots.add( h );
		}
		
		public double getAverageHeight() {
			double avg = 0.0;
			
			for( double d : count ) {
				avg += d;
			}
			
			avg /= count.size();
			return avg;
		}
		
		public double getAverageBootstrap() {
			double avg = 0.0;
			
			for( double d : boots ) {
				avg += d;
			}
			
			avg /= boots.size();
			return avg;
		}
		
		@Override
		public int compareTo(NodeSet o) {
			int val = o.count.size() - count.size();
			if( val == 0 ) return o.nodes.size() - nodes.size(); 
			else return val;
		}
	};
	
	public class Node {
		String 				name;
		String				id;
		String				meta;
		int					metacount;
		String				imgurl;
		private double		h;
		private double		h2;
		private double		bootstrap;
		String				color;
		List<String>		infolist;
		List<Node>			nodes;
		int			leaves = 0;
		Node		parent;
		int			comp = 0;
		double		fontsize = -1.0;
		double		framesize = -1.0;
		double		frameoffset = -1.0;
		
		double		canvasx;
		double		canvasy;
		
		String		collapsed = null;
		boolean		selected = false;
		
		public boolean isLeaf() {
			return nodes == null || nodes.size() == 0;
		}
		
		public List<String> traverse() {
			List<String>	ret = new ArrayList<String>();
			
			List<Node> nodes = this.getNodes();
			if( nodes != null && nodes.size() > 0 ) {
				for( Node n : nodes ) {
					ret.addAll( n.traverse() );
				}
				if( nodes.size() == 1 ) ret.add( this.getName() );
			} else ret.add( this.getName() );
			
			return ret;
		}
		
		public Set<String> getLeaveNames() {
			Set<String>	ret = new HashSet<String>();
			
			List<Node> nodes = this.getNodes();
			if( nodes != null && nodes.size() > 0 ) {
				for( Node n : nodes ) {
					ret.addAll( n.getLeaveNames() );
				}
				if( nodes.size() == 1 ) ret.add( this.getName() );
			} else ret.add( this.getName() );
			
			return ret;
		}
		
		public Node getRoot() {
			Node root = this;
			
			Node parent = root.getParent();
			while( parent != null ) {
				root = parent;
				parent = root.getParent();
			}
			
			return root;
		}
		
		public List<String> getInfoList() {
			return infolist;
		}
		
		public Node findNode( String id ) {
			if( id.equals( this.id ) ) {
				return this;
			} else {
				for( Node n : this.nodes ) {
					Node ret = n.findNode( id );
					if( ret != null ) {
						return ret;
					}
				}
			}
			return null;
		}
		
		public Node getOtherChild( Node child ) {
			if( nodes != null && nodes.size() > 0 ) {
				int i = nodes.indexOf( child );
				return i == 0 ? nodes.get(1) : nodes.get(0);
			}
			return null;
		}
		
		public Node firstLeaf() {
			Node res = null;
			if( nodes == null || nodes.size() == 0 ) {
				res = this;
			} else {
				for( Node subn : nodes ) {
					res = subn.firstLeaf();
					break;
				}
			}
			return res;
		}
		
		public Set<String> nodeCalc( List<Set<String>>	ls ) {
			Set<String>	s = new HashSet<String>();
			if( nodes == null || nodes.size() == 0 ) {
				s.add( id );
			} else {
				for( Node subn : nodes ) {
					Set<String> set = subn.nodeCalc( ls );
					s.addAll( set );
				}
				ls.add( s );
			}
			return s;
		}
		
		public Set<String> nodeCalcMap( Map<Set<String>,NodeSet>	ls ) {
			Set<String>	s = new HashSet<String>();
			if( nodes == null || nodes.size() == 0 ) {
				s.add( id == null ? name : id );
			} else {
				for( Node subn : nodes ) {
					Set<String> set = subn.nodeCalcMap( ls );
					s.addAll( set );
				}
				if( nodes.size() == 1 ) s.add( id == null ? name : id );
				
				NodeSet	heights;
				if( ls.containsKey( s ) ) {
					heights = ls.get( s );
					//ls.put( s, ls.get(s)+1 );
				} else {
					heights = new NodeSet( s );
					ls.put( s, heights );
				}
				for( Node subn : nodes ) {
					if( subn.isLeaf() ) {
						//System.err.println( subn.getName() + "  " + subn.geth() );
						heights.addLeaveHeight( subn.getName(), subn.geth() );
					}
				}
				heights.addHeight( this.geth() );
				
				double bt = this.getBootstrap();
				if( bt > 0.0 ) {
					/*if( s.size() == 2 && s.contains("t.scotoductusSA01") ) {
						System.err.println( "bootstrap " + s + "  " + this.getBootstrap() );
					}*/
					heights.addBootstrap( bt );
				} else heights.addBootstrap( 1.0 );
			}
			return s;
		}
		
		public Set<String> leafIdSet() {
			Set<String> lidSet = new HashSet<String>();
			
			if( nodes == null || nodes.size() == 0 ) {
				lidSet.add( id );
			} else {
				for( Node subn : nodes ) {
					lidSet.addAll( subn.leafIdSet() );
				}
			}
			
			return lidSet;
		}
		
		public String getId() {
			return id;
		}
		
		public void setSelected( boolean selected ) {
			this.selected = selected;
		}
		
		public boolean isSelected() {
			return this.selected;
		}
		
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
		
		public Node( String name, boolean parse ) {
			this();
			this.setName( name, parse );
			/*this.name = name;
			this.id = name;*/
		}
		
		public Node( String name ) {
			this();
			this.setName( name );
			/*this.name = name;
			this.id = name;*/
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
		
		public double getBootstrap() {
			return bootstrap;
		}
		
		public double geth2() {
			return h2;
		}
		
		public double geth() {
			return h;
		}
		
		public void setBootstrap( double bootstrap ) {
			this.bootstrap = bootstrap;
		}
		
		public void seth( double h ) {
			this.h = h;
		}
		
		public void seth2( double h2 ) {
			this.h2 = h2;
		}
		
		public String toStringWoLengths() {
			return generateString( false );
			
			/*String str = "";
			if( nodes.size() > 0 ) {
				str += "(";
				int i = 0;
				
				/*String n1 = nodes.get(0).toStringSortedWoLengths();
				if( nodes.size() > 1 ) {
					String n2 = nodes.get(1).toStringSortedWoLengths();
					if( n1.compareTo( n2 ) > 0 ) {
						str += n2+","+n1+")";
					} else {
						str += n1+","+n2+")";
					}
				} else {
					str += n1+")";
				}*
				for( i = 0; i < nodes.size()-1; i++ ) {
					str += nodes.get(i).toStringWoLengths()+",";
				}
				str += nodes.get(i).toStringWoLengths()+")";
			}
			
			if( meta != null && meta.length() > 0 ) {
				if( name != null && name.length() > 0 ) str += "'"+name+";"+meta+"'";
				else str += "'"+meta+"'";
			} else if( name != null && name.length() > 0 ) str += name;
			
			return str;*/
		}
		
		public String generateString( boolean wlen ) {
			String str = "";
			if( nodes.size() > 0 ) {
				str += "(";
				int i = 0;
				for( i = 0; i < nodes.size()-1; i++ ) {
					str += nodes.get(i).generateString(wlen)+",";
				}
				str += nodes.get(i).generateString(wlen)+")";
			}
			
			if( meta != null && meta.length() > 0 ) {
				//System.err.println("muuu " + meta);
				if( name != null && name.length() > 0 ) {
					str += name;
					if( color != null && color.length() > 0 ) str += "["+color+"]";
					if( infolist != null ) {
						for( String info : infolist ) {
							str += info;
						}
					}
					String framestr = this.getFrameString();
					if( framestr != null ) str += "{"+framestr+"}";
					str += ";"+meta; //"'"+name+";"+meta+"'";
				} else {
					if( color != null && color.length() > 0 ) str += "["+color+"]";
					if( infolist != null ) {
						for( String info : infolist ) {
							str += info;
						}
					}
					String framestr = this.getFrameString();
					if( framestr != null ) str += "{"+framestr+"}";
					str += ";"+meta; //"'"+meta+"'";
				}
			} else if( name != null && name.length() > 0 ) {
				str += name;
				if( color != null && color.length() > 0 ) str += "["+color+"]";
				if( infolist != null ) {
					for( String info : infolist ) {
						str += info;
					}
				}
				String framestr = this.getFrameString();
				if( framestr != null ) str += "{"+framestr+"}";
				/*if( fontsize != -1.0 ) {
					if( framesize == -1.0 ) str += "{"+fontsize+"}";
					else str += "{"+fontsize+" "+framesize+"}";
				}*/
			}
			
			if( wlen ) str += ":"+h;
			// change: if( color != null && color.length() > 0 ) str += ":"+color;
			//else str += ":0.0";
			
			return str;
		}
		
		public String toString() {
			return generateString( true );
		}
		
		public List<Node> getNodes() {
			return nodes;
		}
		
		public void addNode( Node node, double h ) {
			if( !nodes.contains( node ) ) {
				nodes.add( node );
				node.h = h;
				node.setParent( this );
			}
		}
		
		public void removeNode( Node node ) {
			nodes.remove( node );
			node.setParent( null );
			
			if( nodes.size() == 1 ) {
				Node parent = this.getParent();
				if( parent != null && parent.getNodes().remove( this ) ) {
					Node thenode = nodes.get(0);
					thenode.seth( thenode.geth() + this.geth() );
					
					String hi = thenode.getName();
					String lo = this.getName();
					
					if( hi != null && hi.length() > 0 && lo != null && lo.length() > 0 ) {
						try {
							double l = Double.parseDouble( lo );
							double h = Double.parseDouble( hi );
							
							if( l > h ) thenode.setName( lo );
						} catch( Exception e ) {};
					}
					
					parent.getNodes().add( thenode );
					thenode.setParent( parent );
				}
			}
		}
		
		public void setName( String newname ) {
			setName( newname, true );
		}
		
		public void addInfo( String info ) {
			if( infolist == null ) infolist = new ArrayList<String>();
			infolist.add( info );
		}
		
		public void clearInfo() {
			if( this.infolist != null ) this.infolist.clear();
		}
		
		public void setName( String newname, boolean parse ) {
			if( parse ) {
				if( newname != null ) {
					int fi = newname.indexOf(';');
					if( fi == -1 ) {						
						int ci = newname.indexOf("[");
						//int si = newname.indexOf("{");
						/*if( ci == -1 ) {
							if( si == -1 ) {
								this.setName( newname, false );
								this.setFontSize( -1.0 );
							} else {
								this.setName( newname.substring(0,si), false );
								int se = newname.indexOf("}",si+1);
								String mfstr = newname.substring(si+1,se);
								String[] mfsplit = mfstr.split(" ");
								this.setFontSize( Double.parseDouble( mfsplit[0] ) );
								if( mfsplit.length > 1 ) this.setFrameSize( Double.parseDouble( mfsplit[1] ) );
								if( mfsplit.length > 2 ) this.setFrameOffset( Double.parseDouble( mfsplit[2] ) );
							}
							this.setColor( null );
							clearInfo();
						} else {*/
						if( ci >= 0 ) {
							this.name = newname.substring(0,ci);
							int ce = newname.indexOf("]",ci+1);
							String metastr = newname.substring(ci+1,ce);
							
							int coli = metastr.indexOf("#");
							if( coli >= 0 ) {
								this.setColor( metastr.substring(coli, coli+7) );
							}
							int si = metastr.indexOf("{");
							if( si == -1 ) {
								this.setFontSize( -1.0 );
								
								ci = newname.indexOf( '[', ce+1 );
								while( ci != -1 ) {
									addInfo( newname.substring(ce+1, ci) );
									ce = newname.indexOf( ']', ci+1 );
									addInfo( newname.substring(ci, ce+1) );
									
									ci = newname.indexOf( '[', ce+1 );
								}
								int vi = Math.min(si, fi);
								if( vi > ce+1 ) addInfo( newname.substring(ce+1, vi) );
							} else {
								//this.name = newname.substring(0,Math.min(ci, si));
								/*int se = metastr.indexOf("}",si+1);
								
								ci = newname.indexOf( '[', ce+1 );
								while( ci != -1 && ci < si ) {
									addInfo( newname.substring(ce+1, ci) );
									ce = newname.indexOf( ']', ci+1 );
									addInfo( newname.substring(ci, ce+1) );
									
									ci = newname.indexOf( '[', ce+1 );
								}
								int vi = Math.min(si, fi);
								if( vi > ce+1 ) addInfo( newname.substring(ce+1, vi) );
								
								String mfstr = newname.substring(si+1,se);
								String[] mfsplit = mfstr.split(" ");
								this.setFontSize( Double.parseDouble( mfsplit[0] ) );
								if( mfsplit.length > 1 ) this.setFrameSize( Double.parseDouble( mfsplit[1] ) );
								if( mfsplit.length > 2 ) this.setFrameOffset( Double.parseDouble( mfsplit[2] ) );*/
							}
						} else this.name = newname;
						this.id = this.name;
						this.setMeta( null );
					} else {
						this.setName( newname.substring(0,fi) );
						this.setMeta( newname.substring(fi+1) );
					}
				} else {
					this.name = newname;
					try {
						double val = Double.parseDouble( newname );
						this.setBootstrap( val );
					} catch( Exception e ) {
						
					}
	 				this.setMeta( null );
					this.setColor( null );
					clearInfo();
				}
			} else {
				this.name = newname;
				/*this.id = newname;
				try {
					double val = Double.parseDouble( newname );
					this.setBootstrap( val );
				} catch( Exception e ) {
					
				}*/
			}
		}
		
		public String getFullname() {
			return "";
		}
		
		public String getName() {
			return name;
		}
		
		public double getFontSize() {
			return fontsize;
		}
		
		public double getFrameSize() {
			return framesize == -1.0 ? fontsize : framesize;
		}
		
		public double getFrameOffset() {
			return frameoffset;
		}
		
		public String getFrameString() {
			if( fontsize != -1.0 ) {
				if( framesize != -1.0 ) {
					if( frameoffset != -1.0 ) return fontsize+" " + framesize + " " + frameoffset;
					return fontsize+" "+framesize;
				} else {
					return ""+fontsize;
				}
			}
			
			return null;
		}
		
		public void setFontSize( double fs ) {
			this.fontsize = fs;
		}
		
		public void setFrameSize( double fs ) {
			this.framesize = fs;
		}
		
		public void setFrameOffset( double fo ) {
			this.frameoffset = fo;
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
		
		public void setColor( String color ) {
			this.color = color;
		}
		
		public int countSubnodes() {
			int total = 0;
			if( !isCollapsed() && nodes != null && nodes.size() > 0 ) {
				for( Node node : nodes ) {
					total += node.countLeaves();
				}
				total += nodes.size();
			} else total = 1;
			
			return total;
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
		
		public int countParentHeight() {
			int val = 0;
			Node parent = this.getParent();
			while( parent != null ) {
				val++;
				parent = parent.getParent();
			}
			return val;
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
	
	public Node getValidNode( Set<String> s, Node n ) {
		List<Node> subn = n.getNodes();
		if( subn != null ) {
			for( Node sn : subn ) {
				Set<String> ln = sn.getLeaveNames();
				if( ln.containsAll( s ) ) {
					return getValidNode( s, sn );
				}
			}
		}	
		return n;
	}
	
	public boolean isValidSet( Set<String> s, Node n ) {
		if( n.countLeaves() > s.size() ) {
			List<Node> subn = n.getNodes();
			if( subn != null ) {
				for( Node sn : subn ) {
					Set<String> lns = sn.getLeaveNames();
					int cntcnt = 0;
					for( String ln : lns ) {
						if( s.contains(ln) ) cntcnt++;
					}
					if( !(cntcnt == 0 || cntcnt == lns.size()) ) {
						return false; 
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public Node getConnectingParent( Node leaf1, Node leaf2 ) {
		Set<Node> ns = new HashSet<Node>();
		Node parent1 = leaf1.getParent();
		while( parent1 != null ) {
			ns.add( parent1 );
			parent1 = parent1.getParent();
		}
		
		Node parent2 = leaf2.getParent();
		while( parent2 != null ) {
			if( ns.contains( parent2 ) ) break;
			parent2 = parent2.getParent();
		}
		
		return parent2;
	}
	
	public double[] getDistanceMatrix( List<Node> leaves ) {
		double[] ret = new double[ leaves.size() * leaves.size() ];
		
		for( int i = 0; i < leaves.size(); i++ ) {
			ret[i+i*leaves.size()] = 0.0;
		}
		
		for( int i = 0; i < leaves.size(); i++ ) {
			for( int k = i+1; k < leaves.size(); k++ ) {
				Node leaf1 = leaves.get(i);
				Node leaf2 = leaves.get(k);
				Node parent = getConnectingParent(leaf1, leaf2);
				double val = 0.0;
				
				Node par = leaf1.getParent();
				while( par != parent ) {
					val += leaf1.geth();
					leaf1 = par;
					par = leaf1.getParent();
				}
				val += leaf1.geth();
				
				par = leaf2.getParent();
				while( par != parent ) {
					val += leaf2.geth();
					leaf2 = par;
					par = leaf2.getParent();
				}
				val += leaf2.geth();
				
				ret[i+k*leaves.size()] = val;
				ret[k+i*leaves.size()] = val;
			}
		}
		
		return ret;
	}
	
	public Set<String> getLeaveNames( Node node ) {
		Set<String>	ret = new HashSet<String>();
		
		List<Node> nodes = node.getNodes();
		if( nodes != null && nodes.size() > 0 ) {
			for( Node n : nodes ) {
				ret.addAll( getLeaveNames( n ) );
			}
		} else ret.add( node.getName() );
		
		return ret;
	}
	
	public List<Node> getLeaves( Node node ) {
		List<Node>	ret = new ArrayList<Node>();
		
		List<Node> nodes = node.getNodes();
		if( nodes != null && nodes.size() > 0 ) {
			for( Node n : nodes ) {
				ret.addAll( getLeaves( n ) );
			}
		} else ret.add( node );
		
		return ret;
	}
	
	public List<Node> getSubNodes( Node node ) {
		List<Node>	ret = new ArrayList<Node>();
		
		List<Node> nodes = node.getNodes();
		if( nodes != null ) {
			for( Node n : nodes ) {
				ret.add( n );
				ret.addAll( getSubNodes( n ) );
			}
		}
		
		return ret;
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
	
	public void recursiveAdd( String[] list, Node root, int i ) {
		Node father = new Node( list[i] );
		Node mother = new Node( list[i+1] );
		root.addNode(father, 1.0);
		root.addNode(mother, 1.0);
		
		if( i*2+1 < list.length ) recursiveAdd( list, father, i*2 );
		if( (i+1)*2+1 < list.length ) recursiveAdd( list, mother, (i+1)*2 );
	}
	
	public String parseNodeList( String nodeStr ) {
		String[] split = nodeStr.split(",");
		Node n = new Node( split[1] );
		recursiveAdd( split, n, 2 );
		this.setNode( n );
		return n.toString();
	}
	
	public void clearParentNames( Node node ) {
		if( node.getNodes() != null && node.getNodes().size() > 0 ) {
			node.setName("");
			for( Node n : node.getNodes() ) {
				clearParentNames( n );
			}
		}
	}
	
	public void setLoc( int newloc ) {
		this.loc = newloc;
	}
	
	public void init( String str, boolean inverse, Set<String> include, Map<String,Map<String,String>> mapmap, boolean collapse, Set<String> collapset, Map<String,String> colormap, boolean clearParentNodes ) {
		//super();
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
			
			this.setNode( resultnode );
		} /*else {
			System.err.println( str );
		}*/
	}
	
	public void collapseTreeAdvanced( Node node, Collection<String> collapset, boolean simple ) {
		if( node.nodes != null && node.nodes.size() > 0 ) {
			if( node.nodes.size() == 1 ) {
				Node parent = node.getParent();
				if( parent.getNodes().remove( node ) ) {
					Node thenode = node.nodes.get(0);
					thenode.seth( thenode.geth() + node.geth() );
					parent.getNodes().add( thenode );
				}
			}
			
			for( Node n : node.nodes ) {
				collapseTreeAdvanced( n, collapset, simple );
			}
			
			String test = null;
			int count = 0;
			
			boolean collapse = node.nodes.size() > 1;
			if( collapse ) {
				for( Node n : node.nodes ) {
					String nname = n.getName() != null ? n.getName() : "";
					if( collapset == null || collapset.isEmpty() ) {
						if( test == null ) {
							test = nname;
						} else if( test.length() == 0 || nname.length() == 0 || !nname.equals(test) ) { //!(nname.contains(test) || test.contains(nname)) ) {
							test = test.length() > nname.length() ? test : nname;
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
					
					String meta = n.getMeta();
					try {
						if( meta != null && meta.length() > 0 ) {
							int mi = Integer.parseInt( meta );
							count += mi;
						} else count++; 
					} catch( Exception e ) {
						count++;
					}
				}
			}
			
			if( collapse && (collapset == null || collapset.contains(test)) ) {
				node.nodes.clear();
				//node.nodes = null;
				//node.setMeta( Integer.toString(count) );
				node.setName( test+";"+Integer.toString(count) );
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
	
	public void nameParentNodes( Node node ) {
		if( node.nodes != null && node.nodes.size() > 0 ) {
			for( Node n : node.nodes ) {
				nameParentNodes( n );
			}
			boolean check = true;
			String sel = null;
			String col = null;
			for( Node n : node.nodes ) {
				if( n.getName() != null && n.getName().length() > 0 ) {
					if( sel == null ) {
						sel = n.getName();
						col = n.getColor();
					} else {
						if( !sel.equals( n.getName() ) ) {
							check = false;
							break;
						}
					}
				} else check = false;
			}
			if( check ) {
				for( Node n : node.nodes ) {
					if( n.nodes != null && n.nodes.size() > 0 ) {
						n.setName( null );
					}
				}
				String name = (col == null || col.length() == 0) ? sel : sel+"["+col+"]{1.0 3.0 1.00}";
				node.setName( name );
			}
		}
	}
	
	public void nameParentNodesMeta( Node node ) {
		if( node.nodes != null && node.nodes.size() > 0 ) {
			for( Node n : node.nodes ) {
				nameParentNodesMeta( n );
			}
			boolean check = true;
			String sel = null;
			//String col = null;
			for( Node n : node.nodes ) {
				int c1 = n.countMaxHeight();
				/*if( c1 > 4 && n.getMeta() != null && n.getMeta().contains("aquat") ) {
					System.err.println();
				}*/
				if( n.getMeta() != null && n.getMeta().length() > 0 ) {
					if( sel == null ) {
						sel = n.getMeta();
						int i1 = sel.indexOf('[');
						if( i1 == -1 ) i1 = sel.length();
						int i2 = sel.indexOf('{');
						if( i2 == -1 ) i2 = sel.length();
						int i = Math.min(i1, i2);
						sel = sel.substring(0, i);
						//col = n.getColor();
					} else {						
						String nmeta = n.getMeta();
						int i1 = nmeta.indexOf('[');
						if( i1 == -1 ) i1 = nmeta.length();
						int i2 = nmeta.indexOf('{');
						if( i2 == -1 ) i2 = nmeta.length();
						int i = Math.min(i1, i2);
						String str = nmeta.substring(0, i);
						
						if( !sel.equals( str ) ) {
							check = false;
							break;
						}
					}
				} else check = false;
			}
			if( check ) {
				for( Node n : node.nodes ) {
					if( n.nodes != null && n.nodes.size() > 0 ) {
						n.setMeta( "" );
					}
				}
				String meta = sel+"{1.5 2.0 1.10}"; //(col == null || col.length() == 0) ? sel : sel+"["+col+"]";
				node.setMeta( meta );
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
	
	public double rerootRecur( Node oldnode, Node newnode ) {
		for( Node res : oldnode.nodes ) {
			double b;
			if( res == newnode ) b = res.h;
			else b = rerootRecur( res, newnode );
			
			if( b != -1 ) {
				res.nodes.add( oldnode );
				oldnode.parent = res;
				
				double tmph = oldnode.h;
				//res.h = oldnode.h;
				oldnode.h = b;
				oldnode.nodes.remove( res );
				
				setNode( newnode );
				currentNode.countLeaves();
				
				return tmph;
			}
		}
		
		return -1;
	}
	
	public void recursiveReroot() {
		
	}
	
	public void reroot( Node newnode ) {		
		rerootRecur(currentNode, newnode);
		setNode( newnode );
		currentNode.countLeaves();
		
		/*double h = newnode.h;
		
		Node formerparent = newnode.getParent();
		if( formerparent != null ) {
			Node nextparent = formerparent.getParent();
			
			formerparent.nodes.remove( newnode );
			Node newroot = new Node();
			newroot.addNode( newnode, h/2.0 );
			
			Node child = formerparent;
			Node parent = nextparent;
			
			if( parent == null ) {
				for( Node nn : child.getNodes() ) {
					if( nn != child ) {
				//Node erm = child.getNodes().get(0) == newnode ? child.getNodes().get(1) : child.getNodes().get(0);
						newroot.addNode(nn, newnode.h+nn.h);
					}
				}
			} else {
				newroot.addNode( formerparent, h/2.0 );
			}
			
			while( parent != null ) {
				parent.nodes.remove( child );
				
				Node nparent = parent.getParent();
				if( nparent != null ) {
					child.addNode(parent, child.h);
				} else {
					//child.addNode(parent, child.h);
					
					for( Node nn : parent.getNodes() ) {
						if( nn != child ) {
						//Node erm = parent.getNodes().get(0) == child ? parent.getNodes().get(1) : parent.getNodes().get(0);
							child.addNode( nn, child.h+nn.h );
						}
					}
					break;
				}
				
				child = parent;
				parent = nparent;
			}
		
			//newparent.addNode( formerparent, h/2.0 );
			//newnode.setParent( newparent );
			
			currentNode = newroot;
			//console( currentNode.nodes.size() );
			currentNode.countLeaves();
		}*/
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
	
	public static void main(String[] args) {
		try {
			maintree( args );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void maintree( String[] args ) throws IOException {					
		byte[] bb = Files.readAllBytes( Paths.get("/Users/sigmar/777.tre") ); //"c:/sample.tree") ); //"c:/influenza.tree") );
		String str = new String( bb );
		String treestr = null;;
		
		if( str.startsWith("#") ) {
			int i = str.lastIndexOf("begin trees");
			if( i != -1 ) {
				i = str.indexOf('(', i);
				int l = str.indexOf(';', i+1);
				
				treestr = str.substring(i, l).replaceAll("[\r\n]+", "");
			}
		} else treestr = str.replaceAll("[\r\n]+", "");
		
		if( treestr != null ) {
			//System.err.println( treestr.substring( treestr.length()-10 ) );
			
			TreeUtil treeutil = new TreeUtil();
			treeutil.init( treestr, false, null, null, false, null, null, false );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			System.err.println( treeutil.getNode() );
			
		}
	}
	
	public void softReplaceNames( Node node, Map<String,String> namesMap ) {
		List<Node> nodes = node.getNodes();
		for( String key : namesMap.keySet() ) {
			//if( node.getName() != null && node.getName().length() > 0 ) 
			//	System.err.println( "blehehe " + node.getName() );
			if( node.getName() != null && node.getName().contains( key ) ) {
				node.name = namesMap.get( key );
			}
		}
			//if( namesMap.containsKey( node.getName() ) ) node.setName( namesMap.get(node.getName()) );
		for( Node n : nodes ) {
			softReplaceNames(n, namesMap);
		}
	}
	
	public void replaceNames( Node node, Map<String,String> namesMap ) {
		List<Node> nodes = node.getNodes();
		if( nodes == null || nodes.size() == 0 ) {
			if( namesMap.containsKey( node.getName() ) ) node.setName( namesMap.get(node.getName()) );
		} else {
			for( Node n : nodes ) {
				replaceNames(n, namesMap);
			}
		}
	}
	
	public void swapNamesMeta( Node node ) {
		List<Node> nodes = node.getNodes();
		if( nodes != null ) {
			for( Node n : nodes ) {
				swapNamesMeta( n );
			}
		}
		String meta = node.getMeta();
		String name = node.getName() == null ? "" : node.getName();
		name = node.getColor() == null ? name : (name + "["+node.getColor()+"]");
		if( node.infolist != null ) {
			for( String info : node.infolist ) name += info;
		}
		name = node.getFrameString() == null ? name : name + "{" + node.getFrameString() + "}";
		if( meta != null && meta.length() > 0 ) {
			if( name != null && name.length() > 0 ) {
				node.setName( meta+";"+name );
			} else {
				node.setName( meta );
			}
		} else {
			node.setName( ";"+name );
		}
	}
	
	public void replaceNamesMeta( Node node ) {
		List<Node> nodes = node.getNodes();
		if( nodes != null ) {
			for( Node n : nodes ) {
				replaceNamesMeta( n );
			}
		}
		String meta = node.getMeta();
		String name = node.getName() == null ? "" : node.getName();
		name = node.getColor() == null || node.getColor().length() == 0 ? name : (name + "["+node.getColor()+"]");
		if( node.infolist != null ) {
			for( String info : node.infolist ) name += info;
		}
		name = node.getFrameString() == null || node.getFrameString().length() == 0 ? name : name + "{" + node.getFrameString() + "}";
		if( meta != null && meta.length() > 0 ) {
			if( name != null && name.length() > 0 ) {
				node.setName( meta+";"+name );
			} else {
				node.setName( meta );
			}
		} else {
			node.setName( name );
		}
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
	public Node parseTreeRecursive( String str, boolean inverse ) {
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
				/*if( c == '\'' ) {
					while( end < str.length()-1 && n != '\'' ) {
						n = str.charAt(++end);
					}
					si = end-loc-1;
					//String code = str.substring( loc, end );
					//node.name = code.replaceAll("'", "");
					//loc = end+1;
				}*/
				
				boolean outsvig = true;
				boolean brakk = n == '[';
				//while( end < str.length()-1 && n != ',' && n != ')' ) {
				while( end < str.length()-1 && ( brakk || (n != ',' && n != ')' || !outsvig) ) ) {
					n = str.charAt(++end);
					if( n == '[' ) {
						brakk = true;
						//n = str.charAt(++end);
					} else if( n == ']' ) {
						brakk = false;
						//n = str.charAt(++end);
					} else if( outsvig && n == '(' ) {
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
					//int i = code.lastIndexOf("'");
					String name;
					/*if( i > 0 ) {
						split = code.substring(i, code.length()).split(":");
						name = code.substring(0, i+1);
					} else {
						split = code.split(":");
						name = split[0];
					}*/
					
					split = code.split(":");
					name = split[0];
					
					/*int coli = name.indexOf("[#");
					if( coli != -1 ) {
						int ecoli = name.indexOf("]", coli+2);
						node.color = name.substring(coli+1,ecoli);
						name = name.substring(0, coli);
					}
					
					int idx = name.indexOf(';');
					if( idx == -1 ) {
						node.name = name;
					} else {
						node.name = name.substring(0,idx);
						node.meta = name.substring(idx+1);
					}
					node.id = node.name;*/
					node.setName( name );
					//extractMeta( node, mapmap );
					
					/*if( split.length > 2 ) {
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
					}// else node.color = null;*/
					
					String dstr = split[1].trim();
					/*String dstr2 = "";
					if( dstr.contains("[") ) {
						int start = dstr.indexOf('[');
						int stop = dstr.indexOf(']');
						dstr2 = dstr.substring( start+1, stop );
						dstr = dstr.substring( 0, start );
					}*/
					
					try {
						node.h = Double.parseDouble( dstr );
						/*if( dstr2.length() > 0 ) {
							node.h2 = Double.parseDouble( dstr2 );
							if( node.name == null || node.name.length() == 0 ) {
								node.setName( dstr2 );
								/*node.name = dstr2; 
								node.id = node.name;*
							}
						}*/
					} catch( Exception e ) {
						System.err.println();
					}
					
					if( node.h < minh ) minh = node.h;
					if( node.h > maxh ) maxh = node.h;
					
					if( node.h2 < minh2 ) minh2 = node.h2;
					if( node.h2 > maxh2 ) maxh2 = node.h2;
				} else {
					node.setName( code );
					/*int idx = code.indexOf(';');
					if( idx == -1 ) {
						node.name = code;
					} else {
						node.name = code.substring(0,idx);
						node.meta = code.substring(idx+1);
					}
					//node.name = code; //code.replaceAll("'", "");
					node.id = node.name;*/
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
			/*if( n == '\'' ) {
				n = str.charAt(++end);
				while( end < str.length()-1 && n != '\'' ) {
					n = str.charAt(++end);
				}
				si = end-loc-1;
				//String code = str.substring( loc, end );
				//node.name = code.replaceAll("'", "");
				//loc = end+1;
			}*/
			
			boolean brakk = n == '[';
			while( end < str.length()-1 && ( brakk || (n != ',' && n != ')') ) ) {
				n = str.charAt(++end);
				if( n == '[' ) {
					brakk = true;
					//n = str.charAt(++end);
				} else if( n == ']' ) {
					brakk = false;
					//n = str.charAt(++end);
				} 
			}
			
			String code;
			if( n == ']' ) {
				code = str.substring( loc, end+1 );
			} else code = str.substring( loc, end );
			int ci = code.indexOf(":", si);
			if( ci != -1 ) {
				String[] split;
				int i = code.lastIndexOf("'");
				if( i > 0 ) {
					split = code.substring(i, code.length()).split(":");
					ret.setName( code.substring(0, i+1) );
				} else {
					split = code.split(":");
					ret.setName( split.length > 0 ? split[0] : "" );
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
				}// else ret.color = null;
				String dstr = split.length > 1 ? split[1].trim() : "0";
				/*String dstr2 = "";
				if( dstr.contains("[") ) {
					int start = split[1].indexOf('[');
					int stop = split[1].indexOf(']');
					dstr2 = dstr.substring( start+1, stop );
					dstr = dstr.substring( 0, start );
				}*/
				try {
					ret.h = Double.parseDouble( dstr );
					/*if( dstr2.length() > 0 ) {
						ret.h2 = Double.parseDouble( dstr2 );
						if( ret.name == null || ret.name.length() == 0 ) {
							ret.setName( dstr2 );
						}
					}*/
				} catch( Exception e ) {}
				if( ret.h < minh ) minh = ret.h;
				if( ret.h > maxh ) maxh = ret.h;
				if( ret.h2 < minh2 ) minh2 = ret.h2;
				if( ret.h2 > maxh2 ) maxh2 = ret.h2;
			} else {
				ret.setName( code.replaceAll("'", "") );
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

	public double nDistance(Node node1, Node node2) {
		double ret = 0.0;
		
		List<Set<String>>	nlist1 = new ArrayList<Set<String>>();
		node1.nodeCalc( nlist1 );
		
		List<Set<String>>	nlist2 = new ArrayList<Set<String>>();
		node2.nodeCalc( nlist2 );
		
		for( Set<String> s1 : nlist1 ) {
			boolean found = false;
			for( Set<String> s2 : nlist2 ) {
				if( s1.size() == s2.size() && s1.containsAll( s2 ) ) {
					found = true;
					break;
				}
			}
			if( !found ) ret += 1.0;
		}
		
		for( Set<String> s2 : nlist2 ) {
			boolean found = false;
			for( Set<String> s1 : nlist1 ) {
				if( s1.size() == s2.size() && s1.containsAll( s2 ) ) {
					found = true;
					break;
				}
			}
			if( !found ) ret += 1.0;
		}
		
		return ret;
	}
}
