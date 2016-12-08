package info.androidhive.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputID;
    private FirebaseAuth auth;  //firebase의 로그인/회원가입을 위한 authentication(권한) 변수
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {    //로그인이 된 상태로 다시 앱이 켜지면 LoginActivity가 아닌 ChatRoomActivity로 이동한다.
            startActivity(new Intent(LoginActivity.this, ChatRoomActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString(); //입력한 email
                final String password = inputPassword.getText().toString(); //입력한 password

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email, password)    //입력한 email과 password를 firebaseAuth를 이용하여 권한을 넣는다.
                       .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {    //password는 6자리 이상으로 해야 한다.
                                        inputPassword.setError("비밀번호는 6자리 이상으로 입력하세요.");
                                    } else {
                                        Toast.makeText(LoginActivity.this, "로그인 실패!", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, ChatRoomActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onPause() {
        intent = new Intent(this,LoginBgmService.class);
        stopService(intent);
        super.onPause();
    }

    @Override
    protected void onStop() {
        intent = new Intent(this,LoginBgmService.class);
        stopService(intent);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        intent = new Intent(this,LoginBgmService.class);
        startService(intent);
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(this,LoginBgmService.class);
        stopService(intent);
        super.onBackPressed();
    }
}

