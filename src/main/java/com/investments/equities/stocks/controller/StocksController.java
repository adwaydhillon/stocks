package com.investments.equities.stocks.controller;

import com.investments.equities.stocks.service.StocksService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/stocks")
public class StocksController {

    @Autowired
    private StocksService stocksService;

    private static Logger logger = LogManager.getLogger(StocksController.class);

    /**
     * GET stock quote
     * @param ticker stock ticker that the security is traded as
     * @return
     */

    @GetMapping("/intrinsicSharePrice/{ticker}")
    public ResponseEntity<String> getIntrinsicSharePrice(@PathVariable String ticker) {
        try {
            String dcfResponse = stocksService.getIntrinsicSharePrice(ticker);
            return ResponseEntity.ok(dcfResponse);
        } catch (Exception e) {
            logger.error("Error in getting stock information", e.getStackTrace());
        }
        return ResponseEntity.noContent().build();
    }
}