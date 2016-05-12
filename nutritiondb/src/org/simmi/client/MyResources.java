package org.simmi.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.TextResource;

public interface MyResources extends ClientBundle {
	  public static final MyResources INSTANCE =  GWT.create(MyResources.class);
}