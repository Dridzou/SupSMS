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


public class ContactsActivity extends ListActivity {

    @Override
    public long getSelectedItemId() {
        return super.getSelectedItemId();
    }

    @Override
    public int getSelectedItemPosition() {
        return super.getSelectedItemPosition();
    }

    private ListView lv;
    private Cursor cursor;
    private String jsonContacts;
    public String httpResponse;
    public Boolean isSaved;
    public String httpResponseCONTACTS;
    String rememberedLogin;
    String rememberedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Boolean testContacts;
        Gson gson = new Gson();
        ArrayList<Contact> alContacts = new ArrayList();
        Cursor managedCursor = getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        testContacts= managedCursor.getCount() > 0;
        managedCursor.moveToFirst();

        if (testContacts) {
            while (managedCursor.moveToNext()) {
                Integer idContact = Integer.parseInt(managedCursor.getString(0));
                String nameContact = managedCursor.getString(1);
                String phoneContact = managedCursor.getString(2);

                alContacts.add(new Contact(idContact, nameContact, phoneContact, null));
            }
            jsonContacts = gson.toJson(alContacts);
            Log.d("debug: Number of CONTACTS", String.valueOf(alContacts.size()));
        }

        rememberedLogin = GlobalClass.getInstance().userLogin;
        rememberedPassword = GlobalClass.getInstance().userPassword;

        BackupContactConnectionData backupContactConnectionData = new BackupContactConnectionData();
        backupContactConnectionData.execute();
        Toast.makeText(getApplicationContext(), "Synchronization of contacts...", Toast.LENGTH_SHORT).show();
        backupContactConnectionData.cancel(true);

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

    class BackupContactConnectionData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Looper.prepare();

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://91.121.105.200/API/");

                List<NameValuePair> nameValuePairs = new ArrayList<>(4);
                nameValuePairs.add(new BasicNameValuePair("action", "backupcontacts"));
                nameValuePairs.add(new BasicNameValuePair("login", rememberedLogin));
                nameValuePairs.add(new BasicNameValuePair("password", rememberedPassword));
                nameValuePairs.add(new BasicNameValuePair("contacts", jsonContacts));
                System.out.println("LOOOOOGIIIIIIIIIIIN : " + rememberedLogin);
                System.out.println("PAAAAAAAASWWOOOOORD : " + rememberedPassword);
                System.out.println("JSONCCOOOOOOOONTACT : " + jsonContacts);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                ResponseHandler<String> stringResponseHandler = new BasicResponseHandler();
                httpResponse = httpClient.execute(httpPost,stringResponseHandler);

                if (httpResponse.contains("\"success\":true,")){
                    Log.d("CheckSuccess: ", httpResponse);
                    isSaved = true;
                } else{
                    isSaved = false;
                }

            } catch (ClientProtocolException e){
                Toast.makeText(ContactsActivity.this.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e){
                Toast.makeText(ContactsActivity.this.getApplicationContext(),"Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            httpResponseCONTACTS = httpResponse;


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("debug HttpResponse", "httpResponseCONTACTS = " + httpResponseCONTACTS);
                    Toast.makeText(getApplicationContext(), "HTTPRESPONSE CONTACTS" + httpResponseCONTACTS, Toast.LENGTH_LONG).show();
                }
            });
            Looper.loop();

            return null;
        }
    }
}