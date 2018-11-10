package mapmaker.map.features;

import mapmaker.map.MapArea;

/**
 * <p>
 * Classes implementing this interface will be movable on {@link MapArea}
 * </p>
 * @author Adriano
 *
 */
public interface Movable {

	/**
	 * <p>
	 * implement logic for moving Objects on {@link MapArea}.
	 * </p>
	 * @param dx
	 * @param dy
	 */
	public void translate(double dx, double dy);
}
