package mapmaker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;



import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import mapmaker.map.MapArea;
import mapmaker.map.shapes.PolyShape;
import mapmaker.map.tools.ToolState;
import mapmaker.map.tools.Tools;

import javafx.stage.Stage;


/**
 * The start of the MapMaker Application, initializes GUI components and</br>
 * creates connections to managing classes.
 * @author Adriano Dramisino
 *
 */
public class MapMaker extends Application{
	
	/**
	 * <p>
	 * these static final fields are file and directory paths for this application.</br>
	 * </p>
	 */
	private static final String INFO_PATH      = "/resources/icons/info.txt";
	private static final String HELP_PATH      = "/resources/icons/help.txt";
	private static final String CREDITS_PATH   = "/resources/icons/credits.txt";
	private static final String CSS_PATH       = "/resources/css/style.css";
	
	/**
	 * <p>
	 * these static final fields represent the number of sides for the given {@link PolyShape}.</br>
	 * </p>
	 */
	private static final int LINE      = 2;
	private static final int TRIANGLE  = 3;
	private static final int RECTANGLE = 4;
	private static final int PENTAGON  = 5;
	private static final int HEXAGON   = 6;
	
	/**
	 * local {@link MapArea} object.
	 */
	private MapArea map;

	
	/**
	 * <p>
	 * if current map has been saved or not. Used to prompt user with a save request</br>
	 * before creating a new map and losing their work, or before loading a .map and </br>
	 * losing their work.
	 * </p>
	 */
	private static boolean SAVED;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		map = new MapArea();
		ToolBar bottomBar = new ToolBar(new Label("Tool: "), new Separator(), new Label("Options: {}"));

		MenuBar menuBar = new MenuBar(
				new Menu("File", null, 
						makeMenuItem("New", e-> newMap(primaryStage)), 
						makeMenuItem("Open", e-> loadMap(primaryStage)),
						makeMenuItem("Save", e-> saveMap(primaryStage)),
						new SeparatorMenuItem(),
						makeMenuItem("Exit", e-> exit(primaryStage))),
				new Menu("Help", null, 
						makeMenuItem("Credit", e-> displayCredit()), 
						makeMenuItem("Info", e-> displayInfo()),
						new SeparatorMenuItem(),
						makeMenuItem("Help", e-> displayHelp()))
				);
		
		ToolBar sideBar =  new ToolBar(
				makeButton("Select", 
						e->ToolState.state().setTool(Tools.Select),
						f->map.setCursor(Cursor.DEFAULT)),
				makeButton("Move", 
						e->ToolState.state().setTool(Tools.Move),
						f->map.setCursor(Cursor.MOVE)),
					makeMenuButton("Room",
							makeMenuItem("Line", e-> setRoom(LINE)),
							makeMenuItem("Triangle", e-> setRoom(TRIANGLE)),
							makeMenuItem("Rectangle", e-> setRoom(RECTANGLE)),
							makeMenuItem("Pentagon", e-> setRoom(PENTAGON)),
							makeMenuItem("Hexagon", e-> setRoom(HEXAGON))),
				makeButton("Path", 
						e->ToolState.state().setTool(Tools.Path),
						f->map.setCursor(Cursor.DEFAULT)),
				makeButton("Erase", 
						e->ToolState.state().setTool(Tools.Erase),
						f->map.setCursor(Cursor.HAND)),
				makeButton("Door", 
						e->ToolState.state().setTool(Tools.Door),
						f->map.setCursor(Cursor.DEFAULT))
				);
		
		root.setCenter(map);
		root.setTop(menuBar);
		root.setLeft(sideBar);
		root.setBottom(bottomBar);
		
		sideBar.setOrientation(Orientation.VERTICAL);
		sideBar.setId("ToolBar");
		bottomBar.setPrefWidth(root.getWidth());
		
