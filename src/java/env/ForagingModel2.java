package env;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import jason.environment.grid.Location;
import jason.util.Pair;

public class ForagingModel2 extends ForagingModel{

	protected double [][] tauToFood,tauToNest;
	
	protected ForagingModel2(int value) {
		super(value);
	}
	
	public synchronized void updatePheromone(int agent,boolean searchFood){
		Location position=getAgPos(agent);
			
		if(searchFood){
			if(!inNestArea(position)||value>=1){
				if(tauToNest[position.x][position.y]+PHEROMONE<MAX_PHEROMONE){
					tauToNest[position.x][position.y]+=PHEROMONE;
				}else{
					tauToNest[position.x][position.y]=MAX_PHEROMONE;
				}
			}
		}else{
			if(!inItemArea(position)||value>=1){
				if(tauToFood[position.x][position.y]+PHEROMONE<MAX_PHEROMONE){
					tauToFood[position.x][position.y]+=PHEROMONE;
				}else{
					tauToFood[position.x][position.y]=MAX_PHEROMONE;
				}
			}
		}
	}
	
	public synchronized Pair<Location,String> getNextPosition(boolean searchFood,int agent,String from){
		Location position=getAgPos(agent);
		return montecarlo(calculateGrade(position.x,position.y,searchFood,from));
	}
	
	
	protected List<Pair<Double,Pair<Location,String>>> calculateGrade(int x, int y,boolean searchFood,String from){
		ArrayList<Pair<Double,Pair<Location,String>>> val=new ArrayList<>();
		double tau[][]=searchFood?tauToFood:tauToNest;
		
		if(value>=1){
			if(!searchFood || !flag){
				addLocation(x-1,y,val,"\"\"",false,tau);
				addLocation(x-1,y-1,val,"\"\"",false,tau);
				addLocation(x-1,y+1,val,"\"\"",false,tau);
			}	
				
			addLocation(x,y-1,val,"\"\"",false,tau);
			addLocation(x,y+1,val,"\"\"",false,tau);
			
			if(searchFood || !flag){
				addLocation(x+1,y,val,"\"\"",false,tau);
				addLocation(x+1,y-1,val,"\"\"",false,tau);
				addLocation(x+1,y+1,val,"\"\"",false,tau);
			}	
		}else{
			if(from.equals("\"O\"")){
				addLocation(x+1,y,val,"\"O\"",true,tau);
				addLocation(x+1,y-1,val,"\"O\"",true,tau);
				addLocation(x+1,y+1,val,"\"O\"",true,tau);
				
				if(val.size()==0){
					addLocation(x,y-1,val,"\"S\"",true,tau);
					addLocation(x,y+1,val,"\"N\"",true,tau);
				}
				
				if(val.size()==0){
					addLocation(x-1,y,val,"\"E\"",true,tau);
					addLocation(x-1,y-1,val,"\"E\"",true,tau);
					addLocation(x-1,y+1,val,"\"E\"",true,tau);
				}
			}else if(from.equals("\"E\"")){
				addLocation(x-1,y,val,"\"E\"",true,tau);
				addLocation(x-1,y-1,val,"\"E\"",true,tau);	
				addLocation(x-1,y+1,val,"\"E\"",true,tau);
				
				if(val.size()==0){
					addLocation(x,y-1,val,"\"S\"",true,tau);
					addLocation(x,y+1,val,"\"N\"",true,tau);
				}
				
				if(val.size()==0){
					addLocation(x+1,y,val,"\"O\"",true,tau);
					addLocation(x+1,y-1,val,"\"O\"",true,tau);
					addLocation(x+1,y+1,val,"\"O\"",true,tau);
				}
			}else if(from.equals("\"N\"")){
				addLocation(x,y+1,val,"\"N\"",true,tau);
				addLocation(x-1,y+1,val,"\"N\"",true,tau);
				addLocation(x+1,y+1,val,"\"N\"",true,tau);
			
				if(val.size()==0){
					addLocation(x+1,y,val,"\"O\"",true,tau);
					addLocation(x-1,y,val,"\"E\"",true,tau);
				}
				
				if(val.size()==0){
					addLocation(x,y-1,val,"\"S\"",true,tau);
					addLocation(x+1,y-1,val,"\"S\"",true,tau);
					addLocation(x-1,y-1,val,"\"S\"",true,tau);
				}
				
			}else if(from.equals("\"S\"")){
				addLocation(x,y-1,val,"\"S\"",true,tau);
				addLocation(x-1,y-1,val,"\"S\"",true,tau);
				addLocation(x+1,y-1,val,"\"S\"",true,tau);
				
				if(val.size()==0){	
					addLocation(x-1,y,val,"\"E\"",true,tau);
					addLocation(x+1,y,val,"\"O\"",true,tau);
				}
				
				if(val.size()==0){
					addLocation(x,y+1,val,"\"N\"",true,tau);
					addLocation(x-1,y+1,val,"\"N\"",true,tau);
					addLocation(x+1,y+1,val,"\"N\"",true,tau);
				}
			}
		}
		
			
		return val;
	}
	
	private void addLocation(int x,int y,List<Pair<Double,Pair<Location,String>>> val,String direction,boolean flag,double[][]tau){
		if(x>=0&&x<SIZE&&y>=0&&y<SIZE){
			Location location=new Location(x,y);
			Pair<Double,Location> tmp=evaluatePosition(location,tau);
		
			if(flag){
				if(tmp.getFirst()>0){
					val.add(new Pair<>(tmp.getFirst(),new Pair<>(tmp.getSecond(),direction)));
				}
			}else{
				val.add(new Pair<>(tmp.getFirst(),new Pair<>(tmp.getSecond(),direction)));
			}
		}
	}
	
	protected Pair<Double,Location> evaluatePosition(Location location,double [][]tau){
		
		if(agentInWallArea(location)){
			return new Pair<>(0.0,location);
		}
		
		return new Pair<>((tau[location.x][location.y]+1),location);
	}
	
	
	protected synchronized void evaporatePheromone(){
		for(int column=0;column<SIZE;column++){
			for(int row=0;row<SIZE;row++){
				
				if(value!=0){
					tauToFood[column][row]*=RHO;
					tauToNest[column][row]*=RHO;	
				}else{
					if(!((column==0||column==1)&&(row==24||row==25))&&!((column==2&&(row==23||row==24||row==25||row==26))||((column==0||column==1)&&(row==23||row==26)))){
            			tauToNest[column][row]*=RHO;
            		}
            	
            		if(!((column==49||column==48)&&(row==24||row==25))&&!((column==47&&(row==23||row==24||row==25||row==26))||((column==49||column==48)&&(row==23||row==26)))){
            			tauToFood[column][row]*=RHO;
            		}
				}
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
		
		if(value==0){
			for(int column=0;column<=2;column++){
				for(int row=23;row<=26;row++){
					if((column==0||column==1)&&(row==24||row==25)){
        				tauToNest[column][row]=MAX_PHEROMONE*2;
        			}else if((column==2&&(row==23||row==24||row==25||row==26))||((column==0||column==1)&&(row==23||row==26))){
        				tauToNest[column][row]=MAX_PHEROMONE;
        			}
				}
			}
			
			for(int column=47;column<=49;column++){
				for(int row=23;row<=26;row++){
					if((column==49||column==48)&&(row==24||row==25)){
            			tauToFood[column][row]=MAX_PHEROMONE*2;
            		}else if((column==47&&(row==23||row==24||row==25||row==26))||((column==49||column==48)&&(row==23||row==26))){
            			tauToFood[column][row]=MAX_PHEROMONE;
            		}
				}
			}
		}
	}
	
}
