package news.myapp.newproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.InputStream;

import news.myapp.newproject.Adapter.FragmentsAdapter;
import news.myapp.newproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        activityMainBinding.tabLayout.setupWithViewPager(activityMainBinding.viewPager);
    }
}