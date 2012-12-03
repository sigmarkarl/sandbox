package org.simmi.server;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.Fusiontables.Query;
import com.google.api.services.fusiontables.Fusiontables.Query.SqlGet;
import com.google.api.services.fusiontables.FusiontablesRequestInitializer;
import com.google.api.services.fusiontables.model.Sqlresponse;
import com.matis.eurofir.webservices.FDQL;
import com.matis.eurofir.webservices.Ws.PseudoResult;

public class EuroFIRServlet extends HttpServlet {
	/*public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter pw = resp.getWriter();
		
		req.get
		EuroFIRWebService.getFoodFDTP(ids);
		//req.get
		//resp.setContentType("");
		
		//pw.println
	}*/
	
	static String apikey = "AIzaSyD5RTPW-0W9I9K2u70muKiq-rHXL2qhjzk";
	static String componentId = "1V220glQaJXgeWrimZ5gDiyRwhKMNp437ZeAnRNI";
	static String foodId = "1kVdzllCGHpktvP7jjwVGuQ6M5XF3qUMkBBq9Cn4";
	static String componentValueId = "1iPhnOf7BlPQSeMz1zsanxA2mqQ2Kv0PYo-BQE9U";
	static String referenceId = "1qQ34cWDUcgmsms9A3kZ8BuWwOrxf5sdkJ0USJnU";
	
	static Map<String,Set<String>>	tableColumnMap = new HashMap<String,Set<String>>();
	
