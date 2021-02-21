package week3;

import java.util.ArrayList;
import java.util.List;

public class CombineDemo {
    List<Integer> temp = new ArrayList<Integer>();
    List<List<Integer>> ans = new ArrayList<List<Integer>>();

    /**
     * 非递归
     * TC:O((n,k) * k)
     * SC:O(k)
     */
    public List<List<Integer>> combine1(int n, int k) {
        List<Integer> temp = new ArrayList<Integer>();
        List<List<Integer>> ans = new ArrayList<List<Integer>>();
        for (int i = 1; i <= k; i++) {
            temp.add(i);
        }
        temp.add(n + 1);

        int j = 0;
        while (j < k) {
            ans.add(new ArrayList<Integer>(temp.subList(0, k)));
            j = 0;
            while (j < k && temp.get(j) + 1 == temp.get(j + 1)) {
                temp.set(j, j + 1);
                j++;
            }
            temp.set(j, temp.get(j) + 1);
        }
        return ans;
    }

    /**
     * 递归
     * TC:O((n,k) * k)
     * SC:O(n)
     */
    public List<List<Integer>> combine(int n, int k) {
        dfs(1, n, k);
        return ans;
    }

    public void dfs(int cur, int n, int k) {
        if (temp.size() + (n - cur + 1) < k) {
            return;
        }

        if (temp.size() == k) {
            ans.add(new ArrayList<Integer>(temp));
            return;
        }
        temp.add(cur);
        dfs(cur + 1, n, k);
        temp.remove(temp.size() - 1);
        dfs(cur + 1, n, k);
    }
}
