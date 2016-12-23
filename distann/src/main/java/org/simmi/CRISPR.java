/*
 * Copyright (c) 2015 deCODE Genetics Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * deCODE Genetics Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with deCODE.
 */

package org.simmi;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.simmi.shared.Annotation;
import org.simmi.shared.GeneGroup;
import org.simmi.shared.Sequence;
import org.simmi.shared.Tegeval;

import javafx.collections.ObservableList;

import javax.swing.*;

/**
 *
 * @version $Id:  $
 */
public class CRISPR {
	public static String longestCommonSubstring(String str1, String str2) {
		if (str1 == null || str1.length() == 0 || str2 == null || str2.length() == 0 )
			return "";
	 
		String ret = "";
		for( int i = 0; i < str1.length(); i++ ) {
			int count = 0;
			for( int y = 0; y < Math.min(str2.length(),str1.length()-i); y++ ) {
				char c1 = str1.charAt(i+y);
				char c2 = str2.charAt(y);
				
				if( c1 == c2 ) {
					count++;
				} else {
					if( count > ret.length() ) {
						ret = str1.substring(i,count+i);
					}
					count = 0;
				}
			}
			
			if( count > ret.length() ) {
				ret = str1.substring(i,count+i);
			}
		}
		
		for( int i = 0; i < str2.length(); i++ ) {
			int count = 0;
			for( int y = 0; y < Math.min(str1.length(),str2.length()-i); y++ ) {
				char c1 = str2.charAt(i+y);
				char c2 = str1.charAt(y);
				
				if( c1 == c2 ) {
					count++;
				} else {
					if( count > ret.length() ) {
						ret = str2.substring(i,count+i);
					}
					count = 0;
				}
			}
			
			if( count > ret.length() ) {
				ret = str2.substring(i,count+i);
			}
		}
		
		return ret;
		
		/*int[][] num = new int[str1.length()][str2.length()];
		String maxstr = "";
	 
		for (int i = 0; i < str1.length(); i++)
		{
			for (int j = 0; j < str2.length(); j++)
			{
				if (str1.charAt(i) != str2.charAt(j))
					num[i][j] = 0;
				else
				{
					if ((i == 0) || (j == 0))
						num[i][j] = 1;
					else
						num[i][j] = 1 + num[i - 1][j - 1];
	 
					if (num[i][j] > maxstr.length()) {
						maxstr = str1.substring(i,num[i][j]-i);
					}
				}
			}
		}
		return maxstr;*/
	}
	
