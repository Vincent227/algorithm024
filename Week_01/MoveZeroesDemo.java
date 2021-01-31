package week1;

public class MoveZeroesDemo {
    /**
     * 一次遍历
     * TC:O(n)
     * SC:O(1)
     */
    public void moveZeroes2(int[] nums) {
        int j = 0;
        for (int i = 0; i < nums.length; i++) {
             if (nums[i] != 0) {
                 nums[j] = nums[i];
                 if (i != j) {
                     nums[i] = 0; //i,j不相等时候，nums[i]补0
                 }
                 j++;
             }
        }
    }

    /**
     * 一次遍历
     * TC:O(n)
     * SC:O(1)
     */
    public void moveZeroes1(int[] nums) {
        int j = 0, temp;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) { //交换
                temp = nums[i];
                nums[i] = nums[j];
                nums[j++] = temp;
            }
        }
    }

    /**
     * 两次遍历
     * TC:O(n)
     * SC:O(1)
     */
    public void moveZeroes0(int[] nums) {
        int i, j = 0;
        for (i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                nums[j++] = nums[i];
            }
        }

        for (i = j; i < nums.length; i++) {
            nums[i] = 0;
        }
    }
}