		Scene scene = new Scene(root,1000,600);
		scene.getStylesheets().add(MapMaker.class.getResource(CSS_PATH).toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Map Maker");
		primaryStage.show();
	}
	

	
	/**
	 * 
	 * @param name - name to be displayed on {@link MenuItem} and used as {@link MenuItem#setId(String)} for CSS.
	 * @param items - {@link MenuItem}'s that belong to the Room {@link Button}
	 * @return {@link MenuButton}
	 */
	private MenuButton makeMenuButton(String name, MenuItem ... items) {
		MenuButton m = new MenuButton("",null, items);
		m.setId(name);
		m.setOnMouseClicked(e -> map.setCursor(Cursor.CROSSHAIR));
		m.setPopupSide(Side.RIGHT);
		return m;
	}
	
	/**
	 * <p>
	 * create a {@link MenuItem}.</br>
	 * </p>
	 * @param name - name to be displayed on {@link MenuItem} and used as {@link MenuItem#setId(String)} for CSS.
	 * @param handler - {@link EventHandler} object be called when {@link MenuItem} is clicked.
	 * @return created {@link MenuItem}.
	 */
	private MenuItem makeMenuItem( String name, EventHandler<ActionEvent> handler) {
		Label icon = new Label();
		icon.setId( name + "-icon");
		MenuItem item = new MenuItem(name, icon);
		item.setId(name);
		item.setOnAction(handler);
		return item;
	}
	
	/**
	 * <p>
	 * create a {@link Button}.</br>
	 * </p>
	 * @param id - used as {@link Button#setId(String)} for CSS.
	 * @param handler - {@link EventHandler} object be called when {@link Button} is clicked.
	 * @param cursor - sets {@link EventHandler<MouseEvent} 
	 * @return created {@link Button}.
	 */
	private Button makeButton(String id, EventHandler<ActionEvent> handler, EventHandler<MouseEvent> cursor) {
		Button b = new Button();
		b.setId(id);
		b.setOnAction(handler);
		b.setOnMouseClicked(cursor);
		return b;
	}
	
	/**
	 * <p>
	 * sets {@link Tool} to Room and sets number of sides on {@link PolyShape}.  
	 * </p>
	 * 
	 * @param shape - number of sides on {@link PolyShape} to be drawn.
	 */
	private void setRoom(int shape) {
		ToolState.state().setTool(Tools.Room);
		ToolState.state().setOption(shape);
	}

