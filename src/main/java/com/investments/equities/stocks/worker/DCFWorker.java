package com.investments.equities.stocks.worker;

import java.util.ArrayList;

public class DCFWorker {
    private double freeCashFlow;
    private double enterpriseValue;
    private double intrinsicSharePrice;

    private float futureGrowthRate;
    private float discount;
    private float terminalValueMultiple;        // represented as a multiple of free cash flow

    private long sharesOutstandings;

    private ArrayList<ArrayList<Double>> futureCashFlowsInfo;
    private ArrayList<Double> targetCashFlowsInfo;

    private final static int YEARS_INTO_THE_FUTURE = 5;

    private enum rating {
        BUY,
        SELL,
        STRONG_BUY,
        STRONG_SELL
    }

    public DCFWorker(double freeCashFlow, double enterpriseValue, float futureGrowthRate, float discount, float terminalValueMultiple, long sharesOutstandings) {
        this.freeCashFlow = freeCashFlow;
        this.enterpriseValue = enterpriseValue;
        this.futureGrowthRate = futureGrowthRate / 100;
        this.discount = discount;
        this.terminalValueMultiple = terminalValueMultiple;
        this.sharesOutstandings = sharesOutstandings;

        calculateIntrinsicSharePrice();
    }

    private void getFutureCashFlowsInfo() {
        ArrayList<Double> futureCashFlows = new ArrayList<>();
        ArrayList<Double> presentValues = new ArrayList<>();

        double currentCashFlow = freeCashFlow;
        for (int i = 0; i < YEARS_INTO_THE_FUTURE; i++) {
            double presentValue = currentCashFlow / Math.pow(1 + futureGrowthRate, 1 + i);
            futureCashFlows.add(currentCashFlow);
            presentValues.add(presentValue);
            currentCashFlow += Math.abs(currentCashFlow) * futureGrowthRate;
        }

        futureCashFlowsInfo =  new ArrayList<>() {{
            add(futureCashFlows);
            add(presentValues);
        }};
    }

    private void getTargetCashFlows() {
        getFutureCashFlowsInfo();

        double targetCashFlow = terminalValueMultiple * futureCashFlowsInfo.get(0).get(YEARS_INTO_THE_FUTURE - 1);
        double targetPresentValue = targetCashFlow / Math.pow(1 + futureGrowthRate, YEARS_INTO_THE_FUTURE);

        targetCashFlowsInfo = new ArrayList<>() {{
            add(targetCashFlow);
            add(targetPresentValue);
        }};
    }

    private double calculateIntrinsicEnterpriseValue() {
        getTargetCashFlows();
        double intrinsicEnterpriseValue = 0;

        for (double presentValue : futureCashFlowsInfo.get(1)) {
            intrinsicEnterpriseValue += presentValue;
        }

        intrinsicEnterpriseValue += targetCashFlowsInfo.get(1);
        return intrinsicEnterpriseValue;
    }

    private void calculateIntrinsicSharePrice() {
        intrinsicSharePrice = calculateIntrinsicEnterpriseValue() / (double) sharesOutstandings;
    }

    public double getIntrinsicSharePrice() {
        //Rounded down to two decimal places
        double roundedVal = intrinsicSharePrice;
        roundedVal = roundedVal * 100;
        roundedVal = (double)((int) roundedVal);
        roundedVal = roundedVal /100;
        return roundedVal;
    }
}
