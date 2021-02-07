package week2;

import java.util.Arrays;

public class IsAnagramDemo {

    /**
     * 哈希
     * TC:O(n)
     * SC:O(s)  s为字符集大小（ 26 ）
     */
    public boolean isAnagram1(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        int[] letters = new int[26];
        for (int i = 0; i < s.length(); i++) {
            letters[s.charAt(i) - 'a']++;
        }

        for (int i = 0; i < t.length(); i++) {
            letters[t.charAt(i) - 'a']--;
            if (letters[t.charAt(i) - 'a'] < 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * 排序
     * TC:O(nlogn)
     * SC:O(logn)
     */
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        char[] strs = s.toCharArray();
        char[] strt = t.toCharArray();
        Arrays.sort(strs);
        Arrays.sort(strt);

        return Arrays.equals(strs, strt);
    }
}
