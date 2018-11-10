package mapmaker.map.shapes.controls;

import mapmaker.map.features.Movable;
import mapmaker.map.shapes.PolyShape;

/**
 * <p>
 * This class binds two or more {@link PolyShape} objects together.
 * @author Adriano
 * </p>
 *
 */
public class Path extends PolyShape implements Movable{
	/**<p>
	 * number of sides for {@link Path} object.
	 * </p>
	 */
	private static final int PATH = 2;
	
	/**
	 * <p>
	 * call to {@link PolyShape} constructor, instantiates 2 sided {@link PolyShape}
	 * </p>
	 */
	public Path() {
		super(PATH);
	}

	/**<p>
	 * moves path and everything connected to it.
	 * </p>
	 */
	@Override
	public void translate(double dx, double dy) {
		for(Movable m : super.getLocks()) 
			m.translate(dx, dy);
	}

	
}
