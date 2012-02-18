package org.simmi.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class Treedraw implements EntryPoint, MouseDownHandler {
	Canvas		canvas;
	Node		root;
	Node[]		nodearray;
	TreeUtil	treeutil;
	
	public void handleTree( TreeUtil treeutil ) {
		this.treeutil = treeutil;
		//treeutil.getNode().countLeaves()
		
		Node n = treeutil.getNode();
		if( n != null ) {
			root = n;
			drawTree( treeutil );
		}
	}
	
	public void drawTree( TreeUtil treeutil ) {
		double minh = treeutil.getminh();
		double maxh = treeutil.getmaxh();
		
		double minh2 = treeutil.getminh2();
		double maxh2 = treeutil.getmaxh2();
		
		int ww = Window.getClientWidth();
		
		int leaves = root.getLeavesCount();
		int levels = root.countMaxHeight();
		
		console( "leaves " + leaves );
		double	maxheight = root.getMaxHeight();
		
		nodearray = new Node[ leaves ];
		
		canvas.setSize((ww-10)+"px", (10*leaves)+"px");
		canvas.setCoordinateSpaceWidth( ww-10 );
		canvas.setCoordinateSpaceHeight( 10*leaves );
		
		boolean vertical = true;
		boolean equalHeight = false;
		
		Treedraw.this.h = 10*leaves;
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
		
		if( vertical ) {
			drawFramesRecursive( canvas.getContext2d(), root, 0, 0, startx, Treedraw.this.h/2, equalHeight, false, vertical, maxheight, 0 );
			ci = 0;
			drawTreeRecursive( canvas.getContext2d(), root, 0, 0, startx, Treedraw.this.h/2, equalHeight, false, vertical, maxheight );
		} else {
			drawFramesRecursive( canvas.getContext2d(), root, 0, 0, Treedraw.this.w/2, starty, equalHeight, false, vertical, maxheight, 0 );
			ci = 0;
			drawTreeRecursive( canvas.getContext2d(), root, 0, 0, Treedraw.this.w/2, starty, equalHeight, false, vertical, maxheight );
		}
	}
	
	public void handleText( String str ) {
		console( str );
		if( !str.startsWith("(") ) {
			TreeUtil treeutil = new TreeUtil();
			int len = 0;
			List<String> names;
			double[] dvals;
			if( str.startsWith(" ") ) {
				names = new ArrayList<String>();
				String[] lines = str.split("\n");
				len = Integer.parseInt( lines[0].trim() );
				dvals = new double[ len*len ];
				int m = 0;
				int u = 0;
				for( int i = 1; i < lines.length; i++ ) {
					String line = lines[i];
					String[] ddstrs = line.split("[ ]+");
					if( !line.startsWith(" ") ) {
						m++;
						u = 0;
						
						int si = ddstrs[0].indexOf('_');
						String name = si == -1 ? ddstrs[0] : ddstrs[0].substring( 0, si );
						console( "name: " + name );
						names.add( name );
					}
					if( ddstrs.length > 2 ) {
						for( int k = 1; k < ddstrs.length; k++ ) {
							int idx = (m-1)*len+(u++);
							if( idx < 256 ) dvals[idx] = Double.parseDouble(ddstrs[k]);
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
			treeutil.neighborJoin( dvals, len, names );
			console( treeutil.getNode().toString() );
			handleTree( treeutil );
		} else {
			String tree = str.replaceAll("[\r\n]+", "");
			TreeUtil	treeutil = new TreeUtil( tree, false, null, null, false, null, null );
			handleTree( treeutil );
		}
	}
	
	public native String handleFiles( Element ie, int append ) /*-{	
		$wnd.console.log('dcw');
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
	
	public void recursiveReroot( Node node, double x, double y ) {
		if( node != null ) {
			if( Math.abs( node.getCanvasX()-x ) < 5 && Math.abs( node.getCanvasY()-y ) < 5 ) {
				treeutil.reroot(root, node);
				root = node;
			} else {
				for( Node n : node.getNodes() ) {
					recursiveReroot( n, x, y );
				}
			}
		}
	}
	
	public void recursiveNodeClick( Node node, double x, double y, int recursive ) {
		if( node != null ) {
			if( recursive > 1 || (Math.abs( node.getCanvasX()-x ) < 5 && Math.abs( node.getCanvasY()-y ) < 5) ) {
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
					recursiveNodeClick( n, x, y, recursive );
				}
			}
		}
	}
	
	PopupPanel	popup;
	int 	npx;
	int		npy;
	int		px;
	int		py;
	boolean 		toggle = false;
	boolean 		shift = false;
	boolean 		mousedown = false;
	
	@Override
	public void onMouseDown(MouseDownEvent e) {
		toggle = false;
		px = e.getX();
		py = e.getY();
		npx = px;
		npy = py;
		shift = e.isShiftKeyDown();
		int nativebutton = e.getNativeButton();
		
		if( nativebutton == NativeEvent.BUTTON_RIGHT ) {
			mousedown = false;
			popup.setPopupPosition(e.getX(), e.getY());
			popup.show();
		}
	}
	
	@Override
	public void onModuleLoad() {
		RootPanel	rp = RootPanel.get();
		rp.addDomHandler( new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, ContextMenuEvent.getType());
		
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
		int h = Window.getClientHeight();
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
			public void onDrag(DragEvent event) {}
		});
		canvas.addDragEnterHandler( new DragEnterHandler() {
			@Override
			public void onDragEnter(DragEnterEvent event) {}
		});
		canvas.addDragLeaveHandler( new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {}
		});
		canvas.addDragStartHandler( new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {}
		});
		canvas.addDragEndHandler( new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {}
		});
		canvas.addDragOverHandler( new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {}
		});
		canvas.addDoubleClickHandler( new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if( event.isShiftKeyDown() ) {
					int x = event.getX();
					int y = event.getY();
					
					canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
					recursiveReroot( root, x, y );
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
				if( event.isControlKeyDown() ) {
					recursiveNodeCollapse( root, x, y );
					root.countLeaves();
				} else if( event.isShiftKeyDown() ) {
					recursiveNodeClick( root, x, y, 1 );
				} else {
					recursiveNodeClick( root, x, y, 0 );
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
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		
		//console( canvas.getOffsetWidth() + "  " + canvas.getOffsetHeight() );
		canvas.setCoordinateSpaceWidth( w );
		canvas.setCoordinateSpaceHeight( h );
		Context2d context = canvas.getContext2d();
		
		String str = "Drop text in distance matrix or newick tree format to this canvas";
		TextMetrics tm = context.measureText( str );
		context.fillText(str, (w-tm.getWidth())/2.0, h/2.0-8.0);
		str = "Double click to open file dialog";
		tm = context.measureText( str );
		context.fillText(str, (w-tm.getWidth())/2.0, h/2.0+8.0);
		
		rp.add( canvas );
	}
	
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
	
	public void drawFramesRecursive( Context2d g2, TreeUtil.Node node, double x, double y, double startx, double starty, boolean equalHeight, boolean noAddHeight, boolean vertical, double maxheight, int leaves ) {		
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
					if( equalHeight ) {
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
					if( equalHeight ) {	
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
	
	public void drawTreeRecursive( Context2d g2, TreeUtil.Node node, double x, double y, double startx, double starty, boolean equalHeight, boolean noAddHeight, boolean vertical, double maxheight ) {
		
		int total = 0;
		if( vertical ) node.setCanvasLoc( startx, y+starty );
		else node.setCanvasLoc( x+startx, starty );
		for( TreeUtil.Node resnode : node.getNodes() ) {
			int nleaves = resnode.getLeavesCount();
			int nlevels = resnode.countMaxHeight();
			int mleaves = Math.max(1, nleaves);
			
			double nx = 0;
			double ny = 0;
			
			if( vertical ) {
				//minh = 0.0;
				ny = dh*total+(dh*mleaves)/2.0;
				if( equalHeight ) {
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
				if( equalHeight ) {	
					ny = h/25.0+dh*(h/dh-nlevels);
				} else {
					ny = /*h/25+*/starty+(h*resnode.geth())/(maxheight*2.2);
					//ny = 100+(int)(/*starty+*/(h*(node.h+resnode.h-minh))/((maxh-minh)*3.2));
				}
			}
			
			int k = 12;//w/32;
			//int yoff = starty-k/2;
			
			String use = resnode.getName() == null || resnode.getName().length() == 0 ? resnode.getMeta() : resnode.getName();
			use = resnode.isCollapsed() ? resnode.getCollapsedString() : use;
			boolean nullNodes = resnode.isCollapsed() || resnode.getNodes() == null || resnode.getNodes().size() == 0;
			boolean paint = use != null && use.length() > 0;
			
			/*System.err.println( resnode.meta );
			if( resnode.meta != null && resnode.meta.contains("Bulgaria") ) {
				System.err.println( resnode.nodes );
			}*/
			
			/*ci++;
			for( int i = colors.size(); i <= ci; i++ ) {
				colors.add( "rgb( "+(int)(rnd.nextFloat()*255)+", "+(int)(rnd.nextFloat()*255)+", "+(int)(rnd.nextFloat()*255)+" )" );
			}			
			String color = colors.get(ci);*/
			
			String color = resnode.getColor();
			
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
			
			g2.setStrokeStyle( "#333333" );
			//g2.setStroke( vStroke );
			g2.beginPath();
			if( vertical ) {
				g2.moveTo( startx, y+starty );
				g2.lineTo( startx, y+ny );
				g2.moveTo( startx, y+ny );
				g2.lineTo( nx, y+ny );
			} else {
				g2.moveTo( x+startx, starty );
				g2.lineTo( x+nx, starty );
				g2.lineTo( x+nx, ny );
			}
			g2.closePath();
			g2.stroke();
			//g2.setStroke( hStroke );
			//g2.setStroke( oldStroke );
			
			int fontSize = 10;
			
			if( paint ) {
				if( nullNodes ) {
					g2.setFillStyle( "#000000" );
					//g2.setFont( bFont );
					
					String name = resnode.getName();
					if( resnode.getMeta() != null ) name += " ("+resnode.getMeta()+")";
					
					String[] split;
					if( name == null || name.length() == 0 ) split = resnode.getCollapsedString().split("_");
					else split = name.split("_");
					
					int t = 0;
					double mstrw = 0;
					double mstrh = 10;
					
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
							g2.fillText(str, nx+4+10+(t++)*fontSize, y+ny+mstrh/2.0 );
						}
					}
					
					/*int x1 = (int)(x+nx-mstrw/2);
					int x2 = (int)(x+nx+mstrw/2);
					int y1 = (int)(ny+4+h/25+(-1)*bFont.getSize());
					int y2 = (int)(ny+4+h/25+(split.length-1)*bFont.getSize());
					yaml += resnode.name + ": [" + x1 + "," + y1 + "," + x2 + "," + y2 + "]\n";*/
				} else {
					boolean b = use.length() > 2;
					
					g2.setFillStyle( color );
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
					if( vertical ) g2.fillRect( nx-(5*strw)/8, y+ny-(5*strh)/8, (5*strw)/4, k );
					else g2.fillRect( x+nx-(5*strw)/8, ny-k/2.0, (5*strw)/4, k );
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
								g2.fillText( s, nx-strw/2.0, y+ny+strh/2-1-8*(split.length)+i*16 );
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
			total += mleaves;
		}
	}
}
