package org.simmi.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.simmi.TreeUtil;
import org.simmi.TreeUtil.Node;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

public class Treedraw implements EntryPoint {
	Canvas	canvas;
	Node[]	nodearray;
	
	@Override
	public void onModuleLoad() {
		RootPanel	rp = RootPanel.get();
		canvas = Canvas.createIfSupported();
		
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		canvas.setSize(w+"px", h+"px");
		
		canvas.addDropHandler( new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				String tree = event.getData("text/plain");
				
				TreeUtil	treeutil = new TreeUtil( tree, false, null );
				int ww = Window.getClientWidth();
				
				//treeutil.getNode().countLeaves()
				int leaves = treeutil.getNode().getLeavesCount();
				int levels = treeutil.getNode().countMaxHeight();
				
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
				if( vertical ) {
					drawTreeRecursive( canvas.getContext2d(), treeutil.getNode(), 0, 0, startx, Treedraw.this.h/2, equalHeight, false, vertical, treeutil.getminh(), treeutil.getmaxh() );
				} else {
					drawTreeRecursive( canvas.getContext2d(), treeutil.getNode(), 0, 0, Treedraw.this.w/2, starty, equalHeight, false, vertical, treeutil.getminh(), treeutil.getmaxh() );
				}
				
				//drawTreeRecursive( canvas.getContext2d(), treeutil.getNode(), 10, 10, 10, 10, false, false, true, treeutil.getminh(), treeutil.getmaxh());
			}
		});
		canvas.addDragHandler( new DragHandler() {
			@Override
			public void onDrag(DragEvent event) {
				
			}
		});
		canvas.addDragEnterHandler( new DragEnterHandler() {
			@Override
			public void onDragEnter(DragEnterEvent event) {
				
			}
		});
		canvas.addDragLeaveHandler( new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				
			}
		});
		canvas.addDragStartHandler( new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {
				
			}
		});
		canvas.addDragEndHandler( new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {
				
			}
		});
		canvas.addDragOverHandler( new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {}
		});
		
		canvas.addMouseDownHandler( new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if( nodearray != null ) {
					int y = event.getY();
					int i = (nodearray.length*y)/canvas.getCoordinateSpaceHeight();
					final Node n = nodearray[i];
					
					int ci = n.getName().indexOf(",");
					String fname = n.getName().substring(1,ci);
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
				}
			}
		});
		
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
	
	public void drawTreeRecursive( Context2d g2, TreeUtil.Node node, double x, double y, double startx, double starty, boolean equalHeight, boolean noAddHeight, boolean vertical, double minh, double maxh ) {		
		int total = 0;
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
					nx = /*h/25+*/startx+(w*(resnode.geth()-minh))/((maxh-minh)*1.3);
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
					ny = /*h/25+*/starty+(h*(resnode.geth()-minh))/((maxh-minh)*2.2);
					//ny = 100+(int)(/*starty+*/(h*(node.h+resnode.h-minh))/((maxh-minh)*3.2));
				}
			}
			
			int k = 12;//w/32;
			//int yoff = starty-k/2;
			
			String use = resnode.getName() == null || resnode.getName().length() == 0 ? resnode.getMeta() : resnode.getName();
			boolean nullNodes = resnode.getNodes() == null || resnode.getNodes().size() == 0;
			boolean paint = use != null && use.length() > 0;
			
			/*System.err.println( resnode.meta );
			if( resnode.meta != null && resnode.meta.contains("Bulgaria") ) {
				System.err.println( resnode.nodes );
			}*/
			
			ci++;
			for( int i = colors.size(); i <= ci; i++ ) {
				colors.add( "rgb( "+(int)(rnd.nextFloat()*255)+", "+(int)(rnd.nextFloat()*255)+", "+(int)(rnd.nextFloat()*255)+" )" );
			}
			
			String color = colors.get(ci);
			/*if( resnode.color != null ) {
				color = resnode.color;
			}*/
			
			if( vertical ) {
				drawTreeRecursive( g2, resnode, x+w, y+dh*total, nx, (dh*mleaves)/2.0, equalHeight, noAddHeight, vertical, minh, maxh );
			} else {
				drawTreeRecursive( g2, resnode, x+dw*total, y+h, (dw*mleaves)/2.0, /*noAddHeight?starty:*/ny, equalHeight, noAddHeight, vertical, minh, maxh );
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
					
					if( resnode.getMeta() != null ) resnode.setName( resnode.getName() + " ("+resnode.getMeta()+")" );
					String[] split = resnode.getName().split("_");
					int t = 0;
					double mstrw = 0;
					double mstrh = 10;
					if( !vertical ) {
						for( String str : split ) {
							double strw = g2.measureText( str ).getWidth();
							mstrw = Math.max( mstrw, strw );
							g2.fillText(str, (int)(x+nx-strw/2.0), (int)(ny+4+10+(t++)*fontSize) );
						}
					} else {
						for( String str : split ) {
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
