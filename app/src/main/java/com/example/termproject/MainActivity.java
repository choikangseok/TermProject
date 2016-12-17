package com.example.termproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btn, btn1;
    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private ListView m_ListView;//리스트 뷰의 객체를 생성
    private ArrayAdapter<String> m_Adapter;//스트링 형태의 어레이어댑터 객체를 생성
    ArrayList<File> Memo_File = new ArrayList<File>();//File형태의 어레이리스트 생성
    //저장 경로에 있는 파일들 중 txt파일들이 저장될 리스트임
    ArrayList<String> values = new ArrayList<>();//리스트 뷰에서 보이게 될 항목
    File savedfile = new File("/data/data/com.example.termproject/files/");
    //저장경로의 파일들을 File객체를 생성해 저장
    final File[] FILELIST = savedfile.listFiles();//위의 저장된 항목들의 리스트를 File배열을

    boolean isPageOpen = false;
    Animation translateLeftAnim;
    Animation translateRightAnim;

    LinearLayout slidingPage;
    Button sliding;
    TextView gotomovie;
    TextView gotofollomovie;
    TextView gotostopwatch;

    public static MainActivity Clone_Main;//메인 엑티비티를 실행할 때 사용될 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Clone_Main = MainActivity.this;//위에 선언한 메인 엑티비티 객체는 메인 엑티비티를 참조한다

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sliding = (Button)findViewById(R.id.slide);
        slidingPage = (LinearLayout)findViewById(R.id.sliding1);
        gotomovie = (TextView)findViewById(R.id.exervid);
        gotofollomovie = (TextView)findViewById(R.id.followmovie);
        gotostopwatch = (TextView)findViewById(R.id.stopwatch);

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        sliding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPageOpen) {
                    slidingPage.startAnimation(translateRightAnim);
                }
                else {
                    slidingPage.setVisibility(View.VISIBLE);
                    slidingPage.startAnimation(translateLeftAnim);
                }
            }
        });

        gotostopwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StopWatch.class);
                startActivity(intent);
                slidingPage.setVisibility(View.GONE);
                slidingPage.startAnimation(translateRightAnim);
            }
        });

        gotofollomovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Exer_follow.class);
                startActivity(intent);
                slidingPage.setVisibility(View.GONE);
                slidingPage.startAnimation(translateRightAnim);
            }
        });

        gotomovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Movie.class);
                startActivity(intent);
                slidingPage.setVisibility(View.GONE);
                slidingPage.startAnimation(translateRightAnim);
            }
        });

        //*******************************************************************
        // 권한 체크
        //*******************************************************************
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
        //********************************************************************

        btn = (Button)findViewById(R.id.btn);
        btn1 = (Button)findViewById(R.id.btn2);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateMenu.class);
                startActivity(intent);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = FILELIST.length;
                if(count != 0) {
                    String item = FILELIST[count-1].getName();
                    deleteFile(item);
                }
            }
        });

        m_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        //String 문자열 하나를 출력하는 layout으로 어댑터를 생성한다
        m_ListView = (ListView) findViewById(R.id.list_cose);
        //리스트 뷰 객체에 xml에 생성한 리스트 뷰의 id를 연결
        m_ListView.setAdapter(m_Adapter);
        //리스트 뷰 객체의 어댑터를 설정한다
        m_ListView.setOnItemClickListener(onClickListItem);
        //리스트 뷰의 각 리스트들을 눌렀을 때의 동작 리스터를 추가시켜준다.

        //FILELIST 배열에 저장된 파일들의 갯수(길이)만큼 반복문을 반복시켜준다.
        for(int i = 0; i < FILELIST.length; i++ ) {
            if(FILELIST[i].getName().endsWith(".txt"))//FILELIST의 i번째 원소의 파일 이름의 마지막이
                //,txt로 끝나는 경우
                Memo_File.add(FILELIST[i]);//Memo_File리스트에 추가시켜준다.
        }
        for(File file : Memo_File) {
            String f_name;//파일의 이름이 저장될 String형 변수를 선언 후
            f_name = file.getName().substring(0, file.getName().length()-4) ;
            //확인중인 파일의 .txt를 제외한 이름을 저장시킨 뒤
            m_Adapter.add(f_name);//어댑터 객체를 사용해 리스트 뷰에 추가시켜준다
        }
    }



    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final String musicna= m_Adapter.getItem(position);//재정의가 불가능하도록 final로 선언
            String content = "";
            Intent intent =  new Intent(MainActivity.this, PlayTabata.class);
            intent.putExtra("Cose_Name", musicna);
            startActivity(intent);

        }
    };

    private class SlidingPageAnimationListener implements Animation.AnimationListener {

        public void onAnimationEnd(Animation animation) {
            if(isPageOpen) {
                slidingPage.setVisibility(View.GONE);
                isPageOpen = false;
            }
            else {
                isPageOpen = true;
            }
        }
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
