// Just about all the methods other than main are identical to the unit tests of the classes they utilize.
// Support for unit testing was kept intact within the original files themselves, so if you'd like to test
// 	a particular feature, you can compile/run them directly without having to navigate through the UI in this one.

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
			/*else if (choice == 4) // lowestAvgLatencyMST();
			else if (choice == 5) // articulationPoints();*/
			else if (choice == 6) break;
		}
		//reader.close();
	}

	// latency = length / bandwidth
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
                    StdOut.print(e + "   " + bandwidths[i] + " | ");
                }
                StdOut.println();
        }
        else StdOut.printf("%d to %d         no path\n", v, w);

        // Fix this
        Arrays.sort(bandwidths);
        System.out.println("Minimum Bandwidth: "+bandwidths[0]);
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
        StdOut.println();

        StdOut.println("Max flow value = " +  maxflow.value()+"\n");

        /*MaxDijkstraSP sp = new MaxDijkstraSP(g, v);

        if (sp.hasPathTo(w)) {
                StdOut.printf("%d to %d (%.2f)  ", v, w, sp.distTo(w));
                for (Edge e : sp.pathTo(w)) {
                    StdOut.print(e + "   ");
                }
                StdOut.println();
        }
        else StdOut.printf("%d to %d         no path\n", v, w);*/
	}

	private static void lowestAvgLatencyMST(EdgeWeightedGraph g) {
		
	}

	//private static void articulationPoints(EdgeWeightedGraph g) {}
}