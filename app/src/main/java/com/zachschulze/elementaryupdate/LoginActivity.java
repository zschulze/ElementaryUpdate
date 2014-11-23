package com.zachschulze.elementaryupdate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity {

    private EditText email = null;
    private EditText password = null;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText)findViewById(R.id.loginEmail);
        password = (EditText)findViewById(R.id.loginPassword);
        login = (Button)findViewById(R.id.loginSubmit);
    }

    public void login(View view) {
        if (email.getText().toString().equals("parent") &&
                password.getText().toString().equals("password")) {
            Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ParentHomeActivity.class);
            startActivity(intent);
        }
        else if (email.getText().toString().equals("teacher") &&
                password.getText().toString().equals("password")) {
            Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, TeacherHomeActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Authentication Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
