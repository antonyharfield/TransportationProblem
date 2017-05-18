import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Arrays;

/**
 *
 * @author amjad antonyharfield
 */
public class TransportationProblem {

    // Problem
    int stockSize;
    int requiredSize;
    double []required;
    double []stock;
    double [][]cost;

    // Solution
    LinkedList<Variable> feasible = new LinkedList<Variable>();

    public TransportationProblem(int stockSize, int requiredSize ){
        this.stockSize = stockSize;
        this.requiredSize = requiredSize;

        stock = new double[stockSize];
        required = new double[requiredSize];
        cost = new double[stockSize][requiredSize];

        for(int i=0; i < (requiredSize + stockSize -1); i++)
            feasible.add(new Variable());

    }

    public void setStock(double value, int index){
        stock[index] = value;
    }

    public void setRequired(double value, int index){
        required[index] = value;
    }


    public void setCost(double value, int stock, int required){
        cost[stock][required] = value;
    }

    /**
     * initializes the feasible solution list using the North-West Corner
     * @return time elapsed
     */

    public double northWestCorner() {
        long start = System.nanoTime();

        double min;
        int k = 0; //feasible solutions counter

        //isSet is responsible for annotating cells that have been allocated
        boolean [][]isSet = new boolean[stockSize][requiredSize];
        for (int j = 0; j < requiredSize; j++)
            for (int i = 0;  i < stockSize; i++)
                    isSet[i][j] = false;

        //the for loop is responsible for iterating in the 'north-west' manner
        for (int j = 0; j < requiredSize; j++)
            for (int i = 0;  i < stockSize; i++)
                if(!isSet[i][j]){

                    //allocating stock in the proper manner
                    min = Math.min(required[j], stock[i]);

                    feasible.get(k).setRequired(j);
                    feasible.get(k).setStock(i);
                    feasible.get(k).setValue(min);
                    k++;

                    required[j] -= min;
                    stock[i] -= min;

                    //allocating null values in the removed row/column
                    if(stock[i] == 0)
                        for(int l = 0; l < requiredSize; l++)
                            isSet[i][l] = true;
                    else
                        for(int l = 0; l < stockSize; l++)
                            isSet[l][j] = true;
                }
        return (System.nanoTime() - start) * 1.0e-9;
    }

    /**
     * initializes the feasible solution list using the Least Cost Rule
     *
     * it differs from the North-West Corner rule by the order of candidate cells
     * which is determined by the corresponding cost
     *
     * @return double: time elapsed
     */

    public double leastCostRule() {
        long start = System.nanoTime();

        double min;
        int k = 0; //feasible solutions counter

        //isSet is responsible for annotating cells that have been allocated
        boolean [][]isSet = new boolean[stockSize][requiredSize];
        for (int j = 0; j < requiredSize; j++)
            for (int i = 0;  i < stockSize; i++)
                    isSet[i][j] = false;

        int i = 0, j = 0;
        Variable minCost = new Variable();

        //this will loop is responsible for candidating cells by their least cost
        while(k < (stockSize + requiredSize - 1)){

            minCost.setValue(Double.MAX_VALUE);
            //picking up the least cost cell
            for (int m = 0;  m < stockSize; m++)
                for (int n = 0; n < requiredSize; n++)
                    if(!isSet[m][n])
                        if(cost[m][n] < minCost.getValue()){
                            minCost.setStock(m);
                            minCost.setRequired(n);
                            minCost.setValue(cost[m][n]);
                        }

            i = minCost.getStock();
            j = minCost.getRequired();

            //allocating stock in the proper manner
            min = Math.min(required[j], stock[i]);

            feasible.get(k).setRequired(j);
            feasible.get(k).setStock(i);
            feasible.get(k).setValue(min);
            k++;

            required[j] -= min;
            stock[i] -= min;

            //allocating null values in the removed row/column
            if(stock[i] == 0)
                for(int l = 0; l < requiredSize; l++)
                    isSet[i][l] = true;
            else
                for(int l = 0; l < stockSize; l++)
                    isSet[l][j] = true;

        }

        return (System.nanoTime() - start) * 1.0e-9;
    }

