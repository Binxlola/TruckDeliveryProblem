import java.util.Random;

/**
 * @author Jason Smit
 */
public class TruckDelivery {

    public static Town[] route;
    public static String[] actions;


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

        // Calculate the profits for the rest of the route trying each drop-off/pick-up option at the current townS
        int dropOff = currentLoadValue != 0 ? bruteForce(nextTown, 0, profit + dropOffProfit) : profit;
        int pickUp = bruteForce(nextTown, currentTown.getPickup(), currentLoadValue != 0 ? profit + dropOffProfit: profit);
        int skip = bruteForce(nextTown, currentLoadValue, profit);

        if(pickUp >= dropOff && pickUp > skip) {
            actions[currentTownIdx] = "Picked up at town" + currentTownIdx;
        } else if (dropOff >= pickUp && dropOff > skip) {
            actions[currentTownIdx] = "Dropped off at town" + currentTownIdx;
        }

        return Math.max(skip, Math.max(pickUp,dropOff));
    }

    /**
     * This is approximate approach to solve the problem of a truck that follows a linear route and either picks up or drops off
     * a load in order to receive the most profit from the route. This approach will look for local minimum for pickups and local
     * maximums for drops offs in order to determine the max profit possible for the route.
     * @return The max profit or near max profit attainable on the current route
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

    /**
     * Creates a 2D array, runs through the currently set route and populates the 2D array with all possible pick up and drop of pair values.
     * If there was a pickup and drop off prior to the pickup being inspected, it's value may be added with the respective drop off
     * Giving a value that is derived from 2 pick up/drop off pairs in succession. While doing this, a current max value
     * Will be retained, this value represents any number of pick-up/drop-off combinations given their added value is the highest so far.
     * @return The maximum profit attainable along the currently set route.
     */
    public static int exactApproach() {
        int[][] profitTable = new int[route.length - 1][route.length];
        int currentMax = 0;

        for(int i = 0; i < profitTable.length; i++) { // Pickup town
            int pickupValue = route[i].getPickup();
            for(int j = i + 1; j < route.length; j++) { // Starting at first town where a drop is possible iterates all following drop points
                int dropOffProfit = route[j].getDropOff() - pickupValue;
                int previousProfit = i > 0 ? findPastMax(i,profitTable) : 0; // Highest profit obtained along route before current pickup town

                if(i > 0) {
                    profitTable[i][j] = dropOffProfit > 0 ? dropOffProfit + previousProfit : previousProfit; // Can be a combo of a pick-up/drop-off pair prior
                } else {
                    profitTable[i][j] = Math.max(dropOffProfit, 0);
                }

                int finalProfit = profitTable[i][j]; // The profit of the current pick-up/drop-off pair regardless of their being a combo or not
                if(finalProfit > currentMax) { currentMax = finalProfit; }
            }
        }

        return currentMax;
    }

    /**
     * Given a index representing a current pick-up point being inspected and a up to date profit table for a route.
     * Will then traverse from the first town as a pickup and then traverse from the town after as a dropoff upto the given
     * index and find the highest value between these points
     * @param pickupIdx The index value of the pick-up point currently being inspected
     * @param profitTable The up to date profit table
     * @return The highest value found
     */
    private static int findPastMax(int pickupIdx, int[][] profitTable) {
        int currentMax = 0;

        // NOTE: Can go upto but not past the pickup index as route can't take values that are ahead.
        for(int i = 0; i <= pickupIdx; i++) { // Start from first possible pickup town
            for(int j = i + 1; j <= pickupIdx; j++) { // Start from first possible dropoff town
                int profit = profitTable[i][j];
                if(profit > currentMax) {currentMax = profit;}
            }
        }
        return currentMax;
    }


    /**
     * Given a number of town will generate a route with the given number of towns following the restrictions
     * @param nTowns Number of town to be in the route
     */
    public static void createRoute(int nTowns) {
        route = new Town[nTowns];
        Random ran = new Random();

        for(int i = 0; i < nTowns; i++) {
            int pickUp = ran.nextInt(100);
            int dropOff;

            // Drop off value always has to be lower or equal to pick up value
            do {
                dropOff = ran.nextInt(100);
            } while (dropOff > pickUp);

            route[i] = new Town(pickUp, dropOff);
        }
    }

    /**
     * Runs all algorithms with the currently set route
     */
    public static void run() {
        long startTime;
        long duration;
        int profit;


        System.out.println("=====Running Approximate Approach=====");
        startTime = System.currentTimeMillis();
        profit = approximateApproach();
        duration = System.currentTimeMillis() - startTime;
        System.out.println("Final Profit: " + profit);
        System.out.println("completed in: " + (double)(duration / 1000) + " seconds\n");

        System.out.println("=====Running Brute Force Approach=====");
        startTime = System.currentTimeMillis();
        profit = bruteForce(0,0,0);
        duration = System.currentTimeMillis() - startTime;
        System.out.println("Final Profit: " + profit);
        System.out.println("completed in: " + (double)(duration / 1000) + " seconds\n");

        System.out.println("=====Running Exact Approach=====");
        startTime = System.currentTimeMillis();
        profit = exactApproach();
        duration = System.currentTimeMillis() - startTime;
        System.out.println("Final Profit: " + profit);
        System.out.println("completed in: " + (double)(duration / 1000) + " seconds\n");

        System.out.println("Path taken: ");
        for(String action: actions) {
            if (action != null) System.out.println(action);
        }
    }

    /**
     * Creates and sets pre-defined routes and then runs the algorithms for each route
     */
    public static void runTests() {

        route = new Town[1];
        actions = new String[1];
        route[0] = new Town(100,80);
        System.out.println("############## TEST 1 START ##############");
        run();
        System.out.println("############## TEST 1 END ##############\n");

        route = new Town[2];
        actions = new String[2];
        route[0] = new Town(100, 80);
        route[1] = new Town(120, 110);
        System.out.println("############## TEST 2 START ##############");
        run();
        System.out.println("############## TEST 2 END ##############\n");

        route = new Town[5];
        actions = new String[5];
        route[0] = new Town(100, 80);
        route[1] = new Town(120, 110);
        route[2] = new Town(140, 120);
        route[3] = new Town(110, 90);
        route[4] = new Town(180, 150);
        System.out.println("############## TEST 3 START ##############");
        run();
        System.out.println("############## TEST 3 END ##############\n");

        route = new Town[18];
        actions = new String[18];
        route[0] = new Town(140, 80);
        route[1] = new Town(110, 100);
        route[2] = new Town(120, 100);
        route[3] = new Town(170, 120);
        route[4] = new Town(110, 90);
        route[5] = new Town(170, 80);
        route[6] = new Town(140, 80);
        route[7] = new Town(160, 110);
        route[8] = new Town(150, 140);
        route[9] = new Town(140, 140);
        route[10] = new Town(160, 110);
        route[11] = new Town(160, 110);
        route[12] = new Town(110, 80);
        route[13] = new Town(170, 130);
        route[14] = new Town(110, 80);
        route[15] = new Town(150, 130);
        route[16] = new Town(120, 90);
        route[17] = new Town(160, 80);
        System.out.println("############## TEST 4 START ##############");
        run();
        System.out.println("############## TEST 4 END ##############\n");
    }

    public static void main(String[] args) {
        // Run pre build tests
        runTests();

        // Create random route with n towns and run test
//        createRoute(20);
//        run();
    }
}
