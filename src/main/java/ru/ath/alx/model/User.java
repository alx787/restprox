package ru.ath.alx.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users", schema = "restproxdb")
public class User {

    private int id;
    private String name;
    private String password;
    private String passhash;
    private String passtoken;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "password", nullable = false, length = 100)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "passhash", nullable = false, length = 64)
    public String getPasshash() {
        return passhash;
    }

    public void setPasshash(String passhash) {
        this.passhash = passhash;
    }

    @Basic
    @Column(name = "passtoken", nullable = false, length = 64)
    public String getPasstoken() {
        return passtoken;
    }

    public void setPasstoken(String passtoken) {
        this.passtoken = passtoken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(password, that.password) &&
                Objects.equals(passhash, that.passhash) &&
                Objects.equals(passtoken, that.passtoken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, passhash, passtoken);
    }

}
