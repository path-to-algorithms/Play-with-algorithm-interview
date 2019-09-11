package DP.KnapsackProblem;

import static Utils.Helpers.*;

import java.util.Arrays;

/*
* 0/1 Knapsack Problem
*
* - 尝试往容量为 C 的背包里放 n 件不同种物品，其中每件物品的重量为 w[i]，价值为 v[i]。求在不超过背包容量的前提下，向背包中放入
*   哪些物品使得总价值最大。
* - 注意：只要不超过容量即可，不需要完全填满，这是与 L416 的不同之处。
*
* - 分析：该问题本质上是个最优组合问题，尝试以下思路：
*   - Brute Force：
*     1. n 个不同种物品，每种物品都可以选择放入或不放入背包，因此共有 2^n 种组合；
*     2. 计算每种组合的总重，并过滤掉所有总重超过 C 的组合；
*     3. 再对剩下的每种组合遍历计算总价值，并选出最大者。
*     - 总时间复杂度为 O((2^n)*n)。
*
*   - Greedy Algorithm：
*     采用贪心算法（优先放入性价比最高的物品）对于解决这类问题存在缺陷：∵ 本问题是要求的是一个“全局最优解”，而贪心算法是只顾眼前的
*     选择策略 ∴ 很容易举出反例：
*                           Item       0     1     2
*                           Weight     1     2     3
*                           Value      6     10    12
*                           v / w      6     10    12
*     若背包容量为5，此时使用贪心算法优先放入性价比（v/w）最高的物品，则只能放入0、1号物品（价值16，占用3的容量），而剩余的2的容
*     量就被浪费掉了。而事实上，全局最优解应该是放入1、2号物品（价值22，占用5的容量），因此贪心算法对本问题不可行。
*
*   ⭐ DP：
*     能否采用 DP 取决于该问题是否：
*       1. 具有“最优子结构”性质 —— 通过求子问题的最优解可获得原问题的最优解；
*       2. 满足“无后效性” —— 某状态下的收益只与当前决策有关，之前的决策不会影响当前决策（liuyubobobo 认为最优子结构就隐含着无后效性）；
*       3. 具有“重叠子问题”。
*     而要确定问题是否满足这3个条件，需要先识别出原问题中的子问题，而要识别子问题可通过原问题中的约束条件（或变量）得到启发：
*       1. 每次从前 i 个物品中找出最佳价值组合（i 是变量，每次决定放/不放一个物品后 i 都会-1）；
*       2. 背包的剩余容量 j（每次放入一个物品后 j 都会减小）；
*     这两个约束条件是该问题中的变量，不同的变量组合就意味着该问题的不同状态，因此这两个变量也就是构成子问题的参数：
*       - 定义子问题：f(i, j) 表示“用前 i 个物品填充剩余容量 j 的背包所能得到的最大价值”。
*       - 写出状态转移方程：
*         - ∵ 对任意一件物品 i 都有放/不放两种选择 ∴ f 的最大价值 = max(放 i 能得到的最大价值, 不放 i 能得到的最大价值)。
*         - 放 i 能得到的最大价值为 v(i) + f(i-1, j-w(i))，即物品 i 的价值 + 用前 i-1 个物品中填充剩余容量所能得到的最大价值；
*         - 不放 i 能得到的最大价值为 f(i-1, j)，即用前 i-1 个物品填充容量 j 所能得到的最大价值；
*         - ∴ 完整方程为 f(i, j) = max(f(i-1, j), v(i) + f(i-1, j-w(i)))。
*
* - 💎 心得：
*   1. DP 问题使用填表法会非常有助于理解，练习时要多画一画；
*   2. DP vs. 贪心算法：
*      这两种算法的区别在于是否需要全局最优，还是局部最优即可。DP 的“全局最优”体现在子问题的定义，以及从子问题递推出原问题上：
*        a. 只用第0个物品填充剩余容量所能得到的最大价值；
*        b. 用第0、1个物品填充剩余容量所能得到的最大价值；
*        c. 用第0、1、2个物品填充剩余容量所能得到的最大价值；
*        d. ...
*        n. 用所有物品填充剩余容量所能得到的最大价值；
*      其中，a 是 b 的子问题，b 的最优解是建立在 a 的最优解之上的；b 是 c 的子问题，c 的最优解是建立在 b 的最优解之上的；以此类推。
*      可见，在该递推过程中“局部问题”的范围逐渐增大，直到覆盖全局“全局问题”为止。若是采用贪心，则会是：
*        a. 用性价比最高的物品填充剩余容量；
*        b. 用性价比第2高的物品填充剩余容量；
*        ...
*      该过程中不存在递推关系，后一个问题的解不建立在前一个问题的解之上，每个问题都是关注的是个体，因此“局部问题”的范围都是一样的。
*
* - 详解 SEE: 微信搜“【动态规划】一次搞定三种背包问题”。
* */

public class _ZeroOneKnapsack {
    /*
    * 解法1：Recursion + Memoization
    * - 思路：top-down 方式。
    * - 时间复杂度 O(n*c)，即填表的耗时；空间复杂度 O(n*c)。
    * */
    public static int knapsack(int[] w, int[] v, int c) {
        int n = w.length;
        int[][] cache = new int[n][c + 1];  // ∵ 状态转移方程有两个输入变量 ∴ 缓存也是二维的（n 行 c+1 列；c 的取值范围是 [0,c]）
        for (int[] row : cache)
            Arrays.fill(row, -1);           // ∵ 要缓存的值可能有0 ∴ 都初始化为-1
        return largestValue(n - 1, c, w, v, cache);
    }

