package com.example.assignment.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.example.assignment.CommonClass.Constant;
import com.example.assignment.CommonClass.UserDetails;
import com.example.assignment.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.Objects;


public class LoginScreen extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = LoginScreen.class.getSimpleName();
    private SignInButton mSignInButton;
    UserDetails mUserDetails;
    String str_image = "", str_name = "", str_email="";
    private ProgressDialog mConnectionProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        initUI();
        mUserDetails = new UserDetails();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        Constant.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInButton.setScopes(googleSignInOptions.getScopeArray());
    }

    private void initUI() {
        mSignInButton = findViewById(R.id.btn_sign_in);
        mSignInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_sign_in:
                mConnectionProgressDialog.show();
                GooglePlus_SignIn();
                break;
        }
    }

    private void GooglePlus_SignIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(Constant.mGoogleApiClient);
        startActivityForResult(intent, Constant.REQ_CODE);
    }


    private void handleResult(GoogleSignInResult googleSignInResult) {

        if (googleSignInResult.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
            assert googleSignInAccount != null;
            str_name = googleSignInAccount.getDisplayName();
            str_email = googleSignInAccount.getEmail();
            if (googleSignInAccount.getPhotoUrl()!=null)
                 str_image = googleSignInAccount.getPhotoUrl().toString();
            UpdateUI();
        } else {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void UpdateUI() {
        Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
        intent.putExtra("userName", str_name);
        intent.putExtra("emailId", str_email);
        intent.putExtra("imageURL", str_image);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQ_CODE) {
            mConnectionProgressDialog.dismiss();
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(googleSignInResult);
        }
    }
}