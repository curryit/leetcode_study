package com.leetcode;

public class text {
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);//容量为2
        cache.put(1,1);
        cache.put(2,2);
        System.out.println(cache.get(1)); //预期输出1
        cache.put(3,3); //容量满，淘汰key=2
        System.out.println(cache.get(2)); //预期输出-1
        cache.put(4,4); //淘汰key=1
        System.out.println(cache.get(1)); //-1
        System.out.println(cache.get(3)); //3
        System.out.println(cache.get(4)); //4
    }
}