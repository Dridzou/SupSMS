package com.example.hugo.supsms;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MenuActivity extends ActionBarActivity {

    //Pour désactiver la possiblité de retourner sur la page de menu en appuyant
    // sur la touche "back" quand on doit se loger
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        Button buttonBUContacts = (Button)findViewById(R.id.buttonBUContacts);
        Button buttonBUSMS = (Button)findViewById(R.id.buttonBUSMS);
        Button buttonAbout = (Button)findViewById(R.id.buttonAbout);
        Button buttonLogout = (Button)findViewById(R.id.buttonLogout);

        buttonBUSMS.setOnClickListener(
            new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent myIntent = new Intent(MenuActivity.this, SMSActivity.class);
                    startActivityForResult(myIntent, 0);
                }
            }
        );

        buttonBUContacts.setOnClickListener(
            new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent myIntent = new Intent(MenuActivity.this, ContactsActivity.class);
                    startActivityForResult(myIntent, 0);
                }
            }
        );

        buttonAbout.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MenuActivity.this, AboutActivity.class);
                        startActivityForResult(myIntent, 0);
                    }
                }
        );

        buttonLogout.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MenuActivity.this, MainActivity.class);
                        startActivityForResult(myIntent, 0);
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
