package env;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import jason.environment.grid.Area;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import jason.util.Pair;

public abstract class ForagingModel extends GridWorldModel{

	protected final static int SIZE=50;
	protected final static int NUMBER_FOOD=100;
	protected final static int MAX_PHEROMONE=10000;
	protected final static int PHEROMONE=10;
	protected final static double RHO=0.7;
	public final static int NUMBER_AGENTS=100;
	public final static int FOOD=16;
	public final static int NEST=32;
	
	private int itemNest=0;
	private static Area NEST_AREA;
	private static Area FOOD_AREA;
	private static Area wall_area,wall_area2,wall_area3,wall_area4,wall_area5,wall_area6,wall_area7;
	private static Area wall_area8;
	protected int value;
	protected boolean flag;
	private List<Boolean> carryingFood,cooperative;
	protected List<Item> item4824,item4825,item4924,item4925,carryingItem;
	private boolean flagItem4824,flagItem4825,flagItem4924,flagItem4925;
	private List<Location> itemInNest;
	protected boolean flagNewWall;
	
	public ForagingModel(int value){
		super(SIZE,SIZE,NUMBER_AGENTS);
		this.value=value;
		flag=value%2==0?false:true;
		flagNewWall=false;
		
		initialize();
		initializeACO();
		setAgentPosition();
		setEnvironment();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");    
		System.out.println("Start time: "+sdf.format(new Date(System.currentTimeMillis())));
		
		new Thread(()->{
			int i=0;
			int count=1;
			
			while(true){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				i++;
				
				if(i==4){
					i=0;
					refillArea();
				}
				
				
				if(count==20 && !flagNewWall){
					addNewWall();
				}else{
					count++;
				}
				
				evaporatePheromone();
				System.out.println(sdf.format(new Date(System.currentTimeMillis())));
			}
		}).start();
	}
	
	public synchronized boolean agentInNest(int agent){
		return getAgPos(agent).isInArea(NEST_AREA);
	}
	
	public synchronized boolean inNestArea(Location location){
		return location.isInArea(NEST_AREA);
	}
	
	public synchronized boolean agentInFoodArea(int agent){
		return getAgPos(agent).isInArea(FOOD_AREA);
	}
	
	public synchronized boolean carryingFood(int agent){
		return carryingFood.get(agent);
	}
	
	public synchronized boolean getCooperative(int agent){
		return cooperative.get(agent);
	}
	
	public synchronized void setCooperative(int agent){
		cooperative.set(agent,true);
	}
	
	public synchronized Pair<Integer,Item> pickUpFood(int agent,double weight){
		// 0 no item
		// 1 ok
		// 2 too heavy
		
		int result=0;
		int index=-1;
		Item item=null;
		Location position=getAgPos(agent);
			
		if(position.x==48 && position.y==24){
			if(item4824.size()!=0){
				index=montecarloItem(item4824);
				if(item4824.get(index).getWeight()<=(weight*5)){
					item=item4824.remove(index);
					item.addAgent(agent, weight*5);
					if(item.getNumberAgents()>1){
						flagItem4824=false;
					}
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
					if(item.getNumberAgents()>1){
						flagItem4825=false;
					}
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
					if(item.getNumberAgents()>1){
						flagItem4924=false;
					}
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
					if(item.getNumberAgents()>1){
						flagItem4925=false;
					}
					result=1;
				}else{
					item4925.get(index).addAgent(agent,weight*5);
					result=2;
				}
			}else{
				result=0;
			}
		}
			
		if(item!=null&&item.getNumberAgents()>1){
			carryingItem.add(item);
		}
		
		carryingFood.set(agent,result==1?true:false);
		
		return new Pair<>(result,item);
	}
	
	public synchronized Item moveItem(int agent,Pair<Location,Double> position){
		
		boolean flag=false;
		
		for(int i=0;i<carryingItem.size() && !flag;i++){
			if(carryingItem.get(i).getAgents().contains(agent)){
				flag=true;
				if(carryingItem.get(i).addItemPosition(agent, position)){
					return carryingItem.get(i);
				}
			}
		}
		
		return null;
	}
	
