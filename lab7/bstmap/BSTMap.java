package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;

    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left;
        private BSTNode right;
        private int size;
        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
        public BSTNode(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(BSTNode node) {
        if (node == null) {
            return 0;
        }
        return node.size;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    private boolean containsKey(BSTNode node, K key) {
        if (node == null) {
            return false;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return containsKey(node.left, key);
        } else if (cmp > 0) {
            return containsKey(node.right, key);
        } else {
            return true;
        }
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTNode node, K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        } else {
            return node.value;
        }
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        root = put(root, key, value);
        return;
    }

    private BSTNode put(BSTNode node, K key, V value) {
        if (node == null) {
            return new BSTNode(key, value, 1);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else  {
            node.value = value;
        }
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(BSTNode node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.print(node.key + " ");
        printInOrder(node.right);
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        keySet(root, keys);
        return keys;
    }

    private void keySet(BSTNode node, Set<K> keys) {
        if (node == null) {
            return;
        }
        keys.add(node.key);
        keySet(node.left, keys);
        keySet(node.right, keys);
    }

    @Override
    public V remove(K key) {
        V res = get(key);
        if (res == null) {
            return res;
        }
        root = remove(root, key);
        return res;
    }

    private BSTNode remove(BSTNode node, K key) {
        if (node == null) {
            return node;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
            node.size = size(node.left) + size(node.right) + 1;
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
            node.size = size(node.left) + size(node.right) + 1;
        } else {
            if (node.left == null
                    && node.right == null) {
                return null;
            } else if (node.left == null) {
                BSTNode temp = node.right;
                node = null;
                return temp;
            } else if (node.right == null) {
                BSTNode temp = node.left;
                node = null;
                return temp;
            } else {
                BSTNode tempLeft = node.left;
                BSTNode tempRight = node.right;
                BSTNode cur = node.right;
                int leftSize = size(node.left);
                while (cur.left != null) {
                    cur.size += leftSize;
                    cur = cur.left;
                }
                cur.size += leftSize;
                cur.left = tempLeft;
                node = null;
                return tempRight;
            }
        }
        return node;
    }

    @Override
    public V remove(K key, V value) {
        V res = get(key);
        if (res == value) {
            root = remove(root, key);
        }
        return res;
    }

    private int getSize(K key) {
        BSTNode temp = getNode(root, key);
        return size(temp);
    }

    private BSTNode getNode(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return getNode(node.left, key);
        } else if (cmp > 0) {
            return getNode(node.right, key);
        } else {
            return node;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }
}
