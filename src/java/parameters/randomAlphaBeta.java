package parameters;

import java.util.Random;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

public class randomAlphaBeta extends DefaultInternalAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Object execute(final TransitionSystem ts, final Unifier un,
            final Term[] args) throws Exception {
        
    	double alpha=0.2+(new Random().nextDouble()*0.4);
    	double beta=1-alpha;
    	
    	un.unifies(args[0],new NumberTermImpl(alpha));
    	un.unifies(args[1],new NumberTermImpl(beta));
    	
        return true;
    }
}