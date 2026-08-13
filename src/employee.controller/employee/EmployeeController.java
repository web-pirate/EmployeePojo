package employee;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private static EmployeeHibernateApi api;
    static {
        HibernateUtil.configure();
        try {
            api = new EmployeeHibernateApi();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @PostMapping("")
    public void add(@RequestBody EmployeeForm form) throws SQLException {
        api.insert(convert(form));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") int id) throws SQLException {
        api.delete(id);
    }

    @GetMapping("{id}")
    public EmployeeData get(@PathVariable("id") int id) throws SQLException {
        EmployeePojo p = api.select(id);
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + id);
        }
        return convert(p);
    }

    @PutMapping("{id}")
    public void update(@PathVariable("id") int id, @RequestBody EmployeeForm form) throws SQLException {
        EmployeePojo p = convert(form);
        api.update(id,p);
    }

    @GetMapping("")
    public List<EmployeeData> getAll() throws SQLException {
        List<EmployeeData> list = new ArrayList<>();
        List<EmployeePojo> list2 = api.selectAll();
        for (EmployeePojo p : list2) {
            list.add(convert(p));
        }
        return list;
    }

    private static EmployeeData convert(EmployeePojo p) {
        EmployeeData d = new EmployeeData();
        d.setId(p.getId());
        d.setName(p.getName());
        d.setAge(p.getAge());
        return d;
    }

    private static EmployeePojo convert(EmployeeForm f) {
        EmployeePojo p = new EmployeePojo();
        p.setName(f.getName());
        p.setAge(f.getAge());
        return p;
    }
}
