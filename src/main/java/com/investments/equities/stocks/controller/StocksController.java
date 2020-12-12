package com.investments.equities.stocks.controller;

import com.investments.equities.stocks.service.StocksService;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/stocks")
public class StocksController {

    @Autowired
    private StocksService stocksService;

    private static Logger log = LogManager.getLogger(StocksController.class);

    /**
     * GET stock info
     * @param ticker stock ticker that the security is traded as
     * @return
     */

    @GetMapping("/info/{ticker}")
    public ResponseEntity<String> getStockInfo(@PathVariable String ticker) {
        try {
            HttpResponse httpResponse = stocksService.getInfo(ticker);
            String responseBody = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
            return ResponseEntity.ok(responseBody);
        } catch (IOException e) {
            log.error("IO Error in getting stock information", e.getStackTrace());
        }
        return ResponseEntity.noContent().build();
    }
}