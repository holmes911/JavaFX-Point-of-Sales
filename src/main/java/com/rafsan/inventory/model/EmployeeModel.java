package com.rafsan.inventory.model;

import com.rafsan.inventory.HibernateUtil;
import com.rafsan.inventory.dao.EmployeeDao;
import com.rafsan.inventory.entity.Employee;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Query;
import org.hibernate.Session;

public class EmployeeModel implements EmployeeDao {

    private static Session session;

    @Override
    public ObservableList<Employee> getEmployees() {

        ObservableList<Employee> list = FXCollections.observableArrayList();

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<Employee> employees = session.createQuery("from Employee").list();
        session.beginTransaction().commit();
        employees.stream().forEach(list::add);

        return list;
    }

    @Override
    public Employee getEmployee(long id) {

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Employee employee = session.get(Employee.class, id);
        session.getTransaction().commit();

        return employee;
    }
    
    @Override
    public String getEmployeeType(String username){
    
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("from Employee where userName = :username");
        query.setParameter("username", username);
        Employee employee = (Employee) query.uniqueResult();
        session.getTransaction().commit();
       
        return employee.getType();
    }

    @Override
    public String getEmployeeTypeBySupervisor(String supervisorCode){

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("from Employee where supervisorCode = :supervisorCode");
        query.setParameter("supervisorCode", supervisorCode);
        Employee employee = (Employee) query.uniqueResult();
        session.getTransaction().commit();

        return employee.getType();
    }

    @Override
    public void saveEmployee(Employee employee) {

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(employee);
        session.getTransaction().commit();
    }

    @Override
    public void updateEmployee(Employee employee) {

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Employee e = session.get(Employee.class, employee.getId());
        e.setFirstName(employee.getFirstName());
        e.setLastName(employee.getLastName());
        e.setUserName(employee.getUserName());
        e.setPassword(employee.getPassword());
        e.setPhone(employee.getPhone());
        e.setAddress(employee.getAddress());
        e.setSupervisorCode(employee.getSupervisorCode());
        session.getTransaction().commit();
    }

    @Override
    public void deleteEmployee(Employee employee) {
        
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Employee e = session.get(Employee.class, employee.getId());
        session.delete(e);
        session.getTransaction().commit();
    }
    
    @Override
    public boolean checkUser(String username) {

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("from Employee where userName = :username");
        query.setParameter("username", username);
        Employee employee = (Employee) query.uniqueResult();
        session.getTransaction().commit();

        return employee != null;
    }
    
    @Override
    public boolean checkPassword(String username, String password) {

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("from Employee where userName = :username");
        query.setParameter("username", username);
        Employee employee = (Employee) query.uniqueResult();
        session.getTransaction().commit();

        return employee.getPassword().equals(password);
    }

    public boolean checkSupervisorUser(String supervisorCode) {
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("from Employee where supervisorCode = :supervisorCode");
        query.setParameter("supervisorCode", supervisorCode);
        Employee employee = (Employee) query.uniqueResult();
        session.getTransaction().commit();

        return employee != null;
    }
}
