package com.example.hugo.supsms;

import android.app.Application;

/**
 * Created by Hugo on 03/02/2015.
 */
public class GlobalClass extends Application {

    private static GlobalClass mInstance= null;
    public String userLogin;
    public String userPassword;

    public static synchronized GlobalClass getInstance(){
        if(null == mInstance){
            mInstance = new GlobalClass();
        }
        return mInstance;
    }


    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
