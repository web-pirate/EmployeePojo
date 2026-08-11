package employee;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

public class EmployeeHibernateApi {
	private static final Logger logger = Logger.getLogger(EmployeeHibernateApi.class);

	private HibernateUtil hbu;

	public EmployeeHibernateApi() throws ClassNotFoundException, IOException, SQLException {
		hbu = new HibernateUtil();
	}

	public void insert(EmployeePojo p) throws SQLException {
		logger.info("Inserting Employees");

		hbu.createSession();
		hbu.beginTransaction();
		Session s = hbu.getSession();
		s.save(p);

		hbu.commitTransaction();
		hbu.closeSession();

	}

	public EmployeePojo select(int id) throws SQLException {
		logger.info("Selecting an Employees");

		hbu.createSession();
		hbu.beginTransaction();
		Session s = hbu.getSession();
		EmployeePojo p = s.get(EmployeePojo.class, id);
		hbu.commitTransaction();
		hbu.closeSession();

		return p;
	}

	public List<EmployeePojo> selectAll() throws SQLException {
		logger.info("Selecting all Employees");

		hbu.createSession();
		hbu.beginTransaction();
		Session s = hbu.getSession();
		Query q = s.createQuery("select o from EmployeePojo o");
		List<EmployeePojo> employees = q.getResultList();
		hbu.commitTransaction();
		hbu.closeSession();

		return employees;
	}

	public void update(int id, EmployeePojo p) throws SQLException {
		logger.info("Updating an Employees");

		hbu.createSession();
		hbu.beginTransaction();
		Session s = hbu.getSession();
		EmployeePojo existingEmployee = s.find(EmployeePojo.class, id);
		if (existingEmployee != null) {
			existingEmployee.setName(p.getName());
			existingEmployee.setAge(p.getAge());
			s.update(existingEmployee);
		}
		hbu.commitTransaction();
		hbu.closeSession();

	}

	public void delete(int id) throws SQLException {

		hbu.createSession();
		hbu.beginTransaction();
		Session s = hbu.getSession();
		EmployeePojo p = s.find(EmployeePojo.class, id);
		s.delete(p);
		hbu.commitTransaction();
		hbu.closeSession();

	}

	public void deleteAll() throws SQLException {

		hbu.createSession();
		hbu.beginTransaction();
		Session s = hbu.getSession();
		Query q = s.createQuery("select o from EmployeePojo o");
		List<EmployeePojo> list = q.getResultList();
		for (EmployeePojo p : list) {
			s.delete(p);
		}

		hbu.commitTransaction();
		hbu.closeSession();

	}

	public void printAll() throws SQLException {
		List<EmployeePojo> list = selectAll();
		for (EmployeePojo p : list) {
			logger.warn("Employee ID: " + p.getId() + ", Name: " + p.getName() + ", Age: " + p.getAge());
		}
	}
}
