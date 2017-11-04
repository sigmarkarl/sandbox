package org.simmi.distann;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.java_websocket.WebSocket;
import org.simmi.javafasta.shared.Cog;
import org.simmi.javafasta.shared.Gene;
import org.simmi.javafasta.shared.GeneGroup;
import org.simmi.javafasta.shared.Sequence;
import org.simmi.javafasta.shared.Serifier;
import org.simmi.javafasta.shared.Tegeval;

import flobb.ChatServer;

public class WSServer {
	public static ChatServer startServer( GeneSetHead genesethead ) throws UnknownHostException {
		return startServer(genesethead, 8887);
	}
		
	public static ChatServer startServer( GeneSetHead genesethead, int port ) throws UnknownHostException {
		GeneSet geneset = genesethead.geneset;	
		final ChatServer[] cs = new ChatServer[1];
			cs[0] = new ChatServer( port ) {
				String evalstr = "0.00001";
				Set<String> specset = new HashSet<>();
				
				@Override
				public void onMessage( WebSocket conn, String message ) {
					//System.err.println( message );
					if( message.startsWith(">") ) {
						final GeneSet.RunnableResult run = resl -> {
                            //BufferedReader br = new BufferedReader( new StringReader( resl ) );
                            /*try {
                                String line = br.readLine();
                                while( line != null ) {
                                    /*if( rr != null ) {
                                        rr.run( line+"\n" );
                                        //res += line+"\n";
                                    }*

                                    String res = line+"\n";
                                    if( line.startsWith("> ") ) {
                                        int i = line.indexOf(' ', 2);
                                        if( i == -1 ) i = line.length();
                                        String id = line.substring(2, i);

                                        /*line = br.readLine();
                                        while( line != null && !line.startsWith("Query=") && !line.startsWith(">") ) {
                                            res += line+"\n";
                                            line = br.readLine();
                                        }*/

                            if( resl.equals("close") ) cs[0].sendToAll(resl);
                            else {
                                byte[] bb = Base64.getEncoder().encode(resl.getBytes());

                                int i = resl.indexOf(' ', 2);
                                int k = resl.indexOf('\n', 2);
                                if( i == -1 ) i = resl.length();
                                if( k == -1 ) k = resl.length();

                                i = Math.min(i, k);

                                String id = resl.substring(2, i);
                                Gene g = geneset.genemap.get( id );
                                if( g != null && (specset.isEmpty() || specset.contains(g.getSpecies())) ) {
                                    GeneGroup gg = g.getGeneGroup();

                                    String ec = gg.getEc();
                                    Set<String> kegg = new TreeSet<String>();

                                    for( String pathw : geneset.pathwaymap.keySet() ) {
                                        Set<String> ecs = geneset.pathwaymap.get( pathw );
                                        int u = pathw.lastIndexOf(' ');
                                        if( ecs.contains(ec) ) kegg.add( pathw.substring(u) + "=" + pathw.substring(0,u).replace(",", "") );
                                    }

                                    Cog cog = gg.getCog(geneset.cogmap);
                                    String cogid = cog != null ? cog.id : null;
                                    String symbol = gg.getSymbol();
                                    if( cog != null && symbol == null ) symbol = cog.genesymbol;

                                    String seqstr = g.id + "\t" + gg.getName() + "\t" + symbol + "\t" + ec + "\t" + cogid + "\t" + geneset.cazymap.get(g.refid) + "\t" + gg.getCommonGO(false, true, null) + "\t" + kegg + "\t" + g.getSpecies() + "\t" + gg.genes.size() + "\t" + new String(bb) + "\n";
                                    //String old =  g.id + "\t" + gg.getCommonName() + "\t" + gg.getCommonSymbol() + "\t" + gg.getCommonEc() + "\t" + gg.getCommonCazy(cazymap) + "\t" + gg.getCommonGO(false, true, null) + "\t" + g.getSpecies() + "\t" + new String(bb) + "\n";
                                    cs[0].sendToAll( seqstr );
                                }
                            }
                            //cs[0].sendToAll( res );
                        };
						genesethead.doBlast( message, evalstr, true, run, false );
					} else if( message.contains("ready") ) {
						cs[0].sendToAll( "simmi" ); //cs[0].message );
					} else if( message.contains("cogchart:") ) {
						boolean uniform = false;
						if( message.substring(0).contains("uniform") ) uniform = true;
						//Set<String> species = getSelspec( GeneSet.this, specList );
						Set<String> selspec = new HashSet<>( geneset.specList );
						
						Set<Character> includedCogs = Cog.charcog.keySet();
						final Map<String,String>					all = new TreeMap<>();
						final Map<String, Map<Character,Integer>> 	map = new TreeMap<>();
						try {
							genesethead.cogCalc( null, includedCogs, map, selspec, false );
							StringWriter fw = geneset.writeCog( map, includedCogs, uniform );
							//String repl = fw.toString();
							
							//final String smuck = sb.toString().replace("smuck", restext.toString());
							cs[0].sendToAll( fw.toString() );
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if( message.contains("pancore:") ) {
						//Set<String> species = getSelspec( GeneSet.this, specList );
						Set<String> selspec = new HashSet<String>( geneset.specList );
						
						final String[] categories = { "Core: ", "Accessory: " };
						final List<GeneSet.StackBarData> lsbd = new ArrayList<>();
						StringBuilder restext = ActionCollection.panCore( genesethead, selspec, categories, lsbd );
						
						//final String smuck = sb.toString().replace("smuck", restext.toString());
						cs[0].sendToAll( restext.toString() );
					} else if( message.contains("anim:") ) {
						String spcstr = message.substring(5);
						//Set<String> species = getSelspec( GeneSet.this, specList );
						List<String> spcsl = geneset.specList;
						if( spcstr.length() > 0 ) {
							String[] spcs = spcstr.split(",");
							spcsl = Arrays.asList(spcs);
						}
						boolean skipplasmid = false;
						Set<String> species = new HashSet<>( spcsl );
						BufferedImage bimg = geneset.animatrix( species, geneset.clusterMap, null, geneset.allgenegroups, skipplasmid );
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						try {
							ImageIO.write(bimg, "png", baos);
							baos.close();
							String str = Base64.getEncoder().encodeToString( baos.toByteArray() );
							cs[0].sendToAll(str);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if( message.contains("geneatlas:") ) {
						String spec1 = message.substring(10);
						BufferedImage bimg = genesethead.gatest( spec1 );
						
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						try {
							ImageIO.write(bimg, "jpg", baos);
							baos.close();
							String str = Base64.getEncoder().encodeToString( baos.toByteArray() );
							cs[0].sendToAll(str);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if( message.contains("syntgrad:") ) {
						String spec1 = message.substring(9);
						
						Set<GeneGroup> sgg = new HashSet<>();
						int keggind = spec1.indexOf("kegg:");
						if( keggind != -1 ) {
							String[] sp = spec1.substring(keggind+5,spec1.length()).split(",");
							Set<String> str = new HashSet<>( Arrays.asList(sp) );
							
							spec1 = spec1.substring(0,keggind);
							for( Gene g : geneset.genelist ) {
								if( specset.isEmpty() || specset.contains(g.getSpecies()) ) {
									String gref = g.name;
									GeneGroup gg = g.getGeneGroup();
									String ec = gg.getEc();
									
									boolean cont = false;
									for( String sval : str ) {
										if( sval.startsWith("EC") ) {
											String ecstr = sval.substring(2).trim();
											if( ecstr.equals(ec) ) cont = true; 
										} else {
											cont = gref.toLowerCase().contains(sval.toLowerCase());
											for( String pathw : geneset.pathwaymap.keySet() ) {
												//int k = pathw.lastIndexOf(' ');
												String pname = pathw.replace(",", "");
												if( sval.equals( pname ) ) {
													Set<String> ecs = geneset.pathwaymap.get( pathw );
													if( ecs.contains(ec) ) {
														cont = true;
													}
												}
											}
										}
										
										if( cont ) break;
									}
									
									//boolean keggcont = false;
									//for( String kc : )
									
									if( cont ) {
										sgg.add( gg );
									} else {
										String cazy = geneset.cazymap.get( g.refid );
										if( cazy != null ) {
											//String ec = g.getGeneGroup().getCommonEc();
											String kegg = null;
											
											for( String pathw : geneset.pathwaymap.keySet() ) {
												Set<String> ecs = geneset.pathwaymap.get( pathw );
												if( ecs.contains(ec) ) kegg = pathw.substring(pathw.lastIndexOf(' '));
											}
											
											int i = cazy.indexOf('(');
											if( i == -1 ) i = cazy.length();
											cazy = cazy.substring(0,i);
											if( str.contains(cazy) ) {
												sgg.add( gg );
											}
										}
									}
								}
							}
							
							if( sgg.size() > 0 ) {
								for (GeneGroup ngg : genesethead.table.getItems()) {
									if( sgg.contains(ngg) ) {
										genesethead.genefilterset.add(ngg.groupIndex);
									}
								}
								genesethead.updateFilter(genesethead.table, genesethead.label);
							}
						}
						
						int w = 1024;
						int h = 1024;
						
						SyntGrad sg = new SyntGrad();
						final BufferedImage bi = new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB );
						
						final Graphics2D g2 = bi.createGraphics();
						g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
						g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
						
						if( spec1 != null && spec1.length() > 0 ) {
							List<Sequence> clist = geneset.speccontigMap.get(spec1);
							sg.drawImage( genesethead, g2, spec1, clist, geneset.specList, w, h );
						} else sg.drawImage( genesethead, g2, null, null, geneset.specList, w, h );
						
						if( sgg.size() > 0 ) {
							genesethead.genefilterset.clear();
							genesethead.updateFilter(genesethead.table, genesethead.label);
						}
						
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						try {
							ImageIO.write(bi, "png", baos);
							baos.close();
							String str = Base64.getEncoder().encodeToString( baos.toByteArray() );
							cs[0].sendToAll(str);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if( message.contains("stat:") ) {
						String querystr = message.substring(5).trim();
						Collection<String> str = geneset.specList;
						if( querystr.length() > 0 ) {
							String[] sp = querystr.split(",");
							str = new HashSet<>( Arrays.asList(sp) );
						}
						
						String htmlstr = "";
						try {
							htmlstr = ActionCollection.htmlTable(geneset, str, geneset.speccontigMap, false);
						} catch (IOException e) {
							e.printStackTrace();
						}
						String ans = Base64.getEncoder().encodeToString( htmlstr.getBytes() );
						cs[0].sendToAll(ans);
					} else if( message.contains("neigh:") ) {
						//Set<String> species = getSelspec( GeneSet.this, specList );
						//Set<String> species = new HashSet<String>( specList );
						
						String trim = message.substring(6).trim();
						Gene g = geneset.refmap.get( trim );
						GeneGroup[] gg = { g.getGeneGroup() };
						
						Neighbour nb = new Neighbour( new HashSet<GeneGroup>( Arrays.asList(gg) ) );
						nb.forward();
						nb.setZoomLevel( 0.3 );
						nb.names.setSelectedItem("Default names");
						BufferedImage bimg = nb.getImage( geneset, 50, 6000 );
						try {
							//nb.neighbourMynd( GeneSet.this, (Container)comp, genelist, new HashSet<GeneGroup>( Arrays.asList(gg) ), contigmap );
							//BufferedImage bimg = animatrix( species, clusterMap, null, allgenegroups );
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ImageIO.write(bimg, "png", baos);
							baos.close();
							String str = Base64.getEncoder().encodeToString( baos.toByteArray() );
							cs[0].sendToAll(str);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else if( message.contains("tree:") ) {
						String querystr = message.substring(5);
						String[] sp = querystr.split(",");
						Set<String> str = new HashSet<String>( Arrays.asList(sp) );
						
						int max = 0;
						Set<String>							specset = new HashSet<String>();
						Map<GeneGroup,Integer>				genegroups = new HashMap<GeneGroup,Integer>();
						for( Gene g : geneset.genelist ) {
							if( str.contains(g.refid) ) {
								GeneGroup gg = g.getGeneGroup();
								specset.addAll( gg.getSpecies() );
								for( Tegeval tv : gg.getTegevals() ) {
									Sequence alseq = tv.getAlignedSequence();
									if( alseq == null ) {
										Sequence sb = tv.getProteinSequence();
										int l = sb.length();
										if( l > max ) max = l;
									} else {
										int l = alseq.length();
										if( l > max ) max = l;
									}
								}
								genegroups.put( gg, max );
							}
						}
						
						Serifier serifier = geneset.getConcatenatedSequences(false, genegroups, specset, true);
						serifier.renameDuplicates();
						String tree = serifier.getFastTree( serifier.lseq, geneset.user, true );
						cs[0].sendToAll( tree );
						//Serifier serifier = getConcatenatedSequences( false );
					} else if( message.contains("query:") ) {
						StringBuilder sb = new StringBuilder();
						//int i = 0;
						
						Set<String> specset = new HashSet<String>();
						int indsp = message.indexOf("spec:");
						if( indsp == -1 ) indsp = message.length();
						else {
							String[] specspl = message.substring( indsp+5, message.length() ).split(",");
							specset.addAll( Arrays.asList( specspl ) );
						}
						
						String querystr = message.substring(6, indsp);
						String[] sp = querystr.split(",");
						Set<String> str = new HashSet<String>( Arrays.asList(sp) );
						for( Gene g : geneset.genelist ) {
							if( specset.isEmpty() || specset.contains(g.getSpecies()) ) {
								String gref = g.name;
								GeneGroup gg = g.getGeneGroup();
								String ec = gg.getEc();
								
								boolean cont = false;
								for( String sval : str ) {
									if( sval.startsWith("EC") ) {
										String ecstr = sval.substring(2).trim();
										if( ecstr.equals(ec) ) cont = true; 
									} else {
										cont = gref.toLowerCase().contains(sval.toLowerCase());
										for( String pathw : geneset.pathwaymap.keySet() ) {
											//int k = pathw.lastIndexOf(' ');
											String pname = pathw.replace(",", "");
											if( sval.equals( pname ) ) {
												Set<String> ecs = geneset.pathwaymap.get( pathw );
												if( ecs.contains(ec) ) {
													cont = true;
												}
											}
										}
									}
									
									if( cont ) break;
								}
								
								//boolean keggcont = false;
								//for( String kc : )
								
								if( cont ) {
									Set<String> kegg = new TreeSet<String>();
									
									for( String pathw : geneset.pathwaymap.keySet() ) {
										Set<String> ecs = geneset.pathwaymap.get( pathw );
										int k = pathw.lastIndexOf(' ');
										if( ecs.contains(ec) ) kegg.add( pathw.substring(k) + "=" + pathw.substring(0,k).replace(",", "") );
									}
									
									Cog cog = gg.getCog(geneset.cogmap);
									String cogid = cog != null ? cog.id : null;
									String symbol = gg.getSymbol();
									String go = gg.getCommonGO(false, true, null);
									if( cog != null && symbol == null ) symbol = cog.genesymbol;
									String seqstr = g.id + "\t" + gg.getName() + "\t" + symbol + "\t" + ec + "\t" + cogid + "\t" + geneset.cazymap.get(g.refid) + "\t" + go + "\t" + kegg + "\t" + g.getSpecies() + "\t" + gg.genes.size() + "\n";
									sb.append( seqstr );
								} else {
									String cazy = geneset.cazymap.get( g.refid );
									if( cazy != null ) {
										//String ec = g.getGeneGroup().getCommonEc();
										String kegg = null;
										
										for( String pathw : geneset.pathwaymap.keySet() ) {
											Set<String> ecs = geneset.pathwaymap.get( pathw );
											if( ecs.contains(ec) ) kegg = pathw.substring(pathw.lastIndexOf(' '));
										}
										
										int i = cazy.indexOf('(');
										if( i == -1 ) i = cazy.length();
										cazy = cazy.substring(0,i);
										if( str.contains(cazy) ) {
											//GeneGroup gg = g.getGeneGroup();
											Cog cog = gg.getCog(geneset.cogmap);
											String cogid = cog != null ? cog.id : null;
											String symbol = gg.getSymbol();
											if( cog != null && symbol == null ) symbol = cog.genesymbol;
											sb.append( g.id + "\t" + gg.getName() + "\t" + symbol + "\t" + ec + "\t" + cogid + "\t" + geneset.cazymap.get(g.refid) + "\t" + gg.getCommonGO(false, true, null) + "\t" + kegg + "\t" + g.getSpecies() + "\t" + gg.genes.size() + "\n" );
										}
									}
								}
							}
						}
						/*for( Gene g : genelist ) {
							GeneGroup gg = g.getGeneGroup();
							sb.append( g.id + "\t" + gg.getCommonName() + "\t" + gg.getCommonSymbol() + "\t" + gg.getCommonEc() + "\t" + gg.getCommonCazy(cazymap) + "\t" + gg.getCommonGO(true, null) + "\n" );
						
							if( i++ > 10 ) break;
						}*/
						if( sb.length() > 0 ) {
							cs[0].sendToAll( sb.toString() );
							cs[0].sendToAll( "close" );
						}
					} else if( message.contains("request:") ) {
						String idlist = message.substring(8).trim();
						String[] split = idlist.split(",");
						StringBuilder sb = new StringBuilder();
						for( String id : split ) {
							Gene g = geneset.refmap.get(id);
							if( g != null ) {
								try {
									g.getFasta( sb, false );
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						cs[0].sendToAll( sb.toString() );
					} else if( message.contains("evalue:") ) {
						specset.clear();
						int sind = message.indexOf("spec:");
						if( sind == -1 ) sind = message.length();
						else {
							String[] specspl = message.substring(sind+5,message.length()).split(",");
							specset.addAll( Arrays.asList(specspl) );
						}
						evalstr = message.substring(7,sind).trim();
					} else if( message.contains("specs:") ) {
						Set<String> cz = new TreeSet<String>();
						for( Gene g : geneset.genelist ) {
							String cazy = geneset.cazymap.get( g.refid );
							if( cazy != null ) {
								int i = cazy.indexOf('(');
								if( i == -1 ) i = cazy.length();
								cazy = cazy.substring(0,i);
								cz.add( cazy );
							}
						}
						
						Set<String> keggset = new TreeSet<String>();
						for( Gene g : geneset.genelist ) {
							String ec = g.getGeneGroup().getEc();
							for( String pathw : geneset.pathwaymap.keySet() ) {
								Set<String> ecs = geneset.pathwaymap.get( pathw );
								//int k = pathw.lastIndexOf(' ');
								if( ecs.contains(ec) ) keggset.add( /*pathw.substring(k) + "=" + */pathw.replace(",", "") );
							}
						}
						
						StringBuilder sb = new StringBuilder();
						for( String spec : geneset.specList ) {
							if( sb.length() == 0 ) sb.append( "specs:"+spec );
							else sb.append( ","+spec );
						}
						int l = sb.length();
						for( String cazy : cz ) {
							if( sb.length() == l ) sb.append( "cazy:"+cazy );
							else sb.append( ","+cazy );
						}
						l = sb.length();
						for( String kegg : keggset ) {
							if( sb.length() == l ) sb.append( "kegg:"+kegg );
							else sb.append( ","+kegg );
						}
						
						System.err.println( sb.toString() );
						cs[0].sendToAll( sb.toString() );
					}
				}
			};
			cs[0].start();
			
			return cs[0];
	}
}
