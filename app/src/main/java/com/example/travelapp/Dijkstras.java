package com.example.travelapp;

import com.example.travelapp.BinaryMinHeap;
import com.example.travelapp.fragments.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Dijkstras {

    /**
     * Computes the shortest path between two nodes in a weighted graph.
     * Input graph is guaranteed to be valid and have no negative-weighted edges.
     */

    public static List<Integer> getShortestPath(Graph g, int src, int tgt) {
        List<Integer> l = new ArrayList<Integer>();
        if (src == tgt) {
            l.add(src);
            return l;
        }
        int size = g.getSize();
        int[] dist = new int[size];
        Integer[] parent = new Integer[size];
        for (int i = 0; i < size; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        dist[src] = 0;
        Set<Integer> s = new HashSet<Integer>();
        BinaryMinHeap<Integer, Integer> h = new BinaryMinHeap<Integer, Integer>();
        for (int i = 0; i < size; i++) {
            h.add(dist[i], i);
        }
        while (h.size() != 0) {
            int u = h.extractMin().getValue();
            s.add(u);
            for (int v : g.outNeighbors(u)) {
                if (dist[v] == Integer.MAX_VALUE || dist[v] > dist[u] + g.getWeight(u, v)) {
                    dist[v] = dist[u] + g.getWeight(u, v);
                    if (h.containsValue(v)) {
                        h.decreaseKey(v, dist[v]);
                    }
                    parent[v] = u;

                }
            }
        }
        if (parent[tgt] == null) {
            return l;
        }
        if (parent[tgt] == src) {
            l.add(src);
            l.add(tgt);
            return l;
        }
        int curr = tgt;
        while (parent[curr] != null && parent[curr] != src) {
            l.add(curr);
            curr = parent[curr];
            if (parent[curr] == null) {
                return new ArrayList<Integer>();
            }
        }
        l.add(curr);
        l.add(src);
        Collections.reverse(l);
        return l;
    }

}
