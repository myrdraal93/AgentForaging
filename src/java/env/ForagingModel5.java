package env;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import jason.environment.grid.Location;
import jason.util.Pair;

public class ForagingModel5 extends ForagingModel{

	protected double [][] tauToFood,tauToNest;
	
	protected ForagingModel5(int value) {
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
	
	public synchronized Location getNextPosition(boolean searchFood,int agent){
		Location position=getAgPos(agent);
		return montecarlo(calculateGrade(position.x,position.y,searchFood));
	}
	
	
	protected List<Pair<Double,Location>> calculateGrade(int x, int y,boolean searchFood){
		ArrayList<Pair<Double,Location>> val=new ArrayList<>();
		double tau[][]=searchFood?tauToFood:tauToNest;
		Location location;
		
		if(value>=1){
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
		}else if(value==0){
			
			Pair<Double,Location> tmp=null;
			
			if(searchFood){
				if((x+1)<SIZE){
					location=new Location(x+1,y);
					tmp=evaluatePosition(location,tau);
					
					if(tmp.getFirst()>0 && getAgAtPos(location)<10){
						val.add(tmp);
					}
					
					if((y-1)>=0){
						location=new Location(x+1,y-1);
						tmp=evaluatePosition(location,tau);
						
						if(tmp.getFirst()>0 && getAgAtPos(location)<10){
							val.add(tmp);
						}
					}
					
					if((y+1)<SIZE){
						location=new Location(x+1,y+1);
						tmp=evaluatePosition(location,tau);
						
						if(tmp.getFirst()>0 && getAgAtPos(location)<10){
							val.add(tmp);
						}
					}
					
					if(val.size()==0){
						
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
						
						if((y-1)>=0){
							location=new Location(x,y-1);
							val.add(evaluatePosition(location,tau));
						}
						
						if((y+1)<SIZE){
							location=new Location(x,y+1);
							val.add(evaluatePosition(location,tau));
						}	
					}
					
				}else{
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
					
					if((y-1)>=0){
						location=new Location(x,y-1);
						val.add(evaluatePosition(location,tau));
					}
					
					if((y+1)<SIZE){
						location=new Location(x,y+1);
						val.add(evaluatePosition(location,tau));
					}
					
				}
			}else{
				if((x-1)>=0){
					location=new Location(x-1,y);
					
					tmp=evaluatePosition(location,tau);
					
					if(tmp.getFirst()>0 && getAgAtPos(location)<10){
						val.add(tmp);
					}
					
					if((y-1)>=0){
						location=new Location(x-1,y-1);
						tmp=evaluatePosition(location,tau);
						
						if(tmp.getFirst()>0 && getAgAtPos(location)<10){
							val.add(tmp);
						}
					}
					
					if((y+1)<SIZE){
						location=new Location(x-1,y+1);
						tmp=evaluatePosition(location,tau);
						
						if(tmp.getFirst()>0 && getAgAtPos(location)<10){
							val.add(tmp);
						}
					}
					
					if(val.size()==0){
						
						if((y-1)>=0){
							location=new Location(x,y-1);
							val.add(evaluatePosition(location,tau));
						}
						
						if((y+1)<SIZE){
							location=new Location(x,y+1);
							val.add(evaluatePosition(location,tau));
						}
						
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
					
				}else{
					
					if((y-1)>=0){
						location=new Location(x,y-1);
						val.add(evaluatePosition(location,tau));
					}
					
					if((y+1)<SIZE){
						location=new Location(x,y+1);
						val.add(evaluatePosition(location,tau));
					}
					
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
			}
		}else{
			
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
	
	protected synchronized void evaporatePheromoneInArea(int column,int rowStart,int rowEnd){
		/*for(int i=column-3;i<=column+3;i++){
			for(int j=rowStart;j<=rowEnd;j++){
				if(i==column-3||i==column+3){
					tauToFood[i][j]=MIN_PHEROMONE+40;
                	tauToNest[i][j]=MIN_PHEROMONE+40;
				}else if(i==column-2||i==column+2){
					tauToFood[i][j]=MIN_PHEROMONE+20;
                	tauToNest[i][j]=MIN_PHEROMONE+20;
				}else if(i==column-1||i==column+1){
					tauToFood[i][j]=MIN_PHEROMONE;
                	tauToNest[i][j]=MIN_PHEROMONE;
				}
				
				tauToFood[i][j]=PHEROMONE;
            	tauToNest[i][j]=PHEROMONE;
			}
		}*/
	}
}
