package com.vindra.seriestracker.dao;

import com.vindra.seriestracker.entity.KindleSeries;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class KindleSeriesDao {

    @PersistenceContext
    private EntityManager em;

    // find series by asin
    public KindleSeries findBySeriesAsin(String seriesAsin) {

        return em.find(KindleSeries.class, seriesAsin);
    }

    // add series
    public void save(KindleSeries kindleSeries) {

        em.persist(kindleSeries);
    }

    // delete series
    public void delete(KindleSeries kindleSeries) {

        em.remove(kindleSeries);
    }

    // update series
    public void update(KindleSeries kindleSeries) {

        em.merge(kindleSeries);
    }

    public List<KindleSeries> findAll() {

        List<KindleSeries> kindleSeriesList = new ArrayList<>();

        kindleSeriesList = em.createNativeQuery(
                "SELECT * FROM KINDLE_SERIES ORDER BY RAND()",
                KindleSeries.class).getResultList();

        return kindleSeriesList;

    }



}
