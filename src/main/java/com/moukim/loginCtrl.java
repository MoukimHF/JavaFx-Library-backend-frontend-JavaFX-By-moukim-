package com.moukim;

import com.moukim.models.Admin;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.SessionId;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
// Login View Controller
public class loginCtrl {

    // JavaFx variables
    @FXML
    public Pane signinPane;
    @FXML
    public Pane signUpPane;

    @FXML
    public Pane facebookSignUpPane;

    @FXML
    private Pane adminPane;

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
    @FXML
    public TextField txtUsernameReg;
    @FXML
    public TextField txtPasswordReg;
    @FXML
    public TextField txtPasswordConfReg;

    @FXML
    public ProgressBar passwordChecker;
    Timer timer;
    public boolean loggedIN =false;
    public String username ;
    private String emailAfterFacebookLogin;
    static int totalAttempts = 3;
    int clicked = 0;
    // a helper function to check the hashed password to the entered password
    public boolean checkPassword( String password , String cryptedPassword){
        if(BCrypt.checkpw(password,cryptedPassword))
            return true;
        else
            return false ;
    }

    //handling user authentification
    public void handleAuthentification ( ActionEvent event )throws IOException {
        clicked++;
        System.out.println(clicked);
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
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("authentification Dialog");
                        alert.setHeaderText("Incorrect password please try again ");
                        alert.showAndWait();
                        totalAttempts--;
                        System.out.println(totalAttempts);
                    }




            } else{
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
                    alert.setHeaderText("There is no user registered under this username");
                    alert.showAndWait();
                }


            }
            // j'ai ajouté cette foncionalité pour que l'utilisateur ne puisse jamais faire plusieur tentatives
            // de connexion incorrectes
            // j'ai implementé une petite fonction qui calcule le nombre de tentative quand elle est egale à 5
            // le button #disabled jusqu'a le timer arrive à 20(juste bsh manab9ash nestana barsha )
            if (clicked == 3) {

                System.out.println("Maximum number of attempts exceeded");

                    btnSignin.setDisable(true);

                timer = new Timer();

                timer.schedule(new RemindTask(), 20000);


            }
            con.close();
        } catch (ClassNotFoundException | SQLException e1) {
            e1.printStackTrace();
        }

    }
    class RemindTask extends TimerTask {
        public void run() {
            System.out.println("Time's up!");
            if (btnSignin.isDisabled()) btnSignin.setDisable(false);
            else btnSignin.setDisable(true);
            timer.cancel(); //Terminate the timer
        }
    }

    //my best part :D
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


    //hiding and showing different pane
    public void handleSignUp ( ActionEvent event ) {

        signinPane.setVisible(false);
        signUpPane.setVisible(true);
        facebookSignUpPane.setVisible(false);

    }

    //hiding and showing different pane
    public void handleSigninPage ( ActionEvent event ) {

        signUpPane.setVisible(false);
        signinPane.setVisible(true);
        facebookSignUpPane.setVisible(false);

    }


    // initializing hibernate session
    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static SessionFactory buildSessionFactory() {
        // Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    // a helper funcion to find a user by id using hibernate
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

    //adding a user using hibernate
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

    //creating an account and validating the user's info to check if the username exists or not
    // adding a user with hibernate
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

    //what happens after the user sign up with facebook :D
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

    // implementing a nice progressive bar to calculate the user password strength
    // i've wrote this algorithm for the first time
    private static int calculatePasswordStrength(String password) {
        //total score of password
        int iPasswordScore = 0;

        if( password.length() < 4 )
            return 0;
        else if( password.length() >= 7 )
            iPasswordScore += 2;
        else
            iPasswordScore += 1;

        //if it contains one digit, add 2 to total score
        if( password.matches("(?=.*[0-9]).*") )
            iPasswordScore += 4;

        //if it contains one lower case letter, add 2 to total score
        if( password.matches("(?=.*[a-z]).*") )
            iPasswordScore += 3;

        //if it contains one upper case letter, add 2 to total score
        if( password.matches("(?=.*[A-Z]).*") )
            iPasswordScore += 3;

        //if it contains one special character, add 2 to total score
        if( password.matches("(?=.*[~!@#$%^&*()_-]).*") )
            iPasswordScore += 5;

        return iPasswordScore;


    }
        public void progressmove ( KeyEvent keyEvent ) {

            System.out.println(calculatePasswordStrength(txtPasswordReg.getText()));
            System.out.println(passwordChecker);
            int strength = calculatePasswordStrength(txtPasswordReg.getText());
            if(strength==0){
                passwordChecker.setProgress(0.04);
            }
            if(strength>0 && strength<=2){
                passwordChecker.setProgress(0.2);
                System.out.println(passwordChecker);

            }
            else if(strength>2 && strength<=3){
                passwordChecker.setProgress(0.3);
            }
            else if(strength>3 && strength<=5){
                passwordChecker.setProgress(0.35);
            }
            else if(strength>5 && strength<=6){
                passwordChecker.setProgress(0.4);
            }
            else if(strength>6 && strength<=7){
                passwordChecker.setProgress(0.45);
            }
            else if(strength>7 && strength<=8){
                passwordChecker.setProgress(0.5);
            }
            else if(strength>8 && strength<=10){
                passwordChecker.setProgress(0.6);
            }
            else if(strength>10 && strength<=11){
                passwordChecker.setProgress(0.65);
            }
            else if(strength>11 && strength<=13){
                passwordChecker.setProgress(0.7);
            }
            else if(strength>13 && strength<=15){
                passwordChecker.setProgress(0.75);
            }
            else if(strength>15 && strength<=17){
                passwordChecker.setProgress(0.85);
            }
            else if(strength>=17){
                passwordChecker.setProgress(1);
            }

    }

    @FXML
    private Button adminPanelButton;
    @FXML
    private TextField adminTextField;
    @FXML
    private TextField adminPasswordField;


    public void goToAdminPanel ( ActionEvent event ) {
// changing the button text programmatically while hiding and showing different Pane
        if (adminPanelButton.getText()=="user panel"){
            signinPane.setVisible(true);
            signUpPane.setVisible(false);
            facebookSignUpPane.setVisible(false);
            adminPane.setVisible(false);
            adminPanelButton.setText("admin panel");
        }else{
            signinPane.setVisible(false);
            signUpPane.setVisible(false);
            facebookSignUpPane.setVisible(false);
            adminPane.setVisible(true);
            adminPanelButton.setText("user panel");
        }
    }

    public void handleAdminAuth ( ActionEvent event ) throws IOException {


        Admin admin =   findAdminByUsername(adminTextField.getText());
        System.out.println("admin password : "+admin.getPassword());
        if(admin.getPassword()==null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("authentification Dialog");
            alert.setHeaderText("admin Not found :( ");
            alert.showAndWait();
        }
        else if (admin.getPassword().equals(adminPasswordField.getText())){
            System.out.println("yeeeeeeeeeeeeeess");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AdminPanel.fxml"));
            Parent AdminViewParent = loader.load();
            Scene AdminViewScene = new Scene(AdminViewParent);
            AdminPanel secController = loader.getController();
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(AdminViewScene);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("authentification Dialog");
            alert.setHeaderText("incorrect Password :( ");
            alert.showAndWait();
        }





    }
    private static com.moukim.models.Admin findAdminByid( int id){
        //open session
        Session session = sessionFactory.openSession();
        //retreive the persistent object
        com.moukim.models.Admin admin = session.get(com.moukim.models.Admin.class,id);
        //close session
        session.close();
        //return object
        return admin;
    }

    private static Admin findAdminByUsername( String username){
Admin ad = new Admin();
       try {
           Session session = sessionFactory.openSession();
           session.getTransaction().begin();
            ad = (Admin) session.createNativeQuery("select * from admin ad where username='"+username+"' ",Admin.class).getSingleResult();
           if(ad instanceof Admin){
               System.out.println("admin found");
               System.out.println(ad);
               session.getTransaction().commit();
               session.close();
               return ad;
           }
           session.getTransaction().commit();
           session.close();


       }catch (NoResultException e ){
           e.getStackTrace();
       }

        return ad;



    }
}
