package StackAndQueue.BFSAndGraphShortestPath;

import java.util.*;
import java.util.function.Function;

import static Utils.Helpers.log;

/*
* Word Ladder II
*
* - 题目与 L127 基本一致，区别在于返回结果得是所有的最短路径。
* */

public class L126_WordLadderII {
    /*
    * 方法1：构建邻接表 + BFS + 回溯
    * - 思路：因为要找到所有最短路径，因此不能在 BFS 中记录步数直接返回，而是：
    *   1. 需要在 BFS 中计算从起点到每个顶点的最少步数，保存在 steps 中。
    *   2. 再根据 steps 进行回溯查找，找到所有最短路径。回溯的逻辑是：
    *      a. 从 beginWord 出发，根据 steps 中的信息不断查找最短路径上的下一个相邻顶点，直到到达 endWord，并一路上记录下该路径所经顶点，得到路径。
    *      b. 在查找最短路径上的下一个相邻顶点时，若遇到路径分叉（存在多条最短路径），则每条都要尝试一遍。
    * - 时间复杂度 O(n^2)，空间复杂度 O(n)。
    * */
    public static List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> res = new ArrayList<>();
        if (!wordList.contains(endWord)) return res;
        if (!wordList.contains(beginWord)) wordList.add(beginWord);

        int n = wordList.size();
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {  // 构建邻接表（本题中使用邻接矩阵会超时，邻接表可以通过）
            graph.add(new ArrayList<>());
            for (int j = 0; j < n; j++)  // 技巧（这里无法使用）：若写作 j=i+1 的话可以避免遍历相同的 i,j 组合（如 i=0,j=1 和 i=1,j=0），从而加快遍历速度
                if (isSimilar(wordList.get(i), wordList.get(j)))
                    graph.get(i).add(j);
        }

        int beginIndex = wordList.indexOf(beginWord);
        int endIndex = wordList.indexOf(endWord);

        Map<Integer, Integer> steps = new HashMap<>();  // 保存 { 顶点: 起点到该顶点的最少步数 }
        bfs(graph, beginIndex, steps);                  // 通过 BFS 计算 steps（即起点到每个顶点的最少步数）

        List<Integer> indexPath = new ArrayList<>();    // 用于回溯，存储最短路径上每个顶点的 index
        indexPath.add(beginIndex);
        getRes(graph, beginIndex, endIndex, wordList, steps, indexPath, res);  // 根据 steps 回溯，找到所有最短路径

        return res;
    }

    private static void bfs(List<List<Integer>> graph, int beginIndex, Map<Integer, Integer> steps) {
        Queue<Integer> q = new LinkedList<>();
        q.offer(beginIndex);
        steps.put(beginIndex, 0);

        while (!q.isEmpty()) {
            int currIndex = q.poll();
            for (int adjIndex : graph.get(currIndex)) {  // 遍历所有相邻节点的 index
                if (!steps.containsKey(adjIndex)) {  // 若 adjIndex 在 steps 中已存在，则说明在之前已经赋过值了，即有更短的路径到达，因此不能再覆盖
                    steps.put(adjIndex, steps.get(currIndex) + 1);
                    q.offer(adjIndex);
                }
            }
        }
    }

    private static void getRes(List<List<Integer>> graph, int currIndex, int endIndex, List<String> wordList, Map<Integer, Integer> steps, List<Integer> indexPath, List<List<String>> res) {
        if (!indexPath.isEmpty() && indexPath.get(indexPath.size() - 1) == endIndex) {  // 检查是否到达 endWord
            res.add(getPath(indexPath, wordList));  // 若到达则说明 indexPath 中的索引组成了一条最短路径，将对应的 word path 添加到 res 中
            return;
        }
        for (int adjIndex : graph.get(currIndex)) {
            if (steps.get(adjIndex) == steps.get(currIndex) + 1) {  // 检查 adjIndex 所指顶点是否是最短路径上的下一个顶点
                indexPath.add(adjIndex);
                getRes(graph, adjIndex, endIndex, wordList, steps, indexPath, res);  // 递归查找下一个 adjIndex
                indexPath.remove(indexPath.size() - 1);  // 递归结束后将 adjIndex 移除，放入下一个再继续查找
            }
        }
    }

    private static List<String> getPath(List<Integer> tres, List<String> wordList) {
        List<String> path = new ArrayList<>();
        for (int index : tres)
            path.add(wordList.get(index));
        return path;
    }

    private static boolean isSimilar(String w1, String w2) {
        if (w1.length() != w2.length() || w1.equals(w2)) return false;
        int diffCount = 0;
        for (int i = 0; i < w1.length(); i++) {
            if (w1.charAt(i) != w2.charAt(i)) diffCount++;
            if (diffCount > 1) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        List<String> wordList = new ArrayList<>(Arrays.asList("hot", "dot", "dog", "lot", "log", "cog"));
        log(findLadders("hit", "cog", wordList));
        // expects [["hit","hot","dot","dog","cog"], ["hit","hot","lot","log","cog"]]

        List<String> wordList2 = new ArrayList<>(Arrays.asList("a", "b", "c"));
        log(findLadders("a", "c", wordList2));
        // expects [["a","c"]]

        List<String> wordList3 = new ArrayList<>(Arrays.asList("ted", "tex", "red", "tax", "tad", "den", "rex", "pee"));
        log(findLadders("red", "tax", wordList3));
        // expects [["red","ted","tad","tax"], [red, ted, tad, tax], [red, rex, tex, tax]]

        List<String> wordList4 = new ArrayList<>(Arrays.asList("hot", "dot", "dog", "lot", "log"));
        log(findLadders("hit", "cog", wordList4));
        // expects []

        List<String> wordList5 = new ArrayList<>(Arrays.asList("hot", "dog"));
        log(findLadders("hot", "dog", wordList5));
        // expects []

        List<String> wordList6 = new ArrayList<>(Arrays.asList("lest", "leet", "lose", "code", "lode", "robe", "lost"));
        log(findLadders("leet", "code", wordList6));
        // expects [["leet","lest","lost","lose","lode","code"]]

        List<String> wordList7 = new ArrayList<>(Arrays.asList("miss", "dusk", "kiss", "musk", "tusk", "diss", "disk", "sang", "ties", "muss"));
        log(findLadders("kiss", "tusk", wordList7));
        // expects [[kiss, miss, muss, musk, tusk], [kiss, diss, disk, dusk, tusk]]
    }
}