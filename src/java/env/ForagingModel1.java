package env;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import jason.environment.grid.Location;
import jason.util.Pair;

public class ForagingModel1 extends ForagingModel{

	protected double [][] tauToFood,tauToNest,etaToFood,etaToNest;
	
	protected ForagingModel1() {
		super();
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
	
	public Location getNextPosition(double alpha,double beta,boolean searchFood,int ant){
		Location position;
		position=getAgentPosition(ant);
		//synchronized(lockAgent){
			//position=getAgPos(ant);
		//}
		Location location=montecarlo(calculateGrade(position.x,position.y,alpha,beta,searchFood));
		return location;
	}
	
	
	protected List<Pair<Double,Location>> calculateGrade(int x, int y,double alpha,double beta,boolean searchFood){
		ArrayList<Pair<Double,Location>> val=new ArrayList<>();
		
		double eta[][]=searchFood?etaToFood:etaToNest;
		double tau[][];
		
		/*synchronized(*/lock.lock();//){
			tau=searchFood?tauToFood:tauToNest;
			lock.unlock();
		//}
		
		Location location;
		
		if(!searchFood){
			if((x-1)>=0){
				location=new Location(x-1,y);
				val.add(evaluatePosition(location, eta, tau, alpha, beta));
				
				if((y-1)>=0){
					location=new Location(x-1,y-1);
					val.add(evaluatePosition(location, eta, tau, alpha, beta));
					
				}
				
				if((y+1)<SIZE){
					location=new Location(x-1,y+1);
					val.add(evaluatePosition(location, eta, tau, alpha, beta));
				}
				
			}
		}
		
		if((y-1)>=0){
			location=new Location(x,y-1);
			val.add(evaluatePosition(location, eta, tau, alpha, beta));
			
		}
		
		if((y+1)<SIZE){
			location=new Location(x,y+1);
			val.add(evaluatePosition(location, eta, tau, alpha, beta));
		}
		
		if(searchFood){
			if((x+1)<SIZE){
				location=new Location(x+1,y);
				val.add(evaluatePosition(location, eta, tau, alpha,beta));
				
				if((y-1)>=0){
					location=new Location(x+1,y-1);
					val.add(evaluatePosition(location, eta, tau, alpha, beta));
					
				}
				
				if((y+1)<SIZE){
					location=new Location(x+1,y+1);
					val.add(evaluatePosition(location, eta, tau, alpha, beta));
				}
				
			}
		}
		
		return val;
	}
	
	protected Pair<Double,Location> evaluatePosition(Location location,double [][]eta,double [][]tau,double alpha,double beta){
		
		if(antInWallArea(location)){
			return new Pair<>(0.0,location);
		}
		
		lock.lock();
		
		double value=Math.pow(eta[location.x][location.y],beta)*
				Math.pow(tau[location.x][location.y],alpha);
		
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
		
		double[][] distanceToFood=new double[SIZE][SIZE];
		double[][] distanceToNest=new double[SIZE][SIZE];
		tauToFood=new double[SIZE][SIZE];
		tauToNest=new double[SIZE][SIZE];
		etaToFood=new double[SIZE][SIZE];
		etaToNest=new double[SIZE][SIZE];
		
		for(int j=48;j<=49;j++){
			for(int i=24;i<=25;i++){
				
				for(int column=0;column<SIZE;column++){
					for(int row=0;row<SIZE;row++){
						distanceToFood[column][row]=Math.abs(j-column)+Math.abs(i-row);
						distanceToNest[column][row]=Math.abs((j-48)-column)+Math.abs(i-row);
					}
				}
				
				double maxDistanceToFood=0,maxDistanceToNest=0;
				
				for (int k = 0; k < SIZE; k++)
	            {
	                for (int l = 0; l < SIZE; l++)
	                {
	                    if (maxDistanceToFood < distanceToFood[k][l]){
	                    	maxDistanceToFood = distanceToFood[k][l];
	                    }
	                    
	                    if (maxDistanceToNest < distanceToNest[k][l]){
	                    	maxDistanceToNest = distanceToNest[k][l];
	                    }
	                }

	            }
				
	            
	            for (int column = 0; column < SIZE; column++)
	            {
	                for (int row = 0; row < SIZE; row++)
	                {
	                	tauToFood[column][row]=10;
	                	tauToNest[column][row]=10;
	                	etaToFood[column][row]+= maxDistanceToFood - distanceToFood[column][row];
	                	etaToNest[column][row]+= maxDistanceToNest - distanceToNest[column][row];
	                }
	            }
			}
		}
	}
}
