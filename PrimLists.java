import java.io.*;
import java.util.Scanner;

class GraphLists {
    class Node {
        public int vert;
        public int wgt;
        public Node next;
    }

    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    // mst[] holds values of parent[] from the Prim algorithm for printing
    private int V, E;
    private Node[] adj;
    private Node z;
    private int[] mst;

    // used for traversing graph
    private int[] visited;
    private int id;

    // default constructor
    public GraphLists(String graphFile) throws IOException {
        int u, v;
        int e, wgt;
        Node t;

        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);

        String splits = " +"; // multiple whitespace as delimiter
        String line = reader.readLine();
        String[] parts = line.split(splits);
        System.out.println("\nParts[] = " + parts[0] + " " + parts[1]);

        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);

        // create sentinel node
        z = new Node();
        z.next = z;

        // create adjacency lists, initialised to sentinel node z
        visited = new int[V + 1];
        adj = new Node[V + 1];
        for (v = 1; v <= V; ++v)
            adj[v] = z;

        // read the edges
        System.out.println("Reading edges from text file:\n");
        for (e = 1; e <= E; ++e) {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]);
            wgt = Integer.parseInt(parts[2]);

            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));

            // puts edge into adjacency list
            t = new Node();
            t.vert = v;
            t.wgt = wgt;
            t.next = adj[u];
            adj[u] = t;

            t = new Node();
            t.vert = u;
            t.wgt = wgt;
            t.next = adj[v];
            adj[v] = t;
        }
        reader.close();
    }

    // converts vertex into char for pretty printing
    private char toChar(int u) {
        return (char) (u + 64);
    }

    // method to display the graph representation
    public void display() {
        int v;
        Node n;

        System.out.println("\n\nDisplaying adjacency list:");

        for (v = 1; v <= V; ++v) {
            System.out.print("\nadj[" + toChar(v) + "] ->");
            for (n = adj[v]; n != z; n = n.next)
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");
        }
        System.out.println("");
    }


    //=======================================================
    //
    //                 Depth first traversal
    //
    //=======================================================

    // method to initialise Depth First Traversal of Graph
    public void DF(int s) {
        
        id = 0;

        // foreach vertex in V
        for (int v = 1; v <= V; v++) { 
            visited[v] = 0;
        }

        System.out.print("\nDepth First Graph Traversal\n");
        System.out.println("Starting with Vertex " + toChar(s));
        
        // start visiting vertices using DF starting from vertex s.
        dfVisit(0, s);
        
        System.out.print("\n\n");
    }

    // Recursive Depth First Traversal for adjacency list
    private void dfVisit(int prev, int v) {
        Node t;
        int u;

        visited[v] = ++id;
        System.out.print("\n  DF just visited vertex " + toChar(v) + " along " + toChar(prev) + "--" + toChar(v));

        // for each vertex in adj(v)
        for (t = adj[v]; t != z; t = t.next) {
            u = t.vert;
            if (visited[u] == 0) {
                dfVisit(v, u);
            }
        }
    } // end dfVisit()


    //=======================================================
    //
    //          Breadth first traversal using Queue
    //
    //=======================================================
    public void BF(int s) {
        
        Queue Q = new Queue();
        id = 0;
        int u, v;
        Node t;

        // for each vertex in V
        for (v = 1; v <= V; v++) {
            visited[v] = 0;
        }

        System.out.print("\nBreadth First Graph Traversal\n");
        System.out.println("Starting with Vertex " + toChar(s));

        Q.enQueue(s);

        while (!Q.isEmpty()) {
            v = Q.deQueue();

            if (visited[v] == 0) {

                visited[v] = ++id;
                System.out.print("\n  BF just visited vertex " + toChar(v));

                // for each vertex in adj[v]
                for (t = adj[v]; t != z; t = t.next) {
                    u = t.vert;

                    if (visited[u] == 0)
                        Q.enQueue(u);
                } // end for
                
            } // end if
            
        } // end while

    }// End BF()


    //=======================================================
    //
    //        Heap implementation of Prim's algorithm
    //
    //=======================================================
    public void MST_Prim(int s) {
        int v, u;
        int wgt, wgt_sum = 0;
        int[] dist, parent, hPos;
        Node t;

        // Initialising arrays
        dist = new int[V + 1];      // the distance from starting vertex
        parent = new int[V + 1];    // array to hold parent of vertex
        hPos = new int[V + 1];      // heap Position

        // for each vertex in V
        for (v = 0; v <= V; ++v) 
        {
            dist[v] = Integer.MAX_VALUE;
            parent[v] = 0;          // 0 is = null vertex
            hPos[v] = 0;            // indicates v is not in heap
        }

        dist[s] = 0;

        Heap h = new Heap(V, dist, hPos);   // Heap initially empty
        h.insert(s);                        // s will be the root of the MST

        while (!h.isEmpty())    // should repeat |V| -1 times
        {
            v = h.remove();     // add v to the MST
            dist[v] = -dist[v]; // mark v as now in the MST

            wgt_sum -= dist[v]; // add the wgt of v to sum
            
            System.out.println("Adding to MST: Edge " + toChar(parent[v]) + "--(" + -dist[v] + ")--" + toChar(v));

            // for each neighbour of v
            for (t = adj[v]; t != z; t = t.next) 
            {
                u = t.vert;
                wgt = t.wgt;

                // if new weight less than current weight
                if (wgt < dist[u]) 
                {
                    dist[u] = wgt;
                    parent[u] = v;

                    // if not in heap, insert
                    if (hPos[u] == 0) 
                    {
                        h.insert(u);
                    }
                    // if already in heap, siftup the heap node
                    else            
                    {
                        h.siftUp(hPos[u]);  
                    }
                }
            } // end for

        } // end while()

        System.out.print("\n\nWeight of MST = " + wgt_sum + "\n");
        mst = parent;

    } // end MST_Prim()

    
    // Prints out MST
    public void showMST()
    {
        System.out.print("\n\nMinimum Spanning tree parent array is:\n");
        for (int v = 1; v <= V; ++v)
            System.out.println(toChar(v) + " -> " + toChar(mst[v]));
        System.out.println("");
    }

} // End of class GraphLists

