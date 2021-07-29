package com.vindra.seriestracker.task;


import com.vindra.seriestracker.entity.KindleSeries;
import com.vindra.seriestracker.service.KindleSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class KindleTask {

    @Autowired
    KindleSeriesService kindleSeriesService;

    @Scheduled(fixedDelay=2*60*60*1000, initialDelay=60*60*1000)
    public void CheckUpdateForKindle() {
        System.out.println("Check For Update.....");

        List<KindleSeries> kindleSeriesList = kindleSeriesService.getAllSeries();

        kindleSeriesList.forEach(kindleSeries -> {
            kindleSeriesService.checkForNewUpdate(kindleSeries.getSeriesAsin());
        });

        System.out.println("Check For Update Completed");


    }


}
