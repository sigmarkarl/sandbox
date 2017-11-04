package org.simmi.distann;

import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class TransferComponent extends JComponent implements DragGestureListener, DragSourceListener, Transferable, MouseListener {
	private static final long serialVersionUID = 1L;

	public BufferedImage	bimg;
	DragSource 				dragSource;
	Transferable			transferable;
	
	public TransferComponent( BufferedImage bimg, Transferable transferable ) {
		this.bimg = bimg;
		this.addMouseListener( this );
		this.transferable = transferable;
		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}
	
	@Override
	public void dragGestureRecognized(DragGestureEvent evt) {
	    dragSource.startDrag(evt, DragSource.DefaultCopyDrop, transferable, this);
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent(g);
		g.drawImage(bimg, 0, 0, this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.requestFocus();
		this.getTransferHandler().exportAsDrag(this, e, TransferHandler.COPY_OR_MOVE);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return new Object();
	}

	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
		
	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
		
	}

	@Override
	public void dragExit(DragSourceEvent dse) {
		
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		
	}
}
