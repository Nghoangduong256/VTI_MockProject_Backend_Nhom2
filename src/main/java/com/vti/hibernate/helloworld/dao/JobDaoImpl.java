package com.vti.hibernate.helloworld.dao;

import com.vti.hibernate.helloworld.entities.Jobs;
import com.vti.hibernate.helloworld.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class JobDaoImpl implements JobDao {
    @Override
    public boolean save(Jobs job) {

        // Khởi tạo session
        Session session = null;
        Transaction transaction = null;

        // dùng try catch để close session khi dùng xong hoặc cố lỗi thì cũng cần close

        try {
            session = HibernateUtils.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // tạo mới và lưu
            session.persist(job);

            // commit vào data base
            transaction.commit();
        }
        finally {
            if(session != null) {
                session.close();;
            }
        }

        return true;
    }
}
