package com.investments.equities.stocks.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {
    private static Logger log = LogManager.getLogger(RequestController.class);

    /**
     * Sample GET request
     * @param
     * @return
     */

    @RequestMapping("/stocks")
    String helloWorld() {
        log.info("nimbus - application is up!");
        return "Hello world!";
    }
}