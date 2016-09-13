package com.vetthai.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vetthai.app.model.JsonMenuUrl;
import com.vetthai.app.model.MenuUrl;
import com.vetthai.app.model.Profile;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult> {
    private String deviceId;
    private WebView webView;
    private FrameLayout container;
    private AQuery aq = new AQuery(this);
    private GoogleApiClient mGoogleApiClient;
    private NavigationView navigationView;
    String json;
    private List<MenuUrl> resultMenu;
    final List<MenuItem> Menuitems = new ArrayList<>();
    private TextView navHead_name;
    private TextView navHead_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //savedInstanceState.c
        setContentView(R.layout.activity_main);
        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        json = "{" +
                "  results: [" +
                "   {title:\"วินิจฉัยโรค\",url:\"http://www.vetthai.com/a1.php?uid=" + deviceId +"&t="+(new Date().getTime())+ "\"}," +
                "   {title:\"ข้อมูลโรค\",url:\"http://www.vetthai.com/a2.php?uid=" + deviceId +"&t="+(new Date().getTime())+ "\"}," +
                "   {title:\"ข้อมูล โรงพยาบาล\",url:\"http://www.vetthai.com/a3.php?uid=" + deviceId +"&t="+(new Date().getTime())+ "\"}," +
                "   {title:\"ออกจากระบบ\",url:\"http://www.vetthai.com/a4.php?uid=" + deviceId +"&t="+(new Date().getTime())+ "\"}" +
                "  ]" +
                "}";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initMenu(json);

        navHead_name = (TextView) findViewById(R.id.navHead_name);
        navHead_email = (TextView) findViewById(R.id.navHead_email);
        getUserProfile();

        container = (FrameLayout) findViewById(R.id.container);
        container.setVisibility(FrameLayout.GONE);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDisplayZoomControls(false);
        final ProgressDialog pd = new ProgressDialog(this);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                pd.setMessage("Loading");
                pd.show();

                //finish progress
                if (progress == 100) pd.dismiss();

            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl("http://www.vetthai.com/a1.php?uid=" + deviceId+"&t="+(new Date().getTime())); //initial
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int position = this.Menuitems.indexOf(item);

        container.setVisibility(FrameLayout.GONE);
        webView.loadUrl(this.resultMenu.get(position).getUrl());
        Log.v("url", this.resultMenu.get(position).getUrl());
        if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {

        if (mGoogleApiClient.isConnected()) {
            //Google plus+
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            this.finish();
        } else {
            //facebook
            LoginManager.getInstance().logOut();
            this.finish();
        }
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onConnected(Bundle bundle) {
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {

    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void initMenu(String jsonString) {

        navigationView.getMenu().clear();//clear menu in nav
        Gson gson = new Gson();
        JsonMenuUrl map = gson.fromJson(jsonString, JsonMenuUrl.class);

        StringBuilder builder = new StringBuilder();
        builder.setLength(0);

        this.resultMenu = map.getResults();
        Menu menu_view = this.navigationView.getMenu();
        for (int i = 0; i < resultMenu.size(); i++) {
            menu_view.add(resultMenu.get(i).getTitle());
        }

        for (int i = 0; i < menu_view.size(); i++) {
            this.Menuitems.add(menu_view.getItem(i));
        }

    }

    private void getUserProfile() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                String result = "";
                try {

                    HttpGet httpGet = new HttpGet("http://www.vetthai.com/info.php?uid=" + deviceId+"&t="+(new Date().getTime()));
                    HttpClient client = new DefaultHttpClient();

                    HttpResponse response = client.execute(httpGet);

                    int statusCode = response.getStatusLine().getStatusCode();

                    if (statusCode == 200) {
                        InputStream inputStream = response.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result += line;
                        }
                    }

                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                Log.e("result", result.toString());
                return result;
            }

            @Override
            protected void onPostExecute(String jsonResult) {
                super.onPostExecute(jsonResult);
                Gson gson = new Gson();
                Profile profile = gson.fromJson(jsonResult, Profile.class);
                navHead_name.setText(profile.getName());
                navHead_name.refreshDrawableState();
                navHead_email.setText(profile.getEmail());
                navHead_email.refreshDrawableState();

                aq.id(R.id.nav_Img).image(profile.getImage());
                Log.e("onPostExecute", profile.getName());
            }
        }.execute();

    }

}
