package env;
// Environment code for project foraging

import java.util.List;

import jason.asSyntax.*;
import jason.environment.*;
import jason.environment.grid.Location;
import jason.util.Pair;


public class ForagingEnvironment extends Environment {

    private ForagingModel model;
    private int value;
    private static final String MOVE_TO_NEXT_POSITION = "moveToNextPosition";
    private static final String RELEASE_PHEROMONE = "releasePheromone";
    private static final String PICK_UP_FOOD = "pickUpFood";
    private static final String PICK_ITEM_NEST = "pickUpItemNest";
    private static final String DROP_FOOD = "dropFood";
    private static final String RELEASE_PHEROMONE_ITEM = "releasePheromoneItem";
    private static final String MOVE_ITEM="moveItem";
    private static final String MOVE_TO="moveTo";
    private static final String MOVE_INTO_NEST="moveIntoNest";
    private static final String WALK_INTO_NEST="walkIntoNest";
    private static final String REMOVE_COOPERATIVE_TRANSPORT="removePerceptCooperativeTransport";
    private static final String REMOVE_NEXT_POSITION="removeNextPosition";
    private static final String ITEM_IN_NEIGHBORHOOD="itemInNeighborhood";
    private static final String DROP_ITEM_NEST="dropItemNest";

    @Override
    public void init(String[] args) {

        if ((args.length == 2) && args[0].equals("gui")) {
        	value=Integer.parseInt(args[1]);
        	
        	if(value<=3){
        		model=new ForagingModel1(value%3);
        	}else if(value>=7){
        		model=new ForagingModel5(value%3);
        	}else{
        		model=new ForagingModel3(value%3);
        	}
        	
            this.model.setView(new ForagingView(this.model));
        }
        
        initializePercepts();
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
    	String functor=action.getFunctor();
        int agent=Integer.parseInt(agName.substring(5))-1;
        
        if(functor.equals(ForagingEnvironment.MOVE_TO_NEXT_POSITION)) {
        	Location position=null;
        	
        	if(value<=3){
        		position=((ForagingModel1)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
    					Double.parseDouble(action.getTerm(1).toString()),
    					action.getTerm(2).equals(new LiteralImpl("food")),agent);
        	}else if(value>=7){
        		position=((ForagingModel5)model).
    					getNextPosition(action.getTerm(0).equals(new LiteralImpl("food")),agent);
        	}else{
        		position=((ForagingModel3)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
    					Double.parseDouble(action.getTerm(1).toString()),
    					action.getTerm(2).equals(new LiteralImpl("food")),agent);
        	}
        	
            model.moveTo(agent,position);
            
            removePerceptsByUnif(agName,Literal.parseLiteral("position(X,Y)"));
            addPercept(agName,Literal.parseLiteral("position("+position.x+","+position.y+")"));
            
            if(model.agentInFoodArea(agent)){
            	if(!containsPercept(agName, Literal.parseLiteral("food"))){
            		addPercept(agName,Literal.parseLiteral("food"));
            	}
            }else if(containsPercept(agName, Literal.parseLiteral("food"))){
            	removePercept(agName,Literal.parseLiteral("food"));
            }
            
            if(model.agentInNest(agent)){
            	if(!containsPercept(agName, Literal.parseLiteral("nest"))){
            		addPercept(agName,Literal.parseLiteral("nest"));
            	}
            }else if(containsPercept(agName, Literal.parseLiteral("nest"))){
            	removePercept(agName,Literal.parseLiteral("nest"));
            }
            
            return true;
        }else if(functor.equals(ForagingEnvironment.RELEASE_PHEROMONE)){
        	
        	if(value<=3){
        		((ForagingModel1)model).updatePheromone(
     					agent,action.getTerm(0).equals(new LiteralImpl("food")));
        	}else if(value>=7){
        		((ForagingModel5)model).updatePheromone(
    					agent,action.getTerm(0).equals(new LiteralImpl("food")));
        	}else{
        		((ForagingModel3)model).updatePheromone(agent);
        	}
        	
        	return true;
        }else if(functor.equals(PICK_UP_FOOD)){
        	Pair<Integer,Item> result=model.pickUpFood(agent,Double.parseDouble(action.getTerm(0).toString()));

        	if(result.getFirst()==2){
        		addPercept(agName,Literal.parseLiteral("heavy"));
        	}else if(result.getFirst()==1 && result.getSecond().getNumberAgents()>1){
        		List<Integer> agents=result.getSecond().getAgents();
        		
        		System.out.println("Cooperative transport: ");
        		for(int tmp:agents){
        			System.out.print("agent"+(tmp+1)+" ");
        			
        			if(agent!=tmp){
    					removePercept("agent"+(tmp+1),Literal.parseLiteral("heavy"));
    				}
        			
        			model.setCooperative(tmp);
        			
        			addPercept("agent"+(tmp+1),Literal.parseLiteral("ready"));
        		}
        		
        		System.out.println("");
        		return false;
        	}
        	
        	return result.getFirst()==1?true:false;
        }else if(functor.equals(DROP_FOOD)){
        	model.dropFood(agent);
        	return true;
        }else if(functor.equals(RELEASE_PHEROMONE_ITEM)){
        	model.releasePheromoneItem(agent);
        	return true;
        }else if(functor.equals(MOVE_TO)){
        	Location position=new Location(Integer.parseInt(action.getTerm(0).toString()),
            		Integer.parseInt(action.getTerm(1).toString()));
        	model.moveTo(agent,position);
            
            removePerceptsByUnif(agName,Literal.parseLiteral("position(X,Y)"));
            addPercept(agName,Literal.parseLiteral("position("+position.x+","+position.y+")"));
            
            if(model.agentInFoodArea(agent)){
            	if(!containsPercept(agName, Literal.parseLiteral("food"))){
            		addPercept(agName,Literal.parseLiteral("food"));
            	}
            }else if(containsPercept(agName, Literal.parseLiteral("food"))){
            	removePercept(agName,Literal.parseLiteral("food"));
            }
            
            if(model.agentInNest(agent)){
            	if(!containsPercept(agName, Literal.parseLiteral("nest"))){
            		addPercept(agName,Literal.parseLiteral("nest"));
            	}
            }else if(containsPercept(agName, Literal.parseLiteral("nest"))){
            	removePercept(agName,Literal.parseLiteral("nest"));
            }
        	
        	return true;
        }else if(functor.equals(REMOVE_COOPERATIVE_TRANSPORT)){
        	removePerceptsByUnif(agName,Literal.parseLiteral("ready"));
        	return true;
        }else if(functor.equals(REMOVE_NEXT_POSITION)){
        	removePerceptsByUnif(agName,Literal.parseLiteral("next_position(_,_)"));
        	return true;
        }else if(functor.equals(MOVE_INTO_NEST)){
        	model.moveIntoNest(agent);
        	return true;
        }else if(functor.equals(WALK_INTO_NEST)){
        	
        	if(model.walkIntoNest(agent)){
            	if(!containsPercept(agName, Literal.parseLiteral("onItem"))){
            		addPercept(agName,Literal.parseLiteral("onItem"));
            	}
            }else if(containsPercept(agName, Literal.parseLiteral("onItem"))){
            	removePercept(agName,Literal.parseLiteral("onItem"));
            }
        	
        	return true;
        }else if(functor.equals(ITEM_IN_NEIGHBORHOOD)){
        	
        	int item=model.itemInNeighborhood(agent);
        	removePerceptsByUnif(agName,Literal.parseLiteral("item(_)"));
        	addPercept(agName,Literal.parseLiteral("item("+item+")"));
        	
        	return true;
        }else if(functor.equals(PICK_ITEM_NEST)){
        	model.pickItemNest(agent);
        	return true;
        }else if(functor.equals(DROP_ITEM_NEST)){
        	return model.dropItemNest(agent);
        }else if(functor.equals(MOVE_ITEM)){
        	if(!model.agentInNest(agent)){
        		Location position=null;
            	double force=0;
            	
            	if(value<=3){
            		position=((ForagingModel1)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
        					Double.parseDouble(action.getTerm(1).toString()),
        					action.getTerm(2).equals(new LiteralImpl("food")),agent);
            		force=Double.parseDouble(action.getTerm(3).toString());
            	}else if(value>=7){
            		position=((ForagingModel5)model).
        					getNextPosition(action.getTerm(0).equals(new LiteralImpl("food")),agent);
            		force=Double.parseDouble(action.getTerm(1).toString());
            	}else{
            		position=((ForagingModel3)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
        					Double.parseDouble(action.getTerm(1).toString()),
        					action.getTerm(2).equals(new LiteralImpl("food")),agent);
            		force=Double.parseDouble(action.getTerm(3).toString());
            	}
            	
            	Item item=model.moveItem(agent,new Pair<Location,Double>(position,force));
            	
            	if(item!=null){
            		Location position2=item.getNextPosition();
            		List<Integer> agents=item.getAgents();
            		for(int tmp:agents){
            			try{
                			addPercept("agent"+(tmp+1),Literal.parseLiteral("next_position("+position2.x+","+position2.y+")"));
                		}catch(Exception e){
                			e.printStackTrace();
                		}
            		}
            		
            		if(model.inNestArea(position2)){
            			model.removeItem(item);
            		}
            	}
        		
            }else{
            	removePerceptsByUnif(agName,Literal.parseLiteral("next_position(X,Y)"));
            }
        	
        	return true;
        }
    	
        return false;
    }

    private void initializePercepts() {
        clearAllPercepts();

        for(int i=0;i<ForagingModel1.NUMBER_AGENTS;i++){
        	final Location agent = this.model.getAgPos(i);
        	addPercept("agent"+(i+1),Literal.parseLiteral("position("+agent.x+","+agent.y+")"));
        	addPercept("agent"+(i+1),Literal.parseLiteral("nest"));
        }
    }
}
