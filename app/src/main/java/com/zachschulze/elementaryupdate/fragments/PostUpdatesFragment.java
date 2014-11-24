package com.zachschulze.elementaryupdate.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zachschulze.elementaryupdate.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Zach on 11/10/2014.
 */
public class PostUpdatesFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText et;
    private String update;
    public static final String TAG = PostUpdatesFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_post_updates_fragment, container, false);
        et = (EditText) view.findViewById(R.id.editText1);
        update = et.getText().toString();

        final Button button = (Button) view.findViewById(R.id.button_id);
        button.setOnClickListener(this);

        final Button clear = (Button) view.findViewById(R.id.button2);
        clear.setOnClickListener(this);

        Spinner spinner1 = (Spinner) view.findViewById(R.id.spinner);
        spinner1.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(),
                R.array.updatesTemplates1, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        Spinner spinner2 = (Spinner) view.findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                R.array.updatesTemplates2, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String item = (String) parent.getItemAtPosition(pos);
        update += item;
        if (update != "") {
            item += " ";
        }
        String newText = et.getText().toString() + item;
        et.setText("");
        et.append(newText);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button2) {
            update = "";
            et.setText(update);
        }
        if (view.getId() == R.id.button_id) {
            new HttpAsyncTask().execute();
        }
    }

    /* json post function (in progress) */
    public static String Post (final String updateText) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://ec2-54-68-187-75.us-west-2.compute.amazonaws.com/updates/json/");
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("updateText", updateText);
            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept","application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Error";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return Post(update);
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), "Data sent", Toast.LENGTH_LONG).show();
        }
    }
}