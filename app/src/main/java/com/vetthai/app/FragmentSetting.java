package com.vetthai.app;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import com.vetthai.app.model.Post;
import com.vetthai.app.model.Mapping;



public class FragmentSetting extends Fragment implements View.OnClickListener {

    private EditText edt_ip;
    private Button btn_setting;
    private TextView tv_setting;
    OnMenuList callback;

    public FragmentSetting() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        callback = (OnMenuList) getActivity();
        edt_ip = (EditText) rootView.findViewById(R.id.edt_ip);
        btn_setting = (Button) rootView.findViewById(R.id.btn_setting);
        tv_setting = (TextView) rootView.findViewById(R.id.tv_setting);
        btn_setting.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        if (v == btn_setting) {
            String ip = "http://" + edt_ip.getText().toString() + "/sitemap/?uid=" + ((User) getActivity().getApplication()).getId();
            Toast.makeText(getActivity(), ip, Toast.LENGTH_SHORT).show();
            new SimpleTask().execute("http://blog.teamtreehouse.com/api/get_recent_summary/");
        }
    }

    private class SimpleTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
        }

        protected String doInBackground(String... urls) {
            String result = "";
            try {

                HttpGet httpGet = new HttpGet(urls[0]);
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
            return result;
        }

        protected void onPostExecute(String jsonString) {
            // Dismiss ProgressBar
            showData(jsonString);
        }
    }

    private void showData(String jsonString) {
        Gson gson = new Gson();
        Mapping map = gson.fromJson(jsonString, Mapping.class);

        StringBuilder builder = new StringBuilder();
        builder.setLength(0);

        List<Post> posts = map.getPosts();

        for (Post post : posts) {
            builder.append(post.getTitle());
            builder.append("\n\n");
        }
        tv_setting.setText(builder.toString());
        callback.onMenuList(posts);
    }

    public interface OnMenuList {
        void onMenuList(List<Post> menu);
    }
}
