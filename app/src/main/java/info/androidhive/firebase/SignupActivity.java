package info.androidhive.firebase;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

public class SignupActivity extends AppCompatActivity {
    private SQLiteDatabase mDB;
    Cursor mCursor;

    private EditText inputEmail, inputPassword,inputID;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String text="";
    public static int key;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public static String email;
    public static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().getRoot();

        mDB = openOrCreateDatabase("myDB.db", MODE_PRIVATE, null);

        if(mDB!=null) {
            mDB.execSQL("drop table if exists my_table");
            mDB.execSQL("create table my_table (_id integer primary key autoincrement, "
                    + "sid text not null , empty int null);");
        }

        mCursor = mDB.query("my_table",
                new String[] {"sid","empty"},
                null,
                null,
                null,
                null,
                "_id",
                "5");


        try{
            InputStream is = getAssets().open("id_storage.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);

            StringTokenizer stringTokenizer = new StringTokenizer(text, " \n");
            ContentValues v = new ContentValues();
            while(stringTokenizer.hasMoreElements()){
                String id = stringTokenizer.nextToken();
                v.put("sid",id);
                v.put("empty",0);
                mDB.insert("my_table",null,v);
            }

        }catch(IOException e){}


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputID = (EditText)findViewById(R.id.ID);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String id = inputID.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(id)) {
                    Toast.makeText(getApplicationContext(), "Enter id!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int p=0;
                try{
                    mDB = openOrCreateDatabase("myDB.db", 0, null);
                    mCursor = mDB.rawQuery("SELECT sid FROM " + "my_table", null);
                    if (mCursor.moveToFirst()) {
                        int k = 0;
                        do {
                            if (p != 0)
                                break;

                            //        for (int j = 0; j < mCursor.getColumnCount(); j++) {
                            if (id.equals(mCursor.getString(k))) {
                                progressBar.setVisibility(View.VISIBLE);
                                //create user
                                ContentValues u = new ContentValues();
                                u.put("empty", 1);
                                mDB.update("my_table", u, "sid=" + mCursor.getString(k), null);
                                auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                // If sign in fails, display a message to the user. If sign in succeeds
                                                // the auth state listener will be notified and logic to handle the
                                                // signed in user can be handled in the listener.
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Intent intent = new Intent(SignupActivity.this, ChatRoomActivity.class);
                                                    intent.putExtra("send_empty", key);

                                                    String userName = email;
//                                                    String userName = name;
                                                    UserData username = new UserData(userName);
                                                    databaseReference.child("user").push().setValue(username);


                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });
                                p++;
                                break;

                            }
//                                else{
//                                    if(mCursor.getInt(j)!=0&&k==0) {
//                                        Toast.makeText(SignupActivity.this, "Not Exist Id!!!",
//                                                Toast.LENGTH_SHORT).show();
//                                        k++;
//                                        //return;
//                                    }
//                                }

                        } while (mCursor.moveToNext());
                        if (p == 0) {
                            Toast.makeText(SignupActivity.this, "Not Exist Id!!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch(Exception e){}

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}