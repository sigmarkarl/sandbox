package org.simmi.treedraw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.simmi.treedraw.shared.TreeUtil;
import org.simmi.treedraw.shared.TreeUtil.Node;

public class TreeLegend {
	
	public static void colorMapRecursive( Node node, Map<String,String> colorMap ) {
		String color = node.getColor();
		if( color != null && color.length() > 0 ) {
			colorMap.put(color, node.getName());
		}
		List<Node> subnodes = node.getNodes();
		if( subnodes != null ) {
			for( Node n : subnodes ) {
				colorMapRecursive( n, colorMap );
			}
		}
	}
	
	public void treeLegend() {
		File f = new File( "/home/sigmar/new.tre" );
		char[] cbuf = new char[ (int )f.length() ];
		try {
			FileReader fr = new FileReader( f );
			fr.read( cbuf );
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TreeUtil tu = new TreeUtil();
		Node tree = tu.parseTreeRecursive( new String(cbuf), false );
		tu.setNode( tree );
		
		Map<String,String>	colorMap = new HashMap<String,String>();
		colorMapRecursive( tree, colorMap );
		
		Map<String,String>	nameMap = new TreeMap<String,String>();
		for( String color : colorMap.keySet() ) {
			String name = colorMap.get(color);
			name = name.substring(0, name.indexOf('_'));
			
			nameMap.put( name, color );
		}
		
		makeImage( nameMap );
	}
	
	public static void makeImage( Map<String,?> nameMap ) {
		BufferedImage	bi = new BufferedImage(400,500,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bi.createGraphics();
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont( g2.getFont().deriveFont(30.0f) );
		int i = 0;
		for( String name : nameMap.keySet() ) {
			Object colorObj = nameMap.get(name);
			Color c;
			if( colorObj instanceof Color ) c = (Color)colorObj;
			else {
				String color = (String)colorObj;
				c = new Color( Integer.parseInt(color.substring(1,3), 16), Integer.parseInt(color.substring(3,5), 16), Integer.parseInt(color.substring(5,7), 16) );
			}
			//System.err.println( color + "  " + name );
			
			g2.setColor( c );
			g2.fillRect(0, i, 45, 45);
			g2.setColor( Color.black );
			g2.drawString(name, 50, i+36);
			i += 50;
		}
		
		try {
			ImageIO.write(bi, "png", new File("/home/sigmar/legend.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Map<String,Color> locleg = new HashMap<String,Color>();
		/*locleg.put("Geysir norður", new Color(0.0f, 0.5f, 1.0f) );
		locleg.put("Geysir vestur", new Color(0.0f, 1.0f, 0.5f) );
		locleg.put("Flúðir", new Color(1.0f, 0.0f, 1.0f) );
		locleg.put("Ölkelduháls", new Color(1.0f, 1.0f, 0.0f) );
		locleg.put("Hrafntinnusker", new Color(0.0f, 0.0f, 1.0f) );
		locleg.put("Reykjadalir", new Color(0.0f, 1.0f, 0.0f) );
		locleg.put("Vondugil", new Color(1.0f, 0.0f, 0.0f) );
		locleg.put("Hurðarbak", new Color(1.0f, 0.0f, 0.5f) );
		locleg.put("Kleppjárnsreykir", new Color(1.0f, 0.5f, 0.0f) );
		locleg.put("Deildartunguhver", new Color(0.5f, 0.0f, 0.5f) );*/
		
		makeImage( locleg );
	}
}
