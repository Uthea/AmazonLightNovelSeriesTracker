package com.vindra.seriestracker.service;

import com.amazon.kindle.KindleSeriesFactory;
import com.vindra.seriestracker.dao.KindleSeriesDao;
import com.vindra.seriestracker.entity.KindleSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class KindleSeriesService {

    private KindleSeriesDao kindleSeriesDao;
    private KindleSeriesFactory kindleSeriesFactory;

    @Autowired
    public KindleSeriesService(KindleSeriesDao kindleSeriesDao, KindleSeriesFactory kindleSeriesFactory) {
        this.kindleSeriesDao = kindleSeriesDao;
        this.kindleSeriesFactory = kindleSeriesFactory;
    }


    @Transactional
    public KindleSeries getKindleSeries (String seriesAsin) {
        //TODO add code to handle when asin series not found
        return kindleSeriesDao.findBySeriesAsin(seriesAsin);
    }

    @Transactional
    public void saveKindleSeries (String seriesAsin) {
        // TODO add exception handling for rest

        // TODO check if entered series asin is valid or not

        // generate kindle series using factory
        KindleSeries kindleSeries = kindleSeriesFactory.createKindleSeries(seriesAsin);

        kindleSeriesDao.save(kindleSeries);
    }

    @Transactional
    public void updateKindleSeries(KindleSeries inputKindleSeries) {
        // find the series from database
        KindleSeries kindleSeries = kindleSeriesDao.findBySeriesAsin(inputKindleSeries.getSeriesAsin());

        // TODO throw exception if kindleSeries is null

        // update database record
        kindleSeriesDao.update(inputKindleSeries);
    }

    @Transactional
    public void deleteKindleSeries(String seriesAsin) {
        // find the series from database
        KindleSeries kindleSeries = kindleSeriesDao.findBySeriesAsin(seriesAsin);

        // TODO throw exception if kindleSeries is null

        // delete series record
        kindleSeriesDao.delete(kindleSeries);
    }

    @Transactional
    public void checkForNewUpdate(String seriesAsin) {
        // find the series from database
        KindleSeries kindleSeries = kindleSeriesDao.findBySeriesAsin(seriesAsin);

        // check for update
        kindleSeriesFactory.checkForUpdate(kindleSeries);

        // commit update to database
        kindleSeriesDao.update(kindleSeries);

    }

    @Transactional
    public List<KindleSeries> getAllSeries() {
        return kindleSeriesDao.findAll();
    }



}
