package com.example.lab8kotlin;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaceItem implements Parcelable {
    private String address;
    private String lat;
    private String lng;

    public PlaceItem(String address, String lat, String lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    protected PlaceItem(Parcel in) {
        this.address = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
    }

    public static final Parcelable.Creator<PlaceItem> CREATOR = new Creator<PlaceItem>() {
        @Override
        public PlaceItem createFromParcel(Parcel source) {
            return new PlaceItem(source);
        }

        @Override
        public PlaceItem[] newArray(int size) {
            return new PlaceItem[size];
        }
    };

}
