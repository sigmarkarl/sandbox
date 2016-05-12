package org.simmi.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Prop extends PopupPanel {
	Connectron		connectron;
	Label			nLabel;
	Label			ktLabel;
	//JLabel			hmLabel;
	Label			textLabel;
	TextBox			name;
	TextBox			kt;
	Button			colorbutton;
	//JTextField		home;
	TextArea		text;
	String			color = "#ffffff";
	
	//JColorChooser	colorchooser = new JColorChooser();
	
	public Corp		currentCorp = null;
	String lightGray = "#dddddd";
	
	public void setBounds( int x, int y, int w, int h ) {
		this.setPopupPosition(x, y);
		this.setPixelSize(w, h);
	}
	
	public Prop() {
		nLabel = new Label("Name:");
		ktLabel = new Label("Type:");
		//hmLabel = new JLabel("Heimili:");
		textLabel = new Label("Text:");
		name = new TextBox();
		kt = new TextBox();
		//home = new JTextField();
		text = new TextArea();
		colorbutton = new Button("color");
		text.setReadOnly( false );
		
		VerticalPanel	vp = new VerticalPanel();
		vp.add(nLabel);
		vp.add(ktLabel);
		//this.add(hmLabel);
		vp.add(textLabel);
		vp.add(name);
		vp.add(kt);
		//this.add(home);
		vp.add(text);
		vp.add(colorbutton);
		
		this.setSize("400px", "300px");
		this.add( vp );
		
		KeyPressHandler kl = new KeyPressHandler() {			
			@Override
			public void onKeyPress(KeyPressEvent e) {
				if( e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER ) {
					Connectron ct = Prop.this.getConnectron();
					ct.remove( Prop.this );
					ct.repaint();
				}
			}
		};
		/*name.addKeyListener( kl );
		kt.addKeyListener( kl );
		text.addKeyListener( kl );
		
		colorbutton.setAction( new AbstractAction("color") {
			@Override
			public void actionPerformed(ActionEvent e) {
				color = JColorChooser.showDialog(Prop.this, "Node color", color);
			}
		});*/
	}
	
	public Connectron getConnectron() {
		return connectron;
	}
}
