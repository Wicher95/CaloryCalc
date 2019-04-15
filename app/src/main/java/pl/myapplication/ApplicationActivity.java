package pl.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import pl.myapplication.application.CaloryCalc;
import pl.myapplication.databse.entities.User;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class ApplicationActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        int userId = (int) getIntent().getLongExtra("userId", 1);
        AsyncTask.execute(() -> {
            //TO-DO Change user Id to the currently selected user
            user = CaloryCalc.getDatabase().userDAO().getUserById(userId);
            setUserData();
        });
    }

    private void setUserData() {
        String text = "Użytkownik: " + user.getName() + "\nPłeć: " + user.getGender() + "\nWiek: " + user.getAge() + "\nWaga: " + user.getWeight() + "kg"
                + "\nTryb życia: " + user.getLifestyle();
        ((TextView)findViewById(R.id.userData)).setText(text);
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
}
