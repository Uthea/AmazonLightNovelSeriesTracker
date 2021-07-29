package com.vindra.seriestracker.rest;


import com.vindra.seriestracker.entity.Kindle;
import com.vindra.seriestracker.rest.requestbody.InputKindle;
import com.vindra.seriestracker.service.KindleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class KindleRestController {

    private KindleService kindleService;

    @Autowired
    public KindleRestController(KindleService kindleService) {
        this.kindleService = kindleService;
    }

    @GetMapping("kindle/{asin}")
    Kindle getKindle(@PathVariable String asin) {
        return kindleService.getKindle(asin);
    }

    @PutMapping("kindle")
    String updateKindle(@RequestBody Kindle inputKindle) {
        kindleService.updateKindle(inputKindle);

        return "Kindle with ASIN : " + inputKindle.getAsin() + " successfully updated";
    }

    @DeleteMapping("kindle/{asin}")
    String deleteKindle(@PathVariable String asin) {
        kindleService.deleteKindle(asin);

        return "Kindle with ASIN : " + asin + "successfully deleted";
    }
}
