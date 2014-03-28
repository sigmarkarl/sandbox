package org.simmi.client;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
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
import com.google.gwt.dom.client.CanvasElement;
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
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.Blob;
import elemental.html.Console;
import elemental.html.WebSocket;

//import elemental.client.Browser;
//import elemental.html.Console;

public class Treedraw implements EntryPoint {
	Canvas				canvas;
	Node				root;
	Node				selectedNode;
	Node[]				nodearray;
	private TreeUtil	treeutil;
	boolean				center = false;
	int					equalHeight = 0;
	boolean				showscale = true;
	boolean				showbubble = false;
	boolean				showlinage = false;
	boolean				showleafnames = true;
	boolean				rightalign = false;
	boolean				circular = false;
	boolean				radial = false;
	
	private void setTreeUtil( TreeUtil tu, String val ) {
		if( this.treeutil != null ) console( "batjong2 " + val );
		else console( "batjong2333333333333333333333333333333333333333333333333333 " + val );
		this.treeutil = tu;
	}
	
	public void handleTree() {
		//this.treeutil = treeutil;
		//treeutil.getNode().countLeaves()
		
		Node n = treeutil.getNode();
		if( n != null ) {
			root = n;
			drawTree( treeutil );
		}
	}
	
	public double getMaxInternalNameLength( Node n, Context2d ctx ) {
		double ret = 0.0;
		String name = n.getName();
		List<Node> nl = n.getNodes();
		if( nl != null ) {
			if( name != null && nl.size() > 0 ) ret = ctx.measureText( name ).getWidth();
		
			for( Node nn : n.getNodes() ) {
				double val = getMaxInternalNameLength( nn, ctx );
				if( val > ret ) ret = val;
			}
		}
		return ret;
	}
	
	double clientscale = 1.0;
	public int getClientWidth() {
		return (int)(clientscale*Window.getClientWidth());
	}
	
	int count = 0;
	private double spread = 0.0;
	private double[] bounds = new double[4];
	private double[] lbounds = new double[4];
	public double[] constructNode(Context2d ctx, TreeUtil tree, Node node, double angleStart,
			double angleFinish, double xPosition, double yPosition,
			double length, boolean nodraw) {
		final double branchAngle = (angleStart + angleFinish) / 2.0;

		final double directionX = Math.cos(branchAngle);
		final double directionY = Math.sin(branchAngle);
		double[] nodePoint = new double[] { xPosition + (length * directionX), yPosition + (length * directionY) };
		
		double x0 = nodePoint[0];
		double y0 = nodePoint[1];

		if( !node.isLeaf() ) {
		//if (!tree.isExternal(node)) {

			// Not too clear how to do hilighting for radial trees so leave it
			// out...
			// if (hilightAttributeName != null &&
			// node.getAttribute(hilightAttributeName) != null) {
			// constructHilight(tree, node, angleStart, angleFinish, xPosition,
			// yPosition, length, cache);
			// }

			//node.get
			List<Node> children = node.getNodes(); //tree.getChildren(node);
			int[] leafCounts = new int[children.size()];
			int sumLeafCount = 0;

			int i = 0;
			for (Node child : children) {
				leafCounts[i] = child.countLeaves(); //jebl.evolution.trees.Utils.getExternalNodeCount(tree, child);
				sumLeafCount += leafCounts[i];
				i++;
			}
			
			//Browser.getWindow().getConsole().log( "s " + sumLeafCount );

			double span = (angleFinish - angleStart);

			if( !node.isRoot() ) {
			//if (!tree.isRoot(node)) {
				span *= 1.0 + (spread / 10.0);
				angleStart = branchAngle - (span / 2.0);
				angleFinish = branchAngle + (span / 2.0);
			}

			double a2 = angleStart;

			//Browser.getWindow().getConsole().log( "erm " + angleStart + "  " + angleFinish );
			
			boolean rotate = false;
			/*if (node.getAttribute("!rotate") != null && ((Boolean) node.getAttribute("!rotate"))) {
				rotate = true;
			}*/
			for (i = 0; i < children.size(); ++i) {
				int index = i;
				if (rotate) {
					index = children.size() - i - 1;
				}

				Node child = children.get(index);

				final double childLength = child.getLength();
				double a1 = a2;
				a2 = a1 + (span * leafCounts[index] / sumLeafCount);
				double[] childPoint = constructNode( ctx, tree, child, a1, a2, nodePoint[0], nodePoint[1], childLength, nodraw);
				double x1 = childPoint[0];
				double y1 = childPoint[1];
				//Line2D branchLine = new Line2D.Double(childPoint.getX(), childPoint.getY(), nodePoint.getX(), nodePoint.getY());

				Object[] colouring = null;//new Object[] {}; //null;
				/*if (branchColouringAttribute != null) {
					colouring = (Object[]) child.getAttribute(branchColouringAttribute);
				}*/
				if (colouring != null) {
					// If there is a colouring, then we break the path up into
					// segments. This should allow use to iterate along the
					// segments
					// and colour them as we draw them.

					//double nodeHeight = tree.getHeight(node);
					//double childHeight = tree.getHeight(child);
					//GeneralPath branchPath = new GeneralPath();

					// to help this, we are going to draw the branch backwards
					ctx.beginPath();
					ctx.moveTo(x1, y1);
					float interval = 0.0F;
					for (int j = 0; j < colouring.length - 1; j += 2) {
						// float height = ((Number)colouring[j+1]).floatValue();
						// float p = (height - childHeight) / (nodeHeight -
						// childHeight);
						interval += ((Number) colouring[j + 1]).floatValue();
						double p = interval / childLength; //(nodeHeight - childHeight);
						double x = x1 + ((x0 - x1) * p);
						double y = y1 + ((y0 - y1) * p);
						ctx.lineTo(x, y);
					}
					ctx.lineTo(x0, y0);
					ctx.closePath();
					ctx.stroke();

					// add the branchPath to the map of branch paths
					//cache.branchPaths.put(child, branchPath);

				} else {
					// add the branchLine to the map of branch paths
					//cache.branchPaths.put(child, branchLine);
					if( nodraw ) {
						if( x1 < bounds[0] ) bounds[0] = x1;
						if( x1 > bounds[2] ) bounds[2] = x1;
						if( y1 < bounds[1] ) bounds[1] = y1;
						if( y1 > bounds[3] ) bounds[3] = y1;
						
						if( x0 < bounds[0] ) bounds[0] = x0;
						if( x0 > bounds[2] ) bounds[2] = x0;
						if( y0 < bounds[1] ) bounds[1] = y0;
						if( y0 > bounds[3] ) bounds[3] = y0;
					} else {
						double xscale = (canvas.getCoordinateSpaceWidth()-10.0-(lbounds[2]-lbounds[0]))/(bounds[2]-bounds[0]);
						//double yscale = (canvas.getCoordinateSpaceHeight()-10.0-(lbounds[3]-lbounds[1]))/(bounds[3]-bounds[1]);
						//double scale = Math.min(xscale, yscale);
						double xoffset = 5.0-lbounds[0]-bounds[0]*xscale;
						double yoffset = 5.0-lbounds[1]-bounds[1]*xscale;
						
						double xx1 = xscale*x1 + xoffset;
						double yy1 = xscale*y1 + yoffset;
						
						double xx2 = xscale*x0 + xoffset;
						double yy2 = xscale*y0 + yoffset;
						
						ctx.setStrokeStyle("#000000");
						ctx.beginPath();
						ctx.moveTo(xx1,yy1);
						ctx.lineTo(xx2,yy2);
						ctx.closePath();
						ctx.stroke();
					}
				}

				//cache.branchLabelPaths.put(child, (Line2D) branchLine.clone());
			}

			/*double[] nodeLabelPoint = new double[] { xPosition
					+ ((length + 1.0) * directionX), yPosition
					+ ((length + 1.0) * directionY) };

			/*Line2D nodeLabelPath = new Line2D.Double(nodePoint, nodeLabelPoint);
			cache.nodeLabelPaths.put(node, nodeLabelPath);*
			
			double x1 = 100.0*nodePoint[0] + 300.0;
			double y1 = 100.0*nodePoint[1] + 300.0;
			
			double x2 = 100.0*nodeLabelPoint[0] + 300.0;
			double y2 = 100.0*nodeLabelPoint[1] + 300.0;
			
			ctx.setStrokeStyle("#00FF00");
			ctx.beginPath();
			ctx.moveTo(x1,y1);
			ctx.lineTo(x2,y2);
			ctx.closePath();
			ctx.stroke();*/

		} else {
			double x1 = xPosition + ((length + 1.0) * directionX);
			double y1 = yPosition + ((length + 1.0) * directionY);
			if( nodraw ) {
				ctx.setFillStyle("#000000");
				double horn = Math.atan2( y1-y0, x1-x0 );
				
				String name = node.getName();
				TextMetrics tm = ctx.measureText( name );
				double strlen = tm.getWidth();
				/*if( Math.abs(horn) > Math.PI/2.0 ) {
					horn += Math.PI;
				}*/
				double x = strlen*Math.cos( horn );
				double y = strlen*Math.sin( horn );
				
				if( x < lbounds[0] ) lbounds[0] = x;
				if( x > lbounds[2] ) lbounds[2] = x;
				if( y < lbounds[1] ) lbounds[1] = y;
				if( y > lbounds[3] ) lbounds[3] = y;
			} else {
				ctx.setFillStyle("#000000");
				String name = node.getName();
				
				double xscale = (canvas.getCoordinateSpaceWidth()-10.0-(lbounds[2]-lbounds[0]))/(bounds[2]-bounds[0]);
				//double yscale = (canvas.getCoordinateSpaceHeight()-10.0-(lbounds[3]-lbounds[1]))/(bounds[3]-bounds[1]);
				//double scale = Math.min(xscale, yscale);
				double xoffset = 5.0-lbounds[0]-bounds[0]*xscale;
				double yoffset = 5.0-lbounds[1]-bounds[1]*xscale;
				
				double xx1 = xscale*x1 + xoffset;
				double yy1 = xscale*y1 + yoffset;
				
				double xx2 = xscale*x0 + xoffset;
				double yy2 = xscale*y0 + yoffset;
				
				double horn = Math.atan2( y0-y1, x0-x1 );
				
				TextMetrics tm = ctx.measureText( name );
				double strlen = tm.getWidth();
				if( Math.abs(horn) > Math.PI/2.0 ) {
					horn += Math.PI;
					
					ctx.translate(xx2, yy2);
					ctx.rotate( horn );
					ctx.fillText( name, 3.0, 3.0);
					ctx.rotate( -horn );
					ctx.translate(-xx2, -yy2);
				} else {
					ctx.translate(xx2, yy2);
					ctx.rotate( horn );
					ctx.fillText( name, -strlen-3.0, 3.0 );
					ctx.rotate( -horn );
					ctx.translate(-xx2, -yy2);
				}
			}
			
			/*double[] taxonPoint = new double[] { xPosition + (length + 1.0) * directionX, yPosition + (length + 1.0) * directionY };
			/*Point2D taxonPoint = new Point2D.Double(xPosition
					+ ((length + 1.0) * directionX), yPosition
					+ ((length + 1.0) * directionY));

			Line2D taxonLabelPath = new Line2D.Double(nodePoint, taxonPoint);
			cache.tipLabelPaths.put(node, taxonLabelPath);*
			
			double x1 = 100.0*nodePoint[0] + 300.0;
			double y1 = 100.0*nodePoint[1] + 300.0;
			
			double x2 = 100.0*taxonPoint[0] + 300.0;
			double y2 = 100.0*taxonPoint[1] + 300.0;
			
			ctx.setStrokeStyle("#0000FF");
			ctx.beginPath();
			ctx.moveTo(x1,y1);
			ctx.lineTo(x2,y2);
			ctx.closePath();
			ctx.stroke();*/
		}

		/*Point2D nodeShapePoint = new Point2D.Double(xPosition
				+ ((length - 1.0) * directionX), yPosition
				+ ((length - 1.0) * directionY));
		Line2D nodeShapePath = new Line2D.Double(nodePoint, nodeShapePoint);
		cache.nodeShapePaths.put(node, nodeShapePath);

		// add the node point to the map of node points
		cache.nodePoints.put(node, nodePoint);*
		
		double[] nodeShapePoint = new double[] {xPosition + (length - 1.0) * directionX, yPosition + (length - 1.0) * directionY };
		
		double x1 = 100.0*nodePoint[0] + 300.0;
		double y1 = 100.0*nodePoint[1] + 300.0;
		
		double x2 = 100.0*nodeShapePoint[0] + 300.0;
		double y2 = 100.0*nodeShapePoint[1] + 300.0;
		
		ctx.setStrokeStyle("#FFFF00");
		ctx.beginPath();
		ctx.moveTo(x1,y1);
		ctx.lineTo(x2,y2);
		ctx.closePath();
		ctx.stroke();*/

		return nodePoint;
	}
	
