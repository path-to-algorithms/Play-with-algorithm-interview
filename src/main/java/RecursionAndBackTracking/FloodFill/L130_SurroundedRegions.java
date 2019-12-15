package RecursionAndBackTracking.FloodFill;

import static Utils.Helpers.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
 * Surrounded Regions
 *
 * - Given a 2D board containing 'X' and 'O' (the letter O), capture all regions surrounded by 'X'.
 * - A region is captured by flipping all 'O's into 'X's in that surrounded region.
 * */

public class L130_SurroundedRegions {
    /*
     * 解法1：Flood Fill + Recursion (DFS)
     * - 思路：
     *   -> 首先一眼可知该题可使用 Flood Fill 求解。只是在不同于 L200_NumberOfIslands，该题中对有效 region 的定义是四周都
     *      是 'X' 的 'O'，而与边界相邻的 'O' 则是无效的 region。
     *      -> 如此一来，程序的主体仍然可以是 Flood Fill，只需要在遍历 'O' 的邻居时加入对边界的判断 —— 若该 '0' 与边界相邻
     *         则整个 region 无效，只有当 Flood Fill 在没有碰到边界的情况下正常结束时才算找到了有效的 region，进而再将其中
     *         的所有 'O' 都 flip 成 'X'。
     *         -> ∵ 要先遍历整个 region 后才能知道是否有效 ∴ 需要一个列表来暂存当前 region 中所有坐标，若遍历之后 region
     *            有效则 flip 其中的所有坐标，否则直接丢弃即可。
     *
     * - 实现：在遍历 region 时，一旦发现某个 'O' 的邻居越界（意味着该 region 无效），此时我们有两种方案：
     *     1. 则立即退出当前 Flood Fill，不再继续遍历该 region，在 board 上搜索下一个 region；
     *     2. 继续当前 Flood Fill，遍历完该 region；
     *   ∵ 已经发现该 region 无效，所以肯定不会 flip 它 ∴ 这两种方案的不同点在于是否一次性遍历完该 region，并将其中的所有 'O'
     *   标记为已填充。若不一次性遍历完（方案1），让该 region 中留有未遍历的 'O'，那么后面结果可能出错，例如 test case 3：
     *
     *      O O O O       O O O O   - ∵ 第一排与边界相邻 ∴ 顺时针遍历邻居时马上就会越界，从而马上退出（但已标记为已填充）
     *      X O X O  -->  X X X O   - 而同一个 region 的 [1,1] 还未填充 ∴ 在访问它并顺时针遍历它的邻居时 ∵ 它上面的 'O'
     *      X O O X       X X X X     已填充 ∴ 不会再访问，从未无法知道它是与边界相邻的 ∴ 仍然认为该 region 是有效的。
     *      X X X O       X X X O
     *
     *   ∴ 只能采用方案2，一次性将一个 region 遍历完，即使发现该 region 无效也先不退出，等所有 'O' 都被标记为已填充后才可以
     *   安心继续在 board 上搜索下一个 region。
     *
     * - 时间复杂度 O(l*w)，空间复杂度 O(l*w)。
     * */

    private static boolean[][] filled;
    private static int l, w;
    private static int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public static void solve(char[][] board) {
        if (board == null || board.length == 0 || board[0].length == 0) return;
        l = board.length;
        w = board[0].length;
        filled = new boolean[l][w];

        for (int m = 0; m < l; m++) {
            for (int n = 0; n < w; n++) {
                if (board[m][n] == 'O' && !filled[m][n]) {
                    List<Pair<Integer, Integer>> list = new ArrayList<>();  // 用于暂存当前 region 的所有格子
                    if (validRegion(board, m, n, list))        // 若该 region 有效，则 flip 该其中的所有 'O'
                        for (Pair<Integer, Integer> p : list)
                            board[p.getKey()][p.getValue()] = 'X';
                }
            }
        }
    }

