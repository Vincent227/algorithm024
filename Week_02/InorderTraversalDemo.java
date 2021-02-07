package week2;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class InorderTraversalDemo {


    /**
     * 迭代
     * TC:O(M)
     * SC:O(M)  M为树的高度
     */
    public List<Integer> inorderTraversal1(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<TreeNode>();
        while(stack.size() > 0 || root != null) {
            if(root != null) {
                stack.add(root);
                root = root.left;
            } else {
                TreeNode tmp = stack.pop();
                res.add(tmp.val);
                root = tmp.right;
            }
        }
        return res;
    }

    /**
     * 递归
     * TC:O(M)
     * SC:O(M)  M为树的高度
     */
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        dfs(res,root);
        return res;
    }

    void dfs(List<Integer> res, TreeNode root) {
        if(root == null) {
            return;
        }
        dfs(res,root.left);
        res.add(root.val);
        dfs(res,root.right);
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
}
