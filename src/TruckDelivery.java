import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TruckDelivery {

    public static Town[] route;
    public static ArrayList<Object> data = new ArrayList<>();
//    public static String[] actions = new String[5];


    /**
     * A brute force approach to solving the problem of a truck that follows a linear route and either picks up or drops off
     * a load in order to receive the most profit from the route. This brute force approach uses recursion to evaluate every
     * possible scenario.
     * @param currentTownIdx The current town the truck is visiting along the route
     * @param currentLoadValue The current value of the load the truck is carrying (if any)
     * @param profit The total current profit made alon the route
     * @return The overall maximum profit that can be achieved along the route
     */
    public static int bruteForce(int currentTownIdx, int currentLoadValue, int profit) {

        // The end of the route has been reached, the total profit is returned
        if(currentTownIdx == route.length) {
            return profit;
        }

        int nextTown = currentTownIdx + 1;
        Town currentTown = route[currentTownIdx];
        int dropOffProfit = currentTown.getDropOff() - currentLoadValue;

        // Calculate the profits for the rest of the route trying each drop-off/pick-up option at the current town
        // NOTE: can't drop off if there is no current load (no load value)
        // NOTE: If these is no load the truck can pick up but there will be no profit gain
        // NOTE: The Truck can always skip the current town, neither picking up nor dropping off. Profit will remain the same
        int dropOff = currentLoadValue != 0 ? bruteForce(nextTown, 0, profit + dropOffProfit) : 0;
        int pickUp = bruteForce(nextTown, currentTown.getPickup(), currentLoadValue != 0 ? profit + dropOffProfit: profit);
        int skip = currentTownIdx != route.length ? bruteForce(nextTown, currentLoadValue, profit) : profit;

//        if(pickUp > dropOff && pickUp > skip) {
//            actions[currentTownIdx] = "Picked up at town" + currentTownIdx;
//        } else if (dropOff > pickUp && dropOff > skip) {
//            actions[currentTownIdx] = "Dropped off at town" + currentTownIdx;
//        }

        return Math.max(skip, Math.max(pickUp,dropOff));
    }

    /**
     * This is approximate approach to solve the problem of a truck that follows a linear route and either picks up or drops off
     * a load in order to receive the most profit from the route. This approach will look for local minimum for pickups and local
     * maximums for drops offs in order to determine the max profit possible for the route.
     * @return
     */
    public static int approximateApproach() {
        int currentLoadValue = 0;
        int profit = 0;
        boolean hasLoad = false;

        // At least 2 or more towns, allowing for at least one pick-up and drop-off
        if(route.length > 1) {
            for(int i = 0; i < route.length; i++) {
                // These represent the pickup value or drop of value of a town and the town ahead and behind
                // NOTE: If current town is index 0, previous pickup value will be set to infinity. Therefore current town will have lower pickup value
                // NOTE: If current town is index (route length - 1), next pick-up and drop-off values will be set to respective infinity.
                // Note: If we drop-off in a town the counter is decremented and load flag changed, allowing for immediate pickup in town of drop-off
                int currentTown = hasLoad ? route[i].getDropOff() : route[i].getPickup();
                double previous = hasLoad ? route[i-1].getDropOff() : i==0 ? Double.POSITIVE_INFINITY : route[i-1].getPickup();
                double next = hasLoad ? i == route.length -1 ? Double.NEGATIVE_INFINITY : route[i+1].getDropOff() : i == route.length -1 ? Double.POSITIVE_INFINITY : route[i+1].getPickup();

                if(hasLoad ? (currentTown > previous && currentTown > next) : (currentTown < previous && currentTown < next)) {
                    if(hasLoad) {
                        profit += currentTown - currentLoadValue;
                        currentLoadValue = 0;
                        hasLoad = false;
                        i--;
                    } else {
                        currentLoadValue = currentTown;
                        hasLoad = true;
                    }
                }
            }
        }

        return profit;
    }

    public static int exactApproach() {
        String[] actions = new String[route.length];

        return findMax(0, route.length -1, 1);
    }

    public static int findMax(int start, int end, int gap) {
        if(start == end) {return  0;}

        int currentMax = 0;
        int mid = end;

         for(int i = start; i < end; i++) {
             int currentValue = route[i].getPickup();
             int profit = i + gap <= end ? route[i + gap].getDropOff() - currentValue : 0;

             if(profit > currentMax) {
                currentMax = profit;
                mid = i + gap;
             }

         }

        return currentMax + findMax(start, mid - 1, gap + 1) + findMax(mid, end, gap);
    }

    public static void createRoute(int nTowns) {
        route = new Town[nTowns];
        Random ran = new Random();
        for(int i = 0; i < nTowns; i++) {
            route[i] = new Town(ran.nextInt(100), ran.nextInt(100));
        }
    }

    public static void main(String[] args) {
        float startTime;
        float endTime;
//        data.add(0);
//
//        route = new Town[1];
//        route[0] = new Town(100,80);
//
//        route = new Town[2];
//        route[0] = new Town(100, 80);
//        route[1] = new Town(120, 110);


        // The route example posted on the discussion forum
//        route = new Town[5];
//        route[0] = new Town(100, 80);
//        route[1] = new Town(120, 110);
//        route[2] = new Town(140, 120);
//        route[3] = new Town(110, 90);
//        route[4] = new Town(180, 150);

//        startTime = System.currentTimeMillis();
//        System.out.println(bruteForce(0,0, 0));
//        endTime = System.currentTimeMillis();
//        System.out.println("completed in: " + (endTime - startTime) + " seconds");
//
//        startTime = System.currentTimeMillis();
//        System.out.println(exactApproach());
//        endTime = System.currentTimeMillis();
//        System.out.println("completed in: " + (endTime - startTime) + " seconds");



//        route = new Town[18];
//        route[0] = new Town(140, 80);
//        route[1] = new Town(110, 100);
//        route[2] = new Town(120, 100);
//        route[3] = new Town(170, 120);
//        route[4] = new Town(110, 90);
//        route[5] = new Town(170, 80);
//        route[6] = new Town(140, 80);
//        route[7] = new Town(160, 110);
//        route[8] = new Town(150, 140);
//        route[9] = new Town(140, 140);
//        route[10] = new Town(160, 110);
//        route[11] = new Town(160, 110);
//        route[12] = new Town(110, 80);
//        route[13] = new Town(170, 130);
//        route[14] = new Town(110, 80);
//        route[15] = new Town(150, 130);
//        route[16] = new Town(120, 90);
//        route[17] = new Town(160, 80);

        createRoute(6);

        startTime = System.currentTimeMillis();
        System.out.println(bruteForce(0,0, 0));
        System.out.println("completed in: " + (System.currentTimeMillis() - startTime) + " seconds");

        startTime = System.currentTimeMillis();
        System.out.println(exactApproach());
        System.out.println("completed in: " + (System.currentTimeMillis() - startTime) + " seconds");

//        System.out.println(bruteForce(0,0, 0));
//        System.out.println(approximateApproach());
//        System.out.println(exactApproach());
    }
}
