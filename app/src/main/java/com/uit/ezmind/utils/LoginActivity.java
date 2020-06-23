package com.uit.ezmind.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.common.SignInButton;
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

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextInputLayout tlEmail, tlPassword;
    private TextInputEditText etEmail, etPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initViews();

        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.action_bar);
        TextView title = bar.getCustomView().findViewById(R.id.tvTitle);
        title.setText(R.string.welcome);

        SignInButton signInButton = findViewById(R.id.google_sign_in);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        tlEmail = findViewById(R.id.tl_email);
        tlPassword = findViewById(R.id.tl_password);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tlEmail.setError(null);
                tlPassword.setError(null);
            }
        });
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tlEmail.setError(null);
                tlPassword.setError(null);
            }
        });


        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skip();
            }
        });

        findViewById(R.id.sign_up_suggestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()) {
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();

                    tlEmail.clearFocus();
                    tlPassword.clearFocus();
                    if (email.isEmpty()) tlEmail.setError(getText(R.string.field_required));
                    else if (password.isEmpty()) tlPassword.setError(getText(R.string.field_required));
                    else {
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle(R.string.logging_in);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage(getText(R.string.please_wait));
                        progressDialog.show();
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            skip();
                                            return;
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Log in failed", Toast.LENGTH_SHORT).show();
                                        }
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            tlPassword.setError(getString(R.string.password_requirement));
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            tlEmail.setError(getString(R.string.invalid_credential));

                                        } catch (FirebaseAuthUserCollisionException e) {
                                            tlEmail.setError(getString(R.string.email_used));
                                        } catch (Exception ignored) {

                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                } else
                    Toast.makeText(LoginActivity.this, getText(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void skip() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent intent = new Intent(LoginActivity.this, MapManagerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        super.onResume();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
