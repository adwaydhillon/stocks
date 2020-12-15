package com.investments.equities.stocks.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.investments.equities.stocks.exception.FieldNotFoundException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import static com.investments.equities.stocks.common.CommonConstants.ALPHA_VANTAGE_API_KEY;
import static com.investments.equities.stocks.common.CommonConstants.ALPHA_VANTAGE_BASE_URL;
import static com.investments.equities.stocks.common.CommonConstants.ANNUAL_EARNINGS;
import static com.investments.equities.stocks.common.CommonConstants.ANNUAL_REPORTS;
import static com.investments.equities.stocks.common.CommonConstants.ASSET_TYPE;
import static com.investments.equities.stocks.common.CommonConstants.CAPITAL_EXPENDITURES;
import static com.investments.equities.stocks.common.CommonConstants.CURRENCY;
import static com.investments.equities.stocks.common.CommonConstants.EBITDA;
import static com.investments.equities.stocks.common.CommonConstants.EV_TO_EBITDA;
import static com.investments.equities.stocks.common.CommonConstants.NAME;
import static com.investments.equities.stocks.common.CommonConstants.OPERATING_CASH_FLOW;
import static com.investments.equities.stocks.common.CommonConstants.PARAM_DELIMITER_AMPERSAND;
import static com.investments.equities.stocks.common.CommonConstants.PARAM_DELIMITER_QUERY;
import static com.investments.equities.stocks.common.CommonConstants.PARAM_EQUALS;
import static com.investments.equities.stocks.common.CommonConstants.REPORTED_EPS;
import static com.investments.equities.stocks.common.CommonConstants.SHARES_OUTSTANDING;

@Component
public class ApiClient {

    private CloseableHttpClient client;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LogManager.getLogger(ApiClient.class);

    public ApiClient() {
        client = HttpClients.createDefault();
    }

    public double getFreeCashFlow(String ticker) throws IOException {
        JsonNode annualCashFlowStatement = getCashFlows(ticker).get(ANNUAL_REPORTS);

        if (annualCashFlowStatement == null) {
            throw new FieldNotFoundException("Annual cash flows not found in cash flow statement for stock ticker: " + ticker);
        }

        JsonNode operatingCashflowNode = annualCashFlowStatement.get(0).get(OPERATING_CASH_FLOW);
        JsonNode capitalExpendituresNode = annualCashFlowStatement.get(0).get(CAPITAL_EXPENDITURES);

        if (operatingCashflowNode == null) {
            throw new FieldNotFoundException("Operating cash flow not found in the cash flow statement for stock ticker: " + ticker);
        }

        if (capitalExpendituresNode == null) {
            throw new FieldNotFoundException("Capital expenditures not found in the cash flow statement for stock ticker: " + ticker);
        }

        String stringOperatingCashflow = operatingCashflowNode.asText();
        String stringCapitalExpenditures = capitalExpendituresNode.asText();

        double operatingCashflow = Double.valueOf(stringOperatingCashflow);
        double capitalExpenditures = Double.valueOf(stringCapitalExpenditures);

        return operatingCashflow - capitalExpenditures;
    }

    public float getMedianPastEarningsGrowth(String ticker) throws IOException {
        JsonNode earningsStatement = getEarnings(ticker).get(ANNUAL_EARNINGS);

        if (earningsStatement == null) {
            throw new FieldNotFoundException("Annual earnings per share not found in earnings sheet for stock ticker: " + ticker);
        }

        ArrayList<Float> pastEarningsPerShare = new ArrayList<>();
        ArrayList<Float> pastEarningsPerShareGrowth = new ArrayList<>();

        // If the company has not been profitable for more than 6 years, or is a newly public company then the sample size will be less than 6
        int earningsSampleSize = earningsStatement.size() > 6 ? 6 : earningsStatement.size();

        for (int i = 0; i < earningsSampleSize; i++) {
            String stringEPS = earningsStatement.get(i).get(REPORTED_EPS).asText();
            pastEarningsPerShare.add(Float.valueOf(stringEPS));
        }

        for (int i = 0; i < earningsSampleSize - 1; i++) {
            float oldEps = pastEarningsPerShare.get(i + 1);
            float newEps = pastEarningsPerShare.get(i);

            float growth = newEps - oldEps;
            float growthRate = (growth / oldEps) * 100;

            pastEarningsPerShareGrowth.add(growthRate);
        }

        //Get median growth
        pastEarningsPerShareGrowth.sort(Comparator.naturalOrder());
        int earningsGrowthSampleSize = pastEarningsPerShareGrowth.size();
        float median;
        if (pastEarningsPerShareGrowth.size() % 2 == 0) {
            median = (pastEarningsPerShareGrowth.get(earningsGrowthSampleSize/2) + pastEarningsPerShareGrowth.get(earningsGrowthSampleSize/2 - 1))/2;
        }
        else {
            median = pastEarningsPerShareGrowth.get(earningsGrowthSampleSize/2);
        }
        return median;
    }

