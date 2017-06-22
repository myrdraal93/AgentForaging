package env;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import jason.environment.grid.Location;
import jason.util.Pair;

public class ForagingModel1 extends ForagingModel{

	protected double [][] tauToFood,tauToNest,etaToFood,etaToNest;
	
	protected ForagingModel1(int value) {
		super(value);
	}
	
	public synchronized void updatePheromone(int agent,boolean searchFood){
		Location position=getAgPos(agent);
		
		if(searchFood){
			if(tauToNest[position.x][position.y]+PHEROMONE<MAX_PHEROMONE){
				tauToNest[position.x][position.y]+=PHEROMONE;
			}else{
				tauToNest[position.x][position.y]=MAX_PHEROMONE;
			}
		}else{
			if(tauToFood[position.x][position.y]+PHEROMONE<MAX_PHEROMONE){
				tauToFood[position.x][position.y]+=PHEROMONE;
			}else{
				tauToFood[position.x][position.y]=MAX_PHEROMONE;
			}
		}
	}
	
	public synchronized Location getNextPosition(double alpha,double beta,boolean searchFood,int agent){
		Location position=getAgPos(agent);
		Location location=montecarlo(calculateGrade(position.x,position.y,alpha,beta,searchFood));
		return location;
	}
	
	
	protected List<Pair<Double,Location>> calculateGrade(int x, int y,double alpha,double beta,boolean searchFood){
		ArrayList<Pair<Double,Location>> val=new ArrayList<>();
		
		double eta[][]=searchFood?etaToFood:etaToNest;
		double tau[][];
		
		tau=searchFood?tauToFood:tauToNest;
		
		Location location;
		
		if(value>=1){
			if(!searchFood || !flag){
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
			
			if(searchFood || !flag){
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
		}else if(value==0){
			
			Pair<Double,Location> tmp=null;
			
			if(searchFood){
				if((x+1)<SIZE){
					location=new Location(x+1,y);
					tmp=evaluatePosition(location, eta, tau, alpha,beta);
					
					if(tmp.getFirst()>0 && getAgAtPos(location)<10){
						val.add(tmp);
					}
					
					if((y-1)>=0){
						location=new Location(x+1,y-1);
						tmp=evaluatePosition(location, eta, tau, alpha,beta);
						
						if(tmp.getFirst()>0 && getAgAtPos(location)<10){
							val.add(tmp);
						}
					}
					
					if((y+1)<SIZE){
						location=new Location(x+1,y+1);
						tmp=evaluatePosition(location, eta, tau, alpha,beta);
						
						if(tmp.getFirst()>0 && getAgAtPos(location)<10){
							val.add(tmp);
						}
					}
					
					if(val.size()==0){
						
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
						
						if((y-1)>=0){
							location=new Location(x,y-1);
							val.add(evaluatePosition(location, eta, tau, alpha, beta));
						}
						
						if((y+1)<SIZE){
							location=new Location(x,y+1);
							val.add(evaluatePosition(location, eta, tau, alpha, beta));
						}	
					}
					
				}else{
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
					
					if((y-1)>=0){
						location=new Location(x,y-1);
						val.add(evaluatePosition(location, eta, tau, alpha, beta));
					}
					
					if((y+1)<SIZE){
						location=new Location(x,y+1);
						val.add(evaluatePosition(location, eta, tau, alpha, beta));
					}
					
				}
			}else{
				if((x-1)>=0){
					location=new Location(x-1,y);
					
					tmp=evaluatePosition(location, eta, tau, alpha,beta);
					
					if(tmp.getFirst()>0 && getAgAtPos(location)<10){
						val.add(tmp);
					}
					
					if((y-1)>=0){
						location=new Location(x-1,y-1);
						tmp=evaluatePosition(location, eta, tau, alpha,beta);
						
						if(tmp.getFirst()>0 && getAgAtPos(location)<10){
							val.add(tmp);
						}
					}
					
					if((y+1)<SIZE){
						location=new Location(x-1,y+1);
						tmp=evaluatePosition(location, eta, tau, alpha,beta);
						
						if(tmp.getFirst()>0 && getAgAtPos(location)<10){
							val.add(tmp);
						}
					}
					
					if(val.size()==0){
						
						if((y-1)>=0){
							location=new Location(x,y-1);
							val.add(evaluatePosition(location, eta, tau, alpha, beta));
						}
						
						if((y+1)<SIZE){
							location=new Location(x,y+1);
							val.add(evaluatePosition(location, eta, tau, alpha, beta));
						}
						
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
					
				}else{
					
					if((y-1)>=0){
						location=new Location(x,y-1);
						val.add(evaluatePosition(location, eta, tau, alpha, beta));
					}
					
					if((y+1)<SIZE){
						location=new Location(x,y+1);
						val.add(evaluatePosition(location, eta, tau, alpha, beta));
					}
					
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
			}
		}else{
			
		}
		
		return val;
	}
	
	protected Pair<Double,Location> evaluatePosition(Location location,double [][]eta,double [][]tau,double alpha,double beta){
		
		if(agentInWallArea(location)){
			return new Pair<>(0.0,location);
		}
		
		
		return new Pair<>(Math.pow(eta[location.x][location.y],beta)*
				Math.pow(tau[location.x][location.y],alpha),location);
	}
	
	
	protected synchronized void evaporatePheromone(){
		for(int column=0;column<SIZE;column++){
			for(int row=0;row<SIZE;row++){
				tauToFood[column][row]*=RHO;
				tauToNest[column][row]*=RHO;
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
	                	tauToFood[column][row]=PHEROMONE;
	                	tauToNest[column][row]=PHEROMONE;
	                	etaToFood[column][row]+= maxDistanceToFood - distanceToFood[column][row];
	                	etaToNest[column][row]+= maxDistanceToNest - distanceToNest[column][row];
	                }
	            }
			}
		}
	}
	
	protected synchronized void evaporatePheromoneInArea(int column,int rowStart,int rowEnd){
	/*	for(int i=column-3;i<=column+3;i++){
			for(int j=rowStart;j<=rowEnd;j++){
				if(i==column-3||i==column+3){
					tauToFood[i][j]=PHEROMONE+40;
                	tauToNest[i][j]=PHEROMONE+40;
				}else if(i==column-2||i==column+2){
					tauToFood[i][j]=PHEROMONE+20;
                	tauToNest[i][j]=PHEROMONE+20;
				}else if(i==column-1||i==column+1){
					tauToFood[i][j]=PHEROMONE;
                	tauToNest[i][j]=PHEROMONE;
				}
				tauToFood[i][j]=PHEROMONE;
            	tauToNest[i][j]=PHEROMONE;
			}
		}*/
	}
}