	double fontscale = 5.0;
	double hchunk = 10.0;
	public void drawTree( TreeUtil treeutil ) {
		int ww = getClientWidth();
		if( radial ) {
			if( treeutil != null ) {
				Node root = treeutil.getNode();
				//Browser.getWindow().getConsole().log("heyhey");
				count = 0;
				
				Context2d ctx = canvas.getContext2d();
				
				bounds[0] = Double.MAX_VALUE;
				bounds[1] = Double.MAX_VALUE;
				bounds[2] = Double.NEGATIVE_INFINITY;
				bounds[3] = Double.NEGATIVE_INFINITY;
				
				lbounds[0] = Double.MAX_VALUE;
				lbounds[1] = Double.MAX_VALUE;
				lbounds[2] = Double.NEGATIVE_INFINITY;
				lbounds[3] = Double.NEGATIVE_INFINITY;
				
				if( hchunk != 10.0 ) {
					String fontstr = (int)(fontscale*Math.log(hchunk))+"px sans-serif";
					if( !fontstr.equals(ctx.getFont()) ) ctx.setFont( fontstr );
				}
				
				constructNode( ctx, treeutil, root, 0.0, Math.PI * 2, 0.0, 0.0, 0.0, true );
				
				double xscale = (canvas.getCoordinateSpaceWidth()-10.0-(lbounds[2]-lbounds[0]))/(bounds[2]-bounds[0]);
				//double yscale = (canvas.getCoordinateSpaceHeight()-10.0-(lbounds[3]-lbounds[1]))/(bounds[3]-bounds[1]);
				//double scale = Math.min(xscale, yscale);
				double xoffset = 5.0-lbounds[0]-bounds[0]*xscale;
				double yoffset = 5.0-lbounds[1]-bounds[1]*xscale;
				
				int hval = (int)((bounds[3]-bounds[1])*xscale+(lbounds[3]-lbounds[1])+10);
				canvas.setSize((ww-10)+"px", hval+"px");
				canvas.setCoordinateSpaceWidth( ww-10 );
				canvas.setCoordinateSpaceHeight( hval );
				
				if( hchunk != 10.0 ) {
					String fontstr = (int)(fontscale*Math.log(hchunk))+"px sans-serif";
					if( !fontstr.equals(ctx.getFont()) ) ctx.setFont( fontstr );
				}
				
				ctx.setFillStyle("#FFFFFF");
				ctx.fillRect(0.0, 0.0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
				ctx.setStrokeStyle("#000000");
				constructNode( ctx, treeutil, root, 0.0, Math.PI * 2, 0.0, 0.0, 0.0, false );
			}
		} else {
			/*double minh = treeutil.getminh();
			double maxh = treeutil.getmaxh();
			
			double minh2 = treeutil.getminh2();
			double maxh2 = treeutil.getmaxh2();*/
			
			int leaves = root.getLeavesCount();
			int levels = root.countMaxHeight();
			
			nodearray = new Node[ leaves ];
			
			String treelabel = treeutil.getTreeLabel();
			
			int hsize = (int)(hchunk*leaves);
			if( treelabel != null ) hsize += 2*hchunk;
			if( showscale ) hsize += 2*hchunk;
			if( circular ) {
				canvas.setSize((ww-10)+"px", (ww-10)+"px");
				canvas.setCoordinateSpaceWidth( ww-10 );
				canvas.setCoordinateSpaceHeight( ww-10 );
			} else {
				canvas.setSize((ww-10)+"px", (hsize+2)+"px");
				canvas.setCoordinateSpaceWidth( ww-10 );
				canvas.setCoordinateSpaceHeight( hsize+2 );
			}
			
			boolean vertical = true;
			//boolean equalHeight = false;
			
			Treedraw.this.h = hchunk*leaves; //circular ? ww-10 : hchunk*leaves;
			Treedraw.this.w = ww - 10;
			
			if( vertical ) {
				dh = hchunk;
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
			ctx.setFillStyle("#FFFFFF");
			ctx.fillRect(0.0, 0.0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
			if( hchunk != 10.0 ) {
				String fontstr = (int)(fontscale*Math.log(hchunk))+"px sans-serif";
				if( !fontstr.equals(ctx.getFont()) ) ctx.setFont( fontstr );
			}
			if( treelabel != null ) {
				ctx.setFillStyle("#000000");
				ctx.fillText( treelabel, 10, hchunk+2 );
			}
			//console( "leaves " + leaves );
			//double	maxheightold = root.getMaxHeight();
			
			Node mnnode = getMaxNameLength( root, ctx );
			String maxstr = mnnode.getName();
			Node node = equalHeight > 0 ? mnnode : getMaxHeight( root, ctx, ww-30, showleafnames );
			if( node != null ) {
				double gh = getHeight(node);
				String name = node.getName();
				//if( node.getMeta() != null ) name += " ("+node.getMeta()+")";
				double textwidth = showleafnames ? ctx.measureText(name).getWidth() : 0.0;
				
				double mns = 0.0;
				if( showlinage ) {
					double ml = getMaxInternalNameLength( root, ctx );
					mns = ml+30;
				}
				double addon = mns;
				
				double maxheight = 0.0;
				if( circular ) maxheight = equalHeight > 0 ? ((ww-30)*circularScale-(textwidth)*2.0) : (gh*(ww-30)*circularScale)/((ww-60)*circularScale-(textwidth+mns)*2.0);
				else maxheight = equalHeight > 0 ? (ww-30-textwidth) : (gh*(ww-30))/(ww-60-textwidth-mns);
				
				if( equalHeight > 0 ) dw = maxheight/levels;
				
				if( vertical ) {
					//drawFramesRecursive( ctx, root, 0, treelabel == null ? 0 : hchunk*2, startx, Treedraw.this.h/2, equalHeight, false, vertical, maxheight, 0, addon );
					ci = 0;
					if( center ) drawTreeRecursiveCenter( ctx, root, 0, treelabel == null ? 0 : hchunk*2, startx, Treedraw.this.h/2, equalHeight, false, vertical, maxheight, addon, maxstr );
					else drawTreeRecursive( ctx, root, 0, treelabel == null ? 0 : hchunk*2, startx, Treedraw.this.h/2, equalHeight, false, vertical, maxheight, addon, maxstr );
				} else {
					drawFramesRecursive( ctx, root, 0, 0, Treedraw.this.w/2, starty, equalHeight, false, vertical, maxheight, 0, addon );
					ci = 0;
					if( center ) drawTreeRecursiveCenter( ctx, root, 0, 0, Treedraw.this.w/2, starty, equalHeight, false, vertical, maxheight, addon, maxstr );
					else drawTreeRecursive( ctx, root, 0, 0, Treedraw.this.w/2, starty, equalHeight, false, vertical, maxheight, addon, maxstr );
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
	
	public Node getMaxNameLength( Node root, Context2d ctx ) {
		List<Node>	leaves = new ArrayList<Node>();
		recursiveLeavesGet( root, leaves );
		
		Node sel = null;
		double max = 0.0;
		console( ""+leaves.size() );
		for( Node node : leaves ) {
			String name = node.getName();
			//if( node.getMeta() != null ) name += " ("+node.getMeta()+")";
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
				//if( node.getMeta() != null ) name += " ("+node.getMeta()+")";
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
	
	private double[] parseDistance( int len, String[] lines, List<String> names ) {
		double[] dvals = new double[ len*len ];
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
		
		return dvals;
	}
	
	public native String createObjectURL( elemental.html.Blob bb ) /*-{
		var urlstr = $wnd.URL.createObjectURL( bb );
		$wnd.console.log( urlstr );
		return urlstr;
	}-*/;
	
	List<Sequence> currentSeqs = null;
	public void handleText( String str ) {
		//Browser.getWindow().getConsole().log("erm " + str);
		if( str != null && str.length() > 1 && !str.startsWith("{") && !str.startsWith("\"") && !str.startsWith("!") ) {
			List<Sequence> seqs = currentSeqs;
			currentSeqs = null;
			//TreeUtil	treeutil;
			
			//elemental.html.Window wnd = Browser.getWindow();
			//Console cnsl = wnd.getConsole();
			/*if( cnsl != null ) {
				cnsl.log( "eitthvad i gangi" );
			}*/
			
			if( str.startsWith("propogate") ) {
				/*if( cnsl != null ) {
					cnsl.log( str );
				}*/
				
				int iof = str.indexOf('{');
				int eof = str.indexOf('}', iof+1);
				String[] split = str.substring(iof+1, eof).split(",");
				if( treeutil.getNode() != null ) {
					treeutil.propogateSelection( new HashSet<String>( Arrays.asList(split) ), treeutil.getNode() );
					handleTree();
				}
			} else if( str.startsWith("#") ) {
				int i = str.lastIndexOf("begin trees");
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
					TreeUtil treeutil = new TreeUtil();
					treeutil.init( tree, false, null, null, false, null, null, false );
					setTreeUtil( treeutil, tree );
					treeutil.replaceNames( treeutil.getNode(), namemap );
					handleTree();
				}
			} else if( str.startsWith(">") ) {
				//final TreeUtil 
				setTreeUtil( new TreeUtil(), str );
				try {
					final List<Sequence> lseq = importReader( str );
					currentSeqs = lseq;
					
					final DialogBox db = new DialogBox();
					VerticalPanel	dbvp = new VerticalPanel();
					
					final CheckBox ewCheck = new CheckBox("Entropy weighted dist-matrix");
					final CheckBox egCheck = new CheckBox("Exclude gaps");
					final CheckBox btCheck = new CheckBox("Bootstrap");
					final CheckBox ctCheck = new CheckBox("Jukes-cantor");
					
					final RadioButton rb = new RadioButton("Parsimony insertion", "parseChoice");
					if( seqs != null && seqs.get(0).getLength() == currentSeqs.get(0).getLength() ) {
						final RadioButton rb2 = new RadioButton("New tree", "parseChoice");
						
						rb2.addClickHandler( new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								if( !rb2.getValue() ) {
									ewCheck.setEnabled( false );
									egCheck.setEnabled( false );
									btCheck.setEnabled( false );
									ctCheck.setEnabled( false );
								} else {
									ewCheck.setEnabled( true );
									egCheck.setEnabled( true );
									btCheck.setEnabled( true );
									ctCheck.setEnabled( true );
								}
							}
						});
						
						dbvp.add( rb );
						dbvp.add( rb2 );
					}
					ctCheck.setValue( true );
					
					dbvp.add( ewCheck );
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
					
					db.addCloseHandler( new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							if( rb.getValue() ) {
								if( treeutil != null ) {
									
								}
							} else {
								boolean excludeGaps = egCheck.getValue();
								boolean bootstrap = btCheck.getValue();
								boolean cantor = ctCheck.getValue();
								boolean entropyWeight = ewCheck.getValue();
								
								List<Integer>	idxs = null;
								if( excludeGaps ) {
									int start = Integer.MIN_VALUE;
									int end = Integer.MAX_VALUE;
									
									for( Sequence seq : lseq ) {
										if( seq.getRealStart() > start ) start = seq.getRealStart();
										if( seq.getRealStop() < end ) end = seq.getRealStop();
									}
									
									idxs = new ArrayList<Integer>();
									for( int x = start; x < end; x++ ) {
										//int i;
										boolean skip = false;
										for( Sequence seq : lseq ) {
											char c = seq.charAt( x );
											if( c != '-' && c != '.' && c == ' ' ) {
												skip = true;
												break;
											}
										}
										
										if( !skip ) {
											idxs.add( x );
										}
									}
								}
								
								double[]	dvals = new double[ lseq.size()*lseq.size() ];
								double[] ent = null;
								if( entropyWeight ) ent = Sequence.entropy( lseq );
									
									/*if( idxs != null ) {
										int total = idxs.size();
										ent = new double[total];
										Map<Character,Integer>	shanmap = new HashMap<Character,Integer>();
										for( int x = 0; x < total; x++ ) {
											shanmap.clear();
											
											for( Sequence seq : lseq ) {
												char c = seq.charAt( idxs.get(x) );
												int val = 0;
												if( shanmap.containsKey(c) ) val = shanmap.get(c);
												shanmap.put( c, val+1 );
											}
											
											double res = 0.0;
											for( char c : shanmap.keySet() ) {
												int val = shanmap.get(c);
												double p = (double)val/(double)lseq.size();
												res -= p*Math.log(p);
											}
											ent[x] = res/Math.log(2.0);
										}
									} else {
										
									}
									}*/
							
								Sequence.distanceMatrixNumeric(lseq, dvals, idxs, false, cantor, ent);
								
								List<String>	names = new ArrayList<String>();
								for( Sequence seq : lseq ) {
									names.add( seq.getName() );
								}
								Node n = treeutil.neighborJoin( dvals, names, null, true );
								
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
										Sequence.distanceMatrixNumeric( lseq, dvals, idxs, true, cantor, ent );
										Node nn = treeutil.neighborJoin(dvals, names, null, true);
										treeutil.arrange( nn, comp );
										treeutil.compareTrees( tree, n, nn );
										
										//String btree = nn.toStringWoLengths();
										//System.err.println( btree );
									}
									treeutil.appendCompare( n );
								}
								setNode( n );
								handleTree();
							}
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if( str.startsWith("[") ) {
				int k = str.indexOf(']');
				int i = str.indexOf('(', k+1);
				if( i != -1 ) {
					String treestr = str.substring(i);
					String tree = treestr.replaceAll("[\r\n]+", "");
					TreeUtil treeutil = new TreeUtil();
					treeutil.init( tree, false, null, null, false, null, null, false );
					
					setTreeUtil( treeutil, str );
					handleTree();
				}
			} else if( !str.startsWith("(") ) {
				setTreeUtil( new TreeUtil(), str );
				final List<String> names;
				final double[] dvals;
				final int len;
				
				boolean b = false;				
				if( str.startsWith(" ") || str.startsWith("\t") ) {
					names = new ArrayList<String>();
					final String[] lines = str.split("\n");
					len = Integer.parseInt( lines[0].trim() );
					dvals = parseDistance( len, lines, names );
					
					if( root != null && len == root.countLeaves() ) {
						b = true;
						
						final DialogBox db = new DialogBox( false, true );
						db.setModal( true );
						db.getElement().getStyle().setBackgroundColor( "#EEEEEE" );
						//db.setSize("400px", "300px");
						VerticalPanel	vp = new VerticalPanel();
						vp.add( new Label("Apply distances to existing tree?") );
						HorizontalPanel hp = new HorizontalPanel();
						
						Button	yesb = new Button("Yes");
						Button nob = new Button("No");
						yesb.addClickHandler( new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								Node n = treeutil.neighborJoin( dvals, names, root, true );
								setNode( n );
								handleTree();
								
								db.hide();
							}
						});
						nob.addClickHandler( new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								Node n = treeutil.neighborJoin( dvals, names, null, true );
								setNode( n );
								handleTree();
								
								db.hide();
							}
						});
						
						hp.add( yesb );
						hp.add( nob );
						vp.add( hp );
						db.add( vp );
						db.center();
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
				
				if( !b ) {
					Node n = treeutil.neighborJoin( dvals, names, null, true );
					setNode( n );
					//console( treeutil.getNode().toString() );
					handleTree();
				}
			} else {
				//Browser.getWindow().getConsole().log("what");
				String tree = str.replaceAll("[\r\n]+", "");
				TreeUtil	treeutil =  new TreeUtil();
				treeutil.init( tree, false, null, null, false, null, null, false );
				setTreeUtil( treeutil, str );
				handleTree();
			}
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
	
	public void setNode( Node n ) {
		if( n == null ) console( "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeermmmmmmmmmmmmmmmmmmmmmmmmmmm" );
		else console( "fjorulalli " + n.toString() );
		if( n.toString().indexOf("bow_data") == -1 ) treeutil.setNode( n );
	}
	
	public native void click( JavaScriptObject e ) /*-{
		e.click();
	}-*/;
	
	TextBox label;
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
				if( label != null ) label.setText( file.getFilename() );
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
	
	private native String uploadToGoogleDrive( String name, String base64str ) /*-{
		$wnd.gdname = name;
		$wnd.gdbase = base64str;
		$wnd.console.log( 'about to' );
		$wnd.setTimeout($wnd.checkAuth, 1);
		
//		$wnd.console.log("ermi " + $wnd.sim);
//		if( $wnd.sim == 'simmi' ) {
//			$wnd.console.log("ermi2");
//			
//			var metadata = {
//            	'title': name,
//            	'mimeType': contentType
//            };
//			$wnd.doRequest( base64str, "text/plain", metadata );
//		}
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
				if( li != -1 ) node.setName( nname.substring(0,li), false );
			} else if( harshness == 1 ) {
				int li = nname.lastIndexOf('-');
				if( li != -1 ) {
					if( nname.charAt(li-3) == 'U' && nname.charAt(li-2) == 'S' && nname.charAt(li-1) == 'A' ) {
						int val = nname.indexOf('_', li+1);
						node.setName( nname.substring( 0, Math.min(nname.length(), val == -1 ? nname.length() : val) ), false );
					} else node.setName( nname.substring(0,li), false );
				}
			} else {
				int li = nname.lastIndexOf('_');
				if( li != -1 ) node.setName( nname.substring(0,li), false );
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
	
	public void reroot( int x, int y ) {		
		canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		if( !rerooting ) {
			rerooting = true;
			
			Node newroot = recursiveReroot( root, x, y );
			if( newroot != null ) {
				newroot.setParent( null );
				treeutil.reroot( newroot);
				root = newroot;
			}
			
			rerooting = false;
		}
		if( treeutil != null ) drawTree( treeutil );
	}
	
	public native boolean dropHandler( JavaScriptObject dataTransfer ) /*-{
		var succ = false;
		try {
			var file;
			if( dataTransfer.files.length > 0 ) file = dataTransfer.files[0];
			var s = this;
			if( file ) {
				succ = true;
				var reader = new FileReader();
				reader.onload = function(e) {
					var res = e.target.result;
					s.@org.simmi.client.Treedraw::handleText(Ljava/lang/String;)( res );
				};
				reader.readAsText( file );
			}
		} catch( e ) {
			if( $wnd.console ) $wnd.console.log( 'error '+e );
		}
		return succ;
	}-*/;
	
	public native boolean ieSpec() /*-{
		var s = this;
		function whatKey(evt) {
			var keycode = evt.keyCode;
			var c = evt.charCode;
			if( c == '\r' || c == '\n' ) {
				evt.stopPropagation();
				evt.preventDefault();
			}
			if( !s.@org.simmi.client.Treedraw::inTextBox ) s.@org.simmi.client.Treedraw::keyCheck(CIZ)( c, keycode, false );
		}
		$wnd.addEventListener('keypress', whatKey, true);
	}-*/;
	
	public void scaleMeta( Node n, double x ) {
		double frmo = n.getFrameOffset();
		if( frmo > 0.0 ) n.setFrameOffset( frmo+x );
		else n.setFrameOffset( 1.0 );
		for( Node rn : n.getNodes() ) {
			scaleMeta( rn, x );
		}
	}
	
	boolean oldie = false;
	boolean	ie = false;
	boolean inTextBox = false;
	public void keyCheck( char c, int keycode, boolean alt ) {
		Browser.getWindow().getConsole().log("ermuf");
		//boolean shift = (keycode | KeyCodes.KEY_SHIFT) != 0;
		if( c == 'p' || c == 'P' ) {
			if( c == 'p' ) {
				circularScale += 0.01;
			} else {
				circularScale -= 0.01;
			}
		} else if( c == 'x' || c == 'X' ) {
			if( c == 'x' ) {
				scaleMeta( root, -0.01 );
			} else {
				scaleMeta( root, 0.01 );
			}
		} else if( c == 'a' || c == 'A' ) {
			String[] ts = new String[] {"T.unknown", "T.composti", "T.rehai", "T.yunnanensis", "T.kawarayensis", "T.scotoductus", "T.thermophilus", "T.eggertsoni", "T.islandicus", "T.igniterrae", "T.brockianus", "T.aquaticus", "T.oshimai", "T.filiformis", "T.antranikianii", "T.unkownchile"};
			Collection<String> cset = c == 'A' ? new HashSet<String>( Arrays.asList(ts) ) : null;
			treeutil.collapseTreeAdvanced(root, cset, true);
			root.countLeaves();
		} else if( c == 't' || c == 'T' ) {
			if( c == 't' ) treeutil.nameParentNodes( treeutil.getNode() );
			else treeutil.nameParentNodesMeta( treeutil.getNode() );
		} else if( c == '*' ) {
			clientscale *= 1.25;
		} else if( c == '/' ) {
			clientscale *= 0.8;
		} else if( c == '_' ) {
			fontscale *= 1.25;
		} else if( c == '?' ) {
			fontscale *= 0.8;
		} else if( c == '+' ) {
			hchunk *= 1.25;
		} else if( c == '-' ) {
			hchunk *= 0.8;
		} else if( c == 'y' || c == 'Y' ) {
			treeutil.reduceParentSize( treeutil.getNode() );
		} else if( c == 'w' || c == 'W' ) {
			treeutil.swapNamesMeta( treeutil.getNode() );
		} else if( c == 'v' || c == 'V' ) {
			treeutil.replaceNamesMeta( treeutil.getNode() );
		} else if( c == 'n' || c == 'N' ) {
			showleafnames = !showleafnames;
		} else if( c == 'j' || c == 'J' ) {
			rightalign = !rightalign;
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
		} else if( c == 'g' || c == 'G' ) {
			if( treeutil != null && root != null ) {
				List<Node> nn = root.getNodes();
				if( nn != null && nn.size() == 3 ) {
					double h = 0.0;
					Node seln = null;
					for( Node n : nn ) {
						if( n.geth() > h ) {
							h = n.geth();
							seln = n; 
						}
					}
					root.removeNode( seln );
					Node newroot = treeutil.new Node();
					newroot.addNode( root, 1.0*h/3.0 );
					newroot.addNode( seln, 2.0*h/3.0 );
					treeutil.setNode( newroot );
					newroot.countLeaves();
					root = treeutil.getNode();
				}
			}
		} else if( selectedNode != null ) {
			if( c == 'f' || c == 'F' ) {
				treeutil.grisj( selectedNode );
				root.countLeaves();
			} else if( c == 'c' || c == 'C' ) {
				selectedNode.setCollapsed( selectedNode.isCollapsed() ? null : "collapsed" );
				root.countLeaves();
			} else if( c == 'd' || c == 'D' || keycode == KeyCodes.KEY_DELETE ) {
				Node parent = selectedNode.getParent();
				if( parent != null ) parent.removeNode( selectedNode );
				selectedNode = null;
				root.countLeaves();
			} else if( c == 'e' || c == 'E' || c == '\r' ) {				
				final TextBox	text = new TextBox();
				
				String newtext = selectedNode.getColor() == null ? selectedNode.getName() : selectedNode.getName() + "[" + selectedNode.getColor() + "]";
				if( selectedNode.getInfoList() != null ) {
					for( String info : selectedNode.getInfoList() ) {
						newtext += info;
					}
				}
				if( selectedNode.getFontSize() != -1.0 ) {
					//if( selectedNode.getFrameSize() != -1.0 ) 
					newtext += "{" + selectedNode.getFrameString() + "}";
					//else newtext += "{" + selectedNode.getFontSize() + "}";
				}
				if( selectedNode.getMeta() != null && selectedNode.getMeta().length() > 0 ) newtext += ";"+selectedNode.getMeta();
				
				text.setText( newtext );
				
				final PopupPanel	pp = new PopupPanel();
				pp.add( text );
				pp.setPopupPosition( (int)selectedNode.getCanvasX(), (int)selectedNode.getCanvasY()+canvas.getAbsoluteTop()-5 );
				pp.setAutoHideEnabled( true );
				inTextBox = true;
				pp.show();
				
				pp.addCloseHandler( new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						inTextBox = false;
					}
				});
				
				final Boolean[] b = new Boolean[1];
				b[0] = true;
				text.addKeyPressHandler( new KeyPressHandler() {
					@Override
					public void onKeyPress(KeyPressEvent event) {
						Browser.getWindow().getConsole().log("textbox key");
						
						event.stopPropagation();
						//event.preventDefault();
						
						int c = event.getCharCode();
						int key = event.getNativeEvent().getKeyCode();
						if( key == KeyCodes.KEY_ESCAPE ) {
							b[0] = false;
							pp.hide();
						} else if( key == KeyCodes.KEY_ENTER || key == 18 || c == '\n' || c == '\r' ) {
							pp.hide();
						}
					}
				});
				text.addKeyDownHandler( new KeyDownHandler() {
					@Override
					public void onKeyDown(KeyDownEvent event) {
						Browser.getWindow().getConsole().log("textbox down key");
						
						event.stopPropagation();
					}
				});
				/*text.addKeyDownHandler( new KeyDownHandler() {
					@Override
					public void onKeyDown(KeyDownEvent event) {
						event.stopPropagation();
						int c = event.getNativeEvent().getCharCode();
						int key = event.getNativeKeyCode();
						if( key == KeyCodes.KEY_ESCAPE ) {
							b[0] = false;
							pp.hide();
						} else if( key == KeyCodes.KEY_ENTER || key == 18 || c == '\n' || c == '\r' ) {
							pp.hide();
						}
					}
				});*/
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
				if( c == 'c' || c == 'C' ) {			pp.hide();
						} else console( Character.toString( c ) );
					}
				});*/
				pp.addCloseHandler( new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if( b[0] ) {
							String name = text.getText();
							selectedNode.setName( name );	
							if( treeutil != null ) drawTree( treeutil );
						}
					}
				});
				
				//String newtext = selectedNode.getColor() == null ? selectedNode.getName() : selectedNode.getName() + "[" + selectedNode.getColor() + "]";
				//if( selectedNode.getMeta() != null && selectedNode.getMeta().length() > 0 ) newtext += ";";
				text.setText( newtext );
				text.selectAll();
				//text.setFocus( true );
				/*selectedNode.getParent().removeNode( selectedNode );
				selectedNode = null;
				root.countLeaves();*/
			} else if( c == 's' || c == 'S' ) {
				Browser.getWindow().getConsole().log("canvas key");
				//canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
				//Node newroot = recursiveReroot( root, x, y );
				selectedNode.setParent( null );
				setNode( selectedNode );
					//treeutil.reroot(root, newroot);
				root = selectedNode;
				root.seth( 0.0 );
				root.seth2( 0.0 );
			} else if( c == 'i' || c == 'I' ) {
				treeutil.invertSelectionRecursive( root );
			} else if( c == 'r' || c == 'R' ) {
				if( treeutil != null && selectedNode != null ) {
					Browser.getWindow().getConsole().log("ermerm");
					if( !treeutil.isRooted() ) {
						Browser.getWindow().getConsole().log("sermerm");
						/*if( treeutil.getNode() != null ) {
							console( "not null first" );
							if( c == 'c' || c == 'C' ) {			
							console( "muu " + treeutil.getNode().toString() );
						} else {
							console( "null first" );
						}
						
						//console( selectedNode.toString() );
						if( treeutil.getNode() != null ) {
							console( "not null" );
							console( "muu " + treeutil.getNode().toString() );
						} else {
							console( "null" );
						}
						
						treeutil.reroot( selectedNode );
						//treeutil.rerootRecur( treeutil.currentNode, selectedNode );*/
					
						selectedNode.setParent( null );
						treeutil.reroot( selectedNode );
					} else {
						Browser.getWindow().getConsole().log("ermerms");
						Node selparent = selectedNode.getParent();
						if( selparent != null ) {
							double h2 = selectedNode.geth()/2.0;
							Node newroot = treeutil.new Node();
							treeutil.setNode( newroot );
							
							Node parent = selparent.getParent();
							double h = selparent.geth();
							
							if( parent == null ) {
								//List<Node> otherChilds = selparent.getOtherChild( selectedNode );							
								selparent.getNodes().remove( selectedNode );
								
								for( Node n : selparent.getNodes() ) {
									double oh2 = n.geth()/2.0;
									if( n == selparent.getNodes().get(0) ) newroot.addNode( selectedNode, h2+oh2 );
									newroot.addNode( n, (h2+oh2)*2.0 - selectedNode.geth() );
								}
							} else {
								selparent.getNodes().remove( selectedNode );
								newroot.addNode( selectedNode, h2 );
								newroot.addNode( selparent, h2 );
							}
							
							while( parent != null ) {
								Node nextparent = parent.getParent();
								
								double hh = parent.geth();
								if( nextparent == null ) {
									//Node otherchild = parent.getOtherChild( selparent );
									parent.getNodes().remove( selparent );
									
									for( Node n : parent.getNodes() ) {
										//double oh2 = n.geth()/2.0;
										//newroot.addNode( selectedNode, h2+oh2 );
										//newroot.addNode( n, h2+oh2 );
										selparent.addNode( n, hh+n.geth() );
									}								
								} else {
									parent.getNodes().remove( selparent );
									selparent.addNode( parent, h );
								}
								
								h = hh;
								selparent = parent;
								parent = nextparent;
							}
							newroot.countLeaves();
						}
					}
					root = treeutil.getNode();
				}
			}
		}
		if( treeutil != null ) {
			drawTree( treeutil );
		}
	}
	
