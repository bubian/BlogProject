package com.pds.ui.login;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;

import com.google.android.material.button.MaterialButton;
import com.pds.ui.R;
import com.pds.ui.utils.ToastUtil;
import com.pds.ui.utils.ViewUtil;
import com.pds.ui.view.et.ClearEditText;
import com.pds.util.data.StringUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author pengdaosong
 */
public class LoginCommonWidget extends FrameLayout implements View.OnClickListener {

    // 手机验证码登录
    public static final int MODE_LOGIN_BY_CODE = 0x01;
    //  账号密码登录
    public static final int MODE_LOGIN_BY_PW = 0x02;
    private static final int GET_SMS_STATE_KEY = R.id.password_assist_view;
    private static final String TAG = LoginCommonWidget.class.getSimpleName();
    MaterialButton btnLogin;
    private
    @DisplayViewMode
    int mDisplayViewMode = MODE_LOGIN_BY_CODE;
    private final String VERTIFY_CODE_STR;
    private final String ACCOUNT_HINT_MOBILEPHONE_MODE;
    private final String PASSWORD_HINT_MOBILEPHONE_MODE;
    private final String PASSWORD_HINT_ACCOUNT_MODE;
    ClearEditText accountEditView;
    EditText passwordEditView;
    TextView passwordAssistView;

