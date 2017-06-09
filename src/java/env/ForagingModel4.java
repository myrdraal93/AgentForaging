package env;

import java.util.ArrayList;
import java.util.List;

import jason.environment.grid.Location;
import jason.util.Pair;

public class ForagingModel4 extends ForagingModel3{

	protected ForagingModel4() {
		super();
	}
	
	
	@Override
	protected List<Pair<Double,Location>> calculateGrade(int x, int y,double alpha,double beta,boolean searchFood){
		ArrayList<Pair<Double,Location>> val=new ArrayList<>();
		double eta[][]=searchFood?etaToFood:etaToNest;
		
		Location location;
		
		if((x-1)>=0){
			location=new Location(x-1,y);
			val.add(evaluatePosition(location, eta,alpha, beta,searchFood));
				
			if((y-1)>=0){
				location=new Location(x-1,y-1);
				val.add(evaluatePosition(location, eta,alpha, beta,searchFood));		
			}
				
			if((y+1)<SIZE){
				location=new Location(x-1,y+1);
				val.add(evaluatePosition(location, eta,alpha, beta,searchFood));
			}
				
		}
		
		
		if((y-1)>=0){
			location=new Location(x,y-1);
			val.add(evaluatePosition(location, eta,alpha, beta,searchFood));
			
		}
		
		if((y+1)<SIZE){
			location=new Location(x,y+1);
			val.add(evaluatePosition(location, eta,alpha, beta,searchFood));
		}
		
		
		if((x+1)<SIZE){
			location=new Location(x+1,y);
			val.add(evaluatePosition(location, eta,alpha,beta,searchFood));
				
			if((y-1)>=0){
				location=new Location(x+1,y-1);
				val.add(evaluatePosition(location, eta,alpha, beta,searchFood));	
			}
				
			if((y+1)<SIZE){
				location=new Location(x+1,y+1);
				val.add(evaluatePosition(location, eta,alpha, beta,searchFood));
			}
		}
		
		return val;
	}	
}
