package mapmaker.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import mapmaker.map.features.Movable;
import mapmaker.map.shapes.PolyShape;
import mapmaker.map.shapes.controls.ControlPoint;
import mapmaker.map.shapes.controls.Path;
import mapmaker.map.tools.SelectionArea;
import mapmaker.map.tools.ToolState;
import mapmaker.map.tools.Tools;

/**
 * <p>
 * create this class once. this class will hold all control behavior related to shapes.</br>
 * </p>
 * @author Shahriar (Shawn) Emami
 * @version Oct 8, 2018
 */
public class MapArea extends Pane{

	/**
	 * <p>
	 * instead of calling getChildren every time you can call directly the reference of it which is initialized in constructor.</br>
	 * </p>
	 */
	private ObservableList<Node> children;
	
	/**
	 * <p>
	 * {@link List} of selected points using object of {@link SelectionArea} 
	 * </p>
	 */
	private ObservableList<ControlPoint> selectedPoints;
	
	/**
	 * <p>
	 * active shape that is currently being manipulated.</br>
	 * </p>
	 */
	private PolyShape activeShape;
	
	/**
	 * <p>
	 * last location of the mouse.</br>
	 * </p>
	 */
	private double startX, startY;
	
	/**
	 * <p>
	 * Reference to ToolSate so you don't have to call ToolSate.getState() every time.</br>
	 * </p>
	 */
	private ToolState tool;
	
	/**
	 * <p>
	 * object used to select {@link ControlPoint}'s to be used.
	 * </p>
	 */
	private SelectionArea select;
	
	/**
	 * <p>
	 * if true, {@link Tools#MOVE} will use {@link MapArea#moveSelected(EventTarget, MouseEvent)},</br> 
	 * to move selected nodes only.
	 * </p>
	 */
	private boolean SELECTED;
	
	/**
	 * <p>
	 * local instance of {@link Path} class
	 * </p>
	 */
	private Path path;
	
	
	/**
	 * <p>
	 * create a new object and register mouse events.</br>
	 * </p>
	 */
	public MapArea() {
		super();
		tool = ToolState.state();
		children = getChildren();
		registerMouseEvents();
		selectedPoints = FXCollections.observableArrayList();
	}
	
	/**
	 * <p>
	 * helper function to register all helper functions for mouse events.</br>
	 * </p>
	 */
	public void registerMouseEvents() {
		addEventHandler(MouseEvent.MOUSE_PRESSED, this::pressClick);
		addEventHandler(MouseEvent.MOUSE_DRAGGED, this::dragClick);
		addEventHandler(MouseEvent.MOUSE_RELEASED, this::releaseClick);
	}
	
	/**
	 * <p>
	 * this method is called by the JavaFX event system. should not be called manually.</br>
	 * this function will be called when {@link MouseEvent#MOUSE_PRESSED} is triggered.</br>
	 * </p>
	 * @param e - {@link MouseEvent} object
	 */
	public void pressClick(MouseEvent e) {
		e.consume();
		startX = e.getX();
		startY = e.getY();
		switch(activeTool())
		{
			case Door:   break; 
			case Move:   break;
			case Path:  
				path = new Path();
				children.add(path);
				break;
			case Select: 
			
				select = new SelectionArea();
				select.start(startX, startY);
				children.add(select);
				deselectPoints();
				break;
			case Erase:
				erase(e);
				break;
			case Room: 			
				activeShape = new PolyShape(tool.getOption());
				children.add(activeShape);
				break;
			default:
				throw new UnsupportedOperationException( "Cursor for Tool \"" + activeTool().name() + "\" is not implemneted");
		}
	}

	/**
	 * <p>
	 * this method is called by the JavaFX event system. should not be called manually.</br>
	 * this function will be called when {@link MouseEvent#MOUSE_DRAGGED} is triggered.</br>
	 * </p>
	 * @param e - {@link MouseEvent} object
	 */
	public void dragClick(MouseEvent e) {
		e.consume();
		switch(tool.getTool()) 
		{
		case Door:   break;
		case Erase:  break;
		
		case Path:   
			path.reDraw(startX, startY, e.getX(), e.getY(), true);
			break;
		case Select: 
			select.end(e.getX(), e.getY());
			break;
		case Move:
			move(e, e.getTarget());
			break;
		case Room: 
			activeShape.reDraw(startX, startY, e.getX(), e.getY(), true);	
			break;
		default:
			throw new UnsupportedOperationException( "Cursor for Tool \"" + activeTool().name() + "\" is not implemneted");		
		}
	}
	
	
	/**
	 * <p>
	 * this method is called by the JavaFX event system. should not be called manually.</br>
	 * this function will be called when {@link MouseEvent#MOUSE_RELEASED} is triggered.</br>
	 * </p>
	 * @param e - {@link MouseEvent} object
	 */
	public void releaseClick(MouseEvent e) {
		e.consume();
		switch(tool.getTool()) {
		case Door:   break;
		case Move:   break;
		case Path:  
			setPath(e, e.getTarget());
			break;
		case Select: 
			selectPoints();
			break;
		case Erase:  break;
		case Room: 
			activeShape.registerControlPoints();
            children.addAll(activeShape.getControlPoints());
			break;
		default:
			throw new UnsupportedOperationException( "Release for Tool \"" + activeTool().name() + "\" is not implemneted");
	}
	activeShape = null;
		
	}
	
	
	
	/**
	 * <p>
	 * helper function that returns the current {@link Tools}.</br>
	 * </p>
	 * @return current active {@link Tools}
	 */
	private Tools activeTool() {
		return tool.getTool();
	}
	
