package com.example.travelapp.fragments;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Graph {

    HashMap<Integer, Integer>[] arr;

    public Graph(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        arr = (HashMap<Integer, Integer>[]) new HashMap[n];
        for (int i = 0; i < n; i ++) {
            arr[i] = new HashMap<Integer, Integer>();
        }
    }

//    Returns the number of vertices in the graph.

    public int getSize() {
        return arr.length;
    }

//    Determines if there's an directed edge from u to v.

    public boolean hasEdge(int u, int v) {
        int len = arr.length;
        if (u >= len || v >= len || u < 0 || v < 0) {
            throw new IllegalArgumentException();
        }
        return arr[u].containsKey(v);
    }

//    Returns the weight of an the directed edge u-v.

    public int getWeight(int u, int v) {
        int len = arr.length;
        if (u >= len || v >= len || u < 0 || v < 0) {
            throw new IllegalArgumentException();
        }
        if (! this.hasEdge(u, v)) {
            throw new NoSuchElementException();
        }
        return arr[u].get(v);
    }

//     Creates an edge u-v if it does not already exist. Does not modify the edge weight
//     if u-v already exists.
    public boolean addEdge(int u, int v, int weight) {
        int len = arr.length;
        if (u == v || u >= len || v  >= len || u < 0 || v < 0) {
            throw new IllegalArgumentException();
        }
        if (arr[u].containsKey(v)) {
            return false;
        }
        arr[u].put(v, weight);
        return true;
    }

    // Returns the out-neighbors of the specified vertex.

    public Set<Integer> outNeighbors(int v) {
        int len = arr.length;
        if (v < 0 || v >= len) {
            throw new IllegalArgumentException();
        }
        Set<Integer> out = new HashSet<Integer>();
        for (Map.Entry<Integer, Integer> e : arr[v].entrySet()) {
            out.add(e.getKey());
        }
        return out;
    }
}