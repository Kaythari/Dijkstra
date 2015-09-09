import java.io.*;
import java.util.*;
/**
 * This program implements a graph data structure of cities and roads information to find connections between two cities. 
 * According to user's desired method call, the system operates the functions like finding shortest path, inserting new path and deleting a road. 
 * The process include implementations like priority queue, dijkstra's algorithm and so on. 
 * @author Kaythari Phon
 */
public class ShortestPath
{
	public static Digraph[] digraph = new Digraph[20];
	/**
	 * In the main method, the program access and reads in the files, then performs the command a user enters by calling the methods 
	 * then prints out information to the console.	 
	 * @param args
	 */
	public static void main(String[] args)
	{	//File readers to read the city files
		File cityInfo = new File("city.dat"); 
		File roadInfo = new File("road.dat"); 
				
		try
		{
			Scanner file1 = new Scanner(cityInfo);
			for(int i = 0; i < 20; i++)
			{	//read in the file, tokenizer is used to separate the space
				String temp = file1.nextLine(); 
				StringTokenizer in = new StringTokenizer(temp, " ");
				String token1 = in.nextToken();
				String token2 = in.nextToken();
				String token3 = in.nextToken();
				
				if(in.countTokens() == 3)
					token3 = token3 + " " + in.nextToken();
				
				String token4 = in.nextToken();
				String token5 = in.nextToken();
				digraph[i] = new Digraph(Integer.parseInt(token1), token2, token3, Integer.parseInt(token4), Integer.parseInt(token5));
			}
			file1.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found."); 
		}
		
		try
		{
			Scanner file2 = new Scanner(roadInfo);
			while(file2.hasNextLine())
			{
				String temp = file2.nextLine(); 
				StringTokenizer in = new StringTokenizer(temp, " ");
				int token1 = Integer.parseInt(in.nextToken()) - 1; //because our index starts from 0
				int token2 = Integer.parseInt(in.nextToken()) - 1;
				int token3 = Integer.parseInt(in.nextToken());
				digraph[token1].setEdge(token1, token2, token3);
			}
		file2.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found."); 
		}

		Scanner keyboard = new Scanner(System.in);
		boolean endLoop = false;
		do
		{
			System.out.print("Command? "); 
			String userInput = keyboard.nextLine();
			String token1, token2, token3;
			StringTokenizer token;
			switch(userInput.toUpperCase())
			{
				case "Q":
					System.out.print("City code: " );
					String code = keyboard.nextLine();
					Query(code.toUpperCase()); 
					break; 
				case "D": 
					System.out.print("City codes: ");
					code = keyboard.nextLine();
					token = new StringTokenizer(code, " "); 
					if (token.countTokens() != 2)
					{
						System.out.println("Invaid input."); 
						break; 
					}
					token1 = token.nextToken(); 
					token2 = token.nextToken();
					Dijkstras(token1.toUpperCase(), token2.toUpperCase());
					break; 
				case "I":
					System.out.print("City codes and distance: ");
					code = keyboard.nextLine();
					token = new StringTokenizer(code, " "); 
					if (token.countTokens() != 3)
					{
						System.out.println("Invaid input."); 
						break; 
					}
					token1 = token.nextToken(); 
					token2 = token.nextToken();
					token3 = token.nextToken();
					Insert(token1.toUpperCase(), token2.toUpperCase(), Integer.parseInt(token3.toUpperCase())); 
					break; 
				case "R":
					System.out.print("City codes: ");
					code = keyboard.nextLine();
					token = new StringTokenizer(code, " "); 
					if (token.countTokens() != 2)
					{
						System.out.println("Invaid input."); 
						break; 
					}
					token1 = token.nextToken(); 
					token2 = token.nextToken();
					Remove(token1.toUpperCase(), token2.toUpperCase()); 
					break; 
				case "H":
					System.out.println(" Q Query the city information by entering the city code.\n D Find the minimum distance between two cities."
							+ "\n I Insert a road by entering two city codes and distance.\n R Remove an existing road by entering two city codes."
							+ "\n H Display this message.\n E Exit.");
					break; 
				case "E": 
					endLoop = true;
					break; 
				default: 
					System.out.println("Invalid input.");
					break;
			}
		}
		while(!endLoop);
		keyboard.close();
	}
	
	/**
	 * Accept a city code and prints out its designated information 
	 * @param cityCode
	 */
	public static void Query(String cityCode)
	{
		int i = getCityLocation(cityCode); 
		if(i != -1)
			System.out.println(digraph[i].num + " " + digraph[i].cityCode + " " + digraph[i].cityName + " " + digraph[i].population + " " + digraph[i].elevation);
		else 
			System.out.println("Invalid Input."); 
	}
	
	/**
	 * Getting the position the city codes are stored in which are form index 0 - 19
	 * @param cityCode
	 * @return city code at index i, otherwise, not found. 
	 */
	public static int getCityLocation(String cityCode)
	{
		for (int i = 0; i < 20; i++)
		{
			if (cityCode.equals(digraph[i].cityCode)) //check if city code is equal to the city code at i position
			{
				return i; 
			}
		}
		return -1; //city not found 
	}
	
