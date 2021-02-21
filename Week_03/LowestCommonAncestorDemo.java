package week3;

import java.util.*;

public class LowestCommonAncestorDemo {

    /**
     * 非递归
     * TC:O(n)
     * SC:O(n)
     */
    public TreeNode lowestCommonAncestor1(TreeNode root, TreeNode p, TreeNode q) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        Queue<TreeNode> queue = new LinkedList<>();

        parent.put(root, null);
        queue.add(root);

        while (!parent.containsKey(p) || !parent.containsKey(q)) {
            TreeNode node = queue.poll();
            if (node.left != null) {
                parent.put(node.left, node);
                queue.add(node.left);
            }
            if (node.right != null) {
                parent.put(node.right, node);
                queue.add(node.right);
            }
        }

        Set<TreeNode> ancestors = new HashSet<>();

        while (p != null) {
            ancestors.add(p);
            p = parent.get(p);
        }

        while (!ancestors.contains(q))
            q = parent.get(q);

        return q;
    }

    /**
     * 递归
     * TC:O(n)
     * SC:O(n)
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        if (left == null) {
            return right;
        }

        if (right == null) {
            return left;
        }

        return root;
    }

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }
}
