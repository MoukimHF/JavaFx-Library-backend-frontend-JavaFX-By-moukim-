package com.moukim.models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Admin {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "incrementator")
    @GenericGenerator(name = "incrementator",strategy = "increment")
    private int id;
    @NotNull
    @Size(min = 2, max = 14)
    @Column(name = "username")
    private String username;
    @NotNull
    @Column(name = "password")
    private String password;
    public Admin(){}

    public Admin ( int id , @NotNull @Size(min = 2, max = 14) String username , @NotNull String password ) {
        super();
        this.id       = id;
        this.username = username;
        this.password = password;
    }

    public int getId ( ) {
        return id;
    }

    public void setId ( int id ) {
        this.id = id;
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


