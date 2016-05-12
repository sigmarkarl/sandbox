package org.simmi;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.simmi.Simlab.simlab.ByValue;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class Simlab implements ScriptEngineFactory {
	static {
		/*String jwsv = System.getProperty("javawebstart.version");
		String javalp = System.getProperty("java.library.path");
		String jnalp = System.getProperty("jna.library.path");
		
		String lpath = Native.getWebStartLibraryPath("csimlab");
		
		System.err.println( jwsv + " " + javalp + " " + jnalp + " " + lpath );
		
		System.setProperty("jna.library.path", javalp);*/
		
		String jnalib = System.getProperty("jna.library.path");
		if (jnalib == null || jnalib.length() == 0) {
			System.setProperty("jna.library.path", ".");
			boolean iswin = Platform.isWindows();
			boolean is64bit = Platform.is64Bit();

			String filename;
			String resourcename;
			if( !iswin ) {
				if( is64bit ) resourcename = "linux64/libcsimlab.so";
				else resourcename = "linux32/libcsimlab.so";
				
				filename = "libcsimlab.so";
			} else {
				resourcename = "win32/csimlab.dll";
				filename = "csimlab.dll";
			}

			File f = new File(filename);
			if (!f.exists()) {
				InputStream is;
				if (iswin)
					is = Simlab.class.getResourceAsStream("/" + resourcename);
				else
					is = Simlab.class.getResourceAsStream("/" + resourcename);

				if (is != null) {
					try {
						FileOutputStream fos = new FileOutputStream(filename);
						byte[] bb = new byte[1024];
						int r = is.read(bb);
						while (r > 0) {
							fos.write(bb, 0, r);
							r = is.read(bb);
						}
						fos.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.err.println("null");
				}
				// Native.re
			}
		}
		Native.register("csimlab");
	}
	public Reader reader;
	public BufferedReader bufferedreader;
	public Map<String, simlab.ByValue> datalib = new HashMap<String, simlab.ByValue>();
	public simlab.ByValue data = new simlab.ByValue();
	public simlab.ByValue nulldata = new simlab.ByValue(0, 0, 0);

	public static native int jcmd(simlab.ByValue sl);

	public static native int jcmdstr(final String s);

	public static native int jstore(String s, simlab.ByValue sl);

	// public static native int cmd( simlab ... s );
	public static native int crnt(simlab.ByValue s);

	public static native int jcrnt(Buffer bb, int type, int length);

	public static native long getlen();

	public static native long gettype();

	public static native simlab.ByValue getdata();

	// public static native bsimlab.ByValue stuff3();
	public static native Pointer stuff2();

	public native int sqr();

	public native int prim();

	public native int fibo();

	public native int gcd(simlab.ByValue v);

	public native int range( simlab.ByValue data, simlab.ByValue range );
	
	public native int ranger( simlab.ByValue range );
	
	public native int flip(simlab.ByValue data, simlab.ByValue chunk);

	public native int put( simlab.ByValue ret, simlab.ByValue data, simlab.ByValue idx );
	
	public native int get( simlab.ByValue ret, simlab.ByValue data, simlab.ByValue idx );
	
	public native int getter( simlab.ByValue data, simlab.ByValue idx );
	
	public native int shift( final simlab.ByValue d, final simlab.ByValue v, final simlab.ByValue c );

	public native int init();
	
	public native int invert();
	
	public native int simlab_min( simlab.ByValue ret, simlab.ByValue c );
	
	public native int simlab_max( simlab.ByValue ret, simlab.ByValue c );
	
	public native int histeq( simlab.ByValue hist, simlab.ByValue chunk, simlab.ByValue min, simlab.ByValue max );
	
	public native int hist( simlab.ByValue ret, simlab.ByValue bin, simlab.ByValue chunk, simlab.ByValue min, simlab.ByValue max );
	
	public native int diff( final simlab.ByValue chunk );
	
	public native int integ( final simlab.ByValue chunk );
	
	public native int copy( simlab.ByValue val );

	public native int add(simlab.ByValue val);

	public native int sub(simlab.ByValue val);

	public native int simlab_mul(simlab.ByValue val);

	public native int simlab_div(simlab.ByValue val);

	public native int simlab_floor();

	public native int simlab_ceil();
	
	public native int matmul( simlab.ByValue ret, simlab.ByValue mul, simlab.ByValue val );

	public native int mod(simlab.ByValue val);
	
	public native int imod(simlab.ByValue val);

	public synchronized native int set(simlab.ByValue val);

	public native int set(simlab.ByValue val, simlab.ByValue map);

	public native int poly(simlab.ByValue val, simlab.ByValue pw);

	public native int transmem(simlab.ByValue m, simlab.ByValue vc);

	public native int trans(simlab.ByValue val, simlab.ByValue val2);
	
	public native int transirr(simlab.ByValue ret, simlab.ByValue cl, simlab.ByValue cl2);

	public native int conv(simlab.ByValue convee, simlab.ByValue chunk, simlab.ByValue c_chunk);

	public native int deconv(simlab.ByValue convee, simlab.ByValue chunk, simlab.ByValue c_chunk);

	public native int filter(simlab.ByValue convee, simlab.ByValue chunk, simlab.ByValue c_chunk);

	public native int ifilter(simlab.ByValue convee, simlab.ByValue chunk, simlab.ByValue c_chunk);
	
	public native int wlet( simlab.ByValue c );
	
	public native int awlet( simlab.ByValue c );
	
	public native int minlet( simlab.ByValue c );
	
	public native int aminlet( simlab.ByValue c );
	
	public native int maxlet( simlab.ByValue c );
	
	public native int amaxlet( simlab.ByValue c );

	public native int simlab_sin(simlab.ByValue val);

	public native int simlab_cos();

	public native static byte getPseudoByte(long p, int k);
	public native static byte getPseudoByter(long p, int k);
	
	public native static long getPseudoUint(long p, int k);
	public native static long getPseudoUintr(long p, int k);
	public native static int getPseudoInt(long p, int k);
	public native static int getPseudoIntr(long p, int k);

	public native static float getPseudoFloat(long p, int k);
	public native static float getPseudoFloatr(long p, int k);
	public native static double getPseudoDouble(long p, int k);
	public native static double getPseudoDoubler(long p, int k);

	public native int printall();

	public native int simmi(simlab.ByValue s);

	public native int sort(simlab.ByValue s, simlab.ByValue w);

	public native int intersect(final simlab.ByValue s);

	public native int reorder(final simlab.ByValue ro);
	public native int rearrange(final simlab.ByValue ra);
	public native int irrange( final simlab.ByValue ret, final simlab.ByValue ord, final simlab.ByValue el );
	public native int ordir( final simlab.ByValue ret, final simlab.ByValue ord, final simlab.ByValue el );

	@mann(name="invidx: inverts reorder indexes", dataChanging=true)
	public native int invidx();

	@mann(name="creates sorting indexes indicating the order of the sequence", dataChanging=true)
	public native int sortidx();
	
	@mann(name="creates suffix indexes indicating the order of the suffix sequence", dataChanging=true)
	public native int suffidx( final simlab.ByValue data, final simlab.ByValue r );

	public native int transidx(final simlab.ByValue c, final simlab.ByValue r);

	public native int permute(final simlab.ByValue c, final simlab.ByValue start);

	public native int sum( final simlab.ByValue ret, final simlab.ByValue chunk, final simlab.ByValue size );
	
	public native int idx();

	public native int indexer();

	//public native int find(final simlab.ByValue s);

	private native int viewer(simlab.ByValue s, simlab.ByValue t);

	public static long BOOLEN = 1;
	public static long DUOLEN = 2;
	public static long QUADLEN = 4;
	public static long UBYTELEN = 8;
	public static long BYTELEN = 9;
	public static long USHORTLEN = 16;
	public static long SHORTLEN = 17;
	public static long UINTLEN = 32;
	public static long INTLEN = 33;
	public static long FLOATLEN = 34;
	public static long ULONGLEN = 64;
	public static long LONGLEN = 65;
	public static long DOUBLELEN = 66;

	boolean where = false;

	public int bb() {
		DoubleBuffer db = bb.asDoubleBuffer();
		System.err.println(bb.limit() + "  " + db.get(1));

		return 0;
	}

	static public List<Object> objects = new ArrayList<Object>();
	static Map<Long, ByteBuffer> buffers = new HashMap<Long, ByteBuffer>();

	public static class simlab extends Structure implements Cloneable {
		public static class ByValue extends simlab implements Structure.ByValue {
			public ByValue() {
				super();
			}

			public ByValue(long cnst) {
				super(0, LONGLEN, cnst);
			}

			public ByValue(double cnst) {
				super(0, DOUBLELEN, Double.doubleToRawLongBits(cnst));
			}

			public ByValue(String str) {
				/*
				 * byte[] bb = str.getBytes(); long pval =
				 * Simlab.this.allocateDirect(bb.length); ByteBuffer bff =
				 * buffers.get( pval ); for( byte b : bb ) { bff.put( b ); }
				 * super( 0, BYTELEN, pval );
				 */
			}

			public ByValue(long len, long typ, long ptr) {
				super(len, typ, ptr);
			}
		}

		public long buffer;
		public long length;
		public long type;

		public simlab() {
			length = 0;
			type = 0;
			buffer = 0;
		}

		public simlab(long len, long typ, long ptr) {
			length = len;
			type = typ;
			buffer = ptr;
		}
		
		public boolean isJavaObject() {
			return type == Byte.MIN_VALUE;
		}

		public Object getJavaObject() {
			if (type == Byte.MIN_VALUE) {
				return objects.get((int) length);
			}

			return null;
		}
		
		public double getRealValue() {
			if( type == 66 ) return getDouble();
			return getLong();
		}

		public double getDouble() {
			if (length == 0 && type == 66) {
				return Double.longBitsToDouble(buffer);
			} else if (type == 66) {
				return buffers.get(buffer).asDoubleBuffer().get(0);
			} else if (type == 65) {
				return (double) buffers.get(buffer).asLongBuffer().get(0);
			} else if (type == 34) {
				return (double) buffers.get(buffer).asFloatBuffer().get(0);
			} else if (type == 33) {
				return (double) buffers.get(buffer).asIntBuffer().get(0);
			}

			return buffer;
		}

		public long getLong() {
			if (length > 0) {
				if (type == 66) {
					return (long) buffers.get(buffer).asDoubleBuffer().get(0);
				} else if (type == 65) {
					return buffers.get(buffer).asLongBuffer().get(0);
				} else if (type == 34) {
					return (long) buffers.get(buffer).asFloatBuffer().get(0);
				} else if (type == 33) {
					return (long) buffers.get(buffer).asIntBuffer().get(0);
				}
			}

			if (type == 66) {
				return (long) Double.longBitsToDouble(buffer);
			}

			return buffer;
		}

		public int getByteLength() {
			if (type < 8)
				return (int) (type * length) / 8;
			return (int) ((type / 8) * length);
		}

		public byte[] getByteArray(long offset, long length) {
			//byte[] bb = new byte[(int) length];
			ByteBuffer bb = ByteBuffer.allocate((int)length);
			if( type < 0 ) {
				if( type == -8 || type == -9 ) {
					for (int i = 0; i < bb.limit(); i++) {
						bb.put( i, getPseudoByte( (int)offset + i ) );
					}
				} else if( type == -32 ) {
					int i = 0;
					while( bb.position() < bb.limit() ) {
						bb.putInt( getPseudoInt( (int)offset + i ) );
					}
				} else if( type == -34 ) {
					int i = 0;
					while( bb.position() < bb.limit() ) {
						bb.putFloat( getPseudoFloat( (int)offset + i++ ) );
					}
				} else if( type == -66 ) {
					int i = 0;
					while( bb.position() < bb.limit() ) {
						bb.putDouble( getPseudoDouble( (int)offset + i++ ) );
					}
				}
			} else {
				ByteBuffer bbb = getByteBuffer();
				for (int i = 0; i < length; i++) {
					bb.put( i, bbb.get((int) offset + i) );
				}
			}

			return bb.array();
		}

		/*
		 * public ByteBuffer getByteBuffer( long offset, long length ) { if(
		 * buffers.containsKey( buffer ) ) { return } return
		 * getByteBuffer(offset, length); }
		 */

		public ByteBuffer getByteBuffer() {
			ByteBuffer bb = null;
			if (buffers.containsKey(buffer)) {
				bb = buffers.get(buffer);
			} else if( length > 0 ) {
				bb = new Pointer(buffer).getByteBuffer(0, getByteLength());
				buffers.put(buffer, bb);
			}

			return bb;
		}

		public Buffer getBuffer() {
			ByteBuffer bb = getByteBuffer();
			if( bb != null ) {
				if (type == 66)
					return bb.asDoubleBuffer();
				else if (type == 65)
					return bb.asLongBuffer();
				else if (type == 34)
					return bb.asFloatBuffer();
				else if (type == 33)
					return bb.asIntBuffer();
				else if (type == 32)
					return bb.asIntBuffer();
				else if (type == 17)
					return bb.asShortBuffer();
			}

			return bb;
		}
		
		public final short getShort( int i ) {
			return getByteBuffer().asShortBuffer().get(i);
		}
		
		public final double get( int i ) {
			ByteBuffer bb = getByteBuffer();
			if( bb != null ) {
				if (type == 66)
					return bb.asDoubleBuffer().get(i);
				else if (type == 65)
					return bb.asLongBuffer().get(i);
				else if (type == 34)
					return bb.asFloatBuffer().get(i);
				else if (type == 33)
					return bb.asIntBuffer().get(i);
				else if (type == 17)
					return bb.asShortBuffer().get(i);
			}

			return 0;
		}
		
		public final byte getPseudoByte( int k ) {
			return Simlab.getPseudoByte( buffer, k );
		}
		
		public final byte getPseudoByter( int k ) {
			return Simlab.getPseudoByter( buffer, k );
		}
		
		public final long getPseudoUint( int k ) {
			return Simlab.getPseudoUint( buffer, k );
		}
		
		public final int getPseudoInt( int k ) {
			return Simlab.getPseudoInt( buffer, k );
		}
		
		public final long getPseudoIntr( int k ) {
			return Simlab.getPseudoIntr( buffer, k );
		}
		
		public final float getPseudoFloat( int k ) {
			return Simlab.getPseudoFloat( buffer, k );
		}
		
		public final float getPseudoFloatr( int k ) {
			return Simlab.getPseudoFloatr( buffer, k );
		}
		
		public final double getPseudoDouble( int k ) {
			return Simlab.getPseudoDouble( buffer, k );
		}
		
		public final double getPseudoDoubler( int k ) {
			return Simlab.getPseudoDoubler( buffer, k );
		}

		public simlab.ByValue clone() {
			// allocateDirect( 10 );
			return new simlab.ByValue(length, type, buffer);
		}

		// private Pointer p = null;
		/*
		 * private Pointer getThePointer() { Pointer p = null; if(
		 * pointers.containsKey(buffer) ) p = pointers.get(buffer); else { p =
		 * new Pointer(buffer); pointers.put(buffer, p); } /*if( p == null ||
		 * Pointer.nativeValue(p) != buffer ) { p = new Pointer(buffer);
		 * 
		 * if( (type == 8 || type == 9) && buffer != 0 && length > 0 ) { s =
		 * p.getString(0); } else s = buffer + " " + type + " " + length;*
		 * 
		 * return p; }
		 */

		public String getTheString() {
			String s = null;
			// System.err.println("tostring " + buffer + "  " + type + "  " +
			// length);
			if ((type == 8 || type == 9) && buffer != 0 && length > 0) {
				//getByteBuffer().put( (int)length, (byte)0 );
				s = Native.getDirectBufferPointer(getByteBuffer()).getString(0);
				// ).getString(0);
				/*
				 * ByteBuffer bb = getByteBuffer(); byte[] dst = new
				 * byte[bb.limit()]; bb.get(dst); s = new String(dst, 0,
				 * dst.length - 1);
				 */
			} else
				s = buffer + " " + type + " " + length;

			return s;
		}
		
		public void put( int i, double val ) {
			DoubleBuffer db = (DoubleBuffer)getBuffer();
			if( db != null ) {
				db.put( i, val );
			} else {
				buffer = Double.doubleToRawLongBits( val );
			}
		}

		/*
		 * public boolean equals( simlab sl ) { return length == sl.length &&
		 * type == sl.type; }
		 */
	}

	/*
	 * public static class psimlab extends Structure { public static class
	 * ByValue extends psimlab implements Structure.ByValue { } public Pointer
	 * buffer; public long length; public long type; }
	 * 
	 * public static class bsimlab extends Structure { public static class
	 * ByValue extends bsimlab implements Structure.ByValue { }
	 * 
	 * public bsimlab() {
	 * 
	 * }
	 * 
	 * public bsimlab( ByteBuffer buffer, long length, long type ) { this.buffer
	 * = buffer; this.length = length; this.type = type; }
	 * 
	 * public ByteBuffer buffer; public long length; public long type; }
	 */

	public static int jcrnt_local() {
		simlab.ByValue ps = new simlab.ByValue(0, 66, 14);
		return crnt(ps);
	}

	Object obj = null;

	private long allocateDirect(int size) {
		ByteBuffer bb = ByteBuffer.allocateDirect(size);
		bb.order(ByteOrder.nativeOrder());

		Pointer p = Native.getDirectBufferPointer(bb);
		long pval = Pointer.nativeValue(p);

		buffers.put(pval, bb);

		return pval;
	}

	private long allocateDouble(int size) {
		ByteBuffer bb = ByteBuffer.allocateDirect(size * 8);
		bb.order(ByteOrder.nativeOrder());

		Pointer p = Native.getDirectBufferPointer(bb);
		long pval = Pointer.nativeValue(p);
		buffers.put(pval, bb);

		return pval;
	}

	File currentDir = null;

	public int cd(final simlab.ByValue dir) {
		currentDir = new File(dir.getTheString());

		return 1;
	}
	
	public int ssh( final simlab.ByValue user, final simlab.ByValue pass, final simlab.ByValue host, final simlab.ByValue port, final simlab.ByValue cmd ) throws InterruptedException, Exception {
		/*SshClient	sshclient = SshClient.setUpDefaultClient();
		sshclient.start();
		
		String hoststr = host.getTheString();
		int portn = (int)port.getLong();
		ClientSession session = sshclient.connect(hoststr, portn).await().getSession();
        int ret = ClientSession.WAIT_AUTH;

        String login = user.getTheString();
        String password = pass.getTheString();
        while ((ret & ClientSession.WAIT_AUTH) != 0) {
            session.authPassword(login, password);
            ret = session.waitFor(ClientSession.WAIT_AUTH | ClientSession.CLOSED | ClientSession.AUTHED, 0);
        }
        
        String command = cmd.getTheString();
        //ClientChannel channel = session.createExecChannel( command );
        ChannelExec channel = session.createExecChannel( command );
        //channel.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        /*Writer w = new OutputStreamWriter(baos);
        //for (String cmd : command) {
            w.append( command ); //.append(" ");
        //}
        w.append("\n");
        w.close();*
        channel.setIn(new ByteArrayInputStream(baos.toByteArray()));
        channel.setOut( System.out );
        channel.setErr( System.err );
        channel.open().await();
        channel.waitFor(ClientChannel.CLOSED, 0);
        session.close(false);
        
        sshclient.stop();*/
		
		return 5;
	}

	public int viewer(final simlab.ByValue s) {
		long pval = allocateDirect(100);

		crnt(data);
		viewer(s, new simlab.ByValue(0, 65, pval));
		simlab.ByValue sb = getdata();
		data.buffer = sb.buffer;
		data.type = sb.type;
		data.length = sb.length;

		return 1;
	}

	public int view(final simlab.ByValue lstart, final simlab.ByValue lsize) {
		long start = lstart.buffer;
		long size = lsize.buffer;
		if (size == 0) {
			size = data.length - start;
		}
		data.length = size;
		data.buffer = data.buffer + bytelength(data.type, start);

		return 1;
	}

	public int create(final simlab.ByValue sl) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String className = sl.getTheString();
		Class theclass = Class.forName(className);
		obj = theclass.newInstance();
		data.buffer = 0;
		data.length = objects.size();
		data.type = Byte.MIN_VALUE;
		objects.add(obj);

		return 1;
	}

	public int call(final simlab.ByValue methn, final simlab.ByValue... sl) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (data.type == Byte.MIN_VALUE) {
			String methodName = methn.getTheString();
			List<Class> lClass = new ArrayList<Class>();
			List<Object> lObj = new ArrayList<Object>();
			for (simlab.ByValue v : sl) {
				if (v.type == BYTELEN) {
					lClass.add(String.class);
					lObj.add(v.getTheString());
				} else if (v.type == INTLEN || v.type == UINTLEN || v.type == LONGLEN || v.type == ULONGLEN) {
					lClass.add(int.class);
					lObj.add((int) v.buffer);
				} else if (v.type == BOOLEN) {
					lClass.add(boolean.class);
					lObj.add(v.buffer == 1);
				}
			}
			Object obj = data.getJavaObject();
			if (obj != null) {
				Method meth = obj.getClass().getMethod(methodName, lClass.toArray(new Class[0]));
				meth.invoke(obj, lObj.toArray());
			}
		}

		return 1;
	}

	public int reverse(final simlab.ByValue s) {
		//return flip(s);
		
		return 1;
	}
	
	public int tolower() {
		if( (data.type == 8 || data.type == 9) ) {
			String low = data.getTheString().toLowerCase();
			byte[] lowbytes = low.getBytes();
			ByteBuffer	bb = data.getByteBuffer();	
			System.err.println( lowbytes.length + "  " + bb.limit() );
			bb.position(0);
			bb.put( lowbytes, 0, Math.min( lowbytes.length, bb.limit() ) );
		}
		
		return 0;
	}
	
	public int min( final simlab.ByValue c, final simlab.ByValue s ) {
		int chunk = (int)c.getLong();
		int size = (int)c.getLong();
		int length = (int)data.length;
		
		int retsize = (chunk-size+1);
		int retlen = (length*retsize)/chunk;
		
		int bl = bytelength(data.type, retlen);
		long p = allocateDirect( bl );
		simlab.ByValue ret = new simlab.ByValue( retlen, data.type, p );
		
		crnt( data );
		simlab_min( ret, c );
		
		data.buffer = p;
		data.length = retlen;
		
		return 1;
	}
	
	public int max( final simlab.ByValue c, final simlab.ByValue s ) {
		int chunk = (int)c.getLong();
		int size = (int)c.getLong();
		int length = (int)data.length;
		
		int retsize = (chunk-size+1);
		int retlen = (length*retsize)/chunk;
		
		int bl = bytelength(data.type, retlen);
		long p = allocateDirect( bl );
		simlab.ByValue ret = new simlab.ByValue( retlen, data.type, p );
		
		crnt( data );
		simlab_max( ret, c );
		
		data.buffer = p;
		data.length = retlen;
		
		return 1;
	}
	
	//public int histq()
	
	public int hist( final simlab.ByValue sl_bin, final simlab.ByValue sl_chunk ) {
		hist( sl_bin, sl_chunk, nulldata, nulldata );

		return 2;
	}
	
	public int hist( final simlab.ByValue sl_bin, final simlab.ByValue sl_chunk, final simlab.ByValue sl_min, final simlab.ByValue sl_max ) {
		long bin = sl_bin.getLong();
		long chunk = sl_chunk.getLong();
		long length = data.length;
		
		long retlen = bin*length/chunk;
		//System.err.println( bin + "  " + length + "  " + chunk );
		long p = allocateDirect( bytelength( UINTLEN, (int)retlen ) );
		
		final simlab.ByValue ret = new simlab.ByValue( retlen, UINTLEN, p );
		
		crnt( data );
		hist( ret, sl_bin, sl_chunk, sl_min, sl_max );
		
		data.buffer = ret.buffer;
		data.length = ret.length;
		data.type = ret.type;
		
		return 4;
	}
	
	@mann(name="ordir: reorders array with irregularly sized elements", dataChanging=true)
	public int ordir( @pann(name="vOrder") final simlab.ByValue ord, @pann(name="vElementIndices") final simlab.ByValue el ) {
		long p = allocateDirect( bytelength( data.type, (int)data.length ) );
		final simlab.ByValue ret = new simlab.ByValue( data.length, data.type, p );
		
		crnt( data );
		ordir( ret, ord, el );
		
		data.buffer = ret.buffer;
		data.length = ret.length;
		data.type = ret.type;
		
		return 2;
	}
	
	@mann(name="irrange: rearranges array with irregularly sized elements", dataChanging=true)
	public int irrange( @pann(name="vOrder") final simlab.ByValue ord, @pann(name="vElementIndices") final simlab.ByValue el ) {
		long p = allocateDirect( bytelength( data.type, (int)data.length ) );
		final simlab.ByValue ret = new simlab.ByValue( data.length, data.type, p );
		
		crnt( data );
		irrange( ret, ord, el );
		
		data.buffer = ret.buffer;
		data.length = ret.length;
		data.type = ret.type;
		
		return 2;
	}
	
	@mann(name="transirr: transpose of data with irregular sized elements", dataChanging=true)
	public int transirr( @pann(name="vByteSizeOfElements") final simlab.ByValue cl, @pann(name="iNumberOfColumns") final simlab.ByValue cl2 ) {
		long p = allocateDirect( bytelength( data.type, (int)data.length ) );
		final simlab.ByValue ret = new simlab.ByValue( data.length, data.type, p );
		
		crnt( data );
		transirr( ret, cl, cl2 );
		
		data.buffer = ret.buffer;
		data.length = ret.length;
		data.type = ret.type;
		
		return 2;
	}
	
	@mann(name="transirr: transpose of data with irregular sized columns", dataChanging=true)
	public int transirr( @pann(name="vByteSizeOfColumns") final simlab.ByValue cl ) {
		transirr( cl, nulldata );
		
		return 1;
	}
	
	private long _gcd( long a, long b ) {
		if( b == 0 ) return a;
		else return _gcd( b, a%b );
	}
	
	@mann(name="abs: absolute value", dataChanging=true)
	public int abs() {
		if( data.type == BYTELEN ) {
			ByteBuffer bb = data.getByteBuffer();
			for( int i = 0; i < bb.limit(); i++ ) {
				bb.put(i, (byte)Math.abs(bb.get(i)) );
			}
		} else if( data.type == SHORTLEN ) {
			ShortBuffer sb = data.getByteBuffer().asShortBuffer();
			for( int i = 0; i < sb.limit(); i++ ) {
				sb.put(i, (short)Math.abs(sb.get(i)) );
			}
		} else if( data.type == INTLEN ) {
			IntBuffer ib = data.getByteBuffer().asIntBuffer();
			for( int i = 0; i < ib.limit(); i++ ) {
				ib.put(i, Math.abs(ib.get(i)) );
			}
		} else if( data.type == FLOATLEN ) {
			FloatBuffer fb = data.getByteBuffer().asFloatBuffer();
			for( int i = 0; i < fb.limit(); i++ ) {
				fb.put(i, Math.abs(fb.get(i)) );
			}
		} else if( data.type == LONGLEN ) {
			LongBuffer lb = data.getByteBuffer().asLongBuffer();
			for( int i = 0; i < lb.limit(); i++ ) {
				lb.put(i, Math.abs(lb.get(i)) );
			}
		} else if( data.type == DOUBLELEN ) {
			DoubleBuffer db = data.getByteBuffer().asDoubleBuffer();
			for( int i = 0; i < db.limit(); i++ ) {
				db.put(i, Math.abs(db.get(i)) );
			}
		}
		
		return 0;
	}
	
	public int matmul( final simlab.ByValue mul ) {
		long val = _gcd( data.length, mul.length );
		matmul( mul, new simlab.ByValue( val ) );
		
		return 1;
	}
	
	public int matmul( final simlab.ByValue mul, final simlab.ByValue val ) {
		int mullength = (int)mul.length;
		int length = (int)data.length;
		int v = (int)val.getLong();
		
		int retc = (mullength/v);
		int retr = (length/v);
		int retlen = retc*retr;
		
		long p = allocateDirect( bytelength(data.type, retlen) );
		final simlab.ByValue ret = new simlab.ByValue( retlen, data.type, p );
		
		crnt( data );
		matmul( ret, mul, val );
		
		data.buffer = p;
		data.length = retlen;
		
		return 2;
	}
	
	public int diff() {
		crnt( data );
		diff( nulldata );
		
		return 0;
	}
	
	public int integ() {
		crnt( data );
		integ( nulldata );
		
		return 0;
	}
	
	public int mean() {
		sum();
		crnt( data );
		simlab_div( new simlab.ByValue( data.length ) );
		
		return 0;
	}
	
	public int sum( final simlab.ByValue sl_chunk, final simlab.ByValue sl_size ) {
		int chunk = (int)sl_chunk.getLong();
		int size = (int)sl_size.getLong();
		int length = (int)data.length;
		
		int retsize = (chunk-size+1);
		int retlen = (length*retsize)/chunk;
		
		long p = allocateDirect( bytelength(data.type, retlen) );
		final simlab.ByValue ret = new simlab.ByValue( retlen, data.type, p );
		
		if( data.type > 100 ) {
			
		} else {
			crnt( data );
			sum( ret, sl_chunk, sl_size );
			
			data.buffer = ret.buffer;
			data.type = ret.type;
			data.length = ret.length;
		}
		
		return 2;
	}
	
	public int sum( final simlab.ByValue sl_chunk ) {
		sum( sl_chunk, sl_chunk );
		
		return 1;
	}
	
	public int sum() {
		sum( new simlab.ByValue(data.length) );
		
		return 0;
	}
	
	public int subsetsum( final simlab.ByValue sl ) {
		if( data.type > 66 ) {
			
		} else {
			if( 8%sl.type == 2 ) {
				double val = sl.getDouble();
			} else {
				long val = sl.getLong();
			}
		}
		
		return 1;
	}

	public int zero() {
		if (data.type > 0) {
			ByteBuffer bb = data.getByteBuffer();
			int i = 0;
			for (; i < bb.limit(); i += 4) {
				bb.putInt(i, 0);
			}
			for (; i < bb.limit() - 4; i++) {
				bb.put((byte) 0);
			}
		}

		return 0;
	}

	/*
	 * public static void jerm() { simlab s = erm(); //System.err.println(
	 * s.buffer + "   " + s.type + "   " + s.length ); }
	 */

	ByteBuffer bb;

	public int loadimage() throws IOException {
		/*
		 * String urlstr = null; if (sl.length == 0) { JFileChooser chooser =
		 * new JFileChooser(); if (chooser.showOpenDialog(null) ==
		 * JFileChooser.APPROVE_OPTION) { urlstr =
		 * chooser.getSelectedFile().toURI().toString(); } } else { urlstr =
		 * sl.toString(); }
		 * 
		 * if (urlstr != null) { URL url = new URL(urlstr);
		 */

		ByteBuffer bb = data.getByteBuffer();
		byte[] b2 = new byte[bb.limit()];
		for (int i = 0; i < bb.limit(); i++) {
			b2[i] = bb.get();
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(b2);

		BufferedImage img = ImageIO.read(bais);
		
		simlab.ByValue r = null;
		simlab.ByValue c = null;
		if( datalib.containsKey("r") ) {
			r = datalib.get("r");
		} else {
			r = new simlab.ByValue( (long)img.getHeight() );
			datalib.put("r", r);
		}
		
		if( datalib.containsKey("c") ) {
			c = datalib.get("c");
		} else {
			c = new simlab.ByValue( (long)img.getWidth() );
			datalib.put("c", c);
		}
		//System.out.println(img.getWidth() + " " + img.getHeight());

		int size = img.getWidth() * img.getHeight() * 4;
		long ptr = allocateDirect(size);
		// bb = ByteBuffer.allocateDirect(img.getWidth() * img.getHeight() * 4);
		// bset.add(bb);
		IntBuffer ib = buffers.get(ptr).asIntBuffer();
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				ib.put(img.getRGB(x, y));
				// img.get
			}
		}

		// NativeLongByReference nat = new NativeLongByReference();
		// nat.setPointer( Native.getDirectBufferPointer( bb ) );
		// data = new simlab.ByValue(bb.limit() / 4, UINTLEN, ptr);
		data.buffer = ptr;
		data.type = UINTLEN;
		data.length = ib.limit();

		// }

		return 1;
	}

	public int dumpimage(final simlab.ByValue urlsl, final simlab.ByValue wsl) throws URISyntaxException, IOException {
		final long t = data.length;
		final long w = wsl.buffer;
		final long h = (t / w);
		BufferedImage bi = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_ARGB);
		//simlab.ByValue ps = getdata();
		IntBuffer ib = data.getByteBuffer().asIntBuffer(); // getIntArray(0,// t);
		for (int i = 0; i < t; i++) {
			bi.setRGB((int) (i % w), (int) (i / w), ib.get(i));
		}

		String urlstr = urlsl.getTheString();
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		ImageIO.write(bi, urlstr, baos);
		
		byte[] ba = baos.toByteArray();
		long p = allocateDirect( ba.length );
		ByteBuffer bb = buffers.get( p );
		bb.put( ba );
		
		data.type = BYTELEN;
		data.length = ba.length;
		data.buffer = p;
		
		baos.close();

		return 2;
	}

	private AudioFormat getFormat() {
		float sampleRate = 44100;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

	public int join(final simlab.ByValue v) {
		int databytelen = data.getByteLength();
		int size = v.getByteLength() + databytelen;
		long pval = allocateDirect(size);
		ByteBuffer bb = buffers.get(pval);
		int i = 0;
		ByteBuffer pb = data.getByteBuffer();
		for (; i < databytelen; i++) {
			bb.put(i, pb.get(i));
		}
		pb = v.getByteBuffer();
		for (; i < size; i++) {
			bb.put(i, pb.get(i - databytelen));
		}

		data.buffer = pval;
		data.length += v.getByteLength() / (data.type / 8);

		return 1;
	}
	
	public int yuv2rgb( final simlab.ByValue ww, final simlab.ByValue hh ) {
		int w = (int)ww.getLong();
		int h = (int)hh.getLong();
		
		int framesize = (int)w*h;
		int nframes = (int)(2*data.length)/(3*framesize);
		
		ByteBuffer	srcbb = data.getByteBuffer();
		
		int t = 4*framesize*nframes;
		long p = allocateDirect( t );
		data.buffer = p;
		data.type = INTLEN;
		data.length = framesize*nframes;
		
		ByteBuffer 	dstbb = data.getByteBuffer();
		
		for( int f = 0; f < nframes; f++ ) {
			for( int i = 0; i < framesize; i++ ) {
				int start = f*framesize;
				
				int xx = i%w;
				int yy = i/w;
				
				int uff = (yy/2)*(w/2)+(xx/2)+framesize;
				byte y = srcbb.get( i+3*start/2 );
				byte u = srcbb.get( uff+3*start/2 );
				byte v = srcbb.get( uff+framesize/4+3*start/2 );
				
				int nind = (start+i)*4;
				dstbb.put( nind+0, (byte)(y+1.13983*v) );
				dstbb.put( nind+1, (byte)(y-0.39465*u-0.58060*v) );
				dstbb.put( nind+2, (byte)(y+2.03211*u) );
			}
		}
		
		return 0;
	}
	
	public int simlab_clone() {
		ByteBuffer old = data.getByteBuffer();
		long p = allocateDirect( bytelength(data.type, data.length) );
		ByteBuffer cur = buffers.get( p );
		
		cur.put( old );
		data.buffer = p;
		
		return 0;
	}
	
	public int cast( final simlab.ByValue casto ) {
		long type = casto.getLong();
		
		final simlab.ByValue old = data.clone();
		long p = allocateDirect( bytelength(type, data.length) );
		data.buffer = p;
		data.type = type;
		
		crnt( data );
		copy( old );
		
		return 1;
	}

	public int shift( final simlab.ByValue v, final simlab.ByValue r ) {
		shift(data, r, v);

		return 1;
	}

	public int shift( final simlab.ByValue v ) {
		if( v.length > 1 ) shift(v, new simlab.ByValue(data.length/v.length) );
		else shift(v, new simlab.ByValue(data.length) );

		return 1;
	}
	
	public int range( simlab.ByValue range ) {
		long len = 0;
		for( int i = 0; i < range.length; i+=2 ) {
			len += range.get(i+1) - range.get(i);
		}
		long p = allocateDirect( bytelength(INTLEN, len) );
		
		data.buffer = p;
		data.length = len;
		data.type = INTLEN;
		
		range( data, range );
		
		return 1;
	}
	
	public int suffidx() {
		long p = allocateDirect( bytelength(INTLEN, data.length) );
		simlab.ByValue id = new simlab.ByValue(data.length, INTLEN, p);
		crnt(id);
		idx();
		suffidx(data, id);
		
		data.buffer = id.buffer;
		data.type = id.type;
		data.length = id.length;
		
		return 0;
	}

	public int flip() {
		//crnt(data);
		flip( data, new simlab.ByValue(data.length) );
		//data = getdata();

		return 0;
	}
	
	public int flip( final simlab.ByValue chk ) {
		flip( data, chk );
		
		return 1;
	}

	public int sin() {
		return simlab_sin(nulldata);
	}

	// public int permute( final simlab.ByValue)

	public int transidx(final simlab.ByValue c) {
		crnt(data);
		transidx(c, nulldata);
		data = getdata();

		return 1;
	}

	public int sort() {
		crnt(data);
		sort(nulldata, nulldata);
		data = getdata();

		return 0;
	}

	public int sort(final simlab.ByValue v) {
		crnt(data);
		sort(v, nulldata);
		data = getdata();

		return 1;
	}

	public int trans( @pann(name="iNumberOfColumns [negative value for inverse transpose]") final simlab.ByValue v) {
		crnt(data);
		trans(v, nulldata);
		data = getdata();
		
		long val = v.getLong();
		update( data.buffer, val < 0 ? (-data.length/val) : val );

		return 1;
	}

	public void record() {
		simlab.ByValue data = getdata();

		final AudioFormat format = getFormat();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		try {
			TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();

			int length = (int) data.length;
			ByteBuffer bb = data.getByteBuffer();

			int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
			ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
			ShortBuffer sb = buffer.asShortBuffer();

			int total = 0;
			while (total < length) {
				int r = line.read(buffer.array(), 0, buffer.limit());
				if (data.type == 66) {
					DoubleBuffer db = bb.asDoubleBuffer();
					for (int i = total; i < total + r / 2; i++) {
						if (i >= db.limit())
							System.err.println(db.limit() + "   " + i);
						db.put(i, sb.get(i - total));
					}
				}
				total += r / 2;
			}

			line.stop();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void play2() {
		simlab bb = getdata();
		Pointer bbb = stuff2();
		// bsimlab bb3 = stuff3();

		long type = bb.type;

		/*
		 * Pointer ptr = bb.getThePointer(); if (type == 66) { for (int i = 0; i
		 * < 10; i++) { double d = ptr.getDouble(i * 8); }
		 * 
		 * if (bbb != null) { DoubleBuffer db = bbb.getByteBuffer(0, 10 *
		 * 8).asDoubleBuffer(); for (int i = 0; i < 10; i++) { double d =
		 * db.get(i); } } else {
		 * 
		 * } } else if (type == 33) {
		 * 
		 * for (int i = 0; i < 10; i++) { double d = ptr.getInt(i); } } else if
		 * (type == 16) { for (int i = 0; i < 10; i++) { short s =
		 * ptr.getShort(i * 2); char d = ptr.getChar(i); byte b =
		 * ptr.getByte(i); } }
		 */
	}

	public void play() {
		ByteBuffer bb = data.getByteBuffer();

		ByteBuffer audio = ByteBuffer.allocate((int) (data.length * 2));
		ShortBuffer saudio = audio.asShortBuffer();

		if (data.type == 66) {
			DoubleBuffer db = bb.asDoubleBuffer();
			for (int i = 0; i < db.limit(); i++) {
				saudio.put(i, (short) db.get(i));
			}
		}

		InputStream input = new ByteArrayInputStream(audio.array());
		final AudioFormat format = getFormat();
		final int len = audio.limit() / format.getFrameSize();
		final AudioInputStream ais = new AudioInputStream(input, format, len);

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine line;
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();

			int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
			byte buffer[] = new byte[bufferSize];

			int count;
			while ((count = ais.read(buffer, 0, buffer.length)) != -1) {
				if (count > 0) {
					line.write(buffer, 0, count);
				}
			}

			line.drain();
			line.close();
			ais.close();
			input.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int surface( final simlab.ByValue ww, final simlab.ByValue... timer ) {
		final String name = "";
		final simlab.ByValue slptr = data.clone();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame(name);
				frame.setSize(800, 600);
				final SurfaceComp c = new SurfaceComp(name, slptr, (int) ww.getLong(), 0, 0);
				c.setSize(800, 600);
				c.setFocusable(true);
				c.requestFocus();
				//c.setIgnoreRepaint(true);
				frame.add(c);
				// datalib.put(name, current);
				Set<SimComp> compset;
				if (compmap.containsKey(data.buffer)) {
					compset = compmap.get(data.buffer);
				} else {
					compset = new HashSet<SimComp>();
					compmap.put(data.buffer, compset);
				}
				compset.add(c);

				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				if (timer.length > 1) {
					long time = timer[1].buffer;

					final Timer tmr = new Timer((int) time, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								Simlab.this.run(timer[0]);
							} catch (IllegalArgumentException e1) {
								e1.printStackTrace();
							} catch (IllegalAccessException e1) {
								e1.printStackTrace();
							} catch (InvocationTargetException e1) {
								e1.printStackTrace();
							}
							c.reload();
						}
					});

					frame.addWindowListener(new WindowListener() {
						@Override
						public void windowOpened(WindowEvent e) {}

						@Override
						public void windowIconified(WindowEvent e) {}

						@Override
						public void windowDeiconified(WindowEvent e) {}

						@Override
						public void windowDeactivated(WindowEvent e) {
							tmr.stop();
						}

						@Override
						public void windowClosing(WindowEvent e) {}

						@Override
						public void windowClosed(WindowEvent e) {
							tmr.stop();
						}

						@Override
						public void windowActivated(WindowEvent e) {}
					});
					// tmr.set
					tmr.start();
				}
				frame.setVisible(true);
			}
		});
		
		return 1 + timer.length;
	}

	public int plot(final simlab.ByValue ww, final simlab.ByValue... timer) {
		final String name = "";
		final simlab.ByValue slptr = data.clone();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame(name);
				frame.setSize(800, 600);
				final ChartComp c = new ChartComp(name, slptr, (int) ww.getLong(), 0, 0);
				frame.add(c);
				// datalib.put(name, current);
				Set<SimComp> compset;
				if (compmap.containsKey(data.buffer)) {
					compset = compmap.get(data.buffer);
				} else {
					compset = new HashSet<SimComp>();
					compmap.put(data.buffer, compset);
				}
				compset.add(c);

				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				if (timer.length > 1) {
					long time = timer[1].buffer;

					final Timer tmr = new Timer((int) time, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								Simlab.this.run(timer[0]);
							} catch (IllegalArgumentException e1) {
								e1.printStackTrace();
							} catch (IllegalAccessException e1) {
								e1.printStackTrace();
							} catch (InvocationTargetException e1) {
								e1.printStackTrace();
							}
							c.reload();
						}
					});

					frame.addWindowListener(new WindowListener() {
						@Override
						public void windowOpened(WindowEvent e) {}

						@Override
						public void windowIconified(WindowEvent e) {}

						@Override
						public void windowDeiconified(WindowEvent e) {}

						@Override
						public void windowDeactivated(WindowEvent e) {
							tmr.stop();
						}

						@Override
						public void windowClosing(WindowEvent e) {}

						@Override
						public void windowClosed(WindowEvent e) {
							tmr.stop();
						}

						@Override
						public void windowActivated(WindowEvent e) {}
					});
					// tmr.set
					tmr.start();
				}
				frame.setVisible(true);
			}
		});

		return 1 + timer.length;
	}
	
	@mann(name="fill: creates a matrix from the array given the element locations, the column size is the largest of the elements", dataChanging=true)
	public int fill( @pann(name="vIndices") final simlab.ByValue vIndices ) {
		fill( vIndices, nulldata );
		
		return 1;
	}
	
	@mann(name="fill: creates a vector of equal sized elements from the array, given the element locations and specifying the element size", dataChanging=true)
	public int fill( @pann(name="vIndices") final simlab.ByValue vIndices, @pann(name="[optional] iElementSize") final simlab.ByValue iElementSize ) {
		int elsize = (int)iElementSize.getLong();
		int velsize = (int)(elsize*vIndices.length);
		long p = allocateDirect( (int)(velsize*data.type/8) );
		final simlab.ByValue ret = new simlab.ByValue( velsize, data.type, p );
		
		int size = (int)(data.type/8);
		Buffer b = vIndices.getBuffer();
		ByteBuffer src = data.getByteBuffer();
		ByteBuffer dst = ret.getByteBuffer();
		int start = 0;
		for( int i = 0; i < vIndices.length; i++ ) {
			int next = (int)vIndices.get(i);
			for( int k = 0; k < Math.min(next-start,elsize); k++ ) {
				for( int u = 0; u < size; u++ ) {
					dst.put( (i*elsize+k)*size+u, src.get((start+k)*size+u) );
				}
			}
			start = next;
		}
		data.buffer = ret.buffer;
		data.length = ret.length;
		data.type = ret.type;
		
		return 2;
	}

	@mann(name="image: shows image in a window, optionally with a timer function", dataChanging=false)
	public int image( @pann(name="vDimensions") final simlab.ByValue ww, @pann(name="[optional] fTimer, iInterval") final simlab.ByValue ... timer) {
		final String name = "";// sl.getTheString();
		
		final long w;
		final long h;
		if( ww.length <= 1 ) {
			w = ww.getLong();
			h = (data.length / w);
		} else {
			DoubleBuffer db = ww.getByteBuffer().asDoubleBuffer();
			w = (long)db.get(0);
			h = (long)db.get(1);
		}
		final long t = w*h;
		//BufferedImage.type
		final BufferedImage bi = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_RGB);
		// final Pointer ptr = data.getPointer();
		final simlab.ByValue slptr = data.clone();

		IntBuffer ib = slptr.getByteBuffer().asIntBuffer(); // getIntArray(0,
															// t);
		for (int i = 0; i < t; i++) {
			int x = (int) (i % w);
			int y = (int) (i / w);
			if( x < bi.getWidth() && y < bi.getHeight() ) bi.setRGB( x, y, ib.get(i) );
			else {
				System.err.println("outofbounds");
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame(name);
				frame.setSize(800, 600);
				final ImageComp c = new ImageComp(name, bi, slptr, (int) w, (int) h);
				frame.add(c);
				// datalib.put(name, current);
				Set<SimComp> compset;
				if (compmap.containsKey(data.buffer)) {
					compset = compmap.get(data.buffer);
				} else {
					compset = new HashSet<SimComp>();
					compmap.put(data.buffer, compset);
				}
				compset.add(c);

				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				if (timer.length > 1) {
					long time = timer[1].buffer;

					final Timer tmr = new Timer((int) time, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								Simlab.this.run(timer[0]);
							} catch (IllegalArgumentException e1) {
								e1.printStackTrace();
							} catch (IllegalAccessException e1) {
								e1.printStackTrace();
							} catch (InvocationTargetException e1) {
								e1.printStackTrace();
							}
							c.reload();
						}
					});

					frame.addWindowListener(new WindowListener() {
						@Override
						public void windowOpened(WindowEvent e) {
						}

						@Override
						public void windowIconified(WindowEvent e) {
						}

						@Override
						public void windowDeiconified(WindowEvent e) {
						}

						@Override
						public void windowDeactivated(WindowEvent e) {
							tmr.stop();
						}

						@Override
						public void windowClosing(WindowEvent e) {
						}

						@Override
						public void windowClosed(WindowEvent e) {
							tmr.stop();
						}

						@Override
						public void windowActivated(WindowEvent e) {
						}
					});
					// tmr.set
					tmr.start();
				}
				frame.setVisible(true);
			}
		});

		return 1 + timer.length;
	}

	public int rugl(final simlab.ByValue sl) {
		List<simlab.ByValue> slist = (List<simlab.ByValue>) objects.get((int) sl.length);

		for (int i = 0; i < slist.size(); i++) {
			simlab.ByValue sb = slist.get(i);
			if (sb.type == Byte.MIN_VALUE) {
				Object o = sb.getJavaObject();
				if (o != null && o instanceof Method && ((Method) o).getName().equals("fetch")) {
					System.err.println("rugl " + slist.get(i + 1).getTheString());
				}
			}
		}

		return 1;
	}

	public int run(final simlab.ByValue... therunner) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		simlab.ByValue olddata = data.clone();
		simlab.ByValue runner = data.clone();

		if (therunner.length > 0)
			runner = therunner[0];

		// Pointer p = runner.getThePointer();

		if (runner.type == Byte.MIN_VALUE) {
			List<simlab.ByValue> slist = (List<simlab.ByValue>) runner.getJavaObject();
			int i = 0;
			while (i < slist.size()) {
				simlab.ByValue sl = slist.get(i);
				Object o = objects.get((int) sl.length);
				
				if( o instanceof Method ) {
					Method m = (Method)o;
	
					boolean nat = Modifier.isNative(m.getModifiers());
	
					// System.err.println("run " + m.getName());
					/*
					 * if (sl.type == Byte.MIN_VALUE) { Method m = (Method)
					 * objects.get((int) sl.length); System.err.println("method " +
					 * m.getName()); } else if (sl.type == 8 || sl.type == 9) {
					 * System.err.println(sl.toString()); } else {
					 * System.err.println(data.buffer + "  " + data.type + "  " +
					 * data.length); }
					 */
	
					if (nat) {
						/*
						 * if( Platform.isWindows() ) {
						 * Long.reverseBytes(data.buffer);
						 * Long.reverseBytes(data.type);
						 * Long.reverseBytes(data.length); }
						 */
						crnt(data);
					}
	
					int len = m.getParameterTypes().length;
					int ret = 0;
					if (len == 0)
						ret = (Integer) m.invoke(Simlab.this);
					else if (len == 1) {
						simlab.ByValue sb = slist.get(i + 1);
						// System.err.println("with param " + sb.toString());
						o = m.invoke(Simlab.this, sb);
	
						// DoubleBuffer db = null;
						// if( m.getName().equals("set") ) {
						/*
						 * simlab.ByValue sbv = datalib.get("drw"); db =
						 * sbv.getPointer().getByteBuffer(0,
						 * sbv.getByteLength()).asDoubleBuffer();
						 * 
						 * for( int ik = 400; ik < 500; ik++ ) { System.err.print(
						 * db.get(ik) + " " ); } System.err.println();
						 */
						// if( m.getName().equals("add") &&
						// sb.toString().equals("ind") ) {
						/*
						 * sbv = datalib.get("ind"); db =
						 * sbv.getPointer().getByteBuffer(0,
						 * sbv.getByteLength()).asDoubleBuffer();
						 * 
						 * for( int ik = 0; ik < 10; ik++ ) { System.err.print(
						 * db.get(ik) + " u " ); } System.err.println();
						 */
						// }
						// }
						/*
						 * if( db != null ) { for( int ik = 400; ik < 500; ik++ ) {
						 * System.err.print( db.get(ik) + " " ); }
						 * System.err.println(); }
						 */
						ret = (Integer) o;
					} else if (len == 2)
						ret = (Integer) m.invoke(Simlab.this, slist.get(i + 1), slist.get(i + 2));
					else if (len == 3)
						ret = (Integer) m.invoke(Simlab.this, slist.get(i + 1), slist.get(i + 2), slist.get(i + 3));
					else if (len == 4)
						ret = (Integer) m.invoke(Simlab.this, slist.get(i + 1), slist.get(i + 2), slist.get(i + 3), slist.get(i + 4));
					i += ret;
	
					if (nat) {
						simlab.ByValue sb = getdata();
						data.buffer = sb.buffer;
						data.type = sb.type;
						data.length = sb.length;
						/*
						 * if( Platform.isWindows() ) {
						 * Long.reverseBytes(data.buffer);
						 * Long.reverseBytes(data.type);
						 * Long.reverseBytes(data.length); }
						 */
					}
				} else if( o instanceof simlab.ByValue ){
					simlab.ByValue slb = (simlab.ByValue)o;
					data.buffer = slb.buffer;
					data.type = slb.type;
					data.length = slb.length;
				}
				i++;
			}
		}

		// buh("ind");
		// buh("calc");
		// buh("drw");

		data.buffer = olddata.buffer;
		data.type = olddata.type;
		data.length = olddata.length;

		return 1;
	}

	public int buh(String name) {
		simlab.ByValue sbv = datalib.get(name);
		DoubleBuffer db = sbv.getByteBuffer().asDoubleBuffer();

		System.err.println(sbv.buffer + " " + sbv.type + "  " + sbv.length);
		for (int ik = 0; ik < 10; ik++) {
			System.err.print(db.get(ik) + " u ");
		}
		System.err.println();

		return 0;
	}

	private void update(long p, final long w) {
		if( compmap.containsKey( p ) ) {
			Set<SimComp> set = (Set<SimComp>)compmap.get(p);
			for( SimComp scc : set ) {
				if( scc instanceof ImageComp ) {
					ImageComp sc = (ImageComp)scc;
					if( data.length == sc.h*sc.w ) {
						sc.w = (int) ((sc.h * sc.w) / w);
						sc.h = (int) w;
						sc.bi = new BufferedImage(sc.w, sc.h, BufferedImage.TYPE_INT_RGB);
						sc.reload();
					}
				}
			}
		}
	}

	/*
	 * public void trans( final simlab.ByValue... sl ) { long w = 0;//
	 * sl[0].buffer.getValue().longValue(); crnt(data); jcmdstr("trans " + w);
	 * ImageComp sc = (ImageComp)compmap.get(data.buffer); if (sc != null) { if
	 * (data.type == 32L) { if (w > 0) { sc.w = (int) ((sc.h * sc.w) / w); sc.h
	 * = (int) w; } else { sc.h = (int) ((sc.h * sc.w) / -w); sc.w = (int) -w; }
	 * sc.bi = new BufferedImage(sc.w, sc.h, BufferedImage.TYPE_INT_RGB); }
	 * sc.reload(); } }
	 */

	public int nil() {
		data.buffer = 0;

		return 0;
	}

	public int echo( final simlab.ByValue sl ) {
		String val = sl.getTheString();
		System.out.println(val);

		return 1;
	}

	public int welcome() {
		System.out.println("Welcome to Simlab 3.0");

		return 0;
	}

	public int str(final simlab.ByValue s) {
		byte[] buffer = s.getTheString().getBytes();
		long pval = allocateDirect( buffer.length );
		ByteBuffer bb = buffers.get( pval );
		bb.put(buffer);
		
		data.buffer = pval;
		data.type = 9;
		data.length = buffer.length;
		//jcrnt(bb, 8, buffer.length);
		
		return 1;
	}

	public int read(final simlab.ByValue sl) throws IOException {
		String val = sl.getTheString();
		InputStream stream;
		try {
			URL url = new URL(val);
			stream = url.openStream();
		} catch (MalformedURLException murle) {
			if (currentDir == null)
				stream = new FileInputStream(val);
			else
				stream = new FileInputStream(new File(currentDir, val));
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bb = new byte[1024];
		int r = stream.read(bb);
		int t = 0;
		while (r > 0) {
			t += r;
			baos.write(bb, 0, r);
			r = stream.read(bb);
		}
		data.length = t;
		data.type = 8;
		bb = baos.toByteArray();
		long pval = allocateDirect(t);
		ByteBuffer bbuff = buffers.get(pval);// ByteBuffer.allocateDirect(t);
		for (int i = 0; i < t; i++) {
			bbuff.put(i, bb[i]);
		}
		data.buffer = pval;
		baos.close();

		return 1;
	}

	public void write(final simlab.ByValue s) {
		try {
			// URL url = new URL( s );
			// URLConnection c = url.openConnection();
			// c.setDoOutput( true );
			// OutputStream out = c.getOutputStream();
			FileOutputStream out;
			if (currentDir != null)
				out = new FileOutputStream(new File(currentDir, s.getTheString()));
			else
				out = new FileOutputStream(s.getTheString());
			byte[] bb = data.getByteArray(0, data.getByteLength());
			// byte[] bb = ps.getByteArray(0, (int) ps.length);
			out.write(bb);
			out.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void current(final simlab.ByValue sl) {
		data.buffer = sl.buffer;
		data.type = sl.type;
		data.length = sl.length;
	}

	public int store(final simlab.ByValue ps) {
		String name = ps.getTheString();

		// data.buffer = objects.size();
		if (datalib.containsKey(name)) {
			simlab.ByValue erm = datalib.get(name);
			erm.buffer = data.type;
			erm.type = data.type;
			erm.length = data.length;
		} else {
			datalib.put(name, data.clone());
		}
		// if (data.type == Byte.MIN_VALUE) {
		// objects.add(obj);
		// }
		jstore(name, data);

		return 1;
	}

	public int fetch(final simlab.ByValue ps) {
		if (ps.type == BYTELEN) {
			String name = ps.getTheString();
			if (datalib.containsKey(name)) {
				simlab.ByValue tdata = datalib.get(name);
				data.buffer = tdata.buffer;
				data.type = tdata.type;
				data.length = tdata.length;
			}
		} else {
			data.buffer = ps.buffer;
			data.type = ps.type;
			data.length = ps.length;
		}

		return 1;
	}

	public int tail(final simlab.ByValue sl) {
		long chunk = sl.buffer;

		// Pointer p = data.getByteBuffer();
		if (data.type >= 0) {
			int bl = (int) (data.type / 8);
			ByteBuffer bb = data.getByteBuffer();

			if (data.type == DOUBLELEN) {
				DoubleBuffer dbb = bb.asDoubleBuffer();
				int i = (int) (data.length - chunk);
				for (; i < data.length - 1; i++) {
					System.out.printf("%e\t", dbb.get(i));
				}
				System.out.printf("%e\n", dbb.get(i));
			} else if (data.type == FLOATLEN) {
				int i = (int) (data.length - chunk);
				for (; i < data.length - 1; i++) {
					System.out.print(bb.getFloat(i * bl) + "\t");
				}
				System.out.print(bb.getFloat(i * bl) + "\n");
			} else if (data.type == INTLEN) {
				int i = (int) (data.length - chunk);
				for (; i < data.length - 1; i++) {
					System.out.print(bb.getInt(i * bl) + "\t");
				}
				System.out.println(bb.getInt(i * bl));
			}
		} else {
			/*
			 * if (data.type == -INTLEN) { // int bl = (int) (-data.type / 8);
			 * if (data.length >= 0) { for (int i = 0; i < data.length; i +=
			 * chunk) { int k = i; for (; k < Math.min(data.length, i + chunk) -
			 * 1; k++) { // System.out.print(getPseudoIntr(p, k) + "\t"); }
			 * //System.out.println(getPseudoIntr(p, k)); } } else { for (int k
			 * = 0; k < chunk - 1; k++) { // System.out.print(getPseudoInt(p, k)
			 * + "\t"); } //System.out.println(getPseudoInt(p, (int) (chunk -
			 * 1))); } } else if (data.type == -UINTLEN) { if (data.length >= 0)
			 * { for (int i = 0; i < data.length; i += chunk) { int k = i; for
			 * (; k < Math.min(data.length, i + chunk) - 1; k++) {
			 * System.out.print(getPseudoUintr(p, k) + "\t"); }
			 * System.out.println(getPseudoUintr(p, k)); } } else { for (int k =
			 * 0; k < chunk - 1; k++) { System.out.print(getPseudoInt(p, k) +
			 * "\t"); } System.out.println(getPseudoInt(p, (int) (chunk - 1)));
			 * } } else if (data.type == -DOUBLELEN) { if (data.length >= 0) {
			 * for (int i = 0; i < data.length; i += chunk) { int k = i; for (;
			 * k < Math.min(data.length, i + chunk) - 1; k++) {
			 * System.out.print(getPseudoDoubler(p, k) + "\t"); }
			 * System.out.println(getPseudoDoubler(p, k)); } } else { for (int k
			 * = 0; k < chunk - 1; k++) { System.out.print(getPseudoInt(p, k) +
			 * "\t"); } System.out.println(getPseudoInt(p, (int) (chunk - 1)));
			 * } }
			 */
		}

		return 1;
	}
	
	public int kendall( final simlab.ByValue otherdata, final simlab.ByValue chunk ) {
		long ch = chunk.getLong();
		
		Buffer b = data.getBuffer();
		if( b instanceof IntBuffer ) {
			IntBuffer	x = (IntBuffer)b;
			IntBuffer	y = (IntBuffer)otherdata.getBuffer();
			
			int c = 0;
			int d = 0;
			for( int i = 0; i < data.length; i++ ) {
				for( int j = i+1; j < data.length; j++ ) {
					int xi = x.get(i);
					int xj = x.get(j);
					int yi = y.get(i);
					int yj = y.get(j);
					if( xi > xj && yi > yj || (xi < xj && yi < yj) ) c++;
					if( xi > xj && yi < yj || (xi < xj && yi > yj) ) d++; 
				}
			}
			
			double kt = (double)(2*(c-d))/(double)(data.length*(data.length-1));
			
			data.buffer = Double.doubleToLongBits(kt);
			data.type = DOUBLELEN;
			data.length = 0;
		} else if( b instanceof DoubleBuffer ) {
			DoubleBuffer	x = (DoubleBuffer)b;
			DoubleBuffer	y = (DoubleBuffer)otherdata.getBuffer();
			
			int c = 0;
			int d = 0;
			for( int i = 0; i < data.length; i++ ) {
				for( int j = i+1; j < data.length; j++ ) {
					double xi = x.get(i);
					double xj = x.get(j);
					double yi = y.get(i);
					double yj = y.get(j);
					if( xi > xj && yi > yj || (xi < xj && yi < yj) ) c++;
					if( xi > xj && yi < yj || (xi < xj && yi > yj) ) d++; 
				}
			}
			
			double kt = (double)(2*(c-d))/(double)(data.length*(data.length-1));
			
			data.buffer = Double.doubleToLongBits(kt);
			data.type = DOUBLELEN;
			data.length = 0;
		}
		
		return 1;
	}
	
	public int entropy() {
		entropy( datalib.get("e") );
		
		return 1;
	}
	
	public int entropy( final simlab.ByValue log ) {
		entropy( log, new simlab.ByValue( data.length ) );
		
		return 1;
	}
	
	public int entropy( final simlab.ByValue log, final simlab.ByValue chunk ) {
		Buffer 	b = data.getBuffer();
		double 	lv = log.getRealValue();
		long	ch = chunk.getLong();
		long	len = data.length/ch;
		simlab.ByValue rb;
		if( len == 1 ) rb = new simlab.ByValue(0.0);
		else {
			long p = allocateDirect( bytelength(DOUBLELEN, len) );
			rb = new simlab.ByValue( len, DOUBLELEN, p );
		}
		if( b instanceof IntBuffer ) {
			IntBuffer ib = (IntBuffer)b;
			for( int i = 0; i < data.length; i+=ch ) {
				Map<Integer,Integer>	mm = new HashMap<Integer,Integer>();
				for( int k = i; k < i+ch; k++ ) {
					int val = ib.get( k );
					if( mm.containsKey( val ) ) {
						mm.put( val, mm.get( val )+1 );
					} else {
						mm.put(val, 1);
					}
				}
				double ret = 0.0;
				for( int k = i; k < i+ch; k++ ) {
					double p = (double)mm.get( ib.get(k) )/(double)ch;
					ret += p*Math.log( p )/Math.log( lv );
				}
				rb.put( (int)(i/ch), -ret );
			}
		} else if( b instanceof ByteBuffer ) {
			ByteBuffer ib = (ByteBuffer)b;
			for( int i = 0; i < data.length; i+=ch ) {
				Map<Byte,Integer>	mm = new HashMap<Byte,Integer>();
				for( int k = i; k < i+ch; k++ ) {
					byte val = ib.get( k );
					if( mm.containsKey( val ) ) {
						mm.put( val, mm.get( val )+1 );
					} else {
						mm.put(val, 1);
					}
				}
				double ret = 0.0;
				for( int k = i; k < i+ch; k++ ) {
					double p = (double)mm.get( ib.get(k) )/(double)ch;
					ret += p*Math.log( p )/Math.log( lv );
				}
				rb.put( (int)(i/ch), -ret );
			}
		}
		
		data.buffer = rb.buffer;
		data.length = rb.length;
		data.type = rb.type;
		
		return 2;
	}

	public int head(final simlab.ByValue sl) {
		long chunk = sl.buffer;

		// Pointer p = data.getThePointer();
		if (data.type >= 0) {
			int bl = (int) (data.type / 8);
			ByteBuffer bb = data.getByteBuffer();

			if (data.type == DOUBLELEN) {
				DoubleBuffer dbb = bb.asDoubleBuffer();
				int i = 0;
				for (; i < chunk - 1; i++) {
					System.out.printf("%e\t", dbb.get(i));
				}
				System.out.printf("%e\n", dbb.get(i));
			} else if (data.type == FLOATLEN) {
				int i = 0;
				for (; i < chunk - 1; i++) {
					System.out.print(bb.getFloat(i * bl) + "\t");
				}
				System.out.print(bb.getFloat(i * bl) + "\n");
			} else if (data.type == INTLEN || data.type == UINTLEN) {
				int i = 0;
				for (; i < chunk - 1; i++) {
					System.out.print(bb.getInt(i * bl) + "\t");
				}
				System.out.println(bb.getInt(i * bl));
			}
		} else {
			if (data.type == -BYTELEN || data.type == -8) { // int bl = (int) (-data.type / 8);
				if (data.length >= 0) {
					//for (int i = 0; i < data.length; i += chunk) {
					System.err.println( "errb "+data.buffer );
					for (int k = 0; k < chunk - 1; k++) {
						System.out.print(data.getPseudoByter(k) + "\t");
					}
					System.out.println(data.getPseudoByter((int) (chunk - 1)));
					//}
				} else {
					for (int k = 0; k < chunk - 1; k++) {
						System.out.print(data.getPseudoByte(k) + "\t");
					}
					System.out.println(data.getPseudoByte((int) (chunk - 1)));
				}
			} else if (data.type == -INTLEN) { // int bl = (int) (-data.type / 8);
				if (data.length >= 0) {
					//for (int i = 0; i < data.length; i += chunk) {
					for (int k = 0; k < chunk - 1; k++) {
						System.out.print(data.getPseudoInt(k) + "\t");
					}
					System.out.println(data.getPseudoInt((int) (chunk - 1)));
					//}
				} else {
					for (int k = 0; k < chunk - 1; k++) {
						System.out.print(data.getPseudoInt(k) + "\t");
					}
					System.out.println(data.getPseudoInt((int) (chunk - 1)));
				}
			} else if (data.type == -UINTLEN) {
				if (data.length >= 0) {
					for (int k = 0; k < chunk - 1; k++) {
						System.out.print(data.getPseudoInt(k) + "\t");
					}
					System.out.println(data.getPseudoInt((int) (chunk - 1)));
				} else {
					for (int k = 0; k < chunk - 1; k++) {
						System.out.print(data.getPseudoInt(k) + "\t");
					}
					System.out.println(data.getPseudoInt((int) (chunk - 1)));
				}
			} else if (data.type == -DOUBLELEN) {
				if (data.length >= 0) {
					for (int k = 0; k < chunk - 1; k++) {
						System.out.print(data.getPseudoDouble(k) + "\t");
					}
					System.out.println(data.getPseudoDouble((int) (chunk - 1)));
				} else {
					for (int k = 0; k < chunk - 1; k++) {
						System.out.print(data.getPseudoDouble(k) + "\t");
					}
					System.out.println(data.getPseudoDouble((int) (chunk - 1)));
				}
			}
		}

		return 1;
	}

	public int print() {
		if (data.type == UBYTELEN || data.type == BYTELEN) {
			String pstr = data.getTheString();
			System.out.println(pstr);
		} else {
			print(new simlab.ByValue(data.length));
		}

		return 0;
	}

	public int print(final simlab.ByValue sl) {
		long chunk = sl.buffer;

		// if( data.type != 66 ) {
		// System.err.println(data.buffer + "  " + data.type + "  " +
		// data.length );
		// System.err.println(data.toString());
		// return 1;
		// }

		// Pointer p = data.getThePointer();
		if (data.length == 0) {
			if (data.type == INTLEN || data.type == LONGLEN || data.type == ULONGLEN ) {
				System.out.println(data.buffer);
			} else if (data.type == DOUBLELEN) {
				System.out.println(Double.longBitsToDouble(data.buffer));
			}
		} else if (data.type >= 0) {
			int bl = (int) (data.type / 8);
			ByteBuffer bb = data.getByteBuffer();

			if (data.type == DOUBLELEN) {
				DoubleBuffer dbb = bb.asDoubleBuffer();
				for (int i = 0; i < data.length; i += chunk) {
					int k = i;
					for (; k < Math.min(data.length, i + chunk) - 1; k++) {
						System.out.printf("%e\t", dbb.get(k));
						// System.out.print(dbb.get(k) + "\t");
					}
					System.out.printf("%e\n", dbb.get(k));
				}
			} else if (data.type == LONGLEN || data.type == ULONGLEN) {
				LongBuffer dbb = bb.asLongBuffer();
				for (int i = 0; i < data.length; i += chunk) {
					int k = i;
					for (; k < Math.min(data.length, i + chunk) - 1; k++) {
						System.out.printf("%d\t", dbb.get(k));
						// System.out.print(dbb.get(k) + "\t");
					}
					System.out.printf("%d\n", dbb.get(k));
				}
			} else if (data.type == FLOATLEN) {
				for (int i = 0; i < data.length; i += chunk) {
					int k = i;
					for (; k < Math.min(data.length, i + chunk) - 1; k++) {
						System.out.print(bb.getFloat(k * bl) + "\t");
					}
					System.out.println(bb.getFloat(k * bl));
				}
			} else if (data.type == INTLEN || data.type == UINTLEN) {
				for (int i = 0; i < data.length; i += chunk) {
					int k = i;
					for (; k < Math.min(data.length, i + chunk) - 1; k++) {
						System.out.print(bb.getInt(k * bl) + "\t");
					}
					System.out.println(bb.getInt(k * bl));
				}
			} else if (data.type == UBYTELEN || data.type == BYTELEN) {
				if( data.type == UBYTELEN ) {
					for (int i = 0; i < data.length; i += chunk) {
						int k = i;
						for (; k < Math.min(data.length, i + chunk) - 1; k++) {
							System.out.print( (int)bb.get(k) + "\t" );
						}
						System.out.println( (int)bb.get(k) );
					}
				} else {
					String str = data.getTheString();
					for (int i = 0; i < data.length; i += chunk) {
						int k = i;
						for (; k < Math.min(data.length, i + chunk) - 1; k++) {
							System.out.print( str.charAt(k) );
						}
						System.out.println( str.charAt(k) );
					}
				}
				/*
				 * if( chunk == 0 ) { System.out.println( new
				 * String(bb.toString()) ); } else { byte[] bchunk = new
				 * byte[(int)chunk]; for (int i = 0; i < data.length; i +=
				 * chunk) { //int k = i; bb.get( bchunk ); System.out.println(
				 * new String(bchunk) ); } }
				 */
			} else if( data.type > 66 ) {
				if( 8%data.type == 2 ) {
					//data.getByteBuffer().get
				} else {
					byte[] bbv = new byte[(int)(data.getByteLength()/data.length)];
					for (int i = 0; i < data.length; i += chunk) {
						int k = i;
						for( ; k < Math.min(i+chunk,data.length) - 1; k++ ) {
							bb.position( (int)(k*bbv.length) );
							bb.get( bbv );
							BigInteger bi = new BigInteger( bbv );
							System.out.print( bi+"\t" );
						}
						bb.position( (int)(k*bbv.length) );
						bb.get( bbv );
						BigInteger bi = new BigInteger( bbv );
						System.out.println( bi+"\n" );
					}
				}
			}
		} else {
			if (data.type == -INTLEN) { // int bl = (int) (-data.type / 8);
				if (data.length >= 0) {
					for (int i = 0; i < data.length; i += chunk) {
						int k = i;
						for (; k < Math.min(data.length, i + chunk) - 1; k++) {
							System.out.print(data.getPseudoIntr(k) + "\t");
						}
						System.out.println(data.getPseudoIntr(k));
					}
				} else {
					for (int k = 0; k < chunk - 1; k++) {
						System.out.print(data.getPseudoInt(k) + "\t");
					}
					System.out.println(data.getPseudoInt((int) (chunk - 1)));
				}
			} else if (data.type == -UINTLEN) {
				if (data.length >= 0) {
					for (int i = 0; i < data.length; i += chunk) {
						int k = i;
						for (; k < Math.min(data.length, i + chunk) - 1; k++) {
							System.out.print(data.getPseudoUint(k) + "\t");
						}
						System.out.println(data.getPseudoUint(k));
					}
				} else {
					for (int k = 0; k < chunk - 1; k++) {
						System.out.print(data.getPseudoInt(k) + "\t");
					}
					System.out.println(data.getPseudoInt((int) (chunk - 1)));
				}
			} else if (data.type == -DOUBLELEN) {
				if (data.length >= 0) {
					for (int i = 0; i < data.length; i += chunk) {
						int k = i;
						for (; k < Math.min(data.length, i + chunk) - 1; k++) {
							System.out.print(data.getPseudoDoubler(k) + "\t");
						}
						System.out.println(data.getPseudoDoubler(k));
					}
				} else {
					for (int k = 0; k < chunk - 1; k++) {
						System.out.print(data.getPseudoInt(k) + "\t");
					}
					System.out.println(data.getPseudoInt((int) (chunk - 1)));
				}
			}
		}
		return 1;
	}

	public int back() {
		return 0;
	}

	Map<String, Long> bmap = new HashMap<String, Long>();

	public List<simlab.ByValue> parsePar(StringTokenizer st) {
		List<simlab.ByValue> olist = new ArrayList<simlab.ByValue>();
		tempbuffer.position(0);
		// long offset = 0;
		// long pvalr =
		// Pointer.nativeValue(Native.getDirectBufferPointer(tempbuffer));
		// System.err.println(pvalr);
		while (st.hasMoreTokens()) {
			String str = st.nextToken(endStr);
			if (str.startsWith("\"")) {
				// clist.add( str.getClass() );
				
				String val = "";
				int start = 1;
				while( st.hasMoreElements() && (!str.endsWith("\"")/* || str.charAt(str.length()-2) == '\\'*/) ) {
					String nstr = str.substring(start, str.length());
					val += nstr.replace("\\n", "\n");
					start = 0;
					
					str = st.nextToken("\"");
				}
				
				if( str.equals("\"") ) {
					val += st.nextToken("\"");
					start = 0;
				}
				
				String nstr = str.substring(start, str.length() - start);
				val += nstr.replace("\\n", "\n");
				// System.err.println("eehehe " + val);

				long pval = 0;
				if (bmap.containsKey(val)) {
					pval = bmap.get(val);
				} else {
					byte[] bytes = val.getBytes();
					// Pointer ptr = nptr.getPointer( tempbuffer.position() );
					pval = allocateDirect(bytes.length);
					bb = buffers.get(pval);// ByteBuffer.allocateDirect(bytes.length);
					for (byte b : bytes) {
						bb.put(b);
					}
					bmap.put(val, pval);
				}
				// tempbuffer.put(bytes);
				// long pval =
				// Pointer.nativeValue(Native.getDirectBufferPointer(bb));
				// long pval = pvalr + offset;
				// offset += bytes.length;

				// ptr.nativeValue( ptr );
				// NativeLongByReference lbr = new NativeLongByReference();
				// lbr.setPointer( ptr );

				// LongByReference lbr = new Longbyre
				simlab.ByValue nsl = new simlab.ByValue(bb.limit(), BYTELEN, pval);
				olist.add(nsl);
				/*
				 * } else if (str.startsWith("-")) { simlab.ByValue nsl = null;
				 * try { long l = Long.parseLong(str); nsl = new
				 * simlab.ByValue(0, LONGLEN, -l); } catch (Exception e) { }
				 * 
				 * if (nsl == null) { try { double d = Double.parseDouble(str);
				 * nsl = new simlab.ByValue(0, DOUBLELEN,
				 * Double.doubleToRawLongBits(-d)); } catch (Exception e) { } }
				 * 
				 * if (nsl != null) olist.add(nsl);
				 */
			} else if (str.startsWith("[")) {
				List<Double> d_vec = new ArrayList<Double>();

				float fval;
				String val;
				if( str.endsWith("]") ) {
					val = str.substring(1,str.length()-1);
				} else {
					val = str.substring(1);
					fval = Float.parseFloat(val); // sscanf( result+1, "%e", &fval//
					
					d_vec.add((double) fval);
					str = st.nextToken(endStr);
					int len = str.length();
					while (str.charAt(len - 1) != ']') {
						// sscanf( result, "%e", &fval );
						// val = str.substring(1);
						fval = Float.parseFloat(str);
						d_vec.add((double) fval);
						str = st.nextToken(endStr);
						len = str.length();
					}
					val = str.substring(0, str.length() - 1);
				}
				fval = Float.parseFloat(val);
				d_vec.add((double) fval);

				long pval = allocateDirect(8 * d_vec.size());
				bb = buffers.get(pval); // ByteBuffer.allocateDirect(8 *
										// d_vec.size());
				DoubleBuffer dd = bb.asDoubleBuffer();
				for (double d : d_vec) {
					dd.put(d);
				}
				simlab.ByValue nsl = new simlab.ByValue(d_vec.size(), DOUBLELEN, pval);
				olist.add(nsl);
			} else if (str.contains(".")) {
				double d = Double.parseDouble(str);
				simlab.ByValue nsl = new simlab.ByValue(0, DOUBLELEN, Double.doubleToRawLongBits(d));
				// nsl.type = DOUBLELEN;
				// nsl.length = 0;
				// nsl.buffer = new DoubleByReference(d);
				// clist.add( double.class );
				olist.add(nsl);
			} else if (str.contains("*")) {
				simlab.ByValue nsl = null;

				String[] split = str.split("\\*");
				double d = 1.0;
				long l = 1;
				for (String n : split) {
					boolean lfail = false;
					try {
						long lng = Long.parseLong(n);
						l *= lng;
					} catch (Exception e) {
						lfail = true;
					}

					if (lfail) {
						try {
							double dbl = Double.parseDouble(n);
							d *= dbl;
						} catch (Exception e) {
						}
					}
				}

				if (d > 1.0)
					nsl = new simlab.ByValue(0, DOUBLELEN, Double.doubleToRawLongBits(d * l));
				else
					nsl = new simlab.ByValue(0, LONGLEN, l);

				if (nsl != null)
					olist.add(nsl);
			} else {
				simlab.ByValue nsl = null;
				try {
					long l = Long.parseLong(str);
					nsl = new simlab.ByValue(0, LONGLEN, l);
				} catch (Exception e) {
				}

				if (nsl == null) {
					try {
						double d = Double.parseDouble(str);
						nsl = new simlab.ByValue(0, DOUBLELEN, Double.doubleToRawLongBits(d));
					} catch (Exception e) {
					}
				}

				if (nsl == null) {
					if( str.equals("len") ) {
						nsl = new simlab.ByValue(0, LONGLEN, data.length);
					} else if (datalib.containsKey(str)) {
						nsl = datalib.get(str);
					} else {
						Method[] mm = this.getClass().getMethods();
						Method them = null;
						for (Method me : mm) {
							if (str.equals(me.getName())) {
								them = me;
								break;
							}
						}
						if (them != null) {
							Function f = Function.getFunction("csimlab", str);
							long fp = f == null ? 0 : Pointer.nativeValue(f);
							nsl = new simlab.ByValue(objects.size(), Byte.MIN_VALUE, fp);
							objects.add(them);
						} else {
							nsl = new simlab.ByValue();
							datalib.put(str, nsl);
						}
					}
				}
				if (nsl != null)
					olist.add(nsl);
			}
		}

		return olist;
	}

	private Method getMethod(String fname, int osize) {
		Method m = null;
		try {
			if (osize == 0) {
				try {
					m = Simlab.class.getMethod(fname);
				} catch (Exception e) {
				}
				if (m == null)
					m = Simlab.class.getMethod("simlab_" + fname);
			} else if (osize == 1) {
				try {
					m = Simlab.class.getMethod(fname, simlab.ByValue.class);
				} catch (Exception e) {
				}
				if (m == null)
					m = Simlab.class.getMethod("simlab_" + fname, simlab.ByValue.class);
			} else if (osize == 2) {
				try {
					m = Simlab.class.getMethod(fname, simlab.ByValue.class, simlab.ByValue.class);
				} catch (Exception e) {
				}
				if (m == null)
					m = Simlab.class.getMethod("simlab_" + fname, simlab.ByValue.class, simlab.ByValue.class);
			} else if (osize == 3) {
				try {
					m = Simlab.class.getMethod(fname, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class);
				} catch (Exception e) {
				}
				if (m == null)
					m = Simlab.class.getMethod("simlab_" + fname, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class);
			} else if (osize == 4) {
				try {
					m = Simlab.class.getMethod(fname, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class);
				} catch (Exception e) {
				}
				if (m == null)
					m = Simlab.class.getMethod("simlab_" + fname, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class);
			} else if (osize == 5) {
				try {
					m = Simlab.class.getMethod(fname, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class);
				} catch (Exception e) {
				}
				if (m == null)
					m = Simlab.class.getMethod("simlab_" + fname, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class, simlab.ByValue.class);
			}
		} catch (Exception e) {
		}

		if (m == null) {
			try {
				m = Simlab.class.getMethod(fname, simlab.ByValue[].class);
			} catch (NoSuchMethodException e) {
			}

			if (m == null) {
				try {
					m = Simlab.class.getMethod(fname, simlab.ByValue.class, simlab.ByValue[].class);
				} catch (NoSuchMethodException e) {
				}
			}
		}

		return m;
	}

	public static String endStr = " ,)\n";

	public static boolean debug = true;

	public int cmd(final simlab.ByValue sl) {
		String s = sl.getTheString();
		return command(s);
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	public @interface pann {
	    public String name();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface mann {
	    public String name();
	    public boolean dataChanging();
	}
	
	@mann(name="help: prints names of all available methods", dataChanging=false)
	public int help() {
		Method[] mm = Simlab.this.getClass().getMethods();
		for( Method m : mm ) {
			mann ma = m.getAnnotation( mann.class );
			if( ma != null ) {
				System.out.println(ma.name());
			}
		}
		
		return 0;
	}
	
	/**
	 * 
	 * @param f
	 * @return
	 */
	@mann(name="usage: prints usage information for a method", dataChanging=false)
	public int usage( @pann(name="strMethodName") final simlab.ByValue f ) {
		String fstr = f.getTheString();
		Method[] mm = Simlab.this.getClass().getMethods();
		for( Method m : mm ) {
			if( m.getName().equals(fstr) ) {
				mann ma = m.getAnnotation( mann.class );
				if( ma != null ) {
					System.out.println(ma.name());
				
					Annotation[][] aaa = m.getParameterAnnotations();
					System.out.print("Usage: "+fstr);
					if( aaa.length > 0 ) System.out.print("(");
					boolean first = true;
					for( Annotation[] aa : aaa ) {
						for( Annotation a : aa ) {
							if( a instanceof pann ) {
								if( first ) {
									System.out.print( " "+((pann)a).name() );
									first = false;
								} else System.out.print( ", "+((pann)a).name() );
							}
						}
					}
					if( aaa.length > 0 ) System.out.println(" )");
				}
			}
		}
		
		return 1;
	}
	
	private void runMethod( Method m, List<simlab.ByValue> olist ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		boolean nv = Modifier.isNative(m.getModifiers());
		if (nv) {
			crnt(data);
		}
		
		if (m.getParameterTypes().length > 0 && m.getParameterTypes()[0] == simlab.ByValue[].class) {
			m.invoke(Simlab.this, new Object[] { olist.toArray(new simlab.ByValue[0]) });
		} else if (m.getParameterTypes().length > 1 && m.getParameterTypes()[1] == simlab.ByValue[].class) {
			List<simlab.ByValue> list = olist.subList(1, olist.size());
			/*
			 * if( list.size() == 0 ) { Object[] stuff = new
			 * Object[] { new simlab.ByValue[0] };
			 * m.invoke(Simlab.this, olist.get(0), new
			 * simlab.ByValue[0] ); } else { Object[] stuff = new
			 * Object[] { list.toArray(new simlab.ByValue[0]) };
			 * m.invoke(Simlab.this, olist.get(0), list.toArray(new
			 * simlab.ByValue[0])); }
			 */
			m.invoke(Simlab.this, olist.get(0), list.toArray(new simlab.ByValue[0]));
		} else if (olist.size() == 0) {
			m.invoke(Simlab.this);
		} else if (olist.size() == 1) {
			m.invoke(Simlab.this, olist.get(0));
		} else if (olist.size() == 2) {
			m.invoke(Simlab.this, olist.get(0), olist.get(1));
		} else if (olist.size() == 3) {
			m.invoke(Simlab.this, olist.get(0), olist.get(1), olist.get(2));
		} else if (olist.size() == 4) {
			m.invoke(Simlab.this, olist.get(0), olist.get(1), olist.get(2), olist.get(3));
		} else if (olist.size() == 5) {
			m.invoke(Simlab.this, olist.get(0), olist.get(1), olist.get(2), olist.get(3), olist.get(4));
		}
		if (nv) {
			simlab.ByValue sb = getdata();
			data.buffer = sb.buffer;
			data.type = sb.type;
			data.length = sb.length;
		}
	}

	private int command(String s) {
		StringTokenizer st = new StringTokenizer(s);
		if (st.hasMoreTokens()) {
			String fname = st.nextToken(" (_\n");
			try {
				// clist.toArray( new Class<?>[ clist.size() ]
				// List<Class<?>> clist = new ArrayList<Class<?>>();
				List<simlab.ByValue> olist = parsePar(st);

				// if( olist.size() == 0 ) {
				/*
				 * Class[] cc = new Class[olist.size()]; for( int i = 0; i <
				 * cc.length; i++ ) { cc[i] = simlab.ByValue.class; }
				 */

				// m = Simlab.class.getMethod( fname );

				Method m = getMethod(fname, olist.size());

				if (m == null) {
					simlab.ByValue slval = datalib.get(fname);
					if (slval != null) {
						data.buffer = slval.buffer;
						data.type = slval.type;
						data.length = slval.length;
					} else {
						System.out.println("No function: " + fname);
					}
				} else {
					/*
					 * } else if( olist.size() == 1 ) { m =
					 * Simlab.class.getMethod( fname, olist.get(0).getClass() );
					 * }
					 */
					// Object[] args = new Object[] { olist.toArray( new
					// simlab.ByValue[ olist.size() ] ) };
					// if( where ) data = getdata();
					// where = false;
					
					mann mnn = m.getAnnotation( mann.class );
					if( data.isJavaObject() && data.getJavaObject() instanceof List && mnn != null && mnn.dataChanging() ) {
						long oldbuf = data.buffer;
						long oldtyp = data.type;
						long oldlen = data.length;
						
						List<simlab.ByValue>	lsb = (List<simlab.ByValue>)data.getJavaObject();
						for( simlab.ByValue sb : lsb ) {
							data.buffer = sb.buffer;
							data.type = sb.type;
							data.length = sb.length;
							
							runMethod( m, olist );
						}
						
						data.buffer = oldbuf;
						data.type = oldtyp;
						data.length = oldlen;
					} else {
						runMethod( m, olist );
					}
					
					// if (m.getName().contains("conv")) {}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			/*
			 * if( m == null ) { byte[] bytes = Native.toByteArray( fname );
			 * Pointer ptr = Native.getDirectBufferPointer( tempbuffer );
			 * NativeLongByReference lbr = new NativeLongByReference();
			 * lbr.setPointer( ptr );
			 * 
			 * if( !where ) crnt( data ); where = true;
			 * 
			 * simlab.ByValue slbv = new simlab.ByValue( bytes.length, BYTELEN,
			 * lbr ); jcmd( slbv ); for( Pointer key : compmap.keySet() ) {
			 * SimComp sc = compmap.get( key ); if( sc != null ) sc.reload(); }
			 * }
			 */

			if (compmap.containsKey(data.buffer)) {
				for (SimComp sc : compmap.get(data.buffer)) {
					if (sc != null) {
						sc.reload();
					}
				}
			}

			/*
			 * for (long key : compmap.keySet()) { SimComp sc =
			 * compmap.get(key); if (sc != null) sc.reload(); }
			 */

			/*
			 * ByteBuffer buf = ByteBuffer.allocateDirect(6); buf.put(0, 's');
			 * buf.put(1, 'i'); buf.put(2, 'm'); buf.put(3, 'm'); buf.put(4,
			 * 'i'); buf.put(5, '\n');
			 * 
			 * simlab sl = new simlab();
			 * 
			 * Pointer ptr = Native.getDirectBufferPointer( buf ); //Pointer p =
			 * new Pointer(); sl.buffer = ptr.; sl.type = 8; sl.length =
			 * s.length();
			 * 
			 * cmd( sl );
			 */
		}

		return 1;
	}

	private int bytelength(long type, long length) {
		if (type < 8)
			return (int) (type * length) / 8;
		return (int) ((type / 8) * length);
	}

	public int type(final simlab.ByValue type) {
		long val = type.buffer;
		long newtype;
		long oldtype;

		if (val < 8)
			newtype = val;
		else
			newtype = (val / 8) * 8;
		if (data.type < 8)
			oldtype = data.type;
		else
			oldtype = (data.type / 8) * 8;

		// data = new simlab.ByValue((long) (((long) data.length * oldtype) /
		// (long) newtype), val, data.buffer);
		long newlen = (long) (((long) data.length * oldtype) / (long) newtype);
		data.type = val;
		data.length = newlen;

		return 1;
	}

	public int simlab_new(final simlab.ByValue type, final simlab.ByValue len) {
		long lenval = len.buffer;
		data.type = type.buffer;
		if (lenval == 0) {
			data.length = 0;
		} else {
			int bytelen = bytelength(data.type, lenval);
			long pval = allocateDirect(bytelen);
			data.buffer = pval;
			data.length = lenval;
		}

		return 2;
	}

	public int simlab_new(final simlab.ByValue type) {
		simlab_new(type, new simlab.ByValue(0, 65, 0));

		return 1;
	}

	public int resize(final simlab.ByValue len) {
		long lenval = len.buffer;
		int bytelen = bytelength(data.type, lenval);

		// NativeLongByReference nat = new NativeLongByReference();
		// nat.setPointer( Native.getDirectBufferPointer( bb ) );

		long pval = allocateDirect(bytelen); // Pointer.nativeValue(Native.getDirectBufferPointer(bb));
		
		if( buffers.containsKey(data.buffer) ) {
			ByteBuffer oldbuffer = buffers.get(data.buffer);
			ByteBuffer newbuffer = buffers.get(pval);
			for( int i = 0; i < Math.min( oldbuffer.limit(), newbuffer.limit() ); i++ ) {
				newbuffer.put(i, oldbuffer.get(i));
			}
			
			buffers.remove( data.buffer );
			for( String name : datalib.keySet() ) {
				simlab.ByValue sl = datalib.get(name);
				if( sl.buffer == data.buffer ) {
					sl.buffer = pval;
					sl.length = lenval;
				}
			}
		}
		// data = new simlab.ByValue(lenval, data.type, pval);
		data.buffer = pval;
		data.length = lenval;

		return 1;
	}

	public int var() {
		for (String s : datalib.keySet()) {
			simlab.ByValue slbv = datalib.get(s);
			System.out.println(s + "  " + slbv.type + "  " + slbv.length);
		}

		return 0;
	}

	public int printtype() {
		System.out.println(data.type);

		return 0;
	}

	public int printname() {
		for (Entry<String, ByValue> e : datalib.entrySet()) {
			System.err.println(e + "  " + data.buffer + " " + data.type);
			if (e.getValue() != null && e.getValue().equals(data)) {
				System.err.println("mu2");
				System.out.println(e.getKey());
				return 0;
			}
		}
		System.err.println("mu");
		return 0;
	}

	public int len() {
		data.buffer = data.length;
		data.length = 0;
		data.type = 65;

		return 0;
	}

	public int getthetype() {
		data.buffer = data.type;
		data.length = 0;
		data.type = 65;

		return 0;
	}

	//PrintStream origerr = null;
	public int printlen() {
		//if( origerr == null ) origerr = System.err;
		//origerr.println( System.out );
		
		System.out.println(data.length);

		return 0;
	}

	public int printval(final simlab.ByValue... sl) {
		if (data.length > 0) {
			
		} else {
			
		}

		return 0;
	}
	
	public int find( final simlab.ByValue m ) {
		Buffer b = m.getBuffer();
		if( b instanceof ByteBuffer ) {
			ByteBuffer bb = (ByteBuffer)b;
			Set<Byte>	bs = new HashSet<Byte>();
			for( int i = 0; i < m.length; i++ ) {
				bs.add( bb.get(i) );
			}
			
			Buffer db = data.getBuffer();
			if( db instanceof ByteBuffer ) {
				List<Integer>	lb = new ArrayList<Integer>();
				ByteBuffer dbb = (ByteBuffer)db;
				for( int i = 0; i < data.length; i++ ) {
					if( bs.contains( dbb.get(i) ) ) lb.add(i);
				}
				
				long p = allocateDirect( bytelength(INTLEN, lb.size()) );
				IntBuffer bpi = buffers.get(p).asIntBuffer();
				
				for( int i = 0; i < lb.size(); i++ ) {
					bpi.put(i,lb.get(i));
				}
				
				data.buffer = p;
				data.type = INTLEN;
				data.length = lb.size();
			}
		} else if( b instanceof IntBuffer ) {
			IntBuffer ib = (IntBuffer)b;
			Set<Integer>	is = new HashSet<Integer>();
			for( int i = 0; i < m.length; i++ ) {
				is.add( ib.get(i) );
			}
			
			Buffer db = data.getBuffer();
			if( db instanceof IntBuffer ) {
				List<Integer>	li = new ArrayList<Integer>();
				IntBuffer dbi = (IntBuffer)db;
				for( int i = 0; i < data.length; i++ ) {
					if( is.contains( dbi.get(i) ) ) li.add(i);
				}
				
				long p = allocateDirect( bytelength(data.type, li.size()) );
				IntBuffer bpi = buffers.get(p).asIntBuffer();
				
				for( int i = 0; i < li.size(); i++ ) {
					bpi.put(i,li.get(i));
				}
				
				data.buffer = p;
				data.type = INTLEN;
				data.length = li.size();
			}
		} else if( b instanceof DoubleBuffer ) {
			DoubleBuffer db = (DoubleBuffer)b;
			Set<Double>	ds = new HashSet<Double>();
			for( int i = 0; i < m.length; i++ ) {
				ds.add( db.get(i) );
			}
			
			Buffer db2 = data.getBuffer();
			if( db2 instanceof IntBuffer ) {
				List<Integer>	ld = new ArrayList<Integer>();
				IntBuffer dbi = (IntBuffer)db2;
				for( int i = 0; i < data.length; i++ ) {
					if( ds.contains( (double)dbi.get(i) ) ) ld.add(i);
				}
				
				long p = allocateDirect( bytelength(data.type, ld.size()) );
				IntBuffer bpi = buffers.get(p).asIntBuffer();
				
				for( int i = 0; i < ld.size(); i++ ) {
					bpi.put(i,ld.get(i));
				}
				
				data.buffer = p;
				data.type = INTLEN;
				data.length = ld.size();
			}
		}
		
		return 1;
	}
	
	public int getter( final simlab.ByValue idx ) {
		getter( data, idx );
		simlab.ByValue sb = getdata();
		data.buffer = sb.buffer;
		data.type = sb.type;
		data.length = sb.length;
		
		return 1;
	}
	
	@mann(name="put: changes values in positions", dataChanging=true)
	public int put( @pann(name="what") final simlab.ByValue what, @pann(name="where") final simlab.ByValue where ) {
		put( data, what, where );
		
		return 2;
	}
	
	public int get( final simlab.ByValue what ) {
		//IntBuffer w = (IntBuffer)what.getBuffer();
		long p = allocateDirect( bytelength(data.type, what.length) );
		ByteBuffer ret = buffers.get(p);
		
		get( new simlab.ByValue(what.length, data.type, p), data, what );
		/*Buffer b = data.getBuffer();
		if( b instanceof ByteBuffer ) {
			ByteBuffer bb = (ByteBuffer)b;
			ByteBuffer res = (ByteBuffer)ret;
				
			for( int i = 0; i < what.length; i++ ) {
				res.put(i, bb.get(w.get(i)));
			}
		} else if( b instanceof IntBuffer ) {
			IntBuffer ib = (IntBuffer)b;
			IntBuffer res = (IntBuffer)ret.asIntBuffer();
			
			for( int i = 0; i < what.length; i++ ) {
				res.put(i, ib.get(w.get(i)));
			}
		}*/
		
		data.buffer = p;
		data.length = what.length;
		
		return 1;
	}
	
	public int bigidx() {		
		ByteBuffer bb = data.getByteBuffer();
		long blen = data.getByteLength()/data.length;
		for( int i = 0; i < data.length; i++ ) {
			BigInteger val = BigInteger.valueOf(i);
			byte[] ba = val.toByteArray();
			bb.position( (int)(i*blen+blen-ba.length) );
			bb.put( ba );
		}
		
		return 0;
	}

	public int shuffleidx() {
		crnt(data);
		idx();
		data = getdata();
		shuffle();

		return 0;
	}

	public int shuffle() {
		shuffle(new simlab.ByValue(data.length));

		return 0;
	}

	Random r = new Random();

	public int shuffle(final simlab.ByValue chunk) {
		int ch = (int) chunk.getLong();

		if (data.type / 8 == 8) {
			LongBuffer lb = data.getByteBuffer().asLongBuffer();
			for (int u = 0; u < data.length; u += ch) {
				for (int i = u; i < u + ch; i++) {
					int k = (int) (r.nextDouble() * data.length);
					long tmp = lb.get(i);
					lb.put(i, lb.get(k));
					lb.put(k, tmp);
				}
			}
		} else if (data.type / 8 == 4) {
			IntBuffer ib = data.getByteBuffer().asIntBuffer();
			for (int u = 0; u < data.length; u += ch) {
				for (int i = u; i < u + ch; i++) {
					int k = (int) (r.nextDouble() * data.length);
					int tmp = ib.get(i);
					ib.put(i, ib.get(k));
					ib.put(k, tmp);
				}
			}
		} else if (data.type / 8 == 2) {
			ShortBuffer sb = data.getByteBuffer().asShortBuffer();
			for (int u = 0; u < data.length; u += ch) {
				for (int i = u; i < u + ch; i++) {
					int k = (int) (r.nextDouble() * data.length);
					short tmp = sb.get(i);
					sb.put(i, sb.get(k));
					sb.put(k, tmp);
				}
			}
		} else if (data.type / 8 == 1) {
			ByteBuffer bb = data.getByteBuffer();
			for (int u = 0; u < data.length; u += ch) {
				for (int i = u; i < u + ch; i++) {
					int k = (int) (r.nextDouble() * data.length);
					byte tmp = bb.get(i);
					bb.put(i, bb.get(k));
					bb.put(k, tmp);
				}
			}
		} else {
			int s = (int) (data.type / 8);
			ByteBuffer bb = data.getByteBuffer();
			int sch = s * ch;
			byte[] bs = new byte[s];
			for (int u = 0; u < data.length; u += sch) {
				for (int i = u; i < u + sch; i++) {
					int k = (int) (r.nextDouble() * data.length);
					bb.get(bs);
					// bb.position();
					// bb.put(i, bb.get(k));
					// bb.put(k, tmp);
				}
			}
		}

		return 1;
	}
	
	public int split( final simlab.ByValue sl ) {
		Buffer	bb = data.getBuffer();
		int size = 0;
		if( bb instanceof DoubleBuffer ) {
			double rv = sl.getRealValue();
			DoubleBuffer	db = (DoubleBuffer)bb;
			
			int len = 0;
			int olen = 0;
			List<simlab.ByValue>	lsl = new ArrayList<Simlab.simlab.ByValue>();
			for( int i = 0; i < db.limit(); i++ ) {
				if( db.get(i) == rv ) {
					simlab.ByValue asl = new simlab.ByValue(len-olen, data.type, data.buffer+olen*8);
					lsl.add( asl );
					olen = len+1;
				}
				len++;
			}
			
			if( len-olen > 0 ) {
				simlab.ByValue asl = new simlab.ByValue(len-olen, data.type, data.buffer+olen*8);
				lsl.add( asl );
			}
			
			data.type = Byte.MIN_VALUE;
			data.length = objects.size();
			data.buffer = 0;
			objects.add( lsl );
		}
		
		return 1;
	}
	
	public int remove( final simlab.ByValue sl ) {
		//String str = sl.getTheString();
		
		return 1;
	}

	public int rand() {
		if (data.type > 0) {
			ByteBuffer bb = data.getByteBuffer();

			if (data.type == 66) {
				DoubleBuffer db = bb.asDoubleBuffer();
				for (int i = 0; i < data.length; i++) {
					db.put(r.nextDouble());
				}
			} else if (data.type == 65) {
				LongBuffer lb = bb.asLongBuffer();
				for (int i = 0; i < data.length; i++) {
					lb.put(r.nextLong());
				}
			} else if (data.type == 34) {
				FloatBuffer fb = bb.asFloatBuffer();
				for (int i = 0; i < data.length; i++) {
					fb.put(r.nextFloat());
				}
			} else if (data.type == 33) {
				IntBuffer ib = bb.asIntBuffer();
				for (int i = 0; i < data.length; i++) {
					ib.put(r.nextInt());
				}
			}
		}

		return 0;
	}

	public int dump() throws IOException {
		dump(datalib.get("one"));

		return 0;
	}

	public int dump(final simlab.ByValue sl) throws IOException {
		int cval = (int) sl.getLong();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		ByteBuffer bb = data.getByteBuffer();
		for (int r = 0; r < data.length / cval; r++) {
			for (int c = 0; c < cval; c++) {
				if (data.type == DOUBLELEN) {
					ps.printf("%e\t", bb.asDoubleBuffer().get(r * cval + c));
				} else if (data.type == LONGLEN || data.type == ULONGLEN) {
					ps.printf("%f\t", bb.asLongBuffer().get(r * cval + c));
				} else if (data.type == FLOATLEN) {
					ps.printf("%f\t", bb.asFloatBuffer().get(r * cval + c));
				} else if (data.type == UINTLEN || data.type == INTLEN) {
					ps.printf("%d\t", bb.asIntBuffer().get(r * cval + c));
				}
			}
			ps.println();
		}
		ps.flush();

		byte[] ba = baos.toByteArray();
		long pval = allocateDirect(ba.length);
		bb = buffers.get(pval);
		for (byte b : ba) {
			bb.put(b);
		}
		data.length = ba.length;
		data.type = 8;
		data.buffer = pval;

		ps.close();
		baos.close();

		return 1;
	}

	public int load() {
		if (data.type == 8 || data.type == 9) {
			String str = data.getTheString();
			String[] spl = str.split("\n");
			String[] nspl = spl[0].split("[\t ]+");

			int size = spl.length * nspl.length;
			long pval = allocateDouble(size);
			data.buffer = pval;
			data.type = DOUBLELEN;
			data.length = size;

			DoubleBuffer dd = buffers.get(pval).asDoubleBuffer();
			for (String sp : spl) {
				nspl = sp.split("[\t ]+");
				for (String nsp : nspl) {
					double d = 0.0;
					try {
						d = Double.parseDouble(nsp);
					} catch (NumberFormatException e) {
					}
					dd.put(d);
				}
			}
		}

		return 0;
	}

	public int table(final simlab.ByValue t) {
		final int c = (int) t.buffer;
		final int r = (int) (data.length / c);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("");
				frame.setSize(800, 600);
				TableComp tc = new TableComp("", data.clone(), c, r);
				frame.add(tc);

				Set<SimComp> compset;
				if (compmap.containsKey(data.buffer)) {
					compset = compmap.get(data.buffer);
				} else {
					compset = new HashSet<SimComp>();
					compmap.put(data.buffer, compset);
				}
				compset.add(tc);

				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setVisible(true);
			}
		});

		return 1;
	}

	public int system(final simlab.ByValue s) throws IOException {
		String command = s.getTheString();
		Process p = Runtime.getRuntime().exec(command);
		InputStream is = p.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		while (line != null) {
			System.out.println(line);

			line = br.readLine();
		}

		return 1;
	}

	public int jinit() {
		datalib.put("null", nulldata);
		datalib.put("bit", new simlab.ByValue(0, 65, 1));
		datalib.put("duo", new simlab.ByValue(0, 65, 2));
		datalib.put("quad", new simlab.ByValue(0, 65, 4));
		datalib.put("ubyte", new simlab.ByValue(0, 65, 8));
		datalib.put("byte", new simlab.ByValue(0, 65, 9));
		datalib.put("ushort", new simlab.ByValue(0, 65, 16));
		datalib.put("short", new simlab.ByValue(0, 65, 17));
		datalib.put("uint", new simlab.ByValue(0, 65, 32));
		datalib.put("int", new simlab.ByValue(0, 65, 33));
		datalib.put("float", new simlab.ByValue(0, 65, 34));
		datalib.put("ulong", new simlab.ByValue(0, 65, 64));
		datalib.put("long", new simlab.ByValue(0, 65, 65));
		datalib.put("double", new simlab.ByValue(0, 65, 66));

		datalib.put("PI", new simlab.ByValue(0, 65, Double.doubleToRawLongBits(Math.PI)));
		datalib.put("e", new simlab.ByValue(0, 65, Double.doubleToRawLongBits(Math.E)));

		datalib.put("true", new simlab.ByValue(0, 1, 1));
		datalib.put("false", new simlab.ByValue(0, 1, 0));

		datalib.put("nil", new simlab.ByValue(0, 65, 0));
		datalib.put("one", new simlab.ByValue(0, 65, 1));
		datalib.put("two", new simlab.ByValue(0, 65, 2));
		datalib.put("ten", new simlab.ByValue(0, 65, 10));
		
		indexer();
		datalib.put("indexer", getdata());

		return 0;
	}

	public int compile(simlab.ByValue fnc) {
		/*
		 * int datasize = (bsize+11)/12; if( data.buffer == 0 ) { data.length =
		 * datasize+1; data.type = 96; data.buffer = (long)new simlab[
		 * data.length ]; } else { int nz = data.length+datasize+1; if( nz >
		 * data.length ) { simlab newsize; newsize.buffer = nz; newsize.type =
		 * 32; newsize.length = 0; resize( newsize ); } } simlab* databuffer =
		 * (simlab*)data.buffer; int ind = data.length-datasize-1; simlab &
		 * subdata = databuffer[ind]; subdata = fnc; if( bsize > 0 ) memcpy(
		 * &databuffer[data.length-datasize], &passnext, bsize );
		 */

		return 0;
	}

	public int interprete(final simlab.ByValue sl) throws SecurityException, NoSuchMethodException {
		// Pointer lbr = sl.getPointer();
		String command = sl.getTheString();

		if (command.startsWith("\"")) {
			String substr = command.substring(1, command.length() - 1);
			byte[] bb = Native.toByteArray(substr);
			tempbuffer.position(0);
			tempbuffer.put(bb);
			// NativeLongByReference nt = new NativeLongByReference();
			long pval = Pointer.nativeValue(Native.getDirectBufferPointer(tempbuffer));
			// nt.setPointer( ptr );
			simlab.ByValue str = new simlab.ByValue(substr.length(), Simlab.BYTELEN, pval);
			echo(str);
		} else {
			List<simlab.ByValue> slist;

			StringTokenizer st = new StringTokenizer(command);
			if (st.hasMoreTokens()) {
				String result = st.nextToken(" (_\n");
				if (data.length == 0 && data.buffer == 0) {
					slist = new ArrayList<simlab.ByValue>();
					data.buffer = 0;
					data.length = objects.size();
					data.type = Byte.MIN_VALUE;
					objects.add(slist);
				} else {
					slist = (List<simlab.ByValue>) objects.get((int) data.length);
				}

				List<simlab.ByValue> olist = parsePar(st);
				Method m = getMethod(result, olist.size());
				if (m != null) {
					simlab.ByValue fnc;
					if (Modifier.isNative(m.getModifiers())) {
						Function f = Function.getFunction("csimlab", m.getName());
						fnc = new simlab.ByValue(objects.size(), Byte.MIN_VALUE, f == null ? 0 : Pointer.nativeValue(f));
					} else
						fnc = new simlab.ByValue(objects.size(), Byte.MIN_VALUE, 0);
					objects.add(m);

					slist.add(fnc);
					slist.addAll(olist);
				} else {
					simlab.ByValue sobj = datalib.get( result );
					if( sobj != null ) {
						simlab.ByValue slb = new simlab.ByValue(objects.size(), Byte.MIN_VALUE, 0);
						objects.add( sobj );
						slist.add( slb );
					}
					else System.err.println( "No such data" );
				}
			}

			// char* result = strtok( command, " (_\n" );
			// int func = dsym( module, result );
			// if( func != 0 /*&& (jobj == 0 || jcls == 0 || func == (long)store
			// || func == (long)fetch || func == (long)Class || func ==
			// (long)Data || func == (long)create)*/ ) {
			// simlab.ByValue fnc;
			// fnc.buffer = func;
			// fnc.type = 32;
			// fnc.length = 0;
			// parseParameters( 0 );
			// compile( fnc, passnext );
			// }
		}
		return 1;
	}

	ByteBuffer tempbuffer = ByteBuffer.allocateDirect(8192);

	// Pointer ptr = Native.getDirectBufferPointer( tempbuffer );
	public int parse2(simlab.ByValue sl) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		// simlab.ByValue psl = new simlab.ByValue( data.);
		// NativeLongByReference nat = new NativeLongByReference();
		// nat.setPointer( ptr );

		// Pointer p = new Pointer(sl.buffer);
		String mname = sl.getTheString(); // p.getString(0);
		Method m = this.getClass().getMethod(mname, simlab.ByValue.class);
		parse(m);

		return 1;
	}

	private void parse(Method m) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		// BufferedReader br = new BufferedReader(reader);
		String line = bufferedreader.readLine();
		while (line != null && !line.equalsIgnoreCase("quit")) {
			line = line.trim();

			byte[] bb = Native.toByteArray(line);
			tempbuffer.position(0);
			long pval = Pointer.nativeValue(Native.getDirectBufferPointer(tempbuffer));
			tempbuffer.put(bb);

			simlab.ByValue psl = new simlab.ByValue(bb.length, BYTELEN, pval);
			// psl.length = bb.length;
			// cmd( psl );
			m.invoke(this, psl);

			// if (debug) {
			// System.err.println("after running " + line + "  " + data.buffer +
			// "  " + data.type + "  " + data.length);
			// }

			line = bufferedreader.readLine();
		}
	}

	private void ok() {
		List<simlab.ByValue> slist = (List<simlab.ByValue>) objects.get((int) data.length);
		System.err.println("starting " + data.length + " " + slist.size());
		int i = 0;
		while (i < slist.size()) {
			simlab.ByValue sl = slist.get(i);
			if (sl.type == Byte.MIN_VALUE) {
				Method mm = (Method) sl.getJavaObject();
			} else if (sl.type == 8 || sl.type == 9) {
				System.err.println(sl.getTheString());
			} else {
				System.err.println(sl.buffer + "  " + sl.type + "  " + sl.length);
			}
			i++;
		}
	}

	private int parse() throws IOException {
		// simlab.ByValue psl = new simlab.ByValue( data.);
		// NativeLongByReference nat = new NativeLongByReference();
		// nat.setPointer( ptr );

		// BufferedReader br = new BufferedReader(reader);

		String line = bufferedreader.readLine();
		while (line != null && !line.equalsIgnoreCase("quit")) {
			line = line.trim();

			byte[] bb = Native.toByteArray(line);
			tempbuffer.position(0);
			long pval = Pointer.nativeValue(Native.getDirectBufferPointer(tempbuffer));
			tempbuffer.put(bb);

			simlab.ByValue psl = new simlab.ByValue(bb.length, BYTELEN, pval);
			// psl.length = bb.length;
			cmd(psl);
			/*
			 * if( reader instanceof InputStreamReader ) {
			 * System.out.println("erm"); System.out.flush(); }
			 */
			line = bufferedreader.readLine();
			// System.err.println(line);
		}

		return 0;
	}

	public int parse(final simlab.ByValue... func) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IOException, IllegalAccessException, InvocationTargetException {
		Reader oldreader = reader;
		BufferedReader oldbufferedreader = bufferedreader;

		if(data.buffer != 0 && (data.type == 8 || data.type == 9)) {
			byte[] bb = data.getByteArray(0, data.getByteLength());
			reader = new InputStreamReader(new ByteArrayInputStream(bb));
			bufferedreader = new BufferedReader(reader);
		}

		/*
		 * data.buffer = 0; data.type = 32; data.length = 0; if (sl.length > 0)
		 * { //Pointer lbr = new Pointer(sl.buffer); String path =
		 * sl.toString(); //lbr.getString(0);
		 * 
		 * //Pointer lbrs = new Pointer(func.buffer); //String funcs =
		 * lbrs.getString(0);
		 * 
		 * InputStream stream = null; if (path != null && !path.equals("this"))
		 * { try { URL url = new URL(path); stream = url.openStream(); } catch
		 * (MalformedURLException e) { e.printStackTrace(); } catch (IOException
		 * e) { e.printStackTrace(); }
		 * 
		 * if (stream == null) { File f = new File(path); if (f.exists()) { try
		 * { stream = new FileInputStream(f); } catch (FileNotFoundException e)
		 * { e.printStackTrace(); } } }
		 * 
		 * if (stream != null) { reader = new InputStreamReader(stream);
		 * bufferedreader = new BufferedReader( reader ); } } }
		 */

		data.buffer = 0;
		data.type = 0;
		data.length = 0;

		Method m;
		if (func.length == 0 || func[0].type == 0) {
			m = Simlab.class.getMethod("cmd", simlab.ByValue.class);
		} else if (func[0].type == Byte.MIN_VALUE) {
			m = (Method) func[0].getJavaObject();
		} else {
			final String name = func[0].getTheString();
			m = Simlab.class.getMethod(name, simlab.ByValue.class);
		}
		parse(m);

		reader = oldreader;
		bufferedreader = oldbufferedreader;

		return 0;
	}

	Simple engine = new Simple();

	interface SimComp {
		public void reload();
	}

	class TableComp extends JComponent implements SimComp {
		JScrollPane scrollpane;
		JTable table;
		String name;
		int r, c;
		simlab.ByValue sl;

		public TableComp() {
			super();
		}

		public TableComp(String name, simlab.ByValue ptr, int w, int h) {
			this.name = name;
			this.sl = ptr;
			this.c = w;
			this.r = h;

			final ByteBuffer bb = sl.getByteBuffer();
			table = new JTable();
			table.setAutoCreateRowSorter(true);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setColumnSelectionAllowed(true);
			table.setModel(new TableModel() {
				@Override
				public int getRowCount() {
					return r;
				}

				@Override
				public int getColumnCount() {
					return c;
				}

				@Override
				public String getColumnName(int columnIndex) {
					return Integer.toString(columnIndex);
				}

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if (sl.type == DOUBLELEN)
						return Double.class;
					else if (sl.type == INTLEN)
						return Integer.class;

					return String.class;
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return true;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					if (sl.type == DOUBLELEN)
						return bb.getDouble((int) ((DOUBLELEN / 8) * (rowIndex * getColumnCount() + columnIndex)));
					else if (sl.type == INTLEN)
						return bb.getInt((int) ((INTLEN / 8) * (rowIndex * getColumnCount() + columnIndex)));
					return null;
				}

				@Override
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
					if (sl.type == DOUBLELEN)
						bb.putDouble((int) ((DOUBLELEN / 8) * (rowIndex * getColumnCount() + columnIndex)), (Double) aValue);
					else if (sl.type == INTLEN)
						bb.putInt((int) ((INTLEN / 8) * (rowIndex * getColumnCount() + columnIndex)), (Integer) aValue);
				}

				@Override
				public void addTableModelListener(TableModelListener l) {
					// TODO Auto-generated method stub

				}

				@Override
				public void removeTableModelListener(TableModelListener l) {
					// TODO Auto-generated method stub

				}

			});
			scrollpane = new JScrollPane(table);

			this.setLayout(new BorderLayout());
			this.add(scrollpane);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
		}

		public void reload() {
			table.tableChanged(new TableModelEvent(table.getModel()));
		}

		public void repaint() {
			super.repaint();
		}
	};

	static Color[] clrs = { new Color(200, 0, 0), new Color(0, 200, 0), new Color(0, 0, 200), new Color(200, 200, 0), new Color(200, 0, 200), new Color(0, 200, 200) };
	class ChartComp extends JComponent implements SimComp {
		String name;
		private simlab.ByValue ptr;
		int w;
		int h;
		int chunk;
		Color[] colors;
		// int fw = 100;
		int f = 50;

		public ChartComp(String name, simlab.ByValue ptr, int c, int w, int h) {
			this.name = name;
			this.ptr = ptr;
			this.w = w;
			this.h = h;
			this.chunk = c;

			colors = new Color[(int) (ptr.length / chunk)];
			for (int i = 0; i < colors.length; i++) {
				colors[i] = clrs[i];
			}

			reload();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		double vmin, vmax;

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Buffer b = ptr.getBuffer();
			int ggw = this.getWidth();
			int ggh = this.getHeight();
			g2.setColor(Color.white);
			g2.fillRect(0, 0, ggw, ggh);
			int gw = ggw - 3 * f;
			int gh = ggh - 2 * f;
			if (b instanceof DoubleBuffer) {
				g2.setColor(Color.black);
				g2.drawLine(2 * f, gh + f, gw + 2 * f, gh + f);
				g2.drawLine(2 * f, f, 2 * f, gh + f);
				DoubleBuffer db = (DoubleBuffer) b;
				for (int d = 0; d < db.limit(); d += chunk) {
					g2.setColor(colors[d / chunk]);
					for (int i = 0; i < chunk - 1; i++) {
						double val = db.get(d + i);
						double val2 = db.get(d + i + 1);
						int x1 = (i * gw) / (chunk - 1);
						int x2 = ((i + 1) * gw) / (chunk - 1);
						int y1 = (int) (gh * (1.0 - (val - vmin) / (vmax - vmin)));
						int y2 = (int) (gh * (1.0 - (val2 - vmin) / (vmax - vmin)));
						g2.drawLine(2 * f + x1, f + y1, 2 * f + x2, f + y2);
					}
				}
			}
			ps.printf("%.3f", (float) vmin);
			String vminstr = baos.toString();
			ps.flush();
			baos.reset();
			ps.printf("%.3f", (float) vmax);
			String vmaxstr = baos.toString();
			ps.flush();
			baos.reset();

			g2.setColor(Color.black);
			g2.drawString(vminstr, 2 * f - g2.getFontMetrics().stringWidth(vminstr), gh + f);
			g2.drawString(vmaxstr, 2 * f - g2.getFontMetrics().stringWidth(vmaxstr), f);

			/*String[] erm = new String[] { "A0201 (3089, 38%)", "A6802 (1434, 28%)", "B1501 (978, 18%)", "B5401 (255, 32%)", "B4402 (119, 37%)" };
			for (int i = 0; i < colors.length; i++) {
				g2.setColor(colors[erm.length - i - 1]);
				g2.fillRoundRect(3 * ggw / 4, 125 + 25 * i, 20, 20, 5, 5);
				g2.setColor(Color.black);
				g2.drawString(erm[i], 3 * ggw / 4 + 25, 145 + 25 * i);
			}*/
			// g2.fillRect(100, 100, 100, 100);
		}

		public void reload() {
			Buffer b = ptr.getBuffer();
			if (b instanceof DoubleBuffer) {
				DoubleBuffer db = (DoubleBuffer) b;
				vmin = Double.POSITIVE_INFINITY;
				vmax = Double.NEGATIVE_INFINITY;
				for (int d = 0; d < db.limit(); d++) {
					double val = db.get(d);
					if (val < vmin)
						vmin = val;
					if (val > vmax)
						vmax = val;
				}
			}
			System.err.println(vmin + "  " + vmax);
			repaint();
		}
	};
	
	class SurfaceComp extends Canvas implements SimComp {
		String name;
		private simlab.ByValue ptr;
		int w;
		int h;
		int chunk;
		Color[] colors;
		// int fw = 100;
		int f = 50;
		SurfaceDraw	sd;
		boolean started = false;

		public SurfaceComp(String name, simlab.ByValue ptr, int c, int w, int h) {
			this.name = name;
			this.ptr = ptr;
			this.w = w;
			this.h = h;
			this.chunk = c;
			
			sd = new SurfaceDraw( c );

			reload();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		double vmin, vmax;

		public void paint(Graphics g) {
			/*System.err.println( this.getWidth() + " " + this.getHeight() );
			if( !started && isShowing() ) {
				try {
					Display.setParent(this);
					Display.create();
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
				started = true;
			}
			
			super.paint( g );
			if( started ) sd.draw();*/
		}

		public void reload() {
			/*if( !started && this.isShowing() ) {
				try {
					Display.setParent(this);
					Display.create();
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
				started = true;
			}*/
			
			Buffer b = ptr.getBuffer();
			if (b instanceof DoubleBuffer) {
				DoubleBuffer db = (DoubleBuffer) b;
				vmin = Double.POSITIVE_INFINITY;
				vmax = Double.NEGATIVE_INFINITY;
				for (int d = 0; d < db.limit(); d++) {
					double val = db.get(d);
					if (val < vmin)
						vmin = val;
					if (val > vmax)
						vmax = val;
				}
			}
			//repaint();
			
			if( started ) sd.draw();
		}
	};

	class ImageComp extends JComponent implements SimComp {
		String name;
		BufferedImage bi;
		private simlab.ByValue ptr;
		int w;
		int h;

		public ImageComp(String name, BufferedImage bi, simlab.ByValue ptr, int w, int h) {
			this.name = name;
			this.bi = bi;
			this.ptr = ptr;
			this.w = w;
			this.h = h;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.drawImage(bi, 0, 0, this.getWidth(), this.getHeight(), this);
		}

		public void reload() {
			int t = w * h;
			// int[] ib = ptr.getIntArray(0, t);
			// System.err.println("ptr" + ptr.length + "  " + ptr.type);
			IntBuffer ib = ptr.getByteBuffer().asIntBuffer();
			if (ib.limit() < t) {
				System.err.println("imgsize error " + ib.limit() + "  " + ptr.getByteLength());
			} else {
				for (int i = 0; i < t; i++) {
					// if( i%13 == 0 ) System.err.println( ib[i] );
					bi.setRGB((int) (i % w), (int) (i / w), ib.get(i));
				}
				repaint();
			}
		}

		public void repaint() {
			super.repaint();
		}
	};

	Map<Long, Set<SimComp>> compmap = new HashMap<Long, Set<SimComp>>();

	public class Simple implements ScriptEngine {
		@Override
		public Bindings createBindings() {
			return null;
		}

		@Override
		public Object eval(String script) throws ScriptException {
			return Simlab.this.command(script);
		}

		@Override
		public Object eval(Reader reader) throws ScriptException {
			Simlab.this.reader = reader;
			Simlab.this.bufferedreader = new BufferedReader(reader);
			try {
				Simlab.this.parse();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);

			return null;
		}

		@Override
		public Object eval(String script, ScriptContext context) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(Reader reader, ScriptContext context) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(String script, Bindings n) throws ScriptException {
			return null;
		}

		@Override
		public Object eval(Reader reader, Bindings n) throws ScriptException {
			return null;
		}

		@Override
		public Object get(String key) {
			return null;
		}

		@Override
		public Bindings getBindings(int scope) {
			return null;
		}

		@Override
		public ScriptContext getContext() {
			return null;
		}

		@Override
		public ScriptEngineFactory getFactory() {
			return Simlab.this;
		}

		@Override
		public void put(String key, Object value) {
		}

		@Override
		public void setBindings(Bindings bindings, int scope) {
		}

		@Override
		public void setContext(ScriptContext context) {
		}
	};

	public static void test() {
		Map<Point, String> map = new HashMap<Point, String>();
		Point p = new Point(0, 0);

		map.put(p, "simmi1");

		p.x = 1;

		map.put(p, "simmi2");

		for (Point pp : map.keySet()) {
			System.err.println(pp.hashCode());
			System.err.println(pp.x + "  " + pp.y);
			System.err.println(map.get(pp));
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		/*
		 * ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		 * List<ScriptEngineFactory> scriptEngineFactories =
		 * scriptEngineManager.getEngineFactories(); for( ScriptEngineFactory
		 * scriptEngineFactory : scriptEngineFactories ) { System.err.println(
		 * scriptEngineFactory.getEngineName() ); }
		 */

		Console console = System.console();
		Simlab simlab = new Simlab();
		simlab.init();
		if( console != null ) simlab.welcome();
		simlab.jinit();
		ScriptEngine engine = simlab.getScriptEngine();

		boolean gui = false;
		for (String arg : args) {
			if (arg.equals("--gui")) {
				gui = true;
				break;
			}
		}

		try {
			if (gui) {
				PipedWriter pw = new PipedWriter();
				// BufferedWriter bw = new BufferedWriter( pw );
				// SimConsole simconsole =
				
				JFrame frame = new JFrame();
				frame.setSize(400, 300);
				frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
				frame.add( new SimConsole(pw) );
				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// InputStreamReader ir = new InputStreamReader(in);

				PipedReader pr = new PipedReader(pw);
				// BufferedReader br = new BufferedReader( pr );
				engine.eval(pr);
				
				frame.setVisible( true );
			} else {
				if (args.length > 0) {
					engine.eval(args[0]);
				} else if (console == null) {
					engine.eval(new InputStreamReader(System.in));
				} else {
					engine.eval(console.reader());
				}
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getEngineName() {
		return "Simlab";
	}

	@Override
	public String getEngineVersion() {
		return "1.0";
	}

	@Override
	public List<String> getExtensions() {
		return null;
	}

	@Override
	public String getLanguageName() {
		return "Simple";
	}

	@Override
	public String getLanguageVersion() {
		return "1.0";
	}

	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		return null;
	}

	@Override
	public List<String> getMimeTypes() {
		return null;
	}

	@Override
	public List<String> getNames() {
		return null;
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		return null;
	}

	@Override
	public Object getParameter(String key) {
		return null;
	}

	@Override
	public String getProgram(String... statements) {
		return null;
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return engine;
	}
}