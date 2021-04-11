package com.derlars.fancymessage.Templates;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.derlars.fancymessage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class LoginFragment extends BaseFragment {
    private final static int MESSAGE_TIMEOUT = 60;

    private class Permission {
        private boolean granted = false;
        private String type;
        private String message;

        public Permission(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public void setGranted() { granted = true; }
        public boolean isGranted() { return granted; }
        public String getType() { return type; }
        public String getMessage() { return message; }
    }

    private class Region implements Comparable {
        private String code;
        private String dial;
        private int image;

        public Region(String code, String dial, int image) {
            this.code = code;
            this.dial = dial;
            this.image = image;
        }

        public String getCode() {
            return code;
        }

        public String getDial() {
            return dial;
        }

        public int getImage() {
            return image;
        }

        public String toString() {
            return code + ", " + dial + ", " + image;
        }

        @Override
        public int compareTo(Object o) {
            return dial.compareTo(((Region)o).getDial());
        }
    }

    private class LoginFlagAdapter extends ArrayAdapter<Region> {
        public LoginFlagAdapter(@NonNull Context context, int resource, List<Region> regions) {
            super(context, resource, regions);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initializeView(position,convertView,parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initializeView(position,convertView,parent);
        }

        private View initializeView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_spinner,null);
            }

            ImageView flagImageView = convertView.findViewById(R.id.flag_image_view);
            TextView countryCodeText = convertView.findViewById(R.id.country_code_text);

            countryCodeText.setText(getItem(position).getDial());
            flagImageView.setImageResource(getItem(position).getImage());

            return convertView;
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks serverResponse;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private FirebaseAuth mAuth;

    public List<Permission> permissions = new ArrayList<Permission>();
    public boolean allPermissionsGranted = false;

    private LoginFlagAdapter adapter;

    public static boolean signOutIntention = false;

    @Override
    public void onViewCreated(@NonNull View view) {

    }

    @Override
    public void onBackPress() {

    }

    @Override
    public void onFloatingButtonClick() {

    }
    private void startPermissionManagement() {
        requestPermission(0);
    }

    private void requestPermission(int requestCode) {
        if (requestCode >= permissions.size()) {
            allPermissionsGranted = true;
            for (Permission permission : permissions) {
                if (!permission.isGranted()) {
                    allPermissionsGranted = false;
                    getActivity().finish();
                    return;
                }
            }
            startAuthentication();
        }else if (ContextCompat.checkSelfPermission(getContext(), permissions.get(requestCode).getType()) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permissions.get(requestCode).getType()}, requestCode);
        }else{
            permissions.get(requestCode).setGranted();
            requestPermission(++requestCode);
        }
    }

    private void startAuthentication() {
        mAuth = FirebaseAuth.getInstance();

        if(signOutIntention) {
            signOutIntention = false;
            signOut();
        }

        if (isAuthenticated()) {
            authenticationDone();
        }else{
            requestIdentifier();
        }
    }

    private boolean isAuthenticated() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    public void signOut() {
        mAuth.signOut();
        requestIdentifier();
    }

    public abstract void requestIdentifier();

    public void setIdentifier(String identifier) {
        serverResponse = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()  {
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;

                requestConfirmationCode();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                authenticate(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                } else if (e instanceof FirebaseTooManyRequestsException) {

                }
                requestIdentifier();
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(identifier,MESSAGE_TIMEOUT, TimeUnit.SECONDS, getActivity(), serverResponse);
    }

    private void authenticate(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    authenticationDone();
                } else {
                    wrongCode();
                }
            }
        });
    }

    public abstract void requestConfirmationCode();

    private void setConfirmationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        authenticate(credential);
    }

    public abstract void wrongCode();

    public abstract void authenticationDone();
}
