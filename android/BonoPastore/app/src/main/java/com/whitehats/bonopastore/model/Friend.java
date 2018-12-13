package com.whitehats.bonopastore.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Parcelable {

    private String username;
    private String user_id;
    private String html_url;

    public String getUsername() {
        return username;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getHtml_url() {
        return html_url;
    }

    public Friend(String login, String user_id, String avatar_url) {
        this.username = login;
        this.user_id = user_id;
        this.html_url = avatar_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Friend(Parcel in) {
        super();
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.username = data[0];
        this.user_id = data[1];
        this.html_url = data[2];
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.username,
                this.user_id,
                this.html_url
        });
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

}


