package employee;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class EmployeeDao {

    @PersistenceContext
    EntityManager em;

    public void insert(EmployeePojo p) {
        em.persist(p);
    }

    public void delete(int id) {
        em.remove(select(id));
    }

    public EmployeePojo select(int id) {
        EmployeePojo p = em.find(EmployeePojo.class, id);
        if (p == null) {
            throw new EntityNotFoundException("Employee not found: " + id);
        }
        return p;
    }

    public List<EmployeePojo> selectAll() {
        return getQuery("from EmployeePojo").getResultList();
    }

    public void update(EmployeePojo p) {
        em.merge(p);
    }

    TypedQuery<EmployeePojo> getQuery(String jpql){
        return em.createQuery(jpql, EmployeePojo.class);
    }

}
