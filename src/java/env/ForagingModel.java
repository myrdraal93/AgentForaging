package env;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import jason.environment.grid.Area;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import jason.util.Pair;

public class ForagingModel extends GridWorldModel{

	private final static int SIZE=50;
	private final static int NUMBER_FOOD=15;
	public final static int NUMBER_AGENTS=100;
	public final static int FOOD=16;
	public final static int NEST=32;
	private int itemNest=0;
	private static Area NEST_AREA;
	private static Area FOOD_AREA;
	private static Area wall_area,wall_area2,wall_area3,wall_area4,wall_area5,wall_area6,wall_area7;
	
	private int pheromone;
	private double rho;
	private double [][] tauToFood,tauToNest,etaToFood,etaToNest;
	private List<Boolean> carryingFood,cooperative;
	private List<Item> item4824,item4825,item4924,item4925;
	private List<Location> itemInNest;
	private Object lock,lockFood,lockFoodNest;
	
	protected ForagingModel() {
		super(SIZE,SIZE,NUMBER_AGENTS);
		
		lock=new Object();
		lockFood=new Object();
		lockFoodNest=new Object();
		
		initializeACO();
		setAntPosition();
		setEnvironment();
		
		new Thread(()->{
			int i=0;
			while(true){
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				i++;
				
				if(i==3){
					i=0;
					refillArea();
					System.out.println("Refill");
				}
				
				evaporatePheromone();
			}
		}).start();
	}

	
	public boolean antInNest(int agent){
		boolean tmp;
		
		//synchronized(lockAgent){
			tmp=getAgPos(agent).isInArea(NEST_AREA);
		//}
		return tmp;
	}
	
	public boolean antInFoodArea(int agent){
		
		boolean tmp;
		
		//synchronized(lockAgent){
			tmp=getAgPos(agent).isInArea(FOOD_AREA);
		//}
		return tmp;
	}
	
	public boolean carryingFood(int agent){
		boolean flag;
		synchronized(lockFood){
			flag=carryingFood.get(agent);
		}
		return flag;
	}
	
	public boolean getCooperative(int agent){
		boolean flag;
		synchronized(lockFood){
			flag=cooperative.get(agent);
		}
		return flag;
	}
	
	public void setCooperative(int agent){
		synchronized(lockFood){
			cooperative.set(agent,true);
		}
	}
	
	public void updatePheromone(int agent,boolean searchFood){
		synchronized(lock){
			Location position;
			
			//synchronized(lockAgent){
				position=getAgPos(agent);
			//}
			
			if(searchFood){
				if(tauToNest[position.x][position.y]+pheromone<50000){
					tauToNest[position.x][position.y]+=pheromone;
				}else{
					tauToNest[position.x][position.y]=50000;
				}
			}else{
				if(tauToFood[position.x][position.y]+pheromone<50000){
					tauToFood[position.x][position.y]+=pheromone;
				}else{
					tauToFood[position.x][position.y]=5000;
				}
			}
		}
	}
	
	public Pair<Integer,Item> pickUpFood(int agent,double weight){
		// 0 no item
		// 1 ok
		// 2 too heavy
		
		int result=0;
		int index=-1;
		Item item=null;
		
		synchronized(lockFood){
			Location position;
			
			//synchronized(lockAgent){
				position=getAgPos(agent);
			//}
			
			if(position.x==48 && position.y==24){
				if(item4824.size()!=0){
					index=montecarloItem(item4824);
					if(item4824.get(index).getWeight()<=(weight*5)){
						item=item4824.remove(index);
						item.addAgent(agent, weight*5);
						result=1;
					}else{
						item4824.get(index).addAgent(agent,weight*5);
						result=2;
					}
				}else{
					result=0;
				}
			}else if(position.x==48 && position.y==25){
				if(item4825.size()!=0){
					index=montecarloItem(item4825);
					if(item4825.get(index).getWeight()<=(weight*5)){
						item=item4825.remove(index);
						item.addAgent(agent, weight*5);
						result=1;
					}else{
						item4825.get(index).addAgent(agent,weight*5);
						result=2;
					}
				}else{
					result=0;
				}
			}else if(position.x==49 && position.y==24){
				if(item4924.size()!=0){
					index=montecarloItem(item4924);
					if(item4924.get(index).getWeight()<=(weight*5)){
						item=item4924.remove(index);
						item.addAgent(agent, weight*5);
						result=1;
					}else{
						item4924.get(index).addAgent(agent,weight*5);
						result=2;
					}
				}else{
					result=0;
				}
			}else if(position.x==49 && position.y==25){
				if(item4925.size()!=0){
					index=montecarloItem(item4925);
					if(item4925.get(index).getWeight()<=(weight*5)){
						item=item4925.remove(index);
						item.addAgent(agent, weight*5);
						result=1;
					}else{
						item4925.get(index).addAgent(agent,weight*5);
						result=2;
					}
				}else{
					result=0;
				}
			}
			
			carryingFood.set(agent,result==1?true:false);
			
		}
		
		return new Pair<>(result,item);
	}
	