	String 					treetext;
	int						dim;
	elemental.html.Window	myPopup;
	String 					domain = "http://webconnectron.appspot.com";
	public void showTree( String newtree, int dims ) {
		treetext = newtree;
		dim = dims;
		myPopup = Browser.getWindow().open(domain + "/Webconnectron.html?callback=webconnectron","_blank");
	};
	
	public String fetchSel() {
		return treeutil.getSelectString( treeutil.getNode(), true );
	}
	
	public native void initFuncs() /*-{
		var s = this;
		$wnd.fetchSel = function() {
			return s.@org.simmi.client.Treedraw::fetchSel();
		}
	}-*/;
	
	public native String atob( String dataurl ) /*-{
		var d = atob( dataurl );
		return d;
	}-*/;
	
	public native void createBlobTest( String byteStr, String mimeStr, elemental.html.FileWriter fileWriter, elemental.html.FileEntry fe ) /*-{
		var blob = new Blob( [ byteStr ], { type : "application/octet-stream" } );
		var fr = new FileReader();
		fr.onload = function( e ) {
			$wnd.console.log('okok'+mimeStr);
			var ub = new Uint8Array( e.target.result );
			var bb = new Blob( [ub], {type: mimeStr} );
			fileWriter.write( bb );
			$wnd.console.log('okok3');
			var url = fe.toURL();
			$wnd.open( url, "tree.png" );
		}
		fr.readAsArrayBuffer( blob );
	}-*/;
	
