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
	
	public void updatePheromone(int agent,boolean searchFood){
		/*synchronized(*/lock.lock();//){
			Location position;
			
			position=getAgentPosition(agent);
			//synchronized(lockAgent){
				//position=getAgPos(agent);
			//}
			
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
			lock.unlock();
		//}
	}
	
	public Location getNextPosition(boolean searchFood,int ant){
		Location position;
		position=getAgentPosition(ant);
		//synchronized(lockAgent){
			//position=getAgPos(ant);
		//}
		Location location=montecarlo(calculateGrade(position.x,position.y,searchFood));
		return location;
	}
	
	
	protected List<Pair<Double,Location>> calculateGrade(int x, int y,boolean searchFood){
		ArrayList<Pair<Double,Location>> val=new ArrayList<>();
		
		double tau[][];
		
		/*synchronized(*/lock.lock();//){
			tau=searchFood?tauToFood:tauToNest;
			lock.unlock();
		//}
		
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
		
		if(antInWallArea(location)){
			return new Pair<>(0.0,location);
		}
		
		lock.lock();
		
		double value=tau[location.x][location.y];
		
		lock.unlock();
		
		return new Pair<>(value,location);
		
	}
	
	
	protected void evaporatePheromone(){
		/*synchronized(*/lock.lock();//){
			for(int column=0;column<SIZE;column++){
				for(int row=0;row<SIZE;row++){
					tauToFood[column][row]*=rho;
					tauToNest[column][row]*=rho;
				}
			}
			
			lock.unlock();
		//}
		
		/*synchronized(*/lockFood.lock();//){
			IntStream.range(0,item4824.size()).forEach(i->item4824.get(i).evaporatePheromone());
			IntStream.range(0,item4825.size()).forEach(i->item4825.get(i).evaporatePheromone());
			IntStream.range(0,item4924.size()).forEach(i->item4924.get(i).evaporatePheromone());
			IntStream.range(0,item4925.size()).forEach(i->item4925.get(i).evaporatePheromone());
			lockFood.unlock();
		//}
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
