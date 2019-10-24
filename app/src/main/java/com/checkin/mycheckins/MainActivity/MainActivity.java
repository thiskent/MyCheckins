package com.checkin.mycheckins.MainActivity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.checkin.mycheckins.AppData.IntentKeys;
import com.checkin.mycheckins.MainActivity.Adapter.CheckInAdapter;
import com.checkin.mycheckins.MainActivity.Interface.MainInterface;
import com.checkin.mycheckins.MainActivity.Presenter.MainPresenter;
import com.checkin.mycheckins.Models.CheckinModel;
import com.checkin.mycheckins.NewCheckin.AddCheckInActivity;
import com.checkin.mycheckins.R;
import com.checkin.mycheckins.WebViewActivity.HelpActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainInterface.View {

    private RecyclerView myCheckins;
    private ConstraintLayout noItemFound;
    private CheckInAdapter checkInAdapter;
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        mainPresenter.checkCount(this);
    }

    private void initializeViews() {

        myCheckins = findViewById(R.id.record_recycler);
        noItemFound = findViewById(R.id.noDataFound);

        mainPresenter = new MainPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.checkIn:
                Intent intent1 = new Intent(MainActivity.this, AddCheckInActivity.class);
                intent1.putExtra(IntentKeys.ACTIVITY_TITLE, "New CheckIn");
                intent1.putExtra(IntentKeys.IS_NEW_CHECKIN, true);
                startActivity(intent1);
                break;
            case R.id.help:
                Intent intent2 = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void updateView(boolean shouldShowRecycler, List<CheckinModel> checkIns) {

        if (shouldShowRecycler) {

            myCheckins.setAdapter(null);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            myCheckins.setLayoutManager(layoutManager);

            checkInAdapter = new CheckInAdapter(checkIns, this);
            myCheckins.setAdapter(checkInAdapter);

            noItemFound.setVisibility(View.GONE);
            myCheckins.setVisibility(View.VISIBLE);

        } else {
            myCheckins.setVisibility(View.GONE);
            noItemFound.setVisibility(View.VISIBLE);
        }
    }
}
