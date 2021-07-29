package com.vindra.seriestracker.rest;


import com.vindra.seriestracker.service.KindleSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/test/"))
public class TestRestController {

    @Autowired
    KindleSeriesService kindleSeriesService;


    @GetMapping("check/series/{seriesAsin}")
    public String checkForUpdateSeries(@PathVariable String seriesAsin) {

        kindleSeriesService.checkForNewUpdate(seriesAsin);
        return "update test";
    }

}
