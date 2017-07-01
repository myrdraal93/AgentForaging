package env;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jason.environment.grid.Location;
import jason.util.Pair;

public class Item {

	private double weight;
	private double pheromone;
	private HashMap<Integer,ArrayList<Pair<Pair<Location,String>,Double>>> agents;
	
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
		if(pheromone+5<=1000){
			pheromone+=5;
		}else{
			pheromone=1000;
		}
	}
	
	public void evaporatePheromone(){
		pheromone*=0.7;
	}
	
	public boolean addItemPosition(int agent,Pair<Pair<Location,String>,Double> position){
		agents.get(agent).add(position);
		for(ArrayList<Pair<Pair<Location,String>,Double>>tmp:agents.values()){
			if(tmp.size()==0){
				return false;
			}
		}
		return true;
	}
	
	public Pair<Location,String> getNextPosition(){
		HashMap<Pair<Location,String>,Double> coordinate=new HashMap<>();
    	
		for(ArrayList<Pair<Pair<Location,String>,Double>> tmp:agents.values()){
    		Pair<Pair<Location,String>,Double> pair=tmp.remove(0);
    		if(coordinate.containsKey(pair.getFirst())){
    			double force=coordinate.get(pair.getFirst());
    			coordinate.put(pair.getFirst(),pair.getSecond()+force);
    		}else{
    			coordinate.put(pair.getFirst(),pair.getSecond());
    		}
    	}
		
		double value=-1;
		Pair<Location,String> max=null;
		Object[] set=coordinate.keySet().toArray();
		
		for(Object tmp : set){
			
			if(coordinate.get((Pair<Location,String>)tmp)>value){
				value=coordinate.get((Pair<Location,String>)tmp);
				max=(Pair<Location,String>)tmp;
			}
		
		}
		
		for(Object tmp : set){
			
			if(((Pair<Location,String>)tmp).getFirst().x!=max.getFirst().x||((Pair<Location,String>)tmp).getFirst().y!=max.getFirst().y){
				value-=coordinate.get((Pair<Location,String>)tmp);
			}
			
		}
		
		if(value==0){
			return new Pair<>(new Location(-1,-1),"\"\"");
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
