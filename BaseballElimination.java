import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

/* *****************************************************************************
 *  Name: Roni Zemelman
 *  Date:
 *  Description: Princeton Algorithms II, assignment 3
 **************************************************************************** */
public class BaseballElimination {
    private final int numOfTeams;
    private final int[][] standings;
    private final HashMap<String, Integer> team2id;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {

        In baseballIn = new In(filename);

        // Initialize Structures
        numOfTeams = baseballIn.readInt();
        standings = new int[numOfTeams][4
                + numOfTeams];  // Width has columns team, w, l, r + between

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
                standings[row][4 + i] = baseballIn.readInt();
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

        int row = team2id.get(team1);
        int col = team2id.get(team2);

        return standings[row][4 + col];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
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

        StdOut.println("Games remaining between Atlanta + NY " +
                               baseball.against("Atlanta", "New_York"));

        StdOut.println("Games remaining between Philly + Atlanta " +
                               baseball.against("Philadelphia", "Atlanta"));

        StdOut.println("Games remaining between NY + Philly " +
                               baseball.against("New_York", "Philadelphia"));

        StdOut.println("Games remaining between Montreal + Philly " +
                               baseball.against("Montreal", "Philadelphia"));

    }
}
