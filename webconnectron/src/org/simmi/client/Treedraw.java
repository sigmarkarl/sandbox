package org.simmi.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.simmi.shared.Sequence;
import org.simmi.shared.TreeUtil;
import org.simmi.shared.TreeUtil.Node;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Treedraw implements EntryPoint {
	Canvas		canvas;
	Node		root;
	Node		selectedNode;
	Node[]		nodearray;
	TreeUtil	treeutil;
	boolean	center = false;
	int			equalHeight = 0;
	boolean	showscale = true;
	
	public void handleTree( TreeUtil treeutil ) {
		this.treeutil = treeutil;
		//treeutil.getNode().countLeaves()
		
		Node n = treeutil.getNode();
		if( n != null ) {
			root = n;
			drawTree( treeutil );
		}
	}
	
	double hchunk = 10.0;
	public void drawTree( TreeUtil treeutil ) {
		double minh = treeutil.getminh();
		double maxh = treeutil.getmaxh();
		
		double minh2 = treeutil.getminh2();
		double maxh2 = treeutil.getmaxh2();
		
		int ww = Window.getClientWidth();
		
		int leaves = root.getLeavesCount();
		int levels = root.countMaxHeight();
		
		nodearray = new Node[ leaves ];
		
		String treelabel = treeutil.getTreeLabel();
		
		int hsize = (int)(hchunk*leaves);
		if( treelabel != null ) hsize += 2*hchunk;
		if( showscale ) hsize += 2*hchunk;
		canvas.setSize((ww-10)+"px", (hsize+2)+"px");
		canvas.setCoordinateSpaceWidth( ww-10 );
		canvas.setCoordinateSpaceHeight( hsize+2 );
		
		boolean vertical = true;
		//boolean equalHeight = false;
		
		Treedraw.this.h = hchunk*leaves;
		Treedraw.this.w = ww-10;
		
		if( vertical ) {
			dh = Treedraw.this.h/leaves;
			dw = Treedraw.this.w/levels;
		} else {
			dh = Treedraw.this.h/levels;
			dw = Treedraw.this.w/leaves;
		}
		
		int starty = 10; //h/25;
		int startx = 10; //w/25;
		//GradientPaint shadeColor = createGradient( color, ny-k/2, h );
		//drawFramesRecursive( g2, resultnode, 0, 0, w/2, starty, paint ? shadeColor : null, leaves, equalHeight );
		
		ci = 0;
		//g2.setFont( dFont );
		
		//console( Double.toString( maxheight ) );
		//console( Double.toString( maxh-minh ) );
		//console( Double.toString(maxh2-minh2) );
		
		Context2d ctx = canvas.getContext2d();
		if( hchunk != 10.0 ) {
			String fontstr = (int)(5.0*Math.log(hchunk))+"px sans-serif";
			if( !fontstr.equals(ctx.getFont()) ) ctx.setFont( fontstr );
		}
		if( treelabel != null ) ctx.fillText( treelabel, 10, hchunk+2 );
		//console( "leaves " + leaves );
		//double	maxheightold = root.getMaxHeight();
		
		Node node = equalHeight > 0 ? getMaxNameLength( root, ctx, ww-30 ) : getMaxHeight( root, ctx, ww-30, true );
		if( node != null ) {
			double gh = getHeight(node);
			String name = node.getName();
			if( node.getMeta() != null ) name += " ("+node.getMeta()+")";
			double textwidth = ctx.measureText(name).getWidth();
			
			double maxheight = equalHeight > 0 ? (ww-30-textwidth) : (gh*(ww-30))/(ww-60-textwidth);
			if( equalHeight > 0 ) dw = maxheight/levels;
					
			if( vertical ) {
				drawFramesRecursive( ctx, root, 0, treelabel == null ? 0 : hchunk*2, startx, Treedraw.this.h/2, equalHeight, false, vertical, maxheight, 0 );
				ci = 0;
				if( center ) drawTreeRecursiveCenter( ctx, root, 0, treelabel == null ? 0 : hchunk*2, startx, Treedraw.this.h/2, equalHeight, false, vertical, maxheight );
				else drawTreeRecursive( ctx, root, 0, treelabel == null ? 0 : hchunk*2, startx, Treedraw.this.h/2, equalHeight, false, vertical, maxheight );
			} else {
				drawFramesRecursive( ctx, root, 0, 0, Treedraw.this.w/2, starty, equalHeight, false, vertical, maxheight, 0 );
				ci = 0;
				if( center ) drawTreeRecursiveCenter( ctx, root, 0, 0, Treedraw.this.w/2, starty, equalHeight, false, vertical, maxheight );
				else drawTreeRecursive( ctx, root, 0, 0, Treedraw.this.w/2, starty, equalHeight, false, vertical, maxheight );
			}
			
			if( showscale ) {
				Node n = getMaxHeight( root, ctx, ww, false );
				double h = n.getHeight();
				double wh = n.getCanvasX()-10;
				double ch = canvas.getCoordinateSpaceHeight();
				
				double nh = Math.pow( 10.0, Math.floor( Math.log10( h/5.0 ) ) );
				double nwh = wh*nh/h;
				
				ctx.beginPath();
				ctx.moveTo(10, ch );
				ctx.lineTo(10, ch-5 );
				ctx.lineTo(10+nwh, ch-5 );
				ctx.lineTo(10+nwh, ch );
				ctx.stroke();
				ctx.closePath();
				String htext = ""+nh;
				double sw = ctx.measureText( htext ).getWidth();
				ctx.fillText( htext, 10+(nwh-sw)/2.0, ch-8 );
			}
		}
	}
	
	public double getHeight( Node n ) {
		double h = n.geth();
		double d = h + ((n.getParent() != null) ? getHeight( n.getParent() ) : 0.0);
		return d;
	}
	
	public void recursiveLeavesGet( Node root, List<Node> leaves ) {
		List<Node> nodes = root.getNodes();
		if( nodes == null || nodes.size() == 0 ) {
			leaves.add( root );
		} else {
			for( Node n : nodes ) {
				recursiveLeavesGet( n, leaves );
			}
		}
	}
	
	public Node getMaxNameLength( Node root, Context2d ctx, int ww ) {
		List<Node>	leaves = new ArrayList<Node>();
		recursiveLeavesGet( root, leaves );
		
		Node sel = null;
		double max = 0.0;
		console( ""+leaves.size() );
		for( Node node : leaves ) {
			String name = node.getName();
			if( node.getMeta() != null ) name += " ("+node.getMeta()+")";
			TextMetrics tm = ctx.measureText( name );
			double tw = tm.getWidth();
			//double h = node.getHeight();
			//double val = h/(ww-tw);
			if( tw > max ) {
				max = tw;
				sel = node;
			}
		}
		
		return sel;
	}
	
	public Node getMaxHeight( Node root, Context2d ctx, int ww, boolean includetext ) {
		List<Node>	leaves = new ArrayList<Node>();
		recursiveLeavesGet( root, leaves );
		
		Node sel = null;
		double max = 0.0;
		
		if( includetext ) {
			for( Node node : leaves ) {
				String name = node.getName();
				if( node.getMeta() != null ) name += " ("+node.getMeta()+")";
				TextMetrics tm = ctx.measureText( name );
				double tw = tm.getWidth();
				double h = node.getHeight();
				
				double val = h/(ww-tw);
				if( val > max ) {
					max = val;
					sel = node;
				}
			}
		} else {
			for( Node node : leaves ) {
				double h = node.getHeight();
				if( h > max ) {
					max = h;
					sel = node;
				}
			}
		}
		
		/*if( sel != null ) {
			String name = sel.getName();
			if( sel.getMeta() != null ) name += " ("+sel.getMeta()+")";
			console( name );
		}*/
		
		return sel;
	}
	
	/*	double max = 0.0;
		List<Node>	nodes = root.getNodes();
		if( nodes == null || nodes.size() == 0 ) {
			TextMetrics tm = ctx.measureText( root.getName() );
			max = tm.getWidth();
		} else {
			for( Node n : root.getNodes() ) {
				double nmax = n.getMaxHeight();
				if( nmax > max ) max = nmax;
			}
		}
		return root.geth()+max;
	}*/
	
	public List<Sequence> importReader( String str ) throws IOException {
		List<Sequence> lseq = new ArrayList<Sequence>();
		int i = str.indexOf('>');
		int e = 0;
		String name = null;
		while( i != -1 ) {
			if( name != null ) {
				Sequence s = new Sequence( name, new StringBuilder( str.subSequence(e+1, i-1) ), null );
				s.checkLengths();
				lseq.add( s );
			}
			e = str.indexOf('\n',i);
			name = str.substring(i+1, e);
			i = str.indexOf('>', e);
		}
		if( name != null ) {
			Sequence s = new Sequence( name, new StringBuilder( str.subSequence(e+1, str.length()) ), null );
			s.checkLengths();
			lseq.add( s );
		}
		return lseq;
	}
	
	public void handleText( String str ) {
		if( str.startsWith("#") ) {
			int i = str.lastIndexOf("tree");
			if( i != -1 ) {
				i = str.indexOf('(', i);
				int l = str.indexOf(';', i+1);
				
				Map<String,String> namemap = new HashMap<String,String>();
				int t = str.indexOf("translate");
				int n = str.indexOf("\n", t);
				int c = str.indexOf(";", n);
				
				String treelist = str.substring(n+1, c);
				String[] split = treelist.split(",");
				for( String name : split ) {
					String trim = name.trim();
					int v = trim.indexOf(' ');
					namemap.put( trim.substring(0, v), trim.substring(v+1) );
				}
				
				String tree = str.substring(i, l).replaceAll("[\r\n]+", "");
				TreeUtil	treeutil = new TreeUtil( tree, false, null, null, false, null, null, false );
				treeutil.replaceNames( treeutil.getNode(), namemap );
				handleTree( treeutil );
				
			}
		} else if( str.startsWith(">") ) {
			final TreeUtil treeutil = new TreeUtil();
			try {
				final List<Sequence> lseq = importReader( str );
				
				CheckBox egCheck = new CheckBox("Exclude gaps");
				CheckBox btCheck = new CheckBox("Bootstrap");
				CheckBox ctCheck = new CheckBox("Jukes-cantor");
				ctCheck.setValue( true );
				
				final DialogBox db = new DialogBox();
				VerticalPanel	dbvp = new VerticalPanel();
				dbvp.add( egCheck );
				dbvp.add( btCheck );
				dbvp.add( ctCheck );
				
				db.setModal( true );
				HorizontalPanel hp = new HorizontalPanel();
				Button closeButton = new Button("Ok");
				closeButton.addClickHandler( new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						db.hide();
					}
				});
				hp.add( closeButton );
				hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
				dbvp.add( hp );
				
				db.add( dbvp );
				db.center();
				
				final boolean excludeGaps = egCheck.getValue();
				final boolean bootstrap = btCheck.getValue();
				final boolean cantor = ctCheck.getValue();
				
				db.addCloseHandler( new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						int start = Integer.MIN_VALUE;
						int end = Integer.MAX_VALUE;
						
						for( Sequence seq : lseq ) {
							if( seq.getRealStart() > start ) start = seq.getRealStart();
							if( seq.getRealStop() < end ) end = seq.getRealStop();
						}
						
						double[]	dvals = new double[ lseq.size()*lseq.size() ];
						Sequence.distanceMatrixNumeric(lseq, dvals, start, end, excludeGaps, false, cantor);
						
						List<String>	names = new ArrayList<String>();
						for( Sequence seq : lseq ) {
							names.add( seq.getName() );
						}
						Node n = treeutil.neighborJoin( dvals, names );
						
						if( bootstrap ) {
							Comparator<Node>	comp = new Comparator<TreeUtil.Node>() {
								@Override
								public int compare(Node o1, Node o2) {
									String c1 = o1.toStringWoLengths();
									String c2 = o2.toStringWoLengths();
									
									return c1.compareTo( c2 );
								}
							};
							treeutil.arrange( n, comp );
							String tree = n.toStringWoLengths();
							
							for( int i = 0; i < 100; i++ ) {
								Sequence.distanceMatrixNumeric( lseq, dvals, start, end, excludeGaps, true, cantor );
								Node nn = treeutil.neighborJoin(dvals, names);
								treeutil.arrange( nn, comp );
								treeutil.compareTrees( tree, n, nn );
								
								//String btree = nn.toStringWoLengths();
								//System.err.println( btree );
							}
							treeutil.appendCompare( n );
						}
						treeutil.setNode( n );
						handleTree( treeutil );
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if( !str.startsWith("(") ) {
			TreeUtil treeutil = new TreeUtil();
			int len = 0;
			List<String> names;
			double[] dvals;
			if( str.startsWith(" ") || str.startsWith("\t") ) {
				names = new ArrayList<String>();
				String[] lines = str.split("\n");
				len = Integer.parseInt( lines[0].trim() );
				dvals = new double[ len*len ];
				int m = 0;
				int u = 0;
				for( int i = 1; i < lines.length; i++ ) {
					String line = lines[i];
					String[] ddstrs = line.split("[ \t]+");
					if( !line.startsWith(" ") ) {
						m++;
						u = 0;
						
						//int si = ddstrs[0].indexOf('_');
						//String name = si == -1 ? ddstrs[0] : ddstrs[0].substring( 0, si );
						//console( "name: " + name );
						
						String name = ddstrs[0];
						names.add( name );
					}
					if( ddstrs.length > 2 ) {
						for( int k = 1; k < ddstrs.length; k++ ) {
							int idx = (m-1)*len+(u++);
							if( idx < dvals.length ) dvals[idx] = Double.parseDouble(ddstrs[k]);
							else console( m + " more " + u );
						}
					}
				}
			} else {
				String[] lines = str.split("\n");
				names = Arrays.asList( lines[0].split("\t") );
				len = names.size();
				dvals = new double[len*len];
				for( int i = 1; i < lines.length; i++ ) {
					String[] ddstrs = lines[i].split("\t");
					if( ddstrs.length > 1 ) {
						int k = 0;
						for( String ddstr : ddstrs ) {
							dvals[(i-1)*len+(k++)] = Double.parseDouble(ddstr);
						}
					}
				}
			}
			Node n = treeutil.neighborJoin( dvals, names );
			treeutil.setNode( n );
			//console( treeutil.getNode().toString() );
			handleTree( treeutil );
		} else {
			String tree = str.replaceAll("[\r\n]+", "");
			TreeUtil	treeutil = new TreeUtil( tree, false, null, null, false, null, null, false );
			handleTree( treeutil );
		}
	}
	
	public native String handleFiles( Element ie, int append ) /*-{	
		//$wnd.console.log('dcw');
		var hthis = this;
		file = ie.files[0];
		var reader = new FileReader();
		reader.onload = function(e) {
			hthis.@org.simmi.client.Treedraw::handleText(Ljava/lang/String;)(e.target.result);
		};
		reader.onerror = function(e) {
	  		$wnd.console.log("error", e);
	  		$wnd.console.log(e.getMessage());
		};
		$wnd.console.log('befreadastext');
		reader.readAsText( file, "utf8" );
		$wnd.console.log('afterreadastext');
	}-*/;
	
	public native void click( JavaScriptObject e ) /*-{
		e.click();
	}-*/;
	
	public native void postParent( String from ) /*-{
		var s = this;
		$wnd.from = from;
		$wnd.handleText = function( str ) {
			s.@org.simmi.client.Treedraw::handleText(Ljava/lang/String;)( str );
		}
	
		//var s = this;
		//		$wnd.addEventListener('message',function(event) {
		//			$wnd.console.log('message received from webfasta');
		//			if(event.origin == 'http://'+from+'.appspot.com') {
		//				$wnd.console.log('correct webfasta origin');
		//				s.@org.simmi.client.Treedraw::handleText(Ljava/lang/String;)( event.data );
		//			}
		//		});
			
		//var loadHandler = function(event){
		//	$wnd.console.log('sending message to webfasta');
		//	event.currentTarget.opener.postMessage('ready','http://webfasta.appspot.com');
		//}
		//window.addEventListener('DOMContentLoaded', loadHandler, false);
		$wnd.opener.postMessage('ready','http://'+from+'.appspot.com');
	}-*/;
	
	public void openFileDialog( final int append ) {		
		final DialogBox	db = new DialogBox();
		db.setText("Open file ...");
		
		//HorizontalPanel	vp = new HorizontalPanel();
		final FormPanel	form = new FormPanel();
		form.setAction("/");
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);
	    
		//form.add( vp );*/
		final FileUpload	file = new FileUpload();
		//file.
		//file.getElement().setAttribute("accept", "");
		file.getElement().setId("fasta");
	
		form.add( file );
		db.add( form );
		/*file.fireEvent( GwtEvent)
		
		//SubmitButton	submit = new SubmitButton();	
		//submit.setText("Open");*/
		
		file.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handleFiles( file.getElement(), append );
				db.hide();
			}
		});
		
		db.setAutoHideEnabled( true );
		db.center();
		//file.fireEvent( new ClickEvent(){} );
		click( file.getElement() );
	}
	
	public void selectRecursive( Node node, boolean select ) {
		if( node != null ) {
			node.setSelected( select );
			if( node.getNodes() != null ) for( Node n : node.getNodes() ) {
				selectRecursive( n, select );
			}
		}
	}
	
	public void recursiveNodeCollapse( Node node, double x, double y ) {
		if( node != null ) {
			if( Math.abs( node.getCanvasX()-x ) < 5 && Math.abs( node.getCanvasY()-y ) < 5 ) {
				node.setCollapsed( node.isCollapsed() ? null : "collapsed" );
			} else {
				for( Node n : node.getNodes() ) {
					recursiveNodeCollapse( n, x, y );
				}
			}
		}
	}
	
	public Node recursiveReroot( Node node, double x, double y ) {
		Node ret = null;
		if( node != null ) {
			if( Math.abs( node.getCanvasX()-x ) < 5 && Math.abs( node.getCanvasY()-y ) < 5 ) {
				ret = node;
			} else {
				for( Node n : node.getNodes() ) {
					Node pot = recursiveReroot( n, x, y );
					if( pot != null ) {
						ret = pot;
						break;
					}
				}
			}
		}
		return ret;
	}
	
	public Node recursiveNodeClick( Node node, double x, double y, int recursive ) {
		Node ret = null;
		if( node != null ) {
			if( recursive > 1 || (Math.abs( node.getCanvasX()-x ) < 5 && Math.abs( node.getCanvasY()-y ) < 5) ) {
				ret = node;
				for( int i = 0; i < node.getNodes().size()/2; i++ ) {
					Node n = node.getNodes().get(i);
					node.getNodes().set(i, node.getNodes().get(node.getNodes().size()-1-i) );
					node.getNodes().set(node.getNodes().size()-1-i, n);
				}
				
				if( recursive > 0 ) {
					for( Node n : node.getNodes() ) {
						recursiveNodeClick( n, x, y, recursive+1 );
					}
				}
			} else {
				for( Node n : node.getNodes() ) {
					Node res = recursiveNodeClick( n, x, y, recursive );
					if( res != null ) ret = res;
				}
			}
		}
		return ret;
	}
	
	public Node findSelectedNode( Node node, double x, double y ) {
		Node ret = null;
		if( node != null ) {
			if( (Math.abs( node.getCanvasX()-x ) < 5 && Math.abs( node.getCanvasY()-y ) < 5) ) {
				ret = node;
			} else {
				for( Node n : node.getNodes() ) {
					Node res = findSelectedNode( n, x, y );
					if( res != null ) ret = res;
				}
			}
		}
		return ret;
	}
	
	public native String encode( String input ) /*-{
		var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;
	
		input = this.@org.simmi.client.Treedraw::_utf8_encode(Ljava/lang/String;)(input);
	
		while (i < input.length) {
	
			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);
	
			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;
	
			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}
	
			output = output +
			_keyStr.charAt(enc1) + _keyStr.charAt(enc2) +
			_keyStr.charAt(enc3) + _keyStr.charAt(enc4);
	
		}
	
		return output;
	}-*/;
	
	private native String _utf8_encode( String string ) /*-{
		string = string.replace(/\r\n/g,"\n");
		var utftext = "";
	
		for (var n = 0; n < string.length; n++) {
	
			var c = string.charCodeAt(n);
	
			if (c < 128) {
				utftext += String.fromCharCode(c);
			}
			else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}
	
		}
	
		return utftext;
	}-*/;

	public native String decode( String input ) /*-{
		var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;
	
		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
	
		while (i < input.length) {
			console.log( i );
			
			enc1 = _keyStr.indexOf(input.charAt(i++));
			enc2 = _keyStr.indexOf(input.charAt(i++));
			enc3 = _keyStr.indexOf(input.charAt(i++));
			enc4 = _keyStr.indexOf(input.charAt(i++));
	
			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;
	
			output = output + String.fromCharCode(chr1);
	
			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}
		}
	
		output = this.@org.simmi.client.FacebookTree::_utf8_decode(Ljava/lang/String;)(output);
	
		return output;
	}-*/;

	private native String _utf8_decode( String utftext ) /*-{
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;
	
		while ( i < utftext.length ) {
			c = utftext.charCodeAt(i);
	
			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			}
			else if((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i+1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			}
			else {
				c2 = utftext.charCodeAt(i+1);
				c3 = utftext.charCodeAt(i+2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}
	
		}
	
		return string;
	}-*/;
	
	public void save( String treestr ) {
		String base64tree = encode( treestr );
		//String base64tree = new String( Base64.encodeBase64( treestr.getBytes() ) );
		Anchor	anchor = new Anchor();
		anchor.setHref( "data:text/plain;filename:tmp.ntree;base64,"+base64tree );
		click( anchor.getElement() );
	}
	
	PopupPanel	popup;
	int 	npx;
	int		npy;
	int		px;
	int		py;
	boolean 		toggle = false;
	boolean 		shift = false;
	boolean 		mousedown = false;
	boolean			rerooting = false;
	
	public void recursiveMarkings( Node node ) {
		List<Node> nodes = node.getNodes();
		if( nodes != null && nodes.size() > 0 ) {
			node.setName( "" );
			node.setMeta( "" );
			for( Node newnode : nodes ) {
				recursiveMarkings( newnode );
			}
		}
	}
	
	public void omitLast( Node node, int harshness ) {
		List<Node> nodes = node.getNodes();
		if( nodes != null && nodes.size() > 0 ) {
			for( Node newnode : nodes ) {
				omitLast( newnode, harshness );
			}
		} else {
			String nname = node.getName();
			if( harshness == 2 ) {
				int li = nname.indexOf('_');
				if( li != -1 ) node.setName( nname.substring(0,li) );
			} else if( harshness == 1 ) {
				int li = nname.lastIndexOf('-');
				if( li != -1 ) {
					if( nname.charAt(li-3) == 'U' && nname.charAt(li-2) == 'S' && nname.charAt(li-1) == 'A' ) {
						int val = nname.indexOf('_', li+1);
						node.setName( nname.substring( 0, Math.min(nname.length(), val == -1 ? nname.length() : val) ) );
					} else node.setName( nname.substring(0,li) );
				}
			} else {
				int li = nname.lastIndexOf('_');
				if( li != -1 ) node.setName( nname.substring(0,li) );
			}
		}
	}
	
	public void recursiveZero( Node node ) {
		List<Node> nodes = node.getNodes();
		if( nodes != null ) {
			for( Node newnode : nodes ) {
				recursiveZero( newnode );
			}
		}
		node.seth( Math.max( node.geth(), 0.0 ) );
		node.seth2( Math.max( node.geth2(), 0.0 ) );
	}
	
	@Override
	public void onModuleLoad() {
		RootPanel	rp = RootPanel.get("canvas");
		/*rp.addDomHandler( new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, ContextMenuEvent.getType());*/
		
		popup = new PopupPanel( true );
		final MenuBar	menu = new MenuBar( true );
		popup.add( menu );
		
		menu.addItem( "Save tree", new Command() {
			@Override
			public void execute() {
				save( root.toString() );
				popup.hide();
			}
		});
		
		canvas = Canvas.createIfSupported();
		
		Style s = rp.getElement().getStyle();
		s.setBorderWidth(0.0, Unit.PX);
		s.setPadding(0.0, Unit.PX);
		s.setMargin(0.0, Unit.PX);
		
		int w = Window.getClientWidth();
		int h =180; //Window.getClientHeight();
		canvas.setSize(w+"px", h+"px");
		
		canvas.addDropHandler( new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				String str = event.getData("text/plain");
				handleText( str );
				
				//drawTreeRecursive( canvas.getContext2d(), treeutil.getNode(), 10, 10, 10, 10, false, false, true, treeutil.getminh(), treeutil.getmaxh());
			}
		});
		canvas.addDragHandler( new DragHandler() {
			@Override
			public void onDrag(DragEvent event) {
				console( "drag" );
				
				event.setData("text/plain", root.toString());
			}
		});
		canvas.addDragEnterHandler( new DragEnterHandler() {
			@Override
			public void onDragEnter(DragEnterEvent event) {
				console( "enter" );
			}
		});
		canvas.addDragLeaveHandler( new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) { console( "leave" ); }
		});
		canvas.addDragStartHandler( new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) { console( "start" ); }
		});
		canvas.addDragEndHandler( new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) { console( "end" ); }
		});
		canvas.addDragOverHandler( new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) { console( "over" ); }
		});
		canvas.addDoubleClickHandler( new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if( event.isShiftKeyDown() ) {
					int x = event.getX();
					int y = event.getY();
					
					canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
					if( !rerooting ) {
						rerooting = true;
						
						Node newroot = recursiveReroot( root, x, y );
						if( newroot != null ) {
							newroot.setParent( null );
							treeutil.reroot(root, newroot);
							root = newroot;
						}
						
						rerooting = false;
					}
					if( treeutil != null ) drawTree( treeutil );
				} else {
					openFileDialog( 0 );
				}
			}
		});
		canvas.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				int x = event.getX();
				int y = event.getY();
				
				canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
				
				toggle = false;
				px = event.getX();
				py = event.getY();
				npx = px;
				npy = py;
				shift = event.isShiftKeyDown();
				int nativebutton = event.getNativeButton();
				
				if( nativebutton == NativeEvent.BUTTON_RIGHT ) {
					/*mousedown = false;
					popup.setPopupPosition(px, py);
					popup.show();*/
				} else {
					if( event.isControlKeyDown() ) {
						/*canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
						Node newroot = recursiveReroot( root, x, y );
						if( newroot != null ) {
							newroot.setParent( null );
							treeutil.setNode( newroot );
							//treeutil.reroot(root, newroot);
							root = newroot;
							root.seth( 0.0 );
							root.seth2( 0.0 );
						}
						//recursiveNodeCollapse( root, x, y );
						//root.countLeaves();*/
						
						recursiveNodeClick( root, x, y, 1 );
					} else if( shift ) {
						recursiveNodeClick( root, x, y, 0 );
					} else {
						selectedNode = findSelectedNode( root, x, y );
						if( selectedNode != null ) selectRecursive( selectedNode, !selectedNode.isSelected() );
						/*if( nodearray != null ) {
							int y = event.getY();
							int i = (nodearray.length*y)/canvas.getCoordinateSpaceHeight();
							final Node n = nodearray[i];
							
							int ci = n.getName().indexOf(",");
							String fname = ci > 0 ? n.getName().substring(1,ci) : n.getName();
							RequestBuilder rq = new RequestBuilder( RequestBuilder.POST, "http://130.208.252.7/cgi-bin/blast");
							try {
								rq.sendRequest( fname, new RequestCallback() {
									@Override
									public void onResponseReceived(Request request, Response response) {
										DialogBox	d = new DialogBox();
										d.getCaption().setText("NCBI 16SMicrobial blast");
										d.setAutoHideEnabled( true );
										
										TextArea	ta = new TextArea();
										ta.setSize(800+"px", 600+"px");
										ta.setText( response.getText() );
										d.add( ta );
										
										d.center();
									}
									
									@Override
									public void onError(Request request, Throwable exception) {
										console( exception.getMessage() );
									}
								} );
							} catch (RequestException e) {
								e.printStackTrace();
							}				
						}*/
					}
				}
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		canvas.addKeyPressHandler( new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char c = event.getCharCode();
				int keycode = event.getNativeEvent().getKeyCode();
				if( c == 'a' || c == 'A' ) {
					String[] ts = new String[] {"T.unknown", "T.composti", "T.rehai", "T.yunnanensis", "T.kawarayensis", "T.scotoductus", "T.thermophilus", "T.eggertsoni", "T.islandicus", "T.igniterrae", "T.brockianus", "T.aquaticus", "T.oshimai", "T.filiformis", "T.antranikianii"};
					Collection<String> cset = c == 'A' ? new HashSet<String>( Arrays.asList(ts) ) : null;
					treeutil.collapseTreeAdvanced(root, cset);
					root.countLeaves();
				} else if( c == 'm' || c == 'M' ) {
					recursiveMarkings( root );
					//canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
					//Node newroot = recursiveReroot( root, x, y );
					//selectedNode.setParent( null );
					//treeutil.setNode( selectedNode );
					//treeutil.reroot(root, newroot);
					//root = selectedNode;
					//root.seth( 0.0 );
					//root.seth2( 0.0 );
				} else if( c == 'z' || c == 'Z' ) {
					recursiveZero( root );
				} else if( c == 'o' || c == 'O' ) {
					omitLast( selectedNode != null ? selectedNode : root, 2 );
				} else if( c == 'l' || c == 'l' ) {
					omitLast( selectedNode != null ? selectedNode : root, 1 );
				} else if( c == 'k' || c == 'K' ) {
					omitLast( selectedNode != null ? selectedNode : root, 0 );
				} else if( selectedNode != null ) {
					if( c == 'c' || c == 'C' ) {
						selectedNode.setCollapsed( selectedNode.isCollapsed() ? null : "collapsed" );
						root.countLeaves();
					} else if( c == 'd' || c == 'D' || keycode == KeyCodes.KEY_DELETE ) {
						Node parent = selectedNode.getParent();
						if( parent != null ) parent.removeNode( selectedNode );
						selectedNode = null;
						root.countLeaves();
					} else if( c == 'e' || c == 'E' || c == '\r' ) {
						event.stopPropagation();
						event.preventDefault();
						
						final TextBox	text = new TextBox();
						text.setText( selectedNode.getName() );
						
						final PopupPanel	pp = new PopupPanel();
						pp.add( text );
						pp.setPopupPosition( (int)selectedNode.getCanvasX(), (int)selectedNode.getCanvasY()+canvas.getAbsoluteTop()-5 );
						pp.setAutoHideEnabled( true );
						pp.show();
						
						final Boolean[] b = new Boolean[1];
						b[0] = true;
						text.addKeyDownHandler( new KeyDownHandler() {
							@Override
							public void onKeyDown(KeyDownEvent event) {
								int c = event.getNativeEvent().getCharCode();
								int key = event.getNativeKeyCode();
								if( key == KeyCodes.KEY_ESCAPE ) {
									b[0] = false;
									pp.hide();
								} else if( key == KeyCodes.KEY_ENTER || key == 18 || c == '\n' || c == '\r' ) {
									pp.hide();
								}
							}
						});
						/*text.addKeyPressHandler( new KeyPressHandler() {
							@Override
							public void onKeyPress(KeyPressEvent event) {
								char c = event.getCharCode();
								int  key = event.getNativeEvent().getKeyCode();
								if( c == '\n' || c == '\r' ) {
									if( text.getText().length() == 0 ) {
										text.setText( selectedNode.getName() );
										text.selectAll();
									} else {
										pp.hide();
									}
								} else if( key == KeyCodes.KEY_ESCAPE ) {
									pp.hide();
								} else console( Character.toString( c ) );
							}
						});*/
						pp.addCloseHandler( new CloseHandler<PopupPanel>() {
							@Override
							public void onClose(CloseEvent<PopupPanel> event) {
								if( b[0] ) {
									selectedNode.setName( text.getText() );
									if( treeutil != null ) drawTree( treeutil );
								}
							}
						});
						
						text.setText( selectedNode.getName() );
						text.selectAll();
						//text.setFocus( true );
						/*selectedNode.getParent().removeNode( selectedNode );
						selectedNode = null;
						root.countLeaves();*/
					} else if( c == 'r' || c == 'R' ) {
						//canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
						//Node newroot = recursiveReroot( root, x, y );
						selectedNode.setParent( null );
						treeutil.setNode( selectedNode );
							//treeutil.reroot(root, newroot);
						root = selectedNode;
						root.seth( 0.0 );
						root.seth2( 0.0 );
					} else if( c == 'i' || c == 'I' ) {
						invertSelectionRecursive( root );
					}
				}
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		
		//console( canvas.getOffsetWidth() + "  " + canvas.getOffsetHeight() );
		canvas.setCoordinateSpaceWidth( w );
		canvas.setCoordinateSpaceHeight( h );
		Context2d context = canvas.getContext2d();
		
		String str = "Drop text in distance matrix, aligned fasta or newick tree format to this canvas";
		TextMetrics tm = context.measureText( str );
		context.fillText(str, (w-tm.getWidth())/2.0, h/2.0-8.0);
		str = "Double click to open file dialog";
		tm = context.measureText( str );
		context.fillText(str, (w-tm.getWidth())/2.0, h/2.0+8.0);
		
		final Anchor	treeAnchor = new Anchor("tree");
		treeAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				treeAnchor.setHref( "data:text/plain;base64,"+encode(root.toString()) );
			}
		});
		final Anchor	imageAnchor = new Anchor("image");
		imageAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				imageAnchor.setHref( canvas.toDataUrl() );
			}
		});
		final Anchor	sampleAnchor = new Anchor("sample");
		sampleAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "sample.tree");
				try {
					rb.sendRequest("", new RequestCallback() {
						@Override
						public void onResponseReceived(Request request, Response response) {
							handleText( response.getText() );
						}
						
						@Override
						public void onError(Request request, Throwable exception) {
							console( exception.getMessage() );
						}
					});
				} catch (RequestException e) {
					e.printStackTrace();
				}
			}
		});
		final Anchor	zoominAnchor = new Anchor("in");
		zoominAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hchunk *= 1.25;
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		final Anchor	zoomoutAnchor = new Anchor("out");
		zoomoutAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hchunk *= 0.8;
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		
		RootPanel	help = RootPanel.get("help");
		VerticalPanel	choicePanel = new VerticalPanel();
		choicePanel.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		
		HorizontalPanel	bc = new HorizontalPanel();
		HTML	bctext = new HTML( "Center on" );
		RadioButton	branch = new RadioButton("bc", "branch");
		branch.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				center = event.getValue();
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		RadioButton	cluster = new RadioButton("bc", "cluster");
		cluster.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				center = !event.getValue();
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		cluster.setValue( true );
		bc.add( bctext );
		bc.add( branch );
		bc.add( cluster );
		
		HorizontalPanel	eqhp = new HorizontalPanel();
		RadioButton	uselen = new RadioButton("eq", "Use lengths. ");
		uselen.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				equalHeight = 0;
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		HTML	eqtext = new HTML( "Equal" );
		RadioButton	equidist = new RadioButton("eq", "distance");
		equidist.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				equalHeight = 2;
				showscale = false;
				if( treeutil != null ) {
					drawTree( treeutil );
				}
			}
		});
		RadioButton	equidep = new RadioButton("eq", "depth");
		equidep.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				equalHeight = 1;
				showscale = false;
				if( treeutil != null ) {
					drawTree( treeutil );
				}
			}
		});
		uselen.setValue( true );
		eqhp.add( uselen );
		eqhp.add( eqtext );
		eqhp.add( equidist );
		eqhp.add( equidep );
		
		HorizontalPanel	hp = new HorizontalPanel();
		hp.setSpacing(5);
		HTML html = new HTML("Download as");
		hp.add( html );
		hp.add( treeAnchor );
		html = new HTML("as");
		hp.add( html );
		hp.add( imageAnchor );
		
		html = new HTML(". View in");
		hp.add( html );
		Anchor td = new Anchor("3d");
		td.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showTree( treeutil.currentNode.toString() );
			}
		});
		hp.add( td );
		
		html = new HTML(". Run");
		hp.add( html );
		hp.add( sampleAnchor );
		
		html = new HTML(". Zoom");
		hp.add( html );
		hp.add( zoominAnchor );
		html = new HTML(" / ");
		hp.add( html );
		hp.add( zoomoutAnchor );
		
		HTML arhtml = new HTML( "Arrange by " );
		Button arbl = new Button( "branch length" );
		arbl.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Comparator<Node>	comp = new Comparator<TreeUtil.Node>() {
					@Override
					public int compare(Node o1, Node o2) {
						if( o1.geth() > o2.geth() ) return 1;
						else if( o1.geth() == o2.geth() ) return 0;
						
						return -1;
					}
				};
				treeutil.arrange( treeutil.getNode(), comp );
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		Button arcs = new Button( "cluster size" );
		arcs.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Comparator<Node>	comp = new Comparator<TreeUtil.Node>() {
					@Override
					public int compare(Node o1, Node o2) {
						int c1 = o1.countLeaves();
						int c2 = o2.countLeaves();
						
						if( c1 > c2 ) return 1;
						else if( c1 == c2 ) return 0;
						
						return -1;
					}
				};
				treeutil.arrange( treeutil.getNode(), comp );
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		Button titl = new Button( "title" );
		titl.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Comparator<Node>	comp = new Comparator<TreeUtil.Node>() {
					@Override
					public int compare(Node o1, Node o2) {
						String c1 = o1.toStringWoLengths();
						String c2 = o2.toStringWoLengths();
						
						return c1.compareTo( c2 );
					}
				};
				treeutil.arrange( treeutil.getNode(), comp );
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		HorizontalPanel	arrangehp = new HorizontalPanel();
		arrangehp.setSpacing(5);
		arrangehp.add( arhtml );
		arrangehp.add( arbl );
		arrangehp.add( arcs );
		arrangehp.add( titl );
		
		HorizontalPanel labhp = new HorizontalPanel();
		labhp.setSpacing( 5 );
		CheckBox scalecheck = new CheckBox("Scale");
		scalecheck.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				showscale = event.getValue();
				drawTree( treeutil );
			}
		});
		scalecheck.setValue( true );
		CheckBox labcheck = new CheckBox("Label");
		final TextBox	label = new TextBox();
		label.setText("A tree");
		label.setEnabled( false );
		labcheck.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean b = event.getValue();
				label.setEnabled( b );
				treeutil.setTreeLabel( b ? label.getText() : null );
				drawTree( treeutil );
			}
		});
		
		labhp.add( scalecheck );
		labhp.add( labcheck);
		labhp.add( label );
		
		choicePanel.add( arrangehp );
		choicePanel.add( bc );
		choicePanel.add( eqhp );
		choicePanel.add( labhp );
		choicePanel.add( hp );
		help.add( choicePanel );
		//help.add
		
		rp.add( canvas );
		
		String dist = Window.Location.getParameter("dist");
		if( dist != null ) {
			String dmat = decode( dist );
			handleText( dmat );
		}
		console( Window.Location.getParameterMap().keySet().toString() );
		if( Window.Location.getParameterMap().keySet().contains("callback") ) {
			postParent( Window.Location.getParameter("callback") );
		}
	}
	
	public void invertSelectionRecursive( Node root ) {
		root.setSelected( !root.isSelected() );
		if( root.getNodes() != null ) for( Node n : root.getNodes() ) {
			invertSelectionRecursive( n );
		}
	}
	
	public native void showTree( String tree ) /*-{
		$wnd.showTree( tree );
		
//		$wnd.domain = 'http://127.0.0.1:8888'; //'http://webconnectron.appspot.com';
//		$wnd.treetext = tree;
//		$wnd.receiveMessage = function(event) {
//			$wnd.console.log( $wnd.domain );
//			$wnd.console.log( 'ready message received' );
//			if (event.origin == $wnd.domain) { //"http://webconnectron.appspot.com") {
//				$wnd.console.log( 'correct origin' );
//				if( $wnd.treetext.length > 0 ) {
//					$wnd.myPopup.postMessage($wnd.treetext,$wnd.domain);
//				}
//			}
//		}
//		$wnd.addEventListener("message", $wnd.receiveMessage, false);
//		$wnd.myPopup = window.open($wnd.domain + '/Webconnectron.html?callback=webconnectron','_blank');
	}-*/;
	
	/*showTree = function( newtree ) {
		treetext = newtree;
		myPopup = window.open(domain + '/Treedraw.html?callback=webfasta','_blank');
	}*/
	
	double w;	
	double h;
	double dw;
	double dh;
	
	List<String>	colors = new ArrayList<String>();
	int				ci = 0;
	
	Random	rnd = new Random();
	
	public native void console( String log ) /*-{
		$wnd.console.log( log );
	}-*/;
	
	public void drawFramesRecursive( Context2d g2, TreeUtil.Node node, double x, double y, double startx, double starty, int equalHeight, boolean noAddHeight, boolean vertical, double maxheight, int leaves ) {		
		if( node.getNodes().size() > 0 ) {			
			int total = 0;
			String sc = node.getColor();
			if( sc != null ) {// paint && !(allNull || nullNodes) ) {
				//g2.setPaint( sc );
				g2.setFillStyle( sc );
				
				int k = 12;//(int)(w/32);
				if( vertical ) {
					int xoff = (int)(startx-(1*k)/4);
					g2.fillRect( xoff, (int)(y+k/4-1), (int)(w-xoff-w/17), (int)(dh*leaves) ); //ny-yoff );
				} else {
					int yoff = (int)(starty-(1*k)/4);
					g2.fillRect( (int)(x+k/4), yoff, (int)(dw*total-k/2.0), (int)(h-yoff-h/17) ); //ny-yoff );
				}
				//g2.setPaint( oldPaint );
			}
			
			for( Node resnode : node.getNodes() ) {
				int nleaves = resnode.countLeaves();
				int nlevels = resnode.countMaxHeight();
				int mleaves = Math.max(1, nleaves);
				
				double nx = 0;
				double ny = 0;
				
				if( vertical ) {
					//minh = 0.0;
					ny = dh*total+(dh*mleaves)/2.0;
					if( equalHeight > 0 ) {
						nx = w/25.0+dw*(w/dw-nlevels);
					} else {
						nx = /*h/25+*/startx+(w*resnode.geth())/(maxheight*1.1);
						//ny = 100+(int)(/*starty+*/(h*(node.h+resnode.h-minh))/((maxh-minh)*3.2));
					}

					if( nleaves == 0 ) {
						int v = (int)((nodearray.length*(y+ny))/canvas.getCoordinateSpaceHeight());
						//console( nodearray.length + "  " + canvas.getCoordinateSpaceHeight() + "  " + v );
						if( v >= 0 && v < nodearray.length ) nodearray[v] = resnode;
					}
				} else {
					//minh = 0.0;
					nx = dw*total+(dw*mleaves)/2.0;
					if( equalHeight > 0 ) {	
						ny = h/25.0+dh*(h/dh-nlevels);
					} else {
						ny = /*h/25+*/starty+(h*resnode.geth())/(maxheight*2.2);
						//ny = 100+(int)(/*starty+*/(h*(node.h+resnode.h-minh))/((maxh-minh)*3.2));
					}
				}
				int k = 12;//(int)(w/32);
				
				String use = resnode.getName() == null || resnode.getName().length() == 0 ? resnode.getMeta() : resnode.getName();
				boolean nullNodes = resnode.getNodes() == null || resnode.getNodes().size() == 0;
				boolean paint = use != null && use.length() > 0;
				
				/*ci++;
				for( int i = colors.size(); i <= ci; i++ ) {
					colors.add( "rgb( "+(int)(rnd.nextFloat()*255)+", "+(int)(rnd.nextFloat()*255)+", "+(int)(rnd.nextFloat()*255)+" )" );
				}*/
				//String color = node.getColor(); //colors.get(ci);
				
				/*if( resnode.color != null ) {
					color = resnode.color;
				}
				GradientPaint shadeColor = createGradient(color, (int)(ny-k/2), (int)h);*/
				
				if( vertical ) {
					//drawFramesRecursive( g2, resnode, x+dw*total, y+h, (dw*nleaves)/2.0, ny, paint ? shadeColor : null, nleaves, equalHeight );
					drawFramesRecursive( g2, resnode, x+w, y+dh*total, nx, (dh*mleaves)/2.0, equalHeight, noAddHeight, vertical, maxheight, mleaves );
				} else {
					//drawFramesRecursive( g2, resnode, x+dw*total, y+h, (dw*nleaves)/2.0, ny, paint ? shadeColor : null, nleaves, equalHeight );
					drawFramesRecursive( g2, resnode, x+dw*total, y+h, (dw*mleaves)/2.0, /*noAddHeight?starty:*/ny, equalHeight, noAddHeight, vertical, maxheight, mleaves );
				}
				
				//drawFramesRecursive( g2, resnode, x+dw*total, y+h, (dw*nleaves)/2.0, ny, paint ? shadeColor : null, nleaves, equalHeight );
				total += nleaves;
			}
		}
	}
	
	public Node getNodeRecursive( TreeUtil.Node root, double x, double y ) {
		return null;
	}
	
	public double drawTreeRecursiveCenter( Context2d g2, TreeUtil.Node node, double x, double y, double startx, double starty, int equalHeight, boolean noAddHeight, boolean vertical, double maxheight ) {
		Map<Node,Double>	cmap = new HashMap<Node,Double>();
		int total = 0;
		double nyavg = 0.0;
		for( TreeUtil.Node resnode : node.getNodes() ) {
			int nleaves = resnode.getLeavesCount();
			int nlevels = resnode.countMaxHeight();
			int plevels = resnode.countParentHeight();
			int mleaves = Math.max(1, nleaves);
			
			double nx = 0;
			double ny = 0;
			
			if( vertical ) {
				ny = dh*total+(dh*mleaves)/2.0;
				if( equalHeight > 0 ) {
					//nx = w/25.0+dw*(w/dw-nlevels);
					
					if( equalHeight == 1 ) nx = 30.0+dw*(maxheight/dw-nlevels);
					else nx = 30.0+(dw*plevels);
				} else {
					nx = /*h/25+*/startx+(w*resnode.geth())/(maxheight*1.0);
				}

				if( nleaves == 0 ) {
					int v = (int)((nodearray.length*(y+ny))/canvas.getCoordinateSpaceHeight());
					if( v >= 0 && v < nodearray.length ) nodearray[v] = resnode;
				}
			} else {
				nx = dw*total+(dw*mleaves)/2.0;
				if( equalHeight > 0 ) {	
					ny = h/25.0+dh*(h/dh-nlevels);
				} else {
					ny = /*h/25+*/starty+(h*resnode.geth())/(maxheight*2.2);
				}
			}
			
			if( !resnode.isCollapsed() ) {
				if( vertical ) {
					double newy = dh*total + drawTreeRecursiveCenter( g2, resnode, x+w, y+dh*total, nx, (dh*mleaves)/2.0, equalHeight, noAddHeight, vertical, maxheight );
					cmap.put( resnode, newy );
					nyavg += newy;
				} else {
					drawTreeRecursiveCenter( g2, resnode, x+dw*total, y+h, (dw*mleaves)/2.0, /*noAddHeight?starty:*/ny, equalHeight, noAddHeight, vertical, maxheight );
				}
			} else {
				if( vertical ) resnode.setCanvasLoc( nx, y+dh*total+(dh*mleaves)/2.0 );
				else resnode.setCanvasLoc( x+dw*total+(dw*mleaves)/2.0, ny );
			}
			
			total += mleaves;
		}
		
		double ret = (node.getNodes() != null && node.getNodes().size() > 0) ? nyavg/node.getNodes().size() : dh/2.0;
		if( vertical ) node.setCanvasLoc( startx, y+ret );
		else node.setCanvasLoc( x+ret, starty );
		total = 0;
		for( TreeUtil.Node resnode : node.getNodes() ) {
			int nleaves = resnode.getLeavesCount();
			int nlevels = resnode.countMaxHeight();
			int plevels = resnode.countParentHeight();
			int mleaves = Math.max(1, nleaves);
			
			double nx = 0;
			double ny = 0;
			
			if( vertical ) {
				ny = dh*total+(dh*mleaves)/2.0;
				if( equalHeight > 0 ) {
					if( equalHeight == 1 ) nx = 30.0+dw*(maxheight/dw-nlevels);
					else nx = 30.0+(dw*plevels);
				} else {
					nx = /*h/25+*/startx+(w*resnode.geth())/(maxheight*1.0);
				}

				if( nleaves == 0 ) {
					int v = (int)((nodearray.length*(y+ny))/canvas.getCoordinateSpaceHeight());
					if( v >= 0 && v < nodearray.length ) nodearray[v] = resnode;
				}
			} else {
				nx = dw*total+(dw*mleaves)/2.0;
				if( equalHeight > 0 ) {	
					ny = h/25.0+dh*(h/dh-nlevels);
				} else {
					ny = /*h/25+*/starty+(h*resnode.geth())/(maxheight*2.2);
				}
			}
			
			double newy = cmap.containsKey(resnode) ? cmap.get(resnode) : 0.0;
			if( resnode.isSelected() ) {
				g2.setStrokeStyle( "#000000" );
				g2.setLineWidth( 2.0 );
			}
			else {
				g2.setStrokeStyle( "#333333" );
				g2.setLineWidth( 1.0 );
			}
			g2.beginPath();
			if( vertical ) {
				double yfloor = Math.floor(y+newy);
				g2.moveTo( startx, y+ret );
				g2.lineTo( startx, yfloor );
				g2.moveTo( startx, yfloor );
				g2.lineTo( nx, yfloor );
			} else {
				g2.moveTo( x+startx, starty );
				g2.lineTo( x+nx, starty );
				g2.lineTo( x+nx, ny );
			}
			g2.closePath();
			g2.stroke();
			//g2.setStroke( hStroke );
			//g2.setStroke( oldStroke );
			
			paintTree( g2, resnode, vertical, x, y, nx, Math.floor(newy) );
			total += mleaves;
		}
		
		return ret;
	}
	
	public double drawTreeRecursive( Context2d g2, TreeUtil.Node node, double x, double y, double startx, double starty, int equalHeight, boolean noAddHeight, boolean vertical, double maxheight ) {
		int total = 0;
		if( vertical ) node.setCanvasLoc( startx, y+starty );
		else node.setCanvasLoc( x+startx, starty );
		
		for( TreeUtil.Node resnode : node.getNodes() ) {
			int nleaves = resnode.getLeavesCount();
			int nlevels = resnode.countMaxHeight();
			int plevels = resnode.countParentHeight();
			int mleaves = Math.max(1, nleaves);
			
			double nx = 0;
			double ny = 0;
			
			if( vertical ) {
				//minh = 0.0;
				ny = dh*total+(dh*mleaves)/2.0;
				if( equalHeight > 0 ) {
					//w/25.0
					if( equalHeight == 1 ) nx = 30.0+dw*(maxheight/dw-nlevels);
					else nx = 30.0+(dw*plevels);
				} else {
					nx = /*h/25+*/startx+(w*resnode.geth())/(maxheight*1.0);
					//ny = 100+(int)(/*starty+*/(h*(node.h+resnode.h-minh))/((maxh-minh)*3.2));
				}

				if( nleaves == 0 ) {
					int v = (int)((nodearray.length*(y+ny))/canvas.getCoordinateSpaceHeight());
					//console( nodearray.length + "  " + canvas.getCoordinateSpaceHeight() + "  " + v );
					if( v >= 0 && v < nodearray.length ) nodearray[v] = resnode;
				}
			} else {
				//minh = 0.0;
				nx = dw*total+(dw*mleaves)/2.0;
				if( equalHeight > 0 ) {	
					ny = h/25.0+dh*(h/dh-nlevels);
				} else {
					ny = /*h/25+*/starty+(h*resnode.geth())/(maxheight*2.2);
					//ny = 100+(int)(/*starty+*/(h*(node.h+resnode.h-minh))/((maxh-minh)*3.2));
				}
			}
			
			//int yoff = starty-k/2;
			/*System.err.println( resnode.meta );
			if( resnode.meta != null && resnode.meta.contains("Bulgaria") ) {
				System.err.println( resnode.nodes );
			}*/
			/*ci++;
			for( int i = colors.size(); i <= ci; i++ ) {
				colors.add( "rgb( "+(int)(rnd.nextFloat()*255)+", "+(int)(rnd.nextFloat()*255)+", "+(int)(rnd.nextFloat()*255)+" )" );
			}			
			String color = colors.get(ci);*/
			/*if( resnode.color != null ) {
				color = resnode.color;
			}*/
			
			if( !resnode.isCollapsed() ) {
				if( vertical ) {
					drawTreeRecursive( g2, resnode, x+w, y+dh*total, nx, (dh*mleaves)/2.0, equalHeight, noAddHeight, vertical, maxheight );
				} else {
					drawTreeRecursive( g2, resnode, x+dw*total, y+h, (dw*mleaves)/2.0, /*noAddHeight?starty:*/ny, equalHeight, noAddHeight, vertical, maxheight );
				}
			} else {
				if( vertical ) resnode.setCanvasLoc( nx, y+dh*total+(dh*mleaves)/2.0 );
				else resnode.setCanvasLoc( x+dw*total+(dw*mleaves)/2.0, ny );
			}
		
			//ny+=starty;
			//drawTreeRecursive( g2, resnode, w, h, dw, dh, x+dw*total, y+h, (dw*nleaves)/2, ny, paint ? shadeColor : null );
			
			if( resnode.isSelected() ) {
				g2.setStrokeStyle( "#000000" );
				g2.setLineWidth( 2.0 );
			}
			else {
				g2.setStrokeStyle( "#333333" );
				g2.setLineWidth( 1.0 );
			}
			//g2.setStroke( vStroke );
			g2.beginPath();
			if( vertical ) {
				double yfloor = Math.floor(y+ny);
				g2.moveTo( startx, y+starty );
				g2.lineTo( startx, yfloor );
				g2.moveTo( startx, yfloor );
				g2.lineTo( nx, yfloor );
			} else {
				g2.moveTo( x+startx, starty );
				g2.lineTo( x+nx, starty );
				g2.lineTo( x+nx, ny );
			}
			g2.closePath();
			g2.stroke();
			//g2.setStroke( hStroke );
			//g2.setStroke( oldStroke );
			
			paintTree( g2, resnode, vertical, x, y, nx, ny );
			total += mleaves;
		}
		
		return /*(node.getNodes() != null && node.getNodes().size() > 0) ? nyavg/node.getNodes().size() : */0.0;
	}
	
	public void paintTree( Context2d g2, Node resnode, boolean vertical, double x, double y, double nx, double ny ) {
		int k = 12;//w/32;
		int fontSize = 10;
		
		String use = resnode.getName() == null || resnode.getName().length() == 0 ? resnode.getMeta() : resnode.getName();
		use = resnode.isCollapsed() ? resnode.getCollapsedString() : use;
		boolean nullNodes = resnode.isCollapsed() || resnode.getNodes() == null || resnode.getNodes().size() == 0;
		boolean paint = use != null && use.length() > 0;
		
		String color = resnode.getColor();
		if( paint ) {
			if( nullNodes ) {
				g2.setFillStyle( "#000000" );
				//g2.setFont( bFont );
				
				String name = resnode.getName();
				if( resnode.getMeta() != null ) {
					String meta = resnode.getMeta();
					name += " ("+meta+")";
					
					/*if( meta.contains("T.ign") ) {
						System.err.println();
					}*/
				}
				
				String[] split;
				if( name == null || name.length() == 0 ) split = resnode.getCollapsedString().split("_");
				else split = new String[] { name }; //name.split("_");
				
				int t = 0;
				double mstrw = 0;
				double mstrh = 10;
				
				String fontstr = (resnode.isSelected() ? "bold " : " ")+(int)(5.0*Math.log(hchunk))+"px sans-serif";
				if( !fontstr.equals(g2.getFont()) ) g2.setFont( fontstr );
				
				if( !vertical ) {
					for( String str : split ) {
						double strw = g2.measureText( str ).getWidth();
						mstrw = Math.max( mstrw, strw );
						if( resnode.getColor() != null ) {
							g2.setFillStyle( resnode.getColor() );
							g2.fillRect( (int)(x+nx-strw/2.0), (int)(ny+4+10+(t++)*fontSize), strw, mstrh);
							g2.setFillStyle( "#000000" );
						}
						g2.fillText(str, (int)(x+nx-strw/2.0), (int)(ny+4+10+(t++)*fontSize) );
					}
				} else {
					for( String str : split ) {
						if( resnode.getColor() != null ) {
							double strw = g2.measureText( str ).getWidth();
							g2.setFillStyle( resnode.getColor() );
							g2.fillRect( nx+4+10+(t++)*fontSize, y+ny+mstrh/2.0-mstrh+1.0, strw+15, mstrh*1.15);
							g2.setFillStyle( "#000000" );
						}
						
						boolean it = false;
						boolean sub = false;
						boolean sup = false;
						List<Integer> li = new ArrayList<Integer>();
						int start = 0;
						String[] tags = { "<i>", "<sub>", "<sup>", "</i>", "</sub>", "</sup>" };
						double pos = 0.0;
						
						while( start < str.length() ) {
							li.clear();
							
							for( String tag : tags ) {
								int ti = str.indexOf(tag, start);
								if( ti == -1 ) ti = str.length();
								
								li.add( ti );
							}
							
							int min = Collections.min( li );
							if( min < str.length() ) {
								int mini = li.indexOf( min );
								String tag = tags[mini];
								
								fontstr = (resnode.isSelected() ? "bold" : "")+(it ? " italic " : " ")+(int)( ( (sup || sub) ? 3.0 : 5.0 )*Math.log(hchunk) )+"px sans-serif";
								if( !fontstr.equals(g2.getFont()) ) g2.setFont( fontstr );
								
								String substr = str.substring(start, min);
								g2.fillText(substr, nx+4+10+(t)*fontSize+pos, y+ny+mstrh/2.0 );
								pos += g2.measureText( substr ).getWidth();
								
								int next = min+tag.length();
								start = next;
								if( tag.equals("<i>") ) it = true;
								else if( tag.equals("</i>") ) it = false;
								if( tag.equals("<sup>") ) sup = true;
								else if( tag.equals("</sup>") ) sup = false;
								if( tag.equals("<sub>") ) sub = true;
								else if( tag.equals("</sub>") ) sub = false;
							} else {
								fontstr = (resnode.isSelected() ? "bold" : "")+(it ? " italic " : " ")+(int)( ( (sup || sub) ? 3.0 : 5.0 )*Math.log(hchunk) )+"px sans-serif";
								if( !fontstr.equals(g2.getFont()) ) g2.setFont( fontstr );
								
								String substr = str.substring(start, str.length());
								g2.fillText(substr, nx+4+10+(t)*fontSize+pos, y+ny+mstrh/2.0 );
								start = str.length();
							}
						
						}
					}
				}
				
				/*int x1 = (int)(x+nx-mstrw/2);
				int x2 = (int)(x+nx+mstrw/2);
				int y1 = (int)(ny+4+h/25+(-1)*bFont.getSize());
				int y2 = (int)(ny+4+h/25+(split.length-1)*bFont.getSize());
				yaml += resnode.name + ": [" + x1 + "," + y1 + "," + x2 + "," + y2 + "]\n";*/
			} else {
				boolean b = use.length() > 2;
				
				if( color != null && color.length() > 0 ) g2.setFillStyle( color );
				else g2.setFillStyle( "#000000" );
				double strw = 0;
				String[] split = use.split( "_" );
				if( b ) {
					//g2.setFont( lFont );
					for( String s : split ) {
						strw = Math.max( strw, g2.measureText( s ).getWidth() );
					}
				} else{
					//g2.setFont( bFont );
					for( String s : split ) {
						strw = Math.max( strw, g2.measureText( s ).getWidth() );
					}
				}
				double strh = 10;
				if( vertical ) {
					g2.fillRect( nx-(5*strw)/8, y+ny-(5*strh)/8, (5*strw)/4, k );
				} else g2.fillRect( x+nx-(5*strw)/8, ny-k/2.0, (5*strw)/4, k );
				//g2.fillRoundRect(startx, starty, width, height, arcWidth, arcHeight)
				//g2.fillOval( x+nx-k/2, ny-k/2, k, k );
				g2.setFillStyle( "#ffffff" );
				int i = 0;
				if( vertical ) {
					if( b ) {
						for( String s : split ) {
							g2.fillText(s, nx-strw/2.0, y+ny+strh/2-1-8*(split.length-1)+i*16 );
							i++;
						}
					} else {
						for( String s : split ) {
							g2.fillText(s, nx-strw/2.0, y+ny+strh/2-1-8*(split.length-1)+i*16 );
							i++;
						}
					}
				} else {
					if( b ) {
						for( String s : split ) {
							strw = g2.measureText( s ).getWidth();
							g2.fillText(s, x+nx-strw/2.0, ny+5-8*(split.length-1)+i*16 );
							i++;
						}
					} else {
						for( String s : split ) {
							strw = g2.measureText(s).getWidth();
							g2.fillText(s, x+nx-strw/2.0, ny+6-8*(split.length)+i*16 );
							i++;
						}
					}
				}
			}
		}
	}
}
