package org.simmi.distann;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.simmi.javafasta.DataTable;
import org.simmi.javafasta.shared.*;
import org.simmi.javafasta.unsigned.JavaFasta;
import org.simmi.treedraw.shared.TreeUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GeneSetPopup extends ContextMenu {
    GeneSetHead geneSetHead;
    public GeneSetPopup(GeneSetHead geneSetHead) {
        this.geneSetHead = geneSetHead;
    }

    Set<GeneGroup> updateSplit(GeneGroup gg, double d, double l) {
        Set<GeneGroup> ggmap = new HashSet<>();
        Map<String,Integer> blosumMap = JavaFasta.getBlosumMap( true );
        for( Annotation a : gg.genes ) {
            if( ggmap.stream().flatMap( f -> f.genes.stream() ).noneMatch( p -> a == p ) ) {
                Set<Annotation> ggset = new HashSet<>();
                Sequence seq1 = a.getAlignedSequence();
                for (Annotation ca : gg.genes) {
                    Sequence seq2 = ca.getAlignedSequence();

                    int[] tscore = GeneCompare.blosumValue(seq1, seq1, seq2, blosumMap);
                    int sscore = GeneCompare.blosumValue(seq1, seq2, blosumMap);

                    double dval = (double) (sscore - tscore[1]) / (double) (tscore[0] - tscore[1]);

					/*int[] basescore_count = GeneCompare.blosumValueCount(seq1, seq1, seq2, blosumMap);
					int[] score_count = GeneCompare.blosumValueCount(seq1, seq2, blosumMap);
					int tscore = basescore_count[0];
					int sscore = score_count[0];
					int scount = score_count[1];
					double dval = (double) (sscore) / (double) (tscore);
					double lval = (double)scount/(double)seq1.getUnalignedLength();
					if (dval > d && lval > l) {
						ggset.add(ca);
					}*/

                    if (dval > d) {
                        ggset.add(ca);
                    }
                }
                System.err.println( ggset.size() );

                Set<GeneGroup> osubgg = ggmap.stream().filter( f -> {
                    Set<Annotation> gs = new HashSet<>(ggset); gs.retainAll(f.genes); return gs.size() > 0;
                }).collect(Collectors.toSet());
                GeneGroup subgg;
                if( osubgg.size() > 0 ) {
                    Iterator<GeneGroup> git = osubgg.iterator();
                    subgg = git.next();
                    while( git.hasNext() ) {
                        GeneGroup remgg = git.next();
                        subgg.addGenes( remgg.genes );
                        ggmap.remove( remgg );
                    }
                } else {
                    subgg = new GeneGroup();
                    subgg.setCogMap( gg.getCogMap() );
                    subgg.setKonameMap( gg.getKonameMap() );
                    subgg.setSpecSet( gg.getSpecSet() );
                    ggmap.add( subgg );
                }
                subgg.addGenes( ggset );
            }
        }
        Set<GeneGroup> sgg = new HashSet<>(ggmap);
        return sgg;
    }

    public void saveGeneGroups(List<GeneGroup> gg, List<GeneGroup> c) {
        Map<String,String> env = new HashMap<>();
        env.put("create", "true");
        try(var zfsystem = FileSystems.newFileSystem( geneSetHead.geneset.zipuri, env )) {
            //geneset.zipfilesystem = zfsystem;
            for( Path root : zfsystem.getRootDirectories() ) {
                try(var fstream = Files.walk(root)) {
                    fstream.filter(f -> f.toString().startsWith("/aligned"))
                            //.filter( f -> f.toString().endsWith(".aa") )
                            .filter(f -> {
                                String filename = f.getFileName().toString();
                                return gg.stream().flatMap(ggg -> ggg.genes.stream()).anyMatch(g -> g.getId().equals(filename));
                            }).forEach(p -> {
                                try {
                                    Files.deleteIfExists(p);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            });
                }
						/*for( Gene g : gg.genes ) {
							if( g.keggpathway != null ) {
								String sub = g.keggpathway.substring(0,3);
								Path subf = root.resolve(sub);
								if( Files.exists(subf) ) {
									String[] split = g.keggpathway.split(" ");
									for( String s : split ) {
										Path pimg = subf.resolve(s+".png");
										if( Files.exists(pimg) ) {
											showKeggPathway( sub, pimg );
										}
									}
								}
							}
						}*/
                final Path p = root.resolve("/aligned");
                for (GeneGroup fgg : c) {
                    Path np = p.resolve(fgg.genes.iterator().next().getId());
                    try (Writer w = Files.newBufferedWriter(np)) {
                        fgg.getFasta(w, false);
                    }
                }

                var clustersPath = root.resolve("simpleclusters.txt");
                try(var fos = Files.newBufferedWriter(clustersPath, StandardOpenOption.TRUNCATE_EXISTING)) {
                    var gset = geneSetHead.geneset.allgenegroups.stream().map(ggg -> ggg.genes.stream().map(Annotation::getId).collect(Collectors.toSet()));
                    geneSetHead.geneset.writeClusters(fos, gset);
                }
                break;
            }
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void saveSimpleClusters() {
        Map<String,String> env = new HashMap<>();
        env.put("create", "true");
        try(var zfsystem = FileSystems.newFileSystem( geneSetHead.geneset.zipuri, env )) {
            //geneset.zipfilesystem = zfsystem;
            for( Path root : zfsystem.getRootDirectories() ) {
                var clustersPath = root.resolve("simpleclusters.txt");
                try(var fos = Files.newBufferedWriter(clustersPath, StandardOpenOption.TRUNCATE_EXISTING)) {
                    var gset = geneSetHead.geneset.allgenegroups.stream().map(ggg -> ggg.genes.stream().map(Annotation::getId).collect(Collectors.toSet()));
                    geneSetHead.geneset.writeClusters(fos, gset);
                }
                break;
            }
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void showKeggPathway( String sub, Path p ) {
        SwingUtilities.invokeLater(() -> {
            try {
                final BufferedImage selimg = ImageIO.read(Files.newInputStream(p));
                if (selimg != null) {
                    JFrame frame = new JFrame(sub);
                    frame.setSize(800, 600);
                    final JComponent c = new JComponent() {
                        public void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            g.drawImage(selimg, 0, 0, this);
                        }
                    };
                    Dimension dim = new Dimension(selimg.getWidth(), selimg.getHeight());
                    c.setSize(dim);
                    c.setPreferredSize(dim);
                    JScrollPane sc2 = new JScrollPane(c);
                    frame.add(sc2);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setVisible(true);
                }
            } catch( Exception e ) {
                e.printStackTrace();
            }
        });
    }

    public void init() {
        ContextMenu popup = this;
        MenuItem splitaction = new MenuItem("Split");
        splitaction.setOnAction( e -> {
            Dialog<List<GeneGroup>> dialog = new Dialog<>();
            dialog.setResizable( true );

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 20, 10, 10));

            TextField len = new TextField();
            len.setPromptText("0.5");
            TextField id = new TextField();
            id.setPromptText("0.5");

            grid.add(new Label("%Length:"), 0, 0);
            grid.add(len, 1, 0);
            grid.add(new Label("%Identity:"), 0, 1);
            grid.add(id, 1, 1);

            final ListView<GeneGroup> list = new ListView<>();
            list.setPrefWidth(400);
            grid.add(list, 0, 2, 2, 1);

            final ObservableList<GeneGroup> gg = geneSetHead.table.getSelectionModel().getSelectedItems();
            list.setItems( gg );

            Label groupsize = new Label(""/*+gg.genes.size()*/);
            grid.add(groupsize, 0, 3, 2, 1);

            id.textProperty().addListener((observable, oldValue, newValue) -> {
                if( !newValue.equals(oldValue) ) {
                    boolean failed = false;
                    double d = 0;
                    double l = 0;
                    try {
                        d = Double.parseDouble(newValue);
                        l = Double.parseDouble(newValue);
                    } catch( Exception ex ) { failed = true; }

                    if( !failed && l > 0 ) {
                        List<GeneGroup> lgg = new ArrayList<>();
                        for(GeneGroup ggg : gg) {
                            Set<GeneGroup> sgg = updateSplit(ggg, d, l);
                            lgg.addAll(sgg);
                        }

                        list.setItems(FXCollections.observableList(lgg));
                        dialog.setResultConverter(param -> lgg);
                    }
                }
            });

			/*len.textProperty().addListener((observable, oldValue, newValue) -> {
				if( !newValue.equals(oldValue) ) {
					boolean failed = false;
					double d = 0;
					double l = 0;
					try {
						d = Double.parseDouble(id.getText());
						l = Double.parseDouble(newValue);
					} catch( Exception ex ) { failed = true; }

					if( !failed && l > 0 ) {
						Set<GeneGroup> sgg = updateSplit(gg, d, l);
						List<GeneGroup> lgg = new ArrayList<>(sgg);
						list.setItems(FXCollections.observableList(lgg));
						dialog.setResultConverter(param -> sgg);
					}
				}
			});*/

            dialog.getDialogPane().setContent( grid );
            dialog.getDialogPane().getButtonTypes().add( ButtonType.OK );
            dialog.getDialogPane().getButtonTypes().add( ButtonType.CANCEL );
            Optional<List<GeneGroup>> ogg = dialog.showAndWait();

            ogg.ifPresent( c -> {
                geneSetHead.geneset.allgenegroups.removeAll(gg);
                geneSetHead.geneset.allgenegroups.addAll( c );

                saveGeneGroups(gg, c);
            });
        });
        popup.getItems().add( splitaction );
        MenuItem joinaction = new MenuItem("Join");
        joinaction.setOnAction(event -> {
            final ObservableList<GeneGroup> ogg = geneSetHead.table.getSelectionModel().getSelectedItems();
            var firstGG = ogg.get(0);
            for (int i = 1; i < ogg.size(); i++) {
                var ngg = ogg.get(i);
                firstGG.addGenes(ngg.genes);
                geneSetHead.geneset.allgenegroups.remove(ngg);
            }
            saveSimpleClusters();
        });
        popup.getItems().add( joinaction );

        MenuItem mergejoinaction = new MenuItem("Merge join");
        mergejoinaction.setOnAction(event -> {
            final ObservableList<GeneGroup> ogg = geneSetHead.table.getSelectionModel().getSelectedItems();
            var firstGG = ogg.get(0);
            for (int i = 1; i < ogg.size(); i++) {
                var ngg = ogg.get(i);
                firstGG.mergeAnnotations(ngg.genes);
                geneSetHead.geneset.allgenegroups.remove(ngg);
            }
            saveSimpleClusters();
        });
        popup.getItems().add( mergejoinaction );

        popup.getItems().add( new SeparatorMenuItem() );
        MenuItem showkegg = new MenuItem("Show KEGG pathway");
        showkegg.setOnAction( e -> {
            GeneGroup gg = geneSetHead.table.getSelectionModel().getSelectedItem();

            Map<String,String> env = new HashMap<>();
            env.put("create", "true");

			/*String uristr = "jar:" + geneset.zippath.toUri();
			URI zipuri = URI.create( uristr /*.replace("file://", "file:")* );
			final List<Path>	lbi = new ArrayList<>();*/
            boolean shown = false;
            try {
                geneSetHead.geneset.zipfilesystem = FileSystems.newFileSystem( geneSetHead.geneset.zipuri, env );
                for( Path root : geneSetHead.geneset.zipfilesystem.getRootDirectories() ) {
                    for( Annotation a : gg.genes ) {
                        Gene g = a.getGene();
                        if( g.keggpathway != null ) {
                            String sub = g.keggpathway.substring(0,3);
                            Path subf = root.resolve(sub);
                            if( Files.exists(subf) ) {
                                String[] split = g.keggpathway.split(" ");
                                for( String s : split ) {
                                    Path pimg = subf.resolve(s+".png");
                                    if( Files.exists(pimg) ) {
                                        showKeggPathway( sub, pimg );
                                        shown = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                geneSetHead.geneset.zipfilesystem.close();
            } catch( Exception ex ) {
                ex.printStackTrace();
            }

            if( !shown ) {
                for( Annotation a : gg.genes ) {
                    Gene g = a.getGene();
                    if (g != null && g.keggpathway != null) {
                        String[] keggsplit = g.keggpathway.split(";");
                        Arrays.stream(keggsplit).map( s -> s.split(":")[0] ).findFirst().ifPresent(c -> {
                            try {
                                Desktop.getDesktop().browse(URI.create("http://www.genome.jp/dbget-bin/www_bget?map" + c.substring(2)));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        });
                    }
                }
            }
        });
        popup.getItems().add( showkegg );
        MenuItem plasmid = new MenuItem("Plasmid");
        plasmid.setOnAction( e -> {
            Gene g = geneSetHead.gtable.getSelectionModel().getSelectedItem();
            Sequence contig = g.getTegeval().getContshort();
            String contigstr = contig.toString();
            contig.setPlasmid( !geneSetHead.geneset.plasmids.contains( contigstr ) );
            if( contig.isPlasmid() ) geneSetHead.geneset.plasmids.add( contigstr );
            else geneSetHead.geneset.plasmids.remove( contigstr );

            try {
                Map<String,String> env = new HashMap<>();
                env.put("create", "true");
                //Path path = zipfile.toPath();
                String uristr = "jar:" + geneSetHead.geneset.zippath.toUri();
                geneSetHead.geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
                geneSetHead.geneset.zipfilesystem = FileSystems.newFileSystem( geneSetHead.geneset.zipuri, env );

                //fs = FileSystems.newFileSystem( uri, env );
                //FileSystem fs = FileSystems.newFileSystem(uri, env);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for( String contigname : geneSetHead.geneset.plasmids ) {
                    baos.write( (contigname + "\n").getBytes() );
                }

                Path nf = geneSetHead.geneset.zipfilesystem.getPath("/plasmids.txt");
                long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING );
                //System.err.println( "eeerm " + bl );
                geneSetHead.geneset.zipfilesystem.close();

			    /*Writer writer = Files.newBufferedWriter(nf, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
			    for( String phage : phageset ) {
			    	writer.write( phage + "\n" );
			    }
			    writer.close();*/


                //writer.write("hello");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        popup.getItems().add( plasmid );
        MenuItem designate = new MenuItem("Designate");
        designate.setOnAction( e -> { SwingUtilities.invokeLater(() -> {
            JComboBox<String>	descombo = new JComboBox<>( geneSetHead.geneset.deset.toArray(new String[0]) );
            descombo.setEditable( true );
            JOptionPane.showMessageDialog(null, descombo);
            String val = Objects.requireNonNull(descombo.getSelectedItem()).toString();
            geneSetHead.geneset.deset.add( val );
            if(geneSetHead.isGeneview()) {
                for (Gene g : geneSetHead.gtable.getSelectionModel().getSelectedItems()) {
                    g.getTegeval().designation = val;
                    if (g.id != null) {
                        geneSetHead.geneset.designations.put(g.id, val);
                    } else {
                        System.err.println(g.getRefid());
                    }
                    //ta.append( g.tegeval.id + "\n" );
                }
            } else {
                for (GeneGroup gg : geneSetHead.table.getSelectionModel().getSelectedItems()) {
                    gg.genes.stream().filter(g -> g.getId() != null).forEach(g -> {
                        geneSetHead.geneset.designations.put(g.getId(), val);
                        if(g.getGene()!=null && g.getGene().getTegeval()!=null) g.getGene().getTegeval().designation = val;
                    });
                }
            }

            try {
                Map<String,String> env = new HashMap<>();
                env.put("create", "true");
                //Path path = zipfile.toPath();
                String uristr = "jar:" + geneSetHead.geneset.zippath.toUri();
                geneSetHead.geneset.zipuri = URI.create( uristr /*.replace("file://", "file:")*/ );
                geneSetHead.geneset.zipfilesystem = FileSystems.newFileSystem( geneSetHead.geneset.zipuri, env );

                //fs = FileSystems.newFileSystem( uri, env );
                //FileSystem fs = FileSystems.newFileSystem(uri, env);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for( String geneid : geneSetHead.geneset.designations.keySet() ) {
                    String design = geneSetHead.geneset.designations.get( geneid );
                    baos.write( (geneid + "\t" + design + "\n").getBytes() );
                }

                Path nf = geneSetHead.geneset.zipfilesystem.getPath("/designations.txt");
                long bl = Files.copy( new ByteArrayInputStream( baos.toByteArray() ), nf, StandardCopyOption.REPLACE_EXISTING);
                //System.err.println( "eeerm " + bl );
                geneSetHead.geneset.zipfilesystem.close();

                /*Writer writer = Files.newBufferedWriter(nf, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
                for( String phage : phageset ) {
                    writer.write( phage + "\n" );
                }
                writer.close();*/


                //writer.write("hello");
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            /*JFrame frame = new JFrame("Ids");
            frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
            frame.setSize(800, 600);
            JTextArea	ta = new JTextArea();
            JScrollPane sp = new JScrollPane( ta );
            frame.add( sp );

            frame.setVisible( true );*/
        });
        });
        popup.getItems().add( designate );
        MenuItem koname = new MenuItem("KO to name");
        koname.setOnAction( e -> {
            Set<String>	koids = new HashSet<>();
            for( Gene g : geneSetHead.geneset.genelist ) {
                if( g.koid != null && g.koid.length() > 0 && !(geneSetHead.geneset.ko2name != null && geneSetHead.geneset.ko2name.containsKey( g.koid )) ) koids.add( g.koid );
            }

            try {
                Map<String,String>	ko2name = new HashMap<>();
                int cnt = 0;
                for( String koid : koids ) {
                    URL url = new URL("http://www.kegg.jp/dbget-bin/www_bget?ko:"+koid);
                    InputStream is0 = url.openStream();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br0 = new BufferedReader( new InputStreamReader(is0) );
                    String line0 = br0.readLine();
                    while( line0 != null ) {
                        sb.append( line0 );
                        line0 = br0.readLine();
                    }
                    br0.close();

                    int i = sb.indexOf("<nobr>Name</nobr>");
                    if( i != -1 ) {
                        int k = sb.indexOf(":hidden\">");
                        if( k != -1 ) {
                            k = sb.indexOf(":hidden\">", k+9);
                            if( k != -1 ) {
                                String koname0 = sb.substring(k + 9, sb.indexOf("<br>", k));
                                ko2name.put(koid, koname0);

                                System.err.println(koid + "\t" + koname0);
                            }
                        }
                    }

                    System.err.println( ko2name.size() + " " + koids.size() );
                    //if( cnt++ > 20 ) break;
                }

                FileWriter fw = new FileWriter("~ko2name.txt");
                for( String koid : ko2name.keySet() ) {
                    fw.write( koid + "\t" + ko2name.get(koid) + "\n" );
                }
                fw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        popup.getItems().add( koname );
        popup.getItems().add( new SeparatorMenuItem() );
        MenuItem genegainloss = new MenuItem("Gene gain/loss");
        genegainloss.setOnAction( e -> {
            Map<TreeUtil.Node,List<GeneGroup>>	nodeGainMap = new HashMap<>();
            Map<TreeUtil.Node,List<GeneGroup>>	nodeLossMap = new HashMap<>();

			/*String treestr = "";
			JFileChooser fc = new JFileChooser();
			if( fc.showOpenDialog( applet ) == JFileChooser.APPROVE_OPTION ) {
				File file = fc.getSelectedFile();
				try {
					byte[] bb = Files.readAllBytes( Paths.get(file.toURI()) );
					treestr = new String( bb );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}*/

            Serifier serifier = geneSetHead.getConcatenatedSequences( false, true );
            String 		tree = serifier.getFastTree( serifier.lseq, geneSetHead.geneset.user, false );

            org.simmi.treedraw.shared.TreeUtil tu = new org.simmi.treedraw.shared.TreeUtil();
            TreeUtil.Node n = tu.parseTreeRecursive( tree, false );

            TableModel model = new TableModel() {
                @Override
                public int getRowCount() {
                    return geneSetHead.geneset.getSpecies().size();
                }

                @Override
                public int getColumnCount() {
                    return 1;
                }

                @Override
                public String getColumnName(int columnIndex) {
                    return null;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return String.class;
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }

                @Override
                public Object getValueAt(int rowIndex, int columnIndex) {
                    return geneSetHead.geneset.getSpecies().get( rowIndex );
                }

                @Override
                public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

                @Override
                public void addTableModelListener(TableModelListener l) {}

                @Override
                public void removeTableModelListener(TableModelListener l) {}
            };
            JTable table = new JTable( model );
            table.getSelectionModel().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
            JScrollPane	scroll = new JScrollPane( table );
            FlowLayout flowlayout = new FlowLayout();
            JComponent c = new JComponent() {};
            c.setLayout( flowlayout );
            c.add( scroll );
            JOptionPane.showMessageDialog(geneSetHead.comp, c);

            List<String>	rootgroup = new ArrayList<>();
            int[] rr = table.getSelectedRows();
            for( int r : rr ) {
                rootgroup.add( (String)table.getValueAt(r, 0) );
            }

            //String[] sobj = {"mt.ruber", "mt.silvanus", "o.profundus", "m.hydrothermalis"};
            TreeUtil.Node newnode = tu.getParent( n, new HashSet<>( rootgroup ) );
            tu.rerootRecur( n, newnode );

            File f = new File("/home/sigmar/gain_list.txt");
            try {
                PrintStream ps = new PrintStream( f );
                geneSetHead.geneset.assignGain( newnode, nodeGainMap, ps );
                ps.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

            f = new File("/home/sigmar/loss_list.txt");
            try {
                PrintStream ps = new PrintStream( f );
                geneSetHead.geneset.assignLoss( newnode, nodeLossMap, ps );
                ps.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        popup.getItems().add( genegainloss );
        MenuItem concattree = new MenuItem("Concatenate tree");
        concattree.setOnAction( e -> {
            Serifier serifier = geneSetHead.getConcatenatedSequences( false, true );

			/*if( !succ ) {
				String 				tree = serifier.getFastTree();
				if( cs.connections().size() > 0 ) {
		    		cs.sendToAll( tree );
		    	} else if( Desktop.isDesktopSupported() ) {
		    		cs.message = tree;
		    		//String uristr = "http://webconnectron.appspot.com/Treedraw.html?tree="+URLEncoder.encode( tree, "UTF-8" );
		    		String uristr = "http://webconnectron.appspot.com/Treedraw.html?ws=127.0.0.1:8887";
					try {
						Desktop.getDesktop().browse( new URI(uristr) );
					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
		    	}
				System.err.println( tree );
			}*/
            geneSetHead.showAlignedSequences( geneSetHead.comp, serifier );
        });
        popup.getItems().add( concattree );
        MenuItem majocons = new MenuItem("Majority rule consensus");
        majocons.setOnAction( e -> {
            Serifier serifier = new Serifier();

            Set<GeneGroup>	genegroups = new HashSet<>();
            if( !geneSetHead.isGeneview() ) {
                genegroups.addAll( geneSetHead.table.getSelectionModel().getSelectedItems() );
            } else {
                for (Gene gg : geneSetHead.gtable.getSelectionModel().getSelectedItems()) {
                    genegroups.add( gg.getGeneGroup() );
                }
            }

            org.simmi.treedraw.shared.TreeUtil treeutil = new org.simmi.treedraw.shared.TreeUtil();
            Map<Set<String>, TreeUtil.NodeSet> nmap = new HashMap<>();
            for( GeneGroup ggroup : genegroups ) {
                //List<Sequence>	seqlist = new ArrayList<Sequence>();

                for( Annotation tv : ggroup.getTegevals() ) {
                    String spec = tv.getContshort().getSpec();
                    Sequence seq = tv.getAlignedSequence();

                    //Sequence seq = new Sequence( spec, null );
                    //if( seqstr != null && seqstr.length() > 0 ) seq.append( seqstr );
                    serifier.addSequence( seq );
                }

                String tree = serifier.getFastTree( serifier.lseq, GeneSet.user, false );
                TreeUtil.Node n = treeutil.parseTreeRecursive( tree, false );
                treeutil.setLoc( 0 );
                n.nodeCalcMap( nmap );
            }

            TreeUtil.Node guidetree = null;

            /*********************************** Serifier serifier = getConcatenatedSequences();
             String tree = serifier.getFastTree();
             guidetree = treeutil.parseTreeRecursive( tree, false );*/

            TreeUtil.Node root = DataTable.majoRuleConsensus(treeutil, nmap, guidetree, false);
            String tree = root.toString();

            if( geneSetHead.geneset.cs.getConnections().size() > 0 ) {
                geneSetHead.geneset.cs.sendToAll( tree );
            } else if( Desktop.isDesktopSupported() ) {
                geneSetHead.geneset.cs.message = tree;
                //String uristr = "http://webconnectron.appspot.com/Treedraw.html?tree="+URLEncoder.encode( tree, "UTF-8" );
                String uristr = "http://webconnectron.appspot.com/Treedraw.html?ws=127.0.0.1:8887";
                try {
                    Desktop.getDesktop().browse( new URI(uristr) );
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        popup.getItems().add( majocons );
        MenuItem addsim = new MenuItem("Add similar");
        addsim.setOnAction( e -> {
			/*int r = table.getSelectedRow();
			int c = table.getSelectedColumn();

			Object o = table.getValueAt(r, c);

			if (c >= 18) {
				for (int i = 0; i < table.getRowCount(); i++) {
					Object no = table.getValueAt(i, c);
					if (no != null && !table.isRowSelected(i))
						table.addRowSelectionInterval(i, i);
				}
			} else {
				for (int i = 0; i < table.getRowCount(); i++) {
					Object no = table.getValueAt(i, c);
					if (o.equals(no) && !table.isRowSelected(i))
						table.addRowSelectionInterval(i, i);
				}
			}*/
        });
        popup.getItems().add( addsim );
        MenuItem selsim = new MenuItem("Select similar");
        selsim.setOnAction( e -> {
			/*int r = table.getSelectedRow();
			int c = table.getSelectedColumn();

			Object o = table.getValueAt(r, c);

			table.removeRowSelectionInterval(0, table.getRowCount() - 1);
			if (c >= 18) {
				for (int i = 0; i < table.getRowCount(); i++) {
					Object no = table.getValueAt(i, c);
					if (no != null)
						table.addRowSelectionInterval(i, i);
				}
			} else {
				for (int i = 0; i < table.getRowCount(); i++) {
					Object no = table.getValueAt(i, c);
					if (o.equals(no))
						table.addRowSelectionInterval(i, i);
				}
			}*/
        });
        popup.getItems().add( selsim );
        MenuItem tabtxt = new MenuItem("Table text");
        tabtxt.setOnAction( e -> {
			/*JTextArea ta = new JTextArea();
			ta.setDragEnabled(true);
			JScrollPane scrollpane = new JScrollPane(ta);

			StringBuilder sb = new StringBuilder();
			int[] rr = table.getSelectedRows();
			for (int r : rr) {
				for (int c = 0; c < table.getColumnCount() - 1; c++) {
					Object o = table.getValueAt(r, c);
					if (c > 18) {
						if (o != null) {
							String val = o.toString();
							int k = val.indexOf(' ');
							sb.append(val.substring(0, k));
							sb.append("\t" + val.substring(k + 1));
						} else
							sb.append("\t");
					} else {
						if (o != null) {
							sb.append(o.toString());
						}
					}
					sb.append("\t");
				}
				Object o = table.getValueAt(r, table.getColumnCount() - 1);
				if (o != null) {
					String val = o.toString();
					int k = val.indexOf(' ');
					sb.append(val.substring(0, k));
					sb.append("\t" + val.substring(k + 1));
				} else
					sb.append("\t");
				sb.append("\n");
			}

			ta.setText(sb.toString());
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.add(scrollpane);
			frame.setSize(400, 300);
			frame.setVisible(true);*/
        });
        popup.getItems().add( tabtxt );
        popup.getItems().add( new SeparatorMenuItem() );
        MenuItem ncbil = new MenuItem("NCBI lookup");
        ncbil.setOnAction( e -> {
			/*int r = table.getSelectedRow();
			if (r >= 0) {
				String ref = (String) table.getValueAt(r, 2);
				try {
					Desktop.getDesktop().browse(new URI("http://www.ncbi.nlm.nih.gov/gene?term=" + ref));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}*/
        });
        popup.getItems().add( ncbil );
    }
}
