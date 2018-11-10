package mapmaker.map.tools;




import java.util.function.Consumer;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import mapmaker.map.shapes.controls.ControlPoint;

/**
 * <p>
 * this class extends {@link Rectangle}. it is used to mark and area to select items.</br>
 * this class is meant to be used in 4 phases.
 * <ol>
 * 	<li>create a new SelectionArea in constructor no need to create more than one to recreate it</li>
 * 	<li>in {@link MouseEvent#MOUSE_PRESSED} stage add it to the {@link MapAreaSkeleton#getChildren()}
 * 		and call {@link SelectionArea#start(double, double)} to establish starting location.
 * 		if you have a list of selected item you need to clear it.</li>
 * 	<li>during the {@link MouseEvent#MOUSE_DRAGGED} stage call 
 * 		{@link SelectionArea#end(double, double)} to establish ending position.</li>
 * 	<li>finally in {@link MouseEvent#MOUSE_RELEASED} stage remove selection from {@link MapAreaSkeleton#getChildren()}.
 * 		this is is done to prevent checking for itself. after that call {@link SelectionArea#containsAny(ObservableList, Consumer)} 
 * 		with list of children and a lambda to act when a node is found.at last call 
 * 		{@link SelectionArea#clear()} to reset setting of this class.</li>
 * </ol> 
 * </p>
 * @author Shahriar (Shawn) Emami
 * @version Oct 8, 2018
 */
public class SelectionArea extends Rectangle{

	/**
	 * <p>
	 * starting position of the selection area.</br>
	 * </p>
	 */
	private Point2D start;

	/**
	 * <p>
	 * create a selection are with opacity of 0.4, stroke width of 2, 
	 * fill of {@link Color#LIGHTGRAY} and stroke of {@link Color#GRAY}.</br>
	 * create this class once in the constructor.</br>
	 * </p>
	 */
	
	public SelectionArea(){
		super();
		setOpacity( .4);
		setStrokeWidth( 2);
		setStroke( Color.GRAY);
		setFill( Color.LIGHTGRAY);
	}

	/**
	 * <p>
	 * establish what is the starting position of the selection area.</br>
	 * to be called in {@link MouseEvent#MOUSE_PRESSED}.</br>
	 * </p>
	 * @param x - starting x position
	 * @param y - starting y position
	 */
	public void start( double x, double y){
		start = new Point2D( x, y);
		setX( x);
		setY( y);
	}

	/**
	 * <p>
	 * establish what is the ending position of the selection area.</br>
	 * to be called in {@link MouseEvent#MOUSE_DRAGGED}.</br>
	 * </p>
	 * @param x - ending x position
	 * @param y - ending y position
	 */
	public void end( double x, double y){
		double width = x - start.getX();
		double height = y - start.getY();
		setX( width < 0 ? x : start.getX());
		setY( height < 0 ? y : start.getY());
		setWidth( Math.abs( width));
		setHeight( Math.abs( height));
	}

	/**
	 * <p>
	 * reset all setting of this shape to zero, width, height, x and y.</br>
	 * to be called in {@link MouseEvent#MOUSE_RELEASED}.</br>
	 * </p>
	 */
	public void clear(){
		setX( 0);
		setY( 0);
		setWidth( 0);
		setHeight( 0);
	}

	/**
	 * <p>
	 * check if given node is in the selection area.</br>
	 * </p>
	 * @param node - {@link Node} object to be checked
	 * @return true if it is in the selection area
	 */
	public boolean contains( Node node){
		if (!(node instanceof ControlPoint)) {
			return false;
		}
		return getBoundsInLocal().contains( node.getBoundsInLocal());
	}


	/**
	 * <p>
	 * for each {@link Node} in given list that is inside selection execute lambda {@link Consumer}.</br>
	 * to be called in {@link MouseEvent#MOUSE_RELEASED}.</br>
	 * </p>
	 * @param nodes - {@link Node} object to be checked
	 * @param filter - {@link Node} object to be checked
	 */
	public void containsAny( ObservableList< Node> nodes, Consumer< Node> filter){
		nodes.filtered( this::contains).forEach( filter);
	}
	

}