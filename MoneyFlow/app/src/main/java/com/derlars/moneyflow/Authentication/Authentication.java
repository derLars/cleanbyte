package com.derlars.moneyflow.Authentication;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Authentication {
    private final static long MESSAGE_TIMEOUT = 60L;

    private static Authentication INSTANCE;

    private Activity activity;

    public interface Callback{
        void onPhoneNumberRequested();
        void onAuthenticationStarted();
        void onCodeRequested();
        void onCodeConfirmationStarted();
        void onAuthenticationCompleted();
    }

    Callback callback;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks serverResponse;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private Authentication(Activity activity, Callback callback){
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        this.callback = callback;

    }

    public static Authentication getInstance(Activity activity, Callback callback) {
        if(INSTANCE == null) {
            INSTANCE = new Authentication(activity, callback);
        }else{
            INSTANCE.setCallback(callback);
        }

        return INSTANCE;
    }

    public static Authentication getInstance() {
        return INSTANCE;
    }

    public static void releaseInstance() {
        INSTANCE = null;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void startAuthentication() {
        if(callback != null) {
            if(isSignedIn()) {
                callback.onAuthenticationCompleted();
            }else{
                callback.onPhoneNumberRequested();
            }
        }
    }

    public void authenticate(String phone) {
        serverResponse = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()  {
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                if(callback != null) {
                    callback.onCodeRequested();
                }
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                completeAuthentication(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    if(callback != null) {
                        callback.onPhoneNumberRequested();
                    }
                } else if (e instanceof FirebaseTooManyRequestsException) {}

            }
        };

        if(callback != null) {
            callback.onAuthenticationStarted();
        }

        //PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, MESSAGE_TIMEOUT, TimeUnit.SECONDS, activity, serverResponse);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder().setPhoneNumber(phone).setTimeout(MESSAGE_TIMEOUT,TimeUnit.SECONDS).setActivity(activity).setCallbacks(serverResponse).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void confirm(String code) {
        if(callback != null) {
            callback.onCodeConfirmationStarted();
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        completeAuthentication(credential);
    }

    private void completeAuthentication(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if(callback != null) {
                if (task.isSuccessful()) {
                    callback.onAuthenticationCompleted();
                } else {
                    callback.onPhoneNumberRequested();
                }
            }
        });
    }

    public boolean isSignedIn() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        return currentUser != null;
    }

    public void signOut() {
        mAuth.signOut();
    }

    public String getUID() {
       return mAuth.getUid();
    }
}