// Driver Code
public class PrimLists {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        String fname;
        int s;

        System.out.println("\n\nGraph traversal and Prim's Algorithm\n");

        System.out.print("\nEnter .txt filename: ");
        fname = sc.nextLine();

        System.out.print("\nEnter root vertex: ");
        s = sc.nextInt();

        
        sc.close(); // Closing Scanner after use

        GraphLists g = new GraphLists(fname);
        g.display();

        g.DF(s);

        g.BF(s);

        System.out.println("\n\nPrim's Algorithm:\n");

        g.MST_Prim(s);
        g.showMST();

    } // End of main

} // End of class PrimLists


//=======================================================
//
//  Heap Code for efficient implementation of Prim's Alg
// 
//=======================================================
class Heap {
    private int[] a;    // heap array
    private int[] hPos; // hPos[h[k]] == k
    private int[] dist; // dist[v] = priority of v
    private int N;      // heap size

    // The heap constructor gets passed from the Graph:
    // 1. maximum heap size
    // 2. reference to the dist[] array
    // 3. reference to the hPos[] array
    public Heap(int maxSize, int[] _dist, int[] _hPos) {
        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public void siftUp(int k) {
        int v = a[k];

        a[0] = 0;
        dist[0] = Integer.MIN_VALUE;

        // while distance of current vertex in heap is smaller than parent
        while (dist[v] < dist[a[k/2]]) {
            a[k] = a[k/2];  // parent replaces the child
            hPos[a[k]] = k; // heap position of the parent modified in hPos[]
            k = k / 2;
        }

        a[k] = v;       // original vertex placed in its right place
        hPos[v] = k;    // modified the heap position of the vertex in hPos[]

    }

    public void siftDown(int k) {
        int v, j;
        v = a[k];

        while (k <= N/2) {
            j = 2 * k;

            // if the right side of the tree is smaller than left
            if (j < N && dist[a[j]] > dist[a[j + 1]])
                ++j;

            // if size of parent vertex is less than its child
            if (dist[v] <= dist[a[j]])
                break;

            a[k] = a[j];    // if parent is greater than child, assign parent's position
            hPos[a[k]] = k; // update new position of the vertex in hPos[]

            k = j;          // update position
        }

        a[k] = v;       // Vertex placed after sifting down
        hPos[v] = k;    // Modify hPos[] of the vertex passed
    }

    public void insert(int x) {
        a[++N] = x; // attaches new vertex to the end of the heap
        siftUp(N);  // siftup the new vertex
    }

    public int remove() {
        int v = a[1];
        hPos[v] = 0;    // v is no longer in heap

        a[1] = a[N--];  // last node of heap moved to top
        siftDown(1);    // then sifted down

        a[N + 1] = 0;   // put null node into empty spot

        return v;       // return vertex at top of heap
    }

} // End of class Heap


//=======================================================
//
//          Queue Code for implementation of BF
//
//=======================================================
class Queue {

    class Node {
        public int vert;
        public Node next;
    }

    Node head, tail, z;

    public Queue() {
        z = new Node(); // the sentinel
        z.next = z;     // points to itself
        head = z;       // head is pointing to the sentinel
        tail = null;
    }

    // checks if queue is empty - head reaching the sentinel
    public boolean isEmpty() {
        return head == head.next;
    }

    public void enQueue(int num) {
        Node t = new Node();
        t.vert = num;
        t.next = z;     // new node is initialised to point at sentinel

        if (head == z)  // case of empty list
            head = t;
        else            // case of list not empty
            tail.next = t;

        tail = t; // new node is now at the tail
    }

    // head points to the next node, deleting it
    public int deQueue() {
        int x = head.vert;
        head = head.next;
        return x;
    }

} // End of class Queue