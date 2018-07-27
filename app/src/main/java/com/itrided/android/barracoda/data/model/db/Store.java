package com.itrided.android.barracoda.data.model.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Store {
    @PrimaryKey
    int id;
}
