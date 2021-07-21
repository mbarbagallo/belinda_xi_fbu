package com.example.travelapp;

import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class BinaryMinHeap<Key extends Comparable<Key>, V> {
    private ArrayList<Entry<Key, V>> a;
    private HashMap<V, Integer> indices;

    public BinaryMinHeap() {
        a = new ArrayList<Entry<Key, V>>();
        indices = new HashMap<V, Integer>();
    }

    public int size() {
        return a.size();
    }

    public boolean containsValue(V value) {
        return indices.containsKey(value);
    }

    public void add(Key key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null!");
        }
        if (indices.containsKey(value)) {
            throw new IllegalArgumentException("value is already in the min-heap!");
        }

        a.add(new Entry<Key, V>(key, value));
        int index = a.size() - 1;
        int parent = (index + 1) / 2 - 1;

        indices.put(value, index);
        helperPlace(index, parent);

    }
    // create helper function that both decreaseKey and add can call

    private int helperPlace(int index, int parent) {
        int finIndex = index;
        if (a.size() == 1) {
            return 0;
        }
        if (index > -1 && parent > -1) {
            while (index > 0 && a.get(parent).key.compareTo(a.get(index).key) > 0) {
                //exchange a parent and a i
                Entry<Key, V> parentKey = a.get(parent);
                Entry<Key, V> indexKey = a.get(index);
                a.set(parent, indexKey);
                a.set(index, parentKey);
                indices.replace(indexKey.value, parent);
                indices.replace(parentKey.value, index);
                // update index and parent values
                index = parent;
                parent = (parent + 1) / 2 - 1;
                finIndex = index;
            }
        }
        return finIndex;
    }

    public void decreaseKey(V value, Key newKey) {
        if (!this.containsValue(value)) {
            throw new NoSuchElementException("value is not in the heap");
        }
        int currIndex = indices.get(value);
        if (currIndex >= a.size()) {
            throw new IllegalArgumentException();
        }
        Key currKey = a.get(currIndex).key;
        if (newKey == null) {
            throw new IllegalArgumentException("newKey is null");
        }
        if (newKey.compareTo(currKey) > 0) {
            throw new IllegalArgumentException("newKey > key(value)");
        }
        int index = indices.get(value);

        // update arraylist
        a.set(index, new Entry<Key, V>(newKey, value));
        int parent = (index + 1) / 2 - 1;
        // call helper function to place newKey in the right place
        int finIndex = helperPlace(index, parent);
        // update indices hashmap
        indices.remove(value);
        indices.put(value, finIndex);
    }

    public Entry<Key, V> extractMin() {
        if (a.size() == 0) {
            throw new NoSuchElementException("the min-heap is empty");
        }
        Entry<Key, V> min = a.get(0);
        int len = a.size();
        Entry<Key, V> e = a.get(len - 1);
        a.set(0, e);
        a.set(len - 1, min);
        a.remove(len - 1);
        indices.remove(min.value);
        indices.put(e.value, 0);
        minHeapify(0);
        return min;
    }

    private void minHeapify(int index) {
        int left = 2 * (index + 1) - 1;
        int right = 2 * (index + 1);
        int minimum = left;
        if (left < a.size() && a.get(left).key.compareTo(a.get(index).key) < 0) {
            minimum = left;
        } else {
            minimum = index;
        }
        if (right < a.size() && a.get(right).key.compareTo(a.get(minimum).key) < 0) {
            minimum = right;
        }
        if (minimum != index) {
            // exchange a(i) and a(largest)
            Entry<Key, V> minimumKey = a.get(minimum);
            Entry<Key, V> indexKey = a.get(index);
            a.set(minimum, indexKey);
            a.set(index, minimumKey);
            indices.replace(indexKey.value, minimum);
            indices.replace(minimumKey.value, index);
            minHeapify(minimum);
        }
    }

}
