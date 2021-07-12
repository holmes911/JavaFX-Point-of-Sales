package com.rafsan.inventory.model;

import com.rafsan.inventory.HibernateUtil;
import com.rafsan.inventory.dao.LicenceDao;
import com.rafsan.inventory.entity.Licence;
import org.hibernate.Session;

public class LicenceModel implements LicenceDao {

    private static Session session;

    @Override
    public Licence getLicence(long id) {
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Licence licence = session.get(Licence.class, id);
        session.getTransaction().commit();

        return licence;

    }
}
