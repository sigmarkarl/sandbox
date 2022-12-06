package org.simmi.distann;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FunctionPopup extends ContextMenu {

    GeneSetHead geneSetHead;
    public FunctionPopup(GeneSetHead geneSetHead) {
        this.geneSetHead = geneSetHead;
    }
    public void init() {
        var fpopup = this;
        MenuItem amigo = new MenuItem("Amigo lookup");
        amigo.setOnAction( e -> {
            String go = geneSetHead.ftable.getSelectionModel().getSelectedItem().getGo();
            try {
                // GeneSetHead.this.getAppletContext().
                Desktop.getDesktop().browse(new URI("http://amigo.geneontology.org/amigo/medial_search?q=" + go));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        });
        fpopup.getItems().add( amigo );
        MenuItem keggl = new MenuItem("KEGG lookup");
        keggl.setOnAction( e -> {
            String kegg = geneSetHead.ftable.getSelectionModel().getSelectedItem().getKegg();
            try {
                Desktop.getDesktop().browse(new URI("http://www.genome.jp/dbget-bin/www_bget?rn:" + kegg));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        });
        fpopup.getItems().add( keggl );
        MenuItem ecl = new MenuItem("EC lookup");
        ecl.setOnAction( e -> {
            String ec = geneSetHead.ftable.getSelectionModel().getSelectedItem().getEc();
            try {
                Desktop.getDesktop().browse(new URI("http://enzyme.expasy.org/EC/" + ec));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        });
        fpopup.getItems().add( ecl );
        fpopup.getItems().add( new SeparatorMenuItem() );

        MenuItem excelreport = new MenuItem("Excel report");
        excelreport.setOnAction( e -> {
			/*Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("enzyme");
			int k = 0;
			for( Function f : ftable.getSelectionModel().getSelectedItems() ) {
				//String ec = (String)ftable.getValueAt(r, 1);
				//String go = (String)ftable.getValueAt(r, 0);

				//int i = ftable.getSelectionModel().convertRowIndexToModel(r);
				//Function f = geneset.funclist.get(i);
				for( GeneGroup gg : f.getGeneGroups() ) {
					for( String spec : gg.getSpecies() ) {
						Teginfo ti = gg.getGenes(spec);

						Row row = sheet.createRow(k++);
						org.apache.poi.ss.usermodel.Cell ecell = row.createCell(0);
						ecell.setCellValue( "EC:"+f.getEc() );
						org.apache.poi.ss.usermodel.Cell ncell = row.createCell(1);
						ncell.setCellValue( f.getName() );
						org.apache.poi.ss.usermodel.Cell spell = row.createCell(2);
						spell.setCellValue( spec );
						org.apache.poi.ss.usermodel.Cell seqcell = row.createCell(3);
						seqcell.setCellValue( ti.tset.size() );
					}
					/*for( Gene g :gg.genes ) {
						Row 	row = sheet.createRow(k++);
						Cell 	ecell = row.createCell(0);
						ecell.setCellValue( "EC:"+f.ec );
						Cell 	ncell = row.createCell(1);
						ncell.setCellValue( f.name );
						Cell 	spell = row.createCell(2);
						spell.setCellValue( g.getSpecies() );
						Cell 	seqcell = row.createCell(3);
						seqcell.setCellValue( g.tegeval.getAlignedSequence().toString() );
					}*
				}
				sheet.createRow(k++);
			}

			try {
				Path tempfile = Files.createTempFile("enzyme",".xlsx");
				OutputStream os = Files.newOutputStream( tempfile );
				workbook.write( os );
				os.close();

				Desktop.getDesktop().open( tempfile.toFile() );
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}*/
        });
        fpopup.getItems().add( excelreport );
    }
}
