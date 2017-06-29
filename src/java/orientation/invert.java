// Internal action code for project foraging

package orientation;

import jason.asSemantics.*;
import jason.asSyntax.*;

public class invert extends DefaultInternalAction {

	
	private static final long serialVersionUID = 1L;
	
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	String tmp=args[0].toString();
    	LiteralImpl orientation;
    	
    	if(tmp.equals("\"N\"")){
    		orientation=new LiteralImpl("\"S\"");
    	}else if(tmp.equals("\"S\"")){
    		orientation=new LiteralImpl("\"N\"");
    	}else if(tmp.equals("\"E\"")){
    		orientation=new LiteralImpl("\"O\"");
    	}else{
    		orientation=new LiteralImpl("\"E\"");
    	}
    	
    	un.unifies(args[1],orientation);
    	
        return true;
    }
}
