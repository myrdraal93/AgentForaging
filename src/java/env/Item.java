package env;

import java.util.ArrayList;
import java.util.List;

public class Item {

	private double weight;
	private double pheromone;
	private List<Integer> agents;
	
	public Item(double weight){
		this.weight=weight;
		pheromone=10;
		agents=new ArrayList<>();
	}
	
	public double getWeight(){
		return weight;
	}
	
	public double getPheromone(){
		return pheromone;
	}
	
	public void addPheromone(){
		pheromone+=5;
	}
	
	public void evaporatePheromone(){
		pheromone*=0.9;
	}
	
	public void addAgent(int agent,double strength){
		agents.add(agent);
		weight-=strength;
		//System.out.println("Peso rimasto: "+weight);
	}
	
	public List<Integer> getAgents(){
		return agents;
	}
}
