package employee;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws Exception {
        HibernateUtil.configure();
        EmployeeHibernateApi api = new EmployeeHibernateApi();

        //deleting all employees from the database
        api.deleteAll();

        // Createing n EmployeePojo objects and inserting them into the database
        for (int i = 1; i <= 10; i++) {
            EmployeePojo p = new EmployeePojo();
            p.setId(i);
            p.setName("username " + i);
            p.setAge(20 + i);
            api.insert(p);
        }
        //Get and print all employees from the database
       api.printAll();
	}


}
