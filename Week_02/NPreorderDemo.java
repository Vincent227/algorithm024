package week2;

import leet_code.Problem206;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NPreorderDemo {


    /**
     * 递归
     * TC:O(M)
     * SC:O(M)  M为N叉树的节点数
     */
    public List<Integer> preorder1(Node root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        res.add(root.val);
        for (Node cur : root.children) {
            res.addAll(preorder(cur));
        }

        return res;
    }


    /**
     * 哈希
     * TC:O(M)
     * SC:O(M)  M为N叉树的节点数
     */
    public List<Integer> preorder(Node root) {
        LinkedList<Node> list = new LinkedList<>();
        LinkedList<Integer> res = new LinkedList<>();
        if (root == null) {
            return res;
        }

        list.add(root);
        while (!list.isEmpty()) {
            Node node = list.pollLast();
            res.add(node.val);
            Collections.reverse(node.children);
            for (Node node1 : node.children) {
                list.add(node1);
            }
        }
        return res;
    }


    class Node {
        public int val;
        public List<Node> children;

        public Node() {
        }

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, List<Node> _children) {
            val = _val;
            children = _children;
        }
    }
}