    private static boolean validRegion(char[][] board, int m, int n, List<Pair<Integer, Integer>> list) {
        filled[m][n] = true;
        list.add(new Pair<>(m, n));
        boolean isValid = true;  // ∵ 要一次性遍历完当前 region，不能发现无效就中途 return ∴ 采用变量记录该 region 是否有效

        for (int[] d : directions) {
            int newM = m + d[0], newN = n + d[1];
            if (!validPos(newM, newN)) isValid = false;  // 若任一邻格越界，则说明该格子在边界上，则整个 region 无效
            else if (board[newM][newN] == 'O' && !filled[newM][newN])
                if (!validRegion(board, newM, newN, list))
                    isValid = false;
        }

        return isValid;  // 遍历完后再该 region 是否有效的信息返回
    }

    private static boolean validPos(int m, int n) {
        return m >= 0 && m < l && n >= 0 && n < w;
    }

    /*
     * 超时解：Flood Fill + Recursion (BFS)
     * - 思路：与 L200_NumberOfIslands 解法2一致。
     * - 时间复杂度 O(l*w)，空间复杂度 O(l*w)。
     * */
    public static void solve2(char[][] board) {
        if (board == null || board.length == 0 || board[0].length == 0) return;
        l = board.length;
        w = board[0].length;
        filled = new boolean[l][w];
        List<Pair<Integer, Integer>> list = new ArrayList<>();  // 用于暂存当前 region 的所有格子

        for (int m = 0; m < l; m++) {
            for (int n = 0; n < w; n++) {
                if (board[m][n] == 'O' && !filled[m][n]) {
                    list.clear();                               // 每次使用前先清空
                    if (validRegion2(board, m, n, list))        // 若该 region 有效，则 flip 该其中的所有 'O'
                        for (Pair<Integer, Integer> p : list)
                            board[p.getKey()][p.getValue()] = 'X';
                }
            }
        }
    }

    private static boolean validRegion2(char[][] board, int m, int n, List<Pair<Integer, Integer>> list) {
        boolean isValid = true;
        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        q.offer(new Pair<>(m, n));

        while (!q.isEmpty()) {
            Pair<Integer, Integer> pair = q.poll();
            int oldM = pair.getKey(), oldN = pair.getValue();
            list.add(new Pair<>(oldM, oldN));
            filled[oldM][oldN] = true;

            for (int[] d : directions) {
                int newM = oldM + d[0], newN = oldN + d[1];
                if (!validPos(newM, newN))
                    isValid = false;
                else if (board[newM][newN] == 'O' && !filled[newM][newN])
                    q.offer(new Pair<>(newM, newN));
            }
        }

        return isValid;
    }

    public static void main(String[] args) {
        char[][] board1 = {
            {'X', 'X', 'X', 'X'},
            {'X', 'O', 'O', 'X'},
            {'X', 'X', 'O', 'X'},
            {'X', 'O', 'X', 'X'}
        };
        solve2(board1);
        log(board1);
        /*
         * expects:
         *   X X X X
         *   X X X X
         *   X X X X
         *   X O X X
         * */


        char[][] board2 = {
            {'O', 'O', 'O', 'O'},
            {'X', 'O', 'X', 'O'},
            {'X', 'O', 'O', 'X'},
            {'X', 'O', 'X', 'O'}
        };
        solve2(board2);
        log(board2);
        /*
         * expects: (nothing changes)
         *   O O O O
         *   X O X O
         *   X O O X
         *   X O X O
         * */


        char[][] board3 = {
            {'O', 'O', 'O', 'O'},
            {'X', 'O', 'X', 'O'},
            {'X', 'O', 'O', 'X'},
            {'X', 'X', 'X', 'O'}   // 该行第2个元素与 board2 中不同
        };
        solve2(board3);
        log(board3);
        /*
         * expects: (nothing changes)
         *   O O O O
         *   X O X O
         *   X O O X
         *   X X X O
         * */
    }
}
