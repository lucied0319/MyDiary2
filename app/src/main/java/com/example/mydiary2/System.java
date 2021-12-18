package com.example.mydiary2;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class System extends RealmObject {
    @PrimaryKey
    public int id;
    public String tag;

}
