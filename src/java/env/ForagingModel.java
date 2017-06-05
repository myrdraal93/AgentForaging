package env;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import jason.environment.grid.Area;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import jason.util.Pair;

public abstract class ForagingModel extends GridWorldModel{

	protected final static int SIZE=50;
	protected final static int NUMBER_FOOD=15;
	public final static int NUMBER_AGENTS=100;
	public final static int FOOD=16;
	public final static int NEST=32;
	
	private int itemNest=0;
	private static Area NEST_AREA;
	private static Area FOOD_AREA;
	private static Area wall_area,wall_area2,wall_area3,wall_area4,wall_area5,wall_area6,wall_area7;
	protected ReentrantLock lockAgent,lock,lockFood,lockFoodNest;
	protected int pheromone;
	protected double rho;
	private List<Boolean> carryingFood,cooperative;
	protected List<Item> item4824,item4825,item4924,item4925;
	private List<Location> itemInNest;
	
	public ForagingModel(){
		super(SIZE,SIZE,NUMBER_AGENTS);
		
		lock=new ReentrantLock();
		lockFood=new ReentrantLock();
		lockFoodNest=new ReentrantLock();
		lockAgent=new ReentrantLock();
		
		initialize();
		initializeACO();
		setAntPosition();
		setEnvironment();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");    
		System.out.println("Start time: "+sdf.format(new Date(System.currentTimeMillis())));
		
		new Thread(()->{
			int i=0;
			while(true){
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				i++;
				
				if(i==3){
					i=0;
					refillArea();
					//System.out.println("Refill");
				}
				
				evaporatePheromone();
				System.out.println(sdf.format(new Date(System.currentTimeMillis())));
			}
		}).start();
	}
	
	public boolean antInNest(int agent){
		//boolean tmp;
		
		return getAgentPosition(agent).isInArea(NEST_AREA);
		//synchronized(lockAgent){
		//	tmp=getAgPos(agent).isInArea(NEST_AREA);
		//}
		//return tmp;
		
	}
	
	public boolean antInFoodArea(int agent){
		
		//boolean tmp;
		
		return getAgentPosition(agent).isInArea(FOOD_AREA);
		//synchronized(lockAgent){
			//tmp=getAgPos(agent).isInArea(FOOD_AREA);
		//}
		//return tmp;
	}
	
	public boolean carryingFood(int agent){
		boolean flag;
		/*synchronized(*/lockFood.lock();//){
			flag=carryingFood.get(agent);
			lockFood.unlock();
		//}
		return flag;
	}
	
	public boolean getCooperative(int agent){
		boolean flag;
		/*synchronized(*/lockFood.lock();//){
			flag=cooperative.get(agent);
			lockFood.unlock();
		//}
		return flag;
	}
	
	public void setCooperative(int agent){
		/*synchronized(*/lockFood.lock();//){
			cooperative.set(agent,true);
			lockFood.unlock();
		//}
	}
	
	public Pair<Integer,Item> pickUpFood(int agent,double weight){
		// 0 no item
		// 1 ok
		// 2 too heavy
		
		int result=0;
		int index=-1;
		Item item=null;
		
		/*synchronized(*/lockFood.lock();//){
			Location position;
			
			position=getAgentPosition(agent);
			//synchronized(lockAgent){
				//position=getAgPos(agent);
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
			
			lockFood.unlock();
		//}
		
		return new Pair<>(result,item);
	}
	
	public void releasePheromoneItem(int ant){
		/*synchronized(*/lockFood.lock();//){
			
			Location position;
			
			position=getAgentPosition(ant);
			//synchronized(lockAgent){
				//position=getAgPos(ant);
			//}
			
			if(position.x==48 && position.y==24){
				for(int i=0;i<item4824.size();i++){
					if(item4824.get(i).getAgents().contains(ant)){
						item4824.get(i).addPheromone();
						lockFood.unlock();
						return;
					}
				}
			}else if(position.x==48 && position.y==25){
				for(int i=0;i<item4825.size();i++){
					if(item4825.get(i).getAgents().contains(ant)){
						item4825.get(i).addPheromone();
						lockFood.unlock();
						return;
					}
				}
			}else if(position.x==49 && position.y==24){
				for(int i=0;i<item4924.size();i++){
					if(item4924.get(i).getAgents().contains(ant)){
						item4924.get(i).addPheromone();
						lockFood.unlock();
						return;
					}
				}
			}else if(position.x==49 && position.y==25){
				for(int i=0;i<item4925.size();i++){
					if(item4925.get(i).getAgents().contains(ant)){
						item4925.get(i).addPheromone();
						lockFood.unlock();
						return;
					}
				}
			}
			lockFood.unlock();
		//}
	}
	
	public boolean walkIntoNest(int agent){
		/*synchronized(*/lockFoodNest.lock();//){
			Random r=new Random();
			Location location= getAgPos(agent);
			
			int x=location.x+(r.nextInt(3)-1);
			int y=location.y+(r.nextInt(3)-1);
			
			while(x<0|| x>49 || y<0||y>17){
				x=location.x+(r.nextInt(3)-1);
				y=location.y+(r.nextInt(3)-1);	
			}
			
			Location tmp=new Location(x,y);
			setAgentPosition(agent,tmp);
			//setAgPos(agent, x, y);
			boolean flag=itemInNest.contains(tmp);
			lockFoodNest.unlock();
			return flag;
		//}
	}
	
	public int itemInNeighborhood(int agent){
		/*synchronized(*/lockFoodNest.lock();//){
			int tmp=0;
			
			Location location=getAgentPosition(agent);
			//Location location=getAgPos(agent);
			
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
			
			lockFoodNest.unlock();
			return tmp;
		//}
	}
	
