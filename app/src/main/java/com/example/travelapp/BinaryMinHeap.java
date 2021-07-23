package com.example.travelapp;

import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * BinaryMinHeap class. Implemented with an ArrayList that can be
 * viewed as a nearly complete binary tree, where every node in the
 * tree corresponds to an element in the array. Taken and adapted
 * from a Penn homework assignment.
 * @param <Key> - The key used to determine priority in the heap.
 *             Does not have to be distinct.
 * @param <V> - The value of what we are adding to the heap. In
 *           Dijkstras, this would correspond to the node number.
 */

public class BinaryMinHeap<Key extends Comparable<Key>, V> {

    // arrayList can be viewed as a nearly complete binary tree
    private ArrayList<Entry<Key, V>> arrayList;
    // indices is a HashMap used for easier lookup of where values
    // are in the array
    private HashMap<V, Integer> indices;

    public BinaryMinHeap() {
        arrayList = new ArrayList<Entry<Key, V>>();
        indices = new HashMap<V, Integer>();
    }

    public int size() {
        return arrayList.size();
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

        arrayList.add(new Entry<Key, V>(key, value));
        // set index to where we just added the Entry (at the end = index size - 1 of the arrayList)
        int index = arrayList.size() - 1;
        // calculates the index of the parent of the current node, given the current node's index
        Function<Integer, Integer> calculateParentIndex = (n) -> { return (n + 1) / 2 - 1; };
        int parent = calculateParentIndex.apply(index);

        indices.put(value, index);
        placeElementAtIndex(index, parent);

    }

    /*
        placeElementAtIndex is a helper method that places the element in the passed in index where
        it should belong in the heap. called by decreaseKey and add.
     */

    private int placeElementAtIndex(int index, int parent) {
        if (arrayList.size() == 1) {
            return 0;
        }
        while (index > 0 && arrayList.get(parent).key.compareTo(arrayList.get(index).key) > 0) {
            //exchange the entry at parent and entry at index in arrayList and in indices
            Entry<Key, V> parentKey = arrayList.get(parent);
            Entry<Key, V> indexKey = arrayList.get(index);
            arrayList.set(parent, indexKey);
            arrayList.set(index, parentKey);
            indices.replace(indexKey.value, parent);
            indices.replace(parentKey.value, index);
            // updates the index value
            index = parent;
            // calculates the new index of the parent of the current node,
            // given the current node's index
            Function<Integer, Integer> calculateParentIndex = (n) -> { return (n + 1) / 2 - 1; };
            parent = calculateParentIndex.apply(index);
        }
        return index;
    }

    public void decreaseKey(V value, Key newKey) {
        if (!this.containsValue(value)) {
            throw new NoSuchElementException("value is not in the heap");
        }
        int index = indices.get(value);
        if (index >= arrayList.size()) {
            throw new IllegalArgumentException();
        }
        Key currKey = arrayList.get(index).key;
        if (newKey == null) {
            throw new IllegalArgumentException("newKey is null");
        }
        if (newKey.compareTo(currKey) > 0) {
            throw new IllegalArgumentException("newKey > key(value)");
        }

        // update arraylist
        arrayList.set(index, new Entry<Key, V>(newKey, value));
        // calculates the new index of the parent of the current node,
        // given the current node's index
        Function<Integer, Integer> calculateParentIndex = (n) -> { return (n + 1) / 2 - 1; };
        int parent = calculateParentIndex.apply(index);
        // call helper function to place newKey in the right place
        int finIndex = placeElementAtIndex(index, parent);
        // update indices hashmap
        indices.remove(value);
        indices.put(value, finIndex);
    }

    public Entry<Key, V> extractMin() {
        if (arrayList.size() == 0) {
            throw new NoSuchElementException("the min-heap is empty");
        }
        Entry<Key, V> min = arrayList.get(0);
        int len = arrayList.size();
        Entry<Key, V> lastEntry = arrayList.get(len - 1);
        // swap the minimum Entry and last Entry
        arrayList.set(0, lastEntry);
        arrayList.set(len - 1, min);
        // remove minimum element
        arrayList.remove(len - 1);
        indices.remove(min.value);
        indices.put(lastEntry.value, 0);
        // call minHeapify to maintain heap property
        minHeapify(0);
        return min;
    }

    private void minHeapify(int index) {
        // calculate left and right children of the node at index, given that we are viewing the
        // array as a nearly complete binary tree.
        int left = 2 * (index + 1) - 1;
        int right = 2 * (index + 1);
        // find which element is the minimum.
        int minimum = left;
        if (left < arrayList.size() && arrayList.get(left).key.compareTo(arrayList.get(index).key) < 0) {
            minimum = left;
        } else {
            minimum = index;
        }
        if (right < arrayList.size() && arrayList.get(right).key.compareTo(arrayList.get(minimum).key) < 0) {
            minimum = right;
        }
        if (minimum != index) {
            // exchange minimum of the three elements with the element at index
            Entry<Key, V> minimumKey = arrayList.get(minimum);
            Entry<Key, V> indexKey = arrayList.get(index);
            arrayList.set(minimum, indexKey);
            arrayList.set(index, minimumKey);
            indices.replace(indexKey.value, minimum);
            indices.replace(minimumKey.value, index);
            // recursively calling minHeapify - traversing down the heap and maintaining the heap property
            minHeapify(minimum);
        }
    }

}
