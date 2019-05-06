package HashTable.SetAndMapBasics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static Utils.Helpers.log;

/*
* Valid Anagram
*
* - Determine if string t is an anagram of string s.
* - 思路：找到 anagram 的特点 —— Two strings are anagrams if and only if their character counts are the same.
* */

public class L242_ValidAnagram {
    /*
    * 解法1：排序
    * - 时间复杂度 O(nlogn)，空间复杂度 O(1)。
    * */
    public static boolean isAnagram(String s, String t) {
        if (t.length() != s.length())
            return false;
        char[] sArr = s.toCharArray();
        char[] tArr = t.toCharArray();
        Arrays.sort(sArr);
        Arrays.sort(tArr);
        return Arrays.equals(sArr, tArr);
    }

    /*
    * 解法2：使用 map
    * - 时间复杂度 O(n)，空间复杂度 O(n)。
    * */
    public static boolean isAnagram2(String s, String t) {
        if (t.length() != s.length())
            return false;

        Map<Character, Integer> map = new HashMap<>();
        for (char c : s.toCharArray())  // O(n)
            map.put(c, map.getOrDefault(c, 0) + 1);

        for (char c : t.toCharArray()) {  // O(n)
            if (!map.containsKey(c))
                return false;
            if (map.get(c) > 0) {
                map.put(c, map.get(c) - 1);
                if (map.get(c) == 0)
                    map.remove(c);
            }
        }
        return map.isEmpty();
    }

    /*
    * 解法3：使用 map
    * - 时间复杂度 O(n)，空间复杂度 O(n)。
    * */
    public static boolean isAnagram3(String s, String t) {
        if (t.length() != s.length())
            return false;

        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {  // O(n)，因为之前已经处理过长度不等的情况，因此这里可以一次遍历处理两个字符串
            char sc = s.charAt(i);
            map.put(sc, map.getOrDefault(sc, 0) + 1);  // 一个加
            char tc = t.charAt(i);
            map.put(tc, map.getOrDefault(tc, 0) - 1);  // 一个减
        }

        for (int n : map.values())  // O(n)，这里需要遍历 map
            if (n != 0)
                return false;
        return true;
    }

    /*
    * 解法4：思路与解法3一致，只是使用数组作为字典
    * - 时间复杂度 O(n)，空间复杂度 O(len(charset))。
    * */
    public static boolean isAnagram4(String s, String t) {
        if (t.length() != s.length())
            return false;

        int[] freq = new int[128];  // 使用数组比使用 map 开销小很多
        for (int i = 0; i < s.length(); i++) {
            freq[s.charAt(i)]++;
            freq[t.charAt(i)]--;
        }
        for (int n : freq)
            if (n != 0)
                return false;
        return true;
    }

    public static void main(String[] args) {
        log(isAnagram4("anagram", "nagaram"));  // expects true
        log(isAnagram4("rat", "car"));          // expects false
        log(isAnagram4("abcd", "abc"));         // expects false
        log(isAnagram4("aacc", "ccac"));        // expects false

        log(isAnagram2("anagram", "nagaram"));  // expects true
        log(isAnagram2("rat", "car"));          // expects false
        log(isAnagram2("abcd", "abc"));         // expects false
        log(isAnagram2("aacc", "ccac"));        // expects false
    }
}