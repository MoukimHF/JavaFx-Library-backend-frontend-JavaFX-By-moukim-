package com.moukim.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "incrementator")
    @GenericGenerator(name = "incrementator",strategy = "increment")
    private int id;
    @NotNull
    @Size(min = 2, max = 14)
    @Column(name = "name")
    private String name;
    @NotNull
    @Size(min = 2, max = 14)
    @Column(name = "username")
    private String username;
    @NotNull
    @Column(name = "password")
    private String password;
    @NotNull
    @Size(min = 2, max = 20)
    @Column(name = "lastName")
    private String lastName;
    @NotNull
    @Size(min = 2, max = 50)
    @Column(name = "email")
    private String email;
    @Column(name = "dob")
    private String dob;
    @Column(name = "tel")
    private String tel;

     public User(){

     }

    public User ( int id , String name , String lastName , String email , String dob , String tel ) {
         super();
        this.id       = id;
        this.name     = name;
        this.lastName = lastName;
        this.email    = email;
        this.dob      = dob;
        this.tel      = tel;
    }

    public int getId ( ) {
        return id;
    }

    public void setId ( int id ) {
        this.id = id;
    }

    public String getName ( ) {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public String getLastName ( ) {
        return lastName;
    }

    public void setLastName ( String lastName ) {
        this.lastName = lastName;
    }

    public String getEmail ( ) {
        return email;
    }

    public void setEmail ( String email ) {
        this.email = email;
    }

    public String getDob ( ) {
        return dob;
    }

    public void setDob ( String dob ) {
        this.dob = dob;
    }

    public String getTel ( ) {
        return tel;
    }

    public void setTel ( String tel ) {
        this.tel = tel;
    }

    public String getUsername ( ) {
        return username;
    }

    public void setUsername ( String username ) {
        this.username = username;
    }

    public String getPassword ( ) {
        return password;
    }

    public void setPassword ( String password ) {
        this.password = password;
    }
}
