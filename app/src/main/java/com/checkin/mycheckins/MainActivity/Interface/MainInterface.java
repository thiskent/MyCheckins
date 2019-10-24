package com.checkin.mycheckins.MainActivity.Interface;

import android.content.Context;

import com.checkin.mycheckins.Models.CheckinModel;

import java.util.List;

public class MainInterface {

    public interface View {
        void updateView(boolean shouldShowRecycler, List<CheckinModel> checkIns);
    }

    public interface Presenter {
        void checkCount(Context context);
    }
}
