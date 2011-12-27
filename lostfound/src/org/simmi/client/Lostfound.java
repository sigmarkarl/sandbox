package org.simmi.client;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Lostfound implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	public class Item {
		public Item( String type, Date date ) {
			this.type = type;
			this.date = date;
		}
		
		String	type;
		Date	date;
	};
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final RootPanel	rp = RootPanel.get();
		int w = Window.getClientWidth();
		int h = Window.getClientHeight();
		rp.setSize(w+"px", h+"px");
		
		Style st = rp.getElement().getStyle();
		st.setMargin(0.0, Unit.PX);
		st.setPadding(0.0, Unit.PX);
		st.setBorderWidth(0.0, Unit.PX);
		
		Window.addResizeHandler( new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w = event.getWidth();
				int h = event.getHeight();
				
				rp.setSize(w+"px", h+"px");
			}
		});
		
		HorizontalPanel	hp = new HorizontalPanel();
		hp.setVerticalAlignment( HorizontalPanel.ALIGN_MIDDLE );
		hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
		hp.setSize("100%", "100%");
		
		VerticalPanel	vpleft = new VerticalPanel();
		VerticalPanel	vpright = new VerticalPanel();
		
		hp.add( vpleft );
		hp.add( vpright );
		
		Label	l1 = new Label("Tapað");
		Label	l2 = new Label("Fundið");
		
		HorizontalPanel	lostwhat = new HorizontalPanel();
		Label	lostlab = new Label("Hvað týndist");
		ListBox	lostlist = new ListBox();
		lostlist.addItem("Húfa");
		lostlist.addItem("Vettlingar");
		lostlist.addItem("Fatnaður");
		lostlist.addItem("Húslyklar");
		lostlist.addItem("Bíllyklar");
		lostlist.addItem("Veski");
		lostlist.addItem("Kort");
		lostlist.addItem("GSM-sími");
		lostlist.addItem("Hringur");
		lostlist.addItem("Eyrnalokkar");
		lostlist.addItem("Hálsmen");
		lostlist.addItem("Hjól");
		lostlist.addItem("Sleði/Snjóþota");
		lostlist.addItem("Annað");
		lostwhat.add( lostlab );
		lostwhat.add( lostlist );
		
		CellTable<Item>	losttable = new CellTable<Item>();
		TextColumn<Item> typecol = new TextColumn<Item>() {
			@Override
			public String getValue(Item object) {
				return object.type;
			}
		};
		typecol.setSortable( true );
		TextColumn<Item> datecol = new TextColumn<Item>() {
			@Override
			public String getValue(Item object) {
				return object.date.toString();
			}
		};
		
		List<Item> items = Arrays.asList(
			    new Item("John", new Date(0)),
			    new Item("Mary", new Date(0)) );
		
		losttable.addColumn( typecol, "Hvað" );
		losttable.addColumn( datecol, "Hvenær" );
		
		losttable.setRowCount( items.size(), true );
		losttable.setRowData( items );
		
		//ListHandler<Item>	lh = new ListHandler<Item>( items );
		//losttable.addColumnSortHandler( lh );
		
		//lostwhat.add( losttable );
		
		ListDataProvider<Item> ldp = new ListDataProvider<Item>();
		ldp.addDataDisplay( losttable );
		List<Item>	litem = ldp.getList();
		for( Item i : items ) {
			litem.add( i );
		}
		ListHandler<Item> columnSortHandler = new ListHandler<Item>(litem);
		columnSortHandler.setComparator(typecol,
			new Comparator<Item>() {
		          public int compare(Item o1, Item o2) {
		            if (o1 == o2) {
		              return 0;
		            }

		            // Compare the name columns.
		            if (o1 != null) {
		              return (o2 != null) ? o1.type.compareTo(o2.type) : 1;
		            }
		            return -1;
		          }
		    });
		losttable.addColumnSortHandler(columnSortHandler);

		    // We know that the data is sorted alphabetically by default.
		losttable.getColumnSortList().push(typecol);
		
		vpleft.add( losttable );
		
		HorizontalPanel	foundwhat = new HorizontalPanel();
		Label	foundlab = new Label("Hvað fannst");
		ListBox	foundlist = new ListBox();
		foundlist.addItem("Húfa");
		foundlist.addItem("Vettlingar");
		foundlist.addItem("Fatnaður");
		foundlist.addItem("Húslyklar");
		foundlist.addItem("Bíllyklar");
		foundlist.addItem("Veski");
		foundlist.addItem("Kort");
		foundlist.addItem("GSM-sími");
		foundlist.addItem("Hringur");
		foundlist.addItem("Eyrnalokkar");
		foundlist.addItem("Hálsmen");
		foundlist.addItem("Hjól");
		foundlist.addItem("Sleði/Snjóþota");
		foundlist.addItem("Annað");
		foundwhat.add( foundlab );
		foundwhat.add( foundlist );
		
		HorizontalPanel	first = new HorizontalPanel();
		Label	firstlab = new Label("Í fyrsta lagi");
		DateBox	firstdate = new DateBox();
		first.add( firstlab );
		first.add( firstdate );
		
		HorizontalPanel	last = new HorizontalPanel();
		Label	lastlab = new Label("Í síðasta lagi");
		DateBox	lastdate = new DateBox();
		last.add( lastlab );
		last.add( lastdate );
		
		TextArea	lostdesc = new TextArea();
		TextArea	founddesc = new TextArea();
		
		vpleft.add( l1 );
		vpleft.add( lostwhat );
		vpleft.add( first );
		vpleft.add( lostdesc );
		vpright.add( l2 );
		vpright.add( foundwhat );
		vpright.add( last );
		vpright.add( founddesc );
		
		rp.add( hp );
	}
}