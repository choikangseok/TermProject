package com.example.termproject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.example.termproject.R.id.Cname;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class PlayTabata extends AppCompatActivity {
    TextView musicname;
    TextView content;
    TextView cname;
    String[] result;

    float volume = 1;
    float speed = 0.05f;

    long count=0;

    private boolean isPaused = false;
    private boolean isCanceled = false;

    long PreE = 0;
    long first =0;
    int N=3;
    int firstCount =0;
    private TextView countTxt;
    private CountDownTimer countDownTimer;
    private long timeRemaining = 0;
    private long timepartRemaining =0;
    private static final int MILLISINFUTURE = 5 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;//고정값

    File musicdir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    public final File[] filelist = musicdir.listFiles();
    int position;
    MusicService Music_Ser;//MusicService 서비스의 객체를 생성
    String MusicName;

    Button btnStart;
    Button btnPause;
    Button btnResume;
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_tabata);
        musicname = (TextView) findViewById(R.id.nameofmusic);
        content = (TextView) findViewById(R.id.contentofcose);
        cname = (TextView) findViewById(Cname);
        countTxt = (TextView) findViewById(R.id.count_txt);
        final String cose = getIntent().getExtras().getString("Cose_Name");
        final TextView tView = (TextView) findViewById(R.id.tv);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnResume = (Button) findViewById(R.id.btn_resume);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        //Initially disabled the pause, resume and cancel button
        btnPause.setEnabled(false);
        btnResume.setEnabled(false);
        btnCancel.setEnabled(false);
        cname.setText(cose);
        try {
            String cont = "";
            FileInputStream fis = openFileInput(cose + ".txt");
            //파일 읽기 모드로 name+".txt"라는 이름의 파일을 읽어들임
            byte[] buffer = new byte[fis.available()];
            //버퍼를 생성한 후 읽기모드로 연 파일의 내용을 저장
            fis.read(buffer);//버퍼에 저장된 내용을 읽어들임
            cont = new String(buffer);
            //cont TextView에 버퍼에 저장된 내용을 출력


            result = cont.split("/");
            for (int i = 0; i < result.length; i++) {
                if (i == 0) {
                    musicname.setText(result[i]);
                    MusicName = result[i].substring(13, result[i].length() - 1);
                }
                else
                    content.setText(content.getText() + " " + result[i]);
            }
            //result[0]에는 노래 제목이 들어가 있고
            //result[1+4n]에는 effort
            //result[2+4n]에는 Minutees
            //result[3+4n]에는 Seconds
            //result[4+4n]에는 Name이 들어간다
            for(int i = 3; i < result.length; i=i+4) {
                firstCount += ( 60*parseInt(result[i-1]));
                firstCount += parseInt(result[i]);
                //걸리는 시간의 총합을 구해준다
            }


            for (int i = 0; i < result.length; i = i + 4) {
                /*
                result[1+4*i]
                result[2+4*i]
                result[3+4*i]
                result[4+4*i]
                */
            }

            fis.close();//읽기모드를 끝냄

        } catch (IOException e) {//파일 입력 관련 API를 사용하기 때문에 IOException처리를 해준다
            e.printStackTrace();
        };
        for(int i = 0; i < filelist.length; i++) {
            if(MusicName.equals(filelist[i].getName().substring(0, filelist[i].getName().length()-4))) {
                position = i;
            }
        }

        //Set a Click Listener for start button
        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                isPaused = false;
                isCanceled = false;

                //Disable the start and pause button
                btnStart.setEnabled(false);
                btnResume.setEnabled(false);
                //Enabled the pause and cancel button
                btnPause.setEnabled(true);
                btnCancel.setEnabled(true);

                musicstart();//musicstart()함수를 호출해 스타트서비스해준다

                CountDownTimer timer;
                final long millisInFuture = (firstCount)*1000;

                long countDownInterval = 1000; //1 second
                first=millisInFuture;
                count= parseInt(result[3])*1000 + parseInt(result[2])*60000;
                N=3;
                PreE= parseInt(result[3])*1000 + parseInt(result[2])*60000 ;

                //Initialize a new CountDownTimer instance
                timer = new CountDownTimer(millisInFuture,countDownInterval){
                    public void onTick(long millisUntilFinished){
                        if((first-millisUntilFinished) >= PreE){
                            if(result[N-2].equals("REST"))
                                Music_Ser.FadeOut();
                            if(result[N-2].equals("HARD") || result[N-2].equals("EASY"))
                                Music_Ser.FadeIn();

                            N=N+4;

                            if(N<result.length) {
                                count = parseLong(result[N]) * 1000 + parseLong(result[N - 1]) * 60000;

                            }
                            PreE=PreE+count;
                        }
                        if(isPaused || isCanceled)
                        {
                            //If the user request to cancel or paused the
                            //CountDownTimer we will cancel the current instance
                            cancel();
                        }
                        else {
                            //Display the remaining seconds to app interface
                            //1 second = 1000 milliseconds


                            tView.setText("" + (millisUntilFinished / 1000 )/60 +" : " +
                                    (millisUntilFinished / 1000 )%60);
                            //Put count down timer remaining time in a variable
                            timeRemaining = millisUntilFinished;
                            timepartRemaining = timeRemaining - (first - PreE) ;
                            countTxt.setText("" + (timepartRemaining / 1000 )/60 +" : " +
                                    (timepartRemaining / 1000 )%60);


                        }
                    }
                    public void onFinish(){
                        unbindService(musicConnection);//바인드서비스를 해재해주고
                        stopService(new Intent(PlayTabata.this, MusicService.class));//서비스를 종료시켜준뒤


                        //Do something when count down finished
                        tView.setText("Done");
                        countTxt.setText(String.valueOf("Finish ."));

                        //Enable the start button
                        btnStart.setEnabled(true);
                        //Disable the pause, resume and cancel button
                        btnPause.setEnabled(false);
                        btnResume.setEnabled(false);
                        btnCancel.setEnabled(false);
                    }
                }.start();

            }
        });

        //Set a Click Listener for pause button
        btnPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Music_Ser.Pause();//음악을 일시정지시키고

                //When user request to pause the CountDownTimer
                isPaused = true;

                //Enable the resume and cancel button
                btnResume.setEnabled(true);
                btnCancel.setEnabled(true);
                //Disable the start and pause button
                btnStart.setEnabled(false);
                btnPause.setEnabled(false);
            }
        });

        //Set a Click Listener for resume button
        btnResume.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Music_Ser.Restart();//음악을 재개해주고

                //Disable the start and resume button
                btnStart.setEnabled(false);
                btnResume.setEnabled(false);
                //Enable the pause and cancel button
                btnPause.setEnabled(true);
                btnCancel.setEnabled(true);

                //Specify the current state is not paused and canceled.
                isPaused = false;
                isCanceled = false;

                //Initialize a new CountDownTimer instance
                long millisInFuture = timeRemaining;

                long countDownInterval = 1000;
                new CountDownTimer(millisInFuture, countDownInterval){
                    public void onTick(long millisUntilFinished){

                        if((first-millisUntilFinished) >= PreE){
                            if(result[N-2].equals("REST"))
                                Music_Ser.FadeOut();
                            if(result[N-2].equals("HARD") || result[N-2].equals("EASY"))
                                Music_Ser.FadeIn();

                            N=N+4;


                            if(N<result.length)
                                count= parseLong(result[N])*1000 + parseLong(result[N-1])*60000 ;
                            PreE=PreE+count;
                        }

                        //Do something in every tick
                        if(isPaused || isCanceled)
                        {
                            //If user requested to pause or cancel the count down timer

                            cancel();
                        }
                        else {
                            tView.setText("" + (millisUntilFinished / 1000 )/60 +" : " +
                                    (millisUntilFinished / 1000 )%60);
                            //Put count down timer remaining time in a variable
                            timeRemaining = millisUntilFinished;
                            timepartRemaining = PreE -(first-timeRemaining);
                            countTxt.setText("" + (timepartRemaining / 1000 )/60 +" : " +
                                    (timepartRemaining / 1000 )%60);
                        }
                    }
                    public void onFinish(){
                        unbindService(musicConnection);//바인드서비스를 해재해주고
                        stopService(new Intent(PlayTabata.this, MusicService.class));//서비스를 종료시켜준뒤


                        //Do something when count down finished
                        tView.setText("Done");
                        countTxt.setText(String.valueOf("Finish ."));
                        //Disable the pause, resume and cancel button
                        btnPause.setEnabled(false);
                        btnResume.setEnabled(false);
                        btnCancel.setEnabled(false);
                        //Enable the start button
                        btnStart.setEnabled(true);
                    }
                }.start();

                //Set a Click Listener for cancel/stop button
                btnCancel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        unbindService(musicConnection);//바인드서비스를 해재해주고
                        stopService(new Intent(PlayTabata.this, MusicService.class));//서비스를 종료시켜준뒤

                        //When user request to cancel the CountDownTimer
                        isCanceled = true;

                        //Disable the cancel, pause and resume button
                        btnPause.setEnabled(false);
                        btnResume.setEnabled(false);
                        btnCancel.setEnabled(false);
                        //Enable the start button
                        btnStart.setEnabled(true);

                        //Notify the user that CountDownTimer is canceled/stopped
                        tView.setText("CountDownTimer Canceled/stopped.");
                    }
                });
            }
        });

        //Set a Click Listener for cancel/stop button
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                unbindService(musicConnection);//바인드서비스를 해재해주고
                stopService(new Intent(PlayTabata.this, MusicService.class));//서비스를 종료시켜준뒤


                //When user request to cancel the CountDownTimer
                isCanceled = true;

                //Disable the cancel, pause and resume button
                btnPause.setEnabled(false);
                btnResume.setEnabled(false);
                btnCancel.setEnabled(false);
                //Enable the start button
                btnStart.setEnabled(true);

                //Notify the user that CountDownTimer is canceled/stopped
                tView.setText("CountDownTimer Canceled/stopped.");
            }
        });


    }
