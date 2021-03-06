package BinaryTreeAndRecursion.S1_Basics;

import static Utils.Helpers.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import Utils.Helpers.TreeNode;

/*
 * Maximum Depth of Binary Tree
 *
 * - Given a binary tree, find its maximum depth.
 * */

public class L104_MaximumDepthOfBinaryTree {
    /*
     * 解法1：DFS (Recursion)
     * - 思路：与 L111_MinimumDepthOfBinaryTree 一样，用 DFS 查找树的最大深度有两种思路：
     *   1. 从上到下遍历所有分支，从根节点开始向下层层传递节点的深度，在每找到一个叶子节点时就检查/更新最大深度；
     *   2. 从下到上层层递推每个节点的最大深度 —— 每个节点的最大深度 = min(左子树最大深度, 右子树最大深度) + 1。
     * - 实现：本解法采用思路1。
     * - 时间复杂度 O(n)，空间复杂度 O(h)，其中 h 为树高（平衡树时 h=logn；退化为链表时 h=n）。
     * */
    private static int maxDepth;

    public static int maxDepth(TreeNode root) {
        if (root == null) return 0;
        maxDepth = Integer.MIN_VALUE;
        helper(root, 1);
        return maxDepth;
    }

    private static void helper(TreeNode root, int level) {
        if (root == null) return;                     // 遇到空节点不处理
        if (root.left == null && root.right == null)  // 遇到叶子节点更新 minDepth
            maxDepth = Math.max(maxDepth, level);
        helper(root.left, level + 1);
        helper(root.right, level + 1);
	}

	/*
     * 解法1：DFS (Recursion)
     * - 思路：采用解法1中的思路2。
     * - 时间复杂度 O(n)，空间复杂度 O(h)，其中 h 为树高（平衡树时 h=logn；退化为链表时 h=n）。
     * */
    public static int maxDepth2(TreeNode root) {
        if (root == null) return 0;
        return Math.max(maxDepth2(root.left), maxDepth2(root.right)) + 1;
    }

    /*
     * 解法3：BFS (Level lists in res)
     * - 思路：与 L102_BinaryTreeLevelOrderTraversal 解法1一致。
     * - 时间复杂度 O(n)，空间复杂度 O(n)。
     * */
    public static int maxDepth3(TreeNode root) {
        if (root == null) return 0;
        List<List<TreeNode>> res = new ArrayList<>();
        Queue<Pair<TreeNode, Integer>> q = new LinkedList<>();
        q.offer(new Pair<>(root, 0));

        while (!q.isEmpty()) {
            Pair<TreeNode, Integer> pair = q.poll();
            TreeNode node = pair.getKey();
            int level = pair.getValue();

            if (level == res.size())
                res.add(new ArrayList<>());
            res.get(level).add(node);

            if (node.left != null) q.offer(new Pair<>(node.left, level + 1));
            if (node.right != null) q.offer(new Pair<>(node.right, level + 1));
        }

        return res.size();
    }

    /*
     * 解法4：BFS（解法3的简化版）
     * - 时间复杂度 O(n)，空间复杂度 O(n)。
     * */
    public static int maxDepth4(TreeNode root) {
        if (root == null) return 0;
        Queue<Pair<TreeNode, Integer>> q = new LinkedList<>();
        q.offer(new Pair<>(root, 1));
        int depth = 0;

        while (!q.isEmpty()) {
            Pair<TreeNode, Integer> pair = q.poll();
            TreeNode node = pair.getKey();
            int level = pair.getValue();

            depth = Math.max(depth, level);
            if (node.left != null) q.offer(new Pair<>(node.left, level + 1));
            if (node.right != null) q.offer(new Pair<>(node.right, level + 1));
        }

        return depth;
    }

    /*
     * 解法5：BFS（解法4的简化版）
     * - 时间复杂度 O(n)，空间复杂度 O(n)。
     * */
    public static int maxDepth5(TreeNode root) {
        if (root == null) return 0;
        int depth = 0;
        Queue<TreeNode> q = new LinkedList<>();
        q.offer(root);

        while (!q.isEmpty()) {
            int qSize = q.size();
            while (qSize-- > 0) {  // 一次性将 q 中同一层的节点都消费完
                TreeNode node = q.poll();
                if (node.left != null) q.offer(node.left);
                if (node.right != null) q.offer(node.right);
            }
            depth++;               // 消费完一层的节点后就可以让 depth++
        }

        return depth;
    }

    /*
     * 解法6：DFS (Iteration)
     * - 思路：DFS 无法采用解法5中一次性将一层节点全部遍历完的方式，只能采用解法4的方式将层级信息携带在节点上。
     * - 实现：跟解法3的区别只是采用了 Stack。
     * - 时间复杂度 O(n)，空间复杂度 O(n)。
     * */
    public static int maxDepth6(TreeNode root) {
        if (root == null) return 0;

        int depth = 0;
        Stack<Pair<TreeNode, Integer>> stack = new Stack<>();
        stack.push(new Pair<>(root, 1));

        while (!stack.isEmpty()) {
            Pair<TreeNode, Integer> pair = stack.pop();
            TreeNode node = pair.getKey();
            int level = pair.getValue();

            depth = Math.max(depth, level);
            if (node.left != null) stack.push(new Pair<>(node.left, level + 1));
            if (node.right != null) stack.push(new Pair<>(node.right, level + 1));
        }

        return depth;
    }

    public static void main(String[] args) {
        TreeNode t1 = createBinaryTreeBreadthFirst(new Integer[]{3, 9, 20, null, null, 15, 7});
        log(maxDepth(t1));
        /*
         * expects 3.
         *      3
         *     / \
         *    9  20
         *      /  \
         *     15   7
         * */

        TreeNode t2 = createBinaryTreeBreadthFirst(new Integer[]{1, 2, 3, 4, null, null, 5});
        log(maxDepth(t2));
        /*
         * expects 3.
         *      1
         *     / \
         *    2   3
         *   /     \
         *  4       5
         * */
    }
}
