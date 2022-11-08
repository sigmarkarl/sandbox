package org.simmi.serifier;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import java.io.*;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class FtpFetch {
    public static void ftpFetch(SerifyApplet sa, Map<String,String> searchmap, Path cd, boolean wholeSelected, boolean phageSelected, boolean plasmidSelected, boolean interrupted) throws IOException {
        String ftpsite = "ftp.ncbi.nlm.nih.gov";
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");
        ftp.connect( ftpsite );

        ftp.enterLocalPassiveMode();
        ftp.login("anonymous", "sigmarkarl@hotmail.com");

        for( String search : searchmap.keySet() ) {
            String replace = searchmap.get(search);
            replace = replace == null || replace.length() == 0 ? null : replace;
            if( search.length() == 4 ) {
                String subdir = "/genbank/wgs/";
                ftp.cwd( subdir );
                String fwname = search+".gbk";

                String filename = "wgs."+search+".1.gbff.gz";
									/*File gfile = new File(basesave, fwname+".gz");
									FileOutputStream fos = new FileOutputStream( gfile );
									ftp.retrieveFile("wgs."+search+".1.fsa_nt.gz",fos);
									fos.close();*/

                Path thefile = cd.resolve( fwname ); //new File( basesave, fwname );
                if( !Files.exists( thefile ) ) {
                    //FileWriter fw = new FileWriter( thefile );
                    Writer fw = Files.newBufferedWriter( thefile, StandardOpenOption.CREATE );
                    URL url = new URL( "ftp://"+ftpsite+subdir+filename );
                    InputStream is = new GZIPInputStream( url.openStream() );//ftp.retrieveFileStream( newfname );
                    BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
                    String line = br.readLine();
                    while( line != null ) {
                        fw.write( line + "\n" );
                        line = br.readLine();
                    }
                    br.close();
                    is.close();
                    //ftp.completePendingCommand();
                    fw.close();
                }

                try {
                    Map<String,Path>	urimap = new HashMap<>();
                    urimap.put( fwname.substring(0, fwname.length()-4), thefile );
                    sa.addSequencesPath( fwname, urimap, cd, replace );
                } catch (URISyntaxException e12) {
                    e12.printStackTrace();
                }
                //FTPFile[] files = ftp.listFiles();
            } else {
                if( wholeSelected ) {
                    String subdir = "/genomes/refseq/bacteria/";
                    ftp.changeWorkingDirectory(subdir);//cwd( subdir );
                    FTPFile[] files = ftp.listFiles();
                    for( FTPFile ftpfile : files ) {
                        if( interrupted ) {
                            break;
                        }
                        if( ftpfile.isDirectory() ) {
                            String fname = ftpfile.getName();
                            if( fname.startsWith( search ) ) {
                                if( !ftp.isConnected() ) {
                                    ftp.connect("ftp.ncbi.nih.gov");
                                    ftp.login("anonymous", "sigmarkarl@gmail.com");
                                }
                                String assembly = subdir+fname;
                                ftp.cwd( assembly );
                                FTPFile[] ass = ftp.listFiles();
                                String lat = "";

                                for( FTPFile fass : ass ) {
                                    if( fass.getName().equals( "latest_assembly_versions" ) ) {
                                        lat = "latest_assembly_versions";
                                        break;
                                    } else if( fass.getName().equals( "all_assembly_versions" ) ) {
                                        lat = "all_assembly_versions/suppressed";
                                    }
                                }
                                assembly += "/"+lat;
                                ftp.cwd(assembly);

                                FTPFile[] subfolders = ftp.listFiles();
                                for( FTPFile subfolder : subfolders ) {
                                    String subfoldername = subfolder.getName();
                                    ftp.cwd( "/"+assembly+"/"+subfoldername );
                                    FTPFile[] newfiles = ftp.listFiles();
                                    //File thefile = new File( basesave, fname+".gbk" );
                                    for (FTPFile newftpfile : newfiles) {
                                        if (interrupted) {
                                            break;
                                        }

                                        String newfname = newftpfile.getName();
                                        if (newfname.endsWith(".gbff.gz")) {
                                            //if( newftpfile != newfiles[0] ) fw.write("//\n");
                                            //long size = newftpfile.getSize();
                                            //String basename = fname;
                                            //if( size > 3000000 ) basename = fname;//+".gbk";
                                            //else basename = fname+"_p"+(cnt++);//+".gbk";

                                            //String fwname = basename+"_"+newfname;
                                            //if( size > 1500000 ) fwname = fname+".fna";
                                            //else fwname = fname+"_p"+(cnt++)+".fna";

                                            String urlstr = "ftp://" + ftpsite + assembly + "/" + subfoldername + "/" + newfname;
                                            String spec = fname;
                                            System.err.println("trying to open " + urlstr);
                                            URL url = new URL(urlstr);
                                            InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
                                            GZIPInputStream gis = new GZIPInputStream(is);
                                            BufferedReader br = new BufferedReader(new InputStreamReader(gis));
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            String line = br.readLine();
                                            while (line != null) {
                                                if (line.startsWith("SOURCE")) {
                                                    spec = line.substring(7).trim().replace(' ', '_');
                                                    System.err.println("set spec " + spec);
                                                } else if(line.contains("/strain=")) {
                                                    int si = line.indexOf("/strain=");
                                                    String strain = line.substring(si+9,line.length()-1);
                                                    if(!spec.contains(strain)) spec += "_"+strain;
                                                }
                                                baos.write((line + "\n").getBytes());
                                                line = br.readLine();
                                            }
                                            is.close();

                                            Path thefile = cd.resolve(spec + ".gbff");
                                            if (!Files.exists(thefile)) {
                                                Files.write(thefile, baos.toByteArray(), StandardOpenOption.CREATE);
                                            }

                                            try {
                                                Map<String, Path> urimap = new HashMap<>();
                                                urimap.put(spec, thefile);
                                                sa.addSequencesPath(spec, urimap, thefile, replace);
                                            } catch (URISyntaxException e12) {
                                                e12.printStackTrace();
                                            }
                                            //ftp.completePendingCommand();
                                            //System.err.println("done " + fname);
                                        } else if (newfname.endsWith(".gff.gz")) {
                                            String urlstr = "ftp://" + ftpsite + assembly + "/" + subfoldername + "/" + newfname;
                                            String spec = fname;
                                            System.err.println("trying to open " + urlstr);
                                            URL url = new URL(urlstr);
                                            InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
                                            GZIPInputStream gis = new GZIPInputStream(is);
                                            BufferedReader br = new BufferedReader(new InputStreamReader(gis));
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            String line = br.readLine();
                                            while (line != null) {
                                                baos.write((line + "\n").getBytes());
                                                line = br.readLine();
                                            }
                                            is.close();

                                            Files.createDirectories(cd.resolve("pantools"));
                                            Path thefile = cd.resolve("pantools/"+spec + ".gff");
                                            if (!Files.exists(thefile)) {
                                                Files.write(thefile, baos.toByteArray(), StandardOpenOption.CREATE);
                                            }
                                        } else if (newfname.endsWith(".fna.gz")) {
                                            String urlstr = "ftp://" + ftpsite + assembly + "/" + subfoldername + "/" + newfname;
                                            String spec = fname;
                                            System.err.println("trying to open " + urlstr);
                                            URL url = new URL(urlstr);
                                            InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
                                            GZIPInputStream gis = new GZIPInputStream(is);
                                            BufferedReader br = new BufferedReader(new InputStreamReader(gis));
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            String line = br.readLine();
                                            while (line != null) {
                                                baos.write((line + "\n").getBytes());
                                                line = br.readLine();
                                            }
                                            is.close();

                                            Files.createDirectories(cd.resolve("pantools"));
                                            Path thefile = cd.resolve("pantools/"+spec + ".fna");
                                            if (!Files.exists(thefile)) {
                                                Files.write(thefile, baos.toByteArray(), StandardOpenOption.CREATE);
                                            }
                                        }
                                    }
                                    //ftp.cwd( ".." );
                                }
                            }
                        }
                    }
                }

                                    /*if( draft.isSelected() ) {
                                        String subdir = "/genomes/Bacteria_DRAFT/";
                                        byte[] bb = new byte[30000000];
                                        ftp.cwd( subdir );
                                        FTPFile[] files2 = ftp.listFiles();
                                        for( FTPFile ftpfile : files2 ) {
                                            if( interrupted ) break;
                                            if( ftpfile.isDirectory() ) {
                                                String fname = ftpfile.getName();
                                                if( fname.startsWith( search ) ) {
                                                    ftp.cwd( subdir+fname );
                                                    FTPFile[] newfiles = ftp.listFiles();

                                                    Path thefile = cd.resolve(fname+".gbk");
                                                    if( !Files.exists(thefile) ) {
                                                        final OutputStream fos = Files.newOutputStream( thefile, StandardOpenOption.CREATE );

                                                        for( FTPFile newftpfile : newfiles ) {
                                                            if( interrupted ) break;

                                                            //String newfname = newftpfile.getName().getBaseName();
                                                            String newfname = newftpfile.getName();
                                                            if( newfname.endsWith("scaffold.gbk.tgz") ) {
                                                                //if( newftpfile != newfiles[0] ) fos.write( "//\n".getBytes() );
                                                                //long size = newftpfile.getSize();
                                                                //String fwname = "";
                                                                //if( size > 1500000 ) fwname = fname+".fna";
                                                                //else fwname = fname+"_p"+(cnt++)+".fna";

                                                                //InputStream is = newftpfile.getContent().getInputStream();
                                                                URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
                                                                InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
                                                                System.err.println( "trying "+newfname + (is == null ? "critical" : "success" ) );
                                                                //InputStream gis = is;
                                                                GZIPInputStream gis = new GZIPInputStream( is );

                                                                //File file = new File( basesave, fname.substring(0,fname.length()-4)+".tar" );
                                                                Path file = //uhome.resolve("temp.tar");
                                                                Files.createTempFile("erm", ".tar");
                                                                //cd.resolve(fname.substring(0,fname.length()-4)+".tar");


                                                                if( gis != null ) {
                                                                    int r = gis.read(bb);
                                                                    OutputStream tfos = Files.exists(file) ? Files.newOutputStream( file, StandardOpenOption.TRUNCATE_EXISTING ) : Files.newOutputStream( file, StandardOpenOption.CREATE );
                                                                    while( r > 0 ) {
                                                                        System.err.println( "reading " + r );
                                                                        tfos.write( bb, 0, r );

                                                                        r = gis.read(bb);
                                                                    }
                                                                    gis.close();
                                                                    tfos.close();
                                                                }


                                                                //FileSystemManager fsManager = VFS.getManager();
                                                                //String pp = "tar:"+file.toString();//.replace("file://", "tar://");

                                                                //Map<String,String> env = new HashMap<String,String>();
                                                                //env.put("create", "true");
                                                                String uristr = "tar:" + file.toUri();
                                                                //URI taruri = URI.create( uristr *.replace("file://", "file:")* );
                                                                //FileSystem tarfilesystem = FileSystems.newFileSystem( taruri, env );
                                                                FileSystemManager fsManager = VFS.getManager();
                                                                FileObject jarFile = fsManager.resolveFile(uristr);
                                                                //jarFile.

                                                                // List the children of the Jar file
                                                                FileObject[] children = jarFile.getChildren();
                                                                //System.out.println( "Children of " + jarFile.getName().getURI() );
                                                                //int contig = 1;

																/*for( Path root : tarfilesystem.getRootDirectories() ) {
																Files.list(root).filter( new Predicate<Path>() {
																@Override
																public boolean test(Path t) {
																//String fname = t.getFileName().toString();
																return true;
																}
																}).forEach( new Consumer<Path>() {
																@Override
																public void accept(Path t) {
																try {
																Files.copy(t, fos);
																//sa.addSequences(t.getFileName().toString(), t, null);
																} catch (IOException e) {
																e.printStackTrace();
																}
																}
																});;
																}*

                                                                for ( int i = 0; i < children.length; i++ ) {
                                                                    FileObject child = children[i];

                                                                    //if( i > 0 ) fos.write( "//\n".getBytes() );
                                                                    //String childname = child.getName().getBaseName();
                                                                    //int k = childname.indexOf(".gbk");
                                                                    //if( k == -1 ) k = childname.length();
                                                                    //String lfname = fname+"_contig"+(contig++)+"_"+childname;
                                                                    FileContent fc = child.getContent();
                                                                    InputStream sis = fc.getInputStream();
                                                                    int r = sis.read( bb );
                                                                    //int total = r;

                                                                    while( r != -1 ) {
                                                                        fos.write( bb, 0, r );
                                                                        r = sis.read( bb );
                                                                        //total += r;
                                                                    }
                                                                    sis.close();
                                                                }

                                                                Files.delete( file );

                                                                //fsManager.getFilesCache().clear( jarFile.getFileSystem() );
                                                            }
                                                        }
                                                        fos.close();
                                                    }

                                                    try {
                                                        Map<String,Path>	urimap = new HashMap<>();
                                                        urimap.put( fname, thefile );
                                                        addSequencesPath( fname, urimap, thefile, replace );
                                                    } catch (URISyntaxException e12) {
                                                        e12.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }*/

                if( plasmidSelected ) {
                    String subdir = "/genomes/Plasmids/gbk/";
                    ftp.cwd( subdir );
                    FTPFile[] files = ftp.listFiles();
                    for( FTPFile ftpfile : files ) {
                        if( interrupted ) {
                            break;
                        }
                        //System.err.println("here");
                        //if( ftpfile.isDirectory() ) {
                        String fname = ftpfile.getName();
                        if( fname.contains( search ) ) {
                            if( !ftp.isConnected() ) {
                                ftp.connect("ftp.ncbi.nih.gov");
                                ftp.login("anonymous", "");
                            }
                            //ftp.cwd( subdir+fname );
                            //FTPFile[] newfiles = ftp.listFiles();
                            //int cnt = 1;

                            //File thefile = new File( basesave, fname+".gbk" );
                            Path thefile = cd.resolve(fname+".gbk");
                            if( !Files.exists(thefile) ) {
                                Writer fw = Files.newBufferedWriter( thefile, StandardOpenOption.CREATE );

                                if( fname.endsWith(".gbk") ) {
                                    URL url = new URL( "ftp://"+ftpsite+subdir+"/"+fname );
                                    InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
                                    BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
                                    String line = br.readLine();
                                    while( line != null ) {
                                        fw.write( line + "\n" );
                                        line = br.readLine();
                                    }
                                    is.close();
                                }
                                fw.close();
                            }

                            try {
                                Map<String,Path>	urimap = new HashMap<>();
                                urimap.put( fname, thefile );
                                sa.addSequencesPath( fname, urimap, thefile, replace);
                            } catch (URISyntaxException e12) {
                                e12.printStackTrace();
                            }
                        }
                    }
                }

                if( phageSelected ) {
                    String subdir = "/genomes/refseq/viral/";
                    //fetchViral(ftp, ftpsite, subdir, search, replace, cd);
                    subdir = "/genomes/genbank/viral/";
                    fetchViral(sa, ftp, ftpsite, subdir, search, replace, cd);
                }
            }
        }
    }

    static void fetchViral(SerifyApplet sa, FTPClient ftp, String ftpsite, String subdir, String search, String replace, Path cd) throws IOException {
        ftp.cwd( subdir );
        FTPFile[] files3 = ftp.listFiles();
        for( FTPFile ftpfile : files3 ) {
            //if( interrupted ) break;
            if( ftpfile.isDirectory() ) {
                String fname = ftpfile.getName();
                if( fname.startsWith( search ) ) {//fname.startsWith("Thermus") || fname.startsWith("Meiothermus") || fname.startsWith("Marinithermus") || fname.startsWith("Oceanithermus") ) {
                    if( !ftp.isConnected() ) {
                        ftp.connect("ftp.ncbi.nih.gov");
                        ftp.login("anonymous", "anonymous");
                    }
                    String assembly = subdir+fname;
                    ftp.cwd( assembly );
                    FTPFile[] ass = ftp.listFiles();
                    String lat = "";

                    for( FTPFile fass : ass ) {
                        if( fass.getName().equals( "latest_assembly_versions" ) ) {
                            lat = "latest_assembly_versions";
                            break;
                        } else if( fass.getName().equals( "all_assembly_versions" ) ) {
                            lat = "all_assembly_versions"; //suppressed";
                        }
                    }
                    assembly += "/"+lat;
                    ftp.cwd(assembly);

                    FTPFile[] subfolders = ftp.listFiles();
                    for( FTPFile subfolder : subfolders ) {
                        String subfoldername = subfolder.getName();
                        ftp.cwd("/" + assembly + "/" + subfoldername);
                        FTPFile[] newfiles = ftp.listFiles();
                        //File thefile = new File( basesave, fname+".gbk" );
                        for (FTPFile newftpfile : newfiles) {
															/*ftp.cwd( subdir+fname );
															FTPFile[] newfiles = ftp.listFiles();
															int cnt = 1;*/
                            //if (interrupted) break;

                            String newfname = newftpfile.getName();
                            if (newfname.endsWith(".gbff.gz")) {
                                String spec = fname;
                                String acc = null;
                                String version = null;
                                //URL url = new URL("ftp://" + ftpsite + subdir + fname + "/" + newfname);
                                String urlstr = "ftp://" + ftpsite + assembly + "/" + subfoldername + "/" + newfname;
                                InputStream is = new URL(urlstr).openStream();//ftp.retrieveFileStream( newfname );
                                GZIPInputStream gis = new GZIPInputStream(is);
                                BufferedReader br = new BufferedReader(new InputStreamReader(gis));
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                String line = br.readLine();
                                while (line != null) {
                                    if( line.startsWith("ACCESSION") ) {
                                        acc = line.substring(10).trim().replace(' ','_');
                                    } else if( line.startsWith("VERSION") ) {
                                        version = line.substring(8).trim().replace(' ','_');
                                    } else if( line.startsWith("SOURCE") ) {
                                        spec = line.substring(7).trim().replace(' ','_');
                                        System.err.println("set spec " + spec);
                                    }
                                    baos.write((line + "\n").getBytes());
                                    line = br.readLine();
                                }
                                is.close();

                                Path thefile = cd.resolve(spec + ".gbff");
                                if (!Files.exists(thefile)) {
                                    Files.write(thefile, baos.toByteArray(), StandardOpenOption.CREATE);
                                } else if(acc!=null) {
                                    Path theacc = cd.resolve(spec+"_"+acc + ".gbff");
                                    if (!Files.exists(theacc)) {
                                        Files.write(theacc, baos.toByteArray(), StandardOpenOption.CREATE);
                                    } else if(version!=null) {
                                        Path theversion = cd.resolve(spec+"_"+version+".gbff");
                                        if (!Files.exists(theversion)) {
                                            Files.write(theversion, baos.toByteArray(), StandardOpenOption.CREATE);
                                        }
                                    }
                                }

                                try {
                                    Map<String, Path> urimap = new HashMap<>();
                                    urimap.put(spec, thefile);
                                    sa.addSequencesPath(spec, urimap, thefile, replace);
                                } catch (URISyntaxException e12) {
                                    e12.printStackTrace();
                                }
                            }

														/*for( FTPFile newftpfile : newfiles ) {
														if( interrupted ) break;
														String newfname = newftpfile.getName();
														System.err.println("trying " + newfname + " in " + fname);
														if( newfname.endsWith(".gbk") ) {
														System.err.println("in " + fname);
														long size = newftpfile.getSize();
														String fwname = fname+"_"+newfname;

														File thefile = new File( basesave, fwname );
														if( !thefile.exists() ) {
														FileWriter fw = new FileWriter( thefile );
														URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
														InputStream is = url.openStream(); //ftp.retrieveFileStream( newfname );
														BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
														String line = br.readLine();
														while( line != null ) {
														fw.write( line + "\n" );
														line = br.readLine();
														}
														is.close();
														fw.close();
														System.err.println("done " + fname);
														}
														urimap.put( fwname.substring(0, fwname.length()-4), thefile.toURI().toString() );

														/*try {
														addSequences( fwname, thefile.toURI().toString() );
														} catch (URISyntaxException e) {
														e.printStackTrace();
														}*
														}
														}*/
														/*try {
														addSequencesPath( fname, urimap, uripath );
														} catch (URISyntaxException e) {
														e.printStackTrace();
														}*/

                        }
                    }
                }
            }
        }
    }

    public void ftpExtract( SerifyApplet sa, String basesave, String search ) {
        //String basesave = "/home/sigmar/ftpncbi/";

        String replace = null;
        String ftpsite = "ftp.ncbi.nih.gov";
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect( ftpsite );
            ftp.login("anonymous", "anonymous");

            String subdir = "/genomes/Bacteria/";
            ftp.cwd( subdir );
            FTPFile[] files = ftp.listFiles();
            for( FTPFile ftpfile : files ) {
                if( ftpfile.isDirectory() ) {
                    String fname = ftpfile.getName();
                    if( fname.startsWith( search ) ) {
                        if( !ftp.isConnected() ) {
                            ftp.connect("ftp.ncbi.nih.gov");
                            ftp.login("anonymous", "anonymous");
                        }
                        ftp.cwd( subdir+fname );
                        FTPFile[] newfiles = ftp.listFiles();
                        int cnt = 1;
                        for( FTPFile newftpfile : newfiles ) {
                            String newfname = newftpfile.getName();
                            if( newfname.endsWith(".gbk") ) {
                                long size = newftpfile.getSize();
                                String fwname;
                                if( size > 3000000 ) fwname = fname+"_"+newfname;//+".gbk";
                                else fwname = fname+"_p"+(cnt++)+"_"+newfname;//+".gbk";
                                //if( size > 1500000 ) fwname = fname+".fna";
                                //else fwname = fname+"_p"+(cnt++)+".fna";

                                FileWriter fw = new FileWriter( basesave+fwname );
                                URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
                                InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
                                BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
                                String line = br.readLine();
                                while( line != null ) {
                                    fw.write( line + "\n" );
                                    line = br.readLine();
                                }
                                is.close();
                                //ftp.completePendingCommand();
                                fw.close();
                                System.err.println("done " + fname);

                                try {
                                    sa.addSequences( fwname, Paths.get( basesave, fwname ), replace );
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

            subdir = "/genomes/Bacteria_DRAFT/";
            FileSystemManager fsManager = VFS.getManager();
            //FileObject ftpconn = fsManager.resolveFile( "ftp://anonymous:anonymous@"+ftpsite+ftppath );
            //FileObject[] files2 = ftpconn.getChildren();

            //for( FileObject ftpfile : files2 ) {
            //	String fname = ftpfile.getName().getBaseName();
            byte[] bb = new byte[30000000];
            ftp.cwd( subdir );
            FTPFile[] files2 = ftp.listFiles();
            for( FTPFile ftpfile : files2 ) {
                if( ftpfile.isDirectory() ) {
                    String fname = ftpfile.getName();
                    if( fname.startsWith( search ) ) {
                        //System.err.println( "erm "+fname );
                        ftp.cwd( subdir+fname );
                        FTPFile[] newfiles = ftp.listFiles();
                        //FileObject[] newfiles = ftpfile.getChildren();
                        //amint cnt = 1;
                        //for( FileObject newftpfile : newfiles ) {
                        for( FTPFile newftpfile : newfiles ) {
                            //String newfname = newftpfile.getName().getBaseName();
                            String newfname = newftpfile.getName();
                            if( newfname.endsWith("scaffold.gbk.tgz") ) {
                                //long size = newftpfile.getSize();
                                //String fwname = "";
                                //if( size > 1500000 ) fwname = fname+".fna";
                                //else fwname = fname+"_p"+(cnt++)+".fna";

                                //InputStream is = newftpfile.getContent().getInputStream();
                                URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
                                InputStream is = url.openStream();//ftp.retrieveFileStream( newfname );
                                System.err.println( "trying "+newfname + (is == null ? "critical" : "success" ) );
                                //InputStream gis = is;
                                GZIPInputStream gis = new GZIPInputStream( is );

                                String filename = basesave+fname.substring(0,fname.length()-4)+".tar";
                                if( gis != null ) {
                                    int r = gis.read(bb);
                                    FileOutputStream fos = new FileOutputStream( filename );
                                    while( r > 0 ) {
                                        System.err.println( "reading " + r );
                                        fos.write( bb, 0, r );

                                        r = gis.read(bb);
                                    }
                                    gis.close();
                                    fos.close();
                                }

                                //FileSystemManager fsManager = VFS.getManager();
                                FileObject jarFile = fsManager.resolveFile( "tar://"+filename );

                                // List the children of the Jar file
                                FileObject[] children = jarFile.getChildren();
                                //System.out.println( "Children of " + jarFile.getName().getURI() );
                                int contig = 1;
                                for ( int i = 0; i < children.length; i++ ) {
                                    FileObject child = children[i];
                                    FileOutputStream fos = new FileOutputStream( basesave+fname.substring(0,fname.length())+"_contig"+(contig++)+"_"+child.getName().getBaseName() );
                                    FileContent fc = child.getContent();
                                    InputStream sis = fc.getInputStream();
                                    int r = sis.read( bb );
                                    int total = r;

                                    while( r != -1 ) {
                                        fos.write( bb, 0, r );
                                        r = sis.read( bb );
                                        total += r;
                                    }
                                    fos.close();

                                    try {
                                        sa.addSequences( fname, Paths.get(basesave,fname), replace );
                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                }

                                //FileInputStream	fis = new FileInputStream( filename );

								/*TarInputStream tais = new TarInputStream( fis );
								TarEntry te = tais.getNextEntry();
								int contig = 1;
								// (int)te.getSize() ];

								while( te != null ) {
									//size = te.getSize();
									//if( size > 0 ) {
										byte[] bb = new byte[ 2048 ];

										FileOutputStream fos = new FileOutputStream( "/home/sigmar/ftpncbi/"+fname.substring(0,fname.length())+"_contig"+(contig++)+".gbk" );
										int r = tais.read( bb );
										int total = r;
										//fis.write( bb, 0, r );
										//System.err.println( te.getName() + "  " + total + "  " + size );

										while( r != -1 ) {
											fos.write( bb, 0, r );
											//System.err.println( te.getName() + "  " + total + "  " + size );
											r = tais.read( bb );
											total += r;
										}
										//IOUtils.copy(tis, fis, (int)ae.getSize());
										fos.close();
									//}
									te = tais.getNextEntry();
								}
								tais.close();
								fis.close();*/
                                //ftp.completePendingCommand();
                            }
                        }
                    }
                }
            }

            subdir = "/genomes/Viruses/";
            ftp.cwd( subdir );
            FTPFile[] files3 = ftp.listFiles();
            for( FTPFile ftpfile : files3 ) {
                if( ftpfile.isDirectory() ) {
                    String fname = ftpfile.getName();
                    if( fname.startsWith( search ) ) {//fname.startsWith("Thermus") || fname.startsWith("Meiothermus") || fname.startsWith("Marinithermus") || fname.startsWith("Oceanithermus") ) {
                        if( !ftp.isConnected() ) {
                            ftp.connect("ftp.ncbi.nih.gov");
                            ftp.login("anonymous", "anonymous");
                        }
                        ftp.cwd( subdir+fname );
                        FTPFile[] newfiles = ftp.listFiles();
                        int cnt = 1;
                        for( FTPFile newftpfile : newfiles ) {
                            String newfname = newftpfile.getName();
                            System.err.println("trying " + newfname + " in " + fname);
                            if( newfname.endsWith(".gbk") ) {
                                System.err.println("in " + fname);
                                long size = newftpfile.getSize();
                                String fwname = fname+"_"+newfname;
                                //if( size > 3000000 ) fwname = fname+".gbk";
                                //else fwname = fname+"_p"+(cnt++)+".gbk";
                                //if( size > 1500000 ) fwname = fname+".fna";
                                //else fwname = fname+"_p"+(cnt++)+".fna";

                                FileWriter fw = new FileWriter( basesave+fwname );
                                URL url = new URL( "ftp://"+ftpsite+subdir+fname+"/"+newfname );
                                InputStream is = url.openStream(); //ftp.retrieveFileStream( newfname );
                                BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
                                String line = br.readLine();
                                while( line != null ) {
                                    fw.write( line + "\n" );
                                    line = br.readLine();
                                }
                                is.close();
                                //ftp.completePendingCommand();
                                fw.close();
                                System.err.println("done " + fname);

                                try {
                                    sa.addSequences( fwname, Paths.get(basesave,fwname), replace );
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
