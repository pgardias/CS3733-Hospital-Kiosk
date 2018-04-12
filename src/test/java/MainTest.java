/*
import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Database.*;
import edu.wpi.cs3733d18.teamp.Exceptions.EdgeNotFoundException;
import edu.wpi.cs3733d18.teamp.Exceptions.NodeNotFoundException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;


public class MainTest extends ApplicationTest{
    private static DBSystem database;
    private static DBSystem data;
    private FxAssert fxAssert;
    private Node node;
    @BeforeClass
    public static void initiate(){
        data = DBSystem.getInstance();
        data.init();
        try{
            data.updateStorage();
        } catch (NodeNotFoundException | EdgeNotFoundException e){
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws Exception {
        fxAssert = new FxAssert();
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @AfterClass
    public static void shutOff(){
        data.shutdown();
    }

    private void adminLogin( String userName, String password){

        clickOn("#admin-button");
        clickOn("#usernameTxt");
        write(userName);
        clickOn("#passwordTxt");
        write(password);
        clickOn("#loginButton");
    }

    private void testNewNode(String longName, String x2, String y2, String x3, String y3, String floor, String building, String type){
        clickOn("#addNewButton");
        clickOn("#longNameTxt");
        write(longName);
        clickOn("#nodex2Txt");
        write(x2);
        clickOn("#nodey2Txt");
        write(y2);
        clickOn("#nodex3Txt");
        write(x3);
        clickOn("#nodey3Txt");
        write(y3);
        clickOn("#floors").clickOn(floor);
        clickOn("#buildingComboBox").clickOn(building);
        clickOn("#nodeTypeComboBox").clickOn(type);
        clickOn("#submitFormButton");
    }

    private void testTouchNewNode(int x2d, int y2d, String longName,String floor, String building, String type){

        clickOn("#addNewButton");
        clickOn(x2d,y2d);
        clickOn("#longNameTxt");
        write(longName);
        clickOn("#floors").clickOn(floor);
        clickOn("#buildingComboBox").clickOn(building);
        clickOn("#nodeTypeComboBox").clickOn(type);
        clickOn("#submit");
    }
    private void openEmployeeTable(){
        clickOn("#openEmployeeInfoPageButton");
        sleep(5000);

    }

    private void serviceLogin(String userName, String password){
        clickOn("#service-button");
        clickOn("#usernameTxt");
        write(userName);
        clickOn("#passwordTxt");
        write(password);
        clickOn("#loginButton");
    }

    private void newLanguageRequest(){
        clickOn("#newRequestButton");
        clickOn("#serviceRequestMenu").clickOn("#languageInterpreter");

    }

    private void goBack(){
        clickOn("#backButton");
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/HomeScreen.fxml"));
        primaryStage.setTitle("Brigham & Women's Hospital Kiosk");
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream( "/img/icons/favicon-16x16.png")));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream( "/img/icons/favicon-32x32.png")));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream( "/img/icons/favicon-64x64.png")));
        primaryStage.setScene(new Scene(root));
        primaryStage.setFullScreen(true);
        primaryStage.show();

    }



    @Test
    public void clickOnAdmin(){
        adminLogin("admin","password");
        //testNewNode("test", "12","12","12","12", "02", "Tower", "CONF");
        openEmployeeTable();
        goBack();
    }

    @Test
    public void clickOnServiceRequest(){
        serviceLogin("admin", "password");
        newLanguageRequest();
    }
}*/