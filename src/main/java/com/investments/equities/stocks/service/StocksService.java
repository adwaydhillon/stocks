package com.investments.equities.stocks.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.investments.equities.stocks.client.ApiClient;
import com.investments.equities.stocks.worker.DCFWorker;
import com.investments.equities.stocks.worker.DDMWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.investments.equities.stocks.common.CommonConstants.NAME;
import static com.investments.equities.stocks.common.CommonConstants.CURRENCY;
import static com.investments.equities.stocks.common.CommonConstants.ASSET_TYPE;



@Service
public class StocksService {

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private MessagingService messagingService;

    private DCFWorker dcfWorker;

    private DDMWorker ddmWorker;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LogManager.getLogger(StocksService.class);

    public StocksService() {

    }

    public JsonNode getQuote(String ticker) throws IOException {
        JsonNode stockInfo = apiClient.getQuote(ticker);

        //messagingService.sendMessage(stockInfo.toPrettyString());

        return stockInfo;
    }

    public String getIntrinsicSharePrice(String ticker) throws IOException {
        runDCF(ticker);
        HashMap<String, String> assetInfo = apiClient.getAssetInfo(ticker);

        return "Adway says that the intrinsic share price of " +
                assetInfo.get(ASSET_TYPE) + " of " +
                assetInfo.get(NAME) + " is: " +
                assetInfo.get(CURRENCY) + " " +
                dcfWorker.getIntrinsicSharePrice();
    }

    public void runDCF(String ticker) throws IOException {
        double freeCashFlow = apiClient.getFreeCashFlow(ticker);
        double enterpriseValue = apiClient.getEnterpriseValue(ticker);
        float futureGrowthRate = apiClient.getMedianPastEarningsGrowth(ticker);
        float discount = 10;
        float terminalValueMultiple = 15;
        long sharesOutstandings = apiClient.getSharesOutstanding(ticker);

        dcfWorker = new DCFWorker(freeCashFlow, enterpriseValue, futureGrowthRate, discount, terminalValueMultiple, sharesOutstandings);

        //messagingService.sendMessage(stockInfo.toPrettyString());
    }

}