	public synchronized void removeItem(Item item){
		carryingItem.remove(item);
	}
	
	public synchronized void releasePheromoneItem(int agent){
			
		Location position=getAgPos(agent);
			
		if(position.x==48 && position.y==24){
			for(int i=0;i<item4824.size();i++){
				if(item4824.get(i).getAgents().contains(agent)){
					item4824.get(i).addPheromone();
					return;
				}
			}
		}else if(position.x==48 && position.y==25){
			for(int i=0;i<item4825.size();i++){
				if(item4825.get(i).getAgents().contains(agent)){
					item4825.get(i).addPheromone();
					return;
				}
			}
		}else if(position.x==49 && position.y==24){
			for(int i=0;i<item4924.size();i++){
				if(item4924.get(i).getAgents().contains(agent)){
					item4924.get(i).addPheromone();
					return;
				}
			}
		}else if(position.x==49 && position.y==25){
			for(int i=0;i<item4925.size();i++){
				if(item4925.get(i).getAgents().contains(agent)){
					item4925.get(i).addPheromone();
					return;
				}
			}
		}
	}
	
	public synchronized boolean walkIntoNest(int agent){
		Random r=new Random();
		Location location= getAgPos(agent);
			
		int x=location.x+(r.nextInt(3)-1);
		int y=location.y+(r.nextInt(3)-1);
		
		while(x<0|| x>49 || y<0||y>17){
			x=location.x+(r.nextInt(3)-1);
			y=location.y+(r.nextInt(3)-1);	
		}
			
		Location tmp=new Location(x,y);
		setAgPos(agent,tmp);
		
		return itemInNest.contains(tmp);
	}
	
	public synchronized int itemInNeighborhood(int agent){
		int tmp=0;
			
		Location location=getAgPos(agent);

		for(int i=-1;i<=1;i++){
			for(int j=-1;j<=1;j++){
				if(itemInNest.contains(new Location(location.x+i,location.y+1))){
					tmp++;
				}
			}
		}
		
		return tmp;
	}
	
	public synchronized void pickItemNest(int agent){
		Location location=getAgPos(agent);
		itemInNest.remove(location);
		remove(FOOD,location);	
		carryingFood.set(agent,true);		
	}
	
	public synchronized boolean dropItemNest(int agent){
		Location location=getAgPos(agent);
			
		if(itemInNest.contains(location)){
			return false;
		}
			
		add(FOOD,location);
		itemInNest.add(location);
		carryingFood.set(agent,false);
			
		return true;
	}
	
	public synchronized void moveTo(int agent,Location location){
		setAgPos(agent,location);
	}
	
	public synchronized void moveIntoNest(int agent){
		Random r=new Random();
		Location location=new Location(r.nextInt(50),r.nextInt(18));
			
		while(itemInNest.contains(location)){
			location=new Location(r.nextInt(50),r.nextInt(18));
		}
		
		setAgPos(agent, location);
	}
	
	public synchronized void dropFood(int agent){
		carryingFood.set(agent,false);
		cooperative.set(agent,false);
			
		if(itemNest<100){
			itemNest++;
			Random r=new Random();
			Location locationItem=new Location(r.nextInt(50),r.nextInt(18));
			
			while(itemInNest.contains(locationItem)){
				locationItem=new Location(r.nextInt(50),r.nextInt(18));
			}
		
			add(FOOD,locationItem);
			itemInNest.add(locationItem);
		}
	}
	
