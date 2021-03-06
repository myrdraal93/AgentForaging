package movement;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import jason.asSyntax.VarTerm;
import jason.util.Pair;

public class calculateNextMove extends DefaultInternalAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Object execute(final TransitionSystem ts, final Unifier un,
            final Term[] args) throws Exception {
    	
    	HashMap<Pair<Integer,Integer>,Double> coordinate=new HashMap<>();
    	
    	ListTermImpl list=(ListTermImpl) args[0];
    	for(Term term:list){
    		String []array=term.toString().replaceAll("\\)","").split(",");
    		Pair<Integer,Integer> pair=new Pair<>(Integer.parseInt(array[1]),Integer.parseInt(array[2]));
    		double force=Double.parseDouble(array[3]);
    		
    		if(coordinate.containsKey(pair)){
    			coordinate.put(pair,coordinate.get(pair)+force);
    		}else{
    			coordinate.put(pair,force);
    		}
    	}
    	
    	String []array=args[1].toString().replaceAll("\\)","").split(",");
		Pair<Integer,Integer> pair=new Pair<>(Integer.parseInt(array[1]),Integer.parseInt(array[2]));
		double force=Double.parseDouble(array[3]);
		
		if(coordinate.containsKey(pair)){
			coordinate.put(pair,coordinate.get(pair)+force);
		}else{
			coordinate.put(pair,force);
		}
		
		double value=-1;
		Pair<Integer,Integer> max=null;
		Object[] set=coordinate.keySet().toArray();
		int i=0;
		
		for(Object tmp : set){
			
			if(coordinate.get((Pair<Integer,Integer>)tmp)>value){
				value=coordinate.get((Pair<Integer,Integer>)tmp);
				max=(Pair<Integer,Integer>)tmp;
			}
			/*if(i==0){
				value1=coordinate.get((Pair<Integer,Integer>)tmp);
			}else if(i==1){
				value2=coordinate.get((Pair<Integer,Integer>)tmp);
			}else if(i==2){
				value3=coordinate.get((Pair<Integer,Integer>)tmp);
			}else if(i==3){
				value4=coordinate.get((Pair<Integer,Integer>)tmp);
			}else{
				value5=coordinate.get((Pair<Integer,Integer>)tmp);
			}
			
			i++;*/
		}
		
		for(Object tmp : set){
			
			if(((Pair<Integer,Integer>)tmp).getFirst()!=max.getFirst()||((Pair<Integer,Integer>)tmp).getSecond()!=max.getSecond()){
				value-=coordinate.get((Pair<Integer,Integer>)tmp);
			}
			
		}
		
		if(value==0){
			un.unifies(args[2],new NumberTermImpl(-1));
			un.unifies(args[3],new NumberTermImpl(-1));
		}else{
			un.unifies(args[2],new NumberTermImpl(max.getFirst()));
			un.unifies(args[3],new NumberTermImpl(max.getSecond()));
		}
		
		//System.out.println("Value1: "+value1+" Value2: "+value2+" Value3: "+value3);
		
		/*if(value1==value2&&value2==value3&&value3==value4&&value4==value5){
			un.unifies(args[2],new NumberTermImpl(-1));
			un.unifies(args[3],new NumberTermImpl(-1));
		}else{
			double max=Math.max(value1,Math.max(value2,Math.max(value3, Math.max(value4,value5))));
			Pair<Integer,Integer> tmp;
			if(max==value1){
				tmp=(Pair<Integer,Integer>)set[0];
			}else if(max==value2){
				tmp=(Pair<Integer,Integer>)set[1];
			}else if(max==value3){
				tmp=(Pair<Integer,Integer>)set[2];
			}else if(max==value4){
				tmp=(Pair<Integer,Integer>)set[3];
			}else{
				tmp=(Pair<Integer,Integer>)set[4];
			}
			
			un.unifies(args[2],new NumberTermImpl(tmp.getFirst()));
			un.unifies(args[3],new NumberTermImpl(tmp.getSecond()));
		}*/
		
    	
        return true;
    }
}