	/**
	 * <p>
	 * removes selected {@link PolyShape}, does nothing if {@link ControlPoint} is clicked.
	 * </p>
	 * @param e - {@link MouseEvent}.
	 */
	private void erase(MouseEvent e) {
		if(e.getTarget() instanceof PolyShape) {
		children.removeAll(((PolyShape) e.getTarget()).getControlPoints());
		children.remove(e.getTarget());
		}
	}
	
	/**
	 * <p>
	 * Moves {@link Movbable} nodes/
	 * </p>
	 * @param e - {@link MouseEvent} click
	 * @param t - @link {@link MouseEvent#getTarget()}
	 */
	private void move(MouseEvent e, EventTarget t) {
		double dx = e.getX() - startX;
		double dy = e.getY() - startY;
		if(SELECTED) moveSelected(t, e);
		else if(t instanceof Movable) {
			((Movable)e.getTarget()).translate(dx, dy);
		}
		startX = e.getX();
		startY = e.getY();	
	}
	
	
	/**
	 * <p>
	 * called when {@link Tools#MOVE} is the {@link MapArea#activeTool()},</br>
	 * and {@link MapArea#selected} is true
	 * </p>
	 * @param t - returns {@link EventTarget} of object clicked.
	 * @param e - {@link MouseEvent}
	 */
	private void moveSelected(EventTarget t, MouseEvent e) {
		if(t instanceof Movable) {
			for(int i = 0; i < selectedPoints.size(); i ++) {
				selectedPoints.get(i).translate(e.getX()-startX, e.getY()-startY);
			}
		}
	}
	
	/**
	 * <p>
	 * adds {@link ControlPoint}'s contained within {@link MapArea#select} to</br> 
	 * {@link MapArea#selectedPoints} {@link ArrayList} and sets {@link MapArea#selected}</br>
	 * to true.
	 * </p>
	 */
	private void selectPoints() {
		for(int i = 0; i < children.size(); i++) {
			if (select.contains(children.get(i))) {
				((ControlPoint) children.get(i)).setFill(Color.BLACK);
				selectedPoints.add((ControlPoint)children.get(i));
				SELECTED = true;
			}
		}
		select.clear();
	}
	
	/**
	 * <p>
	 * removes {@link ControlPoint}'s from {@link MapArea#selectedPoints} and sets</br>
	 * {@link MapArea#selectedPoints} to false.
	 * </p>
	 */
	private void deselectPoints() {
		if(SELECTED) {
			for(int i = 0; i < selectedPoints.size(); i++) 
				selectedPoints.get(i).setFill(Color.GREY);
			}
			SELECTED = false;
			selectedPoints.clear();
	}
	
	/**
	 * <p>
	 * establishes {@link Path}, adds {@link ControlPoint}'s  of path to @{link PolyShape#locks},</br>
	 * adds Control Points of any {@link PolyShape} that is connected to path.
	 * </p>
	 * @param e - MouseEvent release click
	 * @param t - {@link MouseEvent#getTarget()} of e
	 */
	private void setPath(MouseEvent e, EventTarget t) {
		path.registerControlPoints();
		children.addAll(path.getControlPoints());
		if(t instanceof Movable) {
			path.addLock((Movable)t);
			path.addLock(getFirstContain(e.getX(), e.getY()));
			((PolyShape)t).addLock((Movable)path.getControlPoints()[1]);
			getFirstContain(e.getX(), e.getY()).addLock((Movable)path.getControlPoints()[0]);
		}
	}
	
	/**
	 * <p>
	 * if first {@link ControlPoint} of {@link Path} is contained within a {@link PolyShape} </br>
	 * this method will return said {@link PolyShape}.
	 * </p>
	 * @param x - {@link MouseEvent#getX()}
	 * @param y - {@link MouseEvent#getY()}
	 * @return
	 */
	private PolyShape getFirstContain(double x, double y) {
		for(Node n : children) 
		{
			if (n instanceof PolyShape) 
				if (n.getBoundsInLocal().contains(x,  y))
					return (PolyShape)n;
		}
			
		return null;
	}
	/**
	 * <p>
	 * create a new string that adds all shapes to one string separated by {@link System#lineSeparator()}.</br>
	 * </p>
	 * @return string containing all shapes.
	 */
	public String convertToString(){
		//for each node in children
		return children.stream()
				//filter out any node that is not PolyShape
				.filter( PolyShape.class::isInstance)
				//cast filtered nodes to PolyShapes
				.map( PolyShape.class::cast)
				//convert each shape to a string format
				.map( PolyShape::convertToString)
				//join all string formats together using new line
				.collect( Collectors.joining( System.lineSeparator()));
	}
	
	/**
	 * <p>
	 * create all shapes that are stored in given map. each key contains one list representing on PolyShape.</br>
	 * </p>
	 * @param map - a data set which contains all shapes in this object.
	 */
	public void convertFromString( Map< Object, List< String>> map){
		//for each key inside of map
		map.keySet().stream()
		//create a new PolyShape with given list in map
		.map( k->new PolyShape( map.get( k)))
		//for each created PolyShape
		.forEach( s->{
			children.add( s);
			children.addAll( s.getControlPoints());
		});;
	}
	
	/**
	 * <p>
	 * call this function to clear all shapes in {@link MapAreaSkeleton}.</br>
	 * </p>
	 */
	public void clearMap(){
		children.clear();
	}
	


}