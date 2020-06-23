package com.uit.ezmind.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.uit.ezmind.R;
import com.uit.ezmind.maploader.MapManagerActivity;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout tlUsername, tlEmail, tlPassword, tlConfirmPassword;
    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar bar=getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.action_bar);
        TextView title= bar.getCustomView().findViewById(R.id.tvTitle);
        title.setText(R.string.sign_up);

        findViewById(R.id.log_in_suggestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLogin();
            }
        });
        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skip();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        findViews();

        Button btnSignUp=findViewById(R.id.sign_up);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkConnected()) {
                    final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setTitle(R.string.signing_up);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage(getText(R.string.please_wait));
                    progressDialog.show();
                    tlUsername.clearFocus();
                    tlEmail.clearFocus();
                    tlPassword.clearFocus();
                    tlConfirmPassword.clearFocus();
                    String email = etEmail.getText().toString();
                    final String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    String confirmPassword = etConfirmPassword.getText().toString();

                    boolean requirementMatched = true;

                    if (username.isEmpty()) {
                        tlUsername.setError(getText(R.string.field_required));
                        requirementMatched = false;
                    }
                    if (email.isEmpty()) {
                        tlEmail.setError(getText(R.string.field_required));
                        requirementMatched = false;
                    }
                    if (!confirmPassword.equals(password)) {
                        requirementMatched = false;
                        Log.i("pass", confirmPassword + "  " + password);
                        tlConfirmPassword.setError(getText(R.string.password_mismatch));
                    }

                    if (requirementMatched)
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .build();

                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SignUpActivity.this, "Sign up succesfully", Toast.LENGTH_SHORT).show();
                                                        skip();
                                                    } else {
                                                        Toast.makeText(SignUpActivity.this, "Can not update profile", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            tlPassword.setError(getString(R.string.password_requirement));
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            tlEmail.setError(getString(R.string.invalid_email));
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            tlEmail.setError(getString(R.string.email_used));
                                        } catch (Exception ignored) {

                                        }

                                    }
                                });
                }
                else Toast.makeText(SignUpActivity.this, getText(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findViews(){
        tlUsername=findViewById(R.id.tl_username);
        tlEmail=findViewById(R.id.tl_email);
        tlPassword=findViewById(R.id.tl_password);
        tlConfirmPassword=findViewById(R.id.tl_confirm_password);

        etUsername=findViewById(R.id.et_username);
        etEmail=findViewById(R.id.et_email);
        etPassword=findViewById(R.id.et_password);
        etConfirmPassword =findViewById(R.id.et_confirm_password);

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tlUsername.setError(null);
            }
        });
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tlEmail.setError(null);
            }
        });
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tlPassword.setError(null);
            }
        });
        etConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tlConfirmPassword.setError(null);
            }
        });
    }

    @Override
    protected void onResume() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        super.onResume();
    }

    private void skip(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent intent=new Intent(SignUpActivity.this, MapManagerActivity.class);
        startActivity(intent);
    }

    private void toLogin(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent intent=new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
