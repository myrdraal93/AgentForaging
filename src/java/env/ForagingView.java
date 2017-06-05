package env;

import java.awt.Color;
import java.awt.Graphics;

import jason.environment.grid.GridWorldView;

public class ForagingView extends GridWorldView{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ForagingModel model;
	
	public ForagingView(ForagingModel model) {
		super(model, "Foraging", 700);
		this.model = model;
        this.setVisible(true);
        this.repaint();
	}
	
	/** draw application objects */
    @Override
    public void draw(final Graphics g, final int x, final int y,
            final int object) {
        switch (object) {
            case ForagingModel.NEST:super.drawAgent(g, x, y, Color.RED, -1);break;
            case ForagingModel.FOOD:super.drawAgent(g, x, y, Color.YELLOW, -1);break;
            default:break;
        }
    }
    
    @Override
    public void drawAgent(final Graphics g, final int x, final int y, Color c,
            final int id) {
        if (id!=-1 && !model.antInNest(id) && !model.antInFoodArea(id)) {
        	c=model.getCooperative(id)?Color.ORANGE:model.carryingFood(id)?Color.BLUE:Color.GREEN;
           	super.drawAgent(g, x, y, c, -1);
        }
    }
    
}
