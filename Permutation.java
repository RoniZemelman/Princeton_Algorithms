import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {

    public static void main(String[] args) {
        // StdIn Strings, return them in random order

        RandomizedQueue<String> r = new RandomizedQueue<>();

        // Read string and enque them
        while(!StdIn.isEmpty()){

            String inputString = StdIn.readString();
            r.enqueue(inputString);

        }

        // Number of times to randomly output
        int k = Integer.parseInt(args[0]);

        for (int i = 0; i < k; i++){

            StdOut.println(r.dequeue());
        }


    }

}
