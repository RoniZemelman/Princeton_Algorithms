import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.HashMap;

/* *****************************************************************************
 *  Name: Roni Zemelman
 *  Date:
 *  Description: Princeton Algorithms II, assignment 3
 **************************************************************************** */
public class BaseballElimination {
    private final int numOfTeams;
    private final int numOfGamePairs;
    private final int[][] standings;
    private final int[][] gamesBetween;
    private final HashMap<String, Integer> team2id;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {

        In baseballIn = new In(filename);

        // Initialize Structures
        numOfTeams = baseballIn.readInt();
        numOfGamePairs = getGamesBetween(numOfTeams - 1); // Account for team being examined
        standings = new int[numOfTeams][4];  // Width has columns team, w, l, r
        gamesBetween = new int[numOfTeams][numOfTeams];

        team2id = new HashMap<String, Integer>();

        int row = 0;
        while (!baseballIn.isEmpty()) {

            // Retrieve data for every team
            String teamName = baseballIn.readString();
            team2id.put(teamName, row);

            standings[row][0] = row; // i
            standings[row][1] = baseballIn.readInt(); // wins
            standings[row][2] = baseballIn.readInt(); // losses
            standings[row][3] = baseballIn.readInt(); // remaining


            // Games remaining between division teams
            for (int i = 0; i < numOfTeams; i++) {
                gamesBetween[row][i] = baseballIn.readInt();
            }
            row++;
        }

    }

    // number of teams
    public int numberOfTeams() {
        return numOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return team2id.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        int row = team2id.get(team);
        return standings[row][1];
    }

    // number of losses for given team
    public int losses(String team) {
        int row = team2id.get(team);
        return standings[row][2];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        int row = team2id.get(team);
        return standings[row][3];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {

        int i = team2id.get(team1);
        int j = team2id.get(team2);

        return gamesBetween[i][j];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {

        // Calculate # of vertices for Flow Network
        int v = 2 + numOfGamePairs + numOfTeams; // s + games + teams + t


        // Build FlowNetwork
        FlowNetwork baseballNetwork = new FlowNetwork(v);
        populateFlowNetwork(baseballNetwork, team);


        StdOut.println(baseballNetwork.toString());
        // Run FordFulkerson to see maxFlow

        // for FordFulkerson.value() < totalGames return true (team is eliminated)

        return true; // Placeholder
    }

    // Returns total combinations of 2 games between n teams
    private int getGamesBetween(int n) {
        return factorial(n) / (2 * factorial(n - 2));
    }

    private int factorial(int n) {
        if (n == 1) return 1;
        return n * factorial(n - 1);
    }

    private void populateFlowNetwork(FlowNetwork baseballNetwork, String team) {

        int teamID = team2id.get(team);

        int s = 0;
        int t = baseballNetwork.V() - 1;
        boolean[][] marked = new boolean[numOfTeams][numOfTeams];

        // Make edges from s to all gamesBetween, gamesBetween to teams
        int currentW = 1;
        for (int i = 0; i < numOfTeams; i++) {
            for (int j = 0; j < numOfTeams; j++) {

                if (!visited(i, j, marked) && i != j
                        && (i != teamID && j != teamID)) {

                    marked[i][j] = true;
                    marked[j][i] = true;

                    FlowEdge etoGame = new FlowEdge(s, currentW, gamesBetween[i][j]);
                    FlowEdge etoI = new FlowEdge(currentW, numOfGamePairs + i,
                                                 Double.POSITIVE_INFINITY);
                    FlowEdge etoJ = new FlowEdge(currentW, numOfGamePairs + j,
                                                 Double.POSITIVE_INFINITY);

                    baseballNetwork.addEdge(etoGame);
                    baseballNetwork.addEdge(etoI);
                    baseballNetwork.addEdge(etoJ);

                    currentW++;
                }
            }
        }

        // Populate edges between teams and t
        int currentTeam = 0;
        for (int i = numOfGamePairs; i < (numOfGamePairs + numOfTeams - 1); i++) {

            int capacity = wins(team) + remaining(team) - standings[currentTeam][1];
/*            StdOut.println("Team " + currentTeam);
            StdOut.printf("Capacity = %d wins + %d remaining - (%d wins of team %d) = %d \n",
                          wins(team), remaining(team),
                          standings[currentTeam][1], currentTeam, capacity);*/
            FlowEdge edgeToT = new FlowEdge(i, t, Math.max(0.0, capacity));

            baseballNetwork.addEdge(edgeToT);

            currentTeam++;
        }

    }

    private boolean visited(int i, int j, boolean[][] marked) {
        return marked[i][j] && marked[j][i];
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        return new Stack<String>(); // Placeholder
    }

    public static void main(String[] args) {

        BaseballElimination baseball = new BaseballElimination(args[0]);

        for (String team : baseball.teams()) {

            StdOut.print(team + " ");
            StdOut.println(baseball.wins(team) + " " + baseball.losses(team)
                                   + " " + baseball.remaining(team));

        }

        StdOut.println(Arrays.deepToString(baseball.gamesBetween));

        baseball.isEliminated("Montreal");

    }
}
