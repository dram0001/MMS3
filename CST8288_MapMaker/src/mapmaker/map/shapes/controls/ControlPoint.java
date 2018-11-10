package mapmaker.map.shapes.controls;

import javafx.beans.value.ChangeListener;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import mapmaker.map.features.Movable;

/**
 * this class creates a one direction binding using lambdas passed to
 * center properties of {@link Circle}. 
 * 
 * @author Shahriar (Shawn) Emami
 * @version Oct 8, 2018
 */
public class ControlPoint extends Circle implements Movable {
		
	/**
	 * <p>
	 * create a {@link ControlPoint} center around x and y.</br>
	 * by default also create the {@link Circle} with stroke width of 5 and {@link Color#GRAY}.</br>
	 * </p>
	 * @param x - center location of of circle on x axis
	 * @param y - center location of of circle on y axis
	 */
		public ControlPoint(double x, double y){
			super(x,y,5,Color.GRAY);
		}

		/**
		 * <p>
		 * add 2 {@link ChangeListener} for each {@link Circle#centerXProperty()} and {@link Circle#centerYProperty()}.</br>
		 * Relevant {@link ChangeListener} will be called when when either 
		 * {@link Circle#centerXProperty()} or {@link Circle#centerYProperty()} are updated.</br>
		 * </p>
		 * @param x - {@link ChangeListener} to be added as listener to {@link Circle#centerXProperty()}
		 * @param y - {@link ChangeListener} to be added as listener to {@link Circle#centerYProperty()}
		 */
		public void addChangeListener(ChangeListener<Number> x, ChangeListener<Number> y) {
			centerXProperty().addListener(x);
			centerYProperty().addListener(y);	
		}

		/**
		 * <p>
		 * move the control point by adding distance traveled, not new position.</br>
		 * </p>
		 * @param dx - distance traveled in x direction
		 * @param dy - distance traveled in y direction
		 */
		public void translate(double dx, double dy) {
			centerXProperty().set(centerXProperty().get() + dx);
			centerYProperty().set(centerYProperty().get() + dy);
		}
}