	public void pickItemNest(int agent){
		/*synchronized(*/lockFoodNest.lock();//){
			Location location=getAgentPosition(agent);
			//Location location=getAgPos(agent);
			itemInNest.remove(location);
			remove(FOOD,location);
			
			/*synchronized(*/lockFood.lock();//){
				carryingFood.set(agent,true);
				lockFood.unlock();
			//}
			lockFoodNest.unlock();
		//}
	}
	
	public boolean dropItemNest(int agent){
		/*synchronized(*/lockFoodNest.lock();//){
			//Location location=getAgPos(agent);
			Location location=getAgentPosition(agent);
			
			if(itemInNest.contains(location)){
				lockFoodNest.unlock();
				return false;
			}
			
			add(FOOD,location);
			itemInNest.add(location);
			
			/*synchronized(*/lockFood.lock();//){
				carryingFood.set(agent,false);
				lockFood.unlock();
			//}
			
			lockFoodNest.unlock();
			return true;
		//}
	}
	
	public void moveTo(int ant,Location location){
		setAgentPosition(ant,location);
		//synchronized(lockAgent){
			//setAgPos(ant,location);
		//}
	}
	
	public void moveIntoNest(int agent){
		/*synchronized(*/lockFoodNest.lock();//){
			Random r=new Random();
			Location location=new Location(r.nextInt(50),r.nextInt(18));
			
			while(itemInNest.contains(location)){
				location=new Location(r.nextInt(50),r.nextInt(18));
			}
			
			setAgentPosition(agent, location);
			lockFoodNest.unlock();
			//setAgPos(agent, location);
		//}
	}
	
	public void dropFood(int agent){
		/*synchronized(*/lockFood.lock();//){
			carryingFood.set(agent,false);
			cooperative.set(agent,false);
			lockFood.unlock();
		//}
		
		/*synchronized(*/lockFoodNest.lock();//){
			
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
			lockFoodNest.unlock();
		//}
	}
	
	private void setAntPosition(){
		Random r=new Random();
		
		IntStream.range(0,NUMBER_AGENTS).forEach(i->{
			setAgentPosition(i,new Location(r.nextInt(2),r.nextInt(2)+24));
			carryingFood.add(false);
			cooperative.add(false);
		});
	}
	
	protected int montecarloItem(List<Item> list){
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
	
	protected Location montecarlo(List<Pair<Double,Location>> val){
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
	
	private void initialize(){
		rho=0.7;
		carryingFood=new ArrayList<>();
		cooperative=new ArrayList<>();
		item4824=new ArrayList<>();
		item4825=new ArrayList<>();
		item4924=new ArrayList<>();
		item4925=new ArrayList<>();
		itemInNest=new ArrayList<>();
        pheromone=10;
        
        IntStream.range(item4824.size(),NUMBER_FOOD-1).forEach(i->item4824.add(new Item(0)));
		IntStream.range(item4825.size(),NUMBER_FOOD-1).forEach(i->item4825.add(new Item(0)));
		IntStream.range(item4924.size(),NUMBER_FOOD-1).forEach(i->item4924.add(new Item(0)));
		IntStream.range(item4925.size(),NUMBER_FOOD-1).forEach(i->item4925.add(new Item(0)));
		
		Random r=new Random();
		
		/*item4824.add(new Item(r.nextInt(41)+30));
		item4825.add(new Item(r.nextInt(41)+30));
		item4924.add(new Item(r.nextInt(41)+30));
		item4925.add(new Item(r.nextInt(41)+30));*/
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
	
	protected boolean antInWallArea(Location location){
		return location.isInArea(wall_area)||location.isInArea(wall_area2)
				||location.isInArea(wall_area3)||location.isInArea(wall_area4)
				||location.isInArea(wall_area5)||location.isInArea(wall_area6)
				||location.isInArea(wall_area7);
	}
	
	private void refillArea(){
		/*synchronized(*/lockFood.lock();//){
			IntStream.range(item4824.size(),NUMBER_FOOD-1).forEach(i->item4824.add(new Item(0)));
			IntStream.range(item4825.size(),NUMBER_FOOD-1).forEach(i->item4825.add(new Item(0)));
			IntStream.range(item4924.size(),NUMBER_FOOD-1).forEach(i->item4924.add(new Item(0)));
			IntStream.range(item4925.size(),NUMBER_FOOD-1).forEach(i->item4925.add(new Item(0)));
			
			Random r=new Random();
		/*	
			if(item4824.size()<NUMBER_FOOD){
				item4824.add(new Item(r.nextInt(41)+30));
			}
			
			if(item4825.size()<NUMBER_FOOD){
				item4825.add(new Item(r.nextInt(41)+30));
			}
			
			if(item4924.size()<NUMBER_FOOD){
				item4924.add(new Item(r.nextInt(41)+30));
			}
			
			if(item4925.size()<NUMBER_FOOD){
				item4925.add(new Item(r.nextInt(41)+30));
			}*/
			lockFood.unlock();
		//}
	}
	
	protected void setAgentPosition(int agent,Location location){
		//lockAgent.lock();
		setAgPos(agent,location);
		//lockAgent.unlock();
	}
	
	protected Location getAgentPosition(int agent){
		//lockAgent.lock();
		Location tmp=getAgPos(agent);
		//lockAgent.unlock();
		
		return tmp;
	}
	
	protected abstract void initializeACO();
	protected abstract void evaporatePheromone();
}
