package com.example.hugo.supsms;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//TEST

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //ca passe
        Button button = (Button)findViewById(R.id.buttonLogin);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("http://91.121.105.200/API/");
                        try {
                            // Add your data

                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                            nameValuePairs.add(new BasicNameValuePair("action", "login"));
                            nameValuePairs.add(new BasicNameValuePair("login", ((EditText)findViewById(R.id.loginName)).getText().toString()));
                            nameValuePairs.add(new BasicNameValuePair("password",((EditText)findViewById(R.id.loginPassword)).getText().toString()));
                            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                            System.out.println("test2");
                            // Execute HTTP Post Request
                            HttpResponse httpResponse = httpClient.execute(httpPost);
                            System.out.println("test");

                            String response = null;
                            HttpEntity httpEntity = null;
                            httpEntity = httpResponse.getEntity();
                            response = EntityUtils.toString(httpEntity);
                            System.out.println(response);


                        } catch (ClientProtocolException e) {
                            // TODO Auto-generated catch block
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                        }
                    }
                }
        );
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
