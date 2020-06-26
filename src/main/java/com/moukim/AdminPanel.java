package com.moukim;

import com.moukim.models.Book;
import com.moukim.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class AdminPanel implements Initializable {
    @FXML
    private TableView<User> tblview;
    @FXML
    private TableColumn<User, String> username;

    @FXML
    private TableColumn<User, Integer> id;

    @FXML
    private TableColumn<User,String> firstName;

    @FXML
    private TableColumn<User,String> lastName;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtUserName;

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        // Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }
    @SuppressWarnings("unchecked")
    public List<User> fetshUserss () {
        Session session = sessionFactory.openSession();
        //create a creteria same as where
        Criteria criteria = session.createCriteria(User.class);
        List<User> users = criteria.list();

        session.close();
        return users;
    }

    public void onEdit() {
        // check the table's selected item and get selected item
        if (tblview.getSelectionModel().getSelectedItem() != null) {
            User selectedUser = tblview.getSelectionModel().getSelectedItem();
            txtId.setText(String.valueOf(selectedUser.getId()));
            txtUserName.setText(selectedUser.getUsername());
        }
    }
    public void fetshUsers ( Event event ) {
        ObservableList<User> users = FXCollections.observableArrayList(fetshUserss());

        //make sure the property value factory should be exactly same as the e.g getStudentId from your model class
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        //add data to the table here.
        tblview.setItems(users);

    }

    @Override
    public void initialize ( URL url , ResourceBundle resourceBundle ) {

    }

    // get the user from table to the text field
    public void blabla ( MouseEvent mouseEvent ) {
        if (mouseEvent.getClickCount() > 1) {
            onEdit();
        }
    }

    public void adminLogout ( ActionEvent event ) throws IOException {
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

    public void makeUserAdmin ( ActionEvent event ) {
        Session session = sessionFactory.openSession();
        com.moukim.models.Admin admin = new com.moukim.models.Admin();
        admin.setUsername(txtUserName.getText());
        String newPass = "admin"+txtId.getText();
        admin.setPassword(newPass);
        add(admin);
        Alert alert1 = new Alert(Alert.AlertType.ERROR);
        alert1.setTitle("Admin");
        alert1.setHeaderText("you're now an admin congrats ! your new password is "+newPass);
        alert1.showAndWait();
        txtUserName.setText("");
        txtId.setText("");
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

    private static void add( com.moukim.models.Admin admin) {
        // Open a session
        Session session = sessionFactory.openSession();
        // Begin a transaction
        session.beginTransaction();

        // Use the session to update the contact
        session.persist(admin);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }
    public void deleteUser ( ActionEvent event ) {
        System.out.println(txtId.getText());

        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();
        User user = findUserById(Integer.parseInt(txtId.getText()));
        session.delete(user);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
        txtId.setText("");
        txtUserName.setText("");
    }
}
