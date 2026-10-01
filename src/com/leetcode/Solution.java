package com.leetcode;

import java.util.HashMap;
import java.util.Map;

// LRU缓存类
class LRUCache {
    // 双向链表节点内部类，用来保存key-value以及前后节点引用
    static class Node{
        int key;    // 缓存键，淘汰节点时需要用key去删除HashMap中的记录，不能只存value
        int value;  // 缓存值
        Node prev;  // 前驱节点引用
        Node next;  // 后继节点引用
        // 节点构造方法
        public Node(int k,int v){
            key = k;
            value = v;
        }
    }

    private final Map<Integer,Node> map;   // 哈希表：key -> 双向链表节点，实现O(1)查找节点
    private final Node dummyHead;          // 虚拟头哨兵节点，不存真实数据，简化链表边界判断
    private final Node dummyTail;          // 虚拟尾哨兵节点，不存真实数据，简化链表边界判断
    private final int capacity;            // 缓存最大容量
    private int size;                // 当前缓存里实际存放的元素数量

    // 构造函数：初始化LRU缓存，传入最大容量
    public LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        dummyHead = new Node(-1, -1);
        dummyTail = new Node(-1, -1);
        // 初始化哨兵节点，头尾相连，空链表状态
        dummyHead.next = dummyTail;
        dummyTail.prev = dummyHead;
        size = 0;
    }

    /**
     * 辅助方法：将指定节点从双向链表中摘除
     * @param node 需要移除的节点
     */
    private void removeNode(Node node){
        // 当前节点的前驱，直接连到当前节点的后继
        node.prev.next = node.next;
        // 当前节点的后继，直接连到当前节点的前驱
        node.next.prev = node.prev;
    }

    /**
     * 辅助方法：把节点添加到链表头部（哨兵头的后面，代表最近使用）
     * @param node 要加入头部的节点
     */
    private void addToHead(Node node){
        // 新节点的next指向原来dummyHead后面的第一个真实节点
        node.next = dummyHead.next;
        // 新节点的prev指向虚拟头
        node.prev = dummyHead;
        // 原来第一个真实节点的前驱指向新节点
        dummyHead.next.prev = node;
        // 虚拟头的next指向新节点
        dummyHead.next = node;
    }

    /**
     * 辅助方法：将已经存在的节点移动到链表头部
     * 逻辑：先摘下来，再放到头部
     * @param node 需要移动的节点
     */
    private void moveToHead(Node node){
        removeNode(node);
        addToHead(node);
    }

    /**
     * 辅助方法：删除链表尾部的真实节点（dummyTail前面那个）
     * 这个节点就是最久未使用，缓存满时淘汰
     * @return 返回被删除的节点，用来在map中删除对应的key
     */
    private Node removeTail(){
        Node tailNode = dummyTail.prev; // 获取尾节点（真实数据）
        removeNode(tailNode);
        return tailNode;
    }

    /**
     * get查询方法
     * @param key 查询的键
     * @return 存在返回value，不存在返回-1
     */
    public int get(int key) {
        // 哈希表找不到key，直接返回-1
        if(!map.containsKey(key)){
            return -1;
        }
        // 找到对应节点
        Node node = map.get(key);
        // 访问过该元素，标记为最近使用，移动到链表头部
        moveToHead(node);
        return node.value;
    }

    /**
     * put新增/更新缓存方法
     * @param key 键
     * @param value 值
     */
    public void put(int key, int value) {
        // 情况1：key已经存在，更新value，移到头部
        if(map.containsKey(key)){
            Node node = map.get(key); // 用key获取节点，map.get(key)返回的是链表节点对象
            node.value = value; // 更新值
            moveToHead(node);   // 更新后，标记为最近使用
        }else{
            // 情况2：key不存在，新建节点
            Node newNode = new Node(key, value);
            map.put(key,newNode); // 哈希表存入映射
            addToHead(newNode);   // 放到链表头部
            size++;               // 当前缓存数量+1

            // 如果当前元素超过最大容量，执行淘汰
            if(size > capacity){
                Node delNode = removeTail(); // 删除最久未使用的尾部节点
                map.remove(delNode.key);      // 哈希表同步删除！！面试高频易错点
                size--;                       // 数量减1
            }
        }
    }
}