package info.androidhive.firebase;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class ChatRoomBgmService extends Service {
    private MediaPlayer mp;

    public ChatRoomBgmService() {
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        mp = MediaPlayer.create(this,R.raw.waitroombgm);
        mp.setLooping(true);
        mp.start();
        return 0;
    }

    public void onDestroy(){
        super.onDestroy();
        mp.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