	private void setAgentPosition(){
		Random r=new Random();
		
		IntStream.range(0,NUMBER_AGENTS).forEach(i->{
			setAgPos(i,new Location(r.nextInt(2),r.nextInt(2)+24));
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
			if(!agentInWallArea(tmp.getSecond())){
				return tmp.getSecond();
			}
		}
		
		
		return val.get(0).getSecond();
	}
	
	private void initialize(){
		carryingFood=new ArrayList<>();
		cooperative=new ArrayList<>();
		carryingItem=new ArrayList<>();
		item4824=new ArrayList<>();
		item4825=new ArrayList<>();
		item4924=new ArrayList<>();
		item4925=new ArrayList<>();
		itemInNest=new ArrayList<>();
        
        IntStream.range(item4824.size(),NUMBER_FOOD-1).forEach(i->item4824.add(new Item(0)));
		IntStream.range(item4825.size(),NUMBER_FOOD-1).forEach(i->item4825.add(new Item(0)));
		IntStream.range(item4924.size(),NUMBER_FOOD-1).forEach(i->item4924.add(new Item(0)));
		IntStream.range(item4925.size(),NUMBER_FOOD-1).forEach(i->item4925.add(new Item(0)));
		
		Random r=new Random();
		
		item4824.add(new Item(r.nextInt(41)+30));
		item4825.add(new Item(r.nextInt(41)+30));
		item4924.add(new Item(r.nextInt(41)+30));
		item4925.add(new Item(r.nextInt(41)+30));
		
		flagItem4824=true;
		flagItem4825=true;
		flagItem4924=true;
		flagItem4925=true;
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
		wall_area8=new Area(new Location(20,19),new Location(20,40));
		
		
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
	
	private synchronized void addNewWall(){
		flagNewWall=true;
        addWall(wall_area8.tl.x,wall_area8.tl.y,wall_area8.br.x,wall_area8.br.y);
        evaporatePheromoneInArea(20,19,40);
	}
	
	protected boolean agentInWallArea(Location location){
		
		if(!flagNewWall){
			return location.isInArea(wall_area)||location.isInArea(wall_area2)
					||location.isInArea(wall_area3)||location.isInArea(wall_area4)
					||location.isInArea(wall_area5)||location.isInArea(wall_area6)
					||location.isInArea(wall_area7);
		}
		
		return location.isInArea(wall_area)||location.isInArea(wall_area2)
				||location.isInArea(wall_area3)||location.isInArea(wall_area4)
				||location.isInArea(wall_area5)||location.isInArea(wall_area6)
				||location.isInArea(wall_area7)||location.isInArea(wall_area8);
	}
	
	
	private synchronized void refillArea(){
		IntStream.range(item4824.size(),NUMBER_FOOD-1).forEach(i->item4824.add(new Item(0)));
		IntStream.range(item4825.size(),NUMBER_FOOD-1).forEach(i->item4825.add(new Item(0)));
		IntStream.range(item4924.size(),NUMBER_FOOD-1).forEach(i->item4924.add(new Item(0)));
		IntStream.range(item4925.size(),NUMBER_FOOD-1).forEach(i->item4925.add(new Item(0)));
			
		Random r=new Random();
		
		if(!flagItem4824 && item4824.size()<NUMBER_FOOD){
			flagItem4824=true;
			item4824.add(new Item(r.nextInt(41)+30));
		}else if(item4824.size()<NUMBER_FOOD){
			item4824.add(new Item(0));
		}
		
		if(!flagItem4825 && item4825.size()<NUMBER_FOOD){
			flagItem4825=true;
			item4825.add(new Item(r.nextInt(41)+30));
		}else if(item4825.size()<NUMBER_FOOD){
			item4825.add(new Item(0));
		}
			
		if(!flagItem4924 && item4924.size()<NUMBER_FOOD){
			flagItem4924=true;
			item4924.add(new Item(r.nextInt(41)+30));
		}else if(item4924.size()<NUMBER_FOOD){
			item4924.add(new Item(0));
		}
			
		if(!flagItem4925 && item4925.size()<NUMBER_FOOD){
			flagItem4925=true;
			item4925.add(new Item(r.nextInt(41)+30));
		}else if(item4824.size()<NUMBER_FOOD){
			item4925.add(new Item(0));
		}
	}
	
	protected abstract void initializeACO();
	protected abstract void evaporatePheromone();
	protected abstract void evaporatePheromoneInArea(int column,int rowStart,int rowEnd);
}
