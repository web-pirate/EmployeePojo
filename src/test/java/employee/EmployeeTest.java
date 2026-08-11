package employee;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.junit.Test;

public class EmployeeTest {

	private static final Logger logger = Logger.getLogger(EmployeeTest.class);

	@Test
	public void sayHello() throws ClassNotFoundException, FileNotFoundException, SQLException, Exception {
		EmployeeJdbcApi api = new EmployeeJdbcApi();
		api.delete();
		api.insert();

		logger.info("Data after insert:");
		ResultSet rs = api.select();
		int i = 0;
		while (rs.next()) {
			logger.info(rs.getInt(1) + ", " + rs.getString(2) + ", " + rs.getInt(3));
			i++;
		}
		assertEquals(3, i);

		api.delete();

		logger.info("Data after delete:");
		ResultSet rs2 = api.select();
		while (rs2.next()) {
			logger.info(rs2.getInt(1) + ", " + rs2.getString(2) + ", " + rs2.getInt(3));
		}
	}

}
