package com.example.hugo.supsms;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ContactsActivity extends ActionBarActivity {

    private String jsonContacts;
    private String httpResponse;
    private String httpResponseCONTACTS;
    private String rememberedLogin;
    private String rememberedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Button buttonToMenu = (Button)findViewById(R.id.buttonBackToMenu);

        buttonToMenu.setOnClickListener(
            new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent myIntent = new Intent(ContactsActivity.this, MenuActivity.class);
                    startActivityForResult(myIntent, 0);
                }
            }
        );

        //Infos part
        Boolean boolCheckInfosCount;
        Gson gson = new Gson();
        ArrayList<Contact> contactList = new ArrayList();
        Cursor contactInfosCursor = getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        boolCheckInfosCount = contactInfosCursor.getCount() > 0;

        //Email part
        Boolean testMails;
        Uri uriMail = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String idMailContact = ContactsContract.CommonDataKinds.Email._ID;
        String displayNameEmailContact = ContactsContract.CommonDataKinds.Email.DISPLAY_NAME;
        String emailContact = ContactsContract.CommonDataKinds.Email.ADDRESS;
        Cursor cursorMailContacts = getContentResolver().query(uriMail, new String[]{idMailContact,displayNameEmailContact,emailContact}, null, null, displayNameEmailContact+ " ASC");

        testMails = cursorMailContacts.getCount() > 0;
        contactInfosCursor.moveToFirst();
        cursorMailContacts.moveToFirst();
        if (boolCheckInfosCount && testMails ) {
            for(Integer i = 1; i <= contactInfosCursor.getCount(); i++) {
                Integer idContact = Integer.parseInt(contactInfosCursor.getString(0));
                String nameContact = contactInfosCursor.getString(1);
                String phoneContact = contactInfosCursor.getString(2);
                String mailContact = null;
                Integer idEmailContact = Integer.parseInt(cursorMailContacts.getString(0));
                String mailContactTest = cursorMailContacts.getString(2);

                //Check les id des mails et des contacts pour coordonner les infos
                if (((idContact+2) == idEmailContact) && (mailContactTest != null )) {
                    mailContact = mailContactTest;
                }

                //Itère sur tous les contacts et tous les mails récupérés grâce aux curseurs
                cursorMailContacts.moveToNext();
                contactInfosCursor.moveToNext();
                contactList.add(new Contact(idContact, nameContact, phoneContact, mailContact));

            }

            jsonContacts = gson.toJson(contactList);
            System.out.println(jsonContacts);
        }


        rememberedLogin = GlobalClass.getInstance().userLogin;
        rememberedPassword = GlobalClass.getInstance().userPassword;

        Toast.makeText(getApplicationContext(), "Getting contacts...", Toast.LENGTH_SHORT).show();
        backupContacts();

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

    private void backupContacts() {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://91.121.105.200/API/");

            List<NameValuePair> nameValuePairs = new ArrayList<>(4);
            nameValuePairs.add(new BasicNameValuePair("action", "backupcontacts"));
            nameValuePairs.add(new BasicNameValuePair("login", rememberedLogin));
            nameValuePairs.add(new BasicNameValuePair("password", rememberedPassword));
            nameValuePairs.add(new BasicNameValuePair("contacts", jsonContacts));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //ResponseHandler<String> stringResponseHandler = new BasicResponseHandler();
            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();
            String response = EntityUtils.toString(httpEntity);
            System.out.println(response);
            // Convert String to json object
            JSONObject json = null;
            try {
                json = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert json != null;
            String str_value=json.getString("success");
            System.out.println(str_value);

            if (str_value == "true") {
                Toast.makeText(getApplicationContext(), "Backup Done !", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e){
            Toast.makeText(ContactsActivity.this.getApplicationContext(),"Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        httpResponseCONTACTS = httpResponse;


    }
}