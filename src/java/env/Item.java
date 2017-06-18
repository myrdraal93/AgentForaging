package env;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jason.environment.grid.Location;
import jason.util.Pair;

public class Item {

	private double weight;
	private double pheromone;
	private HashMap<Integer,ArrayList<Pair<Location,Double>>> agents;
	
	public Item(double weight){
		this.weight=weight;
		pheromone=10;
		agents=new HashMap<>();
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
		pheromone*=0.7;
	}
	
	public boolean addItemPosition(int agent,Pair<Location,Double> position){
		agents.get(agent).add(position);
		for(ArrayList<Pair<Location,Double>>tmp:agents.values()){
			if(tmp.size()==0){
				return false;
			}
		}
		return true;
	}
	
	public Location getNextPosition(){
		HashMap<Location,Double> coordinate=new HashMap<>();
    	
    	for(ArrayList<Pair<Location,Double>> tmp:agents.values()){
    		Pair<Location,Double> pair=tmp.remove(0);
    		if(coordinate.containsKey(pair.getFirst())){
    			double force=coordinate.get(pair.getFirst());
    			coordinate.put(pair.getFirst(),pair.getSecond()+force);
    		}else{
    			coordinate.put(pair.getFirst(),pair.getSecond());
    		}
    	}
		
		double value=-1;
		Location max=null;
		Object[] set=coordinate.keySet().toArray();
		
		for(Object tmp : set){
			
			if(coordinate.get((Location)tmp)>value){
				value=coordinate.get((Location)tmp);
				max=(Location)tmp;
			}
		
		}
		
		for(Object tmp : set){
			
			if(((Location)tmp).x!=max.x||((Location)tmp).y!=max.y){
				value-=coordinate.get((Location)tmp);
			}
			
		}
		
		if(value==0){
			return new Location(-1,-1);
		}
		
		return max;
	}
	
	public void addAgent(int agent,double strength){
		agents.put(agent,new ArrayList<>());
		weight-=strength;
	}
	
	public int getNumberAgents(){
		return agents.size();
	}
	
	public List<Integer> getAgents(){
		return new ArrayList<>(agents.keySet());
	}
}
