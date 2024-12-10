package com.s339862.birthdays;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

// Entity/model class for storing friends in the Room database
@Entity
public class Friend {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String birthday;
    private String phone;

    // This is used when adding a new friend to the database
    // The @Ignore annotation tells Room to ignore this constructor
    // It's necessary because Room only allows one constructor for each entity
    @Ignore
    public Friend(String name, String birthday, String phone) {
        this.name = name;
        this.birthday = birthday;
        this.phone = phone;
    }

    // This is used when updating an existing friend in the database
    public Friend(int id, String name, String birthday, String phone) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
