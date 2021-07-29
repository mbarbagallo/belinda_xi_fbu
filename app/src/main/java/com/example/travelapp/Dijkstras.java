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
    public static Graph createGraph(List<String> ids, Itinerary itinerary, boolean visitAll) throws InterruptedException {
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
                // check max distance - must check both distance currentDistanceIJ and
                // currentDistanceJI since the length of the route from i -> j isn't always the
                // same as the length of the route from j -> i.
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
                if (!visitAll) {
                    if ((i == 0 && j == 1) || (i == 1 && j == 0)) {
                        continue;
                    }
                }
                // directions from i to j
                int currentDistanceIJ =
                        (int) itinerary.getDistance(ids.get(i), ids.get(j), mGeoApiContext).inMeters;
                // normalize weight of distance by dividing it by maxDistance so that it is <= 1
                double distanceWeightIJ = (double) currentDistanceIJ / maxDistance;
                double preferenceWeight = 0.0;
                if (!visitAll) {
                    // normalize weight of preference by dividing it by twice the size since the max
                    // that i + j can be is approximately ids.size() * 2.0.
                    preferenceWeight = (double) (i + j) / (ids.size() * 2.0);
                }
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
        List<Integer> list = new ArrayList<>();
        // hard coded base case of size 1
        if (src == tgt) {
            list.add(src);
            return list;
        }
        // hard coded base case of size = 2 since no edge between 0 and 1 is added in the graph.
        if (graph.getSize() == 2) {
            list.add(src);
            list.add(tgt);
            return list;
        }
        int size = graph.getSize();
        // dist array is a double because when creating the graph, normalizing the weights makes it
        // so that many weights are less than 1, so using int[] would make the weights round to 0
        // and cause issues, while using a double would resolve these.
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

    public static ArrayList<Integer> findShortestPathAllVertices(Graph graph) {
        int graphSize = graph.getSize();
        ArrayList<Integer> shortestPath = new ArrayList<Integer>();

        // array of numbers corresponding to vertices of Graph - from 1 to graphSize - 1
        // excludes 0 since we always start and come back to node 0 at the end.
        int[] numbers = new int[graphSize-1];
        for (int i = 1; i < graphSize; i++) {
            shortestPath.add(0);
            numbers[i - 1] = i;
        }

        int minDistance = Integer.MAX_VALUE;
        // list containing all different possible paths - every possible permutation of nodes
        ArrayList<ArrayList<Integer>> paths = permute(numbers);

        for (ArrayList<Integer> path : paths) {
            int currDistance = 0;
            // initially set to 0 since we start the path at node 0
            int nodeNumber = 0;
            // compute current path weight
            for (int i = 0; i < path.size(); i++) {
                currDistance += graph.getWeight(nodeNumber, path.get(i));
                nodeNumber = path.get(i);
            }
            // adding the edge to complete the loop and go back to node 0
            currDistance += graph.getWeight(nodeNumber, 0);
            // updates minDistance if we find a new smaller minimum
            if (minDistance > currDistance) {
                Collections.copy(shortestPath, path);
                minDistance = currDistance;
            }
        }

        // Add the start node, 0, to the beginning and the end of the path
        shortestPath.add(0, 0);
        shortestPath.add(0);

        return shortestPath;
    }

    public static ArrayList<ArrayList<Integer>> permute(int[] numbers) {
        ArrayList<ArrayList<Integer>> permutations = new ArrayList<ArrayList<Integer>>();

        // start with adding empty list
        permutations.add(new ArrayList<Integer>());

        // For each iteration, a new number will be added to build the list representing the permutation
        for (int i = 0; i < numbers.length; i++) {
            // List of list to be built in current iteration
            ArrayList<ArrayList<Integer>> currList = new ArrayList<ArrayList<Integer>>();

            // for each ArrayList in permutations, create new ArrayLists that add the new number to each
            // possible location to add a number to in the list (size + 1 total slots)
            for (ArrayList<Integer> numList : permutations) {
                for (int j = 0; j < numList.size() + 1; j++) {

                    // create new ArrayList temp, insert current i to create new permutation, and
                    // add this list to result
                    ArrayList<Integer> temp = new ArrayList<Integer>(numList);
                    temp.add(j, numbers[i]);
                    currList.add(temp);
                }
            }
            permutations = new ArrayList<ArrayList<Integer>>(currList);
        }
        return permutations;
    }

}
