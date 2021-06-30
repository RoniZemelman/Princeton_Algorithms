import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
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
    private final HashMap<Integer, String> id2team;
    private FlowNetwork baseballNetwork;
    private boolean eliminated;
    private FordFulkerson maxflow;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {

        In baseballIn = new In(filename);

        // Initialize Structures
        numOfTeams = baseballIn.readInt();
        numOfGamePairs = countCombosRemaining(numOfTeams); // Account for team being examined
        // StdOut.println("Number of remaining games: " + numOfGamePairs);
        standings = new int[numOfTeams][4];  // Width has columns team, w, l, r
        gamesBetween = new int[numOfTeams][numOfTeams];

        team2id = new HashMap<String, Integer>();
        id2team = new HashMap<Integer, String>();

        int row = 0;
        while (!baseballIn.isEmpty()) {

            // Retrieve data for every team
            String teamName = baseballIn.readString();
            team2id.put(teamName, row);
            id2team.put(row, teamName);

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

        // Build Flow Network
        int numRemaining = numOfTeams - 1;
        int v = 2 + numOfGamePairs + numRemaining; // s + games + teams + t
        baseballNetwork = new FlowNetwork(v);

        eliminated = false;
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

        if (team == null || team2id.get(team) == null) throw new IllegalArgumentException();

        // Trivial elimination
        if (wins(team) + remaining(team) < standings[0][1]) {
            StdOut.println("Trivial elimination");
            return true;
        }

        // Build Flow network
        populateFlowNetwork(team);

        // StdOut.println("***FlowNetwork***\n" + baseballNetwork.toString());

        // Run FordFulkerson to see maxFlow
        int s = 0;
        int t = baseballNetwork.V() - 1;
        maxflow = new FordFulkerson(baseballNetwork, s, t);

        // Check if all edges from S are full
        for (FlowEdge e : baseballNetwork.edges()) {
            if (e.from() == s && e.to() <= numOfGamePairs) {
                int w = e.to();
                if (e.residualCapacityTo(w) > 0.0) {
                    return true;
                }
            }
        }

        return false;
    }

    private int countCombosRemaining(int n) {

        int teamID = 0; // TESTING PURPOSES

        boolean[][] marked = new boolean[n][n];

        int counter = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (!visited(i, j, marked) && i != j
                        && (i != teamID && j != teamID)) {

                    marked[i][j] = true;
                    marked[j][i] = true;

                    counter++;
                }
            }
        }
        return counter;
    }

    private void populateFlowNetwork(String team) {
        int teamID = team2id.get(team);
        HashMap<Integer, Integer> ijToV = new HashMap<Integer, Integer>();

        // Build converted i,j in hashmap
        for (int teamNum = 0, k = 0; teamNum < numOfTeams; teamNum++) {
            if (teamNum != teamID) {
                ijToV.put(teamNum, k);
                k++;
            }
        }

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

                    int newI = ijToV.get(i);
                    int newJ = ijToV.get(j);

                    StdOut.printf("Games Found at %d, %d.  New ij: %d,%d\n", i, j, newI,
                                  newJ);

                    // From source -> games -> teams
                    FlowEdge sToGame = new FlowEdge(s, currentW, gamesBetween[i][j]);
                    FlowEdge gameToI = new FlowEdge(currentW, 1 + numOfGamePairs + newI,
                                                    Double.POSITIVE_INFINITY);
                    FlowEdge gameToJ = new FlowEdge(currentW, 1 + numOfGamePairs + newJ,
                                                    Double.POSITIVE_INFINITY);

                    baseballNetwork.addEdge(sToGame);
                    baseballNetwork.addEdge(gameToI);
                    baseballNetwork.addEdge(gameToJ);

                    currentW++;
                }
            }
        }
        // From teams -> t
        for (int i = 0; i < numOfTeams; i++) {

            if (i == teamID) continue;

            int capacity = wins(team) + remaining(team) - standings[i][1];
            int newI = ijToV.get(i);

            FlowEdge teamToSink = new FlowEdge(1 + numOfGamePairs + newI, t, capacity);
            baseballNetwork.addEdge(teamToSink);
        }
    }

    private boolean visited(int i, int j, boolean[][] marked) {
        return marked[i][j] && marked[j][i];
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {

        // Reinitialize Flow Network
        int numRemaining = numOfTeams - 1;
        int v = 2 + numOfGamePairs + numRemaining; // s + games + teams + t
        baseballNetwork = new FlowNetwork(v);

        if (!isEliminated(team)) return null;

        // Check if all edges from S are full
        ArrayList<String> teamsAhead = new ArrayList<>();

        HashMap<Integer, Integer> vToIJ = new HashMap<Integer, Integer>();
        // Build converted i,j in hashmap
        for (int teamNum = 0, k = 0; teamNum < numOfTeams; teamNum++) {
            if (teamNum != team2id.get(team)) {
                vToIJ.put(k, teamNum);
                k++;
            }
        }

        for (FlowEdge e : baseballNetwork.edges()) {

            if (e.from() == 0 && e.to() <= numOfGamePairs) {
                int w = e.to();
                if (e.residualCapacityTo(w) > 0.0) {

                    int originalI = vToIJ.get(w);
                    teamsAhead.add(id2team.get(originalI));
                }
            }
        }
        return teamsAhead;
    }

    public static void main(String[] args) {

        BaseballElimination baseball = new BaseballElimination(args[0]);

/*        for (String team : baseball.teams()) {

            StdOut.print(team + " ");
            StdOut.println(baseball.wins(team) + " " + baseball.losses(team)
                                   + " " + baseball.remaining(team));

        }*/

        String team = args[1];
        boolean eliminated = baseball.isEliminated(team);
        StdOut.println(team + " i = " + baseball.team2id.get(team));
        StdOut.println(eliminated);

        StdOut.println(baseball.certificateOfElimination(args[1]));


        // boolean eliminated = baseball.isEliminated("Detroit"); // for teams5.txt

        // StdOut.println(eliminated);

        // boolean eliminated = baseball.isEliminated("Montreal"); // for teams4.txt

        // StdOut.println(eliminated);

    }
}
