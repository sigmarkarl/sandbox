package org.simmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NavTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
			String sql = "select f.[Ábyrgðaraðilli] from [vdb].[dbo].[Frystilager] f"; 
			//br.readLine();
			
			//System.out.println("Content-type: text/plain\n");
			if( sql != null && sql.length() > 0 ) {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=MATIS;user=simmi2;password=drsmorc.311;";
				Connection con = DriverManager.getConnection(connectionUrl);
				
				PreparedStatement ps = con.prepareStatement( sql );
				
				int ind = sql.indexOf("from");
				int cnt = sql.substring(0,ind).split(",").length;
				
				ResultSet rs = ps.executeQuery();
				while( rs.next() ) {
					int i;
					for( i = 1; i < cnt; i++ ) {
						System.out.print( rs.getObject( i ) + "\t" );
					}
					System.out.println( rs.getObject( i ) );
				}
			} else {
				System.out.println("hello world");
			}
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} /*catch (IOException e) {
			e.printStackTrace();
		}*/
	}

}
