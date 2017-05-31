package math;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

public class pow extends DefaultInternalAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Object execute(final TransitionSystem ts, final Unifier un,
            final Term[] args) throws Exception {
    	
    	
    	double value=Double.parseDouble(args[0].toString());
    	int pow=Integer.parseInt(args[1].toString());
    	
    	un.unifies(args[2],new NumberTermImpl(Math.pow(value,pow)));
    	
        return true;
    }
}
