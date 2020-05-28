package com.moukim;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;
import io.github.bonigarcia.wdm.WebDriverManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.SessionId;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.IOException;
import java.sql.*;

public class loginCtrl {
    @FXML
    public Pane signinPane;
    @FXML
    public Pane signUpPane;

    @FXML
    public Pane facebookSignUpPane;

    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;
    @FXML
    private Button btnSignin;
    @FXML
    private Label welcomeFbUser;
    @FXML
    private PasswordField txtPasswordRegAfterFacebook;

    @FXML
    private PasswordField txtPasswordRegConfAfterFacebook;
    public boolean loggedIN =false;
    public String username ;
    private String emailAfterFacebookLogin;

    public boolean checkPassword( String password , String cryptedPassword){
        if(BCrypt.checkpw(password,cryptedPassword))
            return true;
        else
            return false ;
    }

    public void handleAuthentification ( ActionEvent event )throws IOException {

        try {
            System.out.println("hello from the other side ");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/test1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","moukim","Qsdf12300");
            Statement stmt=con.createStatement();
            String sql = "select * from user where username = '"+txtUsername.getText()+"'";
            ResultSet rs1=stmt.executeQuery(sql);

            if (rs1.next()) {
                String password2 = rs1.getString("password");
                System.out.printf(" %n %s %n",password2);
                boolean authorised = checkPassword(txtPassword.getText(),password2);
                if(authorised) {
                    loggedIN = true;
                    int id = rs1.getInt("id");
                    username = rs1.getString("username");
                    System.out.printf("Welcome %s your id is %d %n" , username , id);


                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("dashboard.fxml"));
                    Parent dashboardViewParent = loader.load();
                    Scene dashboardViewScene = new Scene(dashboardViewParent);
                    dashboardCtrl secController = loader.getController();


                    FXMLLoader loader1 = new FXMLLoader();
                    loader1.setLocation(getClass().getResource("settingsProfile.fxml"));
                    Parent settingsViewParent = loader1.load();
                    Scene settingsViewScene = new Scene(settingsViewParent);
                    SettingsProfile settingsController = loader1.getController();
                    secController.myFunction(username);
                    secController.getId(id);
                    settingsController.givemeID(id);
                    settingsController.givemeUsername(username);
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(dashboardViewScene);
                }
                else{
//
                    if(txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()){
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("empty field");
                        alert1.setHeaderText("Looks like some information are missing");
                        alert1.showAndWait();
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("authentification Dialog");
                        alert.setHeaderText("Looks like your email or password are incorrect");
                        alert.showAndWait();
                    }


                }
            }

            con.close();
        } catch (ClassNotFoundException | SQLException e1) {
            e1.printStackTrace();
        }

    }


    public void facebookAuth ( ActionEvent actionEvent ) {

        String domain = "https://google.com";
        String appId = "236734161099675";

        String authUrl = "https://graph.facebook.com/oauth/authorize?type=user_agent&client_id="+appId+"&redirect_uri="+domain+"&scope=user_about_me,user_birthday,email";


        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        SessionId session =((ChromeDriver)driver).getSessionId();
        System.out.println("Session id: " + session.toString());
        driver.get(authUrl);
        String accessToken;

        while(true){

            if(!driver.getCurrentUrl().contains("facebook.com")){
                String url = driver.getCurrentUrl();
                accessToken = url.replaceAll(".*#access_token=(.+)&data_access.*", "$1");
                System.out.printf("access token = %s %n",accessToken);
                System.out.println("Session id: " + session.toString());
                driver.quit();
                System.out.println("Session id: " + session.toString());
                FacebookClient fbClient = new DefaultFacebookClient(accessToken, Version.LATEST);
                User user = fbClient.fetchObject("me",User.class, Parameter.with("fields", "id,first_name,last_name,email"));

                System.out.printf("user name : %s %n",user.getName());
                System.out.printf("user email : %s %n",user.getEmail());

                emailAfterFacebookLogin=user.getEmail();
                signinPane.setVisible(false);
                signUpPane.setVisible(false);
                facebookSignUpPane.setVisible(true);
                welcomeFbUser.setText("Welcome "+user.getEmail());
//                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
//                alert1.setTitle("your facebook email");
//                alert1.setHeaderText(user.getEmail());
//                alert1.showAndWait();

            }
        }
    }


    public void handleSignUp ( ActionEvent event ) {

        signinPane.setVisible(false);
        signUpPane.setVisible(true);
        facebookSignUpPane.setVisible(false);

    }

    public void handleSigninPage ( ActionEvent event ) {

        signUpPane.setVisible(false);
        signinPane.setVisible(true);
        facebookSignUpPane.setVisible(false);

    }

    @FXML
    public TextField txtUsernameReg;
    @FXML
    public TextField txtPasswordReg;
    @FXML
    public TextField txtPasswordConfReg;

    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static SessionFactory buildSessionFactory() {
        // Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }
    private static com.moukim.models.User findUserByid( int id){
        //open session
        Session session = sessionFactory.openSession();
        //retreive the persistent object
        com.moukim.models.User user = session.get(com.moukim.models.User.class,id);
        //close session
        session.close();
        //return object
        return user;
    }


    private static void add( com.moukim.models.User user) {
        // Open a session
        Session session = sessionFactory.openSession();
        // Begin a transaction
        session.beginTransaction();

        // Use the session to update the contact
        session.persist(user);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }
    public void createAcount ( ActionEvent event ) throws IOException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con1= DriverManager.getConnection("jdbc:mysql://localhost:3306/test1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","moukim","Qsdf12300");
            Statement stmt1=con1.createStatement();
            String sql1 = "select * from user where username = '"+txtUsernameReg.getText()+"'";
            ResultSet rs1=stmt1.executeQuery(sql1);
            if (rs1.next()) {
                System.out.println("exisssssssst");
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("username already exists please choose another");
                alert1.setHeaderText("username exists");
                alert1.showAndWait();
            }
            else if (txtUsernameReg.getText().isBlank() || txtPasswordReg.getText().isBlank()){
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("no field can be empty");
                alert1.setHeaderText("empty field(s)");
                alert1.showAndWait();
            }
            else if ((txtPasswordConfReg.getText().equals(txtPasswordReg.getText()))==false){
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("please check your password confirmation");
                alert1.setHeaderText("password not match");
                alert1.showAndWait();

            }
            else {
                System.out.println("not exisssssssst");
                com.moukim.models.User user = new com.moukim.models.User();
                String password=txtPasswordReg.getText();
                String cryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

                user.setUsername(txtUsernameReg.getText());
                user.setPassword(cryptedPassword);
                add(user);
                signUpPane.setVisible(false);
                signinPane.setVisible(true);
            }
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void createAcountAfterFacebook ( ActionEvent event ) {
    String username =emailAfterFacebookLogin;
try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con1= DriverManager.getConnection("jdbc:mysql://localhost:3306/test1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","moukim","Qsdf12300");
    Statement stmt1=con1.createStatement();
    String sql1 = "select * from user where username = '"+username+"'";
    ResultSet rs1=stmt1.executeQuery(sql1);
    if (rs1.next()) {
        System.out.println("exisssssssst");
        Alert alert1 = new Alert(Alert.AlertType.ERROR);
        alert1.setTitle("username already exists please choose another");
        alert1.setHeaderText("username exists");
        alert1.showAndWait();
    }
    else if (txtPasswordRegAfterFacebook.getText().isBlank()){
        Alert alert1 = new Alert(Alert.AlertType.ERROR);
        alert1.setTitle("no field can be empty");
        alert1.setHeaderText("empty field(s)");
        alert1.showAndWait();
    }
    else if (!(txtPasswordRegAfterFacebook.getText().equals(txtPasswordRegConfAfterFacebook.getText()))){
        Alert alert1 = new Alert(Alert.AlertType.ERROR);
        alert1.setTitle("please check your password confirmation");
        alert1.setHeaderText("password not match");
        alert1.showAndWait();

    }
    else {
        System.out.println("not exisssssssst");
        com.moukim.models.User user = new com.moukim.models.User();
        String password=txtPasswordRegAfterFacebook.getText();
        String cryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

        user.setUsername(username);
        user.setPassword(cryptedPassword);
        add(user);
        facebookSignUpPane.setVisible(false);
        signUpPane.setVisible(false);
        signinPane.setVisible(true);
    }
}catch (ClassNotFoundException e) {
    e.printStackTrace();
} catch (SQLException e) {
    e.printStackTrace();
}



    }
}
