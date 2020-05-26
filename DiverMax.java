package student;

import java.util.*;

import game.GetOutState;
import game.Tile;
import game.FindState;
import game.SewerDiver;
import game.Node;
import game.NodeStatus;
import game.Edge;

public class DiverMax extends SewerDiver {
	
	HashSet <Long> visited1 = new HashSet <Long> ();		//nodes visited in findRing stage
	HashSet <Node> visited2 = new HashSet <Node> ();		//nodes visited in getOut stage

    /** Get to the ring in as few steps as possible. Once you get there, 
     * you must return from this function in order to pick
     * it up. If you continue to move after finding the ring rather 
     * than returning, it will not count.
     * If you return from this function while not standing on top of the ring, 
     * it will count as a failure.
     * 
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the ring in fewer steps.
     * 
     * At every step, you know only your current tile's ID and the ID of all 
     * open neighbor tiles, as well as the distance to the ring at each of these tiles
     * (ignoring walls and obstacles). 
     * 
     * In order to get information about the current state, use functions
     * currentLocation(), neighbors(), and distanceToRing() in FindState.
     * You know you are standing on the ring when distanceToRing() is 0.
     * 
     * Use function moveTo(long id) in FindState to move to a neighboring 
     * tile by its ID. Doing this will change state to reflect your new position.
     * 
     * A suggested first implementation that will always find the ring, but likely won't
     * receive a large bonus multiplier, is a depth-first walk. Some
     * modification is necessary to make the search better, in general.*/
    @Override public void findRing(FindState state) {
        //TODO : Find the ring and return.
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method elsewhere, with a good specification,
        // and call it from this one.
    	dfsWalk2(state);
        
    }


    /** Get out of the sewer system before the steps are all used, trying to collect
     * as many coins as possible along the way. Your solution must ALWAYS get out
     * before the steps are all used, and this should be prioritized above
     * collecting coins.
     * 
     * You now have access to the entire underlying graph, which can be accessed
     * through GetOutState. currentNode() and getExit() will return Node objects
     * of interest, and getNodes() will return a collection of all nodes on the graph. 
     * 
     * You have to get out of the sewer system in the number of steps given by
     * getStepsRemaining(); for each move along an edge, this number is decremented
     * by the weight of the edge taken.
     * 
     * Use moveTo(n) to move to a node n that is adjacent to the current node.
     * When n is moved-to, coins on node n are automatically picked up.
     * 
     * You must return from this function while standing at the exit. Failing to
     * do so before steps run out or returning from the wrong node will be
     * considered a failed run.
     * 
     * Initially, there are enough steps to get from the starting point to the
     * exit using the shortest path, although this will not collect many coins.
     * For this reason, a good starting solution is to use the shortest path to
     * the exit. */
    @Override public void getOut(GetOutState state) {
        //TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        //with a good specification, and call it from this one.
    	getOut4(state);

    }


    /**
     * Basic findRing function. Uses simple dfs to find ring Node. Returns when standing on square with ring.
     * Uses hashSet to keep track of Nodes visited. 
     * @param state
     */
	private void dfsWalk1 (FindState state) {
		long current = state.currentLocation();
		visited1.add(current);
		for (NodeStatus w : state.neighbors()) {
			long newPlace = w.getId();
			if (!visited1.contains(newPlace)){
				visited1.add(newPlace);
				state.moveTo(newPlace);
				if (state.distanceToRing() == 0) { return;}
				dfsWalk1(state);
				if (state.distanceToRing() == 0) { return;}
				state.moveTo(current);
		 	}
		}	
	}

	private void dfsWalk2 (FindState state) {
	
		long current = state.currentLocation();
		visited1.add(current);
		for (NodeStatus w : neighborsOrdered(state.neighbors(), state)) {	
			long newPlace = w.getId();
			if (!visited1.contains(newPlace)){
				visited1.add(newPlace);
				state.moveTo(newPlace);
				if (state.distanceToRing() == 0) { return;}
				dfsWalk1(state);
				if (state.distanceToRing() == 0) { return;}
				state.moveTo(current);
		 	}
		}	
	}
	
