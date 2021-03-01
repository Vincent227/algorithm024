package week4;

public class JumpDemo {

    /**
     * 贪心，反向查找
     * TC:O(n^2)
     * SC:O(1)
     */
    public int jump1(int[] nums) {
        int position = nums.length - 1;
        int steps = 0;
        while (position != 0) {
            for (int i = 0; i < position; i++) {
                if (nums[i] >= position - i) {
                    position = i;
                    steps++;
                    break;
                }
            }
        }
        return steps;
    }

    /**
     * 贪心, 正向查找
     * TC:O(n)
     * SC:O(1)
     */
    public int jump(int[] nums) {
        int end = 0;
        int maxPosition = 0;
        int steps = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            maxPosition = Math.max(maxPosition, nums[i] + i);
            if (i == end) {
                end = maxPosition;
                steps++;
            }
        }
        return steps;
    }
}
