package com.example.termproject;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {
    public String name;//스타트 서비스가 실행될 때 재생되고 있는 음악의 이름이 저장될 변수
    public String filepath;//스타트 서비스가 실행될 때 재생되고 있는 음악의 절대경로가 저장될 변수
    int stop_position;//음악이 일시정지되었을 때 그 위치를 저장하기 위한 변수
    float volume = 1;
    float speed = 0.05f;


    MediaPlayer player = new MediaPlayer();
    public MusicService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // intent: startService() 호출 시 넘기는 intent 객체
        // flags: service start 요청에 대한 부가 정보. 0, START_FLAG_REDELIVERY, START_FLAG_RETRY
        // startId: start 요청을 나타내는 unique integer id
        name = intent.getExtras().getString("Music_Name");//인텐트로 넘겨받은 음악의 이름을 name변수에 저장
        filepath = intent.getStringExtra("File_Path");//인텐트로 넘겨받은 음악의 절대경로를 filepath변수에 저장

        player.stop();//플레이어를 멈추고
        player.release();//릴리즈해준뒤
        player = null;//초기화 시키고
        player = new MediaPlayer();//새로 생성하여서
        try {
            player.setDataSource(filepath);//절대경로변수에 저장된 음악을 소스로 하여
            player.prepare();//준비하고
            player.setLooping(true);
            player.start();//재생시킨다
        } catch(Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    //스탑서비스가 행해질 때 사용되는 onDestroy함수
    public void onDestroy() {
        //음악서비스가 정지됨을 알려준다
        player.stop();//플레이어를 정지시키고
        player.release();//릴리즈한 뒤
        player = null;//초기화시킨다
    }

    public class LocalBinder extends Binder {
        // 클라이언트가 호출할 수 있는 공개 메소드가 있는 현재 Service 객체 반환
        MusicService getService() {
            return MusicService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();//바인드서비스에서 사용될 객체 생성
    @Override
    public IBinder onBind(Intent intent) {
        // 위에서 생성한 LocalBinder 객체를 반환
        return mBinder;
    }

    //음악이 재생중일 때 정지버튼을 누르면 호출되는 함수
    public void Pause(){
        stop_position = player.getCurrentPosition();//재생중이던 음악의 재생위치를 저장후
        player.pause();//플레이어를 일시정지시킨다

    }
    //음악이 일시정지된 상태일 때 정지버튼을 누르면 호출되는 함수
    public void Restart(){
        player.seekTo(stop_position);//플레이어에서 음악의 재생위치를 찾아
        player.start();//그 위치부터 다시 실행시켜준다

    }

    public void nextSong(String filepath, String Musicname) {
        //매개변수로 절대경로와 재생중인 음악의 이름을 받는다
        player.reset();//플레이어를 리셋시키고
        name = Musicname;//매개변수로 받은 이름을 name변수에 저장한뒤
        try {
            player.setDataSource(filepath);//절대경로의 음악을 소스로 하여
            player.prepare();//플레이어를 준비시키고
            player.start();//시작시킨다
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void FadeOut() {
        player.setVolume(0.2f, 0.2f);
       // volume += speed *deltaTime;
    }
    public void FadeIn()
    {
        player.setVolume(1, 1);
        //volume += speed * deltaTime;
    }
}
