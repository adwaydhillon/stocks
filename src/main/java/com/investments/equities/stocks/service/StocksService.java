package com.investments.equities.stocks.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StocksService {
    private CloseableHttpClient client;
    private final String apiKey = "9Z6YQ4XLOM0MIH1C";

    private static Logger log = LogManager.getLogger(StocksService.class);

    public StocksService() {
        client = HttpClients.createDefault();
    }

    public CloseableHttpResponse getInfo(String ticker) throws IOException {
        String getInfoRequestUrl = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + ticker + "&apikey=" + apiKey;
        HttpGet request = new HttpGet(getInfoRequestUrl);
        CloseableHttpResponse response = client.execute(request);
        return response;
    }
}
