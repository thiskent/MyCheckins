package com.checkin.mycheckins.NewCheckin.Interface;

import android.content.Intent;

import com.checkin.mycheckins.Models.CheckinModel;

public interface AddCheckInInterface {

    interface View {
       void singleRecordRemoved();
       void newRecordSaved(boolean isNewCheckin);
       void share(Intent intent);
    }

    interface Presenter {
        void removeSingleRecord(CheckinModel checkinModel);
        void saveNewRecore(CheckinModel checkinModel, boolean isNewCheckin);
        void share(String text, String title);
    }
}