    public double getSolution(){
        double result = 0;
        for(Variable x: feasible){
            result += x.getValue() * cost[x.getStock()][x.getRequired()];
        }

        return result;
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the problem size:");
        int s = scanner.nextInt();
        int r = scanner.nextInt();
        double x;
        TransportationProblem test = new TransportationProblem(s, r);

        System.out.println("Please enter the stocks capacity:");
        for (int i = 0; i < test.stockSize; i++){
            x = scanner.nextDouble();
            test.setStock(x, i);
        }

        System.out.println("Please enter the requirements:");
        for (int i = 0; i < test.requiredSize; i++){
            x = scanner.nextDouble();
            test.setRequired(x, i);
        }

        System.out.println("Please enter the transportation costs:");
        for (int i = 0; i < test.stockSize; i++)
            for (int j = 0; j < test.requiredSize; j++) {
                x = scanner.nextDouble();
                test.setCost(x, i, j);
            }

        System.out.println("Please enter 1 for North West Corner or 2 for Least Cost:");
        int method = scanner.nextInt();
        if (method == 1) {
            test.northWestCorner();
        }
        else {
            test.leastCostRule();
        }

        System.out.println("Solution:");
        for(Variable t: test.feasible){
            System.out.println(t);
        }

        System.out.println("Target function: " + test.getSolution());

        // Check if test.feasible is optimal
        if (test.isOptimal()) {
            System.out.println("Optimal");
        }
        else {
            System.out.println("Not optimal");
        }


    }

    public boolean isOptimal() {
        // First, check the essential condition:
        // The number of occupied cells must be equal to one less
        // than the sum of the number of rows and the number of cols
        if (feasible.size() != stockSize + requiredSize - 1) {
            System.out.println("Essential condition for optimality test not met");
            return false;
        }

        boolean [][]isSet = new boolean[stockSize][requiredSize];
        for (Variable t : feasible) {
            isSet[t.getStock()][t.getRequired()] = true;
        }

        // Second, calculate:
        // (i) a value for each row, denoted Ri
        // (ii) a value for each column, denoted Kj
        // If Cij is the unit cost in the cell in the ith row and jth
        // column then we can obtain the above values using: Cij = Ri + Kj
        // for the occupied cells
        double[] r = new double[stockSize];
        double[] k = new double[requiredSize];
        double NOVALUE = -1;
        Arrays.fill(r, NOVALUE);
        Arrays.fill(k, NOVALUE);
        r[0] = 0;
        boolean didResolve;
        do {
            didResolve = false;
            for (int i = 0; i < stockSize; i++) {
                for (int j = 0; j < requiredSize; j++) {
                    if (isSet[i][j]) {
                        if (r[i] != NOVALUE && k[j] == NOVALUE) {
                            k[j] = cost[i][j] - r[i];
                            //System.out.println("Resolve: k["+(j+1)+"]="+k[j]);
                            didResolve = true;
                        }
                        else if (r[i] == NOVALUE && k[j] != NOVALUE) {
                            r[i] = cost[i][j] - k[j];
                            //System.out.println("Resolve: r["+(i+1)+"]="+r[i]);
                            didResolve = true;
                        }
                    }
                }
            }
        } while (didResolve);
        // Print r k
        System.out.print("R:");
        for (int i = 0; i < stockSize; i++) {
            System.out.print(" "+r[i]);
        }
        System.out.print("\nK:");
        for (int j = 0; j < requiredSize; j++) {
            System.out.print(" "+k[j]);
        }
        System.out.println();

        // Then, calculate the improvement index from each unused cell:
        // Iij = Cij - Ri - Kj
        double[][] improvement = new double[stockSize][requiredSize];
        for (int i = 0; i < stockSize; i++) {
            for (int j = 0; j < requiredSize; j++) {
                if (isSet[i][j]) {
                    improvement[i][j] = 0;
                }
                else {
                    improvement[i][j] = cost[i][j] - r[i] - k[j];
                }
            }
        }
        // Print improvement indices
        System.out.println("Improvement indices:");
        for (int i = 0; i < stockSize; i++) {
            for (int j = 0; j < requiredSize; j++) {
                System.out.print(j == 0 ? "> " : " | ");
                System.out.print(improvement[i][j]);
            }
            System.out.println();
        }

        // If all the improvement indices are greater than or equal to zero,
        // then an optimal solution has been reached.
        for (int i = 0; i < stockSize; i++) {
            for (int j = 0; j < requiredSize; j++) {
                if (improvement[i][j] < 0) {
                    return false; // Not optimal
                }
            }
        }
        return true; // Optimal
    }


}
