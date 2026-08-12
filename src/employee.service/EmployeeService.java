import static java.util.Collections.list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import employee.EmployeePojo;
import employee.dao.EmployeeDao;


@Service
public class EmployeeService {

    @Autowired
    private EmployeeDao dao;


    public void add(EmployeePojo p){
            normalize(p);

            dao.insert(p);
    }
    public void delete(int id){

        dao.delete(id);

    }
    public void update(int id, EmployeePojo newPojo){
        dao.update(id, newPojo);

    }
    public EmployeePojo get(int id){
        EmployeePojo p = getCheck(id);
        return p;

    }
    public List<EmployeePojo> getAll(){
        return dao.selectAll();

    }
    public void update(int id, EmployeePojo newPojo) throws ApiExecption{
        normalize(newPojo);
        getCheck(id);
        dao.update(id, newPojo);
    }
    public EmployeePojo getCheck(int id){
        EmployeePojo p = get(id);
        if(p==null){

            throw new ApiException("Employee with given ID does not exixt, id: "+id);
        }
    }
    private static void normalize(EmployeePojo p){
        p.setName(p.getName().toLowerCase().trail());
    }

}
