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

        mDB = openOrCreateDatabase("myDB.db", MODE_PRIVATE, null);//myDB라는 이름의 데이터베이스를 연다.

        if(mDB!=null) {
            mDB.execSQL("drop table if exists my_table");
            mDB.execSQL("create table my_table (_id integer primary key autoincrement, "
                    + "sid text not null , empty int null);");//DB생성
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
            InputStream is = getAssets().open("id_storage.txt");//학생 정보 데이터 열기
            int size = is.available(); // 학생 정보를 읽어와 담아낼 버퍼 크기
            byte[] buffer = new byte[size]; // 학생 정보를 읽어와서 담아낼 버퍼 생성
            is.read(buffer); //버퍼의 데이터를 읽기
            is.close();
            text = new String(buffer); //버퍼에서 읽어온 데이터를 텍스트 변수에 저장

            StringTokenizer stringTokenizer = new StringTokenizer(text, " \n"); //학생정보(학번)는 스페이스로 나뉘어 있는데 이를 토큰으로 받아 스페이스를 단위기준으로 데이터를 토크나이저에 저장
            ContentValues v = new ContentValues(); //SQLite에 저장할 객체 생성
            while(stringTokenizer.hasMoreElements()){ // 토크나이저에 스페이스 단위로 나뉘어진 요소가 남아있다면
                String id = stringTokenizer.nextToken();
                v.put("sid",id); // 학번 저장
                v.put("empty",0); // 회원가입이 아직 되지않았음을 나타내는 0을 저장
                mDB.insert("my_table",null,v); //my_table이라는 테이블에 v객체에 저장된(학번,회원가입 횟수)를 데이터를 삽입
            }

        }catch(IOException e){}

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

        btnSignUp.setOnClickListener(new View.OnClickListener() {   //SignUp버튼을 눌렀을 때 불러지는 method
            @Override
            public void onClick(View v) {

                email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String id = inputID.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(id)) {
                    Toast.makeText(getApplicationContext(), "학번을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "비밀번호는 6자리 이상으로 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int p=0;
                try{
                    mDB = openOrCreateDatabase("myDB.db", 0, null); //myDB라는 이름의 데이터베이스를 open
                    mCursor = mDB.rawQuery("SELECT sid FROM " + "my_table", null); //my_table테이블에서 raw단위로 Query문 실행
                    if (mCursor.moveToFirst()) {//커서에서 첫번째를 읽어 비어있지 않다면 실행
                        int k = 0;
                        do {
                            if (p != 0)
                                break;

                            if (id.equals(mCursor.getString(k))) {//회원가입창에 입력한 학번이 DB에 저장된 학번에 등록이 되있는지 되있다면 실행
                                progressBar.setVisibility(View.VISIBLE);
                                //create user
                                ContentValues u = new ContentValues();
                                u.put("empty", 1); // 회원가입을 하는 것이므로 empty라는 변수에 숫자1을 삽입
                                mDB.update("my_table", u, "sid=" + mCursor.getString(k), null); // empty를 수정한것을 나타내기위해 update사용
                                auth.createUserWithEmailAndPassword(email, password) //이메일과 비밀번호를 받아 회원가입 실행
                                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                progressBar.setVisibility(View.GONE);
                                                if (!task.isSuccessful()) {//실패할경우 처리
                                                    Toast.makeText(SignupActivity.this, "회원가입에 실패했습니다.",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {//성공했을 경우 처리
                                                    String name = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                                    Toast.makeText(SignupActivity.this, "환영합니다!" + name + "님", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignupActivity.this, ChatRoomActivity.class);
                                                    intent.putExtra("send_empty", key);
                                                    String userName = email;
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
                        } while (mCursor.moveToNext());
                        if (p == 0) {//SQLite DB에 등록되지 않은 학번을 입력하여 회원가입 버튼을 눌렀을 경우.
                            Toast.makeText(SignupActivity.this, "명지대학생이 아닙니다.",
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