    @IntDef({MODE_LOGIN_BY_CODE, MODE_LOGIN_BY_PW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayViewMode {

    }

    private OnClickListener externalOnclickListener;

    public LoginCommonWidget(Context context) {
        this(context, null);
    }

    public LoginCommonWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginCommonWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        VERTIFY_CODE_STR = context.getString(R.string.get_verify_code);
        ACCOUNT_HINT_MOBILEPHONE_MODE = context.getString(R.string.input_mobile_hint);
        PASSWORD_HINT_MOBILEPHONE_MODE = context.getString(R.string.input_sms_code_hint);
        PASSWORD_HINT_ACCOUNT_MODE = context.getString(R.string.input_password_hint);
        initViews();
    }

    private void initViews() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_login_view, this);
        accountEditView = view.findViewById(R.id.account_editview);
        passwordEditView = view.findViewById(R.id.password_editview);
        passwordAssistView = view.findViewById(R.id.password_assist_view);
        passwordAssistView.setOnClickListener(this);
        btnLogin = view.findViewById(R.id.btn_login);
        switchDisplayViewMode(MODE_LOGIN_BY_CODE);
        passwordEditView.setEnabled(false);
        passwordEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnLogin.performClick();
                    return true;
                }
                return false;
            }
        });

        accountEditView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
    }


    public void setExternalOnclickListener(OnClickListener loginBtnOnclickListener) {
        this.externalOnclickListener = loginBtnOnclickListener;
    }

    public EditText getAccountEditView() {
        return accountEditView;
    }

    public String getAccountValue() {
        return accountEditView.getText().toString().trim();
    }

    public String getPasswordValue() {
        return passwordEditView.getText().toString().trim();
    }

    public void setLoginButtonText(String text) {
        btnLogin.setText(text);
    }

    public void switchDisplayViewMode(@DisplayViewMode int viewMode) {
        if (mDisplayViewMode == viewMode) {
            return;
        }
        mDisplayViewMode = viewMode;
        passwordAssistView.setVisibility(viewMode == MODE_LOGIN_BY_CODE ? VISIBLE : GONE);
        if (viewMode == MODE_LOGIN_BY_CODE) {
            accountEditView.setHint(ACCOUNT_HINT_MOBILEPHONE_MODE);
            passwordEditView.setHint(PASSWORD_HINT_MOBILEPHONE_MODE);
            accountEditView.setInputType(InputType.TYPE_CLASS_PHONE);
            passwordEditView.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (viewMode == MODE_LOGIN_BY_PW) {
            accountEditView.setHint(ACCOUNT_HINT_MOBILEPHONE_MODE);
            passwordEditView.setHint(PASSWORD_HINT_ACCOUNT_MODE);
            accountEditView.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            passwordEditView.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        accountEditView.requestFocus();
        passwordEditView.setEnabled(isEnabledPassword());
        passwordEditView.setText(null);
    }

    private boolean isEnabledPassword() {
        String pw = passwordAssistView.getText().toString();
        return !(mDisplayViewMode == MODE_LOGIN_BY_CODE && VERTIFY_CODE_STR
                .equalsIgnoreCase(pw));
    }

    /**
     * @return 当前显示UI模式
     */
    public int getDisplayViewMode() {
        return mDisplayViewMode;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.password_assist_view) {
            getVerificationCode(v);
        } else if (id == R.id.btn_login) {
            performLogin(v);
        }
    }

    private int delayTime = 60;
    Timer timer  = new Timer(true);
    void getVerificationCode(View view) {
        final int id = view.getId();
        //点击获取验证码
        //1.校验手机号格式是否正常
        //2.判断获取状态（是否能获取，倒计时状态不可点击）
        if (mDisplayViewMode != MODE_LOGIN_BY_CODE) {
            return;
        }
        if (passwordAssistView.getTag(GET_SMS_STATE_KEY) == null && checkAccountStr()) {
            passwordEditView.setHint(PASSWORD_HINT_MOBILEPHONE_MODE);
            passwordAssistView.setTag(GET_SMS_STATE_KEY, "ing");
            passwordEditView.setEnabled(true);
            passwordEditView.requestFocus();
            //倒计时
            TimerTask task = new TimerTask() {
                public void run() {
                    //每次需要执行的代码放到这里面。
                    long l = delayTime--;
                    if (l <= 0) {
                        //显示语音验证码提示
                        passwordAssistView.setEnabled(true);
                        passwordAssistView.setTag(GET_SMS_STATE_KEY, null);
                        if (id == R.id.password_assist_view) {
                            passwordAssistView.setText(R.string.get_again);
                        } else {
                            passwordAssistView.setText(R.string.get_verify_code);
                        }
                        return;
                    }
                    passwordAssistView.setText(String.valueOf(l).concat("s"));
                }
            };
            timer.schedule(task, delayTime * 1000,1_000);
            if (externalOnclickListener != null) {
                externalOnclickListener.onClick(view);
            }
        }
    }

    /**
     * @return 检查账号合法性
     */
    private boolean checkAccountStr() {
        boolean isValid = false;
        final String accountStr = accountEditView.getText().toString().trim();
        if (mDisplayViewMode == MODE_LOGIN_BY_CODE) {
            isValid = StringUtils.isMobileNO(accountStr);
        } else if (mDisplayViewMode == MODE_LOGIN_BY_PW) {
            isValid = !TextUtils.isEmpty(accountStr);
        }
        if (!isValid) {
            ViewUtil.shakeView(accountEditView, 250);
            ToastUtil.showMessage(getResources().getString(R.string.error_phone));
            accountEditView.requestFocus();
        }
        return isValid;
    }

    private boolean checkPasswordStr() {
        boolean isValid = false;
        final String pwdStr = passwordEditView.getText().toString().trim();
        String errorTips = passwordEditView.getHint().toString();

        if (mDisplayViewMode == MODE_LOGIN_BY_CODE) {
            isValid = !TextUtils.isEmpty(pwdStr);
        } else if (mDisplayViewMode == MODE_LOGIN_BY_PW) {
            isValid = !TextUtils.isEmpty(pwdStr) && pwdStr.length() >= 6 && pwdStr.length() <= 16;
            if (!isValid && !TextUtils.isEmpty(pwdStr)) {
                errorTips = getResources().getString(R.string.login_pwd_error);
            }
        }
        if (!isValid) {
            ViewUtil.shakeView(passwordEditView, 250);
            ToastUtil.showMessage(errorTips);
            passwordEditView.requestFocus();
        }
        return isValid;
    }


    private void performLogin(View v) {
        if (ViewUtil.isFastDoubleClick()) {
            return;
        }
        if (externalOnclickListener != null && checkAccountStr() && checkPasswordStr()) {
            externalOnclickListener.onClick(v);
        }
    }
}
