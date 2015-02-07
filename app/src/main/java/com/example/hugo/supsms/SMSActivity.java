package com.example.hugo.supsms;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

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

import java.util.ArrayList;
import java.util.List;


public class SMSActivity extends ActionBarActivity {

    private String jsonInboxSMS;
    private String jsonSentSMS;
    private String httpResponseSMSInbox;
    private String httpResponseSMSSent;
    private String rememberedLogin;
    private String rememberedPassword;
    public String httpResponseIn; //
    public String httpResponseSe; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        ArrayList<SMS> inboxList = new ArrayList<>();
        ArrayList<SMS> sentList = new ArrayList<>();
        Uri inboxUri = Uri.parse("content://sms/inbox");
        Uri sentUri = Uri.parse("content://sms/sent");
        Cursor inboxCursor = getContentResolver().query(inboxUri, new String[]{"_id", "address", "date", "body"}, null, null, null);
        inboxCursor.moveToFirst();
        Cursor sentCursor = getContentResolver().query(sentUri, new String[]{"_id", "address", "date", "body"}, null, null, null);
        sentCursor.moveToFirst();

        boolean boolCheckCount = inboxCursor.getCount() > 0; //
        Gson gson = new Gson();

        if(boolCheckCount) {
            while(inboxCursor.moveToNext()) {
                String smsIAddress = inboxCursor.getString(1);
                String temp = inboxCursor.getString(3);
                byte[] data = temp.getBytes();
                String smsIBody = Base64.encodeToString(data, Base64.DEFAULT);

                inboxList.add(new SMS(smsIAddress, smsIBody));

            }

            jsonInboxSMS = gson.toJson(inboxList);
            Log.d("debug: Number of SmsInbox", String.valueOf(inboxList.size()));
        }

        if(sentCursor.getCount() > 0) {
            while(sentCursor.moveToNext()) {
                String smsSAddress = sentCursor.getString(1);
                String temp = sentCursor.getString(3);
                byte[] data = temp.getBytes();
                String smsSBody = Base64.encodeToString(data, Base64.DEFAULT);

                sentList.add(new SMS(smsSAddress, smsSBody));

            }

            jsonSentSMS = gson.toJson(sentList);
            Log.d("debug: Number of SmsSent", String.valueOf(sentList.size()));
        }

        rememberedLogin = GlobalClass.getInstance().userLogin;
        rememberedPassword = GlobalClass.getInstance().userPassword;

        Toast.makeText(getApplicationContext(), "Getting messages...", Toast.LENGTH_SHORT).show();
        backupSMS();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sm, menu);
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


    public void backupSMS() {

            boolean doubleCheckInboxAndSent = false;

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://91.121.105.200/API/");

                List<NameValuePair> nameValuePairs = new ArrayList<>(4);
                nameValuePairs.add(new BasicNameValuePair("action", "backupsms"));
                nameValuePairs.add(new BasicNameValuePair("login", rememberedLogin));
                nameValuePairs.add(new BasicNameValuePair("password", rememberedPassword));
                nameValuePairs.add(new BasicNameValuePair("box", "inbox"));
                nameValuePairs.add(new BasicNameValuePair("sms", jsonInboxSMS));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponseIn = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponseIn.getEntity();
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
                    doubleCheckInboxAndSent = true;
                }
                else {
                    doubleCheckInboxAndSent = false;
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                Toast.makeText(SMSActivity.this.getApplicationContext(),"Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            //if(doubleCheckInboxAndSent) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://91.121.105.200/API/");

                List<NameValuePair> nameValuePairs = new ArrayList<>(4);
                nameValuePairs.add(new BasicNameValuePair("action", "backupsms"));
                nameValuePairs.add(new BasicNameValuePair("login", rememberedLogin));
                nameValuePairs.add(new BasicNameValuePair("password", rememberedPassword));
                nameValuePairs.add(new BasicNameValuePair("box", "sent"));
                nameValuePairs.add(new BasicNameValuePair("sms", jsonSentSMS));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponseSe = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponseSe.getEntity();
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
                Toast.makeText(SMSActivity.this.getApplicationContext(),"Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            //}

            httpResponseSMSInbox = httpResponseIn;
            httpResponseSMSSent = httpResponseSe;


        }
    }





