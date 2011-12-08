package org.simmi;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

public class MyFilter extends RowFilter<TableModel,Integer> {
	private String			filterText;
	int						fInd = 0;
	Set<String> 			cropped = new HashSet<String>();
	TableModel				leftModel;
	Locale					locale;
	
	public MyFilter( TableModel leftModel ) {
		super();
		
		this.leftModel = leftModel;
		this.locale = Locale.getDefault();
		try {
			this.locale = new Locale("is");
		} catch( Exception e ) {
			e.printStackTrace();
		}
		//this.locale = Locale.forLanguageTag("is");
	}
	
	public void setFilterText( String filtertext ) {
		this.filterText = filtertext == null ? null : filtertext.toLowerCase( locale ); //"(?i).*" + filtertext.toLowerCase( locale ) + ".*";
	}
	
	public boolean include( javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry ) {
		// String filterText = field.getText();
		
		String gval = ((String)leftModel.getValueAt(entry.getIdentifier(), 0)).toLowerCase( locale );
		String val = fInd == 0 ? gval : ((String)leftModel.getValueAt(entry.getIdentifier(), 1)).toLowerCase( locale );
		if (filterText != null) {
			if (val != null) {
				boolean b = val.matches( "(?i).*" + filterText + ".*" ); //contains(filterText); //matches(filterText);
				//if( b ) System.err.println( val + "  " + filterText + "  " + b );
				return b;
			}
			return false;
		} else {
			boolean b = cropped.contains(gval);
			if (b)
				return false;
		}
		return true;
	}
	
	public void addFilterListener( final Runnable r ) {
		/*addPipelineListener( new PipelineListener() {
			public void contentsChanged(PipelineEvent arg0) {
				r.run();
			}
		});*/
	}
}
