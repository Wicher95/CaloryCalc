package pl.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import pl.myapplication.application.AndroidBug5497Workaround;
import pl.myapplication.application.CaloryCalc;
import pl.myapplication.databse.entities.User;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private EditText mUserName;
    private EditText mWeight;
    private EditText mAge;
    private Spinner mGender;
    private Spinner mLifestyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidBug5497Workaround.assistActivity(this);
        mContext = this;

        AsyncTask.execute(() -> {
            List<User> users = CaloryCalc.getDatabase().userDAO().retrieveUsers();
            if(users.size() > 0) {
                //TO-DO Pass selected user id to next activity
                //Intent intent = new Intent(this, ApplicationActivity.class);
                //intent.putExtra("userId", users.get(0).getId());
                //startActivity(intent);
            }
        });

        findViewById(R.id.begin).setOnClickListener(v -> RegisterNewUser());
        mUserName = findViewById(R.id.userName);
        mWeight = findViewById(R.id.weight);
        mAge = findViewById(R.id.age);
        mGender = findViewById(R.id.gender);
        mLifestyle = findViewById(R.id.lifestyle);
    }

    private void RegisterNewUser()
    {
        User user = new User();
        if(!mUserName.getText().toString().equals("")) {
            user.setName(mUserName.getText().toString());
        } else {
            return;
        }
        if(!mWeight.getText().toString().equals("")) {
            user.setWeight(Float.valueOf(mWeight.getText().toString()));
        } else {
            return;
        }
        if(!mAge.getText().toString().equals("")) {
            user.setAge(Integer.valueOf(mAge.getText().toString()));
        } else {
            return;
        }
        user.setGender(mGender.getSelectedItem().toString());
        user.setLifestyle(mLifestyle.getSelectedItem().toString());
        AsyncTask.execute(() -> {
            long id = CaloryCalc.getDatabase().userDAO().addUser(user);
            Intent intent = new Intent(this, ApplicationActivity.class);
            intent.putExtra("userId", id);
            startActivity(intent);
        });
    }
}
