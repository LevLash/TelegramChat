package com.bot.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Connection  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String user1;
    private String user2;
    
    public Connection(String user1, String user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public Connection(Long id, String user1, String user2) {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
    }

    public Connection() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser1() {
        return user1;
    }
    
    public void setUser1(String user1) {
        this.user1 = user1;
    }
    
    public String getUser2() {
        return user2;
    }
    
    public void setUser2(String user2) {
        this.user2 = user2;
    }
}