	/**
	 * <p>
	 * display an {@link Alert} to show {@link AlertType#INFORMATION}.</br>
	 * </p>
	 * @param title - string to be displayed as title of {@link Alert}
	 * @param message - string content to be displayed in {@link Alert}
	 */
	private void displayAlert( String title, String context) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(loadFile(context));
		alert.show();
	}
	
	/**
	 * <p>
	 * read a file and convert it to one string separated with provided separator.</br>
	 * </p>
	 * @param path - {@link String} object containing the path to desired file.
	 * @param separator - {@link String} object containing the separator
	 */
	private String loadFile(String path) {
		String str = "";
		try {
			str = Files.lines(Paths.get(path)).reduce(str,(a, b)-> a + System.lineSeparator() + b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * <p>
	 * ask the user where they need to save then get the content to write from 
	 * {@link MapAreaSkeleton#convertToString()}.</br>
	 * </p>
	 * @param primary - {@link Stage} object that will own the {@link FileChooser}.
	 */
	private void saveMap( Stage primary){
		//get the file object to save to
		File file = getFileChooser( primary, true);
		if (file==null) {
			SAVED = false;
			return;
		}
			
		try{
			if( !file.exists())
				file.createNewFile();
			Files.write( file.toPath(), map.convertToString().getBytes());
			SAVED = true;
		}catch( IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>
	 * ask the user what file they need to open then pass the content to 
	 * {@link MapAreaSkeleton#convertFromString(java.util.Map)}.</br>
	 * </p>
	 * @param primary - {@link Stage} object that will own the {@link FileChooser}.
	 */
	private void loadMap( Stage primary){
		//get the file object to load from
		File file = getFileChooser( primary, false);
		if (file==null || !file.exists()) {
			return; 
		}
		try{
			//no parallel (threading) here but this is safer
			AtomicInteger index = new AtomicInteger(0);  
			//index.getAndIncrement()/5 means every 5 elements increases by 1
			//allowing for every 5 element placed in the same key
			//for each line in file group every 5 and pass to map area
			if(!SAVED) 
				newMap(primary);
			
			map.clearMap();
			map.convertFromString( Files.lines( file.toPath()).collect( Collectors.groupingBy( l->index.getAndIncrement()/5)));
			
		}catch( IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>
	 * using the {@link FileChooser} open a new window only showing .map extension;
	 * in starting path of {@link MapMaker#MAPS_DIRECTORY}.</br>
	 * this function can be used to save or open file depending on the boolean argument.</br>
	 * </p>
	 * @param primary - {@link Stage} object that will own the {@link FileChooser}.
	 * @param save - if true show {@link FileChooser#showSaveDialog(javafx.stage.Window)} 
	 * 					else {@link FileChooser#showOpenDialog(javafx.stage.Window)}
	 * @return a {@link File} representing the save or load file object
	 */
	private File getFileChooser( Stage primary, boolean save){
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add( new ExtensionFilter( "Maps", "*.map"));
	//	fileChooser.setInitialDirectory( Paths.get( MAPS_DIRECTORY).toFile());
		return save?fileChooser.showSaveDialog( primary):fileChooser.showOpenDialog( primary);
	}
	
	//https://stackoverflow.com/questions/31899275/difference-between-optionalbuttontype-get-and-alert-getresult
	/**
	 * <p>
	 * asks user if they want to save their current map {@link File} before clearing</br>
	 * {@link MapArea} 
	 * </p>
	 * @param primary - {@link Stage} object that will own the {@link FileChooser}.
	 */
	private void newMap(Stage primary) {
		Optional<ButtonType> result = createSaveAlert().showAndWait();
		if(result.isPresent()) {
			if (result.get() == ButtonType.YES) { 
				saveMap(primary);
				if(SAVED) {
					map.clearMap(); 
					SAVED = false;
				}
			}
			else if(result.get() == ButtonType.NO) 
				map.clearMap();
		}
	}
	
	/**
	 * <p>
	 * private helper function to create {@link Alert} dialogue shown before user clears {@link MapArea}.
	 * </p>
	 * @return an {@link Alert} with appropriate values set.
	 */
	private Alert createSaveAlert() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.getButtonTypes().set(0, ButtonType.YES);
		alert.getButtonTypes().set(1, ButtonType.NO);
		alert.setTitle("New");
		alert.setHeaderText("Save Map");
		alert.setContentText("Would you like to save your current work?");
		return alert;
	}
	
	/**
	 * <p>
	 * safely handles user clicking on Exit button. will close Application</br>
	 * immediately if {@link MapMaker#SAVED} is true, and will prompt user to </br>
	 * save their work otherwise.
	 * </p>
	 * @param primary - {@link Stage} object that will own the {@link FileChooser}. 
	 */
	private void exit(Stage primary) {
		if(!SAVED) {
			newMap(primary);
			primary.close();
		}
		else if(SAVED) {
			primary.close();
		}
	}
	
	/**
	 * <p>
	 * these private helper functions display dialogue of the {@link MenuItem}'s which</br>
	 * belong to the Help {@link Menu}.
	 * </p>
	 */
	private void displayCredit() {displayAlert("Credit", CREDITS_PATH);}
	private void displayHelp() {displayAlert("Help", HELP_PATH);}
	private void displayInfo() {displayAlert("Info", INFO_PATH);}
	public static void main(String[] args) {launch(args);}
	@Override
	public void init() throws Exception {super.init();}
	
	/**
	 * <p>
	 * called when JavaFX application is closed or hidden.</br>
	 * </p>
	 */
	@Override
	public void stop() throws Exception {super.stop();}
}