	public native elemental.html.Blob createStringBlob( String byteStr ) /*-{
		var blob = new Blob( [ byteStr ], { type : "text/plain" } );
		return blob;
	}-*/;
	
	public native elemental.html.Blob getCanvasBlob( CanvasElement canvas ) /*-{
		var blob;
		//console.log('e' + canvas.toBlob + ' ' + canvas.msToBlob );
		if( typeof canvas.msToBlob !== 'undefined' ) {
			blob = canvas.msToBlob();
		} else if( typeof canvas.toBlob !== 'undefined' ) {
			blob = canvas.toBlob();
		}
		return blob;
	}-*/;
	
	public native elemental.html.Blob createBlob( String byteStr, String mimeStr ) /*-{		
		var byteArray = new Uint8Array( byteStr.length );
	    for (var i = 0; i < byteStr.length; i++) {
	        byteArray[i] = byteStr.charCodeAt(i) & 0xff;
	    }
		
		var blob = new Blob( [ byteArray ], { type : "image/png" } );
		return blob;
	}-*/;
	
	public native void createBlob( String byteStr, String mimeStr, elemental.html.FileWriter fileWriter, elemental.html.FileEntry fe ) /*-{		
		var byteArray = new Uint8Array( byteStr.length );
        for (var i = 0; i < byteStr.length; i++) {
            byteArray[i] = byteStr.charCodeAt(i) & 0xff;
        }
		
		var blob = new Blob( [ byteArray ], { type : "image/png" } );
		fileWriter.write( blob );
		
		var url = fe.toURL();
		$wnd.open( url, "tree.png" );
	}-*/;
	
	public native JavaScriptObject createFlags() /*-{
		var flags = { create : true };
		return flags;
	}-*/;
	
	public native void saveOrOpenBlob( Blob blob, String name ) /*-{
		navigator.msSaveOrOpenBlob( blob, name );
	}-*/;
	
	public native void renderSaveToDrive( String id, String objurl, String filename ) /*-{
		try {
			$wnd.gapi.savetodrive.render( id, {
	          src: objurl,
	          filename: filename,
	          sitename: 'Sigmasoft Treedraw'
	        });
		} catch( e ) {
			$wnd.console.log( "savetodrive error " + e );
		}
	}-*/;
	
