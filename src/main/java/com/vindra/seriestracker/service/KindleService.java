package com.vindra.seriestracker.service;

import com.vindra.seriestracker.dao.KindleDao;
import com.vindra.seriestracker.entity.Kindle;
import com.vindra.seriestracker.entity.KindleSeries;
import com.vindra.seriestracker.rest.requestbody.InputKindle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class KindleService {


    private KindleDao kindleDao;

    @Autowired
    public KindleService(KindleDao kindleDao) {
        this.kindleDao = kindleDao;
    }

    @Transactional
    public Kindle getKindle(String asin) {
        //TODO add code to handle when asin not found

        return kindleDao.findByAsin(asin);
    }

    @Transactional
    public void updateKindle(Kindle inputKindle) {
        // find the kindle from database
        Kindle kindle =  kindleDao.findByAsin(inputKindle.getAsin());


        // TODO throw exception if kindleSeries is null


        // set all properties from input
        kindle.setTitle(inputKindle.getTitle());
        kindle.setAsin(inputKindle.getAsin());
        kindle.setLanguage(inputKindle.getLanguage());
        kindle.setPrice(inputKindle.getPrice());
        kindle.setPublicationDate(inputKindle.getPublicationDate());
        kindle.setKindleUnlimited(inputKindle.isKindleUnlimited());
        kindle.setKoboId(inputKindle.getKoboId());
        kindle.setPublisher(inputKindle.getPublisher());

        // update database record
        kindleDao.update(kindle);
    }

    @Transactional
    public void deleteKindle(String asin) {
        // find the kindle from database
        Kindle kindle =  kindleDao.findByAsin(asin);

        // TODO throw exception if kindleSeries is null

        // delete series record
        kindleDao.delete(kindle);
    }

}
