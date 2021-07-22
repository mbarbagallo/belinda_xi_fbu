package com.example.travelapp;

import com.example.travelapp.BinaryMinHeap;
import com.example.travelapp.fragments.Graph;
import com.google.maps.GeoApiContext;
import com.google.maps.model.Distance;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.travelapp.fragments.ComposeFragment.API_KEY;

public class Dijkstras {

    // check distance limits
    public static Graph createGraph(List<String> ids, Itinerary itinerary) {
        GeoApiContext mGeoApiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
        int totalDistance = 0;
        Graph graph = new Graph(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            for (int j = i + 1; j < ids.size(); j++) {
                // directions from i to j
                int currentDistanceIJ =
                        (int) itinerary.getDistance(ids.get(i), ids.get(j), mGeoApiContext).inMeters;
                totalDistance += currentDistanceIJ;
                // directions from j to i
                int currentDistanceJI =
                        (int) itinerary.getDistance(ids.get(j), ids.get(i), mGeoApiContext).inMeters;
                totalDistance += currentDistanceJI;
            }
        }
        int interval = totalDistance / (ids.size() * 2);
        for (int i = 0; i < ids.size(); i++) {
            for (int j = 0; j < ids.size(); j++) {
                int factor = i + j;
                // directions from i to j
                int currentDistanceIJ =
                        (int) itinerary.getDistance(ids.get(i), ids.get(j), mGeoApiContext).inMeters;
                totalDistance += currentDistanceIJ;
                graph.addEdge(i, j, factor * interval);
                // directions from j to i
                int currentDistanceJI =
                        (int) itinerary.getDistance(ids.get(j), ids.get(i), mGeoApiContext).inMeters;
                totalDistance += currentDistanceJI;
                graph.addEdge(j, i, factor * interval);
            }
        }
        return graph;
    }
    
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
