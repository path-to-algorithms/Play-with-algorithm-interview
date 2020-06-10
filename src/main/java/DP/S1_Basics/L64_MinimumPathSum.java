package DP.S1_Basics;

import static Utils.Helpers.log;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/*
 * Minimum Path Sum
 *
 * - Given a m x n grid filled with non-negative numbers, find a path from top left to bottom right which
 *   minimizes the sum of all numbers along its path. Returns the sum at the end.
 * - Rule: You can only move either down or right at any point in time.
 *
 * 💎 若用图论建模则需要注意：
 *   1. 本题与 L120_Triangle 不同，L120 是由一个顶点出发到多个顶点结束，而本题中是由一个顶点出发到一个顶点结束。
 *   2. 该题与 L279_PerfectSquares 不同，L279 可建模成无权图，无权图的最短路径是节点数最少的那条；而该题用图论建模只能建模成
 *      带权图，因为要求的是节点值之和最小的路径，相当于求成本最小的路径。
 * */

public class L64_MinimumPathSum {
    /*
     * 超时解：BFS
     * - 思路：与 L120_Triangle 解法2完全一致，采用 BFS 遍历每一条路径，同时计算最小的节点值之和。
     *        1 → 3 → 1
     *        ↓   ↓   ↓
     *        1 → 5 → 1
     *        ↓   ↓   ↓
     *        4 → 2 → 1
     *   这样该问题就转化为了求左上到右下的所有路径中最小的节点值之和 ∴ 可以用 BFS 找到每一条路径，同时求其中最小的顶点值之和。
     * - 时间复杂度 O(2^n)，空间复杂度 O(n)，其中 n 为节点个数。
     * */
    static class Path {
        final int r, c, sum;
        public Path(int row, int col, int sum) {
            this.r = row;
            this.c = col;
            this.sum = sum;
        }
    }

    public static int minPathSum(int[][] grid) {
        int minSum = Integer.MAX_VALUE;
        int w = grid.length, l = grid[0].length;

        Queue<Path> q = new LinkedList<>();
        q.offer(new Path(0, 0, grid[0][0]));

        while (!q.isEmpty()) {
            Path path = q.poll();
            int r = path.r, c = path.c, sum = path.sum;

            if (r == w - 1 && c == l - 1) {      // 若已抵达右下角
                minSum = Math.min(minSum, sum);  // 求最小的节点值之和
                continue;
            }

            if (c < l - 1)       // 若还没到最右一列，则入队右侧节点
                q.offer(new Path(r, c + 1, sum + grid[r][c + 1]));
            if (r < w - 1)       // 若还没到最后一行，则入队下方节点
                q.offer(new Path(r + 1, c, sum + grid[r + 1][c]));
        }

        return minSum;
    }

    /*
     * // TODO: 解法1：Dijkstra
     * - 思路：带权图的最短路径可使用 Dijkstra 算法，可先解决 https://leetcode.com/problems/network-delay-time/，再解决本问题
     * - 时间复杂度 O()，空间复杂度 O()。
     * */
    public static int minPathSum1(int[][] grid) {
        return 0;
    }

    /*
     * 超时解2：DFS
     * - 思路：从左上到右下递归地计算每个节点到达右下角的 minimum path sum，因此有：
     *   - 子问题定义：f(r,c) 表示从节点 [r,c] 到达右下角的 minimum path sum。
     *   - 递推表达式：f(r,c) = min(f(r+1,c) + f(r,c+1))，其中 r ∈ [0,w)，l ∈ [0,l)。
     *       1 ← 3 ← 1
     *       ↑   ↑   ↑   - f(0,0) = min(f(1,0), f(0,1))
     *       1 ← 5 ← 1            = min(min(f(2,0), f(1,1)), min(f(0,2), f(1,1)))
     *       ↑   ↑   ↑            = ...
     *       4 ← 2 ← 1
     *   其中：
     *     - ∵ f(2,0)、f(0,2) 是边缘节点，只有一个方向可以走 ∴ f(2,0) = f(2,1); f(0,2) = f(1,2)；
     *     - f(1,0)、f(0,1) 都可以走到 f(1,1) ∴ 出现了重复计算。
     * - 时间复杂度 O(2^(l*w))，空间复杂度 O(l*w)。
     * */
    public static int minPathSum0(int[][] grid) {
        if (grid == null || grid[0] == null) return 0;
        return minPathSumFrom(grid, 0, 0);
    }

