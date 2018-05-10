package com.challange.heremobility.pjgwg.ktc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.challange.heremobility.pjgwg.ktc.utils.HomeRecyclerAdapter;
import com.challange.heremobility.pjgwg.ktc.utils.NetworkCheck;

public class HomeActivity extends AppCompatActivity implements HomeRecyclerAdapter.RecyclerOnClickListener{

    RecyclerView recyclerView;

    Toolbar toolbar;
    ProgressBar progressBar;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_home);
        progressBar = (ProgressBar) findViewById(R.id.progress_circle_home);
        progressBar.getIndeterminateDrawable().setColorFilter(R.color.colorPrimaryDark2, PorterDuff.Mode.MULTIPLY);
        toolbar.setTitle("Know this City");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        HomeRecyclerAdapter adapter = new HomeRecyclerAdapter(this,this);

        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onItemClick(int position) {
        if (NetworkCheck.isInternetAvailable(this)){
            int i = 1;
            String title=null;
            progressBar.setVisibility(View.VISIBLE);
            switch (position){
                case 0:
                    i=0;
                case 1:
                    if (i == 0)
                        title = "Locations";
                    else
                        title = "Hotels";
                    break;
                case 2:
                    i = 2;
                case 3:
                    if ( i == 2 )
                        title = "Hospitals";
                    else
                        title = "Local Shops";
                    break;
            }
            Intent intent = new Intent(this,MapActivity.class);
            intent.putExtra("TITLE",title+" Near You.");
            startActivity(intent);

        } else {
            Snackbar.make(getWindow().getDecorView(),"Internet not available. Please try again later",Snackbar.LENGTH_LONG).show();
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.INVISIBLE);
    }
}
