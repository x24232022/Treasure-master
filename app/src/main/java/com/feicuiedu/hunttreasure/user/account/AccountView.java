package com.feicuiedu.hunttreasure.user.account;

import android.os.Message;

/**
 * Created by Administrator on 2017/1/13 0013.
 */

public interface AccountView {
    void showProgress();
    void hideProgress();
    void showMessage(String msg);
    void updataPhoto(String photoUrl);
}
