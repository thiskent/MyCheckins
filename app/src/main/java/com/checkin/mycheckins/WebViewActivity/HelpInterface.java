package com.checkin.mycheckins.WebViewActivity;

public interface HelpInterface {

    interface View {

        void showWebPage(String url);
    }

    interface Presenter {
        void requestWebpage();
    }
}
