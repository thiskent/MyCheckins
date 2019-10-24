package com.checkin.mycheckins.MainActivity.Presenter;

import android.content.Context;
import android.util.Log;

import com.checkin.mycheckins.DatabaseHelper.DatabaseHelper;
import com.checkin.mycheckins.MainActivity.Interface.MainInterface;
import com.checkin.mycheckins.Models.CheckinModel;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements MainInterface.Presenter {

    private DatabaseHelper databaseHelper;
    private List<CheckinModel> checkIns;
    private MainInterface.View view;

    public MainPresenter(MainInterface.View view) {
        checkIns = new ArrayList<>();
        this.view = view;
    }

    @Override
    public void checkCount(Context context) {
        databaseHelper = new DatabaseHelper(context);

        //check for saved checkIns
        if (databaseHelper.getCheckInCount() > 0) {
            if (!checkIns.isEmpty()) {
                checkIns.clear();
            }
            checkIns.addAll(databaseHelper.getAllCheckins());

            view.updateView(true, checkIns);
        } else {
            view.updateView(false, checkIns);
        }
    }
}
