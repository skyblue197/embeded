package info.androidhive.firebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class InitActivity extends Activity {    //어플을 실행했을 때 1.5초 동안 보여주기 위한 activity

    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);   //딜레이가 끝나고 다음 Login activity로 넘어간다.
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        handler.postDelayed(r, 1500);   //onResume이 불러질 때, 1.5초동안 딜레이를 준다.
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        handler.removeCallbacks(r);
    }


}
