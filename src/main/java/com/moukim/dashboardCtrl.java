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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class dashboardCtrl implements Initializable {
    @FXML
    public Label welcomeUser;
    @FXML
    private TextField bookYear;

    @FXML
    private TextArea bookDescription;

    @FXML
    private TextField bookName;

    @FXML
    private TextField bookGenre;

    @FXML
    private TableView<Book> booksTable;
    @FXML
    public TableColumn<Book, Integer> id;

    @FXML
    public TableColumn<Book, String> name;

    @FXML
    public TableColumn<Book, String> dateReleased;
    @FXML
    public TableColumn<Book, String> genre;
    @FXML
    public TableColumn<Book, String> description;

    @FXML
    public
    TabPane dashboardPane;

    public int userId;

    public void saveBookDetails ( ActionEvent event ) {
        EntityManagerFactory entityManagerFactory1 = Persistence.createEntityManagerFactory("org.hibernate.tutorial.jpa");
        Book book = new Book();
            book.setName(bookName.getText());
            book.setGenre(bookGenre.getText());
            book.setDateReleased(bookYear.getText());
            book.setDescription(bookDescription.getText());
            book.setUserId(userId);

        EntityManager entityManager1 = entityManagerFactory1.createEntityManager();
        entityManager1.getTransaction().begin();
        entityManager1.persist(book);
        entityManager1.getTransaction().commit();
        entityManagerFactory1.close();

        clearFields();

    }

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        // Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @SuppressWarnings("unchecked")
    public  List<Book> fetshBookss () {
        Session session = sessionFactory.openSession();
        //create a creteria same as where
        Criteria criteria = session.createCriteria(Book.class);
        criteria.add(Restrictions.eq("userId",userId));
        System.out.printf("user id = %d %n",userId);
        List<Book> books = criteria.list();

        session.close();
        return books;
    }

    private void clearFields() {
        // TODO Auto-generated method stub
        bookName.clear();
        bookDescription.clear();
        bookGenre.clear();
        bookYear.clear();

    }

    public void handleSettingsClick ( ActionEvent event )throws IOException {
        Parent dashboardViewParent = FXMLLoader.load(getClass().getResource("settingsProfile.fxml"));
        Scene dashboardViewScene = new Scene(dashboardViewParent);
        //this line gets the stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(dashboardViewScene);
        window.show();


    }


    public void handleLogout ( ActionEvent event ) throws IOException {
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
        public void myFunction(String text) {
            welcomeUser.setText("Welcome "+text);
        }
      public void getId(int id){
        userId= id;
        }


    public void fetshBooks ( Event event ) {
        ObservableList<Book> books = FXCollections.observableArrayList(fetshBookss());

        //make sure the property value factory should be exactly same as the e.g getStudentId from your model class
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        genre.setCellValueFactory(new PropertyValueFactory<>("Genre"));
        dateReleased.setCellValueFactory(new PropertyValueFactory<>("dateReleased"));
        description.setCellValueFactory(new PropertyValueFactory<>("Description"));
        //add your data to the table here.
        booksTable.setItems(books);

        }


    public void goToAddBook ( ActionEvent event ) {
        dashboardPane.getSelectionModel().select(2);
    }

    public void goToBooks ( ActionEvent event ) {
        dashboardPane.getSelectionModel().select(1);
    }

    public void selectBook ( MouseEvent mouseEvent ) {
        if (mouseEvent.getClickCount() > 1) {
            oneditt();
        }
    }
    private static Book findBookById(int id ){
        //open session
        Session session = sessionFactory.openSession();
        //retreive the persistent object
        Book book = session.get(Book.class,id);
        //close session
        session.close();
        //return object
        return book;
    }
    @FXML
    private TextField bookId;
    public void oneditt() {
        // check the table's selected item and get selected item
        if (booksTable.getSelectionModel().getSelectedItem() != null) {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
            bookId.setText(String.valueOf(selectedBook.getId()));

        }
    }

    @FXML
    private TextField bookNameEd;
    @FXML
    private TextField bookGenreEd;
    @FXML
    private TextField bookYearEd;
    @FXML
    private TextArea bookDescriptionEd;

    public void editBook ( ActionEvent event ) {

        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        bookNameEd.setText(selectedBook.getName());
        bookGenreEd.setText(selectedBook.getGenre());
        bookDescriptionEd.setText(selectedBook.getDescription());
        bookYearEd.setText(selectedBook.getDateReleased());
        dashboardPane.getSelectionModel().select(3);
    }

    private static void update(Book book){
        //open session
        Session session = sessionFactory.openSession();
        //begin transaction
        session.beginTransaction();
        //user the session to update the user
        session.update(book);
        //commit transaction
        session.getTransaction().commit();
        //close session
        session.close();
    }

    public void saveBookDetailsAfterEdit ( ActionEvent event ) {
    Book book = findBookById(Integer.parseInt(bookId.getText()));
    book.setName(bookNameEd.getText());
    book.setDescription(bookDescriptionEd.getText());
    book.setGenre(bookGenreEd.getText());
    book.setDateReleased(bookYearEd.getText());
    update(book);
        dashboardPane.getSelectionModel().select(1);
    }
    public void backToBookAfterNotEdit ( ActionEvent event ) {
        dashboardPane.getSelectionModel().select(1);
    }

    public void deleteBook ( ActionEvent event ) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();
        Book book = findBookById(Integer.parseInt(bookId.getText()));
        session.delete(book);

        session.getTransaction().commit();
        session.close();
       Book selectedItem =  booksTable.getSelectionModel().getSelectedItem();
       booksTable.getItems().remove(selectedItem);
        bookId.setText("");
    }




}
