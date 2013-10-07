package org.simmi.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Nutritiondb implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		/*VerticalPanel	vp = new VerticalPanel();
		//vp.set
		vp.setHorizontalAlignment( VerticalPanel.ALIGN_CENTER );
		vp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
		vp.setSize("100%", "100%");
		
		//vp.add( new HTML("<h1>Food/Nutrition database (data from <a href=\"http://usda.gov/\">http://usda.gov/</a>)</h1>") );
		//vp.add( new HTML( "<script src=\"http://scripts.chitika.net/eminimalls/amm.js\" type=\"text/javascript\"></script>" ) );
		//vp.add( new HTML("All kinds of statistical summaries and graphics from the USDA food and nutrition database") );
		//HorizontalPanel	hp = new HorizontalPanel();
		//vp.add( new HTML("<applet codebase=\"http://nutritiondb.appspot.com\" archive=\"food_usda.jar,swingx-1.6.jar\" code=\"org.simmi.Food\" width=\"100%\" height=\"600\" jnlp_href=\"food.jnlp\"><param name=\"jnlp_href\" value=\"food.jnlp\"/></applet>") );
		
		//hp.add( new HTML( "<script src=\"http://scripts.chitika.net/eminimalls/amm.js\" type=\"text/javascript\"></script>" ) );
		//vp.add( hp );
		//vp.add( new HTML("<a href=\"mailto:huldaeggerts@gmail.com\">huldaeggerts@gmail.com</a> | More apps: <a href=\"http://webspectroscope.appspot.com\">http://webspectroscope.appspot.com</a> | <a href=\"http://websimlab.appspot.com\">http://websimlab.appspot.com</a> | <a href=\"http://fasteignaverd.appspot.com\">http://fasteignaverd.appspot.com</a>") );
		//vp.add( new HTML( "<script src=\"http://scripts.chitika.net/eminimalls/amm.js\" type=\"text/javascript\"></script>" ) );
		
		RootPanel.get("nutdb").add( vp );*/
		
		/*final RootPanel rp = RootPanel.get("stuff");
		int h = Window.getClientHeight();
		rp.setHeight(h+"px");
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int h = event.getHeight();
				rp.setHeight(h+"px");	
			}
		});*/
		
		
		/*final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("GWT User");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 *
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 *
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 *
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				greetingService.greetServer(textToServer, new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						dialogBox.setText("Remote Procedure Call - Failure");
						serverResponseLabel.addStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(SERVER_ERROR);
						dialogBox.center();
						closeButton.setFocus(true);
					}

					public void onSuccess(String result) {
						dialogBox.setText("Remote Procedure Call");
						serverResponseLabel.removeStyleName("serverResponseLabelError");
						serverResponseLabel.setHTML(result);
						dialogBox.center();
						closeButton.setFocus(true);
					}
				});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);*/
	}
}
