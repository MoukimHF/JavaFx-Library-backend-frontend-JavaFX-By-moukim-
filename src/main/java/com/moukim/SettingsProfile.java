package com.moukim;

import com.moukim.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SettingsProfile implements Initializable {
    private static String welcomeUser;
    private static int userId;
    @FXML
    private TextField fname;
    @FXML
    private TextField lname;
    @FXML
    private TextField Email;
    @FXML
    private TextField Tel;
    @FXML
    private DatePicker Dob;
    @FXML
    private Button SaveBtn;
    @FXML
    private TabPane tabPane;
    @FXML
    private Label dobErr;
    @FXML
    private Label emailErr;

    @FXML
    private Label lNameErr;

    @FXML
    private Label nameErr;

    @FXML
    private Label telErr;


    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static SessionFactory buildSessionFactory() {
        // Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }
    private static User findUserById(int id ){
        //open session
     Session session = sessionFactory.openSession();
        //retreive the persistent object
    User user = session.get(User.class,id);
        //close session
    session.close();
        //return object
        return user;
    }

    private static void delete(User user) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to update the contact
        session.delete(user);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }

    private static void update(User user){
        //open session
    Session session = sessionFactory.openSession();
        //begin transaction
session.beginTransaction();
        //user the session to update the user
session.update(user);
        //commit transaction
session.getTransaction().commit();
        //close session
        session.close();
    }

    public static void givemeUsername(String text) {
        welcomeUser=text;
    }
    public static void givemeID(int id){
        userId= id;
    }

    public void saveProfileInfo ( ActionEvent event ) {

        System.out.printf("user iddddd = %s %n",userId);
        System.out.printf("welcome %s",welcomeUser);

       try {
           User user = findUserById(userId);
           user.setName(fname.getText());
           user.setLastName(lname.getText());
           user.setEmail(Email.getText());
           user.setDob(Dob.getEditor().getText());
           user.setTel(Tel.getText());
           update(user);

       }catch (Exception ex){
           ex.getStackTrace();
       }
       SaveBtn.setOnAction(evt->{

           User user = findUserById(userId);

           try {
               if(fname.getText().isBlank()) {
                   nameErr.setVisible(true);
                   throw new Error("name can't be blank");

               }
               else {
                   nameErr.setVisible(false);
                   fname.setText(user.getName());
               }

           }catch (Error err){
               System.out.printf("%s %n",err.getMessage());

           }

           try {
               if(lname.getText().isBlank()) {
                   lNameErr.setVisible(true);
                   throw new Error("last name can't be blank");

               }
               else {
                   lNameErr.setVisible(false);
                   lname.setText(user.getLastName());
               }

           }catch (Error err){
               System.out.printf("%s %n",err.getMessage());

           }


           try {
               String regex = "^(.+)@(.+)$";
               Pattern pattern = Pattern.compile(regex);
               Matcher matcher = pattern.matcher(Email.getText());

               if(Email.getText().isBlank()) {
                   emailErr.setVisible(true);
                   throw new Error("email can't be blank");

               }
               else if(!matcher.matches()){
                   emailErr.setText("please respect email format");
                   emailErr.setVisible(true);
                   throw new Error("please respect email format");

               }
               else {
                   emailErr.setVisible(false);
                   Email.setText(user.getEmail());
               }

           }catch (Error err){
               System.out.printf("%s %n",err.getMessage());

           }
       });

    }

    public String[] formatDate( String date){

        // analyzing the string
        String[] tokensVal = date.split("/");

        return tokensVal;
    }

    public void goToProfile ( ActionEvent event ) {

        tabPane.getSelectionModel().select(1);
        User user = findUserById(userId);
        fname.setText(user.getName());
        lname.setText(user.getLastName());
        Email.setText(user.getEmail());
        Tel.setText(user.getTel());

        try{
        String[] date = user.getDob().split("/");
        if(date.length<2)
            throw new ArrayIndexOutOfBoundsException();
        else
            dobErr.setVisible(false);
        System.out.print(date);
        System.out.printf("day : %s %n month : %s %n" , date[0] , date[1]);

        Dob.setValue(LocalDate.of(Integer.parseInt(date[2]) , Integer.parseInt(date[1]) , Integer.parseInt(date[0])));

    }
        catch (ArrayIndexOutOfBoundsException dob){
            System.out.printf("date of birth invalid %n %n %s",dob.getStackTrace());
            dobErr.setVisible(true);

        }
    }

    public void goToSettings ( ActionEvent event ) {
        tabPane.getSelectionModel().select(0);

    }



    public void goToDashboard ( ActionEvent event ) throws IOException {
        Parent dashboardViewParent = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        Scene dashboardViewScene = new Scene(dashboardViewParent);
        //this line gets the stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(dashboardViewScene);
        window.show();
    }

    public void logOut ( ActionEvent event ) throws IOException {
        loginCtrl login= new loginCtrl();
        login.loggedIN=false;
        Parent dashboardViewParent = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene dashboardViewScene = new Scene(dashboardViewParent);
        //this line gets the stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(dashboardViewScene);
        window.show();
        System.out.printf("logged in = %s %n",login.loggedIN ? "true":"false");
    }

    @Override
    public void initialize ( URL url , ResourceBundle resourceBundle ) {

    }
}
