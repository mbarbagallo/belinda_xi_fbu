package com.example.travelapp;

import android.util.Log;

import com.example.travelapp.BinaryMinHeap;
import com.example.travelapp.fragments.Graph;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.Distance;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.travelapp.fragments.ComposeFragment.API_KEY;

public class Dijkstras {

    private static final String TAG = "Dijkstras";

    // TODO - check max number of google maps api calls
    public static Graph createGraph(List<String> ids, Itinerary itinerary) throws InterruptedException {
        GeoApiContext mGeoApiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
        int totalDistance = 0;
        Graph graph = new Graph(ids.size());
        // calculating totalDistance
        for (int i = 0; i < ids.size(); i++) {
            for (int j = i + 1; j < ids.size(); j++) {
                // get distance of directions from i to j in meters and add to total distance
                int currentDistanceIJ =
                        (int) itinerary.getDistance(ids.get(i), ids.get(j), mGeoApiContext).inMeters;
                totalDistance += currentDistanceIJ;
                // get distance of directions from j to i in meters and add to total distance
                int currentDistanceJI =
                        (int) itinerary.getDistance(ids.get(j), ids.get(i), mGeoApiContext).inMeters;
                totalDistance += currentDistanceJI;
            }
        }
        int interval = totalDistance / (ids.size() * 2);
        for (int i = 0; i < ids.size(); i++) {
            for (int j = i + 1; j < ids.size(); j++) {
                int factor = i + j;
                // directions from i to j
                int currentDistanceIJ =
                        (int) itinerary.getDistance(ids.get(i), ids.get(j), mGeoApiContext).inMeters;
                graph.addEdge(i, j, currentDistanceIJ + factor * interval);
                // directions from j to i
                int currentDistanceJI =
                        (int) itinerary.getDistance(ids.get(j), ids.get(i), mGeoApiContext).inMeters;
                graph.addEdge(j, i, currentDistanceJI + factor * interval);
            }
        }
        return graph;
    }
    
    /**
     * Computes the shortest path between two nodes in a weighted graph.
     * Input graph is guaranteed to be valid and have no negative-weighted edges.
     */

    public static List<Integer> getShortestPath(Graph graph, int src, int tgt) {
        List<Integer> list = new ArrayList<Integer>();
        if (src == tgt) {
            list.add(src);
            return list;
        }
        int size = graph.getSize();
        int[] dist = new int[size];
        // array mapping indices to the Integer representing the node that's the parent of the index
        Integer[] parent = new Integer[size];
        // initially setting all the distances of nodes to be the max value
        for (int i = 0; i < size; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        dist[src] = 0;
        BinaryMinHeap<Integer, Integer> binaryMinHeap = new BinaryMinHeap<Integer, Integer>();
        for (int i = 0; i < size; i++) {
            binaryMinHeap.add(dist[i], i);
        }
        while (binaryMinHeap.size() != 0) {
            int u = binaryMinHeap.extractMin().getValue();
            for (int v : graph.outNeighbors(u)) {
                // edge relaxation
                if (dist[v] == Integer.MAX_VALUE || dist[v] > dist[u] + graph.getWeight(u, v)) {
                    dist[v] = dist[u] + graph.getWeight(u, v);
                    if (binaryMinHeap.containsValue(v)) {
                        // decrease key
                        binaryMinHeap.decreaseKey(v, dist[v]);
                    }
                    parent[v] = u;
                }
            }
        }
        // if src never reaches tgt
        if (parent[tgt] == null) {
            return list;
        }
        // if the path is only from src -> tgt
        if (parent[tgt] == src) {
            list.add(src);
            list.add(tgt);
            return list;
        }
        // following the parent pointer array to get a list of all nodes starting at tgt and
        // traversing up until we reach src and storing
        int curr = tgt;
        while (parent[curr] != null && parent[curr] != src) {
            list.add(curr);
            curr = parent[curr];
            // if we ever continue traversing up the tgt node and hit null and thus can't reach src
            if (parent[curr] == null) {
                return new ArrayList<Integer>();
            }
        }
        list.add(curr);
        list.add(src);
        Collections.reverse(list);
        return list;
    }

}