	@Override
	public void onModuleLoad() {
		final Console console = Browser.getWindow().getConsole();
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		initFuncs();
		//Drive d;
		
		//var domain = 'http://webconnectron.appspot.com';
		//var treetext = "";
		//var dim = 0;
		//var myPopup;
		/*function receiveMessage(event) {
			console.log( 'ready message received' );
			if (event.origin == "http://webconnectron.appspot.com") {
				console.log( 'correct origin' );
				if( treetext.length > 0 ) {
					myPopup.postMessage(dim+""+treetext,domain);
				} else {
					handleText( event.data );
				}
	    	} else if(event.origin == 'http://'+from+'.appspot.com') {
				console.log('correct webfasta origin');
				handleText( event.data );
			}
	  	}
		window.addEventListener("message", receiveMessage, false);*/
		
		final String domain = "http://webconnectron.appspot.com";
		final elemental.html.Window wnd = Browser.getWindow();
		wnd.addEventListener("message", new EventListener() {
			@Override
			public void handleEvent(Event evt) {				
				MessageEvent me = (MessageEvent)evt;
				String dstr = (String)me.getData();
				
				console.log("evrev");
				
				elemental.html.Window source = myPopup; //me.getSource()
				if( dstr.equals("fetchsel") ) {
					String selstr = treeutil.getSelectString( treeutil.getNode(), true );
					wnd.getOpener().postMessage( "propagate{"+selstr+"}", "*");
				} else {
					if( me.getOrigin().equals( domain ) ) {
						//Browser.getWindow().getConsole().log("no "+treetext);
						if( treetext.length() > 0 ) {
							//Browser.getWindow().getConsole().log("nos "+me.getSource());
							source.postMessage(dim+""+treetext,"*");
						} else {
							handleText( dstr );
						}
			    	} else {
			    		console.log("about "+dstr.length());
						handleText( dstr );
					}
				}
			}
		}, false);
		
		RootPanel	rp = RootPanel.get("canvas");
		/*rp.addDomHandler( new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, ContextMenuEvent.getType());*/
		
		/*popup = new PopupPanel( true );
		final MenuBar	menu = new MenuBar( true );
		popup.add( menu );
		
		menu.addItem( "Save tree", new Command() {
			@Override
			public void execute() {
				save( root.toString() );
				popup.hide();
			}
		});*/
		
		final FileUpload	file = new FileUpload();
		file.getElement().setId("fasta");
		
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
				event.preventDefault();
				if( !dropHandler( event.getDataTransfer() ) ) {
					//String str = event.getData("text/plain");
					String str = event.getData("Text");
					handleText( str );
				}
				
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
					reroot( event.getX(), event.getY() );
				} else {
					click( file.getElement() );
					//openFileDialog( 0 );
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
		
		KeyPressHandler keypressHandler = new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char c = event.getCharCode();
				int keycode = event.getNativeEvent().getKeyCode();
				if( c == '\r' || c == '\n' ) {
					event.stopPropagation();
					event.preventDefault();
				}
				//if( event.isControlKeyDown() ) {
					keyCheck( c, keycode, event.isShiftKeyDown() );
				//}
			}
		};
		String useragent = Window.Navigator.getUserAgent();
		console( "USERAGENT " + useragent );
		if( useragent.contains("MSIE") ) {
			/*canvas.addKeyDownHandler( new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					char c = (char)event.getNativeEvent().getCharCode();
					int keycode = event.getNativeKeyCode();//event.getNativeEvent().getKeyCode();
					if( c == '\r' || c == '\n' ) {
						event.stopPropagation();
						event.preventDefault();
					}
					//if( event.isControlKeyDown() ) {
						keyCheck( c, keycode );
					//}
				}
			});*/
			oldie = true;
			ieSpec();
		} else if( useragent.contains(".NET") ) {
			ie = true;
			ieSpec();
		} else {
			canvas.addKeyDownHandler( new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					console( "ok" );
				}
			});
			canvas.addKeyPressHandler( keypressHandler );
		}
		
		//console( canvas.getOffsetWidth() + "  " + canvas.getOffsetHeight() );
		canvas.setCoordinateSpaceWidth( w );
		canvas.setCoordinateSpaceHeight( h );
		Context2d context = canvas.getContext2d();
		
		String str = "Drop text in newick tree, distance matrix or aligned fasta format to this canvas";
		TextMetrics tm = context.measureText( str );
		context.fillText(str, (w-tm.getWidth())/2.0, h/2.0-8.0);
		str = "Double click to open file dialog (non-IE browsers)";
		tm = context.measureText( str );
		context.fillText(str, (w-tm.getWidth())/2.0, h/2.0+8.0);
		
		final Anchor	driveAnchor = new Anchor("google drive");
		driveAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Browser.getWindow().getConsole().log("ok");
				String treestr = root.toString();
				Browser.getWindow().getConsole().log("enc");
				String enctre = encode(treestr);
				Browser.getWindow().getConsole().log("done");
				//"data:text/plain;base64,"+enctre
				uploadToGoogleDrive( "treedraw.tree", enctre );
				//driveAnchor.setHref( "data:text/plain;base64,"+encode(root.toString()) );
			}
		});
		
		final SimplePanel	treeToDrive = new SimplePanel();
		treeToDrive.getElement().setId( "savetreetodrive" );
		final Anchor		treeAnchor = new Anchor("tree");
		treeAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//final Object[] create = {"create", true};
				//String[] create = {"create", "true"};
				
				boolean fail = false;
				try {
					elemental.html.Blob blob = createStringBlob( root.toString() );
					if( oldie || ie ) {
						saveOrOpenBlob( blob, "tree.txt" );
						//elemental.html.Window w = wnd.open( "tree.txt", "tree.txt" );
						//elemental.dom.Element el = w.getDocument().getElementById("treetext");
						//el.setInnerText( root.toString() );
					} else {
						String objurl = createObjectURL( blob );
						wnd.open( objurl, "tree.txt" );
						
						String title = label.getText();
						renderSaveToDrive( "savetreetodrive", objurl, title == null || title.length() == 0 ? "tree.png" : title+".png" );
					}
				} catch( Exception e ) {
					Browser.getWindow().getConsole().log("erm "+e.toString());
					fail = true;
				}

				if( fail ) {
					treeAnchor.setTarget("_blank");
					treeAnchor.setHref( "data:text/plain;base64,"+encode(root.toString()) );
				}
			}
		});
		
		final SimplePanel	imageToDrive = new SimplePanel();
		imageToDrive.getElement().setId( "saveimagetodrive" );
		final Anchor		imageAnchor = new Anchor("image");
		imageAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				imageAnchor.setTarget("");
				
				elemental.html.Blob blob = getCanvasBlob( canvas.getCanvasElement() );
				final String dataurl;
				if( blob == null ) {
					dataurl =  canvas.toDataUrl();
					
					String[] split = dataurl.split(",");
					String byteString = atob( split[1] );
				    String mimeString = split[0].split(":")[1].split(";")[0];
					blob = createBlob( byteString, mimeString );
					//final Object[] create = {"create", true};
					//String[] create = {"create", "true"};
					Browser.getWindow().getConsole().log("erme");
				} else dataurl = createObjectURL( blob );
				
				boolean fail = false;
				try {
					if( oldie || ie ) {
						saveOrOpenBlob(  blob, "tree.png" );
					} else {
						wnd.open( dataurl, "tree.png" );
					}
					
					String title = label.getText();
					renderSaveToDrive( "saveimagetodrive", dataurl, title == null || title.length() == 0 ? "tree.png" : title+".png" );
					//imageAnchor.setHref( objurl );
					
					/*wnd.webkitRequestFileSystem(elemental.html.Window.TEMPORARY, dataurl.length(), new FileSystemCallback() {
						@Override
						public boolean onFileSystemCallback(DOMFileSystem fileSystem) {
							console.log("in filesystem");
							fileSystem.getRoot().getFile("tree.png", createFlags(), new EntryCallback() {
								@Override
								public boolean onEntryCallback(Entry entry) {
									console.log("in file");
									final FileEntry fe = (FileEntry)entry;
									fe.createWriter( new FileWriterCallback() {
										@Override
										public boolean onFileWriterCallback(FileWriter fileWriter) {
											console.log("in write");
											
											//String d = dataurl.substring( dataurl.indexOf("base64,") + 7 );
									        //String decoded = atob(d);
										    
											createBlob( byteString, mimeString, fileWriter, fe );
											
											//fileWriter.write( bb );
											//wnd.open( fe.toURL(), "tree.png" );
											return true;
										}
									});
									return true;
								}
							});
							return true;
						}
					});*/
				} catch( Exception e ) {
					Browser.getWindow().getConsole().log("erm "+e.toString());
					fail = true;
				}

				if( fail ) {
					imageAnchor.setTarget("_blank");
					imageAnchor.setHref( dataurl );
				}
			}
		});
		final Anchor	dmAnchor = new Anchor("distance matrix");
		dmAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				StringBuilder dmsb = new StringBuilder();
				List<Node> leaves = treeutil.getLeaves( treeutil.getNode() );
				double[] d = treeutil.getDistanceMatrix( leaves );
				
				int size = leaves.size();
				dmsb.append( "\t"+size );
				for( int i = 0; i < d.length; i++ ) {
					if( i % size == 0 ) {
						dmsb.append( "\n"+leaves.get(i/size).getName() );
					}
					dmsb.append( "\t"+d[i] );
				}
				
				boolean fail = false;
				try {
					elemental.html.Blob blob = createStringBlob( dmsb.toString() );
					if( oldie || ie ) {
						saveOrOpenBlob( blob, "mat.txt" );
					} else {
						String objurl = createObjectURL( blob );
						wnd.open( objurl, "mat.txt" );
						
						String title = label.getText();
						renderSaveToDrive( "savetreetodrive", objurl, title == null || title.length() == 0 ? "mat.txt" : title+".txt" );
					}
				} catch( Exception e ) {
					Browser.getWindow().getConsole().log("erm "+e.toString());
					fail = true;
				}

				if( fail ) {
					dmAnchor.setTarget("_blank");
					dmAnchor.setHref( "data:text/plain;base64,"+encode(dmsb.toString()) );
				}
				
				//dmAnchor.setHref( "data:text/plain;base64,"+encode( dmsb.toString() ) );
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
							hchunk = 10.0;
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
		final Anchor	ltpAnchor = new Anchor("LTPs108_SSU");
		ltpAnchor.setTitle("Select sub-branch and press R.<br>Maximum canvas height is 32768 pixels");
		ltpAnchor.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "LTPs108_SSU_tree.newick");
				try {
					rb.sendRequest("", new RequestCallback() {
						@Override
						public void onResponseReceived(Request request, Response response) {
							hchunk = 10.0*0.8*0.8*0.8*0.8*0.8;
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
		
		final ListBox circularCheck = new ListBox();
		circularCheck.addItem("Default");
		circularCheck.addItem("Circular");
		circularCheck.addItem("Radial");
		circularCheck.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String sel = circularCheck.getItemText( circularCheck.getSelectedIndex() );
				circular = sel.equals("Circular");
				radial = sel.equals("Radial");
				
				drawTree( treeutil );
			}
		});
		/*circularCheck.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				circular = event.getValue();
				drawTree( treeutil );
			}
		});*/
		cluster.setValue( true );
		bc.add( bctext );
		bc.add( branch );
		bc.add( cluster );
		bc.add( circularCheck );
		
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
		CheckBox	roottree = new CheckBox("Root tree");
		roottree.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				boolean v = event.getValue();
				if( v && root.getNodes().size() == 3 ) {
					double maxh = 0.0;
					Node seln = null;
					for( Node n : root.getNodes() ) {
						if( n.geth() > maxh ) {
							maxh = n.geth();
							seln = n;
						}
					}
					
					Node newroot = treeutil.new Node();
					root.getNodes().remove( seln );
					newroot.addNode( seln, maxh/2.0 );
					newroot.addNode( root, maxh/2.0 );
					
					treeutil.setNode( newroot );
					root = treeutil.getNode();
					
					newroot.countLeaves();
				} else if( !v && root.getNodes().size() == 2 ) {
					root = treeutil.removeRoot( treeutil.getNode() );
					treeutil.setNode( root );
				}
				if( treeutil != null ) drawTree( treeutil );
			}
		});
		
		uselen.setValue( true );
		eqhp.add( uselen );
		eqhp.add( eqtext );
		eqhp.add( equidist );
		eqhp.add( equidep );
		eqhp.add( roottree );
		
		HorizontalPanel	hp = new HorizontalPanel();
		hp.setSpacing(5);
		HTML html = new HTML("Download as");
		hp.add( html );
		hp.add( treeAnchor );
		hp.add( treeToDrive );
		html = new HTML("or");
		hp.add( html );
		hp.add( imageAnchor );
		hp.add( imageToDrive );
		html = new HTML("or");
		hp.add( html );
		hp.add( dmAnchor );
		html = new HTML("or to");
		hp.add( html );
		hp.add( driveAnchor );
		
		html = new HTML(". View in");
		hp.add( html );
		Anchor td = new Anchor("3d");
		td.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showTree( treeutil.getNode().toString(), 3 );
			}
		});
		hp.add( td );
		Anchor twd = new Anchor("(2d)");
		twd.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showTree( treeutil.getNode().toString(), 2 );
			}
		});
		hp.add( twd );
		
		html = new HTML(". Run");
		hp.add( html );
		hp.add( sampleAnchor );
		html = new HTML(" or ");
		hp.add( html );
		hp.add( ltpAnchor );
		
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
		
		Label	selab = new Label("Selection ");
		Button	retsel = new Button("retain");
		retsel.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				treeutil.retainSelection( treeutil.getNode() );
				treeutil.getNode().countLeaves();
				drawTree( treeutil );
			}
		});
		HorizontalPanel	selpan = new HorizontalPanel();
		selpan.add( selab );
		selpan.add( retsel );
		
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
		label = new TextBox();
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
		CheckBox bubblecheck = new CheckBox("Node bubbles");
		bubblecheck.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				showbubble = event.getValue();
				drawTree( treeutil );	
			}
		});
		CheckBox linagecheck = new CheckBox("Internal node linages");
		linagecheck.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				showlinage = event.getValue();
				drawTree( treeutil );
			}
		});
			
		labhp.add( scalecheck );
		labhp.add( labcheck);
		labhp.add( label );
		labhp.add( bubblecheck );
		labhp.add( linagecheck );
		
		final FormPanel	form = new FormPanel();
		form.setAction("/");
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);
		form.add( file );
		file.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String filename = file.getFilename();
				int i = Math.max( filename.lastIndexOf('/'), filename.lastIndexOf('\\') );
				filename = filename.substring(i+1);
				i = filename.indexOf('.');
				if( label != null ) label.setText( filename.substring( 0, i == -1 ? filename.length() : i ) );
				handleFiles( file.getElement(), 0 );
			}
		});
		Button openbutton = new Button("Reload file");
		openbutton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleFiles( file.getElement(), 0 );
			}
		});
		HorizontalPanel openhp = new HorizontalPanel();
		openhp.setSpacing(7);
		openhp.add( form );
		openhp.add( openbutton );
		
		choicePanel.add( openhp );
		choicePanel.add( arrangehp );
		choicePanel.add( selpan );
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
			//String from =  Window.Location.getParameter("callback");
			console.log( "ok" );
			elemental.html.Window opener = Browser.getWindow().getOpener();
			opener.postMessage("ready", "*");
			
			/*console.log( "next" );
			String origin = opener.getLocation().getOrigin();
			if( origin != null ) {
				console.log( "origin " + origin );
				opener.postMessage("ready", origin);
			} else {
				console.log( "callback " + from );
				from = URL.decode( from );
				console.log( opener.getLocation().getHref() );
				if( from.contains("http") ) {
					opener.postMessage( "ready", from );
				} else {
					opener.postMessage( "ready", "http://"+from+".appspot.com" );
				}
			}*/
		} else if( Window.Location.getParameterMap().keySet().contains("tree") ) {
			String enctree = Window.Location.getParameter("tree");
			String tree = URL.decode( enctree );
			handleText( tree );
		} else if( Window.Location.getParameterMap().keySet().contains("ws") ) {
			String encws = Window.Location.getParameter("ws");
			String url = URL.decode( encws );
			Browser.getWindow().getConsole().log( "wsurl: " + url );
			newWebSocket( url );
		}
	}
	
	public native WebSocket newWebSocket( String url ) /*-{
		var s = this;
		var ws = new WebSocket( "ws://"+url );
		ws.onopen = function( e ) {
  			ws.send("ready");
		};
		ws.onmessage = function( e ) {
		 	s.@org.simmi.client.Treedraw::handleText(Ljava/lang/String;)( e.data );
		};	
		return ws;
	}-*/;
	
	double w;
	double h;
	double dw;
	double dh;
	
	List<String>	colors = new ArrayList<String>();
	int				ci = 0;
	
	Random	rnd = new Random();
	
	public native void console( String log ) /*-{
		if( $wnd.console ) $wnd.console.log( log );
	}-*/;
	
	public void drawFramesRecursive( Context2d g2, TreeUtil.Node node, double x, double y, double startx, double starty, int equalHeight, boolean noAddHeight, boolean vertical, double maxheight, int leaves, double addon ) {		
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
					drawFramesRecursive( g2, resnode, x+w, y+dh*total, nx, (dh*mleaves)/2.0, equalHeight, noAddHeight, vertical, maxheight, mleaves, addon );
				} else {
					//drawFramesRecursive( g2, resnode, x+dw*total, y+h, (dw*nleaves)/2.0, ny, paint ? shadeColor : null, nleaves, equalHeight );
					drawFramesRecursive( g2, resnode, x+dw*total, y+h, (dw*mleaves)/2.0, /*noAddHeight?starty:*/ny, equalHeight, noAddHeight, vertical, maxheight, mleaves, addon );
				}
				
				//drawFramesRecursive( g2, resnode, x+dw*total, y+h, (dw*nleaves)/2.0, ny, paint ? shadeColor : null, nleaves, equalHeight );
				total += nleaves;
			}
		}
	}
	
	public Node getNodeRecursive( TreeUtil.Node root, double x, double y ) {
		return null;
	}
	
	public double drawTreeRecursiveCenter( Context2d g2, TreeUtil.Node node, double x, double y, double startx, double starty, int equalHeight, boolean noAddHeight, boolean vertical, double maxheight, double addon, String maxstr ) {
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
					double newy = dh*total + drawTreeRecursiveCenter( g2, resnode, x+w, y+dh*total, nx, (dh*mleaves)/2.0, equalHeight, noAddHeight, vertical, maxheight, addon, maxstr );
					cmap.put( resnode, newy );
					nyavg += newy;
				} else {
					drawTreeRecursiveCenter( g2, resnode, x+dw*total, y+h, (dw*mleaves)/2.0, /*noAddHeight?starty:*/ny, equalHeight, noAddHeight, vertical, maxheight, addon, maxstr );
				}
			} else {
				if( vertical ) resnode.setCanvasLoc( nx, y+dh*total+(dh*mleaves)/2.0 );
				else resnode.setCanvasLoc( x+dw*total+(dw*mleaves)/2.0, ny );
			}
			
			total += mleaves;
		}
		
		double ret = (node.getNodes() != null && node.getNodes().size() > 0) ? nyavg/node.getNodes().size() : dh/2.0;
		if( vertical ) {
			if( circular ) {
				double a = 2.0*Math.PI*(y+ret)/h;
				node.setCanvasLoc( (w+startx*circularScale*Math.cos(a))/2.0, (w+startx*circularScale*Math.sin(a))/2.0 );
			} else node.setCanvasLoc( startx, y+ret );
			//node.setCanvasLoc( startx, y+ret );
		} else node.setCanvasLoc( x+ret, starty );
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
			} else {
				g2.setStrokeStyle( "#333333" );
				g2.setLineWidth( 1.0 );
			}
			g2.beginPath();
			if( vertical ) {
				double yfloor = y+newy; //Math.floor(y+newy);
				
				if( circular ) {
					double a1 = (2.0*Math.PI*(y+ret))/h;
					double a2 = (2.0*Math.PI*(yfloor))/h;
					
					g2.arc( w/2.0, w/2.0, startx*circularScale/2.0, a1, a2, a1 > a2 );
					/*if( a1 > a2 ) {
						g2.moveTo( (w+startx*circularScale*Math.cos(a2))/2.0, (w+startx*circularScale*Math.sin(a2))/2.0 );
						g2.arc( w/2.0, w/2.0, startx*circularScale/2.0, a2, a1 );
					} else {
						g2.moveTo( (w+startx*circularScale*Math.cos(a1))/2.0, (w+startx*circularScale*Math.sin(a1))/2.0 );
						g2.arc( w/2.0, w/2.0, startx*circularScale/2.0, a1, a2 );
					}*/
					g2.moveTo( (w+startx*circularScale*Math.cos(a2))/2.0, (w+startx*circularScale*Math.sin(a2))/2.0 );
					g2.lineTo( (w+nx*circularScale*Math.cos(a2))/2.0, (w+nx*circularScale*Math.sin(a2))/2.0 );
				} else {
					g2.moveTo( startx, y+ret );
					g2.lineTo( startx, yfloor );
					g2.moveTo( startx, yfloor );
					g2.lineTo( nx, yfloor );
				}
			} else {
				g2.moveTo( x+startx, starty );
				g2.lineTo( x+nx, starty );
				g2.lineTo( x+nx, ny );
			}
			g2.stroke();
			g2.closePath();
			
			if( showbubble ) {
				String ncolor = resnode.getColor();
				if( ncolor != null ) {
					g2.setFillStyle( ncolor );
				} else {
					g2.setFillStyle( "#000000" );
				}
				
				double mul = 1.0;
				if( resnode.getFrameSize() != -1 ) mul = resnode.getFrameSize();
				double radius = 1.5*mul;
				if( resnode.getNodes() == null || resnode.getNodes().size() == 0 ) radius = 3.0*mul;
				g2.beginPath();
				if( vertical ) {
					double yfloor = y+newy; //Math.floor(y+newy);
					
					if( circular ) {
						double a = 2.0*Math.PI*yfloor/h;
						g2.arc( (w+nx*circularScale*Math.cos(a))/2.0, (w+nx*circularScale*Math.sin(a))/2.0, radius, 0.0, 2*Math.PI);
					} else {
						g2.arc(nx, yfloor, radius, 0.0, 2*Math.PI);
					}
				} else {
					
				}
				g2.fill();
				g2.closePath();
			}
			//g2.setStroke( hStroke );
			//g2.setStroke( oldStroke );
			
			paintTree( g2, resnode, vertical, x, y, nx, newy /*Math.floor(newy)*/, addon, mleaves, ny, maxstr );
			total += mleaves;
		}
		
		return ret;
	}
	
	public double drawTreeRecursive( Context2d g2, TreeUtil.Node node, double x, double y, double startx, double starty, int equalHeight, boolean noAddHeight, boolean vertical, double maxheight, double addon, String maxstr ) {
		int total = 0;
		
		//double cirscl = 0.5;
		//double mdif = maxheight/w;
		//double cirmul = (cirscl-1.0)/mdif+1.0;
		double cx = startx*circularScale;
		if( vertical ) {
			if( circular ) {
				double a = 2.0*Math.PI*(y+starty)/h;
				node.setCanvasLoc( (w+cx*Math.cos(a))/2.0, (w+cx*Math.sin(a))/2.0 );
			} else node.setCanvasLoc( startx, y+starty );
		} else node.setCanvasLoc( x+startx, starty );
		
		for( TreeUtil.Node resnode : node.getNodes() ) {
			//String fontstr = (resnode.isSelected() ? "bold " : " ")+(int)(strh)+"px sans-serif";
			//if( !fontstr.equals(g2.getFont()) ) g2.setFont( fontstr );
			
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
			double cnx = nx*circularScale;
			
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
					drawTreeRecursive( g2, resnode, x+w, y+dh*total, nx, (dh*mleaves)/2.0, equalHeight, noAddHeight, vertical, maxheight, addon, maxstr );
				} else {
					drawTreeRecursive( g2, resnode, x+dw*total, y+h, (dw*mleaves)/2.0, /*noAddHeight?starty:*/ny, equalHeight, noAddHeight, vertical, maxheight, addon, maxstr );
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
				double yfloor = y+ny; //Math.floor(y+ny);
				if( circular ) {
					double a1 = (2.0*Math.PI*(y+starty))/h;
					double a2 = (2.0*Math.PI*(yfloor))/h;
					
					g2.arc( w/2.0, w/2.0, cx/2.0, a1, a2, a1 > a2 );
					/*if( a1 > a2 ) {
						g2.moveTo( (w+startx*circularScale*Math.cos(a2))/2.0, (w+startx*circularScale*Math.sin(a2))/2.0 );
						//g2.moveTo( (w+startx*Math.cos(y+starty))/2.0, (w+startx*Math.sin(y+starty))/2.0 );
						g2.arc( w/2.0, w/2.0, startx*circularScale/2.0, a2, a1 );
					} else {
						g2.moveTo( (w+startx*circularScale*Math.cos(a1))/2.0, (w+startx*circularScale*Math.sin(a1))/2.0 );
						//g2.moveTo( (w+startx*Math.cos(y+starty))/2.0, (w+startx*Math.sin(y+starty))/2.0 );
						g2.arc( w/2.0, w/2.0, startx*circularScale/2.0, a1, a2 );
					}*/
					//g2.closePath();
					//g2.stroke();
					//g2.beginPath();
					g2.moveTo( (w+cx*Math.cos( a2 ))/2.0, (w+cx*Math.sin( a2 ))/2.0 );
					g2.lineTo( (w+cnx*Math.cos( a2 ))/2.0, (w+cnx*Math.sin( a2 ))/2.0 );
					//g2.closePath();
				} else {
					g2.moveTo( startx, y+starty );
					g2.lineTo( startx, yfloor );
					//g2.moveTo( startx, yfloor );
					g2.lineTo( nx, yfloor );
				}
			} else {
				g2.moveTo( x+startx, starty );
				g2.lineTo( x+nx, starty );
				g2.lineTo( x+nx, ny );
			}
			g2.stroke();
			g2.closePath();
			
			if( showbubble ) {
				String ncolor = resnode.getColor();
				if( ncolor != null ) {
					g2.setFillStyle( ncolor );
				} else {
					g2.setFillStyle( "#000000" );
				}
				
				g2.beginPath();
				double mul = 1.0;
				if( resnode.getFrameSize() != -1 ) mul = resnode.getFrameSize();
				double radius = 1.5*mul;
				if( resnode.getNodes() == null || resnode.getNodes().size() == 0 ) radius = 3.0*mul;
				if( vertical ) {
					double yfloor = y+ny; //Math.floor(y+ny);
					if( circular ) {
						double a = 2.0*Math.PI*yfloor/h;
						g2.arc( (w+nx*circularScale*Math.cos(a))/2.0, (w+nx*circularScale*Math.sin(a))/2.0, radius, 0.0, 2*Math.PI);
					} else {
						g2.arc(nx, yfloor, radius, 0.0, 2*Math.PI);
					}
				} else {
					
				}
				g2.fill();
				g2.closePath();
			}
			//g2.setStroke( hStroke );
			//g2.setStroke( oldStroke );
			
			paintTree( g2, resnode, vertical, x, y, nx, ny, addon, mleaves, ny, maxstr );
			total += mleaves;
		}
		
		return /*(node.getNodes() != null && node.getNodes().size() > 0) ? nyavg/node.getNodes().size() : */0.0;
	}
	
	public boolean internalrotate = false;
	public void drawSingleMundi( boolean vertical, String use, Context2d g2, Node resnode, String color, double frmh, double frmo, double y, double realny, int mleaves, double addon, double strh, double nstrh ) {
		if( vertical ) {
			if( showlinage ) {
				if( circular ) {
					/*if( use != null && use.length() > 0 ) {
						drawMundi( g2, use, color, nstrh, frmh, frmo, y+realny, mleaves, (w-addon*2.0+5)*circularScale, true );
					}*/
					
					if( resnode.getMeta() != null && resnode.getMeta().length() > 0 ) {
						String[] metasplit = resnode.getMeta().split("_");
						
						int k = 0;
						for( String meta : metasplit ) {
							int mi = meta.indexOf( "[#" );
							if( mi == -1 ) mi = meta.length();
							int fi = meta.indexOf( "{" );
							if( fi == -1 ) fi = meta.length();
							String metadata = meta.substring(0,Math.min(mi,fi));
							
							String metacolor = null;
							if( mi < meta.length() ) {
								int me = meta.indexOf(']', mi+1 );
								metacolor = meta.substring(mi+1,me);
							}
							nstrh = strh;
							double mfrmh = strh;
							double metafontsize = 1.0;
							double metaframesize = 1.0;
							double metaframeoffset = -1.0;
							if( fi < meta.length() ) {
								int fe = meta.indexOf('}', fi+1 );
								String metafontstr = meta.substring(fi+1,fe);
								String[] mfsplit = metafontstr.split(" ");
								
								metafontsize = Double.parseDouble( mfsplit[0] );
								nstrh *= metafontsize;
								
								if( mfsplit.length > 1 ) {
									metaframesize = Double.parseDouble( mfsplit[1] );
									if( metaframesize != -1.0 ) mfrmh *= metaframesize;
								}
								if( mfsplit.length > 2 ) metaframeoffset = Double.parseDouble( mfsplit[2] );
							}
							
							k++;
							
							String fontstr = (resnode.isSelected() ? "bold " : " ")+(int)nstrh+"px sans-serif";
							if( !fontstr.equals(g2.getFont()) ) g2.setFont( fontstr );
							drawMundi( g2, metadata, metacolor, nstrh, mfrmh, metaframeoffset, y+realny, mleaves, (w-addon*2.0+5)*circularScale/*+(k*metaframesize*4.0)*/, true );
						}
					}
				}
			}
		}
	}
	
	int neveragain = 0;
	double circularScale = 0.9;
	public void paintTree( Context2d g2, Node resnode, boolean vertical, double x, double y, double nx, double ny, double addon, int mleaves, double realny, String maxstr ) {
		//int k = 12;//w/32;
		int fontSize = 10;
		
		String use = resnode.getName();// == null || resnode.getName().length() == 0 ? resnode.getMeta() : resnode.getName();
		use = resnode.isCollapsed() ? resnode.getCollapsedString() : use;
		boolean nullNodes = resnode.isCollapsed() || resnode.getNodes() == null || resnode.getNodes().size() == 0;
		boolean paint = (use != null && use.length() > 0) || (resnode.getMeta() != null && resnode.getMeta().length() > 0);
		
		if( paint ) {
			String color = resnode.getColor(); // == null ? "#FFFFFF" : resnode.getColor();
			
			double mhchunk = Math.max( 10.0, hchunk );
			//double strw = 0;
			double strh = fontscale*Math.log(mhchunk);
			double nstrh = resnode.getFontSize() == -1.0 ? strh : resnode.getFontSize()*strh;
			double frmh = strh;
			frmh = resnode.getFontSize() == -1.0 ? frmh : resnode.getFrameSize()*frmh;
			double frmo = resnode.getFrameOffset();
			
			if( nullNodes ) {
				if( showleafnames ) {
					g2.setFillStyle( "#000000" );
					//g2.setFont( bFont );
					
					String name = resnode.getName();
					/*if( resnode.getMeta() != null ) {
						String meta = resnode.getMeta();
						name += " ("+meta+")";
						
						/*if( meta.contains("T.ign") ) {
							System.err.println();
						}*
					}*/
					
					String[] split;
					if( name == null || name.length() == 0 && resnode.getCollapsedString() != null ) split = resnode.getCollapsedString().split("_");
					else split = new String[] { name }; //name.split("_");
					
					int t = 0;
					//double mstrw = 0;
					//double mstrh = strh;
					//double fontscale = resnode.getFontSize();
					//if( fontscale != -1.0 ) strh *= fontscale;
					
					String fontstr = (resnode.isSelected() ? "bold " : " ")+(int)nstrh+"px sans-serif";
					if( !fontstr.equals(g2.getFont()) ) g2.setFont( fontstr );
					
					if( !vertical ) {
						for( String str : split ) {
							TextMetrics tm = g2.measureText( str );
							double strw = tm.getWidth();
							//mstrw = Math.max( mstrw, strw );
							/*if( resnode.getColor() != null ) {
								g2.setFillStyle( resnode.getColor() );
								g2.fillRect( (int)(x+nx-strw/2.0), (int)(ny+4+10+(t++)*fontSize), strw, mstrh);
								g2.setFillStyle( "#000000" );
							}*/
							g2.fillText(str, (int)(x+nx-strw/2.0), (int)(ny+4+10+(t++)*fontSize) );
						}
					} else {
						for( String str : split ) {
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
								
								double nnstrh = ( ( (sup || sub) ? 3.0 : 5.0 )*nstrh/5.0 );
								double nfrmh = ( ( (sup || sub) ? 3.0 : 5.0 )*frmh/5.0 );
								//if( fontscale != -1.0 ) nnstrh *= fontscale;
								fontstr = (resnode.isSelected() ? "bold" : "")+(it ? " italic " : " ")+(int)nnstrh+"px sans-serif";
								if( !fontstr.equals(g2.getFont()) ) g2.setFont( fontstr );
								
								double maxstrw = g2.measureText( maxstr ).getWidth();
								
								int min = Collections.min( li );
								if( min < str.length() ) {
									int mini = li.indexOf( min );
									String tag = tags[mini];
									
									String substr = str.substring(start, min);
									double lx = nx+4.0+10.0+(t)*fontSize+pos;
									double ly = y+ny;
									if( circular ) {
										double a = (2.0*Math.PI*ly)/h;
										double val = rightalign ? w-addon+10 : lx;
										double cx = (w+val*circularScale*Math.cos( a ))/2.0;
										double cy = (w+val*circularScale*Math.sin( a ))/2.0;
										
										TextMetrics tm = g2.measureText( substr );
										double strw = tm.getWidth();
										if( a > Math.PI/2.0 && a < 3.0*Math.PI/2.0 ) {
											//u += 0.5*total;
											g2.translate( cx, cy );
											g2.rotate( a+Math.PI );
											if( !showbubble && resnode.getColor() != null ) {
												g2.setFillStyle( resnode.getColor() );
												g2.fillRect( -7+(t++)*fontSize  - (rightalign ? 0.0 : strw), nfrmh/2.0-nfrmh+1.0, (rightalign ? maxstrw : strw) + 15, nfrmh*1.15 );
												g2.setFillStyle( "#000000" );
											}
											g2.fillText( substr, rightalign ? 0.0 : -strw, strh/2.0 );
											g2.rotate( -a-Math.PI );
											g2.translate( -cx, -cy );
											
											List<String> infoList = resnode.getInfoList();
											if( infoList != null ) {
												val += strw;
												for( int i = 0; i < infoList.size(); i+=2 ) {
													cx = (w+val*circularScale*Math.cos( a ))/2.0;
													cy = (w+val*circularScale*Math.sin( a ))/2.0;
													
													String sstr = infoList.get(i);
													tm = g2.measureText( sstr );
													strw = tm.getWidth();
													
													g2.translate( cx, cy );
													g2.rotate( a+Math.PI );
													if( i+1 < infoList.size() && !showbubble ) {
														String colorstr = infoList.get(i+1);
														g2.setFillStyle( colorstr.substring( 1, colorstr.length()-1 ) );
														g2.fillRect( -7+(t++)*fontSize  - (rightalign ? 0.0 : strw), nfrmh/2.0-nfrmh+1.0, (rightalign ? maxstrw : strw) + 15, nfrmh*1.15 );
														g2.setFillStyle( "#000000" );
													}
													g2.fillText( sstr, rightalign ? 0.0 : -strw, strh/2.0 );
													g2.rotate( -a-Math.PI );
													g2.translate( -cx, -cy );
												
													val += strw;
												}
											}
										} else {
											g2.translate( cx, cy );
											g2.rotate( a );
											if( !showbubble && resnode.getColor() != null ) {
												g2.setFillStyle( resnode.getColor() );
												g2.fillRect( -7+(t++)*fontSize - (rightalign ? maxstrw : 0.0), nfrmh/2.0-nfrmh+1.0, (rightalign ? maxstrw : strw) + 15, nfrmh*1.15 );
												g2.setFillStyle( "#000000" );
											}
											g2.fillText( substr, rightalign ? -strw : 0.0, nnstrh/2.0 );
											g2.rotate( -a );
											g2.translate( -cx, -cy );
											
											List<String> infoList = resnode.getInfoList();
											if( infoList != null ) {
												//val += strw;
												for( int i = 0; i < infoList.size(); i+=2 ) {
													cx = (w+val*circularScale*Math.cos( a ))/2.0;
													cy = (w+val*circularScale*Math.sin( a ))/2.0;
													
													String sstr = infoList.get(i);
													tm = g2.measureText( sstr );
													strw = tm.getWidth();
													
													g2.translate( cx, cy );
													g2.rotate( a );
													if( i+1 < infoList.size() && !showbubble ) {
														String colorstr = infoList.get(i+1);
														g2.setFillStyle( colorstr.substring( 1, colorstr.length()-1 ) );
														g2.fillRect( -7+(t++)*fontSize - (rightalign ? 0.0 : -strw), nfrmh/2.0-nfrmh+1.0, (rightalign ? strw : strw) + 15, nfrmh*1.15 );
														g2.setFillStyle( "#000000" );
													}
													g2.fillText( sstr, rightalign ? 0.0 : strw, nnstrh/2.0 );
													g2.rotate( -a );
													g2.translate( -cx, -cy );
												
													val += strw;
												}
											}
										}
										
										/*g2.beginPath();
										g2.moveTo(w/2.0, w/2.0);
										g2.lineTo(cx, cy);
										g2.stroke();
										g2.closePath();*/
										
										//g2.fillText(substr, (w+lx*0.8*Math.cos( a ))/2.0, (w+lx*0.8*Math.sin( a ))/2.0 );
									} else {
										if( !showbubble && resnode.getColor() != null ) {
											double strw = g2.measureText( str ).getWidth();
											g2.setFillStyle( resnode.getColor() );
											g2.fillRect( nx+4+10+(t++)*fontSize, y+ny+nnstrh/2.0-nnstrh+1.0, strw+15, nnstrh*1.15);
											g2.setFillStyle( "#000000" );
										}
										
										ly += nnstrh/2.0;
										if( !rightalign ) {
											g2.fillText(substr, lx, ly );
										} else {
											TextMetrics tm = g2.measureText( substr );
											double strw = tm.getWidth();
											g2.fillText(substr, w-addon-strw, ly );
										}
									}
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
									//fontstr = (resnode.isSelected() ? "bold" : "")+(it ? " italic " : " ")+(int)( ( (sup || sub) ? 3.0 : 5.0 )*Math.log(hchunk) )+"px sans-serif";
									//if( !fontstr.equals(g2.getFont()) ) g2.setFont( fontstr );
									
									String substr = str.substring(start, str.length());
									
									double lx = nx+4+10+(t)*fontSize+pos;
									double ly = y+ny;
									if( circular ) {
										double a = (2.0*Math.PI*ly)/h;
										double val = rightalign ? w-addon+100 : lx;
										double cx = (w+val*circularScale*Math.cos( a ))/2.0;
										double cy = (w+val*circularScale*Math.sin( a ))/2.0;
										//double cx = (w+val*Math.cos( a ))/2.0;
										//double cy = (w+val*Math.sin( a ))/2.0;
										
										TextMetrics tm = g2.measureText( substr );
										double strw = tm.getWidth();
										if( a > Math.PI/2.0 && a < 3.0*Math.PI/2.0 ) {
											//u += 0.5*total;
											g2.translate( cx, cy );
											g2.rotate( a+Math.PI );
											if( !showbubble && resnode.getColor() != null ) {
												g2.setFillStyle( resnode.getColor() );
												g2.fillRect( -7+(t++)*fontSize  - (rightalign ? 0.0 : strw), nfrmh/2.0-nfrmh+1.0, (rightalign ? maxstrw : strw) + 15, nfrmh*1.15  );
												g2.setFillStyle( "#000000" );
											}
											g2.fillText( substr, rightalign ? 0.0 : -strw, nnstrh/2.0 );
											g2.rotate( -a-Math.PI );
											g2.translate( -cx, -cy );
											
											List<String> infoList = resnode.getInfoList();
											if( infoList != null ) {
												//val += strw;
												for( int i = 0; i < infoList.size(); i+=2 ) {
													cx = (w+val*circularScale*Math.cos( a ))/2.0;
													cy = (w+val*circularScale*Math.sin( a ))/2.0;
													
													String sstr = infoList.get(i);
													tm = g2.measureText( sstr );
													strw = tm.getWidth();
													
													g2.translate( cx, cy );
													g2.rotate( a+Math.PI );
													if( i+1 < infoList.size() && !showbubble ) {
														String colorstr = infoList.get(i+1);
														g2.setFillStyle( colorstr.substring( 1, colorstr.length()-1 ) );
														g2.fillRect( -7-(t)*strw  - (rightalign ? strw : 0.0) - 0, nfrmh/2.0-nfrmh+1.0, (rightalign ? strw : strw) + 10, nfrmh*1.15 );
														g2.setFillStyle( "#000000" );
													}
													g2.fillText( sstr, -7-(t)*strw + (rightalign ? -strw : 0.0) + 5, strh/2.0 );
													g2.rotate( -a-Math.PI );
													g2.translate( -cx, -cy );
												
													t++;
													val += strw;
													
													//break;
												}
											}
										} else {
											g2.translate( cx, cy );
											g2.rotate( a );
											if( !showbubble && resnode.getColor() != null ) {
												g2.setFillStyle( resnode.getColor() );
												g2.fillRect(-7+(t++)*fontSize  - (rightalign ? maxstrw : 0.0), nfrmh/2.0-nfrmh+1.0, (rightalign ? maxstrw : strw) + 15, nfrmh*1.15  );
												g2.setFillStyle( "#000000" );
											}
											g2.fillText( substr, rightalign ? -strw : 0.0, nnstrh/2.0 );
											g2.rotate( -a );
											g2.translate( -cx, -cy );
											
											List<String> infoList = resnode.getInfoList();
											//for( String info : infoList ) console( info );
											if( infoList != null ) {
												double tstrw = strw;
												//val += strw;
												for( int i = 0; i < infoList.size(); i+=2 ) {
													cx = (w+val*circularScale*Math.cos( a ))/2.0;
													cy = (w+val*circularScale*Math.sin( a ))/2.0;
													
													String sstr = infoList.get(i);
													tm = g2.measureText( sstr );
													strw = tm.getWidth();
													tstrw += strw;
													
													g2.translate( cx, cy );
													g2.rotate( a );
													if( i+1 < infoList.size() && !showbubble ) {
														String colorstr = infoList.get(i+1);
														g2.setFillStyle( colorstr.substring( 1, colorstr.length()-1 ) );
														g2.fillRect( -7+(t)*strw - (rightalign ? 0.0 : strw) + 5, nfrmh/2.0-nfrmh+1.0, (rightalign ? strw : strw) + 10, nfrmh*1.15 );
														g2.setFillStyle( "#000000" );
													}
													g2.fillText( sstr, -7+(t)*strw + (rightalign ? 0.0 : strw) + 10, nnstrh/2.0 );
													g2.rotate( -a );
													g2.translate( -cx, -cy );
												
													t++;
													val += strw;
													
													//break;
												}
											}
										}
										
										//double a = (2.0*Math.PI*ly)/h;
										//g2.fillText(substr, (w+lx*0.8*Math.cos( a ))/2.0, (w+lx*0.8*Math.sin( a ))/2.0 );
									} else {
										if( !showbubble && resnode.getColor() != null ) {
											double strw = g2.measureText( str ).getWidth();
											g2.setFillStyle( resnode.getColor() );
											g2.fillRect( nx+4+10+(t++)*fontSize, y+ny+nnstrh/2.0-nnstrh+1.0, strw+15, nnstrh*1.15);
											g2.setFillStyle( "#000000" );
										}
										
										ly += nnstrh/2.0;
										if( !rightalign ) {
											g2.fillText(substr, lx, ly );
										} else {
											TextMetrics tm = g2.measureText( substr );
											double strw = tm.getWidth();
											g2.fillText(substr, w-addon-strw, ly );
										}
									}
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
					
					//if( neveragain < 450 ) {
						drawSingleMundi( vertical, use, g2, resnode, color, frmh, frmo, y, realny, mleaves, addon, strh, nstrh );
						//neveragain++;
					//}
				}
			} else {
				boolean b = use.length() > 2;
				
				if( color != null && color.length() > 0 ) g2.setFillStyle( color );
				else g2.setFillStyle( "#000000" );
				
				String fontstr = (resnode.isSelected() ? "bold " : " ")+(int)nstrh+"px sans-serif";
				if( !fontstr.equals(g2.getFont()) ) g2.setFont( fontstr );
				//String[] split = use.split( "_" );
				TextMetrics tm;
				double strw = 0.0;
				if( b ) {
					//g2.setFont( lFont );
					//for( String s : split ) {
					tm = g2.measureText( use );
					strw = Math.max( strw, tm.getWidth() );
					//}
				} else {
					//g2.setFont( bFont );
					//for( String s : split ) {
						//strw = Math.max( strw, g2.measureText( use ).getWidth() );
					//}
					tm = g2.measureText( use );
					strw = Math.max( strw, tm.getWidth() );
				}
				
				//double strh = Math.max( 10.0, hchunk );//10;
				
				if( !showlinage ) {
					if( color != null && color.length() > 0 ) { 
						g2.setFillStyle( color );
					
						if( vertical ) {
							if( circular ) {
								double a = 2.0*Math.PI*(y+ny)/h;
								double cx = (w+nx*circularScale*Math.cos(a))/2.0;
								double cy = (w+nx*circularScale*Math.sin(a))/2.0;
								g2.translate( cx, cy );
								g2.rotate( a );
								if( color != null && color.length() > 0 ) g2.fillRect( -(5*strw)/8, -(5*strh)/8, (5*strw)/4, strh*1.2 );
								else {
									g2.setStrokeStyle("#000000");
									g2.strokeRect( -(5*strw)/8, -(5*strh)/8, (5*strw)/4, strh*1.2 );
								}
								g2.rotate( -a );
								g2.translate( -cx, -cy );
							} else g2.fillRect( nx-(5*strw)/8, y+ny-(5*strh)/8, (5*strw)/4, strh*1.2 );
						} else g2.fillRect( x+nx-(5*strw)/8, ny-strh/2.0, (5*strw)/4, strh*1.2 );
						//g2.fillRoundRect(startx, starty, width, height, arcWidth, arcHeight)
						//g2.fillOval( x+nx-k/2, ny-k/2, k, k );
						g2.setFillStyle( "#000000" );
					}
				}
				
				//int i = 0;
				if( vertical ) {
					if( showlinage ) {
						if( circular ) {
							if( use != null && use.length() > 0 ) {
								drawMundi( g2, use, color, nstrh, frmh, frmo, y+realny, mleaves, (w-addon*2.0+5)*circularScale, false );
							}
							
							if( resnode.getMeta() != null && resnode.getMeta().length() > 0 ) {
								String[] metasplit = resnode.getMeta().split("_");
								
								int k = 0;
								for( String meta : metasplit ) {
									int mi = meta.indexOf( "[#" );
									if( mi == -1 ) mi = meta.length();
									int fi = meta.indexOf( "{" );
									if( fi == -1 ) fi = meta.length();
									String metadata = meta.substring(0,Math.min(mi,fi));
									
									String metacolor = null;
									if( mi < meta.length() ) {
										int me = meta.indexOf(']', mi+1 );
										metacolor = meta.substring(mi+1,me);
									}
									nstrh = strh;
									double mfrmh = strh;
									double metafontsize = 1.0;
									double metaframesize = 1.0;
									double metaframeoffset = -1.0;
									if( fi < meta.length() ) {
										int fe = meta.indexOf('}', fi+1 );
										String metafontstr = meta.substring(fi+1,fe);
										String[] mfsplit = metafontstr.split(" ");
										
										metafontsize = Double.parseDouble( mfsplit[0] );
										nstrh *= metafontsize;
										
										if( mfsplit.length > 1 ) {
											metaframesize = Double.parseDouble( mfsplit[1] );
											if( metaframesize != -1.0 ) mfrmh *= metaframesize;
										}
										if( mfsplit.length > 2 ) metaframeoffset = Double.parseDouble( mfsplit[2] );
									}
									
									k++;
									
									fontstr = (resnode.isSelected() ? "bold " : " ")+(int)nstrh+"px sans-serif";
									if( !fontstr.equals(g2.getFont()) ) g2.setFont( fontstr );
									drawMundi( g2, metadata, metacolor, nstrh, mfrmh, metaframeoffset, y+realny, mleaves, (w-addon*2.0+5)*circularScale/*+(k*metaframesize*4.0)*/, false );
								}
							}
						} else {
							g2.fillText(use, w-addon+10, y+realny+nstrh/2.3 );
							double hdiff = (dh*(mleaves-1)/2.0);
							g2.beginPath();
							g2.moveTo(w-addon+5, y+realny-hdiff);
							//g2.lineTo(w-addon, ny);
							g2.lineTo(w-addon+5, y+realny+hdiff);
							g2.stroke();
							g2.closePath();
						}
					} else {
						if( circular ) {
							double a = 2.0*Math.PI*(y+ny)/h;
							double cx = (w+nx*circularScale*Math.cos(a))/2.0;
							double cy = (w+nx*circularScale*Math.sin(a))/2.0;
							
							g2.translate( cx, cy );
							if( internalrotate ) g2.rotate( a );
							g2.fillText(use, -strw/2.0, nstrh/2.3 );
							if( internalrotate ) g2.rotate( -a );
							g2.translate( -cx, -cy );
						} else {
							if( color != null && color.length() > 0 ) { 
								g2.fillText(use, nx-strw/2.0, y+ny+nstrh/2.3 );
							} else {
								g2.fillText(use, nx-strw-2.0, y+ny-2.0 );
							}
							
							/*if( b ) {
								//for( String s : split ) {
									//g2.fillText(s, nx-strw/2.0, y+ny+strh/2-1-8*(split.length-1)+i*16 );
									//i++;
								//}
								g2.fillText(use, nx-strw/2.0, y+ny+nstrh/2.3 );
							} else {
								//for( String s : split ) {
									//g2.fillText(s, nx-strw/2.0, y+ny+strh/2-1-8*(split.length-1)+i*16 );
									//i++;
								//}
								g2.fillText(use, nx-strw/2.0, y+ny+nstrh/2.3 );
							}*/
						}
					}
				} else {
					if( color != null && color.length() > 0 ) { 
						g2.fillText(use, x+nx-strw/2.0, ny+5 );
					} else {
						g2.fillText(use, x+nx-strw-2, ny-2 );
					}
					
					/*if( b ) {
						/*for( String s : split ) {
							strw = g2.measureText( s ).getWidth();
							g2.fillText(s, x+nx-strw/2.0, ny+5-8*(split.length-1)+i*16 );
							i++;
						}*
						g2.fillText(use, x+nx-strw/2.0, ny+5 );
					} else {
						/*for( String s : split ) {
							strw = g2.measureText(s).getWidth();
							g2.fillText(s, x+nx-strw/2.0, ny+6-8*(split.length)+i*16 );
							i++;
						}*
						g2.fillText(use, x+nx-strw/2.0, ny+6 );
					}*/
				}
			}
		}
	}
	
	public void drawMundi(Context2d g2, String use, String color, double strh, double frmh, double frmo, double yrealny, int mleaves, double rad, boolean single) {
		double hdiff = (dh * (mleaves - 1) / 2.0);
		double a1 = 2.0 * Math.PI * (yrealny - hdiff) / h;
		double a2 = 2.0 * Math.PI * (yrealny + hdiff) / h;

		if (frmo > 0.0) {
			rad *= frmo;
		}
		if (color != null && color.length() > 0) {
			g2.setLineWidth(frmh * 1.5);
			g2.setStrokeStyle(color);
			// g2.fillText(use, w-addon+10, y+realny+strh/2.3 );
			// double rad = w-addon+5;

			// g2.fillText(use, cx, cy );
			// double cy =
			g2.beginPath();

			// g2.moveTo( (w+cx*circularScale*Math.cos(a1))/2.0,
			// (w+cx*circularScale*Math.cos(a1))/2.0 );
			g2.arc(w / 2.0, w / 2.0, rad / 2.0, a1, a2, a1 > a2);
			// g2.lineTo(w-addon, ny);
			// g2.lineTo(w-addon+5, y+realny+hdiff);
			g2.stroke();
			g2.closePath();
			g2.setLineWidth(1.0);
		} else {
			/*
			 * g2.setLineWidth( strh*1.5 ); g2.setStrokeStyle( color );
			 * g2.beginPath(); g2.arc( w/2.0, w/2.0, rad/2.0, a1, a2, a1 > a2 );
			 * g2.stroke(); g2.closePath();
			 */

			g2.setLineWidth(1.0);
			g2.setStrokeStyle("#000000");
			// g2.setFillStyle("#FFEEEE");

			double cx1i = (w + (rad - frmh * 1.5) * Math.cos(a1)) / 2.0;
			double cy1i = (w + (rad - frmh * 1.5) * Math.sin(a1)) / 2.0;
			double cx2i = (w + (rad - frmh * 1.5) * Math.cos(a2)) / 2.0;
			double cy2i = (w + (rad - frmh * 1.5) * Math.sin(a2)) / 2.0;
			double cx1o = (w + (rad + frmh * 1.5) * Math.cos(a1)) / 2.0;
			double cy1o = (w + (rad + frmh * 1.5) * Math.sin(a1)) / 2.0;
			double cx2o = (w + (rad + frmh * 1.5) * Math.cos(a2)) / 2.0;
			double cy2o = (w + (rad + frmh * 1.5) * Math.sin(a2)) / 2.0;

			g2.beginPath();
			// g2.moveTo(cx1i, cy1i);
			// g2.lineTo(cx1o, cy1o);
			g2.arc(w / 2.0, w / 2.0, (rad + frmh * 1.5) / 2.0, a1, a2, false);
			g2.arc(w / 2.0, w / 2.0, (rad - frmh * 1.5) / 2.0, a2, a1, true); // rad+strh);
			// g2.arcTo(cx2i, cy2i, cx1i, cy1i, rad-strh);
			// g2.lineTo(cx2i, cy2i);
			g2.lineTo(cx1o, cy1o);
			// g2.fill();
			g2.stroke();
			g2.closePath();
		}

		g2.setStrokeStyle("#000000");
		g2.setFillStyle("#000000");
		String[] mysplit = use.split("_");
		String fstr = mysplit[0];
		String[] newsplit = new String[mysplit.length - 1];
		for (int i = 1; i < mysplit.length; i++) {
			newsplit[i - 1] = mysplit[i];
		}
		mysplit = newsplit;
		double a = 2.0 * Math.PI * (yrealny) / h;// (a1+a2)/2.0;

		double fstrw = g2.measureText(fstr).getWidth();
		double start = 0.0;
		
		if( single ) {
			if (a >= Math.PI/2.0 && a < 3.0*Math.PI/2.0 ) {
				double am = 2.0 * Math.PI * (yrealny) / h;// (a1+a2)/2.0;
				double cx = (w + (rad) * Math.cos(am)) / 2.0;
				double cy = (w + (rad) * Math.sin(am)) / 2.0;
				g2.translate(cx, cy);
				g2.rotate(am + Math.PI);
				g2.fillText( fstr, 0.0, 0.0);
				g2.rotate(-am - Math.PI);
				g2.translate(-cx, -cy);
			} else {
				double am = 2.0 * Math.PI * (yrealny) / h;// (a1+a2)/2.0;
				double cx = (w + (rad) * Math.cos(am)) / 2.0;
				double cy = (w + (rad) * Math.sin(am)) / 2.0;
				g2.translate(cx, cy);
				g2.rotate(am);
				g2.fillText( fstr, 0.0, 0.0);
				g2.rotate(-am);
				g2.translate(-cx, -cy);
			}
		} else {		
			if (a >= 0.0 && a < Math.PI) {
				for (int i = 0; i < fstr.length(); i++) {
					char c = fstr.charAt(i);
					double am = 2.0 * Math.PI * (yrealny) / h + 2.0 * (fstrw / 2.0 - start) / rad;// (a1+a2)/2.0;
					double cx = (w + (rad) * Math.cos(am)) / 2.0;
					double cy = (w + (rad) * Math.sin(am)) / 2.0;
					g2.translate(cx, cy);
					g2.rotate(am - Math.PI / 2.0);
					g2.fillText(c + "", 0.0, strh / 3.0);
					g2.rotate(-am + Math.PI / 2.0);
					g2.translate(-cx, -cy);
	
					start = g2.measureText(fstr.substring(0, i + 1)).getWidth();
				}
			} else {
				for (int i = 0; i < fstr.length(); i++) {
					char c = fstr.charAt(i);
					double am = 2.0 * Math.PI * (yrealny) / h - 2.0
							* (fstrw / 2.0 - start) / rad;// (a1+a2)/2.0;
					double cx = (w + (rad) * Math.cos(am)) / 2.0;
					double cy = (w + (rad) * Math.sin(am)) / 2.0;
					g2.translate(cx, cy);
					g2.rotate(am + Math.PI / 2.0);
					g2.fillText(c + "", 0.0, strh / 3.0);
					g2.rotate(-am - Math.PI / 2.0);
					g2.translate(-cx, -cy);
	
					start = g2.measureText(fstr.substring(0, i + 1)).getWidth();
				}
		}
		}

		if (a > Math.PI / 2.0 && a < 3.0 * Math.PI / 2.0) {
			int k = 0;
			for (String split : mysplit) {
				double substrw = g2.measureText(split).getWidth();
				double am = 2.0 * Math.PI
						* (yrealny - 0.8 * (mysplit.length - 1) + k * 1.6) / h;// (a1+a2)/2.0;
				double cx = (w + (rad + 10 + hchunk) * Math.cos(am)) / 2.0;
				double cy = (w + (rad + 10 + hchunk) * Math.sin(am)) / 2.0;
				g2.translate(cx, cy);
				g2.rotate(am + Math.PI);
				g2.fillText(split, -substrw, 0.0);
				g2.rotate(-am - Math.PI);
				g2.translate(-cx, -cy);

				k++;
			}
		} else {
			int k = 0;
			for (String split : mysplit) {
				double am = 2.0 * Math.PI
						* (yrealny - 0.8 * (mysplit.length - 1) + k * 1.6) / h;// (a1+a2)/2.0;
				double cx = (w + (rad + 10 + hchunk) * Math.cos(am)) / 2.0;
				double cy = (w + (rad + 10 + hchunk) * Math.sin(am)) / 2.0;
				g2.translate(cx, cy);
				g2.rotate(am);
				g2.fillText(split, 0.0, 0.0);
				g2.rotate(-am);
				g2.translate(-cx, -cy);

				k++;
			}
		}
	}
	
	/*public void drawMundi( Context2d g2, String use, String color, double strh, double frmh, double frmo, double yrealny, int mleaves, double rad ) {
		double hdiff = (dh*(mleaves-1)/2.0);
		double a1 = 2.0*Math.PI*(yrealny-hdiff)/h;
		double a2 = 2.0*Math.PI*(yrealny+hdiff)/h;
		
		if( frmo > 0.0 ) {
			rad *= frmo;
		}
		if( color != null && color.length() > 0 ) {
			g2.setLineWidth( frmh*1.5 );
			g2.setStrokeStyle( color );
			//g2.fillText(use, w-addon+10, y+realny+strh/2.3 );
			//double rad = w-addon+5;
			
			//g2.fillText(use, cx, cy );
			//double cy = 
			g2.beginPath();
			
			//g2.moveTo( (w+cx*circularScale*Math.cos(a1))/2.0, (w+cx*circularScale*Math.cos(a1))/2.0 );
			g2.arc( w/2.0, w/2.0, rad/2.0, a1, a2, a1 > a2 );
			//g2.lineTo(w-addon, ny);
			//g2.lineTo(w-addon+5, y+realny+hdiff);
			g2.stroke();
			g2.closePath();
			g2.setLineWidth( 1.0 );
		} else {
			/*g2.setLineWidth( strh*1.5 );
			g2.setStrokeStyle( color );
			g2.beginPath();
			g2.arc( w/2.0, w/2.0, rad/2.0, a1, a2, a1 > a2 );
			g2.stroke();
			g2.closePath();*
			
			g2.setLineWidth( 1.0 );
			g2.setStrokeStyle( "#000000" );
			//g2.setFillStyle("#FFEEEE");
			
			double cx1i = (w+(rad-frmh*1.5)*Math.cos( a1 ))/2.0;
			double cy1i = (w+(rad-frmh*1.5)*Math.sin( a1 ))/2.0;
			double cx2i = (w+(rad-frmh*1.5)*Math.cos( a2 ))/2.0;
			double cy2i = (w+(rad-frmh*1.5)*Math.sin( a2 ))/2.0;
			double cx1o = (w+(rad+frmh*1.5)*Math.cos( a1 ))/2.0;
			double cy1o = (w+(rad+frmh*1.5)*Math.sin( a1 ))/2.0;
			double cx2o = (w+(rad+frmh*1.5)*Math.cos( a2 ))/2.0;
			double cy2o = (w+(rad+frmh*1.5)*Math.sin( a2 ))/2.0;
			
			g2.beginPath();
			//g2.moveTo(cx1i, cy1i);
			//g2.lineTo(cx1o, cy1o);
			g2.arc(w/2.0, w/2.0, (rad+frmh*1.5)/2.0, a1, a2, false);
			g2.arc(w/2.0, w/2.0, (rad-frmh*1.5)/2.0, a2, a1, true); //rad+strh);
			//g2.arcTo(cx2i, cy2i, cx1i, cy1i, rad-strh);
			//g2.lineTo(cx2i, cy2i);
			g2.lineTo(cx1o, cy1o);
			//g2.fill();
			g2.stroke();
			g2.closePath();
		}
		
		g2.setStrokeStyle( "#000000" );
		g2.setFillStyle("#000000");
		String[] mysplit = use.split("_");
		String fstr = mysplit[0];
		String[] newsplit = new String[ mysplit.length-1 ];
		for( int i = 1; i < mysplit.length; i++ ) {
			newsplit[i-1] = mysplit[i];
		}
		mysplit = newsplit;
		double a = 2.0*Math.PI*(yrealny)/h;//(a1+a2)/2.0;
		
		double fstrw = g2.measureText( fstr ).getWidth();
		double start = 0.0;
		if( a >= Math.PI/2.0 && a < 3.0*Math.PI/2.0 ) {
			double am = 2.0*Math.PI*(yrealny)/h-2.0*(fstrw/2.0-start)/rad;//(a1+a2)/2.0;
			double cx = (w+(rad)*Math.cos(am))/2.0;
			double cy = (w+(rad)*Math.sin(am))/2.0;
			
			g2.translate( cx, cy );
			double val = Math.PI;
			g2.rotate( am+val );
			g2.fillText( fstr+"", 0.0, strh/3.0 );
			g2.rotate( -am-val );
			g2.translate( -cx, -cy );
		} else {
			double am = 2.0*Math.PI*(yrealny)/h-2.0*(fstrw/2.0-start)/rad;//(a1+a2)/2.0;
			double cx = (w+(rad)*Math.cos(am))/2.0;
			double cy = (w+(rad)*Math.sin(am))/2.0;
			
			g2.translate( cx, cy );
			double val = 0.0;
			g2.rotate( am+val );
			g2.fillText( fstr+"", 0.0, strh/3.0 );
			g2.rotate( -am-val );
			g2.translate( -cx, -cy );
		}
		
		if( a > Math.PI/2.0 && a < 3.0*Math.PI/2.0 ) {
			int k = 0;
			for( String split : mysplit ) {
				double substrw = g2.measureText( split ).getWidth();
				double am = 2.0*Math.PI*(yrealny-0.8*(mysplit.length-1)+k*1.6)/h;//(a1+a2)/2.0;
				double cx = (w+(rad+10+hchunk)*Math.cos(am))/2.0;
				double cy = (w+(rad+10+hchunk)*Math.sin(am))/2.0;
				g2.translate( cx, cy );
				double val = Math.PI * rnd.nextDouble();
				g2.rotate( am+val );
				//g2.fillText( split, -substrw, 0.0 );
				g2.rotate( -am-val );
				g2.translate( -cx, -cy );
				
				k++;
			}
		} else {
			int k = 0;
			for( String split : mysplit ) {
				double am = 2.0*Math.PI*(yrealny-0.8*(mysplit.length-1)+k*1.6)/h;//(a1+a2)/2.0;
				double cx = (w+(rad+10+hchunk)*Math.cos(am))/2.0;
				double cy = (w+(rad+10+hchunk)*Math.sin(am))/2.0;
				g2.translate( cx, cy );
				double val = Math.PI * rnd.nextDouble();
				g2.rotate( am+val );
				//g2.fillText( split, 0.0, 0.0 );
				g2.rotate( -am-val );
				g2.translate( -cx, -cy );
				
				k++;
			}
		}
	}*/
}
