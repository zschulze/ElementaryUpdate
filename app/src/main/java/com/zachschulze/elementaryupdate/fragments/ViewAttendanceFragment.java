package com.zachschulze.elementaryupdate.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;

import com.zachschulze.elementaryupdate.ParentHomeActivity;
import com.zachschulze.elementaryupdate.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zach on 10/29/2014.
 */



public class ViewAttendanceFragment extends ListFragment {
    public static final String TAG = ParentHomeActivity.class.getSimpleName();
    protected static ArrayList<HashMap<String, String>> attendance =
            new ArrayList<HashMap<String, String>>();
    protected static JSONArray mAttendanceData;

    int mCurCheckPosition = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        attendance.clear();

        /* --- Get updates from web using Async task --- */
        GetAttendanceTask getAttendanceTask = new GetAttendanceTask();
        getAttendanceTask.execute();

        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    private class GetAttendanceTask extends AsyncTask<Object, Void, JSONArray> {

        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading attendance");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONArray doInBackground(Object... params) {
            int responseCode = -1;
            JSONArray jsonResponse = null;
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://ec2-54-68-187-75.us-west-2.compute.amazonaws.com/att/json/");

            try {
                HttpResponse response = client.execute(httpget);
                StatusLine statusLine = response.getStatusLine();
                responseCode = statusLine.getStatusCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    jsonResponse = new JSONArray(builder.toString());
                    Log.v(TAG, "Code :" + jsonResponse);

                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonUpdate = jsonResponse.getJSONObject(i);
                        JSONObject jsonUpdateFields = jsonUpdate.getJSONObject("fields");
                        String attDate = jsonUpdateFields.getString("attDate");
                        boolean didAtt = jsonUpdateFields.getBoolean("didAtt");
                        Log.v(TAG, attDate + ": " + didAtt);
                    }

                } else {
                    Log.i(TAG, String.format("Unsuccessful HTTP response code: %d", responseCode));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Exception caught: ", e);
            } catch (Exception e) {
                Log.e(TAG, "Exception caught: ", e);
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            super.onPostExecute(result);
            if (this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
            mAttendanceData = result;
            handleUpdateResponse();
            String noAttendance[] = {"There is no attendance"};

            String[] keys = { "didAtt", "attDate"};
            int[] ids = { android.R.id.text1, android.R.id.text2};

            if (mAttendanceData == null) {
                setListAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, noAttendance));
            }
            else {
                // Populate list
                setListAdapter(new SimpleAdapter(getActivity(), attendance,
                        android.R.layout.simple_list_item_2, keys, ids));
            }
        }
    }

    public void handleUpdateResponse() {
        if (mAttendanceData == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.error_title));
            builder.setMessage(getString(R.string.error_message));
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            try {
                JSONArray jsonAttendance = mAttendanceData;
                for (int i = 0; i < jsonAttendance.length(); i++) {
                    JSONObject attendance = jsonAttendance.getJSONObject(i);
                    JSONObject attendanceFields = attendance.getJSONObject("fields");
                    boolean didAtt = attendanceFields.getBoolean("didAtt");
                    String attDate = attendanceFields.getString("attDate");
                    String didAttString;
                    if (didAtt == true) {
                        didAttString = "Present";
                    } else {
                        didAttString = "Not present";
                    }

                    HashMap<String, String> attendanceMap = new HashMap<String, String>();
                    attendanceMap.put("didAtt", didAttString);
                    attendanceMap.put("attDate", attDate);

                    ViewAttendanceFragment.attendance.add(attendanceMap);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Exception caught!", e);
            }
        }
    }
}