    public double getEnterpriseValue(String ticker) throws IOException {
        JsonNode stockOverview = getOverview(ticker);

        String strEBITDA = stockOverview.get(EBITDA).asText();
        if (strEBITDA == null) {
            throw new FieldNotFoundException("EBITDA not found in overview for stock ticker: " + ticker);
        }

        String strEVToEBITDA = stockOverview.get(EV_TO_EBITDA).asText();
        if (strEVToEBITDA == null) {
            throw new FieldNotFoundException("EV_TO_EBITDA ratio not found in overview for stock ticker: " + ticker);
        }

        double ebitda = Double.valueOf(strEBITDA);
        double evToEbitda = Double.valueOf(strEVToEBITDA);

        return evToEbitda * ebitda;
    }

    public long getSharesOutstanding(String ticker) throws IOException {
        JsonNode stockOverview = getOverview(ticker);

        String strSharesOutstanding = stockOverview.get(SHARES_OUTSTANDING).asText();
        if (strSharesOutstanding == null) {
            throw new FieldNotFoundException("Shares Outstanding not found in overview for stock ticker: " + ticker);
        }

        long sharesOutstanding = Long.valueOf(strSharesOutstanding);
        return sharesOutstanding;
    }

    public HashMap<String, String> getAssetInfo(String ticker) throws IOException {
        JsonNode stockOverview = getOverview(ticker);

        String strName = stockOverview.get(NAME).asText();
        if (strName == null) {
            throw new FieldNotFoundException("Name not found in overview for stock ticker: " + ticker);
        }

        String strAssetType = stockOverview.get(ASSET_TYPE).asText();
        if (strAssetType == null) {
            throw new FieldNotFoundException("Asset Type not found in overview for stock ticker: " + ticker);
        }

        String strCurrency = stockOverview.get(CURRENCY).asText();
        if (strCurrency == null) {
            throw new FieldNotFoundException("Currency not found in overview for stock ticker: " + ticker);
        }

        return new HashMap<>()
        {{
            put(NAME, strName);
            put(ASSET_TYPE, strAssetType);
            put(CURRENCY, strCurrency);
        }};
    }

    public JsonNode getQuote(String ticker) throws IOException {
        HashMap<String, String> params = new HashMap<>()
        {{
            put("function", "GLOBAL_QUOTE");
            put("symbol", ticker);
            put("apikey", ALPHA_VANTAGE_API_KEY);
        }};

        getMedianPastEarningsGrowth(ticker);

        String quoteRequestUrl = constructStockApiUrl(params);
        return callStockApi(quoteRequestUrl);
    }

    private JsonNode getCashFlows(String ticker) throws IOException {
        HashMap<String, String> params = new HashMap<>()
        {{
            put("function", "CASH_FLOW");
            put("symbol", ticker);
            put("apikey", ALPHA_VANTAGE_API_KEY);
        }};

        String cashFlowRequestUrl = constructStockApiUrl(params);
        return callStockApi(cashFlowRequestUrl);
    }

    private JsonNode getEarnings(String ticker) throws IOException {
        HashMap<String, String> params = new HashMap<>()
        {{
            put("function", "EARNINGS");
            put("symbol", ticker);
            put("apikey", ALPHA_VANTAGE_API_KEY);
        }};

        String earningsRequestUrl = constructStockApiUrl(params);
        return callStockApi(earningsRequestUrl);
    }

    private JsonNode getOverview(String ticker) throws IOException {
        HashMap<String, String> params = new HashMap<>()
        {{
            put("function", "OVERVIEW");
            put("symbol", ticker);
            put("apikey", ALPHA_VANTAGE_API_KEY);
        }};

        String overviewRequestUrl = constructStockApiUrl(params);
        return callStockApi(overviewRequestUrl);
    }

    private String constructStockApiUrl(HashMap<String, String> params) {
        if (params == null || params.isEmpty()) {
            logger.error("No query parameters passed into URL constructor");
            return null;
        }

        StringBuffer urlBuffer = new StringBuffer(ALPHA_VANTAGE_BASE_URL);
        int paramCount = 0;

        for (String paramKey : params.keySet()) {
            if (paramCount == 0) {
                urlBuffer.append(PARAM_DELIMITER_QUERY);
            } else {
                urlBuffer.append(PARAM_DELIMITER_AMPERSAND);
            }
            urlBuffer.append(paramKey + PARAM_EQUALS + params.get(paramKey));
            paramCount++;
        }
        return urlBuffer.toString();
    }

    private JsonNode callStockApi(String stockApiUrl) throws IOException {
        HttpGet request = new HttpGet(stockApiUrl);
        CloseableHttpResponse response = client.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JsonNode quoteJsonNode = objectMapper.readTree(responseBody);

        return quoteJsonNode;
    }

}
