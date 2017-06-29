package env;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import jason.environment.grid.Location;
import jason.util.Pair;

public class ForagingModel3 extends ForagingModel{

	protected double [][] tau,etaToFood,etaToNest;
	
	protected ForagingModel3(int value) {
		super(value);
	}
	
	public synchronized void updatePheromone(int agent){
		Location position=getAgPos(agent);
			
		if(tau[position.x][position.y]+PHEROMONE<MAX_PHEROMONE){
			tau[position.x][position.y]+=PHEROMONE;
		}else{
			tau[position.x][position.y]=MAX_PHEROMONE;
		}
	}
	
	public synchronized Pair<Location,String> getNextPosition(double alpha,double beta,boolean searchFood,int agent,String from){
		Location position=getAgPos(agent);
		return montecarlo(calculateGrade(position.x,position.y,alpha,beta,searchFood,from));
	}
	
	
	protected List<Pair<Double,Pair<Location,String>>> calculateGrade(int x, int y,double alpha,double beta,boolean searchFood,String from){
		ArrayList<Pair<Double,Pair<Location,String>>> val=new ArrayList<>();
		double eta[][]=searchFood?etaToFood:etaToNest;
		
		if(value>=1){
			if(!searchFood || !flag){
				addLocation(x-1,y,val,"\"\"",false,eta,alpha,beta);
				addLocation(x-1,y-1,val,"\"\"",false,eta,alpha,beta);
				addLocation(x-1,y+1,val,"\"\"",false,eta,alpha,beta);
			}
			
			addLocation(x,y-1,val,"\"\"",false,eta,alpha,beta);
			addLocation(x,y+1,val,"\"\"",false,eta,alpha,beta);
			
			if(searchFood || !flag){
				addLocation(x+1,y,val,"\"\"",false,eta,alpha,beta);
				addLocation(x+1,y-1,val,"\"\"",false,eta,alpha,beta);
				addLocation(x+1,y+1,val,"\"\"",false,eta,alpha,beta);
			}	
		}else{
			if(from.equals("\"O\"")){
				addLocation(x+1,y,val,"\"O\"",true,eta,alpha,beta);
				addLocation(x+1,y-1,val,"\"O\"",true,eta,alpha,beta);
				addLocation(x+1,y+1,val,"\"O\"",true,eta,alpha,beta);
				
				if(val.size()==0){
					addLocation(x,y-1,val,"\"S\"",true,eta,alpha,beta);
					addLocation(x,y+1,val,"\"N\"",true,eta,alpha,beta);
				}
				
				if(val.size()==0){
					addLocation(x-1,y,val,"\"E\"",true,eta,alpha,beta);
					addLocation(x-1,y-1,val,"\"E\"",true,eta,alpha,beta);
					addLocation(x-1,y+1,val,"\"E\"",true,eta,alpha,beta);
				}
			}else if(from.equals("\"E\"")){
				addLocation(x-1,y,val,"\"E\"",true,eta,alpha,beta);
				addLocation(x-1,y-1,val,"\"E\"",true,eta,alpha,beta);
				addLocation(x-1,y+1,val,"\"E\"",true,eta,alpha,beta);
				
				if(val.size()==0){
					addLocation(x,y-1,val,"\"S\"",true,eta,alpha,beta);
					addLocation(x,y+1,val,"\"N\"",true,eta,alpha,beta);	
				}
				
				if(val.size()==0){
					addLocation(x+1,y,val,"\"O\"",true,eta,alpha,beta);
					addLocation(x+1,y-1,val,"\"O\"",true,eta,alpha,beta);	
					addLocation(x+1,y+1,val,"\"O\"",true,eta,alpha,beta);
				}
			}else if(from.equals("\"N\"")){
				addLocation(x,y+1,val,"\"N\"",true,eta,alpha,beta);
				addLocation(x-1,y+1,val,"\"N\"",true,eta,alpha,beta);
				addLocation(x+1,y+1,val,"\"N\"",true,eta,alpha,beta);
					
				if(val.size()==0){
					addLocation(x+1,y,val,"\"O\"",true,eta,alpha,beta);
					addLocation(x-1,y,val,"\"E\"",true,eta,alpha,beta);	
				}
				
				if(val.size()==0){
					addLocation(x,y-1,val,"\"S\"",true,eta,alpha,beta);
					addLocation(x+1,y-1,val,"\"S\"",true,eta,alpha,beta);
					addLocation(x-1,y-1,val,"\"S\"",true,eta,alpha,beta);
				}
				
			}else if(from.equals("\"S\"")){
				addLocation(x,y-1,val,"\"S\"",true,eta,alpha,beta);
				addLocation(x-1,y-1,val,"\"S\"",true,eta,alpha,beta);
				addLocation(x+1,y-1,val,"\"S\"",true,eta,alpha,beta);
				
				if(val.size()==0){
					addLocation(x-1,y,val,"\"E\"",true,eta,alpha,beta);
					addLocation(x+1,y,val,"\"O\"",true,eta,alpha,beta);
				}
				
				if(val.size()==0){
					addLocation(x,y+1,val,"\"N\"",true,eta,alpha,beta);
					addLocation(x-1,y+1,val,"\"N\"",true,eta,alpha,beta);
					addLocation(x+1,y+1,val,"\"N\"",true,eta,alpha,beta);
				}
			}
		}
	
		return val;
	}
	
	private void addLocation(int x,int y,List<Pair<Double,Pair<Location,String>>> val,String direction,boolean flag,double [][] eta,double alpha,double beta){
		if(x>=0&&x<SIZE&&y>=0&&y<SIZE){
			Location location=new Location(x,y);
			Pair<Double,Location> tmp=evaluatePosition(location, eta,alpha, beta);
		
			if(flag){
				if(tmp.getFirst()>0){
					val.add(new Pair<>(tmp.getFirst(),new Pair<>(tmp.getSecond(),direction)));
				}
			}else{
				val.add(new Pair<>(tmp.getFirst(),new Pair<>(tmp.getSecond(),direction)));
			}
		}
	}
	
	protected Pair<Double,Location> evaluatePosition(Location location,double [][]eta,double alpha,double beta){
		
		if(agentInWallArea(location)){
			return new Pair<>(0.0,location);
		}

		double value;
		
		value=Math.pow(eta[location.x][location.y],beta)*
			Math.pow((tau[location.x][location.y]+1),alpha);
		
		return new Pair<>(value,location);
	}
	
	
	protected synchronized void evaporatePheromone(){
		for(int column=0;column<SIZE;column++){
			for(int row=0;row<SIZE;row++){
				tau[column][row]*=RHO;
			}
		}
		
		IntStream.range(0,item4824.size()).forEach(i->item4824.get(i).evaporatePheromone());
		IntStream.range(0,item4825.size()).forEach(i->item4825.get(i).evaporatePheromone());
		IntStream.range(0,item4924.size()).forEach(i->item4924.get(i).evaporatePheromone());
		IntStream.range(0,item4925.size()).forEach(i->item4925.get(i).evaporatePheromone());
	}
	
	protected void initializeACO(){
		
		double[][] distanceToFood=new double[SIZE][SIZE];
		double[][] distanceToNest=new double[SIZE][SIZE];
		tau=new double[SIZE][SIZE];
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
	                	etaToFood[column][row]+= maxDistanceToFood - distanceToFood[column][row];
	                	etaToNest[column][row]+= maxDistanceToNest - distanceToNest[column][row];
	                }
	            }
			}
		}
	}
	
}
