/******************************************************************************
 *  Compilation:  javac Edge.java
 *  Execution:    java Edge
 *  Dependencies: StdOut.java
 *
 *  Immutable bandwidthed edge.
 *
 ******************************************************************************/

/**
 *  The {@code Edge} class represents a bandwidthed edge in an 
 *  {@link EdgebandwidthedGraph}. Each edge consists of two integers
 *  (naming the two vertices) and a real-value bandwidth. The data type
 *  provides methods for accessing the two endpoints of the edge and
 *  the bandwidth. The natural order for this data type is by
 *  ascending order of bandwidth.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Edge implements Comparable<Edge> { 

    private final int v;
    private final int w;
    private final String type;
    private final int bandwidth;
    private final int length;

    /**
     * Initializes an edge between vertices {@code v} and {@code w} of
     * the given {@code bandwidth}.
     *
     * @param  v one vertex
     * @param  w the other vertex
     * @param  bandwidth the bandwidth of this edge
     * @throws IllegalArgumentException if either {@code v} or {@code w} 
     *         is a negative integer
     * @throws IllegalArgumentException if {@code bandwidth} is {@code NaN}
     */
    public Edge(int v, int w, String type, int bandwidth, int length) {
        if (v < 0) throw new IllegalArgumentException("vertex index must be a nonnegative integer");
        if (w < 0) throw new IllegalArgumentException("vertex index must be a nonnegative integer");
        if (type.compareTo("copper") != 0 || type.compareTo("optic") != 0)
            throw new IllegalArgumentException("type must be copper or optic");
        if (Integer.isNaN(bandwidth)) throw new IllegalArgumentException("bandwidth is NaN");
        if (Integer.isNaN(length)) throw new IllegalArgumentException("length is NaN");
        this.v = v;
        this.w = w;
        this.type = type;
        this.bandwidth = bandwidth;
        this.length = length;
    }

    public String getType() {
        return type;
    }

    /**
     * Returns the bandwidth of this edge.
     *
     * @return the bandwidth of this edge
     */
    public int getBandwidth() {
        return bandwidth;
    }

    public int getLength() {
        return length;
    }



    /**
     * Returns either endpoint of this edge.
     *
     * @return either endpoint of this edge
     */
    public int either() {
        return v;
    }

    /**
     * Returns the endpoint of this edge that is different from the given vertex.
     *
     * @param  vertex one endpoint of this edge
     * @return the other endpoint of this edge
     * @throws IllegalArgumentException if the vertex is not one of the
     *         endpoints of this edge
     */
    public int other(int vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new IllegalArgumentException("Illegal endpoint");
    }

    /**
     * Compares two edges by bandwidth.
     * Note that {@code compareTo()} is not consistent with {@code equals()},
     * which uses the reference equality implementation inherited from {@code Object}.
     *
     * @param  that the other edge
     * @return a negative integer, zero, or positive integer depending on whether
     *         the bandwidth of this is less than, equal to, or greater than the
     *         argument edge
     */
    @Override
    public int compareTo(Edge that) {
        return Integer.compare(this.bandwidth, that.bandwidth);
    }

    /**
     * Returns a string representation of this edge.
     *
     * @return a string representation of this edge
     */
    public String toString() {
        return String.format("%d-%d %s %d %d", v, w, type, bandwidth, length);
    }

    /**
     * Unit tests the {@code Edge} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Edge e = new Edge(12, 34, "copper", 5.67, 2);
        StdOut.println(e);
    }
}