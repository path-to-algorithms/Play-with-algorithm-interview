package BinaryTreeAndRecursion.LowestCommonAncestor;

import static Utils.Helpers.createBinaryTreeBreadthFirst;
import static Utils.Helpers.log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import Utils.Helpers.TreeNode;

/*
 * Lowest Common Ancestor of a Binary Tree
 *
 * - Given a binary tree, find the lowest common ancestor (LCA) of two given nodes in the tree.
 *
 * - Note:
 *   1. All of the nodes' values will be unique.
 *   2. p and q are different and both values will exist in the binary tree.
 * */

public class L236_LCAOfBinaryTree {
    /*
     * 解法1：Recursion (DFS)
     * - 思路：在每次进入下层递归之前先通过 contains 方法确定 p、q 在哪个子树上。
     * - 时间复杂度 O(n^2)，空间复杂度 O(h)，其中 h 为树高（平衡树时 h=logn；退化为链表时 h=n）。
     * */
    public static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null)
            return null;
        if (contains(root.left, p) && contains(root.left, q))
            return lowestCommonAncestor(root.left, p, q);
        if (contains(root.right, p) && contains(root.right, q))
            return lowestCommonAncestor(root.right, p, q);
        return root;
    }

    private static boolean contains(TreeNode root, TreeNode node) {  // O(n)
        if (root == null) return false;
        if (root == node) return true;
        return contains(root.left, node) || contains(root.right, node);
    }

    /*
     * 解法2：Recursion (DFS + Backtracking)
     * - 思路：使用回溯法 —— 先通过 DFS 遍历到叶子节点，在回去的路上，若节点是 p 或 q 则返回1。若某个节点的两个子节点都返回1，
     *   或一个返回1，且当前节点就是 p 或 q，则说明该节点就是 LCA。
     *           3                        2        - 在3节点处有 sum=2 ∴ LCA 是3节点
     *         /   \      p=6, q=4      /   \
     *        5     4    --------->    1     1
     *       / \                      / \
     *      6   2                    1   0
     *
     *           3                        1
     *         /   \      p=5, q=2      /   \
     *        5     4    --------->    2     0     - 在5节点处有 sum=2 ∴ LCA 是5节点（注意节点5处要 return 1
     *       / \                      / \            而不能是2，否则节点1会覆盖 lca）
     *      6   2                    0   1
     * - 时间复杂度 O(n)，空间复杂度 O(h)，其中 h 为树高（平衡树时 h=logn；退化为链表时 h=n）。
     * */
    private static TreeNode lca = null;

    public static TreeNode lowestCommonAncestor2(TreeNode root, TreeNode p, TreeNode q) {
        helper(root, p, q);
        return lca;
    }

    private static int helper(TreeNode node, TreeNode p, TreeNode q) {
        if (node == null) return 0;

        int left = helper(node.left, p, q);
        int right = helper(node.right, p, q);
        int mid = (node == p || node == q) ? 1 : 0;

        int sum = left + right + mid;
        if (sum == 2) lca = node;
        return sum > 0 ? 1 : 0;
    }

    /*
     * 解法3：Iteration (DFS)
     * - 思路：
     * - 时间复杂度 O()，空间复杂度 O(h)，其中 h 为树高（平衡树时 h=logn；退化为链表时 h=n）。
     * */
    public static TreeNode lowestCommonAncestor3(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return null;
        Stack<TreeNode> stack = new Stack<>();
        Map<TreeNode, TreeNode> parentMap = new HashMap<>();
        stack.push(root);
        parentMap.put(root, null);

        while (!parentMap.containsKey(p) || !parentMap.containsKey(q)) {
            TreeNode node = stack.pop();

            if (node.left != null) {
                parentMap.put(node.left, node);
                stack.push(node.left);
            }
            if (node.right != null) {
                parentMap.put(node.right, node);
                stack.push(node.right);
            }
        }

        Set<TreeNode> pParentSet = new HashSet<>();
        while (p != null) {
            pParentSet.add(p);
            p = parentMap.get(p);
        }

        while (!pParentSet.contains(q))
            q = parentMap.get(q);

        return q;
    }

    public static void main(String[] args) {
        TreeNode t1 = createBinaryTreeBreadthFirst(new Integer[]{3, 5, 1, 6, 2, 0, 8, null, null, 7, 4});
        /*
         *           3
         *        /     \
         *       5       1
         *      / \     / \
         *     6   2   0   8
         *        / \
         *       7   4
         * */

        log(lowestCommonAncestor3(t1, t1.get(5), t1.get(1)));  // expects 3. (The LCA of nodes 5 and 1 is 3.)
        log(lowestCommonAncestor3(t1, t1.get(7), t1.get(0)));  // expects 3.
        log(lowestCommonAncestor3(t1, t1.get(5), t1.get(4)));  // expects 5.
        log(lowestCommonAncestor3(t1, t1.get(4), t1.get(6)));  // expects 5.
    }
}