	static {
		Set<String>	foodSet = new HashSet<String>();
		
		foodSet.add( "OriginalFoodCode" );
		foodSet.add( "OriginalFoodName" );
		foodSet.add( "EnglishFoodName" );
		foodSet.add( "ScientificFoodName" );
		foodSet.add( "OtherFoodNames" );
		foodSet.add( "OriginalFoodGroupCode" );
		foodSet.add( "FoodGroupIS1" );
		foodSet.add( "FoodGroupIS2" );
		foodSet.add( "FoodGroupIS3" );
		foodSet.add( "CodexFoodStandards" );
		foodSet.add( "ArticleNumber" );
		foodSet.add( "E_number" );
		foodSet.add( "INS_code" );
		foodSet.add( "LangualCodes" );
		foodSet.add( "CODEXAdditives" );
		foodSet.add( "CODEXFood" );
		foodSet.add( "CODEXContaminants" );
		foodSet.add( "FAOBalanceSheet" );
		foodSet.add( "CIAAFood" );
		foodSet.add( "EuroCode2" );
		foodSet.add( "AgriculturalConditions" );
		foodSet.add( "Cuisine" );
		foodSet.add( "EdiblePortion" );
		foodSet.add( "WastePortion" );
		foodSet.add( "NatureofEdiblePortion" );
		foodSet.add( "NatureofWaste" );
		foodSet.add( "TypicalServingSize" );
		foodSet.add( "TypicalWeightperPiece" );
		foodSet.add( "Colour" );
		foodSet.add( "FinalPreparation" );
		foodSet.add( "SpecificGravity" );
		foodSet.add( "NitrogenProteinFactor" );
		foodSet.add( "FattyAcidFactor" );
		foodSet.add( "GenericImage" );
		foodSet.add( "SpecificImage" );
		foodSet.add( "Producer" );
		foodSet.add( "Distributor" );
		foodSet.add( "Retailer" );
		foodSet.add( "AreaOfOrigin" );
		foodSet.add( "AreaOfProcessing" );
		foodSet.add( "AreaofConsumption" );
		foodSet.add( "WebPublishReady" );
		foodSet.add( "ListOfIngredients" );
		foodSet.add( "Remarks" );
		foodSet.add( "DateOfGeneration" );
		foodSet.add( "GeneratedBy" );
		foodSet.add( "DateOfUpdate" );
		foodSet.add( "UpdatedBy" );
		
		Set<String>	componentSet = new HashSet<String>();
		
		componentSet.add( "EuroFIRComponentIdentifier" );
		componentSet.add( "OriginalComponentCode" );
		componentSet.add( "OriginalComponentName" );
		componentSet.add( "EnglishComponentName" );
		componentSet.add( "Algorithm" );
		componentSet.add( "Unit" );
		componentSet.add( "GrunnefniIS" );
		componentSet.add( "YflIS" );
		componentSet.add( "UflIS" );
		componentSet.add( "InndratturIS" );
		componentSet.add( "FAgroupIS" );
		componentSet.add( "WebPublishReady" );
		componentSet.add( "Remarks" );
		componentSet.add( "DateofGeneration" );
		componentSet.add( "GeneratedBy" );
		componentSet.add( "DateOfUpdate" );
		componentSet.add( "UpdatedBy" );
		
		Set<String>	componentValueSet = new HashSet<String>();
		
		componentValueSet.add( "OriginalFoodCode" );
		componentValueSet.add( "OriginalComponentCode" );
		componentValueSet.add( "SelectedValue" );
		componentValueSet.add( "Unit" );
		componentValueSet.add( "Matrixunit" );
		componentValueSet.add( "ValueType" );
		componentValueSet.add( "AcquisitionType" );
		componentValueSet.add( "DateofEvaluation" );
		componentValueSet.add( "DateofEvaluationDisp" );
		componentValueSet.add( "N" );
		componentValueSet.add( "AnalyticalPortionSize" );
		componentValueSet.add( "NoofAnalyticalPortionReplicates" );
		componentValueSet.add( "Mean" );
		componentValueSet.add( "Median" );
		componentValueSet.add( "Minimum" );
		componentValueSet.add( "Maximum" );
		componentValueSet.add( "StandardDeviation" );
		componentValueSet.add( "StandardError" );
		componentValueSet.add( "MethodType" );
		componentValueSet.add( "MethodIndicator" );
		componentValueSet.add( "MethodParameter" );
		componentValueSet.add( "MethodFK" );
		componentValueSet.add( "NoofPrimarySampleUnits" );
		componentValueSet.add( "SampleFK" );
		componentValueSet.add( "OriginalReferenceCode" );
		componentValueSet.add( "QI_Eurofir" );
		componentValueSet.add( "QualityAssessmentFK" );
		componentValueSet.add( "SamplingStrategy" );
		componentValueSet.add( "DateOfAnalysis" );
		componentValueSet.add( "DateOfAnalysisDisp" );
		componentValueSet.add( "Remarks" );
		componentValueSet.add( "DateofGeneration" );
		componentValueSet.add( "GeneratedBy" );
		componentValueSet.add( "DateOfUpdate" );
		componentValueSet.add( "UpdatedBy" );
		
		Set<String>	referenceSet = new HashSet<String>();
		
		referenceSet.add( "OriginalReferenceCode" );
		referenceSet.add( "StandardReferenceCode" );
		referenceSet.add( "AcquisitionType" );
		referenceSet.add( "ReferenceType" );
		referenceSet.add( "OrgCitation" );
		referenceSet.add( "Title" );
		referenceSet.add( "Authors" );
		referenceSet.add( "PublicationDate" );
		referenceSet.add( "Version" );
		referenceSet.add( "OriginalLanguage" );
		referenceSet.add( "ISBN" );
		referenceSet.add( "FirstEditionDate" );
		referenceSet.add( "EditionNumber" );
		referenceSet.add( "NumberofPages" );
		referenceSet.add( "BookTitle" );
		referenceSet.add( "Editors" );
		referenceSet.add( "LongJournalName" );
		referenceSet.add( "AbbreviatedJournalName" );
		referenceSet.add( "ISSN" );
		referenceSet.add( "Volume" );
		referenceSet.add( "Issue" );
		referenceSet.add( "Pages" );
		referenceSet.add( "SeriesName" );
		referenceSet.add( "SeriesNumber" );
		referenceSet.add( "ReportTitle" );
		referenceSet.add( "FileFormat" );
		referenceSet.add( "WWW" );
		referenceSet.add( "DOI" );
		referenceSet.add( "PublicationMedium" );
		referenceSet.add( "OperatingSystem" );
		referenceSet.add( "Validfrom" );
		referenceSet.add( "Remarks" );
		referenceSet.add( "DateOfGeneration" );
		referenceSet.add( "GeneratedBy" );
		referenceSet.add( "DateOfUpdate" );
		referenceSet.add( "UpdatedBy" );
		referenceSet.add( "Citation" );
		
		tableColumnMap.put( "food", foodSet );
		tableColumnMap.put( "component", componentSet );
		tableColumnMap.put( "componentValue", componentValueSet );
		tableColumnMap.put( "reference", referenceSet );
	};
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost( HttpServletRequest req, HttpServletResponse resp ) {
		try {
			fusionTable( req.getInputStream(), resp.getWriter() );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*public static void food( Object conn, String sql, PrintWriter p ) {
		Query query = (Query)conn;
		try {			
			SqlGet sqlget = query.sqlGet( sql );
			Sqlresponse sqlresp = sqlget.execute();
			
			String				oldname = "";
			boolean				hascomponents = true;
			boolean				hasreferences = true;
			
			Map<String,Float>	energyMap = new HashMap<String,Float>();
			Set<String>			energySet = new HashSet<String>();
			
			energySet.add( "PROT" );
			energySet.add( "FAT" );
			energySet.add( "ALC" );
			energySet.add( "CHOT" );
			energySet.add( "FIBT" );
			
			List<String> cols = sqlresp.getColumns();
			Map<String,Integer> colmap = new HashMap<String,Integer>();
			int i = 0;
			for( String col : cols ) {
				colmap.put( col, i++ );
			}
			
			List<List<Object>> rows = sqlresp.getRows();
			for( List<Object> row : rows ) {
				String name = (String)row.get( colmap.get("OriginalFoodCode") );
				String langual = (String)row.get( colmap.get("LangualCodes") );
				boolean sameold = oldname.equals(name);
				
				if( !sameold ) {
					energyMap.clear();
					if( !oldname.equals("") ) {
						p.println("</Components>");
						p.println("</Food>");
					}
					p.println("<Food>");
	                p.println("<FoodDescription>");
	                p.println("<FoodIdentifiers>");
	                p.println("<FoodIdentifier system=\"origfdcd\">");
	                p.println("<Identifier>"+name+"</Identifier>");
	                p.println("</FoodIdentifier>");
	                if( langual != null && langual.length() > 0 ) {
		                p.println("<FoodIdentifier system=\"LanguaL\">");
		                String[] split = langual.split("[ ]+");
		                for( String lang : split ) {
		                	p.println("<Identifier>"+lang+"</Identifier>");
		                }
		                p.println("</FoodIdentifier>");
	                }
	                p.println("</FoodIdentifiers>");
	                
	                try {
	                	String ff1 = rs.getString("FoodGroupIS1");
	                	String ff2 = rs.getString("FoodGroupIS2");
	                	p.println("<FoodClasses>");
	                	p.println("<FoodClass system=\"origgpcd\">"+ff1+"."+ff2+"</FoodClass>");
	                	p.println("</FoodClasses>");
	                } catch( SQLException sqlex ) {
	                	
	                }
	
	                p.println("<FoodNames>");
	                try {
	                	String is = rs.getString("OriginalFoodName");
	                	p.println("<FoodName language=\"is\">"+is+"</FoodName>");
	                } catch( SQLException sqlex ) {
	                	
	                }
	                
	                try {
	                	String en = rs.getString("EnglishFoodName");
	                	p.println("<FoodName language=\"en\">"+en+"</FoodName>");
	                } catch( SQLException sqlex ) {
	                	
	                }
	                p.println("</FoodNames>");
	                
	                p.println("<Remarks></Remarks>");
	                p.println("</FoodDescription>");
	                
	                if( hascomponents ) p.println("<Components>");
				}
                
				if( hascomponents ) {
	                //p.println("<Components>");
	                String eurocd = rs.getString("EuroFIRComponentIdentifier");
					String origcd = rs.getString("OriginalComponentCode");
					String origcpnm = rs.getString("OriginalComponentName");
					String engcpnm = rs.getString("EnglishComponentName");
					String unit = rs.getString("Unit");
					String matrixUnit = rs.getString("MatrixUnit");
					String acquisitionType = rs.getString("AcquisitionType");
					String dateGenerated = rs.getString("DateOfGeneration");
					String methodType = rs.getString("MethodType");
					String methodIndicator = rs.getString("MethodIndicator");
					String methodParameter = rs.getString("MethodParameter");
					String valueType = rs.getString("ValueType");
					String selectedValue = rs.getString("SelectedValue");
					String numberOfAnalyses = rs.getString("N");
					String minimum = rs.getString("Minimum");
					String maximum = rs.getString("Maximum");
					String standardDeviation = rs.getString("StandardDeviation");
					String qualityIndex = rs.getString("QI_Eurofir");
					String remarks = rs.getString("Remarks");
					
					p.println("<Component>");
					
					eurocd = Ws.nullStr(eurocd) ? "" : eurocd.trim();
					origcd = Ws.nullStr(origcd) ? "" : origcd.trim();
					origcpnm = Ws.nullStr(origcpnm) ? "" : origcpnm.trim();
					engcpnm = Ws.nullStr(engcpnm) ? "" : engcpnm.trim();
					
					unit = Ws.nullStr(unit) ? "" : unit;
					matrixUnit = Ws.nullStr(matrixUnit) ? "" : matrixUnit;
					dateGenerated = Ws.nullStr(dateGenerated) ? "" : dateGenerated.substring(0, 10);
					methodType = Ws.nullStr(methodType) ? "" : methodType;
					methodIndicator = Ws.nullStr(methodIndicator) ? "" : methodIndicator;
					methodParameter = Ws.nullStr(methodParameter) ? "" : methodParameter;
					
					if( selectedValue.startsWith("<") ) selectedValue = "less than "+selectedValue.substring(1);
					
	                p.println("<ComponentIdentifiers>");
	                p.println("<ComponentIdentifier system=\"ecompid\">"+eurocd+"</ComponentIdentifier>");	                
	                p.println("<ComponentIdentifier system=\"origcpcd\">"+origcd+"</ComponentIdentifier>");	                
	                p.println("<ComponentIdentifier system=\"origcpnm\">"+origcpnm+"</ComponentIdentifier>");
	                p.println("<ComponentIdentifier system=\"engcpnam\">"+engcpnm+"</ComponentIdentifier>");
	                p.println("</ComponentIdentifiers>");
	                
	                p.println("<Values>");
	                p.println("<Value unit=\""+unit+"\" matrixunit=\""+matrixUnit+"\" dategenerated=\""+dateGenerated+"\" methodtype=\""+methodType+"\" methodindicator=\""+methodIndicator+"\" methodparameter=\""+methodParameter+"\">");
	                valueType = Ws.nullStr(valueType) ? "" : valueType;
	                String selVal = selectedValue.trim();
	                if( selVal.endsWith(",") ) selVal = selVal.substring(0, selVal.length()-1);
	                selVal = selVal.replace(',', '.');
	                p.println("<SelectedValue valuetype=\""+valueType+"\" acquisitionType=\""+(acquisitionType==null?"":acquisitionType)+"\">"+selVal+"</SelectedValue>");
	                
	                float fVal = 0;
	                try {
	                	fVal = Float.parseFloat(selVal);
	                } catch( Exception e ) {
	                	
	                }
	                if( energySet.contains(eurocd) ) energyMap.put(eurocd, fVal);
	                
	                minimum = Ws.nullStr(minimum) ? "<Minimum/>" : "<Minimum>"+minimum.replace(',', '.')+"</Minimum>";
	                p.println(minimum);
	                maximum = Ws.nullStr(maximum) ? "<Maximum/>" : "<Maximum>"+maximum.replace(',', '.')+"</Maximum>";
	                p.println(maximum);
	                standardDeviation = Ws.nullStr(standardDeviation) ? "<StandardDeviation/>" : "<StandardDeviation>"+standardDeviation+"</StandardDeviation>";
	                p.println(standardDeviation);
	                numberOfAnalyses = Ws.nullStr(numberOfAnalyses) ? "<NumberOfAnalyticalPortions/>" : "<NumberOfAnalyticalPortions>"+numberOfAnalyses+"</NumberOfAnalyticalPortions>";
	                p.println(numberOfAnalyses);
	                qualityIndex = Ws.nullStr(qualityIndex) ? "<QualityIndex/>" : "<QualityIndex>"+qualityIndex+"</QualityIndex>";
	                p.println( qualityIndex );
	                remarks = Ws.nullStr(remarks) ? "<Remarks/>" : "<Remarks>"+remarks+"</Remarks>";
	                p.println(remarks);
	                //p.println("<QualityIndex>"+qualityIndex+"</QualityIndex>");
	                //p.println("<Remarks>"+remarks+"</Remarks>");
	
	                if( hasreferences ) {
	                	String referenceType = rs.getString("ReferenceType");
	                	String rAcquisitionType = rs.getString("rAcquisitionType");
	                	String link = rs.getString("WWW");
	                	String citation = rs.getString("Citation");
	                	
		                p.println("<References>");
		                p.println("<ValueReference referencetype=\""+referenceType+"\" acquisitiontype=\""+rAcquisitionType+"\""+((link==null||link.length()==0)?">":" link=\""+link+"\">")+citation.replace('&', 'o')+"</ValueReference>");
		                p.println("<MethodReference referencetype=\""+referenceType+"\" acquisitiontype=\""+rAcquisitionType+"\""+((link==null||link.length()==0)?" />":" link=\""+link+"\" />"));
		                p.println("</References>");
	                }
	                
	                p.println("</Value>");
	                p.println("</Values>");
	                p.println("</Component>");
	                
	                if( energyMap.size() == 5 ) {
	                	float kcalVal = energyMap.get("PROT")*4 + energyMap.get("FAT")*9 + energyMap.get("CHOT")*4 + energyMap.get("FIBT")*2 + energyMap.get("ALC")*7;
	                	float kjVal = energyMap.get("PROT")*17 + energyMap.get("FAT")*37 + energyMap.get("CHOT")*17 + energyMap.get("FIBT")*8 + energyMap.get("ALC")*29;
	                	Ws.energyCalc( p, Float.toString(kcalVal), "kcal" );
	                	Ws.energyCalc( p, Float.toString(kjVal), "kJ" );
	                	energyMap.clear();
	                }
				}
				oldname = name;
			}
			if( !oldname.equals("") ) {
				p.println("</Components>");
				p.println("</Food>");
			}
			
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
	
	public static void fusionTable( InputStream is, PrintWriter pw ) {
		HttpTransport httpTransport = new NetHttpTransport();
	    JsonFactory jsonFactory = new JacksonFactory();
	    
	    GoogleCredential credential = new GoogleCredential.Builder()
		  .setTransport(httpTransport)
		  .setJsonFactory(jsonFactory)
		  //.setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
		  //.setServiceAccountScopes(FusiontablesScopes.FUSIONTABLES_READONLY);
		  //.setServiceAccountPrivateKeyFromP12File( file )
		  //.setServiceAccountPrivateKey( pk )
		  .build();
	    
	    FusiontablesRequestInitializer fri = new FusiontablesRequestInitializer(apikey);
		Fusiontables ft = new Fusiontables.Builder( httpTransport, jsonFactory, credential ).setFusiontablesRequestInitializer(fri).build();
		final Query query = ft.query();
		
		PseudoResult rs = new PseudoResult() {
			int i = -1;
			List<List<Object>>	rows;
			Map<String,Integer>	colind;
			
			@Override
			public boolean next() {
				i++;
				
				if( rows != null && i < rows.size() ) return true;
				return false;
			}

			@Override
			public String getString(String col) {
				if( rows != null && colind.containsKey(col) ) {
					int ci = colind.get( col );
					if( ci < rows.size() ) {
						List<Object> lobj = rows.get( ci );
						if( lobj != null && i < lobj.size() ) return (String)lobj.get(i); 
					}
				}
				return null;
			}

			@Override
			public void init(String fdql) {
				colind = new HashMap<String,Integer>();
				try {
					//String sql = "select OriginalFoodCode from "+foodId+" where OriginalFoodName like '%appel%'";
					
					/*String sql = "select * from "+referenceId; //+" where OriginalComponentCode like '%appel%'";
					SqlGet sqlget = query.sqlGet( sql );
					Sqlresponse sqlresp = sqlget.execute();
					rows = sqlresp.getRows();*/
					
					List<String> ls = FDQL.fdqlToSqls( new ByteArrayInputStream( fdql.getBytes() ), tableColumnMap );
					
					for( String sql : ls ) {
						SqlGet sqlget = query.sqlGet( sql );
						Sqlresponse sqlresp = sqlget.execute();
						List<List<Object>> rowsObjs = sqlresp.getRows();
					}
					
					if( rows != null ) for( List<Object> lobj : rows ) {
						for( Object obj : lobj ) {
							System.err.print( obj.toString() );
						}
						System.err.println();
					}
					
					/*int k = 0;
					for( String str : sqlresp.getColumns() ) {
						System.err.println( str );
						
						colind.put(str, k);
						k++;
					}*/
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void close() {}
		};
		
		try {
			com.matis.eurofir.webservices.EuroFIRWebService.parseStream( rs, is, pw );
		} catch (IOException | SecurityException | IllegalArgumentException | NoSuchAlgorithmException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream( "/home/sigmar/matis/eurofir/src/testrequest3.xml" );
			PrintWriter pw = new PrintWriter( System.out );
			fusionTable( fis, pw );
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
