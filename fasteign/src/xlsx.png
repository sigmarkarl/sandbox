package org.simmi;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

public class Ibud {
	static Map<String, Integer> mmap = new HashMap<String, Integer>();

	static {
		mmap.put("Janúar", 1);
		mmap.put("Febrúar", 2);
		mmap.put("Mars", 3);
		mmap.put("Apríl", 4);
		mmap.put("Maí", 5);
		mmap.put("Júní", 6);
		mmap.put("Júlí", 7);
		mmap.put("Ágúst", 8);
		mmap.put("September", 9);
		mmap.put("Október", 10);
		mmap.put("Nóvember", 11);
		mmap.put("Desember", 12);

		mmap.put("janúar", 1);
		mmap.put("febrúar", 2);
		mmap.put("mars", 3);
		mmap.put("apríl", 4);
		mmap.put("maí", 5);
		mmap.put("júní", 6);
		mmap.put("júlí", 7);
		mmap.put("ágúst", 8);
		mmap.put("september", 9);
		mmap.put("október", 10);
		mmap.put("nóvember", 11);
		mmap.put("desember", 12);
	}
	
	StringProperty nafn;
	IntegerProperty verd;
	IntegerProperty fastmat;
	IntegerProperty brunmat;
	StringProperty teg;
	DoubleProperty ferm;
	IntegerProperty herb;
	Date dat;
	String url;
	String pnr;
	boolean	selected = false;
	String imgurl;
	IntegerProperty	fermverd;
	IntegerProperty	fmverdfm;
	DoubleProperty  verdfm;
	Property<Image>	image;
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelection( boolean sel ) {
		selected = sel;
	}
	
	public Image getMynd() {
		return image.getValue(); 
	}
	
	public Property<Image> myndProperty() {
		 if( image == null ) {
			 image = new Property<Image>() {
				Image img;
				
				@Override
				public Object getBean() {
					return Ibud.this;
				}

				@Override
				public String getName() {
					return "mynd";
				}

				@Override
				public void addListener(ChangeListener<? super Image> arg0) {}

				@Override
				public Image getValue() {
					if( img == null ) img = new Image( imgurl );
					return img;
				}

				@Override
				public void removeListener(ChangeListener<? super Image> arg0) {}

				@Override
				public void addListener(InvalidationListener arg0) {}

				@Override
				public void removeListener(InvalidationListener arg0) {}

				@Override
				public void setValue(Image arg0) {}

				@Override
				public void bind(ObservableValue<? extends Image> arg0) {}

				@Override
				public void bindBidirectional(Property<Image> arg0) {}

				@Override
				public boolean isBound() {
					return false;
				}

				@Override
				public void unbind() {}

				@Override
				public void unbindBidirectional(Property<Image> arg0) {}
			};
			//image.bind( Bindings.divide( verd, ferm ) );
		 }
		 return image;
	}
	
	public double getVerdPerFasteignamat() { return verdfmProperty().get(); };
	public DoubleProperty verdfmProperty() {
		 if( verdfm == null ) {
			 verdfm = new SimpleDoubleProperty(this, "verdfm");
			 
			 DoubleBinding db = new DoubleBinding() {
			    { super.bind(verdProperty(), fastmatProperty()); }
			 
			    @Override
			    protected double computeValue() {
			        if(fastmatProperty().getValue() == 0) {
			            return 0.0;
			        }
			        return verdProperty().doubleValue() / fastmatProperty().doubleValue();
			    }
			};
			verdfm.bind( db ); //Bindings.divide(verd, fastmat)
		 }
		 return verdfm;
	}
	
	public int getFermetraVerd() { return fermverdProperty().get(); };
	public IntegerProperty fermverdProperty() {
		 if( fermverd == null ) {
			 fermverd = new SimpleIntegerProperty(this, "fermverd");
			 fermverd.bind( Bindings.divide( verd, ferm ) );
		 }
		 return fermverd;
	}
	
	public int getFermetraVerdFasteignamats() { return fmverdfmProperty().get(); };
	public IntegerProperty fmverdfmProperty() {
		 if( fmverdfm == null ) {
			 fmverdfm = new SimpleIntegerProperty(this, "fmverdfm");
			 fmverdfm.bind( Bindings.divide( fastmat, ferm ) );
		 }
		 return fmverdfm;
	}
	