    private static int minPathSumFrom(int[][] grid, int r, int c) {
        int w = grid.length;
        int l = grid[0].length;
        int sum = grid[r][c];

        if (r == w - 1 && c == l - 1)
            return sum;
        if (r == w - 1)
            return sum + minPathSumFrom(grid, r, c + 1);
        if (c == l - 1)
            return sum + minPathSumFrom(grid, r + 1, c);

        return sum + Math.min(
            minPathSumFrom(grid, r + 1, c),
            minPathSumFrom(grid, r, c + 1));
    }

    /*
     * 解法2：DFS + Memoization（Recursion with cache）
     * - 思路：在超时解2的基础上加入 Memoization 进行优化。
     * - 时间复杂度 O(l*w)，空间复杂度 O(l*w)。
     * */
    public static int minPathSum2(int[][] grid) {
        if (grid == null || grid[0] == null) return 0;

        int[][] cache = new int[grid.length][grid[0].length];
        for (int[] row : cache)
            Arrays.fill(row, Integer.MAX_VALUE);

        return minPathSumFrom2(grid, 0, 0, cache);
    }

    private static int minPathSumFrom2(int[][] grid, int r, int c, int[][] cache) {
        int w = grid.length;
        int l = grid[0].length;
        int sum = grid[r][c];

        if (r == w - 1 && c == l - 1)
            return sum;

        if (cache[r][c] != Integer.MAX_VALUE)
            return cache[r][c];

        if (r == w - 1)
            return cache[r][c] = sum + minPathSumFrom2(grid, r, c + 1, cache);
        if (c == l - 1)
            return cache[r][c] = sum + minPathSumFrom2(grid, r + 1, c, cache);

        return cache[r][c] = sum + Math.min(
            minPathSumFrom2(grid, r + 1, c, cache),
            minPathSumFrom2(grid, r, c + 1, cache));
    }

    /*
     * 解法3：DP
     * - 思路：在超时解2中，f(r,c) 表示从节点 [r,c] 到达右下角的 minimum path sum，每个节点的解 f(r,c) 是建立在其下游两个
     *   节点的解 f(r+1,c)、f(r,c+1) 之上的 ∴ 可以根据递推表达式 f(r,c) = min(f(r+1,c) + f(r,c+1)) 来设计递归程序 ——
     *   这是自上而下的思路。而 DP 的思路与此是一致的，只是自下而上进行递推，即由 f(w-1,l-1) 递推出 f(w-2,l-1)、f(w-1,l-2)，
     *   再递推出 f(w-2,l-2)…… 如此往复直到递推出 f(0,0) 为止。
     * - 优化：该解法还可以再进行空间优化 —— ∵ 每一行的计算都只依赖于当前行右侧和下一行中的值 ∴ 可以采用类似 _ZeroOneKnapsack
     *   中解法3的滚动数组方案，dp 数组只保留两行并重复利用。但遍历方向需要改为从左上到右下（∵ 需要知道当前是奇/偶数行）。
     * - 时间复杂度 O(l*w)，空间复杂度 O(l*w)。
     * */
    public static int minPathSum3(int[][] grid) {
        if (grid == null || grid[0] == null) return 0;

        int w = grid.length;
        int l = grid[0].length;

        int[][] dp = new int[w][l];
        for (int[] row : dp)
            Arrays.fill(row, Integer.MAX_VALUE);

        dp[w - 1][l - 1] = grid[w - 1][l - 1];  // 设置初值

        for (int r = w - 1; r >= 0; r--) {
            for (int c = l - 1; c >= 0; c--) {  // 由 [w-1,l-1] 开始往左、往上进行递推
                if (r != w - 1)
                    dp[r][c] = Math.min(dp[r][c], grid[r][c] + dp[r + 1][c]);
                if (c != l - 1)
                    dp[r][c] = Math.min(dp[r][c], grid[r][c] + dp[r][c + 1]);
            }
        }

        return dp[0][0];
    }

