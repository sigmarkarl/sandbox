package org.simmi.serifier;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class HttpFetch {
    static void wholeFetch(HttpClient httpClient, URI baseuri) throws IOException, InterruptedException {
        String subdir = "/genomes/refseq/bacteria/";
        URI uri = baseuri.resolve(subdir);
        HttpRequest httpRequest = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        System.err.println("hey " + body);
					/*for( FTPFile ftpfile : files ) {
						if( interrupted ) {
							break;
						}
						if( ftpfile.isDirectory() ) {
							String fname = ftpfile.getName();
							if( fname.startsWith( search ) ) {
								if( !ftp.isConnected() ) {
									ftp.connect("ftp.ncbi.nih.gov");
									ftp.login("anonymous", "");
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
													spec += "_"+strain;
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
												addSequencesPath(spec, urimap, thefile, replace);
											} catch (URISyntaxException e12) {
												e12.printStackTrace();
											}
										}
									}
								}
							}
						}
					}*/
    }

    public static void httpFetch(SerifyApplet sa, Map<String,String> searchmap, Path cd, boolean wholeSelected, boolean phageSelected, boolean plasmidSelected, boolean interrupted) throws IOException, InterruptedException {
        String ftpsite = "ftp.ncbi.nlm.nih.gov";
        HttpClient httpClient = HttpClient.newHttpClient();

        for( String search : searchmap.keySet() ) {
            String replace = searchmap.get(search);
            replace = replace == null || replace.length() == 0 ? null : replace;
            if( search.length() == 4 ) {
                String subdir = "/genbank/wgs/";
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
            } else {
                URI baseuri = URI.create("https://"+ftpsite);
                if( wholeSelected ) {
                    wholeFetch(httpClient, baseuri);
                }

                if( plasmidSelected ) {
                    String subdir = "/genomes/Plasmids/gbk/";
					/*ftp.cwd( subdir );
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
								addSequencesPath( fname, urimap, thefile, replace );
							} catch (URISyntaxException e12) {
								e12.printStackTrace();
							}
						}
					}*/
                }

                if( phageSelected ) {
                    String subdir = "/genomes/refseq/viral/";
					/*ftp.cwd( subdir );
					FTPFile[] files3 = ftp.listFiles();
					for( FTPFile ftpfile : files3 ) {
						if( interrupted ) break;
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
										lat = "all_assembly_versions/suppressed";
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
										if (interrupted) break;

										String newfname = newftpfile.getName();
										if (newfname.endsWith(".gbff.gz")) {
											String spec = fname;
											//URL url = new URL("ftp://" + ftpsite + subdir + fname + "/" + newfname);
											String urlstr = "ftp://" + ftpsite + assembly + "/" + subfoldername + "/" + newfname;
											InputStream is = new URL(urlstr).openStream();//ftp.retrieveFileStream( newfname );
											GZIPInputStream gis = new GZIPInputStream(is);
											BufferedReader br = new BufferedReader(new InputStreamReader(gis));
											ByteArrayOutputStream baos = new ByteArrayOutputStream();
											String line = br.readLine();
											while (line != null) {
												if( line.startsWith("SOURCE") ) {
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
											}

											try {
												Map<String, Path> urimap = new HashMap<>();
												urimap.put(spec, thefile);
												addSequencesPath(spec, urimap, thefile, replace);
											} catch (URISyntaxException e12) {
												e12.printStackTrace();
											}
										}
									}
								}
							}
						}
					}*/
                }
            }
        }
    }
}
