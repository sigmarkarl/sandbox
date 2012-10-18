package org.simmi;

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

import org.simmi.shared.TreeUtil;
import org.simmi.shared.TreeUtil.Node;

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
	
	public static void main(String[] args) {
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
		
		BufferedImage	bi = new BufferedImage(200,450,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bi.createGraphics();
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont( g2.getFont().deriveFont(36.0f) );
		int i = 0;
		for( String name : nameMap.keySet() ) {
			String color = nameMap.get(name);
			Color c = new Color( Integer.parseInt(color.substring(1,3), 16), Integer.parseInt(color.substring(3,5), 16), Integer.parseInt(color.substring(5,7), 16) );
			System.err.println( color + "  " + name );
			
			g2.setColor( c );
			g2.fillRect(0, i, 200, 45);
			g2.setColor( Color.black );
			g2.drawString(name, 20, i+36);
			i += 50;
		}
		
		try {
			ImageIO.write(bi, "png", new File("/home/sigmar/legend.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
