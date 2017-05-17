package com.vmware.hackerrank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution
{

    public static void main(String[] args) {
        // int cost = minimalCost(8, new String[] {"8 1", "5 8", "7 3", "8 6"});

        // int cost = minimalCost(4, new String[] {"1 2", "1 4"});

        // int cost = minimalCost(4, new String[] {"1 2", "2 3", "3 4"});
        int cost = minimalCost(15, new String[] {"1 2", "2 3", "5 6", "6 3", "7 8", "8 3"});

        System.out.println(cost);
    }

    static int minimalCost(int n, String[] pairs) {
        List<FusedRods> fusedRods = new ArrayList<>();

        Set<String> fusedList = new HashSet<>();

        for (String pair : pairs) {
            // find in the fusedRods list
            boolean alreadyFused = false;
            for (FusedRods rods : fusedRods) {
                if (rods.contains(pair)) {
                    rods.add(pair);
                    alreadyFused = true;
                    break;
                }
            }
            if (!alreadyFused) {
                FusedRods rods = new FusedRods(pair);
                fusedRods.add(rods);
            }
            fusedList.addAll(Arrays.asList(pair.split(" ")));
        }

        int cost = 0;
        for (int i = 1; i <= n; i++) {
            if (!fusedList.contains(String.valueOf(i))) {
                cost += 1;
            }
        }

        return cost;

    }

    static class FusedRods {

        Set<String> fused = new HashSet<>();

        public FusedRods(String pair) {
            fused.addAll(Arrays.asList(pair.split(" ")));
        }

        public void add(String pair) {
            fused.addAll(Arrays.asList(pair.split(" ")));
        }

        public boolean contains(String pair) {
            List<String> rodNums = Arrays.asList(pair.split(" "));
            return fused.contains(rodNums.get(0)) || fused.contains(rodNums.get(1));
        }

        @Override
        public String toString() {
            return fused.toString();
        }

    }
}
