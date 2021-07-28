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
        int maxDistance = 0;
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
                // check max distance
                if (currentDistanceIJ > maxDistance) {
                    maxDistance = currentDistanceIJ;
                }
                if (currentDistanceJI > maxDistance) {
                    maxDistance = currentDistanceJI;
                }
            }
        }
        for (int i = 0; i < ids.size(); i++) {
            for (int j = i + 1; j < ids.size(); j++) {
                if ((i == 0 && j == 1) || (i == 1 && j == 0)) {
                    continue;
                }
                // directions from i to j
                int currentDistanceIJ =
                        (int) itinerary.getDistance(ids.get(i), ids.get(j), mGeoApiContext).inMeters;
                // normalize weight of distance by dividing it by maxDistance so that it is <= 1
                double distanceWeightIJ = (double) currentDistanceIJ / maxDistance;
                double preferenceWeight = (double) (i + j) / (ids.size() * 2.0);
                graph.addEdge(i, j, distanceWeightIJ + preferenceWeight);
                // directions from j to i
                int currentDistanceJI =
                        (int) itinerary.getDistance(ids.get(j), ids.get(i), mGeoApiContext).inMeters;
                // normalize weight of distance by dividing it by maxDistance so that it is <= 1
                double distanceWeightJI = currentDistanceJI / maxDistance;
                graph.addEdge(j, i, distanceWeightJI + preferenceWeight);
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
        // hard coded base case of size = 2 since no edge between 0 and 1 is added in the graph.
        if (graph.getSize() == 2) {
            list.add(0);
            list.add(1);
            return list;
        }
        if (src == tgt) {
            list.add(src);
            return list;
        }
        int size = graph.getSize();
        double[] dist = new double[size];
        // array mapping indices to the Integer representing the node that's the parent of the index
        Integer[] parent = new Integer[size];
        // initially setting all the distances of nodes to be the max value
        for (int i = 0; i < size; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        dist[src] = 0;
        BinaryMinHeap<Double, Integer> binaryMinHeap = new BinaryMinHeap<Double, Integer>();
        for (int i = 0; i < size; i++) {
            binaryMinHeap.add(dist[i], i);
        }
        while (binaryMinHeap.size() != 0) {
            int u = binaryMinHeap.extractMin().getValue();
            for (int v : graph.outNeighbors(u)) {
                // edge relaxation
                if (dist[v] == Double.MAX_VALUE || dist[v] > dist[u] + graph.getWeight(u, v)) {
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