	public void releasePheromoneItem(int ant){
		synchronized(lockFood){
			
			Location position;
			
			//synchronized(lockAgent){
				position=getAgPos(ant);
			//}
			
			if(position.x==48 && position.y==24){
				for(int i=0;i<item4824.size();i++){
					if(item4824.get(i).getAgents().contains(ant)){
						item4824.get(i).addPheromone();
						return;
					}
				}
			}else if(position.x==48 && position.y==25){
				for(int i=0;i<item4825.size();i++){
					if(item4825.get(i).getAgents().contains(ant)){
						item4825.get(i).addPheromone();
						return;
					}
				}
			}else if(position.x==49 && position.y==24){
				for(int i=0;i<item4924.size();i++){
					if(item4924.get(i).getAgents().contains(ant)){
						item4924.get(i).addPheromone();
						return;
					}
				}
			}else if(position.x==49 && position.y==25){
				for(int i=0;i<item4925.size();i++){
					if(item4925.get(i).getAgents().contains(ant)){
						item4925.get(i).addPheromone();
						return;
					}
				}
			}
		}
	}
	
	public boolean walkIntoNest(int agent){
		synchronized(lockFoodNest){
			Random r=new Random();
			Location location= getAgPos(agent);
			
			int x=location.x+(r.nextInt(3)-1);
			int y=location.y+(r.nextInt(3)-1);
			
			while(x<0|| x>49 || y<0||y>17){
				x=location.x+(r.nextInt(3)-1);
				y=location.y+(r.nextInt(3)-1);	
			}
			
			setAgPos(agent, x, y);
			
			return itemInNest.contains(new Location(x,y));
		}
	}
	
	public int itemInNeighborhood(int agent){
		synchronized(lockFoodNest){
			int tmp=0;
			
			Location location=getAgPos(agent);
			
			if(itemInNest.contains(new Location(location.x-1,location.y-1))){
				tmp++;
			}
			
			if(itemInNest.contains(new Location(location.x-1,location.y))){
				tmp++;
			}
			
			if(itemInNest.contains(new Location(location.x-1,location.y+1))){
				tmp++;
			}
			
			if(itemInNest.contains(new Location(location.x,location.y-1))){
				tmp++;
			}
			
			if(itemInNest.contains(new Location(location.x,location.y+1))){
				tmp++;
			}
			
			if(itemInNest.contains(new Location(location.x+1,location.y-1))){
				tmp++;
			}
			
			if(itemInNest.contains(new Location(location.x+1,location.y))){
				tmp++;
			}
			
			if(itemInNest.contains(new Location(location.x+1,location.y+1))){
				tmp++;
			}
			
			return tmp;
		}
	}
	
	public void pickItemNest(int agent){
		synchronized(lockFoodNest){
			Location location=getAgPos(agent);
			itemInNest.remove(location);
			remove(FOOD,location);
			
			synchronized(lockFood){
				carryingFood.set(agent,true);
			}
		}
	}
	
	public boolean dropItemNest(int agent){
		synchronized(lockFoodNest){
			Location location=getAgPos(agent);
			
			if(itemInNest.contains(location)){
				return false;
			}
			
			add(FOOD,location);
			itemInNest.add(location);
			
			synchronized(lockFood){
				carryingFood.set(agent,false);
			}
			
			return true;
		}
	}
	
	public void moveIntoNest(int agent){
		synchronized(lockFoodNest){
			Random r=new Random();
			Location location=new Location(r.nextInt(50),r.nextInt(18));
			
			while(itemInNest.contains(location)){
				location=new Location(r.nextInt(50),r.nextInt(18));
			}
			
			setAgPos(agent, location);
		}
	}
	
	public void dropFood(int agent){
		synchronized(lockFood){
			carryingFood.set(agent,false);
			cooperative.set(agent,false);
		}
		
		synchronized(lockFoodNest){
			
			if(itemNest<100){
				itemNest++;
				/*if(itemInNest.size()>=100){
					remove(FOOD,itemInNest.remove(0));
				}*/
			
				Random r=new Random();
				Location locationItem=new Location(r.nextInt(50),r.nextInt(18));
			
				while(itemInNest.contains(locationItem)){
					locationItem=new Location(r.nextInt(50),r.nextInt(18));
				}
			
				add(FOOD,locationItem);
				itemInNest.add(locationItem);
			}
		}
	}
	