    public void setNafn(String value) { nafnProperty().set(value); }
    public String getNafn() { return nafnProperty().get(); }
    public StringProperty nafnProperty() { 
        if (nafn == null) nafn = new SimpleStringProperty(this, "nafn");
        return nafn;
    }
    
    public void setTegund(String value) { tegProperty().set(value); }
    public String getTegund() { return tegProperty().get(); }
    public StringProperty tegProperty() { 
        if (teg == null) teg = new SimpleStringProperty(this, "teg");
        return teg;
    }
    
    public void setHerbergi(int value) { herbProperty().set(value); }
    public int getHerbergi() { return herbProperty().get(); }
    public IntegerProperty herbProperty() { 
        if (herb == null) herb = new SimpleIntegerProperty(this, "herb");
        return herb;
    }
    
    public void setVerd(int value) { verdProperty().set(value); }
    public int getVerd() { return verdProperty().get(); }
    public IntegerProperty verdProperty() { 
        if (verd == null) verd = new SimpleIntegerProperty(this, "verd");
        return verd;
    }
    
    public void setFasteignaMat(int value) { fastmatProperty().set(value); }
    public int getFasteignaMat() { return fastmatProperty().get(); }
    public IntegerProperty fastmatProperty() { 
        if (fastmat == null) {
        	fastmat = new SimpleIntegerProperty(this, "fastmat", -1);
        }
        return fastmat;
    }
    
    public void setBrunabotaMat(int value) { brunmatProperty().set(value); }
    public int getBrunabotaMat() { return brunmatProperty().get(); }
    public IntegerProperty brunmatProperty() { 
        if (brunmat == null) brunmat = new SimpleIntegerProperty(this, "brunmat", -1);
        return brunmat;
    }
    
    public void setFermetrar(double value) { fermProperty().set(value); }
    public double getFermetrar() { return fermProperty().get(); }
    public DoubleProperty fermProperty() { 
        if (ferm == null) ferm = new SimpleDoubleProperty(this, "ferm");
        return ferm;
    }

	public Ibud(String nafn) {
		setNafn( nafn );
	}

	public Ibud(String nafn, int verd, int fastm, int brunm, String teg, int ferm, int herb, String dat, String url) throws ParseException {
		setNafn( nafn );
		setVerd( verd );
		setFasteignaMat( fastm );
		setBrunabotaMat( brunm );
		setTegund( teg );
		setFermetrar( ferm );
		setHerbergi( herb );
		this.dat = DateFormat.getDateInstance().parse(dat);
	}
	
	public void setUrl( String urlstr ) {
		this.url = urlstr;
		/*int i = urlstr.indexOf("fasteign/");
		int n = urlstr.indexOf('/', i+9);
		id = Integer.parseInt( urlstr.substring(i+9, n) );*/
	}
	
	/*int id = -1;
	public int getId() {
		return id;
	}*/

	public void set(int i, Object obj) {
		try {
			if (obj instanceof String) {
				String val = obj.toString();
				val = val.replaceAll("\\.", "");
				if (i == 0)
					setVerd( Integer.parseInt(val) );
				else if (i == 1)
					setFasteignaMat( Integer.parseInt(val) );
				else if (i == 2)
					setBrunabotaMat( Integer.parseInt(val) );
				else if (i == 3)
					setTegund( val );
				else if (i == 4)
					setFermetrar( Integer.parseInt(val) );
				else if (i == 5)
					setHerbergi( Integer.parseInt(val) );
				else if (i == 6) {
					String[] split = val.split(" ");
					if (split.length >= 3 && mmap.containsKey(split[1])) {
						int year = Integer.parseInt(split[2]);
						int month = mmap.get(split[1]);
						int day = Integer.parseInt(split[0]);
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, day);
						dat = cal.getTime();
					}

				}
			} // else dat = (Date)obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getUrlString() {
		return url;
	}
	
	public boolean equals( Object o ) {
		return o instanceof Ibud && url.equals( ((Ibud)o).url );
	}

	public String toString() {
		return "";//nafn + "\t" + verd + "\t" + fastm + "\t" + brunm + "\t" + teg + "\t" + ferm + "\t" + herb + "\t" + dat + "\t" + url;
	}
}