/*
    public void countDownTimer() {
        //메인에서 인텐트로 (전체시간+1) * 1000]을 전달하자
        countDownTimer = new CountDownTimer((firstCount+1)*1000 , COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                count--;
                countTxt.setText(String.valueOf(count));
                if(count==0 ){
                    N=N+4;
                    if(N<result.length)
                        count= parseInt(result[N]);
                }
            }


            public void onFinish() {
                countTxt.setText(String.valueOf("Finish ."));
            }
        };

    }
    */

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
        }
        countDownTimer = null;
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        // Service에 연결(bound)되었을 때 호출되는 callback 메소드
        // Service의 onBind() 메소드에서 반환한 IBinder 객체를 받음 (두번째 인자)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 두번째 인자로 넘어온 IBinder 객체를 LocalService 클래스에 정의된 LocalBinder 클래스 객체로 캐스팅
            MusicService.LocalBinder binder = (MusicService.LocalBinder)service;
            // LocalService 객체를 참조하기 위해 LocalBinder 객체의 getService() 메소드 호출
            Music_Ser = binder.getService();
        }
        // Service 연결 해제되었을 때 호출되는 callback 메소드
        @Override
        public void onServiceDisconnected(ComponentName name) { }
    };

    public void musicstart() {
        //리스트 뷰의 목록을 클릭할 때와 동일한 과정을 수행하지만 차이점은 재생할 음악은 tmp_position임시 순서 변수에 저장된 순서의 음악이다
        final String musicname = MusicName;
        final String filepath = filelist[position].getAbsolutePath();

        Intent intent = new Intent(PlayTabata.this, MusicService.class);//SeeMemo로 넘어가는 인텐트를 생성하
        intent.putExtra("File_Path", filepath);
        intent.putExtra("Music_Name", musicname);//putExtra를 사용해 위에서 저장된 이름을 이동할 액티비티에
        startService(intent);//액티비티를 실행한다.
        bindService(new Intent(this, MusicService.class), musicConnection, Context.BIND_AUTO_CREATE);
    }


}
