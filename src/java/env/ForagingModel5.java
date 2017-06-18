package env;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import jason.environment.grid.Location;
import jason.util.Pair;

public class ForagingModel5 extends ForagingModel{

	protected double [][] tauToFood,tauToNest;
	
	protected ForagingModel5(boolean flag) {
		super(flag);
	}
	
	public synchronized void updatePheromone(int agent,boolean searchFood){
		Location position=getAgPos(agent);
			
		if(searchFood){
			if(tauToNest[position.x][position.y]+pheromone<10000){
				tauToNest[position.x][position.y]+=pheromone;
			}else{
				tauToNest[position.x][position.y]=10000;
			}
		}else{
			if(tauToFood[position.x][position.y]+pheromone<10000){
				tauToFood[position.x][position.y]+=pheromone;
			}else{
				tauToFood[position.x][position.y]=10000;
			}
		}
	}
	
	public synchronized Location getNextPosition(boolean searchFood,int agent){
		Location position=getAgPos(agent);
		return montecarlo(calculateGrade(position.x,position.y,searchFood));
	}
	
	
	protected List<Pair<Double,Location>> calculateGrade(int x, int y,boolean searchFood){
		ArrayList<Pair<Double,Location>> val=new ArrayList<>();
		double tau[][]=searchFood?tauToFood:tauToNest;
		Location location;
		
		if(!searchFood || !flag){
			if((x-1)>=0){
				location=new Location(x-1,y);
				val.add(evaluatePosition(location,tau));
				
				if((y-1)>=0){
					location=new Location(x-1,y-1);
					val.add(evaluatePosition(location,tau));	
				}
				
				if((y+1)<SIZE){
					location=new Location(x-1,y+1);
					val.add(evaluatePosition(location,tau));
				}
				
			}
		}
		
		if((y-1)>=0){
			location=new Location(x,y-1);
			val.add(evaluatePosition(location,tau));
		}
		
		if((y+1)<SIZE){
			location=new Location(x,y+1);
			val.add(evaluatePosition(location,tau));
		}
		
		if(searchFood || !flag){
			if((x+1)<SIZE){
				location=new Location(x+1,y);
				val.add(evaluatePosition(location,tau));
				
				if((y-1)>=0){
					location=new Location(x+1,y-1);
					val.add(evaluatePosition(location,tau));
				}
				
				if((y+1)<SIZE){
					location=new Location(x+1,y+1);
					val.add(evaluatePosition(location,tau));
				}
				
			}
		}
		
		return val;
	}
	
	protected Pair<Double,Location> evaluatePosition(Location location,double [][]tau){
		
		if(agentInWallArea(location)){
			return new Pair<>(0.0,location);
		}
		
		return new Pair<>(tau[location.x][location.y],location);
	}
	
	
	protected synchronized void evaporatePheromone(){
		for(int column=0;column<SIZE;column++){
			for(int row=0;row<SIZE;row++){
				tauToFood[column][row]*=rho;
				tauToNest[column][row]*=rho;
			}
		}
		
		IntStream.range(0,item4824.size()).forEach(i->item4824.get(i).evaporatePheromone());
		IntStream.range(0,item4825.size()).forEach(i->item4825.get(i).evaporatePheromone());
		IntStream.range(0,item4924.size()).forEach(i->item4924.get(i).evaporatePheromone());
		IntStream.range(0,item4925.size()).forEach(i->item4925.get(i).evaporatePheromone());
	}
	
	protected void initializeACO(){
		
		tauToFood=new double[SIZE][SIZE];
		tauToNest=new double[SIZE][SIZE];
		
		for (int column = 0; column < SIZE; column++)
        {
            for (int row = 0; row < SIZE; row++)
            {
            	tauToFood[column][row]=10;
            	tauToNest[column][row]=10;
            }
        }
	}
}
