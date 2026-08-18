import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import employee.EmployeePojo;
import employee.EmployeeDao;


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
    public EmployeePojo get(int id) throws ApiException{
        EmployeePojo p = getCheck(id);
        return p;

    }
    public List<EmployeePojo> getAll(){
        return dao.selectAll();

    }
    @Transactional(rollbackFor = ApiException.class)
    public void update(int id, EmployeePojo newPojo) throws ApiException{
        normalize(newPojo);
        EmployeePojo ex = getCheck(id);
        ex.setAge(newPojo.getAge());
        ex.setName(newPojo.getName());
        newPojo.setId(id);
        dao.update(newPojo);
    }
    @Transactional(rollbackFor = RuntimeException.class)
    public EmployeePojo getCheck(int id) throws ApiException{
        EmployeePojo p = dao.select(id);
        if(p==null){

            throw new ApiException("Employee with given ID does not exist, id: "+id);
        }
        return p;
    }
    private static void normalize(EmployeePojo p){
        p.setName(p.getName().toLowerCase().trim());
    }

}
