package com.example.termproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String htmlPageUrl = "https://www.crossfit.com/";
    String totals="";
    TextView textviewHtmlDocument;

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
    boolean isPoseOpen = false;

    boolean isDelete=false;
    Animation translateLeftAnim;
    Animation translateRightAnim;
    Animation translateDownAnim;
    Animation translateUpAnim;

    LinearLayout slidingPage;
    LinearLayout slidingPose;

    TextView gotomovie;
    TextView gotofollomovie;
    TextView gotostopwatch;

    TextView gotoLeg;
    TextView gotoArm;

    Button slide_close;

    public static MainActivity Clone_Main;//메인 엑티비티를 실행할 때 사용될 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Clone_Main = MainActivity.this;//위에 선언한 메인 엑티비티 객체는 메인 엑티비티를 참조한다

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slidingPage = (LinearLayout)findViewById(R.id.sliding1);
        slidingPose = (LinearLayout)findViewById(R.id.poseslide);

        gotomovie = (TextView)findViewById(R.id.exervid);
        gotofollomovie = (TextView)findViewById(R.id.followmovie);
        gotostopwatch = (TextView)findViewById(R.id.stopwatch);

        gotoArm = (TextView)findViewById(R.id.arm);
        gotoLeg = (TextView)findViewById(R.id.leg);

        slide_close = (Button)findViewById(R.id.slideclose);

        gotoLeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Movie.class);
                intent.putExtra("Part_Name", "Leg");
                startActivity(intent);
                slidingPage.setVisibility(View.GONE);
                slidingPose.setVisibility(View.GONE);
                slidingPose.startAnimation(translateUpAnim);
                slidingPage.startAnimation(translateRightAnim);
            }
        });

        gotoArm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Movie.class);
                intent.putExtra("Part_Name", "Arm");
                startActivity(intent);
                slidingPage.setVisibility(View.GONE);
                slidingPose.setVisibility(View.GONE);
                slidingPose.startAnimation(translateUpAnim);
                slidingPage.startAnimation(translateRightAnim);
            }
        });

        slide_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingPage.setVisibility(View.GONE);
                slidingPose.setVisibility(View.GONE);
                slidingPose.startAnimation(translateUpAnim);
                slidingPage.startAnimation(translateRightAnim);
            }
        });

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        translateDownAnim = AnimationUtils.loadAnimation(this, R.anim.translate_down);
        translateUpAnim = AnimationUtils.loadAnimation(this, R.anim.translate_up);


        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        SlidingPoseAnimationListener poseListener = new SlidingPoseAnimationListener();
        translateDownAnim.setAnimationListener(poseListener);
        translateUpAnim.setAnimationListener(poseListener);


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
                if(isPoseOpen) {
                    slidingPose.startAnimation(translateDownAnim);
                }
                else {
                    slidingPose.setVisibility(View.VISIBLE);
                    slidingPose.startAnimation(translateUpAnim);
                }
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

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Document doc1 = null;
            try {
                doc1 = Jsoup.connect(htmlPageUrl).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements type1 = doc1.select("[class=\"body\"] [class=\"content\"] p");

            try {
                String text =type1.first().html();
                String[] textSplitResult = text.split("<br>");
                if (null != textSplitResult) {
                    for (String t : textSplitResult) {
                        System.out.println(t);
                        totals= totals+ t+"\n";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textviewHtmlDocument = (TextView)findViewById(R.id.today);
            textviewHtmlDocument.setText(totals);
        }
    }



    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(isDelete==true){

                int count = FILELIST.length;
                if(count != 0) {
                    String item = FILELIST[position+1].getName();
                    Toast.makeText(getApplicationContext(), FILELIST[position+1].getName()+" 삭제", Toast.LENGTH_SHORT).show();
                    deleteFile(item);
                }
                isDelete=false;

                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                Clone_Main.finish();

            }
            else {
                final String musicna = m_Adapter.getItem(position);//재정의가 불가능하도록 final로 선언
                String content = "";
                Intent intent = new Intent(MainActivity.this, PlayTabata.class);
                intent.putExtra("Cose_Name", musicna);
                startActivity(intent);
            }
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        //액션바를 추가하기 위해서 만들어준다
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);//다시 돌아갑니다.
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {//액션바의 목록을 선택하게 되면
            case R.id.action_Write://id가 action_Write인 버튼을 누르게 된다면
                Intent intent = new Intent(MainActivity.this, CreateMenu.class);
                startActivity(intent);
                return true;
            case R.id.action_Delete:
                isDelete=true;
                Toast.makeText(getApplicationContext(), "지울 목록을 선택하세요", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_Slide:
                if(isPageOpen) {
                    slidingPage.startAnimation(translateRightAnim);
                }
                else {
                    slidingPage.setVisibility(View.VISIBLE);
                    slidingPage.startAnimation(translateLeftAnim);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);//다시 돌아갑니다.

        }
    }

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
    private class SlidingPoseAnimationListener implements Animation.AnimationListener {

        public void onAnimationEnd(Animation animation) {
            if(isPoseOpen) {
                slidingPose.setVisibility(View.GONE);
                isPoseOpen = false;
            }
            else {
                isPoseOpen = true;
            }
        }
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
    @Override
    public void onStop() {
        super.onStop();

    }

}
