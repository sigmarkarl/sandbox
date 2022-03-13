package org.simmi.distann;

import org.simmi.javafasta.shared.*;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class StaticMethods {
    public static void aahist(File f1, File f2, int val) throws IOException {
        Map<String, Long> aa1map = new HashMap<String, Long>();
        Map<String, Long> aa2map = new HashMap<String, Long>();

        long t1 = 0;
        FileReader fr = new FileReader(f1);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            if (!line.startsWith(">")) {
                for (int i = 0; i < line.length() - val + 1; i++) {
                    String c = line.substring(i, i + val);
                    if (aa1map.containsKey(c)) {
                        aa1map.put(c, aa1map.get(c) + 1L);
                    } else
                        aa1map.put(c, 1L);

                    t1++;
                }
            }
            line = br.readLine();
        }
        br.close();

        // Runtime.getRuntime().availableProcessors()

        long t2 = 0;
        fr = new FileReader(f2);
        br = new BufferedReader(fr);
        line = br.readLine();
        while (line != null) {
            if (!line.startsWith(">")) {
                for (int i = 0; i < line.length() - val + 1; i++) {
                    String c = line.substring(i, i + val);
                    if (aa2map.containsKey(c)) {
                        aa2map.put(c, aa2map.get(c) + 1L);
                    } else
                        aa2map.put(c, 1L);

                    t2++;
                }
            }
            line = br.readLine();
        }
        br.close();

        System.err.println(t1 + "\t" + t2);
        int na1 = 0;
        int na2 = 0;
        int nab = 0;
        int u = 0;
        double dt = 0.0;
        Set<String> notfound = new HashSet<String>();
        Set<String> notfound2 = new HashSet<String>();
        for (int i = 0; i < Math.pow(Sequence.uff.size(), val); i++) {
            String e = "";
            for (int k = 0; k < val; k++) {
                e += Sequence.uff.get((i / (int) Math.pow(Sequence.uff.size(), val - (k + 1))) % Sequence.uff.size()).c;
            }

            if (aa1map.containsKey(e) || aa2map.containsKey(e)) {
                boolean b1 = aa1map.containsKey(e);
                boolean b2 = aa2map.containsKey(e);

                if (!b1) {
                    if (val == 3)
                        notfound.add(e);
                    na1++;
                }
                if (!b2) {
                    if (val == 3)
                        notfound2.add(e);
                    na2++;
                }

                double dval = (b1 ? aa1map.get(e) / (double) t1 : 0.0) - (b2 ? aa2map.get(e) / (double) t2 : 0.0);
                dval *= dval;
                dt += dval;
                u++;

                System.err.println(e + "\t" + (aa1map.get(e)) + "\t" + (aa2map.containsKey(e) ? (aa2map.get(e)) : "-"));
            } else {
                if (val == 3) {
                    notfound.add(e);
                    notfound2.add(e);
                }
                nab++;
            }
        }
        System.err.println("MSE: " + (dt / u) + " for " + val);
        System.err.println("Not found in 1: " + na1 + ", Not found in 2: " + na2 + ", found in neither: " + nab);

        for (String ns : notfound) {
            System.err.println(ns);
        }
        System.err.println();
        for (String ns : notfound2) {
            System.err.println(ns);
        }
    }

    public static void aahist(File f1, File f2) throws IOException {
        Map<Character, Long> aa1map = new HashMap<Character, Long>();
        Map<Character, Long> aa2map = new HashMap<Character, Long>();

        long t1 = 0;
        FileReader fr = new FileReader(f1);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            if (!line.startsWith(">")) {
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (aa1map.containsKey(c)) {
                        aa1map.put(c, aa1map.get(c) + 1L);
                    } else
                        aa1map.put(c, 1L);

                    t1++;
                }
            }
            line = br.readLine();
        }
        br.close();

        long t2 = 0;
        fr = new FileReader(f2);
        br = new BufferedReader(fr);
        line = br.readLine();
        while (line != null) {
            if (!line.startsWith(">")) {
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (aa2map.containsKey(c)) {
                        aa2map.put(c, aa2map.get(c) + 1L);
                    } else
                        aa2map.put(c, 1L);

                    t2++;
                }
            }
            line = br.readLine();
        }
        br.close();

        for (Erm e : Sequence.isoel) {
            char c = e.c;
            if (aa1map.containsKey(c)) {
                System.err.println(e.d + "\t" + c + "\t" + (aa1map.get(c) / (double) t1) + "\t" + (aa2map.containsKey(c) ? (aa2map.get(c) / (double) t2) : "-"));
            }
        }
    }

    public static void newstuff() throws IOException {
        Map<String, Set<String>> famap = new HashMap<String, Set<String>>();
        Map<String, String> idmap = new HashMap<String, String>();
        Map<String, String> nmmap = new HashMap<String, String>();
        File f = new File("/home/sigmar/groupmap.txt");
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = br.readLine();
        while (line != null) {
            String[] split = line.split("\t");
            if (split.length > 1) {
                idmap.put(split[1], split[0]);
                nmmap.put(split[0], split[1]);

                String[] subsplit = split[0].split("_");
                Set<String> fam = null;
                if (famap.containsKey(subsplit[0])) {
                    fam = famap.get(subsplit[0]);
                } else {
                    fam = new HashSet<String>();
                    famap.put(subsplit[0], fam);
                }
                fam.add(split[0]);
            }

            line = br.readLine();
        }
        br.close();

        Set<String> remap = new HashSet<String>();
        Set<String> almap = new HashSet<String>();
        for (String erm : famap.keySet()) {
            if (erm.startsWith("Trep") || erm.startsWith("Borr") || erm.startsWith("Spir")) {
                remap.add(erm);
                almap.addAll(famap.get(erm));
            }
        }
        for (String key : remap)
            famap.remove(key);
        famap.put("TrepSpirBorr", almap);

        for (String fam : famap.keySet()) {
            Set<String> subfam = famap.get(fam);
            System.err.println("fam: " + fam);
            for (String sf : subfam) {
                System.err.println("\tsf: " + nmmap.get(sf));
            }
        }

        f = new File("/home/sigmar/group_21.dat");
        Map<Set<String>, Set<String>> common = new HashMap<Set<String>, Set<String>>();
        /*
         * File[] files = f.listFiles( new FilenameFilter() {
         *
         * @Override public boolean accept(File dir, String name) { if(
         * name.startsWith("group") && name.endsWith(".dat") ) { return true; }
         * return false; } });
         */

        Set<String> erm2 = new HashSet<String>();
        erm2.addAll(famap.get("Brachyspira"));
        erm2.addAll(famap.get("Leptospira"));

        Set<String> all = new HashSet<String>();
        br = new BufferedReader(new FileReader(f));
        line = br.readLine();
        while (line != null) {
            String[] split = line.split("[\t]+");
            if (split.length > 2) {
                Set<String> erm = new HashSet<String>();
                for (int i = 2; i < split.length; i++) {
                    erm.add(idmap.get(split[i].substring(0, split[i].indexOf('.'))));
                    // erm.add( split[i].substring(0, split[i].indexOf('.') ) );
                }

                Set<String> incommon = null;
                if (common.containsKey(erm)) {
                    incommon = common.get(erm);
                } else {
                    incommon = new HashSet<String>();
                    common.put(erm, incommon);
                }
                incommon.add(line);

                if (erm.size() == 13) {
                    // if( erm.containsAll(famap.get("TrepSpirBorr")) &&
                    // erm.containsAll(famap.get("Leptospira")) ) {
                    // if( erm.containsAll(famap.get("TrepSpirBorr")) &&
                    // erm.containsAll(famap.get("Brachyspira")) ) {
                    // if( erm.containsAll(famap.get("Leptospira")) ) {
                    // if( erm.containsAll(famap.get("Brachyspira")) ) {
                    boolean includesAllLeptos = erm.containsAll(famap.get("Leptospira"));
                    boolean includesAllBrachys = erm.containsAll(famap.get("Brachyspira"));
                    Set<String> ho = new HashSet<String>(erm);
                    ho.removeAll(famap.get("Brachyspira"));
                    ho.removeAll(famap.get("TrepSpirBorr"));
                    boolean includesSomeLeptos = ho.size() > 0 && ho.size() != famap.get("Leptospira").size();
                    ho = new HashSet<String>(erm);
                    ho.removeAll(famap.get("Leptospira"));
                    ho.removeAll(famap.get("TrepSpirBorr"));
                    boolean includesSomeBrachys = ho.size() > 0 && ho.size() != famap.get("Brachyspira").size();

                    if (erm.containsAll(famap.get("TrepSpirBorr"))) { // && (
                        // includesSomeBrachys
                        // ||
                        // includesSomeLeptos
                        // ) ) {
                        // int start =
                        // line.indexOf("5743b451ec3e92efc596500d604750ed");
                        // int start =
                        // line.indexOf("be1843abfce51adcaa86b07a3c6bedbb");
                        // int start =
                        // line.indexOf("7394569560a961ac7ffe674befec5056");
                        // Set<String> ho = new TreeSet<String>( erm );
                        // ho.removeAll(famap.get("TrepSpirBorr"));
                        // System.err.println("erm " + ho);
                        int start = line.indexOf("d719570adc9e2969b0374564745432cd");

                        if (start > 0) {
                            int end = line.indexOf('\t', start);
                            // if( end == -1 ) end = line.indexOf('\n', start);
                            if (end == -1)
                                end = line.length();
                            all.add(line.substring(start, end));
                        } else {
                            System.err.println();
                        }
                    }
                }

                /*
                 * if( erm.size() >= 22 ) { int start =
                 * line.indexOf("696cf959d443a23e53786f1eae8eb6c9");
                 *
                 * if( start > 0 ) { int end = line.indexOf('\t', start); //if(
                 * end == -1 ) end = line.indexOf('\n', start); if( end == -1 )
                 * end = line.length(); all.add( line.substring(start, end) ); }
                 * else { System.err.println(); } }
                 */
            }

            line = br.readLine();
        }
        br.close();

        PrintStream ps = new PrintStream("/home/sigmar/iron5.giant");
        // System.setErr( ps );

        int count = 0;
        f = new File("/home/sigmar/21.fsa");
        br = new BufferedReader(new FileReader(f));
        line = br.readLine();
        while (line != null) {
            if (all.contains(line.substring(1))) {
                count++;
                System.err.println(line);
                line = br.readLine();
                while (line != null && !line.startsWith(">")) {
                    System.err.println(line);
                    line = br.readLine();
                }
            } else
                line = br.readLine();
        }
        br.close();

        System.err.println("hey: " + count);

        int total = 0;
        System.err.println("total groups " + common.size());
        for (Set<String> keycommon : common.keySet()) {
            Set<String> incommon = common.get(keycommon);
            System.err.println(incommon.size() + "  " + keycommon.size() + "  " + keycommon);
            total += incommon.size();
        }
        System.err.println(total);
        System.err.println();

        total = 0;
        System.err.println("boundary crossing groups");
        for (Set<String> keycommon : common.keySet()) {
            Set<String> incommon = common.get(keycommon);

            boolean s = true;
            for (String fam : famap.keySet()) {
                Set<String> famset = famap.get(fam);
                if (famset.containsAll(keycommon)) {
                    s = false;
                    break;
                }
            }
            if (s) {
                System.err.println(incommon.size() + "  " + keycommon.size() + "  " + keycommon);
                total++;
            }
        }
        System.err.println("for the total of " + total);

        /*
         * System.err.println( all.size() ); for( String astr : all ) {
         * System.err.println( astr ); }
         */

        ps.close();
    }

    public static void pearsons(Map<Character, Double> what, List<Pepbindaff> peppi) {
        double[] sums = new double[peppi.get(0).pep.length()];
        Arrays.fill(sums, 0.0);
        for (Pepbindaff paff : peppi) {
            for (int i = 0; i < paff.pep.length(); i++) {
                char c = paff.pep.charAt(i);
                sums[i] += what.get(c);
            }
        }
        for (int i = 0; i < peppi.get(0).pep.length(); i++) {
            sums[i] /= peppi.size();
        }

        for (int i = 0; i < peppi.get(0).pep.length(); i++) {
            for (int j = i; j < peppi.get(0).pep.length(); j++) {
                double t = 0.0;
                double nx = 0.0;
                double ny = 0.0;
                for (Pepbindaff paff : peppi) {
                    char c = paff.pep.charAt(j);
                    char ct = paff.pep.charAt(i);
                    double h = what.get(c) - sums[j];
                    double ht = what.get(ct) - sums[i];
                    t += h * ht;
                    nx += h * h;
                    ny += ht * ht;
                }
                double xy = nx * ny;
                double val = (xy == 0 ? 0.0 : (t / Math.sqrt(xy)));
                if (Math.abs(val) > 0.1 || i == j)
                    System.err.println("Pearson (" + i + " " + j + "): " + val);
            }
        }
    }

    public static void kendaltau(List<Pepbindaff> peppi) {
        int size = peppi.get(0).pep.length();
        List<Pepbindaff> erm = new ArrayList<Pepbindaff>();
        for (int x = 0; x < size - 1; x++) {
            for (int y = x + 1; y < size; y++) {
                int con = 0;
                int dis = 0;
                for (int i = 0; i < peppi.size(); i++) {
                    for (int j = i + 1; j < peppi.size(); j++) {
                        char xi = peppi.get(i).pep.charAt(x);
                        char xj = peppi.get(j).pep.charAt(x);
                        char yi = peppi.get(i).pep.charAt(y);
                        char yj = peppi.get(j).pep.charAt(y);

                        if ((xi > xj && yi > yj) || (xi < xj && yi < yj))
                            con++;
                        else if (xi > xj && yi < yj || xi < xj && yi > yj)
                            dis++;
                    }
                }
                double kt = (double) (2 * (con - dis)) / (double) (peppi.size() * (peppi.size() - 1));
                erm.add(new Pepbindaff("kt " + x + " " + y + ": ", kt));
                // System.err.println( "kt "+i+" "+j+": "+kt );
            }
        }
        Collections.sort(erm);
        for (Pepbindaff p : erm) {
            System.err.println(p.pep + " " + p.aff);
        }
    }

    public static void algoinbio() throws IOException {
        FileReader fr = new FileReader("/home/sigmar/dtu/27623-AlgoInBio/week7/A0101/A0101.dat");
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();

        Map<String, Double> pepaff = new TreeMap<String, Double>();
        List<Pepbindaff> peppi = new ArrayList<Pepbindaff>();
        while (line != null) {
            String[] split = line.trim().split("[\t ]+");
            peppi.add(new Pepbindaff(split[0], split[1]));

            line = br.readLine();
        }
        br.close();

        Collections.sort(peppi);

        kendaltau(peppi);
        // pearsons( hydropathyindex, peppi );

        /*
         * for( Pepbindaff paff : peppi ) { System.err.print( paff.pep ); for(
         * int i = 0; i < paff.pep.length(); i++ ) { char c1 =
         * paff.pep.charAt(i); //char c2 = paff.pep.charAt(i+1);
         * //System.err.print(
         * "\t"+Math.min(hydropathyindex.get(c1),hydropathyindex.get(c2) ) );
         * //System.err.print( "\t"+sidechaincharge.get(c) ); System.err.print(
         * "\t"+isoelectricpoint.get(c1) ); //System.err.print(
         * "\t"+Math.min(isoelectricpoint.get(c1),isoelectricpoint.get(c2)) );
         * //System.err.print( "\t"+sidechainpolarity.get(c) ); }
         * System.err.println( "\t"+paff.aff ); }
         *
         * /*double[] hyp = new double[9]; double[] chr = new double[9];
         * double[] iso = new double[9]; double[] pol = new double[9];
         * Arrays.fill(hyp, 0.0); Arrays.fill(chr, 0.0); Arrays.fill(iso, 0.0);
         * Arrays.fill(pol, 0.0); int count = 0; while( line != null ) {
         * String[] split = line.split("[\t ]+"); String pep = split[0]; double
         * val = Double.parseDouble(split[1]); for( int i = 0; i < hyp.length;
         * i++ ) { char c = pep.charAt(i);
         *
         * hyp[i] += val*hydropathyindex.get(c); chr[i] +=
         * val*sidechaincharge.get(c); iso[i] += val*isoelectricpoint.get(c);
         * pol[i] += val*(sidechainpolarity.get(c) == 'p' ? 1.0 : -1.0); }
         * count++; line = br.readLine(); } br.close();
         *
         * /* Lowpoint
         */

        /*
         * fr = new FileReader("/home/sigmar/dtu/27623-AlgoInBio/week7/f000");
         * br = new BufferedReader( fr ); line = br.readLine();
         *
         * double[] hype = new double[9]; double[] chre = new double[9];
         * double[] isoe = new double[9]; double[] pole = new double[9];
         * Arrays.fill(hype, 0.0); Arrays.fill(chre, 0.0); Arrays.fill(isoe,
         * 0.0); Arrays.fill(pole, 0.0); double hypmax = Double.MIN_VALUE;
         * double chrmax = Double.MIN_VALUE; double isomax = Double.MIN_VALUE;
         * double polmax = Double.MIN_VALUE; while( line != null ) { double hypt
         * = 0.0; double chrt = 0.0; double isot = 0.0; double polt = 0.0;
         *
         * String[] split = line.split("[\t ]+"); String pep = split[0]; double
         * val = Double.parseDouble(split[1]); for( int i = 0; i < hyp.length;
         * i++ ) { char c = pep.charAt(i);
         *
         * double d = val*hydropathyindex.get(c) - hyp[i]/count; hype[i] += d*d;
         * d = val*sidechaincharge.get(c) - chr[i]/count; chre[i] += d*d; d =
         * val*isoelectricpoint.get(c) - iso[i]/count; isoe[i] += d*d; d =
         * val*(sidechainpolarity.get(c) == 'p' ? 1.0 : -1.0) - pol[i]/count;
         * pole[i] += d*d;
         *
         * d = count*hydropathyindex.get(c) - hyp[i]; hypt += d*d; d =
         * count*sidechaincharge.get(c) - chr[i]; chrt += d*d; d =
         * count*isoelectricpoint.get(c) - iso[i]; isot += d*d; d =
         * count*(sidechainpolarity.get(c) == 'p' ? 1.0 : -1.0) - pol[i]; polt
         * += d*d; }
         *
         * hypt /= hyp.length; chrt /= hyp.length; isot /= hyp.length; polt /=
         * hyp.length;
         *
         * //hypsum += hypt; if( hypt > hypmax ) hypmax = hypt; if( chrt >
         * chrmax ) chrmax = chrt; if( isot > isomax ) isomax = isot; if( polt >
         * polmax ) polmax = polt;
         *
         * line = br.readLine(); } br.close();
         *
         * for( int i = 0; i < hyp.length; i++ ) { hype[i] /= Math.abs(hyp[i]);
         * chre[i] /= Math.abs(chr[i]); isoe[i] /= Math.abs(iso[i]); pole[i] /=
         * Math.abs(pol[i]);
         *
         * System.err.println( "pos " + i + " " + hype[i] + " " + chre[i] + " "
         * + isoe[i] + " " + pole[i] ); }
         *
         * /*********
         *
         * fr = new FileReader("/home/sigmar/dtu/27623-AlgoInBio/week11/c000");
         * br = new BufferedReader( fr ); line = br.readLine();
         *
         * List<Double> hyplist = new ArrayList<Double>(); List<Double> hyptlist
         * = new ArrayList<Double>(); double hyptsum = 0.0; double hypsum = 0.0;
         * while( line != null ) { double hypt = 0.0; double chrt = 0.0; double
         * isot = 0.0; double polt = 0.0;
         *
         * String[] split = line.split("[\t ]+"); String pep = split[0]; double
         * val = Double.parseDouble(split[1]); for( int i = 0; i < hyp.length;
         * i++ ) { char c = pep.charAt(i);
         *
         * double d = count*hydropathyindex.get(c) - hyp[i]; hypt +=
         * d*d*(1.0-hype[i]); d = count*sidechaincharge.get(c) - chr[i]; chrt +=
         * d*d*(1.0-chre[i]*100); d = count*isoelectricpoint.get(c) - iso[i];
         * isot += d*d*(1.0-isoe[i]); d = count*(sidechainpolarity.get(c) == 'p'
         * ? 1.0 : -1.0) - pol[i]; polt += d*d*(1.0-pole[i]); }
         *
         * hypt /= hyp.length; chrt /= hyp.length; isot /= hyp.length; polt /=
         * hyp.length;
         *
         * double calc = (hypmax - hypt)/hypmax; //double calc = (chrmax -
         * chrt)/chrmax; hypsum += val; hyptsum += calc; hyplist.add(val);
         * hyptlist.add( calc );
         *
         * System.err.println( calc + "  " + val );
         *
         * line = br.readLine(); } br.close();
         *
         * double hypmed = hypsum/hyplist.size(); double hyptmed =
         * hyptsum/hyptlist.size(); double t = 0.0; double nx = 0.0; double ny =
         * 0.0; for( int i = 0; i < hyplist.size(); i++ ) { double h =
         * hyplist.get(i) - hypmed; double ht = hyptlist.get(i) - hyptmed; t +=
         * h*ht; nx += h*h; ny += ht*ht; }
         *
         * double xy = nx * ny; System.err.println( "Pearson: " + (xy == 0 ? 0.0
         * : (t/Math.sqrt(xy))) );
         *
         * /*System.err.println( "hyp" ); for( double d : hyp ) {
         * System.err.println( d ); } System.err.println( "chr" ); for( double d
         * : chr ) { System.err.println( d ); } System.err.println( "iso" );
         * for( double d : iso ) { System.err.println( d ); }
         * System.err.println( "pol" ); for( double d : pol ) {
         * System.err.println( d ); }
         */
    }

    public static void blastparse(String fn) throws IOException {
        Set<String> set = new HashSet<String>();
        FileReader fr = new FileReader(fn);
        BufferedReader br = new BufferedReader(fr);
        String query = null;
        String evalue = null;
        String line = br.readLine();
        int count = 0;
        while (line != null) {
            String trim = line.trim();
            if (query != null && (trim.startsWith(">ref") || trim.startsWith(">sp") || trim.startsWith(">pdb") || trim.startsWith(">dbj") || trim.startsWith(">gb") || trim.startsWith(">emb") || trim.startsWith(">pir") || trim.startsWith(">tpg"))) {
                // String[] split = trim.split("\\|");
                set.add(trim + "\t" + evalue);

                /*
                 * if( !allgenes.containsKey( split[1] ) || allgenes.get(
                 * split[1] ) == null ) { allgenes.put( split[1], split.length >
                 * 1 ? split[2].trim() : null ); }
                 *
                 * /*Set<String> locset = null; if( geneloc.containsKey(
                 * split[1] ) ) { locset = geneloc.get(split[1]); } else {
                 * locset = new HashSet<String>(); geneloc.put(split[1],
                 * locset); } locset.add( query + " " + evalue );
                 */

                query = null;
                evalue = null;
            } else if (trim.startsWith("Query=")) {
                query = trim.substring(6).trim().split("[ ]+")[0];
            } else if (evalue == null && query != null
                    && (trim.startsWith("ref|") || trim.startsWith("sp|") || trim.startsWith("pdb|") || trim.startsWith("dbj|") || trim.startsWith("gb|") || trim.startsWith("emb|") || trim.startsWith("pir|") || trim.startsWith("tpg|"))) {
                String[] split = trim.split("[\t ]+");
                evalue = split[split.length - 1];
            }
            count++;
            line = br.readLine();
        }

        System.err.println(count);

        Map<String, Set<String>> mapset = new HashMap<String, Set<String>>();
        for (String gene : set) {
            if (gene.contains("ribosomal")) {
                Set<String> subset = null;
                if (mapset.containsKey("ribosomal proteins")) {
                    subset = mapset.get("ribosomal proteins");
                } else {
                    subset = new TreeSet<>();
                    mapset.put("ribosomal proteins", subset);
                }
                subset.add(gene);
            } else if (gene.contains("inase")) {
                Set<String> subset = null;
                if (mapset.containsKey("inase")) {
                    subset = mapset.get("inase");
                } else {
                    subset = new TreeSet<>();
                    mapset.put("inase", subset);
                }
                subset.add(gene);
            } else if (gene.contains("flag")) {
                Set<String> subset = null;
                if (mapset.containsKey("flag")) {
                    subset = mapset.get("flag");
                } else {
                    subset = new TreeSet<>();
                    mapset.put("flag", subset);
                }
                subset.add(gene);
            } else if (gene.contains("ATP")) {
                Set<String> subset = null;
                if (mapset.containsKey("ATP")) {
                    subset = mapset.get("ATP");
                } else {
                    subset = new TreeSet<>();
                    mapset.put("ATP", subset);
                }
                subset.add(gene);
            } else if (gene.contains("hypot")) {
                Set<String> subset = null;
                if (mapset.containsKey("hypot")) {
                    subset = mapset.get("hypot");
                } else {
                    subset = new TreeSet<>();
                    mapset.put("hypot", subset);
                }
                subset.add(gene);
            } else {
                Set<String> subset = null;
                if (mapset.containsKey("other")) {
                    subset = mapset.get("other");
                } else {
                    subset = new TreeSet<>();
                    mapset.put("other", subset);
                }
                subset.add(gene);
            }
        }

        PrintStream ps = new PrintStream("/home/sigmar/iron.giant");
        System.setErr(ps);
        for (String genegroup : mapset.keySet()) {
            Set<String> subset = mapset.get(genegroup);
            System.err.println(genegroup + "   " + subset.size());
            for (String gene : subset) {
                System.err.println("\t" + gene);
            }
        }
        ps.close();
    }

    public static void splitGenes(String dir, String filename) throws IOException {
        Map<String, List<Gene>> genemap = new HashMap<>();
        File f = new File(dir, filename);
        BufferedReader br = new BufferedReader(new FileReader(f));
        String last = null;
        // String aa = "";
        String line = br.readLine();
        while (line != null) {
            if (line.startsWith(">")) {
                if (last != null) {
                    String strain = last.split("_")[0].substring(1);
                    List<Gene> genelist = null;
                    if (genemap.containsKey(strain)) {
                        genelist = genemap.get(strain);
                    } else {
                        genelist = new ArrayList<>();
                        genemap.put(strain, genelist);
                    }
                    genelist.add(new Gene(null, last, last));
                }
                last = line + "\n";
                // aa = "";
            }/*
             * else { aa += line+"\n"; }
             */
            line = br.readLine();
        }
        String strain = last.split("_")[0].substring(1);
        List<Gene> genelist = null;
        if (genemap.containsKey(strain)) {
            genelist = genemap.get(strain);
        } else {
            genelist = new ArrayList<>();
            genemap.put(strain, genelist);
        }
        genelist.add(new Gene(null, last, last));
        br.close();

        for (String str : genemap.keySet()) {
            f = new File(dir, str + ".orf.fsa");
            FileWriter fw = new FileWriter(f);
            List<Gene> glist = genemap.get(str);
            for (int i = 0; i < glist.size(); i++) {
                Gene g = glist.get(i);
                fw.write(g.name);
                fw.write(g.getAa());
            }
            fw.close();
        }
    }

    public static void splitGenes(String dir, String filename, int parts) throws IOException {
        List<Gene> genelist = new ArrayList<>();
        File f = new File(dir, filename);
        BufferedReader br = new BufferedReader(new FileReader(f));
        String last = null;
        String aa = "";
        String line = br.readLine();
        while (line != null) {
            if (line.startsWith(">")) {
                if (last != null) {
                    Gene g = new Gene(null, last, last);
                    g.setAa(aa);
                    genelist.add(g);
                }
                last = line + "\n";
                aa = "";
            } else {
                aa += line + "\n";
            }
            line = br.readLine();
        }
        Gene g = new Gene(null, last, last);
        g.setAa(aa);
        genelist.add(g);
        br.close();

        int k = 0;
        int chunk = genelist.size() / parts + 1;
        System.err.println("Number of genes " + genelist.size() + " chunk size " + chunk);
        FileWriter fw = null;
        for (int i = 0; i < genelist.size(); i++) {
            g = genelist.get(i);
            if (i % chunk == 0) {
                f = new File(dir, filename.substring(0, filename.lastIndexOf('.')) + "_" + (k++) + ".aa");
                if (fw != null)
                    fw.close();
                fw = new FileWriter(f);
            }

            fw.write(g.name);
            fw.write(g.getAa());
        }
        fw.close();
    }

    public static void aaset() throws IOException {
        Set<String> set1 = new HashSet<>();
        File fa = new File("/home/sigmar/dtu/27623-AlgoInBio/week7/");
        File[] ff = fa.listFiles((dir, name) -> {
            return name.length() == 5;
        });

        for (File fb : ff) {
            File f = new File(fb, fb.getName() + ".dat");
            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line = br.readLine();
                while (line != null) {
                    String[] s = line.split("[\t ]+");
                    set1.add(s[0]);

                    line = br.readLine();
                }
                br.close();

                Set<String> set2 = new HashSet<>();
                f = new File("/home/sigmar/dtu/27623-AlgoInBio/project/train2.dat");
                br = new BufferedReader(new FileReader(f));
                line = br.readLine();
                while (line != null) {
                    String[] s = line.split("[\t ]+");
                    set2.add(s[0]);

                    line = br.readLine();
                }
                br.close();

                int s1 = set1.size();
                int s2 = set2.size();
                set1.removeAll(set2);
                int ns1 = set1.size();

                if (s1 != ns1) {
                    System.err.println(fb.getName());
                    System.err.println("\t" + s1);
                    System.err.println("\t" + s2);
                    System.err.println("\t" + ns1);
                }
            }
        }
    }

    public static void newsets() throws IOException {
        File mf = new File("/home/sigmar/dtu/new/dtu/main_project/code/SMM/");
        File[] ff = mf.listFiles(pathname -> {
            String name = pathname.getName();
            return name.length() == 5 && (name.startsWith("B") || name.startsWith("A")) && pathname.isDirectory();
        });

        for (File f : ff) {
            for (int x = 0; x < 5; x++) {
                for (int y = x + 1; y < 5; y++) {
                    FileWriter fw = new FileWriter(new File(f, "f00_" + x + "_" + y));
                    for (int u = 0; u < 5; u++) {
                        if (u != x && u != y) {
                            FileReader fr = new FileReader(new File(f, "c00" + u));
                            BufferedReader br = new BufferedReader(fr);
                            String line = br.readLine();
                            while (line != null) {
                                fw.write(line + "\n");
                                line = br.readLine();
                            }
                        }
                    }
                    fw.close();
                }
            }
        }
    }

    /*
     * public static void blastJoin( String name ) throws IOException {
     * FileReader fr = new FileReader( name ); BufferedReader br = new
     * BufferedReader( fr ); String line = br.readLine();
     *
     * Map<String,Map<String,Set<String>>> specmap = new
     * HashMap<String,Map<String,Set<String>>>();
     *
     * String stuff = null; String subject = null; String length = null; String
     * start = null; String stop = null; String score = null; String strand =
     * null;
     *
     * String thespec = null; while( line != null ) { if(
     * line.startsWith("Query=") ) { if( subject != null ) { String inspec =
     * subject.substring(0, subject.indexOf('_')); String spec =
     * stuff.substring(0, stuff.indexOf('_')); if( !spec.equals(inspec) ) {
     * Map<String,Set<String>> contigmap; if( specmap.containsKey(spec) ) {
     * contigmap = specmap.get(spec); } else { contigmap = new
     * HashMap<String,Set<String>>(); specmap.put(spec, contigmap ); }
     *
     * Set<String> hitmap; if( contigmap.containsKey( subject ) ) { hitmap =
     * contigmap.get( subject ); } else { hitmap = new HashSet<String>();
     * contigmap.put( subject, hitmap ); } hitmap.add( stuff + " " + start + " "
     * + stop + " " + score + " " + strand ); }
     *
     * subject = null; } stuff = line.substring(7).trim(); if( thespec == null )
     * thespec = stuff.split("_")[0]; } else if( line.startsWith("Length=") ) {
     * length = line; } else if( line.startsWith(">") ) { if( subject != null )
     * { String inspec = subject.substring(0, subject.indexOf('_')); String spec
     * = stuff.substring(0, stuff.indexOf('_')); if( !spec.equals(inspec) ) {
     * Map<String,Set<String>> contigmap; if( specmap.containsKey(spec) ) {
     * contigmap = specmap.get(spec); } else { contigmap = new
     * HashMap<String,Set<String>>(); specmap.put(spec, contigmap ); }
     *
     * Set<String> hitmap; if( contigmap.containsKey( subject ) ) { hitmap =
     * contigmap.get( subject ); } else { hitmap = new HashSet<String>();
     * contigmap.put( subject, hitmap ); } hitmap.add( stuff + " " + start + " "
     * + stop + " " + score + " " + strand ); } } length = null; start = null;
     * stop = null; subject = line.substring(1).trim(); } else if(
     * line.startsWith("Sbjct") ) { String[] split = line.split("[\t ]+"); if(
     * start == null ) start = split[1]; stop = split[split.length-1]; } else
     * if( length == null && subject != null ) { subject += line; } else if(
     * line.startsWith(" Score") ) { if( start != null ) { String inspec =
     * subject.substring(0, subject.indexOf('_')); String spec =
     * stuff.substring(0, stuff.indexOf('_')); if( !spec.equals(inspec) ) {
     * Map<String,Set<String>> contigmap; if( specmap.containsKey(spec) ) {
     * contigmap = specmap.get(spec); } else { contigmap = new
     * HashMap<String,Set<String>>(); specmap.put(spec, contigmap ); }
     *
     * Set<String> hitmap; if( contigmap.containsKey( subject ) ) { hitmap =
     * contigmap.get( subject ); } else { hitmap = new HashSet<String>();
     * contigmap.put( subject, hitmap ); } hitmap.add( stuff + " " + start + " "
     * + stop + " " + score + " " + strand ); } } score = line; start = null;
     * stop = null; } else if( line.startsWith(" Strand") ) { strand = line; }
     *
     * line = br.readLine(); } fr.close();
     *
     * for( String spec : specmap.keySet() ) { if( spec.contains( thespec ) ) {
     * System.out.println( spec );
     *
     * List<List<String>> sortorder = new ArrayList<List<String>>();
     *
     * Map<List<String>,List<Integer>> joinMap = new
     * HashMap<List<String>,List<Integer>>(); Map<String,Set<String>> contigmap
     * = specmap.get(spec); for( String contig : contigmap.keySet() ) {
     * Set<String> hitmap = contigmap.get(contig); List<String> hitlist = new
     * ArrayList<String>( hitmap ); hitmap.clear(); for( int i = 0; i <
     * hitlist.size(); i++ ) { for( int x = i+1; x < hitlist.size(); x++ ) {
     * String str1 = hitlist.get(i); String str2 = hitlist.get(x);
     *
     * boolean left1 = str1.contains("left"); boolean left2 =
     * str2.contains("left"); boolean minus1 = str1.contains("Minus"); boolean
     * minus2 = str2.contains("Minus"); boolean all1 = str1.contains("all");
     * boolean all2 = str2.contains("all");
     *
     * if( (all1 || all2) || (left1 != left2 && minus1 == minus2) || (left1 ==
     * left2 && minus1 != minus2) ) { String[] split1 = str1.split("[\t ]+");
     * String[] split2 = str2.split("[\t ]+");
     *
     * int start1 = Integer.parseInt( split1[1] ); int stop1 = Integer.parseInt(
     * split1[2] ); int start2 = Integer.parseInt( split2[1] ); int stop2 =
     * Integer.parseInt( split2[2] );
     *
     * if( start1 > stop1 ) { int tmp = start1; start1 = stop1; stop1 = tmp; }
     *
     * if( start2 > stop2 ) { int tmp = start2; start2 = stop2; stop2 = tmp; }
     *
     * if( (stop2-start2 > 50) && (stop1-start1 > 50) && ((start2 > start1-150
     * && start2 < stop1+150) || (stop2 > start1-150 && stop2 < stop1+150)) ) {
     * hitmap.add( str1 ); hitmap.add( str2 );
     *
     * int ind1 = str1.indexOf("_left"); if( ind1 == -1 ) ind1 =
     * str1.indexOf("_right"); if( ind1 == -1 ) ind1 = str1.indexOf("_all");
     * String str1simple = str1.substring(0,ind1); String str1compl =
     * str1.substring(0, str1.indexOf(' ', ind1));
     *
     * int ind2 = str2.indexOf("_left"); if( ind2 == -1 ) ind2 =
     * str2.indexOf("_right"); if( ind2 == -1 ) ind2 = str2.indexOf("_all");
     * String str2simple = str2.substring(0,ind2); String str2compl =
     * str2.substring(0, str2.indexOf(' ', ind2));
     *
     * /*if( minus1 != minus2 ) { if( str1compl.compareTo( str2compl ) > 0 ) {
     * str1compl += " Minus"; } else str2compl += " Minus"; }*
     *
     * if( !str2simple.equals(str1simple) ) { List<String> joinset = new
     * ArrayList<String>(); joinset.add( str1compl ); joinset.add( str2compl );
     * Collections.sort( joinset ); if( minus1 != minus2 ) joinset.set(1,
     * joinset.get(1)+" Minus");
     *
     * if( joinMap.containsKey( joinset ) ) { List<Integer> li =
     * joinMap.get(joinset); li.add( Integer.parseInt(score) ); } else {
     * List<Integer> li = new ArrayList<Integer>(); li.add(
     * Integer.parseInt(score) ); joinMap.put( joinset, li ); } } } } } } if(
     * hitmap.size() > 1 ) { System.out.println( "\t"+contig ); for( String hit
     * : hitmap ) { System.out.println( "\t\t"+hit ); } } }
     *
     * System.out.println("Printing join count");
     * Map<Integer,List<List<String>>> reverseset = new
     * TreeMap<Integer,List<List<String>>>( Collections.reverseOrder() ); for(
     * List<String> joinset : joinMap.keySet() ) { int cnt =
     * joinMap.get(joinset).size();
     *
     * if( joinset.get(0).contains("all") || joinset.get(1).contains("all") )
     * cnt -= 1000;
     *
     * if( reverseset.containsKey(cnt) ) { List<List<String>> joinlist =
     * reverseset.get(cnt); joinlist.add( joinset ); } else { List<List<String>>
     * joinlist = new ArrayList<List<String>>(); joinlist.add( joinset );
     * reverseset.put(cnt, joinlist); } }
     *
     * for( int cnt : reverseset.keySet() ) { List<List<String>> joinlist =
     * reverseset.get(cnt); for( List<String> joinset : joinlist ) {
     * System.out.println( joinset + ": " + cnt); } }
     *
     * for( int cnt : reverseset.keySet() ) { List<List<String>> joinlist =
     * reverseset.get(cnt); for( List<String> joinset : joinlist ) {
     *
     * String str1 = joinset.get(0); String str2 = joinset.get(1);
     *
     * /*for( String joinstr : joinset ) { if( str1 == null ) str1 = joinstr;
     * else { str2 = joinstr; break; } }*
     *
     * boolean minus1 = str1.contains("Minus"); str1 = str1.replace(" Minus",
     * ""); str2 = str2.replace(" Minus", ""); //boolean minus1 =
     * str1.contains("Minus"); //String str1com =
     * str1.substring(0,str1.lastIndexOf('_')); //String str2simple =
     * str1.substring(0,str2.lastIndexOf('_')); String str1simple =
     * str1.substring(0,str1.lastIndexOf('_')); String str2simple =
     * str2.substring(0,str2.lastIndexOf('_'));
     *
     * List<String> seqlist1 = null; List<String> seqlist2 = null; //boolean
     * both = false; for( List<String> sl : sortorder ) { for( String seq : sl )
     * { /*if( seq.contains(str1simple) && seq.contains(str2simple) ) { seqlist1
     * = sl; seqlist2 = sl; } else*
     *
     * if( seq.contains(str1simple) ) { if( seqlist1 == null ) seqlist1 = sl; }
     * else if( seq.contains(str2simple) ) { if( seqlist2 == null ) seqlist2 =
     * sl; } } if( seqlist1 != null && seqlist2 != null ) break; }
     *
     * /*for( List<String> sl1 : sortorder ) { for( List<String> sl2 : sortorder
     * ) { if( sl1 != sl2 ) { for( String str : sl1 ) { if( sl2.contains(str) )
     * { System.err.println( str ); System.err.println(); for( String s1 : sl1 )
     * { System.err.println( s1 ); } System.err.println(); for( String s2 : sl2
     * ) { System.err.println( s2 ); } System.err.println(); } } } } }
     *
     * int count = 0; if( seqlist1 != null ) { for( String s : seqlist1 ) { if(
     * s.contains("00006") ) count++; else if( s.contains("00034") ) count++;
     *
     * if( count == 2 ) { System.err.println(); } } }
     *
     * if( seqlist2 != null ) { for( String s : seqlist2 ) { if(
     * s.contains("00006") ) count++; else if( s.contains("00034") ) count++;
     *
     * if( count == 2 ) { System.err.println(); } } }*
     *
     * boolean left1 = str1.contains("left"); boolean left2 =
     * str2.contains("left");
     *
     * if( seqlist1 == null && seqlist2 == null ) { List<String> seqlist = new
     * ArrayList<String>(); sortorder.add( seqlist );
     *
     * if( left1 ) { if( left2 ) { if( minus1 ) { seqlist.add( str1+" reverse"
     * ); seqlist.add( str2 ); } else { seqlist.add( str2+" reverse" );
     * seqlist.add( str1 ); } } else { if( minus1 ) { seqlist.add(
     * str1+" reverse" ); seqlist.add( str2+" reverse" ); } else { seqlist.add(
     * str2 ); seqlist.add( str1 ); } } } else { if( left2 ) { if( minus1 ) {
     * seqlist.add( str2+" reverse" ); seqlist.add( str1+" reverse" ); } else {
     * seqlist.add( str1 ); seqlist.add( str2 ); } } else { if( minus1 ) {
     * seqlist.add( str2 ); seqlist.add( str1+" reverse" ); } else {
     * seqlist.add( str1 ); seqlist.add( str2+" reverse" ); } } } } else if(
     * (seqlist1 == null && seqlist2 != null) || (seqlist1 != null && seqlist2
     * == null) ) { List<String> seqlist; String selseq = null; String noseq =
     * null;
     *
     * int ind = -1; if( seqlist1 == null ) { seqlist = seqlist2; selseq = str2;
     * noseq = str1;
     *
     * String seqf = seqlist.get(0); String seql = seqlist.get( seqlist.size()-1
     * ); boolean bf = true; //(seqf.contains("left") &&
     * !seqf.contains("reverse")) || (!seqf.contains("left") &&
     * seqf.contains("reverse")); boolean bl = true; //(seql.contains("left") &&
     * seql.contains("reverse")) || (!seql.contains("left") &&
     * !seql.contains("reverse"));
     *
     * if( seqf.contains(str2simple) && bf ) ind = 0; else if(
     * seql.contains(str2simple) && bl ) ind = seqlist.size()-1; } else {
     * seqlist = seqlist1; selseq = str1; noseq = str2;
     *
     * String seqf = seqlist.get(0); String seql = seqlist.get( seqlist.size()-1
     * ); boolean bf = true; //(seqf.contains("left") &&
     * !seqf.contains("reverse")) || (!seqf.contains("left") &&
     * seqf.contains("reverse")); boolean bl = true; //(seql.contains("left") &&
     * seql.contains("reverse")) || (!seql.contains("left") &&
     * !seql.contains("reverse"));
     *
     * if( seqf.contains(str1simple) && bf ) ind = 0; else if(
     * seql.contains(str1simple) && bl ) ind = seqlist.size()-1; }
     *
     * if( ind != -1 ) { String tstr = seqlist.get(ind); boolean leftbef =
     * tstr.contains("left"); boolean leftaft = selseq.contains("left"); boolean
     * allaft = false;//selseq.contains("all"); boolean revbef =
     * tstr.contains("reverse"); //boolean revaft = selseq.contains("reverse");
     *
     * boolean leftno = noseq.contains("left"); //boolean revno =
     * selseq.contains("reverse");
     *
     * if( leftbef && revbef) { if( leftaft ) { if( ind == seqlist.size()-1 ||
     * allaft ) { if( leftno ) seqlist.add( seqlist.size(), noseq ); else
     * seqlist.add( seqlist.size(), noseq+" reverse" ); } } else { if( ind == 0
     * || allaft ) { seqlist.add( 0, selseq+" reverse" ); if( leftno )
     * seqlist.add( 0, noseq+" reverse" ); else seqlist.add( 0, noseq ); } } }
     * else if( !leftbef && !revbef ) { if( leftaft ) { if( ind == 0 || allaft )
     * { seqlist.add( 0, selseq ); if( leftno ) seqlist.add( 0, noseq+" reverse"
     * ); else seqlist.add( 0, noseq ); } } else { if( ind == seqlist.size()-1
     * || allaft ) { if( leftno ) seqlist.add( seqlist.size(), noseq ); else
     * seqlist.add( seqlist.size(), noseq+" reverse" ); } } } else if( !leftbef
     * && revbef ) { if( leftaft ) { if( ind == seqlist.size()-1 || allaft ) {
     * seqlist.add( seqlist.size(), selseq+" reverse" ); if( leftno )
     * seqlist.add( seqlist.size(), noseq ); else seqlist.add( seqlist.size(),
     * noseq+" reverse" ); } } else { if( ind == 0 || allaft ) { if( leftno )
     * seqlist.add( 0, noseq+" reverse" ); else seqlist.add( 0, noseq ); } }
     *
     * //if( leftno ) seqlist.add( 0, noseq+" reverse" ); //else seqlist.add( 0,
     * noseq ); } else if( leftbef && !revbef ) { if( leftaft ) { if( ind == 0
     * || allaft ) { if( leftno ) seqlist.add( 0, noseq+" reverse" ); else
     * seqlist.add( 0, noseq ); } } else { if( ind == seqlist.size()-1 || allaft
     * ) { seqlist.add( seqlist.size(), selseq ); if( leftno ) seqlist.add(
     * seqlist.size(), noseq ); else seqlist.add( seqlist.size(),
     * noseq+" reverse" ); } }
     *
     * //if( leftno ) seqlist.add( 0, noseq+" reverse" ); //else seqlist.add( 0,
     * noseq ); }
     *
     * /*if( selseq.contains(str1simple) ) { if( selseq.contains("reverse ") ) {
     * if( left1 ) { if( left2 ) { seqlist.add( ind+1, str2 ); } else {
     * seqlist.add( ind+1, str2+" reverse" ); } } else { if( left2 ) {
     * seqlist.add( ind, str2+" reverse" ); } else { seqlist.add( ind, str2 ); }
     * } } else { if( left1 ) { if( left2 ) { seqlist.add( ind, str2+" reverse"
     * ); } else { seqlist.add( ind, str2 ); } } else { if( left2 ) {
     * seqlist.add( ind+1, str2 ); } else { seqlist.add( ind+1, str2+" reverse"
     * ); } } } } else { if( selseq.contains("reverse ") ) { if( left1 ) { if(
     * left2 ) { seqlist.add( ind+1, str1 ); } else { seqlist.add( ind,
     * str1+" reverse" ); } } else { if( left2 ) { seqlist.add( ind+1,
     * str1+" reverse" ); } else { seqlist.add( ind, str1 ); } } } else { if(
     * left1 ) { if( left2 ) { seqlist.add( ind, str1+" reverse" ); } else {
     * seqlist.add( ind+1, str1 ); } } else { if( left2 ) { seqlist.add( ind,
     * str1 ); } else { seqlist.add( ind+1, str1+" reverse" ); } } } }* } } else
     * if( seqlist1 != seqlist2 ) { String selseq1 = null; String selseq2 =
     * null;
     *
     * int ind1 = -1; if( seqlist1.get(0).contains(str1simple) ) { ind1 = 0;
     * selseq1 = seqlist1.get(0); } else if( seqlist1.get( seqlist1.size()-1
     * ).contains(str1simple) ) { ind1 = seqlist1.size()-1; selseq1 =
     * seqlist1.get( seqlist1.size()-1 ); }
     *
     * int ind2 = -1; if( seqlist2.get(0).contains(str2simple) ) { ind2 = 0;
     * selseq2 = seqlist2.get(0); } else if( seqlist2.get( seqlist2.size()-1
     * ).contains(str2simple) ) { ind2 = seqlist2.size()-1; selseq2 =
     * seqlist2.get( seqlist2.size()-1 ); }
     *
     * boolean success = false;
     *
     * if( selseq1 == null || selseq2 == null ) { System.err.println("bleh"); }
     * else { System.err.println( "joining: " + seqlist1 ); System.err.println(
     * "and: " + seqlist2 );
     *
     * boolean lef1 = selseq1.contains("left"); boolean lef2 =
     * selseq2.contains("left"); boolean rev1 = selseq1.contains("reverse");
     * boolean rev2 = selseq2.contains("reverse");
     *
     * boolean bb = false; if( bb ) { System.err.println("subleh"); } else { if(
     * lef1 && !left1 ) { if( rev1 && ind1 == 0 ) seqlist1.add( 0,
     * str1+" reverse" ); else if( ind1 == seqlist1.size()-1 ) seqlist1.add(
     * seqlist1.size(), str1 ); } else if( !lef1 && left1 ) { if( rev1 && ind1
     * == seqlist1.size()-1 ) seqlist1.add( seqlist1.size(), str1+" reverse" );
     * else if( ind1 == 0 ) seqlist1.add( 0, str1 ); }
     *
     * if( lef2 && !left2 ) { if( rev2 && ind2 == 0 ) seqlist2.add( 0,
     * str2+" reverse" ); else if( ind2 == seqlist2.size()-1 ) seqlist2.add(
     * seqlist2.size(), str2 ); } else if( !lef2 && left2 ) { if( rev2 && ind2
     * == seqlist2.size()-1 ) seqlist2.add( seqlist2.size(), str2+" reverse" );
     * else if( ind2 == 0 ) seqlist2.add( 0, str2 ); }
     *
     * boolean left1beg = seqlist1.get(0).contains("left"); boolean rev1beg =
     * seqlist1.get(0).contains("reverse"); boolean left1end =
     * seqlist1.get(seqlist1.size()-1).contains("left"); boolean rev1end =
     * seqlist1.get(seqlist1.size()-1).contains("reverse");
     *
     * boolean left2beg = seqlist2.get(0).contains("left"); boolean rev2beg =
     * seqlist2.get(0).contains("reverse"); boolean left2end =
     * seqlist2.get(seqlist2.size()-1).contains("left"); boolean rev2end =
     * seqlist2.get(seqlist2.size()-1).contains("reverse");
     *
     * if( seqlist1.get(0).contains(str1simple) ) { if(
     * seqlist2.get(0).contains(str2simple) ) { if( ((left1beg && !rev1beg) ||
     * (!left1beg && rev1beg)) && (((left2beg && !rev2beg) || (!left2beg &&
     * rev2beg))) ) { Collections.reverse( seqlist2 ); for( int u = 0; u <
     * seqlist2.size(); u++ ) { String val = seqlist2.get(u); if(
     * val.contains(" reverse") ) seqlist2.set( u, val.replace(" reverse", "")
     * ); else { int end = val.length()-1; while( val.charAt(end) == 'I' )
     * end--; seqlist2.set(u, val.substring(0,
     * end+1)+" reverse"+val.substring(end+1, val.length()) ); } } success =
     * true; seqlist1.addAll(0, seqlist2); } } else { if( ((left1beg &&
     * !rev1beg) || (!left1beg && rev1beg)) && (((left2end && rev2end) ||
     * (!left2end && !rev2end))) ) { success = true; seqlist1.addAll(0,
     * seqlist2); } } } else { //if( seqlist1.indexOf(str1) == seqlist1.size()-1
     * ) { if( seqlist2.get(0).contains(str2simple) ) { if( ((left1end &&
     * rev1end) || (!left1end && !rev1end)) && (((left2beg && !rev2beg) ||
     * (!left2beg && rev2beg))) ) { success = true;
     * seqlist1.addAll(seqlist1.size(), seqlist2); } } else { if( ((left1end &&
     * rev1end) || (!left1end && !rev1end)) && (((left2end && rev2end) ||
     * (!left2end && !rev2end))) ) { Collections.reverse( seqlist2 ); for( int u
     * = 0; u < seqlist2.size(); u++ ) { String val = seqlist2.get(u); if(
     * val.contains(" reverse") ) seqlist2.set( u, val.replace(" reverse", "")
     * ); else { int end = val.length()-1; while( val.charAt(end) == 'I' )
     * end--; seqlist2.set(u, val.substring(0,
     * end+1)+" reverse"+val.substring(end+1, val.length()) ); } } success =
     * true; seqlist1.addAll(seqlist1.size(), seqlist2); } } }
     *
     * if( success ) { if( !sortorder.remove( seqlist2 ) ) {
     * System.err.println("no remove"); } }
     * System.err.println("result is: "+seqlist1); } } } else {
     * System.err.println( "same shit " + seqlist1 + " " + str1 + " " + str2 );
     * /*for( int k = 0; k < seqlist1.size(); k++ ) { if(
     * seqlist1.get(k).contains(str1) ) seqlist1.set(k, seqlist1.get(k)+"I");
     * else if( seqlist1.get(k).contains(str2) ) seqlist1.set(k,
     * seqlist1.get(k)+"I"); }* int i = 0; i = 2; } } //} //} //} }
     *
     * System.out.println("join"); for( List<String> so : sortorder ) { for( int
     * i = 0; i < so.size(); i++ ) { String s = so.get(i); String ss =
     * s.substring(0, s.indexOf("_contig")+12); if( i == so.size()-1 ) {
     * System.out.println( ss + (s.contains("everse") ? "_reverse" : "") ); }
     * else { String n = so.get(i+1); String nn = n.substring(0,
     * n.indexOf("_contig")+12); if( ss.equals(nn) ) { System.out.println( ss +
     * (s.contains("everse") ? "_reverse" : "") ); i++; } else {
     * System.out.println( ss + (s.contains("everse") ? "_reverse" : "") ); } }
     * } /*for( String s : so ) { System.out.println(s); }*
     * System.out.println(); } } } }
     */

    public static void blast2Filt(String name, String filtname) throws IOException {
        FileReader fr = new FileReader(name);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        Set<String> contigs = new HashSet<String>();
        String stuff = null;
        while (line != null) {
            if (line.startsWith("Query=")) {
                stuff = line.substring(7).trim();
            } else if (line.contains("contig")) {
                contigs.add(stuff);
            }
            // String[] split = line.trim().split("[\t ]+");
            // contigs.add( split[0].trim() );

            line = br.readLine();
        }
        fr.close();

        FileWriter fw = new FileWriter(filtname);
        for (String contig : contigs) {
            fw.write(contig.substring(0, 14) + "\n");
        }
    }

    public static void contigShare(String name) throws IOException {
        FileReader fr = new FileReader(name);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        Set<String> contigs = new HashSet<String>();
        while (line != null) {
            String[] split = line.trim().split("[\t ]+");
            contigs.add(split[0].trim());

            line = br.readLine();
        }
        fr.close();

        System.err.println(contigs.size());
        for (String contig : contigs) {
            System.err.println(contig);
        }
    }

    public static void trimFasta(String name, String newname, String filter, boolean inverted) throws IOException {
        Set<String> filterset = new HashSet<String>();
        File ff = new File(filter);
        FileReader fr = new FileReader(ff);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            filterset.add(line);
            line = br.readLine();
        }
        fr.close();

        trimFasta(name, newname, filterset, inverted);
    }

    public static void trimFasta(String name, String newname, Set<String> filterset, boolean inverted) throws IOException {
        FileWriter fw = new FileWriter(newname);
        BufferedWriter bw = new BufferedWriter(fw);

        Reader fr;
        if (name.endsWith("gz")) {
            FileInputStream fis = new FileInputStream(name);
            GZIPInputStream gis = new GZIPInputStream(fis);
            fr = new InputStreamReader(gis);
        } else {
            fr = new FileReader(name);
        }
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        String seqname = null;
        while (line != null) {
            if (line.startsWith(">")) {
                if (inverted) {
                    seqname = line;
                    for (String f : filterset) {
                        if (line.contains(f)) {
                            seqname = null;
                            break;
                        }
                    }
                    if (seqname != null)
                        bw.write(seqname + "\n");
                } else {
                    seqname = null;
                    for (String f : filterset) {
                        if (line.contains(f)) {
                            bw.write(line + "\n");
                            seqname = line;
                            break;
                        }
                    }
                }
            } else if (seqname != null) {
                bw.write(line + "\n");
            }

            line = br.readLine();
        }
        fr.close();

        bw.flush();
        fw.close();
    }

    public static void flankingFasta(String name, String newname) throws IOException {
        FileWriter fw = new FileWriter(newname);
        BufferedWriter bw = new BufferedWriter(fw);

        int fasti = 60;
        FileReader fr = new FileReader(name);
        BufferedReader br = new BufferedReader(fr, 100000000);
        String line = br.readLine();
        String seqname = null;
        StringBuilder seq = new StringBuilder();
        while (line != null) {
            if (line.startsWith(">")) {
                if (seqname != null) {
                    if (seq.length() < 200) {
                        fw.write(seqname + "_all" + "\n");
                        for (int i = 0; i < seq.length(); i += fasti) {
                            fw.write(seq.substring(i, Math.min(seq.length(), i + fasti)) + "\n");
                        }
                    } else {
                        fw.write(seqname + "_left" + "\n");
                        for (int i = 0; i < 150; i += fasti) {
                            fw.write(seq.substring(i, Math.min(150, i + fasti)) + "\n");
                        }
                        fw.write(seqname + "_right" + "\n");
                        for (int i = seq.length() - 151; i < seq.length(); i += fasti) {
                            fw.write(seq.substring(i, Math.min(seq.length(), i + fasti)) + "\n");
                        }
                    }
                }
                int endind = line.indexOf(' ');
                if (endind == -1)
                    endind = line.indexOf('\t');
                if (endind == -1)
                    endind = line.length();
                seqname = line.substring(0, endind);
                seq = new StringBuilder();
            } else {
                seq.append(line);
            }

            line = br.readLine();
        }
        fr.close();

        bw.flush();
        fw.close();
    }

    public static void eyjo( String blast, String filter, String result, int threshold ) throws IOException {
        Map<String,String>	filtermap = new HashMap<String,String>();
        if( filter != null ) {
            FileReader fr = new FileReader( filter );
            BufferedReader br = new BufferedReader( fr );
            String line = br.readLine();
            String name = null;
            while( line != null ) {
                if( line.startsWith(">") ) {
                    int i = line.indexOf(' ');
                    if( i == -1 ) i = line.length();
                    name = line.substring(1, i);
                } else {
                    filtermap.put( name, line );
                }
                line = br.readLine();
            }
            br.close();
        }

        Map<String,Map<String,List<String>>>	treemapmap = new HashMap<String,Map<String,List<String>>>();
        Map<String,List<String>> treemap = null;// = hlmnew HashMap<String,List<String>>();

        FileReader	fr = new FileReader( blast );
        String hit = null;
        BufferedReader	br = new BufferedReader( fr );
        String line = br.readLine();
        boolean hitb = false;
        while( line != null ) {
            if( line.startsWith("Query=") ) {
                hit = line.substring(7).trim();

                line = br.readLine();
                while( !line.startsWith("Length=") ) {
                    line = br.readLine();
                }
                String lenstr = line.substring(7);
                int len = Integer.parseInt( lenstr );

                if( len >= 300 ) {
                    int i = hit.indexOf(' ');
                    if( i > 0 ) hit = hit.substring(0,i);

                    String val = filtermap.get( hit );
                    if( treemapmap.containsKey( val ) ) treemap = treemapmap.get( val );
                    else {
                        treemap = new HashMap<String,List<String>>();
                        treemapmap.put( val, treemap );
                    }
                    hitb = true;
                } else hitb = false;
            } else if( hitb && line.startsWith("***** No hits") ) {
                String group = "No hits";

                List<String>	hitlist;
                if( treemap.containsKey( group ) ) {
                    hitlist = treemap.get(group);
                } else {
                    hitlist = new ArrayList<String>();
                    treemap.put( group, hitlist );
                }
                hitlist.add( hit+" 0.0 0/0 (0%)" );
            } else if( hitb && line.startsWith(">") ) {
                String group = line.substring(2);

                line = br.readLine();
                while( !line.startsWith("Length") ) {
                    group += line;
                    line = br.readLine();
                }

                String idstr = "";
                String estr = "";
                line = br.readLine();
                while( !line.contains("Strand=") ) {
                    int i = line.indexOf("Expect = ");
                    if( i != -1 ) {
                        estr = line.substring(i+9);
                    }

                    i = line.indexOf("Identities = ");
                    if( i != -1 ) {
                        int k = line.indexOf(',');
                        idstr = line.substring(i+13,k);
                    }

                    line = br.readLine();
                }

                int svidx = idstr.indexOf('(');
                int esvidx = idstr.indexOf('%', svidx+1);
                int id = Integer.parseInt( idstr.substring(svidx+1, esvidx) );
                if( id < threshold ) {
                    group = "No hits";
                    hit += " 0.0 0/0 (0%)";
                } else {
                    hit += " "+idstr;
                    hit += " "+estr;
                }

                List<String>	hitlist;
                if( treemap.containsKey( group ) ) {
                    hitlist = treemap.get(group);
                } else {
                    hitlist = new ArrayList<String>();
                    treemap.put( group, hitlist );
                }
                hitlist.add( hit );
            }
            line = br.readLine();
        }
        br.close();

        Map<String,Integer>	count = new HashMap<String,Integer>();
        for( String val : filtermap.keySet() ) {
            String val2 = filtermap.get( val );
            if( count.containsKey( val2 ) ) {
                count.put( val2, count.get(val2)+1 );
            } else count.put( val2, 1 );
        }

        for( String val : treemapmap.keySet() ) {
            if( count.get(val) > 20 ) {
                treemap = treemapmap.get( val );

                FileWriter	fw = new FileWriter( result+"_"+val+".txt" );
                fw.write("total: 0 subtot: 0\n");

                List<HitList>	lhit = new ArrayList<HitList>();
                for( String group : treemap.keySet() ) {
                    List<String> hitlist = treemap.get( group );
                    lhit.add( new HitList( group, hitlist ) );
                }
                Collections.sort( lhit );

                for( HitList hlist : lhit ) {
                    String 			group = hlist.group;
                    List<String>	hitlist = hlist.hitlist;

                    String first = "No_hits";
                    String lst = group;
                    if( !group.contains("No hits") ) {
                        int i = group.indexOf(' ');
                        first = group.substring(0,i);
                        lst = group.substring(i+1);
                    }

                    String[] split = lst.split(";");
                    fw.write( split[ split.length-1 ] );
                    for( int i = split.length-2; i >= 0; i-- ) {
                        fw.write( " : " + split[i] );
                    }
                    fw.write( " : root" );
                    fw.write( "\n>"+first+"  "+hitlist.size()+"\n(" );
                    fw.write( hitlist.get(0) );
                    for( int i = 1; i < hitlist.size(); i++ ) {
                        fw.write( ","+hitlist.get(i) );
                    }
                    fw.write(")\n\n");
                }
                fw.close();
            }
        }
    }

    static void writeSimplifiedCluster(String filename, Map<Set<String>, Set<Map<String, Set<String>>>> clusterMap) throws IOException {
        FileWriter fos = new FileWriter(filename);
        for (Set<String> set : clusterMap.keySet()) {
            Set<Map<String, Set<String>>> mapset = clusterMap.get(set);
            fos.write(set.toString() + "\n");
            int i = 0;
            for (Map<String, Set<String>> erm : mapset) {
                fos.write((i++) + "\n");

                for (String erm2 : erm.keySet()) {
                    Set<String> erm3 = erm.get(erm2);
                    fos.write("\t" + erm2 + "\n");
                    fos.write("\t\t" + erm3.toString() + "\n");
                }
            }
        }
        fos.close();
    }

    public static void simmi() throws IOException {
        FileReader fr = new FileReader("thermustype.blastout");
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        String current = null;
        GeneSet.StrId currteg = null;
        int currlen = 0;
        boolean done = false;
        Map<String, GeneSet.StrId> tegmap = new HashMap<String, GeneSet.StrId>();
        while (line != null) {
            String trim = line.trim();
            if (trim.startsWith("Query=")) {
                String[] split = trim.substring(7).trim().split("[ ]+");
                current = split[0];
                done = false;
            } else if (trim.startsWith("Length=")) {
                currlen = Integer.parseInt(trim.substring(7).trim());
            } else if (line.startsWith(">") && !done) {
                int i = line.lastIndexOf('|');
                if (i == -1)
                    i = 0;
                String teg = line.substring(i + 1).trim();
                line = br.readLine();
                while (!line.startsWith("Length")) {
                    teg += line;
                    line = br.readLine();
                }
                //if (teg.contains("Thermus") || teg.startsWith("t.")) {
                currteg = new GeneSet.StrId(teg, currlen);
                tegmap.put(current, currteg);
                //}
            } else if (trim.startsWith("Ident") && !done) {
                int sv = trim.indexOf('(');
                int svl = trim.indexOf('%', sv + 1);

                currteg.id = Integer.parseInt(trim.substring(sv + 1, svl));

                done = true;
            }

            line = br.readLine();
        }
        fr.close();

        System.err.println(tegmap.size());

        //int[] iv = { 0, 10, 16, 50, 8, 8, 8, 8, 60, 50, 30, 50, 150, 150, 80, 50 };
        int[] iv = { 0, 10, 16, 50, 100, 100, 60, 30, 500, 150, 500, 500, 30, 50, 200};//, 30, 30 };
        for (int i = 0; i < iv.length-1; i++) {
            iv[i] += 1;
        }
        int[] isum = new int[iv.length];
        isum[0] = iv[0];
        for (int i = 1; i < iv.length; i++) {
            isum[i] = isum[i - 1] + iv[i];
        }

        FileOutputStream fos = new FileOutputStream("noname.txt");
        PrintStream pos = new PrintStream(fos);
        //pos.println( "name\tacc\tspecies\tlen\tident\tdoi\tpubmed\tjournal\tauth\tsub_auth\tsub_date\tcountry\tsource\ttemp\tpH" );
        pos.println( "name\tacc\tfullname\tspecies\tlen\tident\tcountry\tsource\tdoi\tpubmed\tauthor\tjournal\tsub_auth\tsub_date\tlat_lon\tdate\ttitle\tcolor\ttemp\tpH" );

        fr = new FileReader("export.nds");
        br = new BufferedReader(fr);
        line = br.readLine();
        while (line != null) {
            String name = line.substring(isum[0], isum[1]).trim();
            String acc = line.substring(isum[1], isum[2]).trim();
            String fullname = line.substring(isum[2], isum[3]).trim();
            String country = line.substring(isum[3], isum[4]).trim();
            String source = line.substring(isum[4], isum[5]).trim();
            String doi = line.substring(isum[5], isum[6]).trim();
            String pubmed = line.substring(isum[6], isum[7]).trim();
            String author = line.substring(isum[7], isum[8]).trim().replace("\"", "");
            String journal = line.substring(isum[8], isum[9]).trim();
            String sub_auth = line.substring(isum[9], isum[10]).trim().replace("\"", "");
            String sub_date = line.substring(isum[10], isum[11]).trim().replace("\"", "");
            String lat_lon = line.substring(isum[11], isum[12]).trim();
            String date = line.substring(isum[12], isum[13]).trim();
            String title = line.substring( isum[13], Math.min( isum[14], line.length() ) ).trim();
            //String length = line.substring(isum[14], isum[15]).trim();
            //String arb = isum.length > 16 && isum[16] <= line.length() ? line.substring(isum[15], isum[16]).trim() : "";

			/*String country = line.substring(isum[7], isum[8]).trim();
			String doi = line.substring(isum[8], isum[9]).trim();
			String pubmed = line.substring(isum[9], isum[10]).trim();
			String journal = line.substring(isum[10], isum[11]).trim();
			String auth = line.substring(isum[11], isum[12]).trim();
			String sub_auth = line.substring(isum[12], isum[13]).trim();
			String sub_date = line.substring(isum[13], isum[14]).trim();
			String source = isum.length > 14 ? line.substring(isum[14], isum[15] - 1).trim() : "";*/

            if (tegmap.containsKey(name)) {
                // StrId teg = tegmap.get(name);
                GeneSet.StrId teg = tegmap.remove(name);
                if( teg.name.contains("Thermus") || teg.name.contains("Meiothermus") || teg.name.contains("Marinithermus") || teg.name.contains("Oceanithermus") || teg.name.contains("Vulcani") )
                    pos.println(name + "\t" + acc + "\t" + fullname + "\t" + teg.name + "\t" + teg.len + "\t" + teg.id + "\t" + country + "\t" + source + "\t" + doi + "\t" + pubmed + "\t" + author + "\t" + journal + "\t" + sub_auth + "\t" + sub_date + "\t" + lat_lon + "\t" + date + "\t" + title /*+ "\t" + arb*/ + "\t" + teg.color + "\t\t" );
            }
            // System.err.println( line.substring( isum[7], isum[8] ).trim() );

            line = br.readLine();
        }
        fr.close();

        for (String name : tegmap.keySet()) {
            GeneSet.StrId teg = tegmap.get(name);
            pos.println(name + "\t" + name + "\t" + name + "\t" + teg.name + "\t" + teg.len + "\t" + teg.id + "\t" + "Simmaland" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + "" + "\t" + teg.color + "\t" + "\t" );
        }
        fos.close();
    }

    static List<String> res = new ArrayList<>();
    public static void fixFile(String fastafile, String blastlist, String outfile) throws IOException {
        Set<String> faset = new HashSet<>();
        FileReader fr = new FileReader(fastafile);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            if (line.startsWith(">")) {
                String[] split = line.split("[\t ]+");
                faset.add(split[0].substring(1));

                int ind = line.lastIndexOf('|');
                String resval;
                if (ind > 0) {
                    String sub = line.substring(ind);
                    resval = line.substring(1, ind + 1);
                    split = sub.split("_");
                    ind = 1;
                } else {
                    split = line.split("_");
                    ind = 2;
                    resval = split[0].substring(1) + "_" + split[1];
                }

                int val = 0;
                try {
                    val = Integer.parseInt(split[ind]);
                } catch (Exception e) {
                    System.err.println(split[ind]);
                }
                while (val >= res.size())
                    res.add(null);
                res.set(val, resval);
            }

            line = br.readLine();
        }
        br.close();

        FileWriter fw = new FileWriter(outfile);

        int count = 0;
        int tcount = 0;
        Set<String> regset = new HashSet<>();
        String last = "";
        String lastline = "";
        String lastsp = "";
        fr = new FileReader(blastlist);
        br = new BufferedReader(fr);
        line = br.readLine();
        while (line != null) {
            String[] split = line.split("[\t ]+");
            if (!split[0].equals(last)) {
                int sind = split[0].lastIndexOf("_");
                String shorter = split[0].substring(0, sind);

                sind = last.lastIndexOf("_");
                String lshorter = null;
                if (sind > 0)
                    lshorter = last.substring(0, sind);

                if (shorter.equals(lshorter)) {
                    if (!split[1].equals(lastsp)) {
                        count++;
                        // System.err.println( "erm " + line + "\n    " +
                        // lastline + "  " + lastsp );
                    }
                    tcount++;
                } else {
                    /*
                     * if( regset.contains(shorter) ) { System.err.println(
                     * split[0] + " " + last + "  " + shorter ); } else
                     * regset.add( shorter );(
                     */

                    if (split[0].startsWith("_")) {
                        String[] lsp = split[0].split("_");
                        int val = Integer.parseInt(lsp[1]);
                        // System.err.println( lsp[1] + "  " + res.get( val ) );
                        String str = res.get(val) + split[0];
                        for (int i = 1; i < split.length; i++) {
                            str += "\t" + split[i];
                        }
                        fw.write(str + "\n");

                        regset.add(res.get(val) + shorter);
                    } else {
                        fw.write(line + "\n");

                        regset.add(shorter);
                    }
                }

                lastsp = split[1];
            }

            last = split[0];
            lastline = line;

            line = br.readLine();
        }
        fw.close();
        br.close();

        System.err.println(count + "  " + tcount);

        faset.removeAll(regset);
        for (String s : faset) {
            System.err.println(s);
        }
        System.err.println(faset.size());
    }

    public static void funcMappingStatic( Reader rd ) throws IOException {
        //Map<String,Set<String>> unipGo = new HashMap<String,Set<String>>();
        Map<String,String>	symbolmap = new HashMap<>();
        FileWriter fw = new FileWriter("/root/sp2go.txt");
        BufferedReader br = new BufferedReader(rd);
        String line = br.readLine();
        String prev = null;
        while (line != null) {
            if( !line.startsWith("!") ) {
                String[] split = line.split("\t");
                if( split.length > 4 ) {
                    String id = split[1];
                    String sm = split[2];
                    String go = split[4];
                    symbolmap.put(id, sm);
                    if( go.startsWith("GO:") ) {
                        if( !id.equals( prev ) ) {
                            if( prev == null ) fw.write( id + " = " + go );
                            else fw.write( "\n" + id + " = " + go );
                        } else fw.write( " " + go );
                    }
                    prev = split[1];
                }
            }
            line = br.readLine();
        }
        br.close();
        fw.close();

        fw = new FileWriter( "/root/smap.txt" );
        for( String id : symbolmap.keySet() ) {
            String sm = symbolmap.get( id );
            fw.write(id + "\t" + sm + "\n");
        }
        fw.close();
    }

    public static StringBuilder getSelectedSeqs(JTable table, List<Gene> genelist ) {
        StringBuilder sb = new StringBuilder();

        int[] rr = table.getSelectedRows();
        for (int r : rr) {
            int cr = table.convertRowIndexToModel(r);
            Gene gg = genelist.get(cr);
            //if(gg.species != null) {
            sb.append(gg.name).append(":\n");
            //for (String sp : gg.species.keySet()) {
            Annotation tv = gg.getTegeval();
            sb.append(">").append(tv.getName()).append(" ").append(tv.getSpecies()).append(" ").append(tv.eval).append("\n");
            for (int i = 0; i < tv.getLength(); i += 70) {
                sb.append(tv.getSubstring(i, Math.min(i + 70, tv.getLength()))).append("\n");
            }
        }

        return sb;
    }

    public static double[] load16SCorrelation(Reader r, List<String> order) throws IOException {
        List<Double> ret = new ArrayList<>();

        Map<String, Map<String, Integer>> tm = new TreeMap<>();

        String currentSpec = null;
        Map<String, Integer> subtm = null;
        BufferedReader br = new BufferedReader(r);
        String line = br.readLine();
        while (line != null) {
            if (line.startsWith("Query=")) {
                currentSpec = line.substring(7).split("_")[0];
                if (!tm.containsKey(currentSpec)) {
                    subtm = new TreeMap<String, Integer>();
                    tm.put(currentSpec, subtm);
                } else
                    currentSpec = null;
            } else if (line.startsWith(">") && currentSpec != null) {
                String thespec = line.substring(2).split("_")[0];
                if (!subtm.containsKey(thespec)) {
                    line = br.readLine();
                    String trim = line.trim();
                    while (!trim.startsWith("Score")) {
                        line = br.readLine();
                        trim = line.trim();
                    }
                    int score = 0;
                    try {
                        score = Integer.parseInt(trim.split("[ ]+")[2]);
                    } catch( Exception e ) {
                        System.err.println( line );
                    }

                    subtm.put(thespec, score);
                }
            }

            line = br.readLine();
        }

        for (String key : tm.keySet()) {
            subtm = tm.get(key);
            for (String subkey : subtm.keySet()) {
                ret.add(subtm.get(subkey).doubleValue());
            }

            order.add(key);
        }

        System.err.println(order);

        double sum = 0.0;
        for (double d : ret) {
            sum += d;
        }

        double[] dret = new double[ret.size()];
        for (int i = 0; i < ret.size(); i++) {
            dret[i] = ret.get(i) / sum;
        }

        return dret;
    }

    private static Map<String, String> koMapping(Reader r, List<Function> funclist, List<Gene> genelist) throws IOException {
        Map<String, String> ret = new HashMap<String, String>();
        BufferedReader br = new BufferedReader(r);
        String line = br.readLine();
        String name = null;
        while (line != null) {
            if (line.startsWith("NAME")) {
                String[] split = line.split("[\t ]+");
                if (split.length > 1) {
                    int com = split[1].indexOf(',');
                    if (com != -1) {
                        name = split[1].substring(1, com);
                    } else {
                        name = split[1].substring(1);
                    }
                }
            } else if (line.contains("TTJ:") || line.contains("TTH:")) {
                ret.put(name, line.trim().replaceAll("[\t ]+", ""));
            }

            line = br.readLine();
        }

        return ret;
    }

    private static void blastAlign(Reader r, String main, String second) {
        // BufferedReader br = new BufferedReader();
    }

    public static void recursiveSet(int fin, int val) {
        if (val < fin) {
            recursiveSet(fin, val + 1);
        } else {

        }

        //javax.web
    }

    static Map<String, String> swapmap = new HashMap<>();
    public static void printnohits(String[] stuff, File dir, File dir2) throws IOException {
        //loci2aasequence(stuff, dir2);
        for (String st : stuff) {
            System.err.println("Unknown genes in " + swapmap.get(st + ".out"));

            File ba = new File(dir, "new2_" + st + ".out");
            BufferedReader br = new BufferedReader(new FileReader(ba));
            String line = br.readLine();
            String name = null;
            // String ac = null;
            while (line != null) {
                if (line.startsWith("Query= ")) {
                    name = line.substring(8).split(" ")[0];
                }

                if (line.contains("No hits")) {
                    // System.err.println( name + "\t" +
                    // aas.get(swapmap.get(st+".out")+" "+name) );
                }

                line = br.readLine();
            }
            br.close();
        }
    }

    public static void createConcatFsa(String[] names, File dir) throws IOException {
        FileWriter fw = new FileWriter(new File(dir, "all.fsa"));
        for (String name : names) {
            File f = new File(dir, name + ".fsa");
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while (line != null) {
                if (line.startsWith(">"))
                    fw.write(">" + name + "_" + line.substring(1) + "\n");
                else
                    fw.write(line + "\n");

                line = br.readLine();
            }
            br.close();
        }
        fw.close();
    }

    public static void dummy() throws IOException {}
}
