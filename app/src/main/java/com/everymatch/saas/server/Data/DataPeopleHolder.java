package com.everymatch.saas.server.Data;

import java.io.Serializable;
import java.util.ArrayList;

public class DataPeopleHolder implements Serializable {
    public int count;
    private ArrayList<DataPeople> users;

    public DataPeopleHolder() {
        users = new ArrayList<>();
    }

    public ArrayList<DataPeople> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }

        return users;
    }

    public void setUsers(ArrayList<DataPeople> users){
        this.users = users;
    }
}