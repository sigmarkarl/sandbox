package org.simmi;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataExtract {
	Connection	con;
	
	public DataExtract() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			connect();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void connect() throws SQLException {
		String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=ISGEM2;user=simmi;password=mirodc30;";
		//String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=order;integratedSecurity=true;";
		con = DriverManager.getConnection(connectionUrl);
	}
	
	public void extractDataFiles() throws IOException, SQLException {
		FileWriter	fw = new FileWriter("/home/sigmar/sandbox/sorttable/src/thsGroups.txt");
		String sql = "select * from thsGroups";
		PreparedStatement 	ps = con.prepareStatement(sql);
		ResultSet 			rs = ps.executeQuery();

		while (rs.next()) {
			fw.write( rs.getObject(1)+"\t" );
			fw.write( rs.getObject(2)+"\n" );
		}
		
		fw.close();
		rs.close();
		ps.close();
		
		fw = new FileWriter("/home/sigmar/sandbox/sorttable/src/Component.txt");
		sql = "select * from Component";
		ps = con.prepareStatement(sql);
		rs = ps.executeQuery();

		while (rs.next()) {
			//fw.write( 0+"\t" );
			fw.write( rs.getObject(2)+"\t" );
			fw.write( rs.getObject(3)+"\t" );
			String str = rs.getObject(4)+"";
			str = str.trim()+"\t";
			fw.write( str );
			str = rs.getObject(5)+"";
			str = str.trim()+"\t";
			fw.write( str );
			str = rs.getObject(6)+"";
			str = str.trim()+"\t";
			fw.write( str );
			str = rs.getObject(7)+"";
			str = str.trim()+"\t";
			fw.write( str );
			str = rs.getObject(8)+"";
			str = str.trim()+"\t";
			fw.write( str );
			str = rs.getObject(9)+"";
			str = str.trim()+"\n";
			fw.write( str );
		}
		
		fw.close();
		rs.close();
		ps.close();
		
		fw = new FileWriter("/home/sigmar/sandbox/sorttable/src/Food.txt");
		sql = "select 0,OriginalFoodCode,OriginalFoodName,EnglishFoodName,ScientificFoodName,OtherFoodNames,OriginalFoodGroupCode,FoodGroupIS1,FoodGroupIS2,FoodGroupIS3,CodexFoodStandards,ArticleNumber,E_number,INS_code,0,CODEXAdditives,CODEXFood,CODEXContaminants,FAOBalanceSheet,CIAAFood,EuroCode2,AgriculturalConditions,Cuisine,EdiblePortion,WastePortion,NatureofEdiblePortion,NatureofWaste,TypicalServingSize,TypicalWeightperPiece,Colour,FinalPreparation,SpecificGravity,NitrogenProteinFactor,FattyAcidFactor,GenericImage,SpecificImage,Producer,Distributor,Retailer,AreaOfOrigin,AreaOfProcessing,AreaofConsumption,WebPublishReady,ListOfIngredients,0,0,GeneratedBy,0,UpdatedBy from food where WebPublishReady = 'J'";
		
		ps = con.prepareStatement(sql);
		rs = ps.executeQuery();

		while (rs.next()) {
			//fw.write( 0+"\t" );
			fw.write( rs.getObject(2)+"\t" );
			fw.write( rs.getObject(3)+"\t" );
			String str = rs.getObject(4)+"";
			str = str.trim()+"\t";
			fw.write( str );
			str = rs.getObject(5)+"";
			str = str.trim()+"\t";
			fw.write( str );
			str = rs.getObject(6)+"";
			str = str.trim()+"\t";
			fw.write( str );
			str = rs.getObject(7)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(8)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(9)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(10)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(11)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(12)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(13)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(14)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(15)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(16)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(17)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(18)+"";
			str = str.trim()+"\t";
			fw.write( str );
			
			str = rs.getObject(19)+"";
			str = str.trim()+"\n";
			fw.write( str );
		}
		
		fw.close();
		rs.close();
		ps.close();
		
		fw = new FileWriter("/home/sigmar/sandbox/sorttable/src/result.txt");
		//sql = "select cv.OriginalFoodCode,OriginalComponentCode,SelectedValue,Unit,Matrixunit,ValueType,AcquisitionType,0,DateofEvaluation,DateofEvaluationDisp,N,AnalyticalPortionSize,NoofAnalyticalPortionReplicates,Mean,Median,[Minimum],Maximum,StandardDeviation,StandardError,0,0,0,0,NoofPrimarySampleUnits,0,OriginalReferenceCode,QI_Eurofir,QualityAssessmentFK,SamplingStrategy,0,0,0,0,cv.GeneratedBy,0,cv.UpdatedBy from ComponentValue cv, Food f where f.WebPublishReady = 'J' and f.OriginalFoodCode = cv.OriginalFoodCode and cv.OriginalComponentCode in (1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 20, 21, 23, 24, 28, 29, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 44, 137, 138)";
		sql = "select cv.OriginalFoodCode,OriginalComponentCode,SelectedValue from ComponentValue cv, Food f where f.WebPublishReady = 'J' and f.OriginalFoodCode = cv.OriginalFoodCode and cv.OriginalComponentCode in (1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 20, 21, 23, 24, 28, 29, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 44, 137, 138)";

		ps = con.prepareStatement(sql);
		rs = ps.executeQuery();

		while (rs.next()) {
			//fw.write( 0+"\t" );
			fw.write( rs.getObject(1)+"\t" );
			fw.write( rs.getObject(2)+"\t" );
			fw.write( rs.getObject(3)+"\n" );
		}
		
		fw.close();
		rs.close();
		ps.close();
	}
	
	public static void main(String[] args) {
		try {
			DataExtract de = new DataExtract();
			de.extractDataFiles();
			de.con.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
