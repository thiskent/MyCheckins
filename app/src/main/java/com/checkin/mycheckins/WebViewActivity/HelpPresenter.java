package com.checkin.mycheckins.WebViewActivity;

public class HelpPresenter implements HelpInterface.Presenter{

    private static final String url = "https://www.wikihow.com/Check-In-on-Facebook";
    private HelpInterface.View view;

    public HelpPresenter(HelpInterface.View view) {
        this.view = view;
    }

    @Override
    public void requestWebpage() {
        view.showWebPage(url);
    }
}