    /*
     * 解法4：In-place DP
     * - 思路：与解法3一致。
     * - 实现：
     *     1. 不另外建立 dp 数组，而是就地修改 grid 数组；
     *     2. 遍历方向为从左上到右下，不断对 grid 进行填充/更新：f(r,c) = min(f(r-1, c), f(r, c-1))。
     *
     *          1  3  1         1 → 4 → 5         1 → 4 → 5         1 → 4 → 5
     *          1  5  1   -->              -->    ↓   ↓   ↓   -->   ↓   ↓   ↓
     *          4  2  1                           2 → 7 → 6         2 → 7 → 6
     *                                                              ↓   ↓   ↓
     *                                                              6 → 8 → 7
     * - 时间复杂度 O(l*w)，空间复杂度 O(1)。
     * */
    public static int minPathSum4(int[][] grid) {
        if (grid == null || grid[0] == null) return 0;

        int w = grid.length;
        int l = grid[0].length;

        for (int r = 0; r < w; r++) {
            for (int c = 0; c < l; c++) {
                if (r == 0 && c == 0) continue;
                if (r == 0)
                    grid[0][c] += grid[0][c - 1];
                else if (c == 0)
                    grid[r][0] += grid[r - 1][c];
                else
                    grid[r][c] += Math.min(grid[r - 1][c], grid[r][c - 1]);
            }
        }

        return grid[w - 1][l - 1];
    }

    /*
     * 解法5：In-place DP（解法4的另一种写法）
     * - 思路：与解法4一致。
     * - 实现：观察解法4可知 ∵ 第一行和第一列是特殊情况，不需要比较，只有一种选择 ∴ 可以先手动处理这些特殊情况，然后再处理其他
     *   位置上的一般情况：
     *       1   3   1             1 → 4 → 5             1 → 4 → 5              1 → 4 → 5
     *                   Add up                Add up    ↓            Handle    ↓   ↓   ↓
     *       1   5   1  -------->  1   5   1  -------->  2   5   1  --------->  2 → 7 → 6
     *                   1st row               1st col   ↓           the rest   ↓   ↓   ↓
     *       4   2   1             4   2   1             6   2   1              6 → 8 → 7
     * - 时间复杂度 O(l*w)，空间复杂度 O(1)。
     * */
    public static int minPathSum5(int[][] grid) {
        int w = grid.length;
        int l = grid[0].length;

        for (int r = 1; r < w; r++)        // Add up 1st row
            grid[r][0] += grid[r - 1][0];

        for (int c = 1; c < l; c++)        // Add up 1st column
            grid[0][c] += grid[0][c - 1];

        for (int r = 1; r < w; r++)        // Handle the rest
            for (int c = 1; c < l; c++)
                grid[r][c] += Math.min(grid[r - 1][c], grid[r][c - 1]);

        return grid[w - 1][l - 1];
    }

    public static void main(String[] args) {
        log(minPathSum2(new int[][]{
            {1, 3, 1},
            {1, 5, 1},
            {4, 2, 1}
        }));  // expects 7. (1->3->1->1->1)

        log(minPathSum2(new int[][]{
            {1, 3, 4},
            {1, 2, 1},
        }));  // expects 5. (1->1->2->1)

        log(minPathSum2(new int[][]{
            {1, 2, 3}
        }));  // expects 6.

        log(minPathSum2(new int[][]{
            {0}
        }));  // expects 0.
    }
}
