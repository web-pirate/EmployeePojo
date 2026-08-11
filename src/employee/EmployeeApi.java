package employee;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.io.InputStream;

public class EmployeeApi {
	private static Object List;
	private Connection con;
	public static void main(String[] args) throws ClassNotFoundException, SQLException, Exception ,FileNotFoundException {
		
	}
	
	public EmployeeApi () throws ClassNotFoundException, IOException, SQLException {
		Properties props = new Properties();
		InputStream inStream = new FileInputStream("employee.properties");
		props.load(inStream);
		Class.forName(props.getProperty("jdbc.driver"));
		con = DriverManager.getConnection(
				props.getProperty("jdbc.url"), props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
		Statement stmt = con.createStatement();
	}
	
	public void insert() throws SQLException {
		System.out.println("Inserting Employees");
		Statement stmt = con.createStatement();
		for(int i = 0; i <3; i++) {
			int id = i;
			int age = 30 + i;
			String name = "name " + i;
			String query = "insert into emp values(" + id +", '" + name + "', " + age + ")";
			System.out.println(query);
			stmt.executeUpdate(query);
		}
		stmt.close();
	}
	public ResultSet select() throws SQLException {
		System.out.println("Selecting Employees");
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from emp");
		return rs;
//		while(rs.next()) {
//			System.out.println(rs.getInt(1) + ", "+ rs.getString(2) +", "+ rs.getInt(3));
//		}
//		stmt.close();
	}
	
	public void delete() throws SQLException {
		System.out.println("deleting Employees");
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from emp");
		java.util.List<Integer> idList = new ArrayList<Integer>() ;
		while(rs.next()) {
			idList.add(rs.getInt(1));
		}
		for(int i=0;i<idList.size(); i++) {
			stmt.executeUpdate("delete from emp where id=" + idList.get(i));
		}
		stmt.close();
	}
}
