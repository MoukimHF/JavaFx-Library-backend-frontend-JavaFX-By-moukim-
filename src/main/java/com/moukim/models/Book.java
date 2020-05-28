package com.moukim.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Book {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "incrementator")
    @GenericGenerator(name = "incrementator",strategy = "increment")
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "name")
    private String name;

    @Column(name = "date_released")
    private String dateReleased;
    @Column(name = "genre")
    private String genre;
    @Column(name = "description")
    private String description;
    public Book(){

    }

    public Book ( int id , User user , String name , String dateReleased , String genre , String description,int userId ) {
        this.id           = id;
        this.userId       =userId;
        this.name         = name;
        this.dateReleased = dateReleased;
        this.genre        = genre;
        this.description  = description;
    }

    public int getId ( ) {
        return id;
    }

    public void setId ( int id ) {
        this.id = id;
    }

    public int getUserId ( ) {
        return userId;
    }

    public void setUserId ( int userId ) {
        this.userId = userId;
    }

    public String getName ( ) {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public String getDateReleased ( ) {
        return dateReleased;
    }

    public void setDateReleased ( String dateReleased ) {
        this.dateReleased = dateReleased;
    }

    public String getGenre ( ) {
        return genre;
    }

    public void setGenre ( String genre ) {
        this.genre = genre;
    }

    public String getDescription ( ) {
        return description;
    }

    public void setDescription ( String description ) {
        this.description = description;
    }
}
