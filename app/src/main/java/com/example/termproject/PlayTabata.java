package com.example.termproject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.termproject.R.id.Cname;
import static com.example.termproject.R.id.count_txt;
import static com.example.termproject.R.id.tv;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class PlayTabata extends AppCompatActivity implements TextToSpeech.OnInitListener {


    Spinner spinmus;
    public ArrayAdapter<String> m_Adapter;//스트링 형태의 어레이어댑터 객체를 생성
    ArrayList<File> Music_File = new ArrayList<File>();//File형태의 어레이리스트 생성
    ArrayList<String> values = new ArrayList<>();//리스트 뷰에서 보이게 될 항목
    File musicdir2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    File files[] = {};//밑에서 다시한번 디렉토리의 파일을 읽어들일 필요가 있으므로 선언
    public final File[] FILELIST = musicdir2.listFiles();//위의 저장된 항목들의 리스트를 File배열을 생성해 저장시켜줌

    boolean isStart;
    boolean firstSelect;
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

    boolean bindState = false;

    ProgressBar prog;
    ProgressBar prog2;

    private TextToSpeech myTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_tabata);
        myTTS = new TextToSpeech(this, this);
        musicname = (TextView) findViewById(R.id.nameofmusic);
        content = (TextView) findViewById(R.id.contentofcose);
        cname = (TextView) findViewById(Cname);
        countTxt = (TextView) findViewById(count_txt);
        final String cose = getIntent().getExtras().getString("Cose_Name");
        final TextView tView = (TextView) findViewById(tv);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnResume = (Button) findViewById(R.id.btn_resume);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        spinmus = (Spinner)findViewById(R.id.spin1);



        prog= (ProgressBar)findViewById(R.id.progressBar1);
        prog2= (ProgressBar)findViewById(R.id.progressBar2);
        prog.setMax(100);
        prog.setScaleY(20f);

        prog2.setMax(100);
        prog2.setScaleY(30f);

        prog.setProgress(0);
        prog2.setProgress(0);


        m_Adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, values);
        spinmus.setAdapter(m_Adapter);
        files = musicdir2.listFiles();//위에서 선언했던 files 배열에 디렉토리에 저장되어 있는 파일들을 저장한다
        int num = files.length;//배열의 길이를 알아낸다
        // 아래 코드는 files 배열의 길이만큼 for 루프를 돈다
        for (int i = 0; i < num; i++) {
            if(FILELIST[i].getName().endsWith(".mp3") || FILELIST[i].getName().endsWith(".MP3") )//FILELIST의 i번째 원소의 파일 이름의 마지막이
                //.mp3 혹은 .MP3로 끝나는 경우
                Music_File.add(FILELIST[i]);//Music_File리스트에 추가시켜준다.
        }

        for(File file : Music_File) {
            String f_name;//파일의 이름이 저장될 String형 변수를 선언 후
            f_name = file.getName().substring(0, file.getName().length()-4) ;
            //확인중인 파일의 .mp3를 제외한 이름을 저장시킨 뒤
            m_Adapter.add(f_name);//어댑터 객체를 사용해 리스트 뷰에 추가시켜준다
        }



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

                    } else
                        content.setText(content.getText() + result[i]);
                }
                //result[0]에는 노래 제목이 들어가 있고
                //result[1+4n]에는 effort
                //result[2+4n]에는 Minutees
                //result[3+4n]에는 Seconds
                //result[4+4n]에는 Name이 들어간다
                for (int i = 3; i < result.length; i = i + 4) {
                    firstCount += (60 * parseInt(result[i - 1]));
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
            }
            ;
            for (int i = 0; i < filelist.length; i++) {
                if (MusicName.equals(filelist[i].getName().substring(0, filelist[i].getName().length() - 4))) {
                    position = i;
                }
            }

        //Set a Click Listener for start button
        btnStart.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){

                isPaused = false;
                isStart = true;
                isCanceled = false;

                //Disable the start and pause button
                btnStart.setEnabled(false);
                btnResume.setEnabled(false);
                //Enabled the pause and cancel button
                btnPause.setEnabled(true);
                btnCancel.setEnabled(true);
                myTTS.speak(result[N+1] + "시작", TextToSpeech.QUEUE_FLUSH, null);




                musicstart();//musicstart()함수를 호출해 스타트서비스해준다

                CountDownTimer timer;
                long millisInFuture = (firstCount)*1000;

                long countDownInterval = 1000; //1 second
                first=millisInFuture;
                count= parseInt(result[3])*1000 + parseInt(result[2])*60000;
                PreE= parseInt(result[3])*1000 + parseInt(result[2])*60000 ;
                N=3;

                //Initialize a new CountDownTimer instance
                timer = new CountDownTimer(millisInFuture,countDownInterval){
                    public void onTick(long millisUntilFinished){

                        if((first-millisUntilFinished) >= PreE){

                            N=N+4;
                            if(result[N-2].equals("\nREST"))
                                myTTS.speak("휴식", TextToSpeech.QUEUE_FLUSH, null);
                            if(result[N-2].equals("\nHARD") || result[N-2].equals("\nEASY"))
                                myTTS.speak(result[N+1] + " 시작", TextToSpeech.QUEUE_FLUSH, null);

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

                            if(result[N-2].equals("\nREST")) {
                                Music_Ser.FadeOut();
                            }
                            if(result[N-2].equals("\nHARD") || result[N-2].equals("\nEASY")) {
                                Music_Ser.FadeIn();
                            }

                            tView.setText("" + (millisUntilFinished / 1000 )/60 +" : " +
                                    (millisUntilFinished / 1000 )%60);
                            prog.setProgress(
                                    ((int)(first/10)-(int)(millisUntilFinished/10))/(int)(first/1000)
                            );
                            //Put count down timer remaining time in a variable
                            timeRemaining = millisUntilFinished;
                            timepartRemaining = timeRemaining - (first - PreE) ;
                            countTxt.setText("" + (timepartRemaining / 1000 )/60 +" : " +
                                    (timepartRemaining / 1000 )%60);
                            if( ((int)timepartRemaining /1000)==5 )
                                    myTTS.speak("파이브", TextToSpeech.QUEUE_FLUSH, null);
                            if( ((int)timepartRemaining /1000)==4 )
                                    myTTS.speak("포호올", TextToSpeech.QUEUE_FLUSH, null);
                            if( ((int)timepartRemaining /1000)==3 )
                                    myTTS.speak("쓰리이", TextToSpeech.QUEUE_FLUSH, null);
                            if( ((int)timepartRemaining /1000)==2 )
                                    myTTS.speak("투우우", TextToSpeech.QUEUE_FLUSH, null);
                            if( ((int)timepartRemaining /1000)==1 )
                                    myTTS.speak("워어언", TextToSpeech.QUEUE_FLUSH, null);


                            prog2.setProgress(
                                    ((int)(count/10)-(int)(timepartRemaining/10))/(int)(count/1000)
                            );


                        }
                    }
                    public void onFinish(){
                        unbindService(musicConnection);//바인드서비스를 해재해주고
                        stopService(new Intent(PlayTabata.this, MusicService.class));//서비스를 종료시켜준뒤
                        bindState = false;
                        isStart=false;
                        //Do something when count down finished
                        tView.setText("Done");
                        countTxt.setText(String.valueOf("Finish ."));

                        myTTS.speak("고생하셨습니다. 당신은 멋쟁이 ", TextToSpeech.QUEUE_FLUSH, null);
                        prog.setProgress(100);
                        prog2.setProgress(100);

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
                myTTS.speak("헐 설마 그만두게?", TextToSpeech.QUEUE_FLUSH, null);
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
                myTTS.speak("다시 시작", TextToSpeech.QUEUE_FLUSH, null);
                Music_Ser.Restart();//음악을 재개해주고
//                Toast.makeText(PlayTabata.this, "확인", Toast.LENGTH_SHORT).show();

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
                            N=N+4;
                            if(result[N-2].equals("\nREST"))
                                myTTS.speak("휴식", TextToSpeech.QUEUE_FLUSH, null);
                            if(result[N-2].equals("\nHARD") || result[N-2].equals("\nEASY"))
                                myTTS.speak(result[N+1] + " 시작", TextToSpeech.QUEUE_FLUSH, null);
                            if(N<result.length) {
                                count = parseLong(result[N]) * 1000 + parseLong(result[N - 1]) * 60000;

                            }
                            PreE=PreE+count;
                        }

                        //Do something in every tick
                        if(isPaused || isCanceled)
                        {
                            //If user requested to pause or cancel the count down timer

                            cancel();
                        }
                        else {
                            if(result[N-2].equals("\nREST")) {
                                Music_Ser.FadeOut();
                            }
                            if(result[N-2].equals("\nHARD") || result[N-2].equals("\nEASY")) {
                                Music_Ser.FadeIn();
                            }
                            tView.setText("" + (millisUntilFinished / 1000 )/60 +" : " +
                                    (millisUntilFinished / 1000 )%60);
                            prog.setProgress(
                                    ((int)(first/10)-(int)(millisUntilFinished/10))/(int)(first/1000)
                            );

                            //Put count down timer remaining time in a variable
                            timeRemaining = millisUntilFinished;
                            timepartRemaining = PreE -(first-timeRemaining);
                            countTxt.setText("" + (timepartRemaining / 1000 )/60 +" : " +
                                    (timepartRemaining / 1000 )%60);
                            prog2.setProgress(
                                    ((int)(count/10)-(int)(timepartRemaining/10))/(int)(count/1000)
                            );
                            if( ((int)timepartRemaining /1000)==5 )
                                myTTS.speak("파이브", TextToSpeech.QUEUE_FLUSH, null);
                            if( ((int)timepartRemaining /1000)==4 )
                                myTTS.speak("포호올", TextToSpeech.QUEUE_FLUSH, null);
                            if( ((int)timepartRemaining /1000)==3 )
                                myTTS.speak("쓰리이", TextToSpeech.QUEUE_FLUSH, null);
                            if( ((int)timepartRemaining /1000)==2 )
                                myTTS.speak("투우우", TextToSpeech.QUEUE_FLUSH, null);
                            if( ((int)timepartRemaining /1000)==1 )
                                myTTS.speak("워어언", TextToSpeech.QUEUE_FLUSH, null);


                        }
                    }
                    public void onFinish(){
                        unbindService(musicConnection);//바인드서비스를 해재해주고
                        stopService(new Intent(PlayTabata.this, MusicService.class));//서비스를 종료시켜준뒤
                        bindState = false;
                        isStart=false;
                        prog.setProgress(100);
                        prog2.setProgress(100);
                        //Do something when count down finished
                        tView.setText("Done");
                        countTxt.setText(String.valueOf("Finish ."));
                        myTTS.speak("고생하셨습니다. 당신은 멋쟁이 ", TextToSpeech.QUEUE_FLUSH, null);
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
                        bindState = false;
                        //When user request to cancel the CountDownTimer
                        isCanceled = true;
                        isStart=false;

                        //

                        prog.setProgress(100);
                        prog2.setProgress(100);

                        //Enable the start button
                        btnStart.setEnabled(true);
                        //Disable the pause, resume and cancel button
                        btnPause.setEnabled(false);
                        btnResume.setEnabled(false);
                        btnCancel.setEnabled(false);
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
                bindState = false;
                //When user request to cancel the CountDownTimer
                isCanceled = true;
                isStart=false;

                //

                prog.setProgress(100);
                prog2.setProgress(100);

                //Enable the start button
                btnStart.setEnabled(true);
                //Disable the pause, resume and cancel button
                btnPause.setEnabled(false);
                btnResume.setEnabled(false);
                btnCancel.setEnabled(false);
            }
        });

        spinmus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int sposition, long id) {

                if(firstSelect==true) {
                    position = sposition;
                    MusicName = files[position].getName().substring(0, files[sposition].getName().length() - 4);
                    if (sposition > musicdir.listFiles().length - 1)
                        sposition = 0;//만일 임시 순서가 리스트뷰의 범위를 벗어나는 경우에는 재조정이 필요

                    String filepath = filelist[sposition].getAbsolutePath();
                    Toast.makeText(PlayTabata.this, filepath, Toast.LENGTH_SHORT).show();

                    if(isStart==true) {
                        Music_Ser.nextSong(filepath, filelist[position].getName().substring(0, filelist[position].getName().length() - 4));
                    }
                    musicname.setText(filelist[sposition].getName().substring(0, filelist[sposition].getName().length() - 4));
                }

                if(isPaused==true ) {
                    Music_Ser.Pause();
                }
                firstSelect=true;
/*
                if (position > musicdir.listFiles().length - 1)
                    position = 0;//만일 임시 순서가 리스트뷰의 범위를 벗어나는 경우에는 재조정이 필요

                String filepath = filelist[position].getAbsolutePath();

                Music_Ser.nextSong(filepath, filelist[position].getName().substring(0, filelist[position].getName().length()-4));
                musicname.setText(filelist[position].getName().substring(0, filelist[position].getName().length()-4));
                */
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
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
        myTTS.shutdown();
        countDownTimer = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(bindState == true) {
            unbindService(musicConnection);//바인드서비스를 해재해주고
            stopService(new Intent(PlayTabata.this, MusicService.class));//서비스를 종료시켜준뒤
            isCanceled = true;

        }
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
        bindState = true;
    }

    public void onInit(int status) {
    }


}
