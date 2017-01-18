package com.feicuiedu.hunttreasure.user.register;

/**
 * Created by Administrator on 2017/1/3 0003.
 */

public interface RegisterView {
    //将方法类中需要调用UI的方法全部变为接口方法
    void showProgress();
    void  hideProgress();
    void showMessage(String msg);
    void navigationToHome();
}
