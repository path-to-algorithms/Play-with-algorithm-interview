package DP.StateTransition;

import static Utils.Helpers.log;

import java.util.Arrays;

/*
* House Robber
*
* - You are a professional robber planning to rob houses along a street where each house has a certain amount
*   of money stashed. However if two adjacent houses were broken into on the same night, the security system
*   will go off and the police will be alerted.
* - Given a list of non-negative integers representing the amount of money of each house, determine the maximum
*   amount of money you can rob tonight without alerting the police.
* */

public class L198_HouseRobber {
    /*
    * 超时解：暴力破解
    * - 思路：该题的本质是一个组合优化问题 —— 在所有房子中，哪几个房子的组合能满足条件：1.房子之间都不相邻 2.收益最大化。因此可以
    *   遍历所有组合，从中筛出所有符合条件的组合，并从中找到最大的收益。
    * - 时间复杂度 O((2^n)*n)，空间复杂度 O()。每个房子有2种可能（偷或不偷），n 个房子共有 2^n 种组合，因此遍历所有组合是
    *   O(2^n) 操作；在所有组合中进行筛选，检查每种组合其是否符合"房子之间不相邻"的条件，这是 O(n) 操作，因此整体是 O((2^n)*n)。
    * */

    /*
    * 解法1：Recursion + Memoization (DFS)
    * - 思路：∵ 该题的本质是一个组合优化问题 ∴ 并不需要求出所有的组合，只需要像 L91_DecodeWays 那样对问题进行分解：
    *                                            [0..n-1]内的最大所得
    *                       偷0号/                    偷1号|            ...   偷n-1号\
    *                  [2..n-1]内的最大所得        [3..n-1]内的最大所得            []内的最大所得
    *             偷2号/   偷3号|     \           偷3号/         \
    *     [4..n-1]内的最大所得   ...    ...  [5..n-1]内的最大所得   ...
    *   这样的分解的含义：[0..n-1]内的最多所得 = Max((偷0号所得 + [2..n-1]内的最多所得), (偷1号所得 + [3..n-1]内的最多所得), ..., 偷n-1号所得)
    *   这样的分解可以很自然的使用递归实现，而又因为分解过程中存在重叠子问题，可以使用 memoization 进行优化。
    * - 时间复杂度 O(n)，空间复杂度 O(n)。
    * */
    public static int rob1(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int[] cache = new int[nums.length + 1];
        Arrays.fill(cache, -1);
        return tryToRob(nums, 0, cache);
    }

    private static int tryToRob(int[] nums, int start, int[] cache) {     // 计算 [start..n-1] 内的最大所得
        if (start >= nums.length) return 0;
        if (cache[start] != -1) return cache[start];

        int res = 0;
        for (int i = start; i < nums.length; i++)
            res = Math.max(res, nums[i] + tryToRob(nums, i + 2, cache));  // 例：res = 1号所得 + [2..n-1]内的最大所得
                                                                          // 这里不用管 i+2 越界问题 ∵ 上面 start >= nums.length 已经覆盖过了
        return cache[start] = res;
    }

    /*
    * 解法2：DP
    * - 思路：recursion 是从前往后递归，而 DP 是从后往前递推，前一个问题的解是建立在后面问题的解的基础上。
    * - 时间复杂度 O(n)，空间复杂度 O(n)。
    * */
    public static int rob2(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        int n = nums.length;
        int[] cache = new int[n];
        Arrays.fill(cache, -1);
        cache[n - 1] = nums[n - 1];  // 先解答最后一个问题，即偷 n-1 号的所得

        for (int start = n - 2; start >= 0; start--)  // 计算 [start..n-1] 内的最大所得
            for (int i = start; i < n; i++)           // 范围固定的情况下，看哪种方案所得最多，例：求[2..5]内的最大所得，是偷2、4所得多还是偷4、5所得多
                cache[start] = Math.max(cache[start], nums[i] + (i + 2 < n ? cache[i + 2] : 0));

        return cache[0];
    }

    public static void main(String[] args) {
        log(rob2(new int[]{3, 4, 1, 2}));     // expects 6. [3, (4), 1, (2)]
        log(rob2(new int[]{4, 3, 1, 2}));     // expects 6. [(4), 3, 1, (2)]
        log(rob2(new int[]{1, 2, 3, 1}));     // expects 4. [(1), 2, (3), 1].
        log(rob2(new int[]{2, 7, 9, 3, 1}));  // expects 12. [(2), 7, (9), 3, (1)]
    }
}