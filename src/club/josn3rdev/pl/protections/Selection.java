package club.josn3rdev.pl.protections;

import java.util.Objects;

import org.bukkit.block.Block;

public class Selection {
	
	private final Block centerPoint;
	  
	private Block corner_one;
	private Block corner_two;
	private Block corner_three;
	private Block corner_four;
	  
	private int radius;
	  
	public Selection(Block centerPoint, int radius) {
		this.radius = radius;
		this.centerPoint = centerPoint;
		getCornerOne(centerPoint, radius);
		getCornerTwo(centerPoint, radius);
		getCornerThree(centerPoint, radius);
		getCornerFour(centerPoint, radius);
	}
	  
	private void getCornerOne(Block centerPoint, int radius) {
		this.corner_one = centerPoint.getWorld().getBlockAt(centerPoint.getX() - radius, centerPoint.getY(), centerPoint.getZ() - radius);
	}
	  
	private void getCornerTwo(Block centerPoint, int radius) {
		this.corner_two = centerPoint.getWorld().getBlockAt(centerPoint.getX() - radius, centerPoint.getY(), centerPoint.getZ() + radius);
	}
	  
	private void getCornerThree(Block centerPoint, int radius) {
		this.corner_three = centerPoint.getWorld().getBlockAt(centerPoint.getX() + radius, centerPoint.getY(), centerPoint.getZ() - radius);
	}
	  
	private void getCornerFour(Block centerPoint, int radius) {
		this.corner_four = centerPoint.getWorld().getBlockAt(centerPoint.getX() + radius, centerPoint.getY(), centerPoint.getZ() + radius);
	}
	  
	public Block getCenterPoint() {
	    return this.centerPoint;
	}
	  
	public Block getCorner_one() {
	    return this.corner_one;
	}
	  
	public Block getCorner_two() {
	    return this.corner_two;
	}
	  
	public Block getCorner_three() {
	    return this.corner_three;
	}
	  
	public Block getCorner_four() {
	    return this.corner_four;
	}
	  
	public int getRadius() {
	    return this.radius;
	}
	  
	public boolean numberInRange(int target, int starting, int ending) {
		return (target >= starting && target <= ending);
	}
	  
	public boolean overlaps(Selection comparator) {
	    if (containsBlock(comparator.getCorner_one()))
	    	return true; 
	    if (containsBlock(comparator.getCorner_two()))
	    	return true; 
	    if (containsBlock(comparator.getCorner_three()))
	    	return true; 
	    return containsBlock(comparator.getCorner_four());
	}
	  
	public boolean containsBlock(Block block) {
		if (block.getWorld() != this.centerPoint.getWorld())
			return false; 
	    if (numberInRange(block.getX(), getCorner_one().getX(), getCorner_four().getX()))
	    	return numberInRange(block.getZ(), getCorner_one().getZ(), getCorner_four().getZ()); 
	    return false;
	}
	  
	public boolean equals(Object o) {
		if (this == o)
			return true; 
	    if (!(o instanceof Selection))
	    	return false; 
	    Selection selection = (Selection)o;
	    return getCenterPoint().equals(selection.getCenterPoint());
	}
	  
	public int hashCode() {
		return Objects.hash(new Object[] { getCenterPoint() });
	}
	  
}