    private static int largestValue(int i, int j, int[] w, int[] v, int[][] cache) {
        if (i < 0 || j == 0) return 0;
        if (cache[i][j] != -1) return cache[i][j];

        int res = largestValue(i - 1, j, w, v, cache);  // 不放入第 i 个的最大价值
        if (j >= w[i])                                  // 若剩余容量充足则可以尝试放入第 i 个
            res = Math.max(res, v[i] + largestValue(i - 1, j - w[i], w, v, cache));

        return cache[i][j] = res;
    }

    /*
    * 解法2：DP
    * - 思路：bottom-up 方式直接 DP：
    *   1. ∵ 该问题中有两个变量 ∴ 状态转移过程可以通过填表发（tabulation）来可视化。动画演示 SEE:
    *      https://coding.imooc.com/lesson/82.html#mid=2955 (18'15'')。
    *   2. ∵ 解法1中要解决问题 f(i, ..)，需要先解决问题 f(i-1, ..)，需要先解决 f(i-2, ..)，... 直到 f(0, ..)，因此 i=0
    *      的情况是该题的最基本问题。
    * - 时间复杂度 O(n*c)，空间复杂度 O(n*c)。
    * */
    public static int knapsack2(int[] w, int[] v, int c) {
        int n = w.length;
        if (n == 0) return 0;

        int[][] dp = new int[n][c + 1];

        for (int j = 0; j < dp.length; j++)  // 先解决最基础的问题（即表的第0行，即只考虑0号物品时不同容量下能得到的最大价值）
            dp[0][j] = (j >= w[0]) ? v[0] : 0;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j <= c; j++) {
                dp[i][j] = dp[i - 1][j];  // cache[i][j] 表示在剩余容量为 j 时从 0-i 号物品中所能得到的最大价值
                if (j >= w[i])
                    dp[i][j] = Math.max(dp[i][j], v[i] + dp[i - 1][j - w[i]]);
            }
        }

        return dp[n - 1][c];
    }

    /*
    * 解法3：解法2的空间优化版（滚动数组）
    * - 思路：观察解法2中的状态转移方程：f(i, j) = max(f(i-1, j), v(i) + f(i-1, j-w(i)))，可发现 f(i,..) 只与 f(i-1,..)
    *   有关，即填表法中除第0行之外的每一行中的值都能通过上一行中的值求得，而与其他行无关。因此：
    *   1. 不需要缓存整个二维表，只需缓存第 i 行和第 i-1 行这两行即可；
    *   2. 在从上到下逐行计算时，交替使用这两行缓存，例如：当缓存了第0、1两行，需要计算第2行，此时不再需要第0行的值，因此可用第2行
    *      的计算结果覆盖第0行值；当需要计算第3行时，不再需要第1行的值，因此可用第3行的计算结果覆盖第1行的值…… 该过程中的规律是：
    *      若要计算的是偶数行则覆盖缓存第0行；若要计算的是奇数行则覆盖缓存第1行。
    *   这样优化之后，空间复杂度从 O(n*c) 降低到了 O(2c)，从而能计算的背包容量大大增加。
    * - 时间复杂度 O(n*c)，空间复杂度 O(c)。
    * */
    public static int knapsack3(int[] w, int[] v, int c) {
        int n = w.length;
        if (n == 0) return 0;

        int[][] dp = new int[2][c + 1];

        for (int j = 0; j < dp.length; j++)
            dp[0][j] = (j >= w[0]) ? v[0] : 0;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j <= c; j++) {
                dp[i % 2][j] = dp[(i-1) % 2][j];  // 若 i 为偶则写第0行，读第1行；若 i 为奇则写第1行，读第0行
                if (j >= w[i])
                    dp[i % 2][j] = Math.max(dp[i % 2][j], v[i] + dp[(i-1) % 2][j - w[i]]);
            }
        }

        return dp[(n-1) % 2][c];  // 凡是有 i 的地方都要 %2
    }

    /*
    * 解法4：解法3的空间优化版（一维数组）
    * - 思路：解法3中说 f(i,..) 只与 f(i-1,..) 有关。实际上还可以更具体 —— 因为状态转移方程中的 j-w(i) 总是 < j，所以
    *   f(i, j) 只与 f(i-1, 1..j) 有关，即填表法中待计算行里的任意一格 [i,j] 的值只与上一行左半部分 [0..j] 区间里的值有关，
    *   而与右边的值无关，因此可以将解法3中的两行 cache 合成一行，每次从右向左进行覆盖。可视化讲解 SEE:
    *   https://coding.imooc.com/lesson/82.html#mid=2984 (8'20'')。
    *   最终状态转移方程简化为：f(i, j) = max(f(j), v[i] + f(j - w[j]))。
    * - 时间复杂度 O(n*c)，空间复杂度 O(c)，本次是常数级的优化，复杂度量级上没有变化。
    * */
    public static int knapsack4(int[] w, int[] v, int c) {
        int n = w.length;
        if (n <= 0) return 0;

        int[] dp = new int[c + 1];
        
        for (int j = 0; j < dp.length; j++)
            dp[j] = (j >= w[0]) ? v[0] : 0;

        for (int i = 1; i < n; i++)
            for (int j = c; j >= w[i]; j--)  // 只覆盖 j >= w[i] 的部分即可，因为 j < w[i]，物品放不进背包因此结果和上次计算是一样的
                dp[j] = Math.max(dp[j], v[i] + dp[j - w[i]]);

        return dp[c];
    }

    public static void main(String[] args) {
        log(knapsack2(new int[]{1, 2, 3}, new int[]{6, 10, 12}, 5));       // expects 22. (10 + 12)
        log(knapsack2(new int[]{1, 3, 4, 2}, new int[]{3, 9, 12, 8}, 5));  // expects 17. (9 + 8)
    }
}
