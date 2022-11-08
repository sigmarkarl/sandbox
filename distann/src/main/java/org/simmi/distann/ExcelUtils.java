package org.simmi.distann;

import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simmi.javafasta.shared.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class ExcelUtils {

    public static void excelCenterExport( GeneSetHead gs, String sheetname, boolean sequences ) {
        SwingUtilities.invokeLater(() -> {
            var ss = new Set[1];
            ss[0] = gs.getSelspec(gs, gs.geneset.getSpecies());
            Platform.runLater(() -> {
                var selspec = ss[0];
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet(sheetname);
                Row header = sheet.createRow(0);
                if (gs.isGeneview()) {

                } else {
                    int r = gs.table.getSelectionModel().getSelectedIndex();
                    var gg = gs.results.getItems().get(r);

                    int cn = 0;
                    org.apache.poi.ss.usermodel.Cell cell = header.createCell(cn++);
                    cell.setCellValue("Pan-number");
                    var lspec = new ArrayList<>(gg.getSpecies());
                    for (String spec : lspec) {
                        cell = header.createCell(cn++);
                        cell.setCellValue(spec);
                    }
                    for (String spec : lspec) {
                        cell = header.createCell(cn++);
                        cell.setCellValue(spec);
                    }
                    for (String spec : lspec) {
                        cell = header.createCell(cn++);
                        cell.setCellValue(spec);
                    }
                    var lann = lspec.stream().map(s -> gg.species.get(s)).map(t -> t.best).toArray(Annotation[]::new);
                    int rc = 1;
                    for (int i = 0; i < 239; i++) {
                        Row row = sheet.createRow(rc++);
                        cn = 0;
                        cell = row.createCell(cn++);
                        cell.setCellValue((i+12)%239+1);
                        for (int k = 0; k < lann.length; k++) {
                            var a = lann[k];
                            var ngg = a.getGeneGroup();
                            if (ngg!=null) {
                                cell = row.createCell(cn);
                                cell.setCellValue(a.getGeneGroup().getName());
                            }
                            cn++;
                        }
                        for (int k = 0; k < lann.length; k++) {
                            var a = lann[k];
                            var ngg = a.getGeneGroup();
                            if (ngg!=null) {
                                var ohit = ngg.genes.stream().filter(an -> gs.geneset.hhblitsmap.containsKey(an.getId())).map(an -> gs.geneset.hhblitsmap.get(an.getId())).findFirst();
                                if (ohit.isPresent()) {
                                    cell = row.createCell(cn);
                                    cell.setCellValue(ohit.get());
                                }
                            }
                            cn++;
                        }
                        for (int k = 0; k < lann.length; k++) {
                            var a = lann[k];
                            var ngg = a.getGeneGroup();
                            if (ngg!=null) {
                                var ohit = ngg.genes.stream().filter(an -> gs.geneset.hhblitsunimap.containsKey(an.getId())).map(an -> gs.geneset.hhblitsunimap.get(an.getId())).findFirst();
                                if (ohit.isPresent()) {
                                    cell = row.createCell(cn);
                                    cell.setCellValue(ohit.get());
                                }
                            }
                            lann[k] = a.getNext();
                            cn++;
                        }
                    }
                }

                try {
                    Path tempfile = Files.createTempFile("geneset", ".xlsx");
                    OutputStream os = Files.newOutputStream(tempfile);
                    workbook.write(os);
                    os.close();

                    Desktop.getDesktop().open(tempfile.toFile());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
        });
    }

    public static void excelExport( GeneSetHead gs, String sheetname, boolean sequences ) {
        SwingUtilities.invokeLater(() -> {
            var ss = new Set[1];
            ss[0] = gs.getSelspec(gs, gs.geneset.getSpecies());
            Platform.runLater(() -> {
                var selspec = ss[0];
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet(sheetname);
                Row header = sheet.createRow(0);
                var xssfColor = new XSSFColor(new byte[] {0,(byte)128,0});
                if( gs.isGeneview() ) {
                    int cn = 0;
                    for( TableColumn tc : gs.gtable.getColumns() ) {
                        org.apache.poi.ss.usermodel.Cell cell = header.createCell(cn++);
                        cell.setCellValue( tc.getText() );
                    }
                    org.apache.poi.ss.usermodel.Cell cell = header.createCell(cn++);
                    cell.setCellValue( "start" );
                    cell = header.createCell(cn++);
                    cell.setCellValue( "stop" );

                    int rc = 1;
                    Row row = sheet.createRow(rc++);
                    XSSFCellStyle cellstyle = (XSSFCellStyle) workbook.createCellStyle();
                    cellstyle.setFillBackgroundColor( xssfColor );
                    for( int r : gs.gtable.getSelectionModel().getSelectedIndices() ) {
                        cn = 0;
                        for( TableColumn tc : gs.gtable.getColumns() ) {
                            Object o = tc.getCellData(r);
                            if( o != null ) {
                                cell = row.createCell(cn);
                                cell.setCellValue( o.toString() );
                                //XSSFCellStyle cellstyle = (XSSFCellStyle) workbook.createCellStyle();
                                //cellstyle.setFillBackgroundColor( new XSSFColor(new Color(0,128,0)) );
                            }
                            cn++;
                        }
                        if (sequences) {
                            cell = row.createCell(cn);
                            Gene g = gs.gtable.getItems().get(r);
                            cell.setCellValue(g.getTegeval().start);
                            cell.setCellStyle(cellstyle);
                            cn++;
                            cell = row.createCell(cn);
                            cell.setCellValue(g.getTegeval().stop);
                            cell.setCellStyle(cellstyle);
                            cn++;

                            for (TableColumn tc : gs.gresults.getColumns().filtered(p -> selspec.contains(p.getText()))) {
                                Object o = tc.getCellData(r);
                                if (o != null) {
                                    cell = row.createCell(cn);
                                    String seqstr;
                                    if (o instanceof Tegeval) {
                                        var te = (Tegeval) o;
                                        seqstr = te.getProteinSequence().getSequenceString();
                                    } else {
                                        var ti = (Teginfo) o;
                                        seqstr = ti.tset.stream().map(Annotation::getProteinSequence).map(Sequence::getSequence).map(StringBuilder::toString).collect(Collectors.joining(","));
                                    }
                                    cell.setCellValue(seqstr);
                                    cell.setCellStyle(cellstyle);
                                }
                                cn++;
                            }
                        } else {
                            for (TableColumn tc : gs.gresults.getColumns()) {
                                Object o = tc.getCellData(r);
                                if (o != null) {
                                    cell = row.createCell(cn);
                                    cell.setCellValue(o.toString());
                                    cell.setCellStyle(cellstyle);
                                }
                                cn++;
                            }
                        }
                        row = sheet.createRow(rc++);
                    }
                } else {
                    int cn = 0;
                    for( TableColumn tc : gs.table.getColumns() ) {
                        org.apache.poi.ss.usermodel.Cell cell = header.createCell(cn++);
                        cell.setCellValue( tc.getText() );
                    }
                    for( TableColumn tc : gs.results.getColumns().filtered(p -> selspec.contains(p.getText())) ) {
                        org.apache.poi.ss.usermodel.Cell cell = header.createCell(cn++);
                        cell.setCellValue( tc.getText() );
                    }

                    int rc = 1;
                    Row row = sheet.createRow(rc++);
                    XSSFCellStyle cellstyle = (XSSFCellStyle) workbook.createCellStyle();
                    cellstyle.setFillBackgroundColor( xssfColor );
                    for( int r : gs.table.getSelectionModel().getSelectedIndices() ) {
                        cn = 0;
                        for( TableColumn tc : gs.table.getColumns() ) {
                            Object o = tc.getCellData(r);
                            if( o != null ) {
                                org.apache.poi.ss.usermodel.Cell cell = row.createCell(cn);
                                cell.setCellValue( o.toString() );
                                //XSSFCellStyle cellstyle = (XSSFCellStyle) workbook.createCellStyle();
                                //cellstyle.setFillBackgroundColor( new XSSFColor(new Color(0,128,0)) );
                            }
                            cn++;
                        }
                        if (sequences) {
                            for (TableColumn tc : gs.results.getColumns().filtered(p -> selspec.contains(p.getText()))) {
                                Teginfo o = (Teginfo)tc.getCellData(r);
                                if (o != null) {
                                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(cn);
                                    var seqstr = o.tset.stream().map(Annotation::getProteinSequence).map(Sequence::getSequence).map(StringBuilder::toString).collect(Collectors.joining(","));
                                    cell.setCellValue(seqstr);
                                    cell.setCellStyle(cellstyle);
                                }
                                cn++;
                            }
                        } else {
                            for (TableColumn tc : gs.results.getColumns().filtered(p -> selspec.contains(p.getText()))) {
                                Object o = tc.getCellData(r);
                                if (o != null) {
                                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(cn);
                                    cell.setCellValue(o.toString());
                                    cell.setCellStyle(cellstyle);
                                }
                                cn++;
                            }
                        }
                        row = sheet.createRow(rc++);
                    }
                }

                try {
                    Path tempfile = Files.createTempFile("geneset",".xlsx");
                    OutputStream os = Files.newOutputStream( tempfile );
                    workbook.write( os );
                    os.close();

                    Desktop.getDesktop().open( tempfile.toFile() );
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
        });
    }
}
