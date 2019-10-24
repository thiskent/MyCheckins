package com.checkin.mycheckins.NewCheckin.Presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.checkin.mycheckins.DatabaseHelper.DatabaseHelper;
import com.checkin.mycheckins.Models.CheckinModel;
import com.checkin.mycheckins.NewCheckin.Interface.AddCheckInInterface;

public class AddCheckInPresenter implements AddCheckInInterface.Presenter {

    private AddCheckInInterface.View view;
    private DatabaseHelper databaseHelper;
    private Context context;

    public AddCheckInPresenter(Context context, AddCheckInInterface.View view) {
        this.context = context;
        this.view = view;

        databaseHelper = new DatabaseHelper(this.context);
    }

    @Override
    public void removeSingleRecord(CheckinModel checkinModel) {
        databaseHelper.deleteCheckIn(checkinModel);
        view.singleRecordRemoved();
    }

    @Override
    public void saveNewRecore(CheckinModel checkinModel, boolean isNewCheckin) {
        if (isNewCheckin) {
            databaseHelper.insertCheckIn(
                    checkinModel.getTitle(),
                    checkinModel.getPlace(),
                    checkinModel.getDescription(),
                    checkinModel.getLocation(),
                    checkinModel.getDate(),
                    checkinModel.getImage());
            view.newRecordSaved(true);
        } else {
            Log.i("update", String.valueOf(databaseHelper.updateCheckin(checkinModel)));
            databaseHelper.updateCheckin(checkinModel);
            view.newRecordSaved(false);
        }
    }

    @Override
    public void share(String body, String title) {

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        view.share(intent);
    }
}
