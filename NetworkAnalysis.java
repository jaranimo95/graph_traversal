import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class NetworkAnalysis {
	
	public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException {

		Scanner reader = new Scanner(new File(args[0]));
		
		System.out.println("---Network Info---");
		EdgeWeightedGraph g = new EdgeWeightedGraph(reader.nextInt()); // Set scanner to read in info from file
		System.out.println("# of Vertices: "+g.V()+"\n");
		int v,w;
		String type;
		int bandwidth;
		int length;

		while(reader.hasNextLine()) {    // Reads in all data in from file, abiding by the predetermined format
				   v = reader.nextInt();
				   if (v < 0 || v >= g.V())
				   	throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (g.V()-1));
				   w = reader.nextInt();
				   if (w < 0 || w >= g.V())
				   	throw new IllegalArgumentException("vertex " + w + " is not between 0 and " + (g.V()-1));
				type = reader.next();
		   bandwidth = reader.nextInt();
		      length = reader.nextInt();
		    System.out.println("V: "+v+"\nW: "+w+"\nEdge Type: "+type+"\nBandwidth: "+bandwidth+"\nLength: "+length+"\n");
		    g.addEdge(new Edge(v, w, type, bandwidth, length));
		}

		reader = new Scanner(System.in); // Reset scanner to act as our input reader from the keyboard

		int choice;
		while(true) {  // Program Loop
			System.out.println("What would you like to do?");
			System.out.println("\t1. Find Lowest Latency Path\n\t2. Determine Copper-Only Connection\n\t3. Find Max Bandwidth Path\n\t4. Find Lowest Avg. Latency MST\n\t5. Determine Articulation Points\n\t6. Quit\n");
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
			/*else if (choice == 2) // copperOnlyConnection();
			else if (choice == 3) // maxBandwidthPath();
			else if (choice == 4) // lowestAvgLatencyMST();
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

        // Work on this later
        Arrays.sort(bandwidths);
        System.out.println("Minimum Bandwidth: "+bandwidths[0]);
	}
}