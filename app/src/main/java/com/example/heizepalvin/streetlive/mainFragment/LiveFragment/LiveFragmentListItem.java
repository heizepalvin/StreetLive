package com.example.heizepalvin.streetlive.mainFragment.LiveFragment;

public class LiveFragmentListItem {
    private int liveImage;
    private String liveThumnail;
    private String liveTitle;
    private String liveNickname;
    private String liveKey;

    public LiveFragmentListItem(String liveTitle,String liveNickname, String livekey,String liveThumnail){
        this.liveTitle = liveTitle;
        this.liveNickname = liveNickname;
        this.liveKey = livekey;
        this.liveThumnail = liveThumnail;
    }

    public int getLiveImage() {
        return liveImage;
    }

    public void setLiveImage(int liveImage) {
        this.liveImage = liveImage;
    }

    public String getLiveTitle() {
        return liveTitle;
    }

    public void setLiveTitle(String liveTitle) {
        this.liveTitle = liveTitle;
    }

    public String getLiveThumnail() {
        return liveThumnail;
    }

    public void setLiveThumnail(String liveThumnail) {
        this.liveThumnail = liveThumnail;
    }

    public String getLiveNickname() {
        return liveNickname;
    }

    public void setLiveNickname(String liveNickname) {
        this.liveNickname = liveNickname;
    }

    public String getLiveKey() {
        return liveKey;
    }

    public void setLiveKey(String liveKey) {
        this.liveKey = liveKey;
    }
}
