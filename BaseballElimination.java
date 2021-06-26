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
    private final int[][] standings;
    private final int[][] gamesBetween;
    private final HashMap<String, Integer> team2id;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {

        In baseballIn = new In(filename);

        // Initialize Structures
        numOfTeams = baseballIn.readInt();
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

        // Build FlowNetwork

        // Run FordFulkerson to see maxFlow

        // for FordFulkerson.value() < numofTeams return true (team is eliminated)

        return true; // Placeholder
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

        StdOut.println(baseball.against("New_York", "Atlanta"));
    }
}