	/**
	 * Basic getOut function. Uses minPath to get shortest path to exit Node from current and follows that. 
	 * @param state
	 */
	private void getOut1 (GetOutState state) {
		//find minimum path from current node to exit node
		List <Node> shortestPath = Paths.minPath (state.currentNode(), state.getExit());
		for (int i = 1; i < shortestPath.size(); i++) {
			state.moveTo(shortestPath.get(i));
		}
			
		return;
	}
	/**
	 * Optimized getOut function. Chooses next Node to move to as neighbor with lowest edge weight,
	 * minimizing steps traveled
	 * @param state
	 */
	private void getOut2 (GetOutState state) {
		boolean gottaGetOutOfHere = false;		
		while (!gottaGetOutOfHere) {
			Iterator <Node> nextIte = state.currentNode().getNeighbors().iterator();
			//for (Node square : exitOrder(state.currentNode().getNeighbors(), state)) 
			//	if (!visited2.contains(square)) { next = square; break;	}
			Node next = nextIte.next();
			if (!gottaGetOutOfHere ) {
				visited2.add(next);
				state.moveTo(next);
				if (Paths.minPath(state.currentNode(), state.getExit()).size() > state.stepsLeft()) gottaGetOutOfHere = true;
				}	
			
		}
		List <Node> shortestPath = Paths.minPath (state.currentNode(), state.getExit());
		for (int i = 1; i < shortestPath.size(); i++) 
			state.moveTo(shortestPath.get(i));
		return;
		
		}
	/**
	 * Takes in Set of neighbors of current node and orders them by increasing edge weight
	 * @param neighbors: allow access to edge weights of neighbors
	 * @param state: allow access to currentNode()
	 * @return neighborsList: list of neighbors in increasing edge weight
	 */
	private List <NodeStatus> neighborsOrdered (Collection <NodeStatus> neighbors, FindState state){
		List <NodeStatus> neighborsList = new LinkedList <NodeStatus> ();
		neighborsList.addAll(neighbors);
		int size = neighborsList.size();
		for (int i = 1; i < size; i++) {
				if (neighborsList.get(i-1).getDistanceToTarget() > neighborsList.get(i).getDistanceToTarget()) {
					NodeStatus temp = neighborsList.get(i);
					neighborsList.set(i, neighborsList.get(i-1));
					neighborsList.set(i-1, temp);
			}
		}
		return neighborsList;
	}

	

private List <NodeStatus> ringOrder (List <NodeStatus> neighbors){
	int size = neighbors.size();
	for (int i = 1; i < size; i++) {
		NodeStatus min = neighbors.get(i);
		for (int j = i-1; j >= 0; j--)  {
			//////////////? this whole sort algorithm
		}
			/*if (neighbors.get(j).compareTo(min) < 0) {
				neighbors.set(i, neighbors.get(j));
				neighbors.set(j, min);*/
		}
	
	return neighbors;
}
/*
private void getOut3 (GetOutState state) {
	boolean gottaGetOutOfHere = false;		
	while (!gottaGetOutOfHere) {
		Node next = exitOrder(state.currentNode().getNeighbors(), state).get(0);
		for (Node square : exitOrder(state.currentNode().getNeighbors(), state)) 
			if (!visited2.contains(square)) { next = square; break;	}
		if (!gottaGetOutOfHere ) {
			visited2.add(next);
			if ((Paths.pathDistance(Path(state.currentNode(), state.getExit()))) >= state.stepsLeft()-20) escape(state);

			state.moveTo(next);
			}	
		
	}
	escape(state);
	return;
	
	}*/

private void getOut4 (GetOutState state) {
	Iterator <Node> next = state.allNodes().iterator();
	while (true) {
		Node rand = next.next();
		List <Node> shortestPath = Paths.minPath (state.currentNode(), rand);
		for (int i = 1; i < shortestPath.size(); i++) {
			if ((Paths.pathDistance(Paths.minPath(state.currentNode(), state.getExit()))) + 100> state.stepsLeft()) {escape(state); return;}

			state.moveTo(shortestPath.get(i));
		}
	}
}
	
	
	
/**to get max out of sewers when their steps are running out*/	
private void escape (GetOutState state) {

	List <Node> shortestPath = Paths.minPath (state.currentNode(), state.getExit());
	for (int i = 1; i < shortestPath.size(); i++) 
		state.moveTo(shortestPath.get(i));
	return;

}
}



