// Just about all the methods other than main are identical to the unit tests of the classes they utilize.
// Support for unit testing was kept intact within the original files themselves, so if you'd like to test
// 	a particular feature, you can compile/run them directly without having to navigate through the UI in this one.
import dependencies.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class NetworkAnalysis {
	
	public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException {

		Scanner reader = new Scanner(new File(args[0]));
		
		//System.out.println("---Network Info---");
		EdgeWeightedGraph g = new EdgeWeightedGraph(reader.nextInt()); // Set scanner to read in info from file
		//System.out.println("# of Vertices: "+g.V()+"\n");
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
		    //System.out.println("V: "+v+"\nW: "+w+"\nEdge Type: "+type+"\nBandwidth: "+bandwidth+"\nLength: "+length+"\n");
		    g.addEdge(new Edge(v, w, type, bandwidth, length));
		}

		reader = new Scanner(System.in); // Reset scanner to act as our input reader from the keyboard

		int choice;
		while(true) {  // Program Loop
			System.out.println("What would you like to do?");
			System.out.println("\t1. Find Lowest Latency Path\n\t2. Determine Copper-Only Connection\n\t3. Find Max Bandwidth Path\n\t4. Find Lowest Avg. Latency ST\n\t5. Determine Articulation Points\n\t6. Quit\n");
			while (true) { // Ensures valid input
				System.out.print("Select a number to choose an option: ");
				choice = reader.nextInt();
				if(choice < 1 || choice > 6) 
				 	System.out.println("\n!! Invalid choice - please select an option between 1 and 6. !!\n");
				else {
					reader.nextLine();
					break;
				}
			}    
				 if (choice == 1) lowestLatencyPath(g);
			else if (choice == 2) copperOnlyConnection(g);
			else if (choice == 3) maxFlow(g);
			else if (choice == 4) lowestAvgLatencyMST(g);
			else if (choice == 5) articulationPoints(g);
			else if (choice == 6) break;
		}
	}

	// latency = length / bandwidth
	// PROBLEM: won't find path if v > w, probably only checking adj[v] instead of adj[v] and adj[w]
	private static void lowestLatencyPath(EdgeWeightedGraph g) throws IllegalArgumentException {
		
		Scanner reader = new Scanner(System.in);
		int v, w;

		System.out.print("Please enter the starting vertex: ");
		v = reader.nextInt();
		if (v < 0 || v >= g.V())
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (g.V()-1));
		System.out.print("Please enter the ending vertex: ");
		w = reader.nextInt();
		if (w < 0 || w >= g.V())
			throw new IllegalArgumentException("vertex " + w + " is not between 0 and " + (g.V()-1));
		
		DijkstraSP dsp = new DijkstraSP(g,v);
		int[] bandwidths = new int[g.E()];
		int i = 0;

        // print shortest path
        if (dsp.hasPathTo(w)) {
                StdOut.printf("\n%d to %d (%.2f)  ", v, w, dsp.distTo(w));
                for (Edge e : dsp.pathTo(w)) {
                    bandwidths[i] = e.getBandwidth();
                    StdOut.print(e + "   ");
                }
                StdOut.println();
        }
        else StdOut.printf("%d to %d         no path\n", v, w);

        // Fix this
        Arrays.sort(bandwidths);
        System.out.println("Minimum Bandwidth: "+bandwidths[0]+"\n");
    }

	private static void copperOnlyConnection(EdgeWeightedGraph g) {
		
		CopperConnected cc = new CopperConnected(g);

        // number of connected components
        int m = cc.count();
        if(m > 1) System.out.println("Graph is not copper connected:");
        else System.out.println("Graph is copper connected:");
        StdOut.println(m + " components");

        // compute list of vertices in each connected component
        Queue<Integer>[] components = (Queue<Integer>[]) new Queue[m];
        for (int i = 0; i < m; i++) {
            components[i] = new Queue<Integer>();
        }
        for (int j = 0; j < g.V(); j++) {
            components[cc.id(j)].enqueue(j);
        }

        // print results
        for (int i = 0; i < m; i++) {
            for (int j : components[i]) {
                StdOut.print(j + " ");
            }
            StdOut.println();
        }
	}
	// PROBLEM: won't find path if v > w, probably only checking adj[v] instead of adj[v] and adj[w]
	private static void maxFlow(EdgeWeightedGraph g) {

		FlowNetwork f = new FlowNetwork(g.V());
		Iterable<Edge> edges = g.edges();
		for(Edge e : edges) 
			f.addEdge( new FlowEdge(e.either(),e.other(e.either()),e.getBandwidth()) );
        
		Scanner reader = new Scanner(System.in);

        System.out.print("Please enter the starting vertex: ");
		int v = reader.nextInt();
		if (v < 0 || v >= f.V())
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (f.V()-1));
		System.out.print("Please enter the ending vertex: ");
		int w = reader.nextInt();
		if (w < 0 || w >= f.V())
			throw new IllegalArgumentException("vertex " + w + " is not between 0 and " + (f.V()-1));

		FordFulkerson maxflow = new FordFulkerson(f, v, w);
        StdOut.println("\nMax flow from " + v + " to " + w);
        for (int j = 0; j < f.V(); j++) {
            for (FlowEdge e : f.adj(j)) {
                if ((j == e.from()) && e.flow() > 0)
                    StdOut.println("   " + e);
            }
        }

        // print min-cut
        StdOut.print("Min cut: ");
        for (int i = 0; i < f.V(); i++) {
            if (maxflow.inCut(i)) StdOut.print(i + " ");
        }
        StdOut.println("\nMax flow value = " +  maxflow.value()+"\n");
	}

	private static void lowestAvgLatencyMST(EdgeWeightedGraph g) {
		System.out.println("\nLowest Average Latency ST:");
		PrimMST mst = new PrimMST(g);
		for (Edge e : mst.edges()) {
            StdOut.println(e);
        }
        StdOut.printf("%.5f\n\n", mst.weight());
	}

	private static void articulationPoints(EdgeWeightedGraph g) {
		
		boolean flag = false; // Used so we don't check vertices twice since our edges are duplex (01 and 10, 21 and 12, etc.)
		EdgeWeightedGraph u;
		CC cc;
		int[] vertsToRemove = new int[2];

		for(int i = 0; i < g.V() - 1; i++) {	   // For all but the last vertex..
			u = new EdgeWeightedGraph(g.V());	   // Create a new graph. This will represent our original graph without the vertices that we wish to remove
			vertsToRemove[0] = i;				   // Signal to remove vertex i from the graph,
			for(int j = 1; j < g.V(); j++) {	   // And for all but the first vertex...
				vertsToRemove[1] = j;		   	   // Signal to remove vertex j from the graph.
				for(Edge e : g.edges()) {	   	   // Now iterate over all the edges of the original graph:
				   								   // If an edge connects to one of the vertices we want to remove, we don't add that edge to the graph, thus, removing the vertex from consideration.
					if(vertsToRemove[0] == e.either() || vertsToRemove[1] == e.either() || vertsToRemove[0] == e.other(e.either()) || vertsToRemove[1] == e.other(e.either())) 
						continue;
					else u.addEdge(e);
				}
				cc = new CC(u);
				// number of connected components
		        int m = cc.count();
		        if(m > 1) System.out.println("Graph is not connected after vertices "+vertsToRemove[0]+" and "+vertsToRemove[1]+" were removed:");
		        else System.out.println("Graph remains connected after all vertex pairs were removed:");
		        StdOut.println(m + " components");

		        // compute list of vertices in each connected component
		        Queue<Integer>[] components = (Queue<Integer>[]) new Queue[m];
		        for (int i = 0; i < m; i++) {
		            components[i] = new Queue<Integer>();
		        }
		        for (int j = 0; j < g.V(); j++) {
		            components[cc.id(j)].enqueue(j);
		        }

		        // print results
		        for (int i = 0; i < m; i++) {
		            for (int j : components[i]) {
		                StdOut.print(j + " ");
		            }
		            StdOut.println();
		        }
			}
		}
	}
}
