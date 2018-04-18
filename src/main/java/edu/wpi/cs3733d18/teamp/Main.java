package edu.wpi.cs3733d18.teamp;
import edu.wpi.cs3733d18.teamp.Database.DBSystem;
import edu.wpi.cs3733d18.teamp.Exceptions.EdgeNotFoundException;
import edu.wpi.cs3733d18.teamp.Exceptions.NodeNotFoundException;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Pathfinding.PathfindingContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static Settings settings = Settings.getSettings();

    //public static Node kioskNode = new Node("Kiosk", true, 0, 0, 0, 2, Node.floorType.LEVEL_2, Node.buildingType.FRANCIS_45, nodeType.KIOS);
    public static Edge kioskEdge = new Edge();
    public static Employee basicUser = new Employee();
    public static Employee currentUser = basicUser;
    public static PathfindingContext pathfindingContext = new PathfindingContext();

    /**
     * start sets up the primary landing page of the application (home page)
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/home/HomeScreen.fxml"));
        //String path = this.getClass().getResource("/css/home.css").toExternalForm();
        root.getStylesheets().add("/css/home.css");
        primaryStage.setTitle("Brigham & Women's Hospital Kiosk");
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream( "/img/icons/favicon-16x16.png")));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream( "/img/icons/favicon-32x32.png")));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream( "/img/icons/favicon-64x64.png")));
        primaryStage.setScene(new Scene(root));
        primaryStage.setFullScreen(true);
        primaryStage.show();

        settings.setPathfindingContext(pathfindingContext);
        pathfindingContext.setPathfindingContext(settings.getPathfindingSettings());
    }
    //Logs out the current user, employee, or admin so they are no longer the currentUser at the kiosk
    public static void logoutCurrentUser() {
        System.out.println("Logging out user with username " + currentUser.getUserName());
        basicUser.setFirstName("Current");
        basicUser.setLastName("User");
        currentUser = basicUser;

    }

    /**
     * Calls the DBSystem function to set up an instance of the database for access while updating storage
     * @param args
     */
    public static void main(String[] args) {

        basicUser.setFirstName("Current");
        basicUser.setLastName("User");
        DBSystem db = DBSystem.getInstance();
        db.init();

        try {
            System.out.println("Loading data into persistent storage...");
            db.updateStorage();
        } catch (NodeNotFoundException n) {
            n.printStackTrace();
            System.out.println(n.getNodeID());
        } catch (EdgeNotFoundException e) {
            e.printStackTrace();
            System.out.println();
        }


        launch(args);
        db.shutdown();
    }
}