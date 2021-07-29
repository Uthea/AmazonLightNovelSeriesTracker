package com.vindra.seriestracker.rest;

import com.vindra.seriestracker.entity.KindleSeries;
import com.vindra.seriestracker.rest.requestbody.Input;
import com.vindra.seriestracker.service.KindleSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class KindleSeriesRestController {

    private KindleSeriesService kindleSeriesService;

    @Autowired
    public KindleSeriesRestController(KindleSeriesService kindleSeriesService) {
        this.kindleSeriesService = kindleSeriesService;
    }

    @GetMapping("/kindle/series/{seriesAsin}")
    public KindleSeries getKindleSeries(@PathVariable String seriesAsin) {
        return kindleSeriesService.getKindleSeries(seriesAsin);
    }

    @PostMapping("/kindle/series")
    @Async
    public String saveKindleSeries(@RequestBody Input body) {

        // access save service
        kindleSeriesService.saveKindleSeries(body.getAsinSeries());

        return "Kindle Series with ASIN : " + body.getAsinSeries() + " is processed";
    }

    @PutMapping("/kindle/series")
    public String updateKindleSeries(@RequestBody KindleSeries inputKindleSeries) {
        kindleSeriesService.updateKindleSeries(inputKindleSeries);

        return "Kindle series with ASIN : " + inputKindleSeries.getSeriesAsin() + " successfully updated";
    }

    @DeleteMapping("/kindle/series/{seriesAsin}")
    public String deleteKindleSeries(@PathVariable String seriesAsin) {
        kindleSeriesService.deleteKindleSeries(seriesAsin);

        return "Kindle series with ASIN : " + seriesAsin + " successfully deleted";
    }



}
