package com.vindra.seriestracker.dao;

import com.vindra.seriestracker.entity.Kindle;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class KindleDao {

    @PersistenceContext
    private EntityManager em;

    public Kindle findByAsin(String asin) {

        return em.find(Kindle.class, asin);
    }

    public void save (Kindle kindle) {
        // TODO add exception handling when asin already exist

        em.persist(kindle);
    }

    // delete kindle record
    public void delete(Kindle kindle) {

        em.remove(kindle);
    }

    // update kindle record
    public void update(Kindle kindle) {

        em.merge(kindle);
    }


}
