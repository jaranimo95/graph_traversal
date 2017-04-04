/******************************************************************************
 *  Compilation:  javac DijkstraSP.java
 *  Execution:    java DijkstraSP input.txt s
 *  Dependencies: EdgeWeightedDigraph.java IndexMinPQ.java Stack.java DirectedEdge.java
 *  Data files:   http://algs4.cs.princeton.edu/44sp/tinyEWD.txt
 *                http://algs4.cs.princeton.edu/44sp/mediumEWD.txt
 *                http://algs4.cs.princeton.edu/44sp/largeEWD.txt
 *
 ******************************************************************************/


/**
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @editor Christian Jarani
 */
import dependencies.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class DijkstraSP {
    private double[] distTo;          // distTo[v] = distance  of shortest s->v path
    private Edge[] edgeTo;            // edgeTo[v] = last edge on shortest s->v path
    private IndexMinPQ<Double> pq;    // priority queue of vertices

    private final double copperSpeed = 230000000;
    private final double opticalSpeed = 200000000;

    /**
     * Computes a shortest-paths tree from the source vertex {@code s} to every other
     * vertex in the edge-weighted digraph {@code G}.
     *
     * @param  G the edge-weighted digraph
     * @param  s the source vertex
     * @throws IllegalArgumentException if an edge weight is negative
     * @throws IllegalArgumentException unless {@code 0 <= s < V}
     */
    public DijkstraSP(EdgeWeightedGraph G, int s) {
        for (Edge e : G.edges()) {
            if (e.getBandwidth() < 0)
                throw new IllegalArgumentException("edge " + e + " has negative weight");
        }

        distTo = new double[G.V()];
        edgeTo = new Edge[G.V()];

        validateVertex(s);

        for (int v = 0; v < G.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
            distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(G.V());
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (Edge e : G.adj(v))
                relax(e);
        }

        // check optimality conditions
        assert check(G, s);
    }

    // relax edge e and update pq if changed
    private void relax(Edge e) {
        int v = e.either(), w = e.other(v);
        double speed = 0;

             if (e.getType().compareTo("copper")  == 0) speed = copperSpeed;
        else if (e.getType().compareTo("optical") == 0) speed = opticalSpeed;

        double latency = e.getLength() / speed;

        if (distTo[w] > distTo[v] + latency) {
            distTo[w] = distTo[v] + latency;
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }

    /**
     * Returns the length of a shortest path from the source vertex {@code s} to vertex {@code v}.
     * @param  v the destination vertex
     * @return the length of a shortest path from the source vertex {@code s} to vertex {@code v};
     *         {@code Double.POSITIVE_INFINITY} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public double distTo(int v) {
        validateVertex(v);
        return distTo[v];
    }

    /**
     * Returns true if there is a path from the source vertex {@code s} to vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return {@code true} if there is a path from the source vertex
     *         {@code s} to vertex {@code v}; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean hasPathTo(int v) {
        validateVertex(v);
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path from the source vertex {@code s} to vertex {@code v}.
     *
     * @param  v the destination vertex
     * @return a shortest path from the source vertex {@code s} to vertex {@code v}
     *         as an iterable of edges, and {@code null} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Edge> pathTo(int v) {
        validateVertex(v);
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[e.either()]) {
            path.push(e);
        }
        return path;
    }


    // check optimality conditions:
    // (i) for all edges e:            distTo[e.to()] <= distTo[e.from()] + e.weight()
    // (ii) for all edge e on the SPT: distTo[e.to()] == distTo[e.from()] + e.weight()
    private boolean check(EdgeWeightedGraph G, int s) {
        
        double speed = 1.00;
        double latency;

        // check that edge weights are nonnegative
        for (Edge e : G.edges()) {

                 if (e.getType().compareTo("copper")  == 0) speed = copperSpeed;
            else if (e.getType().compareTo("optical") == 0) speed = opticalSpeed;

            latency = e.getLength() / speed;
            
            if (latency < 0) {
                System.err.println("negative edge weight detected");
                return false;
            }
        }

        // check that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null) {
            System.err.println("distTo[s] and edgeTo[s] inconsistent");
            return false;
        }
        for (int v = 0; v < G.V(); v++) {
            if (v == s) continue;
            if (edgeTo[v] == null && distTo[v] != Double.POSITIVE_INFINITY) {
                System.err.println("distTo[] and edgeTo[] inconsistent");
                return false;
            }
        }

        // check that all edges e = v->w satisfy distTo[w] <= distTo[v] + e.weight()
        for (int v = 0; v < G.V(); v++) {
            for (Edge e : G.adj(v)) {
                     
                     if (e.getType().compareTo("copper")  == 0) speed = copperSpeed;
                else if (e.getType().compareTo("optical") == 0) speed = opticalSpeed;
                latency = e.getLength() / speed;
                
                int w = e.other(v);
                if (distTo[v] + latency < distTo[w]) {
                    System.err.println("edge " + e + " not relaxed");
                    return false;
                }
            }
        }

        // check that all edges e = v->w on SPT satisfy distTo[w] == distTo[v] + e.weight()
        for (int w = 0; w < G.V(); w++) {
            if (edgeTo[w] == null) continue;
            Edge e = edgeTo[w];
            int v = e.either();

                 if (e.getType().compareTo("copper")  == 0) speed = copperSpeed;
            else if (e.getType().compareTo("optical") == 0) speed = opticalSpeed;
            latency = e.getLength() / speed;

            if (w != e.other(v)) return false;
            if (distTo[v] + latency != distTo[w]) {
                System.err.println("edge " + e + " on shortest path not tight");
                return false;
            }
        }
        return true;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = distTo.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Unit tests the {@code DijkstraSP} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner reader = new Scanner(new File(args[0]));
        
        EdgeWeightedGraph g = new EdgeWeightedGraph(reader.nextInt()); // Set scanner to read in info from file
        int v,w;
        String type;
        int bandwidth;
        double length;

        while(reader.hasNextLine()) {    // Reads in all data in from file, abiding by the predetermined format
            v = reader.nextInt();
            if (v < 0 || v >= g.V())
                throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (g.V()-1));
            w = reader.nextInt();
            if (w < 0 || w >= g.V())
                throw new IllegalArgumentException("vertex " + w + " is not between 0 and " + (g.V()-1));
                type = reader.next();
           bandwidth = reader.nextInt();
              length = reader.nextDouble();
            System.out.println("V: "+v+"\nW: "+w+"\nEdge Type: "+type+"\nBandwidth: "+bandwidth+"\nLength: "+length+"\n");
            g.addEdge(new Edge(v, w, type, bandwidth, length));
        }
        int s = 0;      // Instead of inputting vertices from the command line, just edit them here. This is the source vertex
        int d = 3;      // Same with this one. This is the destination vertex

        // compute shortest paths
        DijkstraSP sp = new DijkstraSP(g, s);

        // print shortest path
        if (sp.hasPathTo(d)) {
                StdOut.printf("%d to %d (%.2f)  ", s, d, sp.distTo(d));
                for (Edge e : sp.pathTo(d)) {
                    StdOut.print(e + "   ");
                }
                StdOut.println();
        }
        else StdOut.printf("%d to %d         no path\n", s, d);
    }

}