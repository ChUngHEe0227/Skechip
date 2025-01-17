package ajou.com.skechip;;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LauncyActivity extends AppCompatActivity {

    private final String TAG = "ssss.LaunchActivity";

    private Long kakaoUserID;
    private String kakaoUserProfileImg;
    private String kakaoUserName;

    //for kakao API
    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcy);

        //for kakao API
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
        //kakao

//        String idx = getPreferences("idx");
//        String username = getPreferences("username");
//        String password = getPreferences("password");
//        Boolean login = getPreferencesBoolean("login");
//
//        if(idx != "" && username != "" && login)
//        {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.putExtra("username", username);
//            intent.putExtra("password", password);
//            intent.putExtra("idx", idx);
//
//            finish();
//            startActivity(intent);
//        }
    }//onCreate

    //for kakao API
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            //로그인 완료 직 후 session 열림
            //이미 런치 후 로그인 된 상태에서 앱 켜도 여기 먼저 뜬다.
            Log.d(TAG, "onSessionOpened");
            startMainActivity();
        }
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
                Log.d(TAG, "onSessionFailed");
                //아마 여기에 카톡 재로그인 로직 구현해야 할 듯?
            }
        }
    }

    //로그인 완료 후 시작할 액티비티
    protected void startMainActivity() {
        //<제호> 런치액티비티에서 프로필 정보를 얻어와서 메인액티비티 시작할 때 넘겨줘야 하는가?
        // -> 아닐 것 같다. 카톡 세션이 오픈 fail 뜨면 다시 로그인하게 해야하므로
        Intent intent = new Intent(this, MainActivity.class);;

        startActivity(intent);
        finish();

    }

    private void onClickSignup() {
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put("nickname", "leo");
        properties.put("age", "33");

        UserManagement.getInstance().requestSignup(new ApiResponseCallback<Long>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d(TAG, "onSessionClosed");
            }

            @Override
            public void onNotSignedUp() {
                Log.d(TAG, "onNotSignedUp");
            }

            @Override
            public void onSuccess(Long result) {
                Log.d(TAG, "onClickSignup - ID :" + result);
            }
        }, properties);

    }


    protected void showErrorDialog(String message) { }

    // 값(Key Data) 삭제하기
    private void removePreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("hi");
        editor.apply();
    }

    public void savePreferences(String key, String value)
    {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value).apply();
    }

    public void savePreferencesBoolean(String key, boolean value){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value).apply();
    }

    public String getPreferences(String key)
    {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public boolean getPreferencesBoolean(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }


    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }




}//LauncyActivity
