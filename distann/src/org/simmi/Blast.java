package org.simmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Blast {
	private StringBuilder aaSearch(String query, Map<String,Tegeval> aas) {
		/*
		 * aquery.name = query; int ind = Arrays.binarySearch(aas, aquery); if(
		 * ind < 0 ) { System.err.println(); } return ind < 0 ? null : aas[ ind
		 * ].aas;
		 */

		Tegeval aass = aas.get(query);
		return aass == null ? null : aass.getProteinSequence();
	}

	private Gene genestuff( List<Set<String>> uclusterlist, String query, String desc, String teg, String val, Map<String,Gene> ret ) {
		Gene gene = null;
		
		if( ret.containsKey(val) ) {
			gene = ret.get(val);
			//int i = 0;
			//System.err.println( uclusterlist.size() );
			for( Set<String>	clusterset : uclusterlist ) {
				//System.err.println( i++ );
				/*boolean mugg = false;
				if( clusterset.size() == 4 ) {
					int count = 0;
					for( String s : clusterset ) {
						if( s.contains("t.oshimai") || s.contains("mt.silvanus") ) count++;
					}
					if( count >= 2 ) {
						System.err.println( clusterset );
						mugg = true;
					}
				}*/
				if( clusterset.contains( query ) ) {
					Tegeval tvl = gene.tegeval;
					if( !clusterset.contains( tvl.name ) ) {
						Gene newgene = genestuff( uclusterlist, query, desc, teg, val+" new", ret );
						
						if( newgene == null ) {
							newgene = new Gene(null, gene.refid + " new", desc, teg);
							newgene.allids = new HashSet<String>();
							//newgene.species = new HashMap<String, Teginfo>();
							//newgene.refid = gene.refid + " new";
							ret.put( newgene.refid, newgene );
						}
						
						return newgene;
						
						//break;
					}
					//if( newgene != gene ) break;
					//newgene = genestuff( gene, clusterset, desc, teg, id, ret );
				}
			}
		}
		
		return gene;
	}
	
	public void panCoreFromNRBlast(Reader rd, Reader rd2, String outfile, String outfile2, Map<String, Gene> ret, Map<String, String> allgenes, Map<String, Set<String>> geneset, 
			Map<String, Set<String>> geneloc, Map<String, Gene> locgene, Set<String> poddur, List<Set<String>> uclusterlist, Map<String,Tegeval> aas, Map<String,Contig> contigmap ) 
			throws IOException {
		FileWriter fw = null;
		if (outfile != null)
			fw = new FileWriter(outfile);

		/*
		 * Set<String> extra = new HashSet<String>();
		 * 
		 * extra.add("protein of unknown function DUF820");
		 * extra.add("ABC transporter ATP-binding protein");
		 * extra.add("short-chain dehydrogenase/reductase SDR");
		 * extra.add("two component transcriptional regulator, winged helix family"
		 * ); extra.add("Tetratricopeptide TPR_2 repeat protein");
		 * extra.add("extracellular solute-binding protein family 1");
		 * extra.add("histidine kinase"); extra.add("cytochrome c class I");
		 * extra.add("NUDIX hydrolase");
		 * extra.add("GCN5-related N-acetyltransferase");
		 * extra.add("acyl-CoA dehydrogenase domain protein");
		 * extra.add("protein of unknown function DUF6 transmembrane");
		 * extra.add("response regulator receiver protein");
		 * extra.add("Peptidase M23"); extra.add("glycosyltransferase");
		 * extra.add("metallophosphoesterase");
		 * extra.add("ABC transporter permease protein");
		 * extra.add("UspA domain protein");
		 * extra.add("transposase, IS605 OrfB family");
		 * extra.add("methyltransferase");
		 * extra.add("aminotransferase class I and II");
		 * extra.add("PilT protein domain protein");
		 * extra.add("hypothetical protein");
		 * extra.add("nucleotidyltransferase");
		 * extra.add("serine/threonine protein kinase");
		 * extra.add("transcriptional regulator");
		 * extra.add("ABC-2 type transporter");
		 * extra.add("AMP-dependent synthetase and ligase");
		 * extra.add("DNA polymerase beta domain protein region");
		 * extra.add("ABC transporter-like protein");
		 * extra.add("Radical SAM domain protein");
		 * extra.add("Extracellular ligand-binding receptor");
		 * extra.add("HAD-superfamily hydrolase, subfamily IA, variant 3");
		 * extra.add("metal dependent hydrolase");
		 * extra.add("sugar ABC transporter, permease protein");
		 * extra.add("acetyl-CoA acetyltransferase");
		 * extra.add("LmbE family protein");
		 * extra.add("transcriptional repressor");
		 * extra.add("transposase IS605 OrfB family");
		 * extra.add("beta-lactamase domain protein");
		 * extra.add("dehydrogenase"); extra.add("enoyl-CoA hydratase");
		 * extra.add("metal dependent phosphohydrolase");
		 * extra.add("two-component response regulator");
		 * extra.add("ABC transporter, permease protein"); extra.add(
		 * "alkyl hydroperoxide reductase/ Thiol specific antioxidant/ Mal");
		 * extra.add("SMC domain protein");
		 * extra.add("protein of unknown function DUF88");
		 * extra.add("Roadblock/LC7 family protein");
		 * extra.add("TetR family transcriptional regulator");
		 * extra.add("translation initiation factor IF-2");
		 * extra.add("glycosyl transferase family 2");
		 * extra.add("Integrase catalytic region");
		 * extra.add("putative lipoprotein");
		 * extra.add("tetratricopeptide repeat domain-containing protein");
		 * extra.add("ABC transporter permease");
		 * extra.add("conserved domain-containing protein");
		 * extra.add("MutT/nudix family protein");
		 * extra.add("sensor histidine kinase");
		 * extra.add("CBS domain-containing protein");
		 * extra.add("transporter, major facilitator family");
		 * extra.add("acyl-CoA dehydrogenase"); extra.add("acetyltransferase");
		 * extra.add("universal stress protein family");
		 * extra.add("putative hydrolase");
		 * extra.add("sugar ABC transporter permease");
		 * extra.add("ATP-dependent protease La"); extra.add("permease");
		 * extra.add("metallo-beta-lactamase family protein");
		 * extra.add("immunogenic protein"); extra.add(
		 * "leucine-, isoleucine-, valine-, threonine-, and alanine-binding");
		 * extra.add("branched-chain amino acid ABC transporter permease");
		 * extra.add("membrane protein"); extra.add("serine protease");
		 * extra.add
		 * ("oxidoreductase, short-chain dehydrogenase/reductase family");
		 * extra.
		 * add("branched-chain amino acid ABC transporter ATP-binding protein");
		 * extra.add("cytochrome c-552");
		 * extra.add("hypothetical protein TaqDRAFT_4901");
		 * extra.add("putative PIN domain-containing protein");
		 * extra.add("ATPase"); extra.add("long-chain-fatty-acid--CoA ligase");
		 * extra.add("thioredoxin"); extra.add("aspartyl-tRNA synthetase");
		 * extra.add("thioredoxin reductase");
		 * extra.add("ggdef domain-containing protein");
		 * extra.add("transposase BAC55317.2");
		 * extra.add("integrase, catalytic region");
		 * extra.add("ribose-phosphate pyrophosphokinase");
		 * extra.add("N-acetyl-gamma-glutamyl-phosphate reductase");
		 * extra.add("putative oxidoreductase");
		 * extra.add("glycolate oxidase subunit GlcE");
		 * extra.add("GntR family transcriptional regulator");
		 * extra.add("3-oxoacyl-[acyl-carrier-protein] reductase");
		 * extra.add("PilT protein domain-containing protein");
		 * extra.add("acetylglutamate kinase");
		 * extra.add("spermidine/putrescine import ATP-binding protein PotA");
		 */

		// uclusterlist with avg identity
		BufferedReader br = new BufferedReader(rd);
		parseBlast( br, fw, ret, allgenes, geneset, geneloc, locgene, poddur, uclusterlist, false, false, aas, contigmap );
		br.close();
		if (fw != null) {
			fw.close();
		}
		
		fw = null;
		if (outfile2 != null)
			fw = new FileWriter(outfile2);

		if( rd2 != null ) {
			BufferedReader br2 = new BufferedReader(rd2);
			//String line = br.readLine();
			//System.err.println( line );
			parseBlast( br2, fw, ret, allgenes, geneset, geneloc, locgene, poddur, uclusterlist, true, true, aas, contigmap );
			br2.close();
			if (fw != null) {
				fw.close();
			}
		}
	}
	
	public void parseBlast( BufferedReader br, FileWriter fw, Map<String, Gene> ret, Map<String, String> allgenes, Map<String, Set<String>> geneset, 
			Map<String, Set<String>> geneloc, Map<String, Gene> locgene, Set<String> poddur, List<Set<String>> uclusterlist, boolean old, boolean addon, Map<String,Tegeval> aas, Map<String,Contig> contigmap ) throws IOException {
		String query = null;
		int start = 0;
		int stop = 0;
		int ori = 0;
		String evalue = null;
		String line = br.readLine();
		int cnt = 0;
		//Tegeval preval = null;
		while (line != null) {
			String trim = line.trim();
			cnt++;
			//if (query != null && (trim.startsWith(">ref") || trim.startsWith(">sp") || trim.startsWith(">pdb") || trim.startsWith(">dbj") || trim.startsWith(">gb") || trim.startsWith(">emb") || trim.startsWith(">pir") || trim.startsWith(">tpg"))) {
			if (query != null && trim.startsWith(">") ) {
				//String[] split = trim.split("\\|");

				/*line = br.readLine();
				while( !line.startsWith("Length=") && !line.startsWith("Query=") ) {
					trim += " "+line.trim();
					line = br.readLine();
				}*/
				
				int i = trim.indexOf(".aa", 2);
				int aaa = 4;
				if( i == -1 ) {
					i = 2;
					aaa = 0;
				}
				int i2 = trim.indexOf(' ', i);
				String id;
				//boolean addon = i == -1;
				if( addon ) {
					int si = trim.indexOf('|');
					int ei = trim.lastIndexOf('|'); //, i2);
					id = trim.substring(si+1,ei);
				} else {
					id = trim.substring(i+aaa,i2).trim();
				}
				//String id = i == -1 ? trim.substring(1,i2).trim() : trim.substring(i+4,i2).trim();
				String desc = trim.substring(i2+1);
				//String id = split[1];
				//String desc = split[2];
				String teg = "";

				int idx = desc.lastIndexOf('[');
				int idx2 = desc.indexOf(']', idx);
				String newline = "";
				if (idx > idx2 || idx == -1) {
					newline = br.readLine();
					if (!newline.startsWith("Query=")) {
						line = line + newline;
						trim = line.trim();

						i = trim.indexOf(' ', 2);
						id = trim.substring(2,i);
						desc = trim.substring(i+1);
						/*split = trim.split("\\|");

						id = split[1];
						desc = split[2];*/

						idx = desc.lastIndexOf('[');
					}
				}

				if (idx > 0) {
					teg = desc.substring(idx);
					desc = desc.substring(0, idx - 1).trim();
				} else {
					desc = desc.trim();
				}

				/*
				 * if( extra.contains(desc) ||
				 * desc.equals("conserved hypothetical protein") ||
				 * desc.equals("transposase") || desc.equals("hydrolase") ||
				 * desc.equals("ABC transporter related") ||
				 * desc.equals("transporter") ||
				 * desc.equals("putative cytoplasmic protein") || desc.equals(
				 * "binding-protein-dependent transport systems inner membrane component"
				 * ) || desc.equals("major facilitator superfamily MFS_1") ||
				 * desc.equals("transposase IS4 family protein") ||
				 * desc.equals("inner-membrane translocator") ) { desc = desc +
				 * " " + id; }
				 */

				/*String[] qsplit = null;
				if (query != null) {
					qsplit = query.split("_");
				}*/

				Set<String> set;
				//String padda = qsplit[0];
				int ival = query.indexOf("|");
				if( ival == -1 ) ival = query.length();
				int ival2 = query.indexOf("ontig");
				if( ival2 == -1 ) ival2 = query.length();
				i = query.lastIndexOf("_", Math.min(ival,ival2) );
				if( i == -1 ) {
					System.err.println();
				}
				String padda = query.substring(0, i);
				
				/*if( padda.startsWith("Ocean") ) padda = "o.profundus";
				else if( padda.startsWith("Marin") ) padda = "m.hydrothermalis";
				else if( padda.contains("Silvanus") ) padda = "mt.silvanus";
				else if( padda.contains("Ruber") ) padda = "mt.ruber";
				else if( padda.contains("t.thermophilus_SG0_5JP17_16") ) {
					padda = "t.thermSG0_5JP17_16";
				}*/
				//if( padda.endsWith(".fna") ) padda = padda.substring(0, padda.length()-4);
				
				if (geneset.containsKey(padda)) {
					set = geneset.get(padda);
				} else {
					set = new HashSet<String>();
					System.err.println("new padda " + padda);
					geneset.put(padda, set);
				}

				String val = id;
				// set.add( desc );

				// int first = query.indexOf('_');
				// int sec = query.indexOf('_', first+1 );

				// System.err.println("contig "+contig);

				//StringBuilder aa = aaSearch(query);
				/*
				 * int nq = query.lastIndexOf('_'); int mq =
				 * query.lastIndexOf('_', nq-1); String nquery; if( mq != -1 ) {
				 * nquery = query.substring(0, nq); } else { nquery = query; }
				 */
				/*StringBuilder dn = dnaSearch(query);
				if( dn == null ) {
					System.err.println( query );
					System.err.println();
				}*/
				// System.err.println( "ermm " + ori );
				double deval = 0.0;
				try {
					deval = Double.parseDouble(evalue);
				} catch( Exception e ) {
					e.printStackTrace();
				}				
				Gene gene;
				String check = addon ? "_" + aaSearch(query, aas) : val;
				if( ret.containsKey( check ) ) {
					gene = genestuff( uclusterlist, query, desc, teg, check, ret );
					if( addon ) {
						gene.name = desc;
						gene.allids.remove( check );
					}
				} else {
					gene = new Gene(null, id, desc, teg);
					gene.allids = new HashSet<String>();
					//gene.species = new HashMap<String, Teginfo>();
					ret.put(val, gene);
				}
				gene.allids.add(id);
				set.add(val);
				
				//aa, dn
				Tegeval tv = aas.get( query );//new Tegeval(gene, teg, deval, query, contig, contloc, start, stop, ori);
				
                if( tv == null || tv.ori != ori || tv.start != start || tv.stop != stop ) {
					System.err.println();
				}
                                
                tv.setGene( gene );
				tv.setTegund( padda );
				tv.setEval( deval );
				
				/*if( preval != null ) {
					Contig precontig = preval.getContshort();
					Contig curcontig = tv.getContshort();
					boolean bu = precontig.equals( curcontig );
					//System.err.println( bu + "  " + precontig.toString().equals( curcontig.toString()) + "  " + (precontig == curcontig) );
					if( bu ) {
						tv.setPrevious( preval );
						tv.setNum( preval.getNum()+1 );
						curcontig.end = tv;
					} else {
						tv.setNum(0);
						curcontig.start = tv;
						curcontig.end = tv;
					}
				} else tv.setNum( 0 );
				preval = tv;*/
				
				/*Teginfo stv;
				if (!gene.species.equals(padda)) {
					stv = new Teginfo();
					gene.species = padda;
					gene.teginfo = stv;
				} else {
					stv = gene.teginfo;
				}*/
				
				/*if( addon ) {
					Tegeval rem = null;
					for( Tegeval te : stv.tset ) {
						if( te.cont.equals( query ) ) {
							rem = te;
							break;
						}
					}
					if( rem != null ) {
						stv.tset.remove( rem );
					}
				}*/
				gene.tegeval = tv;
				//stv.add( tv );
				// gene.blastspec = teg;
				/*
				 * if( gene.species == null ) { gene.species = new
				 * HashMap<String,Set<Tegeval>>(); }
				 */

				if (!allgenes.containsKey(val) || allgenes.get(val) == null) {
					//allgenes.put(val, split.length > 1 ? teg + " " + id : null);
					allgenes.put(val, teg + " " + id );
				}

				Set<String> locset = null;
				if (geneloc.containsKey(val)) {
					locset = geneloc.get(val);
				} else {
					locset = new HashSet<String>();
					geneloc.put(val, locset);
				}
				// locset.add( swapmap.get(name)+"_"+query + " " + evalue );
				locset.add(query + " " + evalue);

				//int li = query.lastIndexOf('_');
				//int ln = query.lastIndexOf('_', li-1);
				//String 	queryshort = query.substring(0, ln)+query.substring(li);
				locgene.put(query, gene);

				query = null;
				if (fw != null) {
					/*
					 * if( line.lastIndexOf('[') > line.indexOf(']') ) { String
					 * newline = br.readLine(); line += newline.trim(); }
					 */
					fw.write(trim + "\n");
				}

				if (newline.startsWith("Query=")) {
					line = newline;
					continue;
				}
			} else if ( trim.contains("No hits")) {
				if( !addon ) {
					Gene gene;
	
					StringBuilder aa = aaSearch(query, aas);
					String aaid = "_"+aa;
					
					
					/*String padda = query.substring(0, query.indexOf('_')); //split("_")[0];
					if( padda.endsWith(".fna") ) padda = padda.substring(0,padda.length()-4);*/
					
					String padda;
					if( query != null ) {
						int ival = query.indexOf("|");
						if( ival == -1 ) ival = query.length();
						int ival2 = query.indexOf("ontig");
						if( ival2 == -1 ) ival2 = query.length();
						int i = query.lastIndexOf("_", Math.min(ival,ival2) );
						padda = query.substring(0, i);
					} else {
						padda = "";
					}
					
					/*if( padda.startsWith("Ocean") ) padda = "o.profundus";
					else if( padda.startsWith("Marin") ) padda = "m.hydrothermalis";
					else if( padda.contains("Silvanus") ) padda = "mt.silvanus";
					else if( padda.contains("Ruber") ) padda = "mt.ruber";
					else if( padda.contains("t.thermophilus_SG0_5JP17_16") ) padda = "t.thermSG0_5JP17_16";*/
					
					if (ret.containsKey(aaid)) {
						gene = ret.get(aaid);
					} else {
						gene = new Gene(null, aaid, aaid, padda);
						ret.put(aaid, gene);
						gene.allids = new HashSet<String>();
						//gene.species = new HashMap<String, Teginfo>();
						ret.put(aaid, gene);
						gene.refid = aaid;
					}
					gene.allids.add(aaid);
	
					double deval = -1.0;
					/*
					 * try { deval = Double.parseDouble(evalue); } catch( Exception
					 * e ) { System.err.println("ok"); }
					 */
					// gene.species.put( padda, new Tegeval( padda, deval,
					// aas.get(query), query ) );
					//Tegeval stv;
					/*if (!gene.species.equals(padda)) {
						stv = new Teginfo();
						gene.species = padda;
						gene.tegeval = stv;
						
						System.err.println( "new annars " + padda );
					} else {
						stv = gene.tegeval;
					}*/
	
					String contigstr = null;
					String contloc = null;
	
					/*int first = query.indexOf('_');
					int sec = query.indexOf('_', first + 1);
					if (sec != -1) {
						contig = query.substring(0, sec);
						contloc = query.substring(first + 1);
					} else {
						contig = query;
						contloc = query.substring(first + 1);
					}*/
					
					int fi = query.indexOf('_');
					int li = query.lastIndexOf('_');
					contigstr = query.substring(0, li);
					contloc = query.substring(fi+1,query.length());
	
					//StringBuilder aastr = aaSearch(query);
					/*int nq = query.lastIndexOf('_');
					int mq = query.lastIndexOf('_', nq - 1);
					String nquery;
					if (mq != -1) {
						nq = mq;
						nquery = query.substring(0, nq);
					} else {
						nquery = query;
					}*/
	
					Contig contig = contigmap.containsKey( contigstr ) ? contigmap.get( contigstr ) : new Contig( contigstr );
					//StringBuilder dn = dnaSearch( query ); //dnaa.get(nquery);
					
					Tegeval tv = aas.get( query ); //new Tegeval(gene, padda, deval, query, contig, contloc, start, stop, ori);
							//new Tegeval(gene, padda, deval, aastr, dn, query, contig, contloc, start, stop, ori);
					tv.setGene( gene );
					tv.setTegund( padda );
					tv.setEval( deval );
					if( tv == null || tv.ori != tv.ori || tv.start != start || tv.stop != stop ) {
						System.err.println();
					}
					
					gene.tegeval = tv;
					//stv.add( tv );
					
					/*if( preval != null ) {
						Contig precontig = preval.getContshort();
						Contig curcontig = tv.getContshort();
						boolean bu = precontig.equals( curcontig );
						//System.err.println( bu + "  " + precontig.toString().equals( curcontig.toString()) + "  " + (precontig == curcontig) );
						if( bu ) {
							tv.setPrevious( preval );
							tv.setNum( preval.getNum()+1 );
							curcontig.end = tv;
						} else {
							tv.setNum(0);
							curcontig.start = tv;
							curcontig.end = tv;
						}
					} else tv.setNum( 0 );
					preval = tv;*/
					
					if (!allgenes.containsKey(aaid) || allgenes.get(aaid) == null) {
						allgenes.put(aaid, "Thermus " + aaid);
					}
	
					Set<String> locset = null;
					if (geneloc.containsKey(aaid)) {
						locset = geneloc.get(aaid);
					} else {
						locset = new HashSet<String>();
						geneloc.put(aaid, locset);
					}
					
					locset.add(query + " -1.0");
					//int li = query.lastIndexOf('_');
					//int ln = query.lastIndexOf('_', li-1);
					//String queryshort = query.substring(0, ln)+query.substring(li);
					locgene.put(query, gene);
				}

				if (fw != null)
					fw.write(line + "\n");
			} else if (trim.startsWith("Query=")) {
				// if( trim.)
				//query = trim.substring(6).trim().split("[ ]+")[0].replace(".fna", "");
				
				/*int count = 0;
				if( query.contains("contig") ) {
					int k = query.indexOf('_');
					while( k != -1 ) {
						k = query.indexOf('_', k+1);
						count++;
					}
				} else if( query.contains("|") && !query.startsWith("mt.") ) {
					count = 3;
				}
				
				if( count == 3 ) {
					int li = query.lastIndexOf('_');
					int ln = query.lastIndexOf('_', li-1);
					query = query.substring(0, ln)+query.substring(li);
				}*/
				
				String[] split = trim.split("#");
				if ( split.length > 1 && split.length < 4) {
					String newline = br.readLine();
					line = line + newline;
					trim = line.trim();
					split = trim.split("#");
				}
				query = trim.substring(6).trim().split("[ ]+")[0]; //.replace(".fna", "");

				if (split.length >= 3) {
					start = Integer.parseInt(split[1].trim());
					stop = Integer.parseInt(split[2].trim());
				}
				if (split.length >= 4) {
					ori = Integer.parseInt(split[3].trim());
				}
				
				int ival = query.indexOf("|");
				if( ival == -1 ) ival = query.length();
				int ival2 = query.indexOf("ontig");
				if( ival2 == -1 ) ival2 = query.length();
				int i = query.lastIndexOf("_", Math.min(ival,ival2) );
				String padda = query.substring(0, i);
				
				poddur.add( padda );
				
				if (fw != null) {
					// String[] split = trim.split("#");
					// if( split.length < 4 ) {
					// line += br.readLine();
					// }
					fw.write(line + "\n");
				}
			} else {
				if( old ) {
					if (query != null && (trim.startsWith("ref|") || trim.startsWith("sp|") || trim.startsWith("pdb|") || trim.startsWith("dbj|") || trim.startsWith("gb|") || trim.startsWith("emb|") || trim.startsWith("pir|") || trim.startsWith("tpg|"))) {
						//} else if (query != null /*&& trim.startsWith("Sequences producing")*/ ) {
						//line = br.readLine();
						//line = br.readLine();
						trim = line.trim();
						
						String[] split = trim.split("[\t ]+");
						evalue = split[split.length - 1];
						if( evalue.length() == 0 ) {
							System.err.println();
						}
						if (fw != null)
							fw.write(trim + "\n");
					}
				} else {
					if (query != null /*&& trim.startsWith("Sequences producing")*/ ) {
						//} else if (query != null /*&& trim.startsWith("Sequences producing")*/ ) {
						//line = br.readLine();
						//line = br.readLine();
						trim = line.trim();
						
						String[] split = trim.split("[\t ]+");
						evalue = split[split.length - 1];
						if( evalue.length() == 0 ) {
							System.err.println();
						}
						if (fw != null)
							fw.write(trim + "\n");
					}
				}
			}

			if( cnt % 1000 == 0 ) {
				System.err.println( cnt );
			}
			line = br.readLine();
		}
	}

	public Map<String, Gene> panCoreFromNRBlast(String[] names, File dir, Map<String,Tegeval> aas, Map<String,Contig> contigmap) throws IOException {
		Map<String, Gene> ret = new HashMap<String, Gene>();

		Map<String, String> allgenes = new HashMap<String, String>();
		Map<String, Set<String>> geneset = new HashMap<String, Set<String>>();
		Map<String, Set<String>> geneloc = new HashMap<String, Set<String>>();
		Map<String, Gene> locgene = new HashMap<String, Gene>();

		// PrintStream ps = new PrintStream("/home/sigmar/iron.giant");
		// System.setOut( ps );

		Set<String> poddur = new HashSet<String>();
		for (String name : names) {
			File f = new File(dir, name);
			// FileReader fr = new FileReader( f );
			panCoreFromNRBlast(new FileReader(f), null, null, null, ret, allgenes, geneset, geneloc, locgene, poddur, null, aas, contigmap);
		}

		for (String name : geneset.keySet()) {
			Set<String> set = geneset.get(name);
			System.out.println(name + " genes total: " + set.size());
			geneset.put(name, set);
		}

		Set<String> allset = new HashSet<String>(allgenes.keySet());
		for (String name : geneset.keySet()) {
			Set<String> gset = geneset.get(name);
			allset.retainAll(gset);
		}
		System.out.println("Core genome size: " + allset.size());
		System.out.println("Pan genome size: " + allgenes.size());

		Set<String> nameset = null;

		for (String gname : allset) {
			System.out.println(gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname));
		}

		boolean info = true;

		for (String aname : poddur) {
			allset = new HashSet<String>(allgenes.keySet());
			nameset = new HashSet<String>(poddur);
			nameset.remove(aname);
			for (String tname : nameset) {
				allset.removeAll(geneset.get(tname));
			}
			// System.err.println( "Genes found only in " + swapmap.get(aname) +
			// "\t" + allset.size() );

			if (allset.size() > 0)
				System.out.println("Genes only in " + aname + "\t" + allset.size());
			if (info) {
				for (String gname : allset) {
					System.out.println(gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname));
				}
			}
		}

		for (String aname : poddur) {
			allset = new HashSet<String>(allgenes.keySet());
			nameset = new HashSet<String>(poddur);
			nameset.remove(aname);
			allset.removeAll(geneset.get(aname));
			for (String tname : nameset) {
				allset.retainAll(geneset.get(tname));
			}

			Set<String> reset = new HashSet<String>();
			for (String name : nameset) {
				// reset.add( swapmap.get(name) );
				reset.add(name);
			}

			if (allset.size() > 0)
				System.out.println("Genes only in all of " + reset + "\t" + allset.size());
			if (info)
				printflubb(allset, allgenes, geneloc);
		}

		List<String> poddulist = new ArrayList<String>(poddur);
		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				allset = new HashSet<String>(allgenes.keySet());
				nameset = new HashSet<String>(poddulist);
				// nameset.add( names[i] );
				// nameset.add( names[y] );
				nameset.remove(poddulist.get(i));
				nameset.remove(poddulist.get(y));

				allset.removeAll(geneset.get(poddulist.get(i)));
				allset.removeAll(geneset.get(poddulist.get(y)));
				for (String tname : nameset) {
					allset.retainAll(geneset.get(tname));
				}

				Set<String> reset = new HashSet<String>();
				// reset.add( swapmap.get(names[i]) );
				// reset.add( swapmap.get(names[y]) );

				for (String name : nameset) {
					// reset.add( swapmap.get(name) );
					reset.add(name);
				}

				if (allset.size() > 0)
					System.out.println("Genes only in all of " + reset + "\t" + allset.size());
				if (info)
					printflubb(allset, allgenes, geneloc);
			}
		}

		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				allset = new HashSet<String>(allgenes.keySet());
				nameset = new HashSet<String>(poddulist);
				// nameset.add( names[i] );
				// nameset.add( names[y] );
				nameset.remove(poddulist.get(i));
				nameset.remove(poddulist.get(y));

				allset.retainAll(geneset.get(poddulist.get(i)));
				allset.retainAll(geneset.get(poddulist.get(y)));
				for (String tname : nameset) {
					allset.removeAll(geneset.get(tname));
				}

				Set<String> reset = new HashSet<String>();
				// reset.add( swapmap.get(names[i]) );
				// reset.add( swapmap.get(names[y]) );
				reset.add(poddulist.get(i));
				reset.add(poddulist.get(y));

				/*
				 * for( String name : nameset ) { reset.add( swapmap.get(name)
				 * ); }
				 */

				if (allset.size() > 0)
					System.out.println("Genes only in all of " + reset + "\t" + allset.size());
				if (info)
					printflubb(allset, allgenes, geneloc);
			}
		}

		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				for (int k = y + 1; k < poddulist.size(); k++) {
					allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k) }), info, false);
					if (info)
						printflubb(allset, allgenes, geneloc);

					allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k) }), info, true);
					if (info)
						printflubb(allset, allgenes, geneloc);
				}
			}
		}

		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				for (int k = y + 1; k < poddulist.size(); k++) {
					for (int l = k + 1; l < poddulist.size(); l++) {
						allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l) }), info, false);
						if (info) {
							for (String gname : allset) {
								System.out.println(gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname));
							}
						}

						allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l) }), info, true);
						if (info) {
							for (String gname : allset) {
								System.out.println(gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname));
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				for (int k = y + 1; k < poddulist.size(); k++) {
					for (int l = k + 1; l < poddulist.size(); l++) {
						for (int m = l + 1; m < poddulist.size(); m++) {
							allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l), poddulist.get(m) }), info, false);
							if (info)
								printflubb(allset, allgenes, geneloc);

							allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l), poddulist.get(m) }), info, true);
							if (info)
								printflubb(allset, allgenes, geneloc);
						}
					}
				}
			}
		}

		for (int i = 0; i < poddulist.size(); i++) {
			for (int y = i + 1; y < poddulist.size(); y++) {
				for (int k = y + 1; k < poddulist.size(); k++) {
					for (int l = k + 1; l < poddulist.size(); l++) {
						for (int m = l + 1; m < poddulist.size(); m++) {
							for (int n = m + 1; n < poddulist.size(); n++) {
								allset = flubb(allgenes.keySet(), poddulist, geneset, Arrays.asList(new String[] { poddulist.get(i), poddulist.get(y), poddulist.get(k), poddulist.get(l), poddulist.get(m), poddulist.get(n) }), info, false);
								if (info)
									printflubb(allset, allgenes, geneloc);
							}
						}
					}
				}
			}
		}

		System.out.println("Unique genes total: " + allgenes.size());

		// ps.close();

		return ret;
	}

	public static void printflubb(Set<String> allset, Map<String, String> allgenes, Map<String, Set<String>> geneloc) {
		for (String gname : allset) {
			System.out.println(gname + "\t" + allgenes.get(gname) + "\t" + geneloc.get(gname));
		}
	}

	public static Set<String> flubb(Set<String> allgeneskeyset, List<String> poddulist, Map<String, Set<String>> geneset, Collection<String> rpoddur, boolean info, boolean invert) {
		Set<String> allset = new HashSet<String>(allgeneskeyset);
		Set<String> nameset = new HashSet<String>(poddulist);
		// nameset.add( names[i] );
		// nameset.add( names[y] );

		nameset.removeAll(rpoddur);
		// nameset.remove( poddulist.get(i) );
		// nameset.remove( poddulist.get(y) );
		// nameset.remove( poddulist.get(k) );

		Set<String> reset = new HashSet<String>();
		if (invert) {
			for (String padda : rpoddur) {
				allset.removeAll(geneset.get(padda));
			}
			// allset.removeAll( geneset.get( poddulist.get(i) ) );
			// allset.removeAll( geneset.get( poddulist.get(y) ) );
			// allset.removeAll( geneset.get( poddulist.get(k) ) );
			for (String tname : nameset) {
				allset.retainAll(geneset.get(tname));
			}

			for (String name : nameset) {
				// reset.add( swapmap.get(name) );
				reset.add(name);
			}
		} else {
			for (String padda : rpoddur) {
				allset.retainAll(geneset.get(padda));
			}
			for (String tname : nameset) {
				allset.removeAll(geneset.get(tname));
			}
			for (String name : rpoddur) {
				reset.add(name);
			}
		}

		/*
		 * reset.add( swapmap.get(names[i]) ); reset.add( swapmap.get(names[y])
		 * ); reset.add( swapmap.get(names[k]) );
		 */

		if (allset.size() > 0)
			System.out.println("Genes only in all of " + reset + "\t" + allset.size());
		return allset;
	}
}
