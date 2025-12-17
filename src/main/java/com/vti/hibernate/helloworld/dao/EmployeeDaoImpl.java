package com.vti.hibernate.helloworld.dao;

import com.vti.hibernate.helloworld.entities.Employees;
import com.vti.hibernate.helloworld.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class EmployeeDaoImpl implements EmployeeDao {
    @Override
    public boolean save(Employees employee) {
        // tuong tưj save method ở JobDaoImpl

        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtils.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.persist(employee);

            transaction.commit();

        }
        finally {
            if(session != null) {
                session.close();
            }
        }

        return false;
    }
}
