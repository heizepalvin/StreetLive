package com.example.heizepalvin.streetlive.mainFragment.LiveFragment;

public class ChattingItem {

    String chattingData;
    String nickname;

    public ChattingItem(String nickname, String chattingData){
        this.nickname = nickname;
        this.chattingData = chattingData;
    }

    public String getChattingData() {
        return chattingData;
    }

    public void setChattingData(String chattingData) {
        this.chattingData = chattingData;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
