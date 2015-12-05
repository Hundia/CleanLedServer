package src.ledserver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import src.ledserver.comments.db.CommentsDb;
import src.ledserver.comments.db.ICommentsDbAPI;
import src.ledserver.udp.ledServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * A sample showing use of a Service to retrieve data in a background thread.
 * Selecting the Refresh button restarts the Service.
 *
 * @see javafx.collections.FXCollections
 * @see javafx.concurrent.Service
 * @see javafx.concurrent.Task
 * @see javafx.scene.control.ProgressIndicator
 * @see javafx.scene.control.TableColumn
 * @see javafx.scene.control.TableView
 */
public class LedFxManager extends Application {

    private IntegerProperty brightness = new SimpleIntegerProperty();
	Logger logger = Logger.getLogger(LedFxManager.class);
	private static final int LONG_SLEEP_TOASTED = 8000;
	private static final int SHORT_SLEEP_DIDNT_TOAST = 1000;
	private static final int NUMBER_OF_SCREENS = 1;
	ledServer server = null;
	private Scene scene;
	private ArrayList<imageListContainer> screensList = new ArrayList<>();
    private HBox box;
    private int stripWidth;
    private int	stripHeight;
    private int sleepBetweenCommentsInMili = SHORT_SLEEP_DIDNT_TOAST;
    private int toastXpos;
    private int toastYpos;
    private int fontSize;
    Stage primary;
    
    //	Used to access the comments database of the system statically Via
    //	a single API.
    public static ICommentsDbAPI CommentsDb = new CommentsDb();
    
    @Override public void start(Stage primaryStage) throws Exception {
       
    	//	Init Logger
    	BasicConfigurator.configure();
    	
    	//	Get app arguments
    	//	TODO - Explain parameters
    	final Parameters params = getParameters();
        final List<String> parameters = params.getRaw();
        System.out.println(parameters);
        
        primary = primaryStage;
        int wait_for_scene_dur_in_mili = Integer.valueOf(parameters.get(4));
        stripWidth = Integer.valueOf(parameters.get(5));
        stripHeight = Integer.valueOf(parameters.get(6));
        toastXpos = Integer.valueOf(parameters.get(7));
        toastYpos = Integer.valueOf(parameters.get(8));
        fontSize = Integer.valueOf(parameters.get(9));
        StackPane sPane = new StackPane();
        File f = new File(parameters.get(3));
        ImageView img = new ImageView(f.toURI().toString());
        img.setFitWidth(180);
        img.setFitHeight(180);
        box = new HBox();
        Locale.setDefault(new Locale("iw", "IL"));
        StackPane.setAlignment( img, Pos.TOP_RIGHT );
        sPane.getChildren().addAll(box);

        //	Init the comments DB, expected to receive the XML and its Scheme
        CommentsDb.init(parameters.get(12));
        
		for (int i = 0; i < NUMBER_OF_SCREENS; i++) {
			imageListContainer tmp = new imageListContainer(stripWidth,stripHeight);
			tmp.init(parameters.get(0), parameters.get(1), parameters.get(2));
			
			//	Add the container for the screens list
			screensList.add(tmp);
			box.getChildren().add(screensList.get(i).rootLayout);
		}
        
		
		//	Set brightness control
		Parent prnt = sPane;
	    final DoubleBinding colorValue = brightness.multiply(2.55);
        StringExpression styleString = Bindings.format("-fx-base:rgb(%1$.0f , %1$.0f, %1$.0f)", colorValue);
        brightness.set(0);
        prnt.styleProperty().bind(styleString);
                
		// Show the scene containing the root layout.
//		scene = new Scene(box);
		scene = new Scene(prnt);
		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.UNDECORATED);
//		primaryStage.setX(1280);

		primaryStage.setX(Integer.valueOf(parameters.get(10)));
		primaryStage.setY(Integer.valueOf(parameters.get(11)));
		
		primaryStage.setHeight(stripHeight);
		primaryStage.setWidth(stripWidth);
        primaryStage.show();
        
        //initBrightnessControl(primaryStage);
        
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	SwipeRight(0);
            	}
        });
        
	    server = new ledServer(10007,this,wait_for_scene_dur_in_mili);
	    Thread t = new Thread(server);
	    t.start();

	    Task<Integer> task = new Task<Integer>() {
	        @Override protected Integer call() throws Exception {
	            while(true) {

	            	System.out.println("sleeping for: " + sleepBetweenCommentsInMili);
			    	try {
						Thread.sleep(sleepBetweenCommentsInMili);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    			
						Platform.runLater(new Runnable() {
						    @Override
						    public void run() {
					    		String comment = screensList.get(0).getCommentForDisplay();
					    		if(null != comment)
					    		{
							    	Toast toast = new Toast();
						    		TextArea tf = new TextArea();     
						    		tf.setStyle("-fx-font-size: " + fontSize);
						    		tf.setText(comment);
						    		tf.setPrefRowCount(2);
						    		tf.setWrapText(true);
						    		tf.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
						    		tf.positionCaret(tf.getText().length());
						    		toast.setContent(tf);
						    		toast.setDuration(Toast.DURATION_LONG);
						    		System.out.println("Toasting - " + comment);
						    		toast.show(primary,toastXpos,toastYpos);	
						    		sleepBetweenCommentsInMili = LONG_SLEEP_TOASTED;
					            	System.out.println("Decided for: LONG_SLEEP_TOASTED");
								}
					    		else {
					    			sleepBetweenCommentsInMili = SHORT_SLEEP_DIDNT_TOAST;
					            	System.out.println("Decided for: SHORT_SLEEP_DIDNT_TOAST");
					    		}
						    }
						});
				}
		    }         
	    };	    	
	
	    new Thread(task).start();

	    
    }
    
    private void initBrightnessControl(Stage primaryStage) {

        Label label = new Label();
        label.textProperty().bind(Bindings.format("Brightness: %1$2d %%", brightness));

        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(100);
        slider.valueProperty().bindBidirectional(brightness);

        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(label, slider);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initOwner(primaryStage);

        stage.show();
    }
	public void setBrightnessLevel(int screen, int level) {
		logger.info("Received request to adjust brightness level to: " + level 
				+ " from: " + brightness.get());
		screensList.get(screen).setImageBrightness(level);
	}
	
    public static void main(String[] args) { 
    		Application.launch(args); 
    	}

	public void SwipeRight(int index) {
		screensList.get(index).SwipeRight();
	}

	/**
	 * Modes: 0 = back, 1 = pic, 2 = play scenematic
	 * Replaces display mode at the corresponding screen
	 * @param newMode
	 */
	public void replaceDisplayMode(int screen,int newMode) {
		screensList.get(screen).replaceDisplayMode(newMode);
	}
	
	public void SwipeLeft(int screen) {
		screensList.get(screen).SwipeLeft();
		
	}

	public void handleAddComment2Pic(int screen, String comment) {

		screensList.get(screen).addTextToCurrentPic(comment);
		
	}

	public void handleDeleteCommentForCurrentPic(int screen) {
		screensList.get(screen).removeCommentsForActivePic();
		
	}
}
