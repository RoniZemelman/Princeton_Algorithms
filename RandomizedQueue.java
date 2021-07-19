import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private static final int INIT_CAPACITY = 2;

    private Item[] array;
    private int n;

    // construct an empty randomized queue
    public RandomizedQueue(){

        array = (Item[]) new Object[INIT_CAPACITY];
        n = 0;

    }

    // is the randomized queue empty?
    public boolean isEmpty(){
        return n == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    private void resize(int capacity){
        assert capacity >= n;

        // Create copy of doubled size
        Item[] copy = (Item[]) new Object[capacity];

        // Copy all non-null items into copy array
        for (int i = 0; i < n; i++){

            copy[i] = array[i];
        }

        // set array to new copied array
        array = copy;

    }

    // add the item
    public void enqueue(Item item){

        if(item == null) throw new IllegalArgumentException();


        // if full, resize array to size n*2
        if (n == array.length) resize(2*array.length);

        // Add item at index n
        array[n] = item;
        n++;

    }

    // remove and return a random item
    public Item dequeue(){

        if(isEmpty()) throw new NoSuchElementException();

        // Find a non-null random area
        int randomIndex = StdRandom.uniform(n);

        // Replace rand_index with last item in array (n-1)
        Item item = array[randomIndex];  // store item at random index

        array[randomIndex] = array[n-1];  // replace it with last index
        array[n-1] = null;

        n--; // downsize array

        // shrink size of array if necessary
        if (n > 0 && n == array.length/4) resize(array.length/2);



        return item;
    }

    // return a random item (but do not remove it)
     public Item sample(){

         if(isEmpty()) throw new NoSuchElementException();

         int next = StdRandom.uniform(n);

         return array[next];

     }

    // return an independent iterator over items in random order
      public Iterator<Item> iterator(){
        return new RandomIterator();
      }

      private class RandomIterator implements Iterator<Item> {

        private Item[] items;
        private int current;

        public RandomIterator(){

            items = (Item[]) new Object[n];

            // Copy items from array to temp items
            if (n >= 0) System.arraycopy(array, 0, items, 0, n);

            // Randomize order of the items
            StdRandom.shuffle(items);


            // set current at index 0
            current = 0;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext(){

            return current <= n-1;  // Current is not at the last index
        }

        public Item next() {

            if(!hasNext()) throw new NoSuchElementException();

            Item item = items[current];

            current++;

            return item;
        }
      }

    // unit testing (required)
    public static void main(String[] args){

    RandomizedQueue<String> r = new RandomizedQueue<>();

    r.enqueue("Roni0");
    r.enqueue("Michelle1");
    r.enqueue("Roni2");
    r.enqueue("Michelle3");
    r.enqueue("Roni4");
    r.enqueue("Michelle5");
    r.enqueue("Roni6");
    r.enqueue("Michelle7");


    StdOut.println("dequed: " + r.dequeue());
    StdOut.println("dequed: " + r.dequeue());


    StdOut.println("length of queue: " + r.size());

    for(String name: r ){
        StdOut.println(name); }
    }


}
