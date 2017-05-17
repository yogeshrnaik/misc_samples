package com.tomtom.flexiclaim;

// Problem: Following is the current implementation of the Flexi-claim system.
// 1) Find if there are any issues in the existing code. if yes, how to fix them?
// 2) The new requirement is to add a new flexi-claim type called 'Clothing Allowance'
// Re-factor the code so that it is clean and if you add new flexi-claim types,
// we should be able to do it by adding new code rather than modifying existing one.
// 3) How to Unit test this class?

// ----------------- Expected Output ---------------------
// Flexi Claim Fri Jun 24 08:55:51 IST 2016
//
// Medical 1000
// Medical 5001 X
// Food Coupons 2000
// Fuel 3000
// Fuel 4000
// Food Coupons 2001 X
// Total Medical claim : 6001
// Total Food Coupons claim : 4001
// Total Fuel claim : 7000
// Grand Total Flexi claim : 17002
// ----------------- Expected Output ---------------------

import static com.tomtom.flexiclaim.FlexiClaim.Type.FOOD_COUPONS;
import static com.tomtom.flexiclaim.FlexiClaim.Type.FUEL;
import static com.tomtom.flexiclaim.FlexiClaim.Type.MEDICAL;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
// Problem: Following is the current implementation of the Flexi-claim system.
// 1) Find if there are any issues in the existing code. if yes, how to fix them?
// 2) New requirement is to add a new flexi-claim type called 'Clothing Allowance'
// Re-factor the code so that it is clean and if you add new flexi-claim types,
// we should be able to do it by adding new code rather than modifying existing one.
// 3) How to Unit test this class?

class FlexiClaim {

    public enum Type {
        MEDICAL, FOOD_COUPONS, FUEL
    }

    public Type type;
    public int amount;

    public FlexiClaim(Type type, int amount) {
        this.type = type;
        this.amount = amount;
    }
}

public class FlexiClaimExercise {

    public void printReport(List<FlexiClaim> claims) {
        int total = 0;
        int medicalTotal = 0;
        int foodCouponsTotal = 0;
        int fuelTotal = 0;
        System.out.println("Flexi Claim " + new Date() + "\n");
        for (FlexiClaim claim : claims) {
            String claimName = "";
            switch (claim.type) {
                case MEDICAL:
                    claimName = "Medical";
                    medicalTotal += claim.amount;
                    break;
                case FOOD_COUPONS:
                    claimName = "Food Coupons";
                    foodCouponsTotal += claim.amount;
                    break;
                case FUEL:
                    claimName = "Fuel";
                    fuelTotal += claim.amount;
                    break;
            }
            String overClaimMarker = claim.type == MEDICAL && claim.amount > 5000
                || claim.type == FOOD_COUPONS && claim.amount > 2000 ? "X" : " ";
            System.out.println(claimName + "\t" + claim.amount + "\t" + overClaimMarker);
            total += claim.amount;
        }

        // Print Totals
        System.out.println("Total Medical claim : " + medicalTotal);
        System.out.println("Total Food Coupons claim : " + foodCouponsTotal);
        System.out.println("Total Fuel claim : " + fuelTotal);
        System.out.println("Grand Total Flexi claim : " + total);
    }

    public static void main(String[] args) {
        List<FlexiClaim> claims = Arrays.asList(new FlexiClaim(MEDICAL, 1000),
            new FlexiClaim(MEDICAL, 5001), new FlexiClaim(FOOD_COUPONS, 2000),
            new FlexiClaim(FUEL, 3000), new FlexiClaim(FUEL, 4000),
            new FlexiClaim(FOOD_COUPONS, 2001));
        new FlexiClaimExercise().printReport(claims);
    }
}