	public Location getNextPosition(double alpha,double beta,boolean searchFood,int ant){
		Location position;
		
		//synchronized(lockAgent){
			position=getAgPos(ant);
		//}
		Location location=montecarlo(calculateGrade(position.x,position.y,alpha,beta,searchFood));
		return location;
	}
	
	public void moveTo(int ant,Location location){
		//synchronized(lockAgent){
			setAgPos(ant,location);
		//}
	}
	
	private int montecarloItem(List<Item> list){
		double sum=0;
		
		for(int i=0;i<list.size();i++){
			sum+=list.get(i).getPheromone();
		}
		
		
		double randNum=new Random().nextDouble()*sum;
		
		for(int i=0;i<list.size();i++){
			randNum-=list.get(i).getPheromone();
			if(randNum<=0){
				return i;
			}
		}	
		
		
		return list.size()-1;
		
	}
	
	private List<Pair<Double,Location>> calculateGrade(int x, int y,double alpha,double beta,boolean searchFood){
		ArrayList<Pair<Double,Location>> val=new ArrayList<>();
		
		double eta[][]=searchFood?etaToFood:etaToNest;
		double tau[][];
		
		synchronized(lock){
			tau=searchFood?tauToFood:tauToNest;
		}
		
		Location location;
		
		if(!searchFood){
			if((x-1)>=0){
				location=new Location(x-1,y);
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
		
		
		if(searchFood){
			if((x+1)<SIZE){
				location=new Location(x+1,y);
				val.add(evaluatePosition(location, eta, tau, alpha,beta));
			}
		}
		
		return val;
	}
	
	private Pair<Double,Location> evaluatePosition(Location location,double [][]eta,double [][]tau,double alpha,double beta){
		
		if(antInWallArea(location)){
			return new Pair<>(0.0,location);
		}
		
		return new Pair<>(Math.pow(eta[location.x][location.y],alpha)*
				Math.pow(tau[location.x][location.y],beta),location);
		
	}
	
	private Location montecarlo(List<Pair<Double,Location>> val){
		double sum=0;
		
		for(int i=0;i<val.size();i++){
			sum+=val.get(i).getFirst();
		}
		
		
		if(sum!=0){
			double randNum=new Random().nextDouble()*sum;
		
			for(int i=0;i<val.size();i++){
				randNum-=val.get(i).getFirst();
				if(randNum<=0){
					return val.get(i).getSecond();
				}
			}	
		
		
			return val.get(val.size()-1).getSecond();
		}
		
		for(Pair<Double,Location> tmp : val){
			if(!antInWallArea(tmp.getSecond())){
				return tmp.getSecond();
			}
		}
		
		return val.get(0).getSecond();
	}
	
	private void evaporatePheromone(){
		synchronized(lock){
			for(int column=0;column<SIZE;column++){
				for(int row=0;row<SIZE;row++){
					tauToFood[column][row]*=rho;
					tauToNest[column][row]*=rho;
				}
			}
		}
		
		synchronized(lockFood){
			IntStream.range(0,item4824.size()).forEach(i->item4824.get(i).evaporatePheromone());
			IntStream.range(0,item4825.size()).forEach(i->item4825.get(i).evaporatePheromone());
			IntStream.range(0,item4924.size()).forEach(i->item4924.get(i).evaporatePheromone());
			IntStream.range(0,item4925.size()).forEach(i->item4925.get(i).evaporatePheromone());
		}
	}
	
	private void initializeACO(){
		rho=0.7;
		carryingFood=new ArrayList<>();
		cooperative=new ArrayList<>();
		double[][] distanceToFood=new double[SIZE][SIZE];
		double[][] distanceToNest=new double[SIZE][SIZE];
		tauToFood=new double[SIZE][SIZE];
		tauToNest=new double[SIZE][SIZE];
		etaToFood=new double[SIZE][SIZE];
		etaToNest=new double[SIZE][SIZE];
		item4824=new ArrayList<>();
		item4825=new ArrayList<>();
		item4924=new ArrayList<>();
		item4925=new ArrayList<>();
		itemInNest=new ArrayList<>();
		
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
	                	tauToFood[column][row]+=25;
	                	tauToNest[column][row]+=25;
	                	etaToFood[column][row]+= maxDistanceToFood - distanceToFood[column][row];
	                	etaToNest[column][row]+= maxDistanceToNest - distanceToNest[column][row];
	                }
	            }
			}
		}
		
		etaToFood[48][24]+=NUMBER_FOOD*10;
        etaToFood[48][25]+=NUMBER_FOOD*10;
        etaToFood[49][24]+=NUMBER_FOOD*10;
        etaToFood[49][25]+=NUMBER_FOOD*10;
        
        pheromone=100;
        
        Random r=new Random();
        IntStream.range(item4824.size(),14).forEach(i->item4824.add(new Item(/*r.nextDouble()*25*/0)));
		IntStream.range(item4825.size(),14).forEach(i->item4825.add(new Item(/*r.nextDouble()*25*/0)));
		IntStream.range(item4924.size(),14).forEach(i->item4924.add(new Item(/*r.nextDouble()*25*/0)));
		IntStream.range(item4925.size(),14).forEach(i->item4925.add(new Item(/*r.nextDouble()*25*/0)));
		
		item4824.add(new Item(50));
		item4825.add(new Item(50));
		item4924.add(new Item(50));
		item4925.add(new Item(50));
	}
	
	private void setAntPosition(){
		Random r=new Random();
		
		IntStream.range(0,NUMBER_AGENTS).forEach(i->{
			setAgPos(i,r.nextInt(2),r.nextInt(2)+24);
			carryingFood.add(false);
			cooperative.add(false);
		});
	}
	
	private void setEnvironment(){
		
		NEST_AREA=new Area(new Location(0,24),new Location(1,25));
		FOOD_AREA=new Area(new Location(48,24),new Location(49,25));
		wall_area=new Area(new Location(24,22),new Location(40,30));
		wall_area2=new Area(new Location(0,34),new Location(15,34));
		wall_area3=new Area(new Location(16,34),new Location(16,49));
		wall_area4=new Area(new Location(26,31),new Location(26,40));
		wall_area5=new Area(new Location(0,18),new Location(49,18));
		wall_area6=new Area(new Location(35,34),new Location(35,49));
		wall_area7=new Area(new Location(36,34),new Location(49,34));
		
		add(NEST, new Location(0,24));
        add(NEST, new Location(0,25));
        add(NEST, new Location(1,24));
        add(NEST, new Location(1,25));
        add(FOOD, new Location(48,24));
        add(FOOD, new Location(48,25));
        add(FOOD, new Location(49,24));
        add(FOOD, new Location(49,25));
        addWall(wall_area.tl.x,wall_area.tl.y,wall_area.br.x,wall_area.br.y);
        addWall(wall_area2.tl.x,wall_area2.tl.y,wall_area2.br.x,wall_area2.br.y);
        addWall(wall_area3.tl.x,wall_area3.tl.y,wall_area3.br.x,wall_area3.br.y);
        addWall(wall_area4.tl.x,wall_area4.tl.y,wall_area4.br.x,wall_area4.br.y);
        addWall(wall_area5.tl.x,wall_area5.tl.y,wall_area5.br.x,wall_area5.br.y);
        addWall(wall_area6.tl.x,wall_area6.tl.y,wall_area6.br.x,wall_area6.br.y);
        addWall(wall_area7.tl.x,wall_area7.tl.y,wall_area7.br.x,wall_area7.br.y);
	}
	
	private boolean antInWallArea(Location location){
		return location.isInArea(wall_area)||location.isInArea(wall_area2)
				||location.isInArea(wall_area3)||location.isInArea(wall_area4)
				||location.isInArea(wall_area5)||location.isInArea(wall_area6)
				||location.isInArea(wall_area7);
	}
	
	private void refillArea(){
		synchronized(lockFood){
			Random r=new Random();
			IntStream.range(item4824.size(),14).forEach(i->item4824.add(new Item(/*r.nextDouble()*25*/0)));
			IntStream.range(item4825.size(),14).forEach(i->item4825.add(new Item(/*r.nextDouble()*25*/0)));
			IntStream.range(item4924.size(),14).forEach(i->item4924.add(new Item(/*r.nextDouble()*25*/0)));
			IntStream.range(item4925.size(),14).forEach(i->item4925.add(new Item(/*r.nextDouble()*25*/0)));
			
			if(item4824.size()<15){
				item4824.add(new Item(50));
			}
			
			if(item4825.size()<15){
				item4825.add(new Item(50));
			}
			
			if(item4924.size()<15){
				item4924.add(new Item(50));
			}
			
			if(item4925.size()<15){
				item4925.add(new Item(50));
			}
			
		}
	}
}