/*package com.example.hugo.supsms;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

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

import java.util.ArrayList;
import java.util.List;


public class SMSActivity extends ActionBarActivity {

    private String jsonInboxSMS;
    private String jsonSentSMS;
    private String httpResponseIn;
    private String httpResponseSe;
    private String httpResponseSMSInbox;
    private String httpResponseSMSSent;
    private String rememberedLogin;
    private String rememberedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        ArrayList<SMS> inboxList = new ArrayList<>();
        ArrayList<SMS> sentList = new ArrayList<>();
        Uri inboxUri = Uri.parse("content://sms/inbox");
        Uri sentUri = Uri.parse("content://sms/sent");
        Cursor inboxCursor = getContentResolver().query(inboxUri, new String[]{"_id", "address", "date", "body"}, null, null, null, null);
        inboxCursor.moveToFirst();
        Cursor sentCursor = getContentResolver().query(sentUri, new String[]{"_id", "address", "date", "body"}, null, null, null, null);
        sentCursor.moveToFirst();

        Boolean boolCheckCount = inboxCursor.getCount() > 0;
        Gson gson = new Gson();

        if(boolCheckCount) {
            while(inboxCursor.moveToNext()) {
                String smsIAddress = inboxCursor.getString(1);
                String smsIBody = inboxCursor.getString(3);

                inboxList.add(new SMS(smsIAddress, smsIBody));

            }

            jsonInboxSMS = gson.toJson(inboxList);
            Log.d("debug: Number of SmsInbox", String.valueOf(inboxList.size()));
        }

        if(sentCursor.getCount() > 0) {
            while(sentCursor.moveToNext()) {
                String smsSAddress = sentCursor.getString(1);
                String smsSBody = sentCursor.getString(3);

                sentList.add(new SMS(smsSAddress, smsSBody));

            }

            jsonSentSMS = gson.toJson(sentList);
            Log.d("debug: Number of SmsSent", String.valueOf(sentList.size()));
        }

        rememberedLogin = GlobalClass.getInstance().userLogin;
        rememberedPassword = GlobalClass.getInstance().userPassword;

        BackupSMSConnectionData backupSMSConnectionData = new BackupSMSConnectionData();
        backupSMSConnectionData.execute();
        Toast.makeText(getApplicationContext(), "Synchronization of messages...", Toast.LENGTH_SHORT).show();
        //backupSmsConnectionData.cancel(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sm, menu);
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


    class BackupSMSConnectionData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Looper.prepare();

            Boolean doubleCheckInboxAndSent = false;

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://91.121.105.200/API/");

                List<NameValuePair> nameValuePairs = new ArrayList<>(4);
                nameValuePairs.add(new BasicNameValuePair("action", "backupsms"));
                nameValuePairs.add(new BasicNameValuePair("login", rememberedLogin));
                nameValuePairs.add(new BasicNameValuePair("password", rememberedPassword));
                nameValuePairs.add(new BasicNameValuePair("box", "inbox"));
                nameValuePairs.add(new BasicNameValuePair("sms", jsonInboxSMS));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse httpResponseIn = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponseIn.getEntity();
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
                    doubleCheckInboxAndSent = true;
                }
                else {
                    doubleCheckInboxAndSent = false;
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                Toast.makeText(SMSActivity.this.getApplicationContext(),"Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            //if(doubleCheckInboxAndSent) {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://91.121.105.200/API/");

                    List<NameValuePair> nameValuePairs = new ArrayList<>(4);
                    nameValuePairs.add(new BasicNameValuePair("action", "backupsms"));
                    nameValuePairs.add(new BasicNameValuePair("login", rememberedLogin));
                    nameValuePairs.add(new BasicNameValuePair("password", rememberedPassword));
                    nameValuePairs.add(new BasicNameValuePair("box", "sent"));
                    nameValuePairs.add(new BasicNameValuePair("sms", jsonSentSMS));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse httpResponseSe = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponseSe.getEntity();
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
                    Toast.makeText(SMSActivity.this.getApplicationContext(),"Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            //}

            httpResponseSMSInbox = httpResponseIn;
            httpResponseSMSSent = httpResponseSe;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("debug HttpResponse 1", "httpResponseINBOX = " + httpResponseSMSInbox);
                    Log.d("debug HttpResponse 2", "httpResponseSENT = " + httpResponseSMSSent);
                    Toast.makeText(getApplicationContext(), "HTTPRESPONSE INBOX" + httpResponseSMSInbox, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "HTTPRESPONSE SENT" + httpResponseSMSSent, Toast.LENGTH_LONG).show();
                }
            });
            Looper.loop();

            return null;
        }

    }
}
*/