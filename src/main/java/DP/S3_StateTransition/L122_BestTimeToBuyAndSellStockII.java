package DP.S3_StateTransition;

import static Utils.Helpers.*;

/*
 * Best Time to Buy and Sell Stock II
 *
 * - Say you have an array for which the ith element is the price of a given stock on day i. Design an
 *   algorithm to find the maximum profit.
 *
 * - You may do as many transactions as you like (i.e., buy one and sell one share of the stock multiple times).
 *
 * - Note you may not engage in multiple transactions at the same time (i.e., you must sell the stock before
 *   you buy again).
 * */

public class L122_BestTimeToBuyAndSellStockII {
    /*
     * 解法1：Peak Valley
     * - 思路：
     * - 时间复杂度 O(n^n)，空间复杂度 O(1)。
     * */
    public static int maxProfit(int[] prices) {
		return 0;
	}

    /*
     * 解法2：DP Top-down
     * - 思路：
     * - 时间复杂度 O(n)，空间复杂度 O(1)。
     * */
    public static int maxProfit2(int[] prices) {
		return 0;
	}

	/*
     * 解法3：DP
     * - 思路：The action we can do on ith day is either buy (if last action is sell), or sell (if last action
     *   is buy), or do nothing.
     *   - buy(i) = max(sell(i) - pri, buy(i-1))；
     *   - sell(i) = max()
     * - 时间复杂度 O(n)，空间复杂度 O(1)。
     * */
    public static int maxProfit3(int[] prices) {
        int n = prices.length;
        int[][] dp = new int[2][n];
        dp[0][0] = 0 - prices[0];  // cost of buying stock
        dp[0][1] = 0;              // 0 profit as cant sell stock

        for (int i = 1; i < n; i++) {
            // most profitable buy is max of cost of buying yesterdays stock, and profit from selling stock
            // yesterday - buying today
            dp[0][i] = Math.max(dp[0][i-1], dp[1][i-1] - prices[i]);

            // max of selling (which is essentially tracking our profit) is max profit we had yesterday and
            // max of having stock yesteday and selling today
            dp[1][i] = Math.max(dp[1][i-1], dp[0][i-1] + prices[i]);
        }

        return dp[1][n - 1];
    }

    /*
     * 解法4：DP
     * - 思路：The action we can do on ith day is either buy (if last action is sell), or sell (if last action
     *   is buy), or do nothing.
     *   - buy(i) = max(sell(i) - pri, buy(i-1))；
     *   - sell(i) = max()
     * - 时间复杂度 O(n)，空间复杂度 O(1)。
     * */
    public static int maxProfit4(int[] prices) {
        if (prices.length == 0) return 0;
        int lastBuy = -prices[0];
        int lastSell = 0;

        for (int price : prices) {
            int currBuy = Math.max(lastBuy, lastSell - price);
            int currSold = Math.max(lastSell, lastBuy + price);
            lastBuy = currBuy;
            lastSell = currSold;
        }

        return lastSell;
    }

    public static void main(String[] args) {
        log(maxProfit(new int[]{7, 1, 5, 3, 6, 4}));  // expects 7. [-, buy, sell, buy, sell, -]
        log(maxProfit(new int[]{1, 2, 3, 4, 5}));     // expects 4. [buy, -, -, -, sell]
        log(maxProfit(new int[]{7, 6, 4, 3, 1}));     // expects 0. no transaction.
    }
}