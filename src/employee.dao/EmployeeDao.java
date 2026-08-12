import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import employee.EmployeePojo;

@Repository

public class EmployeeDao {

    // private HashMap<Integer, EmployeePojo> rows;
    private HashMap<Integer, EmployeePojo> rows;
    private int lastId;

    @PostConstruct
    public void init(){
        rows = new HashMap<>(<Integer>, HashMap())
    }

    public void insert(EmployeePojo p) {
        lastId++;
        p.setId(lastId);
        rows.put(p.getId(), p);


    }
    public void delete(int id){
        rows.remove(id);

    }
    public EmployeePojo select(int id){
        return rows.get(id);
    }
    public List<EmployeePojo> selectAll(){
        ArrayList<EmployeePojo> list = new ArrayList<>(rows.values());
        list.addAll(rows.values());
        return list;
    }
    public void update(int id, EmployeePojo p){
        rows.put(id, p);
    }
}
