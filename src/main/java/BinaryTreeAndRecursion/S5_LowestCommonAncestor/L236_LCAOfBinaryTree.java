package BinaryTreeAndRecursion.S5_LowestCommonAncestor;

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
     * 解法1：DFS (Pre-order Traversal)
     * - 思路：在每次进入下层递归之前先通过 contains 方法确定 p、q 在哪边的子树上。
     * - 时间复杂度 O(n*h)：contains 方法是 O(n)，而 lowestCommonAncestor 是个二分操作 ∴ 是 O(n*h)；
     * - 空间复杂度 O(h)，其中 h 为树高（平衡树时 h=logn；退化为链表时 h=n）。
     * */
    public static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return null;
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
     * 解法2：DFS (Post-order traversal) + Backtracking
     * - 思路：解法1的前序遍历需要每次要先对左右子树进行搜索后才能对当前节点下结论 ∴ 时间复杂度较高。而该解法采用后续遍历 + 回溯：
     *   1. 后续遍历 —— 先遍历过左、右子树后再确定当前节点是否符合条件；
     *   2. 回溯 —— 自底向上在递归返回的路上，用左右子树的递归返回值来判断当前节点是否是 LCA 节点：
     *          3                       2        3). 节点3处的 sum=2 ∴ LCA 是3节点
     *        /   \     p=6, q=4      /   \                ↑
     *       5     4   --------->    1     1     2). 节点5处的 sum=1 ∴ 返回1；节点4就是 q ∴ 也返回1
     *      / \                     / \                    ↑
     *     6   2                   1   0         1). 节点6就是 p ∴ 返回1
     *
     *          3                       1        3). 注意节点5处的返回值是1而不能是2，否则节点3处的 sum 也会是2，从而覆盖了 LCA
     *        /   \     p=5, q=2      /   \                ↑
     *       5     4   --------->    2     0     2). 节点5就是 p ∴ sum=2 ∴ 该节点就是 LCA 节点
     *      / \                     / \                    ↑
     *     6   2                   0   1         1). 节点2就是 q ∴ 返回1
     *
     * - 时间复杂度 O(n)，空间复杂度 O(h)，其中 h 为树高（平衡树时 h=logn；退化为链表时 h=n）。
     * */
    private static TreeNode lca = null;  // lca 节点的指针作为类成员变量

    public static TreeNode lowestCommonAncestor2(TreeNode root, TreeNode p, TreeNode q) {
        helper(root, p, q);
        return lca;
    }

    private static int helper(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return 0;

        int left = helper(root.left, p, q);           // 先遍历左、右子树
        int right = helper(root.right, p, q);

        int curr = (root == p || root == q) ? 1 : 0;  // 再访问当前节点
        int sum = left + right + curr;
        if (sum == 2) lca = root;
        return sum > 0 ? 1 : 0;                       // 不管 sum 是1还是2，在返回时都返回1
    }

    /*
     * 解法3：DFS (Post-order traversal，解法2的简化版)
     * - 思路：与解法2一致，也是通过后续遍历，先遍历左右子树，再决定当前的节点是否符合条件。
     * - 实现：与解法2不同，不再向上层递归返回 int，而是返回 null or TreeNode 来作为判断条件是否达到的依据。
     * - 时间复杂度 O(n)，空间复杂度 O(h)，其中 h 为树高（平衡树时 h=logn；退化为链表时 h=n）。
     * */
    public static TreeNode lowestCommonAncestor3(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left = lowestCommonAncestor3(root.left, p, q);
        TreeNode right = lowestCommonAncestor3(root.right, p, q);
        if (left != null && right != null) return root;
        return left != null ? left : right;
    }

    /*
     * 解法4：DFS (Iteration) + Map + Set (非常有意思的思路！利用多种数据结构)
     * - 思路：两个节点的 LCA 其实就是两节点所在路径的第一个交叉点 ∴ 该题可以转化为求两链表的交叉点（即
     *   L160_IntersectionOfTwoLinkedLists）。但树与链表不同，无法从子节点走到父节点 ∴ 需要一个能够记录这种子节点 -> 父节点
     *   的数据结构作为辅助。Map 刚好可以满足这个需求 ∴ 总体逻辑就是：1. 先遍历树上节点建立这样一个 map；2. 再根据 map 求出
     *   p、q 两条路径的第一个交叉点。
     * - 实现：Step 2、3就是在求两个链表的交叉点，即 L160_IntersectionOfTwoLinkedLists 解法1的经典应用。
     * - 限制：∵ Map 无法插入多个相同的 key ∴ 只能用于 BST，而无法用于一般的二叉树。
     * - 时间复杂度 O(n)，空间复杂度 O(n)。
     * */
    public static TreeNode lowestCommonAncestor4(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return null;
        Stack<TreeNode> stack = new Stack<>();              // 用于存储 p 节点及其所有祖先节点
        Map<TreeNode, TreeNode> treeMap = new HashMap<>();  // 用于存储 <节点, 父节点>（即用 map 表达 BST，类似 TreeMap）
        stack.push(root);
        treeMap.put(root, null);

        // Step 1: 建立 treetMap（即用 map 表达 tree）
        while (!treeMap.containsKey(p) || !treeMap.containsKey(q)) {  // 若 p、q 被收录进了 map 则说明他们的所
            TreeNode node = stack.pop();                                  // 有祖先节点也都已被收录进了 map

            if (node.left != null) {
                treeMap.put(node.left, node);  // 收录子节点（∵ 要与其父节点配对 ∴ 只能在这里收录）
                stack.push(node.left);
            }
            if (node.right != null) {
                treeMap.put(node.right, node);  // 收录子节点
                stack.push(node.right);
            }
        }

        // Step 2: 将 p 节点及其所有祖先节点并放入 set
        Set<TreeNode> pFamilySet = new HashSet<>();
        while (p != null) {
            pFamilySet.add(p);
            p = treeMap.get(p);
        }

        // Step 3: 沿着 q 所在的路径从下往上依次查询每一个节点是否在 pFamilySet 中，在其中的第一个节点就是 LCA
        while (!pFamilySet.contains(q))
            q = treeMap.get(q);

        return q;
    }

    public static void main(String[] args) {
        TreeNode t = createBinaryTreeBreadthFirst(new Integer[]{3, 5, 1, 6, 2, 0, 8, null, null, 7, 4});
        /*
         *           3
         *        /     \
         *       5       1
         *      / \     / \
         *     6   2   0   8
         *        / \
         *       7   4
         * */

        log(lowestCommonAncestor3(t, t.get(5), t.get(1)));  // expects 3. (The LCA of nodes 5 and 1 is 3.)
        log(lowestCommonAncestor3(t, t.get(7), t.get(0)));  // expects 3.
        log(lowestCommonAncestor3(t, t.get(5), t.get(4)));  // expects 5.
        log(lowestCommonAncestor3(t, t.get(4), t.get(6)));  // expects 5.
    }
}