	/**
	 * Dijkstaras method finds the shortest route from one city to another by using priority queue 
	 * It approaches the solution by storing the vertex in the queue and continue updating after checking for minimum distance between two cities
	 * @param city1
	 * @param city2
	 */
	public static void Dijkstras(String city1, String city2)
	{
		int i = getCityLocation(city1); 
		int j = getCityLocation(city2); 
		if (i != -1 && j!= -1) //if there are invalid city codes, would not run
		{
			int[] prev = new int[20]; //use previous store the paths
			for (int k = 0; k < 20; k++) 
			{
				digraph[k].weight = Integer.MAX_VALUE; //set weight to infinity
				prev[k] = -1; // -1 indicates undefined 
				
			}
			digraph[i].weight = 0; //setting the source's weight to zero
			PriorityQueue<Digraph> Q = new PriorityQueue<Digraph>(); //store digraph in the order of lowest to highest weights
			Q.add(digraph[i]); //add the source in first
			while (!Q.isEmpty())
			{
				Digraph u = Q.poll(); //take out the first elements in the queue 
				Queue<Integer> neighbors = new LinkedList<Integer>(); 
				for (int n = 0; n < 20; n++) //to get the neighbors of u
				{
					if(digraph[u.num-1].edges[u.num-1][n] != 0) //u = location, minus 1 to get index, if the edge of that vertex is not equal to zero, then it is a neighbor 
						neighbors.add(n);
				}
				while(!neighbors.isEmpty()) 
				{
					Digraph v = digraph[neighbors.poll()]; //getting the neighbor of the source 
					int newDistance = u.weight + digraph[u.num - 1].edges[u.num-1][v.num-1]; //to find distance from one vertex to another + its weight
					
					if(newDistance < v.weight)//compare to weight of the vertex that i'm measuring
					{
						v.weight = newDistance;
						prev[v.num-1] = u.num-1; //updating the previous
						Q.add(v);
					}
				}	
			}
			Stack<String> path = new Stack<String>(); //to obtain shortest path 
			
			int currentIndex = j; //backtracking to the source
			while(prev[currentIndex] != -1) // -1 indicates there's no more previous city
			{
				path.add(digraph[currentIndex].cityName); //getting the name of all the cities need to pass thru
				currentIndex = prev[currentIndex]; //backtracking previous cities to find the route
			}
			//print out the city at the source because it is not included since it's the first city w/o prev
			System.out.print("The minimum distance between " + digraph[i].cityName + " and " + digraph[j].cityName + " is " + digraph[j].weight + " through the route: " + digraph[currentIndex].cityName); 
			//pop cities to print
			
			while(!path.isEmpty())
				System.out.print(", " + path.pop());
			System.out.print(".\n"); 
		}
		else
			System.out.println("Invalid city code!!");
	}
	
	/**
	 * Accepts two cities input and a distance, check whether if there's a path between the two 
	 * If there is a path, a road already exists, otherwise, insert.
	 * @param city1
	 * @param city2
	 * @param newDistance
	 */
	public static void Insert(String city1, String city2, int newDistance)
	{
		int i = getCityLocation(city1); 
		int j = getCityLocation(city2); 
		if (i != -1 && j!= -1) //if there are no cities, would not run
		{
			if (digraph[i].edges[i][j] == 0) //check if there is a road between two cities 
			{
				digraph[i].edges[i][j] = newDistance; //update the new distance
				System.out.println("You have inserted a road from " + digraph[i].cityName 
						+ " to " + digraph[j].cityName + " with a distance of " + newDistance + "."); 
			}
			else 
				System.out.println("The raod from " + digraph[i].cityName + " to " + digraph[j].cityName + " already existed!"); 
			}
		}
	/**
	 * Accepts two cities input and check whether if there's a path between the two
	 * If there is path, remove, otherwise, there is no road. 
	 * @param city1
	 * @param city2
	 */
	public static void Remove(String city1, String city2) 
	{
		int i = getCityLocation(city1); 
		int j = getCityLocation(city2); 
		if (i != -1 && j!= -1) //if there are no cities, would not run
		{
			if (digraph[i].edges[i][j] != 0) //check if there is a road between two cities 
			{
				digraph[i].edges[i][j] = 0; //set zero to remove 
				System.out.println("The road from " + digraph[i].cityName + " and " +  digraph[j].cityName + " has been removed."); 
			}
			else 
				System.out.println("The road from " + digraph[i].cityName + " to " + digraph[j].cityName + " doesn't exist."); 
			}
	}
}
/**
 * Digraph stores the data of cities and road information and compareTo method to overwrite the weight
 */
class Digraph implements Comparable<Digraph>
{
	 int weight, num, population, elevation; 
	 String cityCode, cityName; 
	 
	 int[][] edges = new int[20][20]; //2-D arrays that stores road info
	 
	 public Digraph(int num, String cityCode, String cityName, int pop, int ele)
	 {
		 this.num = num; 
		 this.population = pop; 
		 this.elevation = ele; 
		 this.cityCode = cityCode; 
		 this.cityName = cityName; 
	 }
	 
	 public void setEdge(int i, int j, int distance)
	 {
		 edges[i][j] = distance;
	 }
	
	//overwrites to compare the weight of a city to another instead of the digraph object (cities)
	public int compareTo(Digraph otherVertex) 
	{
		if (weight == otherVertex.weight)
			return 0;
		else 
			return (weight < otherVertex.weight) ? -1:1; //-1 smaller, 1 bigger, 0 equal, condition to return values
	}
}
