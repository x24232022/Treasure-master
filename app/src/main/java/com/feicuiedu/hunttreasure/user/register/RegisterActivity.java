package com.feicuiedu.hunttreasure.user.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.feicuiedu.hunttreasure.MainActivity;
import com.feicuiedu.hunttreasure.R;
import com.feicuiedu.hunttreasure.commons.ActivityUtils;
import com.feicuiedu.hunttreasure.commons.RegexUtils;
import com.feicuiedu.hunttreasure.custom.AlertDialogFragment;
import com.feicuiedu.hunttreasure.treasure.HomeActivity;
import com.feicuiedu.hunttreasure.user.User;
import com.feicuiedu.hunttreasure.user.register.RegisterPresenter;
import com.feicuiedu.hunttreasure.user.register.RegisterView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements RegisterView {

    private String confirm;
    private String password;
    private String username;
    private ActivityUtils mActivityUtils;
    private ProgressDialog mDialog;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_Username)
    EditText mEtUsername;
    @BindView(R.id.et_Password)
    EditText mEtPassword;
    @BindView(R.id.et_Confirm)
    EditText mEtConfirm;
    @BindView(R.id.btn_Register)
    Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //当设置setContentView会触发onContentChanged方法
        setContentView(R.layout.activity_register);

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        mActivityUtils = new ActivityUtils(this);
        //Toobar设置为ActionBar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            //激活左上角的返回按钮设置返回按钮
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //设置Toobar的标题
            getSupportActionBar().setTitle(R.string.register);
        }
        //设置文本改变监听器
        mEtUsername.addTextChangedListener(textWatcher);
        mEtPassword.addTextChangedListener(textWatcher);
        mEtConfirm.addTextChangedListener(textWatcher);
    }

    //文本改变监听器
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //获取输入框文本内容
            username = mEtUsername.getText().toString();
            password = mEtPassword.getText().toString();
            confirm = mEtConfirm.getText().toString();

            //判断文本内容是否符合要求
            boolean canregister = !(TextUtils.isEmpty(username) ||
                    TextUtils.isEmpty(password) ||
                    TextUtils.isEmpty(confirm)) &&
                    password.equals(confirm);

            //文本内容格式符合要求后激活注册按钮
            mBtnRegister.setEnabled(canregister);
        }
    };

    //Toobar上的选项菜单点击方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_Register)
    public void onClick() {
        if (RegexUtils.verifyUsername(username) != RegexUtils.VERIFY_SUCCESS) {
            AlertDialogFragment.getInstances(
                    getString(R.string.username_error),
                    getString(R.string.username_rules))
                    .show(getSupportFragmentManager(), "usernameError");
            return;
        }
        if (RegexUtils.verifyPassword(password) != RegexUtils.VERIFY_SUCCESS) {
            AlertDialogFragment.getInstances(
                    getString(R.string.password_error),
                    getString(R.string.password_rules))
                    .show(getSupportFragmentManager(), "passwordError");
            return;
        }
        RegisterPresenter presenter=new RegisterPresenter(this);
        presenter.register(new User(username,password));
    }

    //跳转界面
    @Override
    public void navigationToHome() {
        mActivityUtils.startActivity(HomeActivity.class);
        finish();
        Intent intent=new Intent(MainActivity.MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //显示信息
    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    //显示进度
    @Override
    public void showProgress() {
        mDialog = ProgressDialog.show(this, "注册", "正在注册中,请稍后");
    }

    //隐藏进度
    @Override
    public void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


}