	public static void crispr( GeneSetHead genesethead ) {
		GeneSet geneset = genesethead.geneset;
		ObservableList<GeneGroup> lgg = genesethead.table.getSelectionModel().getSelectedItems();
		if( lgg.size() > 0 ) {
			SwingUtilities.invokeLater(() -> {
                Set<String> includeSpecs = genesethead.getSelspec(genesethead, geneset.specList, null);

                Workbook wb = new XSSFWorkbook();
                for (GeneGroup gg : lgg) {
                    Sheet sheet = wb.createSheet(gg.getName());

                    Map<String, String> sspec = new TreeMap<>();
                    for (String spec : gg.getSpecies()) {
                        if (includeSpecs.contains(spec)) sspec.put(Sequence.nameFix(spec, true), spec);
                    }

                    Row row = sheet.createRow(0);
                    Cell cell = row.createCell(0);
                    cell.setCellValue("Strain");
                    cell = row.createCell(1);
                    cell.setCellValue("Location");
                    cell = row.createCell(2);
                    cell.setCellValue("Upstream repeats");
                    cell = row.createCell(3);
                    cell.setCellValue("Repeat sequence");
                    cell = row.createCell(4);
                    cell.setCellValue("Phage hits");
                    cell = row.createCell(5);
                    cell.setCellValue("Phage hits - not identical");
                    cell = row.createCell(6);
                    cell.setCellValue("Downstream repeats");
                    cell = row.createCell(7);
                    cell.setCellValue("Repeat sequence");
                    cell = row.createCell(8);
                    cell.setCellValue("Phage hits");
                    cell = row.createCell(9);
                    cell.setCellValue("Phage hits - not identical");
                    int k = 1;

                    Map<String, List<String>> specudSpacers = new LinkedHashMap<>();

                    for (String spec : sspec.keySet()) {
                        String sp = sspec.get(spec);
                        row = sheet.createRow(k++);
                        cell = row.createCell(0);
                        cell.setCellValue(spec);
                        cell = row.createCell(1);

                        String loc = "";

                        Tegeval best = null;
                        List<Tegeval> ltv = gg.getTegevals(sp);
                        for (Tegeval tv : ltv) {
                            if (tv.seq.isPlasmid()) {
                                loc += "plasmid";
                            } else {
                                loc += "chromosome";
                            }
                            best = tv;
                        }
                        cell.setCellValue(loc);

                        String lcs = "";
                        String plcs = "";
                        int count = 0;
                        int pcount = 0;

                        Map<String, Integer> uphage = new HashMap<>();
                        Map<String, Integer> dphage = new HashMap<>();

                        Map<String, Integer> uphage_n = new HashMap<>();
                        Map<String, Integer> dphage_n = new HashMap<>();

                        List<String> upSpacers = new ArrayList<>();
                        String spacers = "";
                        int u = 0;
                        Annotation before = best;
                        Annotation next = best.getNext();
                        while (next != null) {
                            if (next.type != null && next.type.contains("mummer")) {
                                count++;

                                if (before.type != null && before.type.contains("mummer")) {
                            /*if( before.stop < -10 || next.start < -10 || before.stop > next.start) {
                                System.err.println( before.start + "  " + before.stop + "  " + next.start + "  " + next.stop );
                            }
                            int up = Math.min(before.stop,next.start)+1;
                            int en = Math.max(before.stop,next.start)-1;*/

                                    int up;
                                    int en;
                                    if (next.start > before.start) {
                                        up = before.stop + 1;
                                        en = next.start - 1;
                                    } else {
                                        up = next.stop + 1;
                                        en = before.start - 1;
                                    }

                                    String spacer = best.seq.getSubstring(up, en, 1);
                                    if (spacer.length() > 0) {
                                        upSpacers.add(spacer);
                                        spacers += ">" + u + "\n" + spacer + "\n";
                                    }
                                }

                                String newstr = next.getName();
                                int o = newstr.indexOf('-');
                                if (o != -1) {
                                    newstr = newstr.substring(o + 1);
                                }
                                if (lcs.length() > 0) {
                                    lcs = longestCommonSubstring(lcs, newstr.toUpperCase());
                                } else {
                                    lcs = newstr.toUpperCase();
                                }
                            } else if (before != null && before.type != null && before.type.contains("mummer")) {
                                before = null;
                                break;
                                //} else if( before.type == null ) {
                                //	break;
                            } else if (u - count > 30) {
                                break;
                            }

                            before = next;
                            next = next.getNext();

                            u++;
                        }

                        //spacers = ">1\nACGTCGCTAGCTCGATCGCT\n";
                        if (spacers.length() > 0) {
                            ProcessBuilder pb = new ProcessBuilder("blastn", "-db", "phage_radir.fna", "-word_size", "20", "-num_threads", "4");//,"-num_alignments","1","-num_descriptions","1");
                            pb.directory(new File(System.getProperty("user.home")));
                            try {
                                final Process p = pb.start();
                                final byte[] sbytes = spacers.getBytes();
                                Thread t = new Thread(() -> {
                                    try {
                                        OutputStream os = p.getOutputStream();
                                        os.write(sbytes);
                                        os.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                t.start();

                                InputStream is = p.getInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                String line = br.readLine();

                                Set<String> allready = new HashSet<>();
                                while (line != null) {
                                    if (line.startsWith("Query=")) {
                                        allready.clear();
                                    } else if (line.startsWith(">")) {
                                        String trim = line.substring(1).trim();
                                        String phage = trim.substring(0, trim.indexOf('-'));

                                        if (!allready.contains(phage)) {
                                            line = br.readLine();
                                            while (!line.contains("Gaps")) {
                                                line = br.readLine();
                                            }
                                            int ik = line.indexOf("Gaps");
                                            if (line.substring(ik + 7, line.indexOf('/', ik + 7)).trim().equals("0")) {
                                                if (!uphage.containsKey(phage)) {
                                                    uphage.put(phage, 1);
                                                } else {
                                                    uphage.put(phage, uphage.get(phage) + 1);
                                                }
                                            } else {
                                                if (!uphage_n.containsKey(phage)) {
                                                    uphage_n.put(phage, 1);
                                                } else {
                                                    uphage_n.put(phage, uphage_n.get(phage) + 1);
                                                }
                                            }
                                            allready.add(phage);
                                        }
                                    }
                                    line = br.readLine();
                                }
                                is.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                        List<String> downSpacers = new ArrayList<>();
                        spacers = "";
                        u = 0;
                        before = best;
                        Annotation prev = best.getPrevious();
                        while (prev != null) {
                            if (prev.type != null && prev.type.contains("mummer")) {
                                pcount++;

                                System.err.println("fuck you " + prev.seq.getSpec() + "  " + pcount);
                                if (before.type != null && before.type.contains("mummer")) {
                                    int up;
                                    int en;
                                    if (prev.start > before.start) {
                                        up = before.stop + 1;
                                        en = prev.start - 1;
                                    } else {
                                        up = prev.stop + 1;
                                        en = before.start - 1;
                                    }

                                    String spacer = best.seq.getSubstring(up, en, 1);
                                    if (spacer.length() > 0) {
                                        downSpacers.add(spacer);
                                        spacers += ">" + u + "\n" + spacer + "\n";
                                    }
                                }

                                String newstr = prev.getName();
                                int o = newstr.indexOf('-');
                                if (o != -1) {
                                    newstr = newstr.substring(o + 1);
                                }
                                if (plcs.length() > 0) {
                                    plcs = longestCommonSubstring(plcs, newstr.toUpperCase());
                                } else {
                                    plcs = newstr.toUpperCase();
                                }
                            } else if (before != null && before.type != null && before.type.contains("mummer")) {
                                before = null;
                                //count++;
                                continue;
                                //break;
                                //} else if( before.type == null ) {
                                //	break;
                            } else if (u - pcount > 30) {
                                break;
                            }

                            before = prev;
                            prev = prev.getPrevious();

                            u++;
                        }

                        if (spacers.length() > 0) {
                            ProcessBuilder pb = new ProcessBuilder("blastn", "-db", "phage_radir.fna", "-word_size", "20", "-num_threads", "4", "-num_alignments", "1", "-num_descriptions", "1");
                            pb.directory(new File(System.getProperty("user.home")));
                            try {
                                final Process p = pb.start();
                                final byte[] sbytes = spacers.getBytes();
                                Thread t = new Thread() {
                                    public void run1() {
                                        try {
                                            OutputStream os = p.getOutputStream();
                                            os.write(sbytes);
                                            os.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                t.start();

                                InputStream is = p.getInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                                String line = br.readLine();
                                while (line != null) {
                                    if (line.startsWith(">")) {
                                        String trim = line.substring(1).trim();
                                        String phage = trim.substring(0, trim.indexOf('-'));

                                        line = br.readLine();
                                        while (!line.contains("Gaps")) {
                                            line = br.readLine();
                                        }
                                        int ik = line.indexOf("Gaps");
                                        if (line.substring(ik + 7, line.indexOf('/', ik + 7)).trim().equals("0")) {
                                            if (!dphage.containsKey(phage)) {
                                                dphage.put(phage, 1);
                                            } else {
                                                dphage.put(phage, dphage.get(phage) + 1);
                                            }
                                        } else {
                                            if (!dphage_n.containsKey(phage)) {
                                                dphage_n.put(phage, 1);
                                            } else {
                                                dphage_n.put(phage, dphage_n.get(phage) + 1);
                                            }
                                        }
                                    }
                                    line = br.readLine();
                                }
                                is.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                        cell = row.createCell(2);
                        cell.setCellValue(count);
                        cell = row.createCell(3);
                        cell.setCellValue(lcs);
                        cell = row.createCell(4);
                        cell.setCellValue(uphage.toString());
                        cell = row.createCell(5);
                        cell.setCellValue(uphage_n.toString());


                        cell = row.createCell(6);
                        cell.setCellValue(pcount);
                        cell = row.createCell(7);
                        cell.setCellValue(plcs);
                        cell = row.createCell(8);
                        cell.setCellValue(dphage.toString());
                        cell = row.createCell(9);
                        cell.setCellValue(dphage_n.toString());

                        if (upSpacers.size() > 0) specudSpacers.put(spec + "up", upSpacers);
                        if (downSpacers.size() > 0) specudSpacers.put(spec + "down", downSpacers);
                    }

                    row = sheet.createRow(k);
                    row.createCell(0).setCellValue("Spacers");
                    row = sheet.createRow(++k);
                    int u = 0;
                    for (String spec : specudSpacers.keySet()) {
                        Cell cl = row.createCell(u++);
                        cl.setCellValue(spec);
                    }

                    int l = 0;
                    boolean empty = false;
                    while (!empty) {
                        row = sheet.createRow(++k);
                        empty = true;
                        u = 0;
                        for (String spec : specudSpacers.keySet()) {
                            List<String> ss = specudSpacers.get(spec);
                            if (l < ss.size()) {
                                empty = false;
                                Cell cl = row.createCell(u);
                                String spcr = ss.get(l).toUpperCase();
                                cl.setCellValue(spcr.substring(0, Math.min(50, spcr.length())));
                            }
                            u++;
                        }
                        l++;
                    }
                }

                String userhome = System.getProperty("user.home");
                File f = new File(userhome);
                File file = new File(f, "crispr.xlsx");
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    wb.write(fos);
                    fos.close();
                    Desktop.getDesktop().open(file);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
		}
	}
}
