package RecursionAndBackTracking;

import static Utils.Helpers.*;

import java.util.ArrayList;
import java.util.List;

/*
 * Restore IP Addresses
 *
 * - Given a string containing only digits, restore it by returning all possible valid IP address combinations.
 * */

public class L93_RestoreIPAddresses {
    /*
     * 解法1：Recursion + Backtracking
     * - 思路：采用类似 L17_LetterCombinationsOfPhoneNumber 解法1的回溯法，设置指针 i 在 s 上滑动，∵ 每次滑动可以有3种
     *   不同情况（滑动1~3位）∴ 分别对这3种情况进行分支，递归下去检查是否能得到有效 ip。
     *                                  ""
     *                    /             |            \
     *              "1"                "12"            "123"
     *           /   |   \            /    \          /    \
     *      "1.2" "1.23" "1.234"  "12.3" "12.34"  "123.4" "123.45"
     *       / \    / \     |       / \     |       |
     *       ...    ...    ...      ...    ...     ...
     *   其中：
     *     - 若一个 ip 已有4个 component，但 i 还未到达 s 末尾，则该 ip 无效；
     *     - 若一个 ip 中的 component 个数 < 3，但 i 已到达 s 末尾，则该 ip 无效；
     *     - 若一个 component 大于1位但以"0"开头，则该 component 无效；
     *     - 若一个 component 大于255，则该 component 无效。
     * - 时间复杂度 O(2^n)，空间复杂度 O(n)，其中 n 为 s 的长度（注：O(2^n) 只是量级，并不精确）。
     * */
    public static List<String> restoreIpAddresses(String s) {
        List<String> res = new ArrayList<>();
        dfs(s, 0, "", 0, res);
        return res;
    }

    private static void dfs(String s, int i, String restoredIp, int count, List<String> res) {
        if (count > 4) return;
        if (count == 4 && i == s.length()) res.add(restoredIp);

        for (int n = 1; n <= 3; n++) {
            if (i + n > s.length()) break;

            String comp = s.substring(i, i + n);
            if ((comp.startsWith("0") && comp.length() > 1) || Integer.parseInt(comp) > 255) continue;

            String newIp = restoredIp + comp + (count == 3 ? "" : ".");
            dfs(s, i + n, newIp, count + 1, res);
        }
    }

    public static void main(String[] args) {
        log(restoreIpAddresses("25525511135"));  // expects ["255.255.11.135", "255.255.111.35"]
        log(restoreIpAddresses("123456789"));    // expects ["123.45.67.89"]
        log(restoreIpAddresses("12345"));        // expects ["1.2.3.45", "1.2.34.5", "1.23.4.5", "12.3.4.5"]
        log(restoreIpAddresses("02095"));        // expects ["0.2.0.95", "0.20.9.5"]
    }
}
