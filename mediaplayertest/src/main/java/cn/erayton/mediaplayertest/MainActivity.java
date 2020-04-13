package cn.erayton.mediaplayertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = MainActivity.class.getName() ;
    Button button, pasteButton;
    EditText editText ;
    MediaPlayer mediaPlayer ;
    TextView textView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView() ;
        initMediaPlayer();
    }

    private void initView() {
        button = findViewById(R.id.url_play_btn) ;
        button.setOnClickListener(this);
        pasteButton = findViewById(R.id.paste_btn) ;
        pasteButton.setOnClickListener(this);
        editText = findViewById(R.id.url_et) ;

        editText.setText("https://gitee.com/cxyzy1/audioPlayerDemo/raw/master/test.mp3");
        textView = findViewById(R.id.play_result_tv) ;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.url_play_btn:
                String url = editText.getText().toString() ;     //  判断是不是网址
//                Uri.parse(url) ;
                if (!isNetWorkMp3(url)){
                    setText("Is not netWork or Mp3", R.color.colorAccent);
                 return;
                }

                if (mediaPlayer == null){
                    Toast.makeText(this, "mediaPlayer init failer", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }

                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(url);
//                    mediaPlayer.getDuration() ; //  获取长度
//                    Log.d(TAG, "length:"+mediaPlayer.getDuration()) ;
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            setText("play start", R.color.colorPrimaryDark);
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            Toast.makeText(MainActivity.this, "play finish", Toast.LENGTH_SHORT).show();
                            setText("play finish", R.color.colorPrimaryDark);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.paste_btn:
                if (TextUtils.isEmpty(pasteString)){
                    setText("粘贴板为空", R.color.colorPrimary);
                    return;
                }
                editText.setText(pasteString);
                setText(pasteString, 0);
                break;
                default:
                    break;
        }
    }

    private void initMediaPlayer(){
        mediaPlayer = new MediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPasteString() ;
    }

    private boolean isNetWorkMp3(String str){
        String pattern = "[a-zA-z]+://[^\\s]*.mp3";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
//        System.out.println(m.matches());
        return m.matches() ;

    }
    String pasteString ;

    // 从黏贴板获取数据
    private void getPasteString()
    {
        // 获取并保存粘贴板里的内容
        try {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = clipboard.getPrimaryClip();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        CharSequence text = clipData.getItemAt(0).getText();
                        pasteString = text.toString();
                        Log.d(TAG, "getFromClipboard text=" + pasteString);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "getFromClipboard error");
            e.printStackTrace();
        }
    }

    private void setText(String msg, int color){
            textView.setTextColor(getResources().getColor(color ==0?R.color.colorBlack:color));
            textView.setText(msg);

    }
}
