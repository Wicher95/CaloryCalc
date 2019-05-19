package pl.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import pl.myapplication.application.AndroidBug5497Workaround;
import pl.myapplication.application.CaloryCalc;
import pl.myapplication.databse.entities.User;

public class MainActivity extends AppCompatActivity {

    public static String[] productsArray;
    public static String PREFERENCES = "preferences";
    public static String USER_ID = "userId";
    public static String CURRENT_USER_ID = "currentUserId";

    private Context mContext;
    private EditText mUserName;
    private EditText mWeight;
    private EditText mAge;
    private EditText mHeight;
    private Spinner mGender;
    private Spinner mLifestyle;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidBug5497Workaround.assistActivity(this);
        mContext = this;
        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        productsArray = getResources().getStringArray(R.array.products_array);

        AsyncTask.execute(() -> {
            List<User> users = CaloryCalc.getDatabase().userDAO().retrieveUsers();
            if(users.size() > 0) {
                Intent intent = new Intent(mContext, ApplicationActivity.class);
                long id = sharedPreferences.getLong(CURRENT_USER_ID, 1);
                intent.putExtra(USER_ID, id);
                startActivity(intent);
            }
        });
        findViewById(R.id.begin).setOnClickListener(v -> RegisterNewUser());
        mUserName = findViewById(R.id.userName);
        mWeight = findViewById(R.id.weight);
        mAge = findViewById(R.id.age);
        mHeight = findViewById(R.id.height);
        mGender = findViewById(R.id.gender);
        mLifestyle = findViewById(R.id.lifestyle);
    }

    private void RegisterNewUser()
    {
        User user = new User();
        if(!mUserName.getText().toString().equals("")) {
            user.setName(mUserName.getText().toString());
        } else {
            ShowRegisterToast();
            return;
        }
        if(!mWeight.getText().toString().equals("")) {
            user.setWeight(Float.valueOf(mWeight.getText().toString()));
        } else {
            ShowRegisterToast();
            return;
        }
        if(!mAge.getText().toString().equals("")) {
            user.setAge(Integer.valueOf(mAge.getText().toString()));
        } else {
            ShowRegisterToast();
            return;
        }
        if(!mHeight.getText().toString().equals("")) {
            user.setHeight(Integer.valueOf(mHeight.getText().toString()));
        } else {
            ShowRegisterToast();
            return;
        }
        user.setGender(mGender.getSelectedItem().toString());
        user.setLifestyle(mLifestyle.getSelectedItem().toString());
        AsyncTask.execute(() -> {
            long id = CaloryCalc.getDatabase().userDAO().addUser(user);
            Intent intent = new Intent(mContext, ApplicationActivity.class);
            intent.putExtra(USER_ID, id);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(CURRENT_USER_ID, id);
            editor.apply();
            startActivity(intent);
        });
    }

    private void ShowRegisterToast()
    {
        Toast.makeText(this, "Musisz wypełnić wszystkie pola", Toast.LENGTH_SHORT).show();
    }
}
