import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node<Item> first;
    private Node<Item> last;
    private int numberOfNodes;

    private static class Node<Item> {

        private Item item;
        private Node<Item> next;
        private Node<Item> previous;
    }

    // construct an empty deque - pointers for null
    public Deque() {

        first = null;
        last = null;
        numberOfNodes = 0;

    }

    // is the deque empty?
    public boolean isEmpty() {
        return numberOfNodes == 0;
    }

    // return the number of items on the deque
    public int size() {
        return numberOfNodes;
    }

    // add the item to the front
    public void addFirst(Item item) {

        if (item == null) throw new IllegalArgumentException();

        // If empty, create new node, set both points to null
        if (isEmpty()) {

            first = new Node<>();
            first.next = null;
            last = first;
        }

        else {

            // Store old first node in temp variable pointer
            Node<Item> oldFirst = first;

            // Create new node, 2 directional points
            first = new Node<>();
            first.next = oldFirst;
            oldFirst.previous = first;
        }

        // In both cases, set new node's item to item, previous pointer to null
        first.item = item;
        first.previous = null;
        numberOfNodes++;
    }

    // add the item to the back
    public void addLast(Item item) {

        if (item == null) throw new IllegalArgumentException();

        // Two cases - if empty or not
        if (isEmpty()) addFirst(item);

        else {

            // Store last node in temp variable
            Node<Item> oldLast = last;

            // Create new last node, point forward to null
            // backwards to old last node, and store item.
            // Also have old last node point forward to the new one
            last = new Node<>();
            last.item = item;
            last.next = null;
            last.previous = oldLast;

            oldLast.next = last;

            // Increment number of nodes
            numberOfNodes++;
        }
    }

    // remove and return the item from the front
    public Item removeFirst() {

        if (isEmpty()) throw new NoSuchElementException();

        Item item = first.item;

        // If one item to remove
        if (numberOfNodes == 1) {

            first = null;
            last = null;
        }
        else {

            first = first.next;
            first.previous = null;
        }

        numberOfNodes--;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {

        if (isEmpty()) throw new NoSuchElementException("Empty deque");

        // Store item of last node
        Item item = last.item;

        // If 1 node, removeFirst method
        if (numberOfNodes == 1) return removeFirst();

        else {

            // Move last pointer back, set forward pointer to null (end of list)
            last = last.previous;
            last.next = null;
        }
        numberOfNodes--;
        return item;

    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator(first);
    }

    private class DequeIterator implements Iterator<Item> {

        private Node<Item> current;

        public DequeIterator(Node<Item> first) {
            current = first;
        }

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();

            Item item = current.item;
            current = current.next;

            return item;
        }

    }

    // unit testing (required)
    public static void main(String[] args) {

        Deque<Integer> d = new Deque<Integer>();

        d.addFirst(5);
        d.addFirst(6);


        StdOut.println("Expected: 5 --> 6");
        for (int num : d)
            StdOut.println(num);

        StdOut.println();

        d.removeLast();
        StdOut.println(d.isEmpty());
        StdOut.println("Expected: 6");
        for (int num : d)
            StdOut.println(num);

        StdOut.println("number of nodes: " + d.numberOfNodes);
        d.removeLast();
    }


}
