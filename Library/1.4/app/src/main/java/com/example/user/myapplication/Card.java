package com.example.user.myapplication;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable {

    private static final String EMPTY = "empty";
    String id = EMPTY;
    String name = "EMPTY";
    String  authors = EMPTY;
    int photoId = R.drawable.emma;
    boolean available = false;
    String year = EMPTY;
    String description = EMPTY;
    String link = EMPTY;

    Card(String name, String description, int photoId) {
        this.name = name;
        this.description = description;
        this.photoId = photoId;
    }

    public String availableToString()
    {
        return available ? "The book is available" : "The book is not aavailable";
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        // для сохранения данных используем Bundle, тут думаю понятно
        // Можно обойтись и без него, но так, как мне кажется удобнее
        Bundle bundle = new Bundle();

        bundle.putString("id", id);
        bundle.putString("name", name);
        bundle.putString("authors", authors);
        bundle.putInt("photoId", photoId);
        bundle.putBoolean("available", available);
        bundle.putString("year", year);
        bundle.putString("description", description);
        bundle.putString("link", link);

        // сохраняем
        parcel.writeBundle(bundle);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public Card(Parcel parcel) {
        Bundle bundle = parcel.readBundle();

        id = bundle.getString("id");
        name = bundle.getString("name");
        authors = bundle.getString("authors");
        photoId = bundle.getInt("photoId");
        available = bundle.getBoolean("available");
        year = bundle.getString("year");
        description = bundle.getString("description");
        link = bundle.getString("link");
    }



}
