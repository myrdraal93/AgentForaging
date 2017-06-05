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
    private static final String GET_NEXT_POSITION="getNextPosition";
    private static final String MOVE_TO="moveTo";
    private static final String MOVE_INTO_NEST="moveIntoNest";
    private static final String WALK_INTO_NEST="walkIntoNest";
    private static final String REMOVE_COOPERATIVE_TRANSPORT="removePerceptCooperativeTransport";
    private static final String ITEM_IN_NEIGHBORHOOD="itemInNeighborhood";
    private static final String DROP_ITEM_NEST="dropItemNest";

    @Override
    public void init(String[] args) {

        if ((args.length == 2) && args[0].equals("gui")) {
        	value=Integer.parseInt(args[1]);
        	
        	switch(value){
        		case 1:model=new ForagingModel1();break;
        		case 2:model=new ForagingModel2();break;
        		case 3:model=new ForagingModel3();break;
        		case 4:model=new ForagingModel4();break;
        		case 5:model=new ForagingModel5();break;
        		case 6:model=new ForagingModel6();break;
        		default:break;
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
        	
        	if(value<=4){
        		switch(value){
        			case 1:position=((ForagingModel1)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
        					Double.parseDouble(action.getTerm(1).toString()),
        					action.getTerm(2).equals(new LiteralImpl("food")),agent);break;
        			case 2:position=((ForagingModel2)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
        					Double.parseDouble(action.getTerm(1).toString()),
        					action.getTerm(2).equals(new LiteralImpl("food")),agent);break;
        			case 3:position=((ForagingModel3)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
        					Double.parseDouble(action.getTerm(1).toString()),
        					action.getTerm(2).equals(new LiteralImpl("food")),agent);break;
        			case 4:position=((ForagingModel4)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
        					Double.parseDouble(action.getTerm(1).toString()),
        					action.getTerm(2).equals(new LiteralImpl("food")),agent);break;
        			default:break;
        		}
        		
        	}else{
        		if(value==5){
        			position=((ForagingModel5)model).
        					getNextPosition(action.getTerm(0).equals(new LiteralImpl("food")),agent);
        		}else{
        			position=((ForagingModel6)model).
        					getNextPosition(action.getTerm(0).equals(new LiteralImpl("food")),agent);
        		}
        	}
            
            model.moveTo(agent,position);
            
            removePerceptsByUnif(agName,Literal.parseLiteral("position(X,Y)"));
            addPercept(agName,Literal.parseLiteral("position("+position.x+","+position.y+")"));
            
            if(model.antInFoodArea(agent)){
            	if(!containsPercept(agName, Literal.parseLiteral("food"))){
            		addPercept(agName,Literal.parseLiteral("food"));
            	}
            }else if(containsPercept(agName, Literal.parseLiteral("food"))){
            	removePercept(agName,Literal.parseLiteral("food"));
            }
            
            if(model.antInNest(agent)){
            	if(!containsPercept(agName, Literal.parseLiteral("nest"))){
            		addPercept(agName,Literal.parseLiteral("nest"));
            	}
            }else if(containsPercept(agName, Literal.parseLiteral("nest"))){
            	removePercept(agName,Literal.parseLiteral("nest"));
            }
            
            return true;
        }else if(functor.equals(ForagingEnvironment.RELEASE_PHEROMONE)){
        	
        	if(value==3||value==4){
        		if(value==3){
        			((ForagingModel3)model).updatePheromone(agent);
        		}else{
        			((ForagingModel4)model).updatePheromone(agent);
        		}
        	}else{
        		
        		if(value==1||value==2){
        			 ((ForagingModel1)model).updatePheromone(
         					agent,action.getTerm(0).equals(new LiteralImpl("food")));
        		}else{
        			((ForagingModel5)model).updatePheromone(
        					agent,action.getTerm(0).equals(new LiteralImpl("food")));
        		}
        	}
        	
        	return true;
        }else if(functor.equals(PICK_UP_FOOD)){
        	Pair<Integer,Item> result=model.pickUpFood(agent,Double.parseDouble(action.getTerm(0).toString()));

        	if(result.getFirst()==2){
        		addPercept(agName,Literal.parseLiteral("heavy"));
        	}else if(result.getFirst()==1 && result.getSecond().getAgents().size()>1){
        		List<Integer> agents=result.getSecond().getAgents();
        		
        		System.out.println("Cooperative transport: ");
        		for(int tmp:agents){
        			System.out.print("agent"+(tmp+1)+" ");
        			String list="[";
        			for(int i=0;i<agents.size();i++){
        				if(agents.get(i)!=tmp){
        					list+="agent"+(agents.get(i)+1)+",";
        				}
        				
        				if(agent!=tmp){
        					removePercept("agent"+(tmp+1),Literal.parseLiteral("heavy"));
        				}
        			}
        			
        			model.setCooperative(tmp);
        			list=list.substring(0,list.length()-1)+"]";
        			
        			addPercept("agent"+(tmp+1),Literal.parseLiteral("ready("+(agents.size()-1)+","+list+")"));
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
        }else if(functor.equals(GET_NEXT_POSITION)){
        	if(!model.antInNest(agent)){
        	
        		Location position=null;
            	
            	if(value<=4){
            		switch(value){
            			case 1:position=((ForagingModel1)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
            					Double.parseDouble(action.getTerm(1).toString()),
            					action.getTerm(2).equals(new LiteralImpl("food")),agent);break;
            			case 2:position=((ForagingModel2)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
            					Double.parseDouble(action.getTerm(1).toString()),
            					action.getTerm(2).equals(new LiteralImpl("food")),agent);break;
            			case 3:position=((ForagingModel3)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
            					Double.parseDouble(action.getTerm(1).toString()),
            					action.getTerm(2).equals(new LiteralImpl("food")),agent);break;
            			case 4:position=((ForagingModel4)model).getNextPosition(Double.parseDouble(action.getTerm(0).toString()),
            					Double.parseDouble(action.getTerm(1).toString()),
            					action.getTerm(2).equals(new LiteralImpl("food")),agent);break;
            			default:break;
            		}
            		
            	}else{
            		if(value==5){
            			position=((ForagingModel5)model).
            					getNextPosition(action.getTerm(0).equals(new LiteralImpl("food")),agent);
            		}else{
            			position=((ForagingModel6)model).
            					getNextPosition(action.getTerm(0).equals(new LiteralImpl("food")),agent);
            		}
            	}
            	
        		removePerceptsByUnif(agName,Literal.parseLiteral("next_position(_,_)"));
        		try{
            	addPercept(agName,Literal.parseLiteral("next_position("+position.x+","+position.y+")"));
        		}catch(Exception e){
        			e.printStackTrace();
        		}
            }
        	
        	return true;
        }else if(functor.equals(MOVE_TO)){
        	Location position=new Location(Integer.parseInt(action.getTerm(0).toString()),
            		Integer.parseInt(action.getTerm(1).toString()));
        	model.moveTo(agent,position);
            
            removePerceptsByUnif(agName,Literal.parseLiteral("position(X,Y)"));
            addPercept(agName,Literal.parseLiteral("position("+position.x+","+position.y+")"));
            
            if(model.antInFoodArea(agent)){
            	if(!containsPercept(agName, Literal.parseLiteral("food"))){
            		addPercept(agName,Literal.parseLiteral("food"));
            	}
            }else if(containsPercept(agName, Literal.parseLiteral("food"))){
            	removePercept(agName,Literal.parseLiteral("food"));
            }
            
            if(model.antInNest(agent)){
            	if(!containsPercept(agName, Literal.parseLiteral("nest"))){
            		addPercept(agName,Literal.parseLiteral("nest"));
            	}
            }else if(containsPercept(agName, Literal.parseLiteral("nest"))){
            	removePercept(agName,Literal.parseLiteral("nest"));
            }
        	
        	return true;
        }else if(functor.equals(REMOVE_COOPERATIVE_TRANSPORT)){
        	removePerceptsByUnif(agName,Literal.parseLiteral("ready(_,_)"));
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
