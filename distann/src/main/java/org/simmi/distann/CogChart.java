package org.simmi.distann;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simmi.javafasta.shared.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class CogChart {
    GeneSetHead genesethead;

    public CogChart(GeneSetHead genesethead) {
        this.genesethead = genesethead;
    }

    public void cogTotal() {
        GeneSet geneset = genesethead.geneset;
        JCheckBox cb = new JCheckBox("Plasmid");
        JCheckBox bc = new JCheckBox("Bar chart");
        JCheckBox lr = new JCheckBox("Lowres");
        Set<String>	selspec = genesethead.getSelspec( genesethead, new ArrayList<>( geneset.specList ), cb, bc, lr );

        String nohit = "-";
        final Map<String,Map<String,Integer>>	mm = new HashMap<>();
        Map<String,Integer> map = new HashMap<>();
        Map<String,Integer> mip = new HashMap<>();
        mm.put("Core", map);
        mm.put("Accessory", mip);
        selspec.forEach(s -> mm.put(s, new HashMap<>()));
        //final Map<String,Integer>	map = new HashMap<>();
        if( !genesethead.isGeneview() ) {
            for( GeneGroup gg : genesethead.table.getItems() ) {
                Cog cog = gg.getCog(geneset.cogmap);
                String cogsymbol = (cog != null && cog.cogsymbol != null && cog.cogsymbol.length()>0) ? cog.cogsymbol : "-";
                if( cb.isSelected() ) {
                    Set<String> tmp = new HashSet<>(gg.species.keySet());
                    tmp.retainAll( selspec );

                    if( tmp.size() > 0 ) {
                        int total = gg.size();
                        int p = 0;
                        for( Annotation a : gg.genes ) {
                            Sequence contig = a.getContig();
                            if( contig != null && contig.isPlasmid() ) p++;
                        }

                        if( gg.isOnAnyPlasmid() ) { //(float)p/(float)total > 0.9 ) { //gg.isOnAnyPlasmid() ) {
                            gg.getSpecies().stream().map(mm::get).forEach(mi -> mi.compute(cogsymbol, (k, v) -> v == null ? 1 : v+1));
                            mip.compute(cogsymbol, (k,v) -> v == null ? 1 : v+1);
                        } else {
                            int k = 0;
                            if( map.containsKey( cogsymbol ) ) k = map.get(cogsymbol);
                            map.put( cogsymbol, k+1 );
                        }
                    }
                } else {
                    if( gg.species.keySet().containsAll(selspec) ) {
                        int k = 0;
                        if( map.containsKey( cogsymbol ) ) k = map.get(cogsymbol);
                        map.put( cogsymbol, k+1 );
                    } else {
                        Set<String> tmp = new HashSet<>(gg.species.keySet());
                        tmp.removeAll( selspec );

                        if( tmp.size() < gg.species.size() ) {
                            gg.getSpecies().stream().map(mm::get).forEach(mi -> mi.compute(cogsymbol, (k, v) -> v == null ? 1 : v+1));
                            mip.compute(cogsymbol, (k,v) -> v == null? 1 : v+1);
                        }
                    }
                }
						/*for( String spec : selspec ) {
							if( gg.species.containsKey( spec ) ) {
								Teginfo ti = gg.species.get( spec );
								for( Tegeval tv : ti.tset ) {
									Map<Character,Integer> submap;
									/*if( contigs ) {
										if( map.containsKey( tv.contloc ) ) {

										}
									} else {*
										int val = 0;
										if( map.containsKey( tv.getSpecies() ) ) {
											submap = map.get( tv.getSpecies() );
											if( submap.containsKey(cog.symbol) ) val = submap.get(cog.symbol);
										} else {
											submap = new HashMap<Character,Integer>();
											map.put(spec, submap);
										}
										submap.put( cog.symbol, val+1 );
									//}
								}
							}
						}*/


                /*if( cb.isSelected() ) {
                    Set<String> tmp = new HashSet<>(gg.species.keySet());
                    tmp.retainAll( selspec );

                    if( tmp.size() > 0 ) {
                        int total = gg.size();
                        int p = 0;
                        for( Annotation a : gg.genes ) {
                            if( a.getContig().isPlasmid() ) p++;
                        }

                        if( gg.isOnAnyPlasmid() ) { //(float)p/(float)total > 0.9 ) { //gg.isOnAnyPlasmid() ) {
                            int k = 0;
                            if( mip.containsKey( nohit ) ) k = mip.get(nohit);
                            mip.put( nohit, k+1 );
                        } else {
                            int k = 0;
                            if( map.containsKey( nohit ) ) k = map.get(nohit);
                            map.put( nohit, k+1 );
                        }
                    }
                } else {
                    if( gg.species.keySet().containsAll(selspec) ) {
                        int k = 0;
                        if( map.containsKey( nohit ) ) k = map.get(nohit);
                        map.put( nohit, k+1 );
                    } else {
                        Set<String> tmp = new HashSet<>(gg.species.keySet());
                        tmp.removeAll( selspec );

                        if( tmp.size() < gg.species.size() ) {
                            int k = 0;
                            if( mip.containsKey( nohit ) ) k = mip.get(nohit);
                            mip.put( nohit, k+1 );
                        }
                    }
                }*/
            }
        }

        //Character last = null;
        StringBuilder sb = new StringBuilder();
        for( String s : Cog.coggroups.keySet() ) {
            Set<String> sc = Cog.coggroups.get(s);
            if( s.contains("METABOLISM") ) {
                for( String c : sc ) {
                    sb.append("\t").append(Cog.charcog.get(c));
                }
            } else {
                sb.append("\t").append(s);
                if( sc.contains('V') ) {
                    sb.append( "\tDefence mechanism" );
                }
            }
            //last = c;
        }
        sb.append( "\tNo hit" );

        if( cb.isSelected() ) sb.append( "\nChromosome" );
        else sb.append( "\nCore" );

        int mit = 0;
        int mat = 0;
        for( String s : Cog.coggroups.keySet() ) {
            Set<String> sc = Cog.coggroups.get(s);
            if( s.contains("METABOLISM") ) {
                for( String c : sc ) {
                    int count = 0;
                    if( map.containsKey(c) ) {
                        int val = map.get(c);
                        count = val;
                        //if( c.equals(last) ) sb.append("\n");
                        //sb.append("\t");
                        mat += val;
                    }
                    sb.append("\t").append(count);
                }
            } else {
                int count = 0;
                for( String c : sc ) {
                    if( !c.equals('V') && map.containsKey(c) ) {
                        int val = map.get(c);
                        count += val;
                        //if( c.equals(last) ) sb.append("\n");
                        //sb.append("\t");
                        mat += val;
                    }
                }
                sb.append("\t").append(count);

                if( sc.contains('V') ) {
                    count = 0;
                    if( map.containsKey('V') ) {
                        int val = map.get('V');
                        count += val;
                    }
                    sb.append("\t").append(count);
                }
            }
        }
        int count = 0;
        if( map.containsKey(nohit) ) count = map.get( nohit );
        sb.append("\t").append(count);

        if( cb.isSelected() ) sb.append( "\nPlasmid" );
        else sb.append( "\nAccessory" );

        for( String s : Cog.coggroups.keySet() ) {
            Set<String> sc = Cog.coggroups.get(s);
            if( s.contains("METABOLISM") ) {
                for( String c : sc ) {
                    count = 0;
                    if( mip.containsKey(c) ) {
                        int val = mip.get(c);
                        count = val;
                        mit += val;
                    }
                    sb.append("\t").append(count);
                }
            } else {
                count = 0;
                for( String c : sc ) {
                    if( !c.equals('V') && mip.containsKey(c) ) {
                        int val = mip.get(c);
                        count += val;
                        mit += val;
                    }
                }
                sb.append("\t").append(count);

                if( sc.contains('V') ) {
                    count = 0;
                    if( mip.containsKey('V') ) {
                        int val = mip.get('V');
                        count += val;
                    }
                    sb.append("\t").append(count);
                }
            }
        }
        count = 0;
        if( mip.containsKey(nohit) ) count = mip.get( nohit );
        sb.append("\t").append(count);
        sb.append( "\n" );
        System.err.println( "map size " + mat + " mip size " + mit );

        SwingUtilities.invokeLater(() -> {
            CogChart cogChart = new CogChart(genesethead);
            boolean lowres = lr.isSelected();
            if( geneset.fxframe == null ) {
                geneset.fxframe = new JFrame("COG");
                geneset.fxframe.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
                geneset.fxframe.setSize(800, 600);

                final JFXPanel	fxpanel = new JFXPanel();
                geneset.fxframe.add( fxpanel );

                if(bc.isSelected()) {
                    Platform.runLater(() -> {
                        try {
                            cogChart.initDualStackedBarChart(fxpanel, mm, lowres);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    Platform.runLater(() -> cogChart.initDualPieChart(fxpanel, map, mip));
                }
            } else {
                if(!bc.isSelected()) {
                    Platform.runLater(() -> cogChart.initDualPieChart(null, map, mip));
                } else {
                    Platform.runLater(() -> {
                        try {
                            cogChart.initDualStackedBarChart(null, mm, lowres);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
            geneset.fxframe.setVisible( true );
        });

        JFrame f = new JFrame("GC% chart");
        f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        f.setSize( 800, 600 );

        JTextArea	ta = new JTextArea();
        ta.setText( sb.toString() );
        JScrollPane	sp = new JScrollPane(ta);
        f.add( sp );
        f.setVisible( true );
    }

    public StringWriter writeSimpleCog( Map<String,Map<String,Integer>> map ) throws IOException {
        StringWriter fw = new StringWriter();
        fw.write( "['Species" );
        for( String coggroup : Cog.coggroups.keySet() ) {
            fw.write("','"+coggroup);
        }
        fw.write("']");
        for( String s : map.keySet() ) {
            fw.write(",\n");
            fw.write( "['"+s+"'" );
            Map<String,Integer> cm = map.get( s );
            //for( Character cogchar : Cog.charcog.keySet() ) {
            for( String coggroup : Cog.coggroups.keySet() ) {
                int val = 0;
                Set<String> groupchars = Cog.coggroups.get(coggroup);
                for( String cogchar : groupchars ) {
                    if( cm.containsKey( cogchar ) ) {
                        val += cm.get(cogchar);
                    }
                }
                fw.write(","+val);
            }
            fw.write("]");
        }
        fw.close();

        return fw;
    }

    public StringWriter writeCogDouble( Map<String,Map<String,Double>> map, Set<String> includedCogs, boolean uniform ) throws IOException {
        StringWriter fw = new StringWriter();
        fw.write("[");
        fw.write( "['Species" );
        for( String cogchar : includedCogs ) {
            String coglong = Cog.charcog.get( cogchar );
            fw.write("','"+coglong);
        }
        fw.write("']");

        Map<String,Integer> totmap = new HashMap<>();
        for( String s : map.keySet() ) {
            int total = 0;
            Map<String,Double> cm = map.get( s );
            for( String cogchar : includedCogs ) {
                double val = 0;
                if( cm.containsKey( cogchar ) ) {
                    val = cm.get(cogchar);
                }
                total += val;
            }
            totmap.put( s, total );
        }

        for( String s : map.keySet() ) {
            fw.write(",\n");
            int total = totmap.get( s );
            fw.write( "['"+Sequence.nameFix(s)+"'" );
            Map<String,Double> cm = map.get( s );
            for( String cogchar : includedCogs ) {
                double val = 0;
                //String coglong = Cog.charcog.get(cogchar);
                //Character cogchar = Cog.cogchar.get( coglong );
                if( cm.containsKey( cogchar ) ) {
                    val = cm.get(cogchar);
                }// else val = -1;

                if( uniform ) {
                    fw.write(","+((double)val/(double)total));
                } else {
                    fw.write(","+val);
                }
            }
            fw.write("]");
        }
        fw.write("]");

		/*fw.write( "Species" );
		for( String cog : all.keySet() ) {
			String coglong = all.get( cog );
			fw.write("\t"+coglong);
		}
		for( String s : map.keySet() ) {
			int total = 0;
			fw.write( "\n"+s );
			Map<String,Integer> cm = map.get( s );
			for( String cog : all.keySet() ) {
				int val = 0;
				if( cm.containsKey( cog ) ) val = cm.get(cog);
				fw.write("\t"+val);
			}
			//fw.write("\n");
		}

		/*for( String cog : all ) {
			fw.write( "\n"+cog );
			for( String spec : map.keySet() ) {
				Map<String,Integer> cm = map.get( spec );
				if( cm.containsKey( cog ) ) fw.write( "\t" + cm.get( cog )  );
				else fw.write( "\t" + 0  );
			}
		}*/

        fw.close();

        return fw;
    }

    public StringWriter writeCogLowres( Map<String,Map<String,Integer>> map, boolean uniform, String groupName ) throws IOException {
        StringWriter fw = new StringWriter();
        fw.write("[");
        fw.write( "['"+groupName );
        for( String coglong : Cog.coggroups.keySet() ) {
            fw.write("','"+coglong);
        }
        fw.write("']");

        Set<String> includedCogs = Cog.coggroups.values().stream().reduce((a,b) -> {
            var l = new HashSet<String>();
            l.addAll(a);
            l.addAll(b);
            return l;
        }).get();

        Map<String,Integer> totmap = new HashMap<>();
        for( String s : map.keySet() ) {
            int total = 0;
            Map<String,Integer> cm = map.get( s );
            for( String cogchar : includedCogs ) {
                int val = 0;
                if( cm.containsKey( cogchar ) ) {
                    val = cm.get(cogchar);
                }
                total += val;
            }
            totmap.put( s, total );
        }

        for( String s : map.keySet() ) {
            fw.write(",\n");
            int total = totmap.get( s );
            fw.write( "['"+s+"'" );
            Map<String,Integer> cm = map.get( s );
            for( String group : Cog.coggroups.keySet() ) {
                var incogs = Cog.coggroups.get(group);
                int val = 0;
                for (String cogchar : incogs) {
                    //String coglong = Cog.charcog.get(cogchar);
                    //Character cogchar = Cog.cogchar.get( coglong );
                    if (cm.containsKey(cogchar)) {
                        val += cm.get(cogchar);
                    }// else val = -1;
                }

                if (uniform) {
                    fw.write("," + ((double) val / (double) total));
                } else {
                    fw.write("," + val);
                }
            }
            fw.write("]");
        }
        fw.write("]");
        fw.close();

        return fw;
    }

    public StringWriter writeCog( Map<String,Map<String,Integer>> map, Set<String> inclCogs, boolean uniform, String groupName ) throws IOException {
        StringWriter fw = new StringWriter();
        fw.write("[");
        fw.write( "['"+groupName );

        List<String> includedCogs = new ArrayList<>();
        Set<String> incogs = new HashSet<>(inclCogs);
        String cogchr = "-";
        if(incogs.remove(cogchr)) {
            includedCogs.add(cogchr);
            String coglong = Cog.charcog.get(cogchr);
            fw.write("','-(" + cogchr + ") " + coglong);
        }

        cogchr = "S";
        if(incogs.remove(cogchr)) {
            includedCogs.add(cogchr);
            String coglong = Cog.charcog.get(cogchr);
            fw.write("','-(" + cogchr + ") " + coglong);
        }

        cogchr = "R";
        if(incogs.remove(cogchr)) {
            includedCogs.add(cogchr);
            String coglong = Cog.charcog.get(cogchr);
            fw.write("','-(" + cogchr + ") " + coglong);
        }

        for( String cogchar : incogs ) {
            includedCogs.add(cogchar);
            String coglong = Cog.charcog.get( cogchar );
            fw.write("','("+cogchar+") "+coglong);
        }
        fw.write("']");

        //includedCogs = treeSet;

        Map<String,Integer> totmap = new HashMap<>();
        for( String s : map.keySet() ) {
            int total = 0;
            Map<String,Integer> cm = map.get( s );
            for( String cogchar : includedCogs ) {
                int val = 0;
                if( cm.containsKey( cogchar ) ) {
                    val = cm.get(cogchar);
                }
                total += val;
            }
            totmap.put( s, total );
        }

        for( String s : map.keySet() ) {
            fw.write(",\n");
            int total = totmap.get( s );
            fw.write( "['"+s+"'" );
            Map<String,Integer> cm = map.get( s );
            for( String cogchar : includedCogs ) {
                int val = 0;
                //String coglong = Cog.charcog.get(cogchar);
                //Character cogchar = Cog.cogchar.get( coglong );
                if( cm.containsKey( cogchar ) ) {
                    val = cm.get(cogchar);
                }// else val = -1;

                if( uniform ) {
                    fw.write(","+((double)val/(double)total));
                } else {
                    fw.write(","+val);
                }
            }
            fw.write("]");
        }
        fw.write("]");

		/*fw.write( "Species" );
		for( String cog : all.keySet() ) {
			String coglong = all.get( cog );
			fw.write("\t"+coglong);
		}
		for( String s : map.keySet() ) {
			int total = 0;
			fw.write( "\n"+s );
			Map<String,Integer> cm = map.get( s );
			for( String cog : all.keySet() ) {
				int val = 0;
				if( cm.containsKey( cog ) ) val = cm.get(cog);
				fw.write("\t"+val);
			}
			//fw.write("\n");
		}

		/*for( String cog : all ) {
			fw.write( "\n"+cog );
			for( String spec : map.keySet() ) {
				Map<String,Integer> cm = map.get( spec );
				if( cm.containsKey( cog ) ) fw.write( "\t" + cm.get( cog )  );
				else fw.write( "\t" + 0  );
			}
		}*/

        fw.close();

        return fw;
    }

    private void cogSeq( Sequence c, Map<String,Integer>	submap, Map<String,Set<String>>	descmap, boolean accessory ) {
        if (c.getAnnotations() != null) for (Annotation a : c.getAnnotations()) {
            Gene g = a.getGene();
            if(g!=null) {
                Cog cog = g.cog;
                if (cog == null) {
                    cog = genesethead.geneset.cogmap.get(g.id);
                    if(cog==null) {
                        GeneGroup gg = g.getGeneGroup();
                        if(gg!=null) for(Annotation ann : gg.genes) {
                            Gene gene = ann.getGene();
                            if(gene!=null) {
                                cog = gene.cog;
                                if(cog!=null) break;
                            }
                        }
                    }
                    g.cog = cog;
                }
                String cogsymbol = "-";
                if (cog != null && cog.cogsymbol != null && cog.cogsymbol.length()>0) cogsymbol = cog.cogsymbol;
                String annotation = cog != null ? "("+cog.cogsymbol+") " + cog.annotation : "(-) No annotation";
                GeneGroup gg = g.getGeneGroup();
                if(gg != null && (!accessory || gg.genes.size() < 2)) {
                    cogsymbol.chars().forEach(cc -> {
                        String splitSymbol = String.valueOf((char)cc);
                        submap.compute(splitSymbol, (k, v) -> v == null ? 1 : v + 1);
                        descmap.compute(splitSymbol, (k, v) -> {
                            if (v == null) {
                                Set<String> vset = new HashSet<>();
                                vset.add(annotation);
                                return vset;
                            } else {
                                v.add(annotation);
                                return v;
                            }
                        });
                    });
                } else if(gg == null) {
                    System.err.println();
                }
                    /*int val = 0;
                    if (submap.containsKey(cog.symbol)) val = submap.get(cog.symbol);
                    submap.put(cog.symbol, val + 1);*/
            }
        }
    }

    public void cogCalc( String filename, Set<String> includedCogs, Map<String,Map<String,Integer>> map, Set<String> selspec, boolean contigs, Map<String,Set<String>> descmap, boolean accessory ) throws IOException {
        if( !genesethead.isGeneview() ) {
            for( String spec : selspec ) {
                if( contigs ) {
                    Sequence seq = genesethead.geneset.contigmap.get(spec);
                    Map<String,Integer>	submap;
                    if( map.containsKey( seq.getGroup() ) ) {
                        submap = map.get( seq.getGroup() );
                    } else {
                        submap = new HashMap<>();
                        map.put( seq.getGroup(), submap );
                    }
                    cogSeq( seq, submap, descmap, accessory );
                } else {
                    Map<String,Integer>	submap = new HashMap<>();
                    map.put(spec, submap);

                    List<Sequence> sctg = genesethead.geneset.speccontigMap.get(spec);
                    for (Sequence c : sctg) {
                        cogSeq( c, submap, descmap, accessory );
                    }
                }
            }

			/*for( int r = 0; r < table.getRowCount(); r++ ) {
				//int i = table.convertRowIndexToModel(r);
				//if( i >= 0 && i < allgenegroups.size() ) {
			for( int i = 0; i < allgenegroups.size(); i++ ) {
					GeneGroup gg = allgenegroups.get(i);
					Cog cog = gg.getCommonCog(cogmap);
					if( cog != null && includedCogs.contains(cog.symbol) ) {
						for( String spec : selspec ) {
							if( gg.species.containsKey( spec ) ) {
								Teginfo ti = gg.species.get( spec );
								for( Tegeval tv : ti.tset ) {
									Map<Character,Integer> submap;
									if( contigs ) {
										if( map.containsKey( tv.contloc ) ) {

										}
									} else {
										int val = 0;
										//if( map.containsKey( tv.getSpecies() ) ) {
										submap = map.get( tv.getSpecies() );
										if( submap.containsKey(cog.symbol) ) val = submap.get(cog.symbol);
										/*} else {
											submap = new HashMap<Character,Integer>();
											map.put(spec, submap);
										}*
										submap.put( cog.symbol, val+1 );
									}
								}
							}
						}
					}
				//}
			}*/
        } else {

        }
        //cogCalc(filename, br, map, selspec, contigs);
    }

    public void run() {
        try {
                /*Map<String,String> env = new HashMap<String,String>();
                //env.put("create", "true");
                //Path path = zipfile.toPath();
                String uristr = "jar:" + geneset.zippath.toUri();
                geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")* );
                geneset.zipfilesystem = FileSystems.newFileSystem( geneset.zipuri, env );

                Path nf = geneset.zipfilesystem.getPath("/cog.blastout");

                //InputStream is = new GZIPInputStream( new FileInputStream( fc.getSelectedFile() ) );
                //uni2symbol(new InputStreamReader(is), bw, unimap);

                //bw.close();
                //long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );

                BufferedReader br = Files.newBufferedReader(nf);*/

            GeneSet geneset = genesethead.geneset;
            final JCheckBox contigs = new JCheckBox("Show contigs");
            final JCheckBox uniform = new JCheckBox("Uniform");
            final JCheckBox accessory = new JCheckBox("Accessory");
            Set<String> selspec = genesethead.getSelspec(genesethead, new ArrayList<>(geneset.specList), contigs, uniform, accessory);

            final List<String> coglist = new ArrayList<>(Cog.charcog.keySet());
            HashSet<String> includedCogs = new HashSet<>();
            JTable cogtable = new JTable();
            cogtable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            TableModel cogmodel = new CogTableModel(coglist);
            boolean web = true;

            JCheckBox webbox = new JCheckBox("Web based");
            webbox.setSelected(true);

            cogtable.setModel(cogmodel);
            JScrollPane cogscroll = new JScrollPane(cogtable);

            Object[] objs = new Object[]{cogscroll, webbox};
            JOptionPane.showMessageDialog(null, objs);
            web = webbox.isSelected();

            int[] rr = cogtable.getSelectedRows();
            for (int r : rr) {
                includedCogs.add(coglist.get(r));
            }

            final Map<String,Set<String>> cogAnnoMap = new TreeMap<>();
            final Map<String, String> all = new TreeMap<>();
            final Map<String, Map<String, Integer>> map = new LinkedHashMap<>();
            CogChart cogChart = new CogChart(genesethead);
            cogChart.cogCalc(null, includedCogs, map, selspec, contigs.isSelected(), cogAnnoMap, accessory.isSelected());

            Map<String, Row> rl = new HashMap<>();

            Workbook wb = new XSSFWorkbook();
            Sheet sh = wb.createSheet("COG");

            Row namerow = sh.createRow(0);

            namerow.createCell(0).setCellValue("Cog id");
            namerow.createCell(1).setCellValue("Cog anno");
            for(String cogid : cogAnnoMap.keySet()) {
                Set<String> annos = cogAnnoMap.get(cogid);
                if (!rl.containsKey(cogid)) {
                    Row row = sh.createRow(rl.size() + 1);
                    rl.put(cogid, row);

                    row.createCell(0).setCellValue(cogid);
                    row.createCell(1).setCellValue(annos.toString());
                }
            }

            int k = 2;
            for (String sp : map.keySet()) {
                namerow.createCell(k).setCellValue(sp);
                Map<String, Integer> mm = map.get(sp);
                if (mm != null) for (String c : mm.keySet()) {
                    int cn = mm.get(c);
                    Row r = rl.get(c);
                    r.createCell(k).setCellValue(cn);
                }
                k += 1;
            }

            String userhome = System.getProperty("user.home");
            File tf = new File(userhome);
            File f = new File(tf, "tmp.xlsx");
            FileOutputStream fos = new FileOutputStream(f);
            wb.write(fos);
            fos.close();

            Desktop.getDesktop().open(f);

            StringWriter fw = writeCog(map, includedCogs, uniform.isSelected(), "Species");
            String repl = fw.toString();

            fw = writeSimpleCog(map);
            String stuff = fw.toString();

            String stxt = "";
            final StringBuilder sb = new StringBuilder();
            InputStream is = GeneSet.class.getResourceAsStream("cogchart.html");
            if (is != null) {
                try {
                    int c = is.read();
                    while (c != -1) {
                        sb.append((char) c);
                        c = is.read();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                stxt = sb.toString().replace("smuck", repl);
            }
            final String smuck = stxt;

            //String b64str = Base64.encodeBase64String( smuck.getBytes() );
                /*JSObject window = null;
                try {
                    window = JSObject.getWindow( geneset );
                } catch( NoSuchMethodError | Exception exc ) {
                    exc.printStackTrace();
                }*/

            if (web) {
                Platform.runLater(() -> {
                    final Stage dialog = new Stage();
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.initOwner(genesethead.primaryStage);
                    VBox dialogVbox = new VBox(20);
                    dialogVbox.getChildren().add(new Text("This is a Dialog"));
                    Scene dialogScene = new Scene(dialogVbox, 300, 200);
                    dialog.setScene(dialogScene);
                    initWebPage(dialog, smuck);
                    dialog.show();
                });

                    /*boolean succ = true;
                    try {
                        window.setMember("smuck", smuck);
                        //window.eval("var binary = atob(b64str)");
                        //window.eval("var i = binary.length");
                        //window.eval("var view = new Uint8Array(i)");
                        //window.eval("while(i--) view[i] = binary.charCodeAt(i)");
                        window.eval("var b = new Blob( [smuck], { \"type\" : \"text\\/html\" } );");
                        window.eval("open( URL.createObjectURL(b), '_blank' )");
                    } catch( Exception exc ) {
                        exc.printStackTrace();
                    }*

                    try {
                        window.setMember("smuck", smuck);

                        //window.eval("var binary = atob(b64str)");
                        //window.eval("var i = binary.length");
                        //window.eval("var view = new Uint8Array(i)");
                        //window.eval("while(i--) view[i] = binary.charCodeAt(i)");
                        window.eval("var b = new Blob( [smuck], { \"type\" : \"text\\/html\" } );");
                        window.eval("open( URL.createObjectURL(b), '_blank' )");
                    } catch( Exception exc ) {
                        exc.printStackTrace();
                    }*/

                if (Desktop.isDesktopSupported()) {
                    try {
                        //File uf = new File(userhome);
                        File smf = new File(tf, "smuck.html");
                        FileWriter fwr = new FileWriter(smf);
                        fwr.write(smuck);
                        fwr.close();
                        Desktop.getDesktop().browse(smf.toURI());
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
            } else {
                Platform.runLater(() -> {
                    final Stage dialog = new Stage();
                    dialog.initModality(Modality.APPLICATION_MODAL);
                    dialog.initOwner(genesethead.primaryStage);
                    VBox dialogVbox = new VBox(20);
                    dialogVbox.getChildren().add(new Text("This is a Dialog"));
                    Scene dialogScene = new Scene(dialogVbox, 300, 200);
                    dialog.setScene(dialogScene);
                    initStackedBarChart(dialog, all, map, uniform.isSelected());
                    dialog.show();
                });
            }
            geneset.zipfilesystem.close();

            JFrame fr = new JFrame("GC% chart");
            fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            fr.setSize(800, 600);

            JTextArea ta = new JTextArea();
            ta.setText(repl + "\n" + stuff);
            JScrollPane sp = new JScrollPane(ta);
            fr.add(sp);
            fr.setVisible(true);
        } catch(IOException e1){
            e1.printStackTrace();
        }
    }

    static Scene scene = null;
    private static Scene createScene( String[] names, double[] xdata, double[] ydata ) {
        final NumberAxis xAxis = new NumberAxis(-0.5, 0.5, 0.025);
        final NumberAxis yAxis = new NumberAxis(-0.5, 0.5, 0.025);
        final ScatterChart<Number,Number> sc = new ScatterChart<Number,Number>(xAxis,yAxis);
        xAxis.setLabel("Dim 1");
        yAxis.setLabel("Dim 2");
        sc.setTitle("Genes");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("PCA");
        for( int i = 0; i < xdata.length; i++ ) {
            XYChart.Data d = new XYChart.Data( xdata[i], ydata[i] );
            //Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
            series1.getData().add( d );
        }

        sc.getData().addAll(series1);
        if( scene == null ) {
            scene = new Scene( sc );
        } else scene.setRoot( sc );

        for (XYChart.Series<Number, Number> s : sc.getData()) {
            int i = 0;
            for (XYChart.Data<Number, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip( names[i++] ) );
            }
        }

        sc.setBackground( Background.EMPTY );

        final ContextMenu menu = new ContextMenu();
        javafx.scene.control.MenuItem mi = new javafx.scene.control.MenuItem();
        mi.setOnAction(arg0 -> {
            WritableImage fximg = sc.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("c:/fximg.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.getItems().add( mi );
        sc.setOnMouseClicked(event -> {
            if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                menu.show(sc, event.getScreenX(), event.getScreenY());
            }
        });

        return scene;
    }

    private Scene createWebPageScene( String webp ) {

		/*if( scene == null ) {
        	scene = new Scene( sc );
        } else scene.setRoot( sc );*/

        return scene;
    }

    private Scene createDualPieChartScene( Map<String,Integer> map, Map<String,Integer> mip ) {
        List<String> speclist = new ArrayList<>();
        /*for( String spec : mip.keySet() ) {
        	speclist.add( nameFix(spec) );
        }*/

        HBox hbox = new HBox();

        final PieChart sc = new PieChart();
        sc.labelsVisibleProperty().set( true );
        sc.setLegendVisible(false);
        sc.setLegendSide( Side.RIGHT );
        sc.setTitle("COG core");

        final PieChart sc2 = new PieChart();
        sc2.labelsVisibleProperty().set( true );
        sc2.setLegendVisible(false);
        sc2.setLegendSide( Side.RIGHT );
        sc2.setVisible( true );
        sc2.setTitle("COG accessory");

        //Font f = sc.getXAxis().settic
        //sc.setStyle( "-fx-font-size: 2.4em;" );
        //System.err.println( sc.getXAxis().getStyle() );

       /* Map<String,Integer> countmap = new HashMap<String,Integer>();
        for( String spec : map.keySet() ) {
        	Map<Character,Integer> submap = map.get(spec);
        	int total = 0;
        	for( Character f : submap.keySet() ) {
        		total += submap.get(f);
        	}
        	countmap.put( spec, total );
        }*/

        //PieChart.Data d = new PieChart.Data();
        for( String s : Cog.coggroups.keySet() ) {
            Set<String> schar = Cog.coggroups.get( s );
            if( s.contains("METABOLISM") ) {
                for( String cogsymbol : schar ) {
                    if( cogsymbol != null ) {
                        int count = 0;
                        if( map.containsKey(cogsymbol) ) count = map.get( cogsymbol );

				        /*XYChart.Series<String,Number> core = new XYChart.Series<String,Number>();
				        int i = longname.indexOf(',', 50);
				        if( i == -1 ) i = longname.length();
				        core.setName( longname.substring(0,i) );
				        for( String spec : map.keySet() ) {
				        	Map<Character,Integer> submap = map.get(spec);
				        	//int last = 0;
				        	//for( String f : submap.keySet() ) {
				        	if( submap.containsKey(flock) ) {
				        		int total = countmap.get(spec);
					        	int ival = submap.get( flock );
					        	String fixspec = nameFix(spec);
					        	XYChart.Data<String,Number> d = uniform ?  new XYChart.Data<String,Number>( fixspec, (double)ival/(double)total ) : new XYChart.Data<String,Number>( fixspec, ival );
					        	//Tooltip.install( d.getNode(), new Tooltip( flock ) );
					        	core.getData().add( d );
				        	}

					        //last = last+ival;
				        }*/
                        PieChart.Data d = new PieChart.Data( cogsymbol/*Cog.charcog.get(cogsymbol)*/, count );
                        ObservableList<PieChart.Data> ob = sc.getData();
                        ob.add( d );
                    }
                }
            } else {
                int count = 0;
                for( String cogsymbol : schar ) {
                    if( cogsymbol != null ) {
                        if( map.containsKey(cogsymbol) ) count += map.get( cogsymbol );
                    }
                }
                PieChart.Data d = new PieChart.Data( s/*Cog.charcog.get(cogsymbol)*/, count );
                ObservableList<PieChart.Data> ob = sc.getData();
                ob.add( d );
            }
        }

        for( String s : Cog.coggroups.keySet() ) {
            Set<String> schar = Cog.coggroups.get( s );
            if( s.contains("METABOLISM") ) {
                for( String cogsymbol : schar ) {
                    if( cogsymbol != null ) {
                        int count = 0;
                        if( mip.containsKey(cogsymbol) ) count = mip.get( cogsymbol );
                        PieChart.Data d = new PieChart.Data( cogsymbol/*Cog.charcog.get(cogsymbol)*/, count );
                        ObservableList<PieChart.Data> ob = sc2.getData();
                        ob.add( d );
                    }
                }
            } else {
                int count = 0;
                for( String cogsymbol : schar ) {
                    if( cogsymbol != null ) {
                        if( mip.containsKey(cogsymbol) ) count += mip.get( cogsymbol );
                    }
                }
                PieChart.Data d = new PieChart.Data( s/*Cog.charcog.get(cogsymbol)*/, count );
                ObservableList<PieChart.Data> ob = sc2.getData();
                ob.add( d );
            }
        }

        /*XYChart.Series<String,Number> pan = new XYChart.Series<String,Number>();
        pan.setName("Pan");
        //for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d = new XYChart.Data<String,Number>( "dd", 100 );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan.getData().add( d );
        //}
        XYChart.Series<String,Number> pan2 = new XYChart.Series<String,Number>();
        pan2.setName("Core");
        //for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d2 = new XYChart.Data<String,Number>( "2", 200 );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan2.getData().add( d2 );
        //}
        sc.getData().addAll(pan, pan2);*/

        hbox.getChildren().addAll( sc, sc2 );
        if( scene == null ) {
            scene = new Scene( hbox );
        } else scene.setRoot( hbox );

        /*for (XYChart.Series<String, Number> s : sc.getData()) {
        	//int i = 0;
            for (XYChart.Data<String, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip( s.getName()+": "+d.getYValue() ) );
            }
        }*/

        HBox.setHgrow(sc, Priority.ALWAYS);
        HBox.setHgrow(sc2, Priority.ALWAYS);

        sc.setBackground( Background.EMPTY );

        final ContextMenu menu = new ContextMenu();
        javafx.scene.control.MenuItem mi = new javafx.scene.control.MenuItem();
        mi.setOnAction(arg0 -> {
            WritableImage fximg = sc.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("/Users/sigmar/fximg.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.getItems().add( mi );
        sc.setOnMouseClicked(event -> {
            if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                menu.show(sc, event.getScreenX(), event.getScreenY());
            }
        });

        return scene;
    }

    private Scene createDualStackedBarChartScene( Map<String,Map<String,Integer>> mm, boolean lowres ) throws IOException {
        //Map<String, Map<String,Integer>> mm = Map.of("Core", map, "Accessory", mip);
        StringWriter fw = lowres ? writeCogLowres(mm, true, "Species") : writeCog(mm, Cog.charcog.keySet(), true, "Species");
        String repl = fw.toString();

        //fw = writeSimpleCog(map);
        //String stuff = fw.toString();

        InputStream is = GeneSet.class.getResourceAsStream("cogchart.html");
        String smuck = new String(is.readAllBytes()).replace("smuck", repl).replace("isStacked: true", "isStacked: 'percent'");

        //String b64str = Base64.encodeBase64String( smuck.getBytes() );
                /*JSObject window = null;
                try {
                    window = JSObject.getWindow( geneset );
                } catch( NoSuchMethodError | Exception exc ) {
                    exc.printStackTrace();
                }*/

        if (Desktop.isDesktopSupported()) {
            Path p = Paths.get("/Users/sigmar/smuck2.html");
            Files.writeString(p, smuck, StandardOpenOption.CREATE);
            Desktop.getDesktop().browse(p.toUri());
        }




        List<String> speclist = new ArrayList<>();
        /*for( String spec : mip.keySet() ) {
        	speclist.add( nameFix(spec) );
        }*/

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis 	yAxis = new NumberAxis();

        xAxis.setTickLabelRotation( 90.0 );

        /*for( String spec : map.keySet() ) {
            speclist.add( Sequence.nameFix(spec) );
        }
        xAxis.setCategories( FXCollections.observableArrayList( speclist ) );*/

        HBox hbox = new HBox();

        final StackedBarChart<String,Number> sc = new StackedBarChart<>(xAxis,yAxis);
        //sc.labelsVisibleProperty().set( true );
        sc.setLegendVisible(true);
        sc.setLegendSide( Side.RIGHT );
        sc.setTitle("COG");

        /*XYChart.Series<String,Number> d1 = new XYChart.Series<>();
        sc.getData().add(d1);
        d1.setName("COG core");
        XYChart.Series<String,Number> d2 = new XYChart.Series<>();
        sc.getData().add(d2);
        d2.setName("COG accessory");*/

        /*final StackedBarChart<String,Number> sc2 = new StackedBarChart<>(xAxis,yAxis);
        //sc2.labelsVisibleProperty().set( true );
        sc2.setLegendVisible(false);
        sc2.setLegendSide( Side.RIGHT );
        sc2.setVisible( true );
        sc2.setTitle("COG accessory");*/

        //Font f = sc.getXAxis().settic
        //sc.setStyle( "-fx-font-size: 2.4em;" );
        //System.err.println( sc.getXAxis().getStyle() );

       /* Map<String,Integer> countmap = new HashMap<String,Integer>();
        for( String spec : map.keySet() ) {
        	Map<Character,Integer> submap = map.get(spec);
        	int total = 0;
        	for( Character f : submap.keySet() ) {
        		total += submap.get(f);
        	}
        	countmap.put( spec, total );
        }*/

        //PieChart.Data d = new PieChart.Data();

        Map<String,XYChart.Series<String, Number>> blehmap = new HashMap<>();
        if(lowres) {
            for (String s : Cog.coggroups.keySet()) {
                XYChart.Series<String, Number> d1 = new XYChart.Series<>();
                sc.getData().add(d1);
                d1.setName(s);
                blehmap.put(s, d1);
            }
        } else {
            Cog.coggroups.values().stream().flatMap(Collection::stream).forEach(c -> {
                XYChart.Series<String, Number> d1 = new XYChart.Series<>();
                sc.getData().add(d1);
                d1.setName(c);
                blehmap.put(c, d1);
            });
        }

        mm.forEach((spc, map) -> {
            int sum = map.entrySet().stream().filter(e -> Cog.coggroups.values().stream().anyMatch(s -> s.contains(e.getKey()))).map(Map.Entry::getValue).mapToInt(i -> i).sum();
            for (String s : Cog.coggroups.keySet()) {
                Set<String> schar = Cog.coggroups.get(s);
            /*if( s.contains("METABOLISM") ) {
                for( String cogsymbol : schar ) {
                    if( cogsymbol != null ) {
                        int count = 0;
                        if( map.containsKey(cogsymbol) ) count = map.get( cogsymbol );

				        /*XYChart.Series<String,Number> core = new XYChart.Series<String,Number>();
				        int i = longname.indexOf(',', 50);
				        if( i == -1 ) i = longname.length();
				        core.setName( longname.substring(0,i) );
				        for( String spec : map.keySet() ) {
				        	Map<Character,Integer> submap = map.get(spec);
				        	//int last = 0;
				        	//for( String f : submap.keySet() ) {
				        	if( submap.containsKey(flock) ) {
				        		int total = countmap.get(spec);
					        	int ival = submap.get( flock );
					        	String fixspec = nameFix(spec);
					        	XYChart.Data<String,Number> d = uniform ?  new XYChart.Data<String,Number>( fixspec, (double)ival/(double)total ) : new XYChart.Data<String,Number>( fixspec, ival );
					        	//Tooltip.install( d.getNode(), new Tooltip( flock ) );
					        	core.getData().add( d );
				        	}

					        //last = last+ival;
				        }*
                        XYChart.Series<String,Number> d1 = new XYChart.Series<>();
                        sc.getData().add(d1);
                        d1.setName(cogsymbol);

                        XYChart.Data<String,Number> d = new XYChart.Data<>(cogsymbol/*Cog.charcog.get(cogsymbol)*, count);
                        ObservableList<XYChart.Data<String,Number>> ob = d1.getData();
                        ob.add( d );
                    }
                }
            } else {*/
                int count = 0;
                for (String cogsymbol : schar) {
                    if (cogsymbol != null) {
                        if (Cog.coggroups.values().stream().anyMatch(su -> su.contains(cogsymbol)) && map.containsKey(cogsymbol))
                            count += map.get(cogsymbol);
                        if (!lowres) {
                            XYChart.Series<String, Number> d1 = blehmap.get(cogsymbol);
                            XYChart.Data<String, Number> d = new XYChart.Data<>(spc/*Cog.charcog.get(cogsymbol)*/, count / (double) sum);
                            ObservableList<XYChart.Data<String, Number>> ob = d1.getData();
                            ob.add(d);
                        }
                    }
                }

                if (lowres) {
                    XYChart.Series<String, Number> d1 = blehmap.get(s);
                    XYChart.Data<String, Number> d = new XYChart.Data<>(spc/*Cog.charcog.get(cogsymbol)*/, count / (double) sum);
                    ObservableList<XYChart.Data<String, Number>> ob = d1.getData();
                    ob.add(d);
                }
                //}
            }
        });

        /*sum = mip.entrySet().stream().filter(e -> Cog.coggroups.values().stream().anyMatch(s -> s.contains(e.getKey()))).map(Map.Entry::getValue).mapToInt(i -> i).sum();
        for( String s : Cog.coggroups.keySet() ) {
            Set<String> schar = Cog.coggroups.get( s );
            /*if( s.contains("METABOLISM") ) {
                for( String cogsymbol : schar ) {
                    if( cogsymbol != null ) {
                        int count = 0;
                        if( mip.containsKey(cogsymbol) ) count = mip.get( cogsymbol );

                        XYChart.Series<String,Number> d1 = new XYChart.Series<>();
                        sc.getData().add(d1);
                        d1.setName(cogsymbol);

                        XYChart.Data<String,Number> d = new XYChart.Data<>(cogsymbol/*Cog.charcog.get(cogsymbol)*, count);
                        ObservableList<XYChart.Data<String,Number>> ob = d1.getData();
                        ob.add( d );
                    }
                }
            } else {*
                int count = 0;
                for( String cogsymbol : schar ) {
                    if( cogsymbol != null ) {
                        if( Cog.coggroups.values().stream().anyMatch(su -> su.contains(cogsymbol)) && mip.containsKey(cogsymbol) ) count += mip.get( cogsymbol );
                        if(!lowres) {
                            XYChart.Series<String, Number> d1 = blehmap.get(cogsymbol);
                            XYChart.Data<String, Number> d = new XYChart.Data<>("Accessory"/*Cog.charcog.get(cogsymbol)*, count / (double) sum);
                            ObservableList<XYChart.Data<String, Number>> ob = d1.getData();
                            ob.add(d);
                        }
                    }
                }

                if(lowres) {
                    XYChart.Series<String, Number> d1 = blehmap.get(s);
                    XYChart.Data<String, Number> d = new XYChart.Data<>("Accessory"/*Cog.charcog.get(cogsymbol)*, count / (double) sum);
                    ObservableList<XYChart.Data<String, Number>> ob = d1.getData();
                    ob.add(d);
                }
            //}
        }*/

        /*XYChart.Series<String,Number> pan = new XYChart.Series<String,Number>();
        pan.setName("Pan");
        //for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d = new XYChart.Data<String,Number>( "dd", 100 );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan.getData().add( d );
        //}
        XYChart.Series<String,Number> pan2 = new XYChart.Series<String,Number>();
        pan2.setName("Core");
        //for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d2 = new XYChart.Data<String,Number>( "2", 200 );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan2.getData().add( d2 );
        //}
        sc.getData().addAll(pan, pan2);*/

        hbox.getChildren().addAll( sc );
        if( scene == null ) {
            scene = new Scene( hbox );
        } else scene.setRoot( hbox );

        /*for (XYChart.Series<String, Number> s : sc.getData()) {
        	//int i = 0;
            for (XYChart.Data<String, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip( s.getName()+": "+d.getYValue() ) );
            }
        }*/

        HBox.setHgrow(sc, Priority.ALWAYS);
        //HBox.setHgrow(sc2, Priority.ALWAYS);

        sc.setBackground( Background.EMPTY );

        final ContextMenu menu = new ContextMenu();
        javafx.scene.control.MenuItem mi = new javafx.scene.control.MenuItem();
        mi.setOnAction(arg0 -> {
            WritableImage fximg = sc.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("/Users/sigmar/fximg.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.getItems().add( mi );
        sc.setOnMouseClicked(event -> {
            if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                menu.show(sc, event.getScreenX(), event.getScreenY());
            }
        });

        return scene;
    }

    private Scene createStackedBarChartScene( Map<String,String> all, Map<String,Map<String,Integer>> map, boolean uniform ) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis 	yAxis = new NumberAxis();

        xAxis.setTickLabelRotation( 90.0 );

        List<String> speclist = new ArrayList<>();
        for( String spec : map.keySet() ) {
            speclist.add( Sequence.nameFix(spec) );
        }
        xAxis.setCategories( FXCollections.observableArrayList( speclist ) );
        //yAxis.

        final StackedBarChart<String,Number> sc = new StackedBarChart<>(xAxis,yAxis);
        sc.setLegendSide( Side.RIGHT );
        xAxis.setLabel("");
        yAxis.setLabel("");
        sc.setTitle("COG catogories");

        //Font f = sc.getXAxis().settic
        //sc.setStyle( "-fx-font-size: 2.4em;" );
        //System.err.println( sc.getXAxis().getStyle() );
        sc.getXAxis().setStyle("-fx-tick-label-font-size: 1.4em;");
        sc.getYAxis().setStyle("-fx-tick-label-font-size: 1.4em;");

        Map<String,Integer> countmap = new HashMap<>();
        for( String spec : map.keySet() ) {
            Map<String,Integer> submap = map.get(spec);
            int total = 0;
            for( String f : submap.keySet() ) {
                total += submap.get(f);
            }
            countmap.put( spec, total );
        }

        for( String flock : all.keySet() ) {
            //Map<String,Integer> submap = map.get( spec );
            String longname = all.get(flock);
            XYChart.Series<String,Number> core = new XYChart.Series<>();
            int i = longname.indexOf(',', 50);
            if( i == -1 ) i = longname.length();
            core.setName( longname.substring(0,i) );
            for( String spec : map.keySet() ) {
                Map<String,Integer> submap = map.get(spec);
                //int last = 0;
                //for( String f : submap.keySet() ) {
                if( submap.containsKey(flock) ) {
                    int total = countmap.get(spec);
                    int ival = submap.get( flock );
                    String fixspec = Sequence.nameFix(spec);
                    XYChart.Data<String,Number> d = uniform ? new XYChart.Data<>(fixspec, (double) ival / (double) total) : new XYChart.Data<>(fixspec, ival);
                    //Tooltip.install( d.getNode(), new Tooltip( flock ) );
                    core.getData().add( d );
                }

                //last = last+ival;
            }
            sc.getData().add( core );
        }

        /*XYChart.Series<String,Number> pan = new XYChart.Series<String,Number>();
        pan.setName("Pan");
        //for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d = new XYChart.Data<String,Number>( "dd", 100 );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan.getData().add( d );
        //}
        XYChart.Series<String,Number> pan2 = new XYChart.Series<String,Number>();
        pan2.setName("Core");
        //for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d2 = new XYChart.Data<String,Number>( "2", 200 );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan2.getData().add( d2 );
        //}
        sc.getData().addAll(pan, pan2);*/
        if( scene == null ) {
            scene = new Scene( sc );
        } else scene.setRoot( sc );

        for (XYChart.Series<String, Number> s : sc.getData()) {
            //int i = 0;
            for (XYChart.Data<String, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip( s.getName()+": "+d.getYValue() ) );
            }
        }

        sc.setBackground( Background.EMPTY );

        final ContextMenu menu = new ContextMenu();
        javafx.scene.control.MenuItem mi = new javafx.scene.control.MenuItem();
        mi.setOnAction(arg0 -> {
            WritableImage fximg = sc.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("c:/fximg.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.getItems().add( mi );
        sc.setOnMouseClicked(event -> {
            if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                menu.show(sc, event.getScreenX(), event.getScreenY());
            }
        });

        return scene;
    }

    private static Scene createStackedBarChartScene(List<GeneSet.StackBarData> lsbd, String[] categories ) {
        final CategoryAxis 	xAxis = new CategoryAxis();
        final NumberAxis 	yAxis = new NumberAxis();

        xAxis.setTickLabelRotation( 90.0 );

        List<String>	names = new ArrayList<>();
        for( GeneSet.StackBarData sbd : lsbd ) {
            names.add( sbd.name );
        }
        xAxis.setCategories( FXCollections.observableArrayList( names ) );
        //yAxis.

        final StackedBarChart<String,Number> sc = new StackedBarChart<>(xAxis, yAxis);
        xAxis.setLabel("");
        yAxis.setLabel("");
        sc.setTitle("Pan-core genome");

        //Font f = sc.getXAxis().settic
        //sc.setStyle( "-fx-font-size: 2.4em;" );
        //System.err.println( sc.getXAxis().getStyle() );
        sc.getXAxis().setStyle("-fx-tick-label-font-size: 1.4em;");
        sc.getYAxis().setStyle("-fx-tick-label-font-size: 1.4em;");

        for( String category : categories ) {
            XYChart.Series<String,Number> core = new XYChart.Series<>();
            GeneSet.StackBarData last = lsbd.get( lsbd.size()-1 );
            int lastval = last.b.get( category );
            core.setName( category + lastval );
            for( GeneSet.StackBarData sbd : lsbd ) {
                XYChart.Data<String,Number> d = new XYChart.Data<>(sbd.name, sbd.b.get(category));
                //Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
                core.getData().add( d );
            }

            sc.getData().add( core );
        }
        /*XYChart.Series<String,Number> pan = new XYChart.Series<String,Number>();
        pan.setName("Pan: " + ydata[ydata.length-1] );
        for( int i = 0; i < ydata.length; i++ ) {
        	XYChart.Data<String,Number> d = new XYChart.Data<String,Number>( names[i], ydata[i]-xdata[i] );
        	//Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
        	pan.getData().add( d );
        }*/

        //sc.getData().addAll(core, pan);
        if( scene == null ) {
            scene = new Scene( sc );
        } else scene.setRoot( sc );

        /*for (XYChart.Series<Number, Number> s : sc.getData()) {
        	int i = 0;
            for (XYChart.Data<Number, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip( names[i++] ) );
            }
        }*/

        sc.setBackground( Background.EMPTY );

        final ContextMenu menu = new ContextMenu();
        javafx.scene.control.MenuItem mi = new javafx.scene.control.MenuItem();
        mi.setOnAction(arg0 -> {
            WritableImage fximg = sc.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("c:/fximg.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.getItems().add( mi );
        sc.setOnMouseClicked( new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                    menu.show(sc, event.getScreenX(), event.getScreenY());
                }
            }
        });

        return scene;
    }

    private static Scene createBarChartScene( String[] names, XYChart.Series<String,Number> data, String xTitle, String yTitle, double start, double stop, double step, String title ) {
        final CategoryAxis 	xAxis = new CategoryAxis();
        final NumberAxis 	yAxis = new NumberAxis( start, stop, step ); // 0.6, 0.7, 0.02
        //yAxis.set

        /*yAxis.setTickLabelFormatter( new StringConverter<Number>() {
			@Override
			public String toString(Number arg0) {
				return Double.toString( Math.round( (arg0.doubleValue() + 0.6)*100.0 )/100.0 );
			}

			@Override
			public Number fromString(String arg0) {
				return Double.parseDouble( arg0 );
			}
		});*/

        xAxis.setTickLabelRotation( 90.0 );

        xAxis.setCategories( FXCollections.observableArrayList( Arrays.asList(names) ) );
        //yAxis.

        final BarChart<String,Number> sc = new BarChart<>(xAxis, yAxis);
        xAxis.setLabel( xTitle );
        yAxis.setLabel( yTitle );
        sc.setTitle( title );

        xAxis.setStyle("-fx-tick-label-font-size: 1.4em;");
        yAxis.setStyle("-fx-tick-label-font-size: 1.4em;");

        sc.getData().addAll(data);
        if( scene == null ) {
            scene = new Scene( sc );
        } else scene.setRoot( sc );

        /*for (XYChart.Series<Number, Number> s : sc.getData()) {
        	int i = 0;
            for (XYChart.Data<Number, Number> d : s.getData()) {
                Tooltip.install( d.getNode(), new Tooltip( names[i++] ) );
            }
        }*/

        sc.setBackground( Background.EMPTY );

        final ContextMenu menu = new ContextMenu();
        javafx.scene.control.MenuItem mi = new javafx.scene.control.MenuItem();
        mi.setOnAction(arg0 -> {
            WritableImage fximg = sc.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("c:/fximg.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.getItems().add( mi );
        sc.setOnMouseClicked(event -> {
            if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
                menu.show(sc, event.getScreenX(), event.getScreenY());
            }
        });

        return scene;
    }

    private Scene createScene( String webp ) {
        //Group  root  =  new  Group();
        final WebView wv = new WebView();
        wv.setPrefSize(1600, 900);
        //wv.

        WebEngine we = wv.getEngine();
        we.loadContent( webp );

        javafx.scene.control.ScrollPane sp = new javafx.scene.control.ScrollPane();
        sp.setPrefViewportWidth(1600);
        sp.setPrefViewportHeight(900);
        sp.setContent( wv );
        final Scene  scene = new  Scene(sp);
		/*we.setOnResized(
	        new EventHandler<WebEvent<Rectangle2D>>() {
	            public void handle(WebEvent<Rectangle2D> ev) {
	                Rectangle2D r = ev.getData();

	                System.err.println( r.getWidth() + "  " + r.getHeight() );
	                //stage.setWidth(r.getWidth());
	                //stage.setHeight(r.getHeight());
	                scene.getWindow().setWidth( r.getWidth() );
	                scene.getWindow().setHeight( r.getHeight() );
	            }
		 });*/

        final ContextMenu menu = new ContextMenu();
        javafx.scene.control.MenuItem mi = new MenuItem("Save");
        mi.setOnAction(arg0 -> {
            SnapshotParameters sp1 = new SnapshotParameters();
            //sp.
            WritableImage fximg = wv.snapshot(sp1, null );
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(fximg, null), "png", new File("/Users/sigmar/Desktop/chart.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.getItems().add( mi );
        scene.setOnMouseClicked(event -> {
            //if (javafx.scene.input.MouseButton.SECONDARY.equals(event.getButton())) {
            menu.show(wv, event.getScreenX(), event.getScreenY());
            //}
        });
		/*File file = new File("/Users/sigmar/Desktop/chart.png");
		WritableImage wi = wv.snapshot( new SnapshotParameters(), null );
		try {
		    ImageIO.write(SwingFXUtils.fromFXImage(wi, null), "png", file);
		} catch (IOException e) {}*/

        /*Text  text  =  new  Text();
        text.setX(40);
        text.setY(100);
        text.setFont(new Font(25));
        text.setText("Welcome JavaFX!");
        root.getChildren().add(text);*/
        return (scene);
    }

    public void initFX(JFXPanel fxPanel, String webp) {
        Scene scene = createScene( webp );
        fxPanel.setScene(scene);
    }

    public void initFXChart( JFXPanel fxPanel, String[] names, double[] xdata, double[] ydata ) {
        Scene scene = createScene( names, xdata, ydata );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }

    public void initWebPage(Stage stage, String webp ) {
        Scene scene = createScene( webp );
        if( stage != null ) stage.setScene(scene);
    }

    public void initDualPieChart(JFXPanel fxPanel, Map<String,Integer> map, Map<String,Integer> mip ) {
        Scene scene = createDualPieChartScene( map, mip );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }

    public void initDualStackedBarChart(JFXPanel fxPanel, Map<String,Map<String,Integer>> map, boolean lowres) throws IOException {
        Scene scene = createDualStackedBarChartScene( map, lowres );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }

    public void initStackedBarChart( Stage stage, Map<String,String> all, Map<String,Map<String,Integer>> map, boolean uniform ) {
        Scene scene = createStackedBarChartScene( all, map, uniform );
        if( stage != null ) stage.setScene(scene);
    }

    public void initStackedBarChart(JFXPanel fxPanel, List<GeneSet.StackBarData> lsbd, String[] categories ) {
        Scene scene = createStackedBarChartScene( lsbd, categories );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }

    public void initBarChart( JFXPanel fxPanel, String[] names, int[] xdata, String xTitle, String yTitle, double start, double stop, double step, String title ) {
        XYChart.Series<String,Number> data = new XYChart.Series<>();
        //core.setName("Core: " + xdata[xdata.length-1] );
        for( int i = 0; i < xdata.length; i++ ) {
            XYChart.Data<String,Number> d = new XYChart.Data<>(names[i], xdata[i]);
            //Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
            data.getData().add( d );
        }

        Scene scene = createBarChartScene( names, data, xTitle, yTitle, start, stop, step, title );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }

    public void initBarChart( JFXPanel fxPanel, String[] names, double[] xdata, String xTitle, String yTitle, double start, double stop, double step, String title ) {
        XYChart.Series<String,Number> data = new XYChart.Series<>();
        //core.setName("Core: " + xdata[xdata.length-1] );
        for( int i = 0; i < xdata.length; i++ ) {
            String name = names[i];
            double dval = xdata[i];
            XYChart.Data<String,Number> d = new XYChart.Data<>(name, dval);
            //Tooltip.install( d.getNode(), new Tooltip( names[i] ) );
            data.getData().add( d );
        }

        Scene scene = createBarChartScene( names, data, xTitle, yTitle, start, stop, step, title );
        if( fxPanel != null ) fxPanel.setScene(scene);
    }
}
