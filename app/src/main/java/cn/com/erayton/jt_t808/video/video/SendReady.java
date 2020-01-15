package cn.com.erayton.jt_t808.video.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.library.live.vd.VDEncoder;

import cn.com.erayton.jt_t808.R;
import cn.com.erayton.usagreement.utils.LogUtils;

public class SendReady extends AppCompatActivity {
    private String hostMsg = null ;
    private int portMsg = 0 ;
    private EditText url;
    private EditText port;
    private EditText framerate;
    private EditText publishbitrate;
    private EditText collectionbitrate;
    private EditText pu_width;
    private EditText pu_height;
    private EditText pr_width;
    private EditText pr_height;
    private EditText collectionbitrate_vc;
    private EditText publishbitrate_vc;
    private RadioGroup videoCode;
    private RadioGroup preview;
    private RadioGroup rotate;
    private Button begin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_ready);
        getData() ;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        url = findViewById(R.id.url);
        port = findViewById(R.id.port);
        framerate = findViewById(R.id.framerate);
        publishbitrate = findViewById(R.id.publishbitrate);
        collectionbitrate = findViewById(R.id.collectionbitrate);
        pr_width = findViewById(R.id.pr_width);
        pr_height = findViewById(R.id.pr_height);
        collectionbitrate_vc = findViewById(R.id.collectionbitrate_vc);
        publishbitrate_vc = findViewById(R.id.publishbitrate_vc);
        pu_width = findViewById(R.id.pu_width);
        pu_height = findViewById(R.id.pu_height);
        videoCode = findViewById(R.id.svideoCode);
        preview = findViewById(R.id.preview);
        rotate = findViewById(R.id.rotate);
        begin = findViewById(R.id.begin);

        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.e("onClick ------------------------");
                start();
            }
        });
    }

    private void getData() {
        LogUtils.e("getData ------------------------");
        Bundle bundle = getIntent().getExtras() ;
        if (bundle != null && bundle.size() != 0){
            hostMsg= bundle.getString("ip") ;
            portMsg = bundle.getInt("port") ;
            LogUtils.e("ip:"+hostMsg+",port:"+portMsg);
            Toast.makeText(this, "ip:"+hostMsg+",port:"+portMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void start() {

        begin.setEnabled(false);
        if (!judgeTheConnect(url.getText().toString())){
            begin.setEnabled(true);
            return;
        }

        Intent intent = new Intent(this, Send.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.getText().toString());
        bundle.putInt("port", Integer.parseInt(port.getText().toString()));
        bundle.putInt("framerate", Integer.parseInt(framerate.getText().toString()));
        bundle.putInt("publishbitrate", Integer.parseInt(publishbitrate.getText().toString()) * 1024);
        bundle.putInt("collectionbitrate", Integer.parseInt(collectionbitrate.getText().toString()) * 1024);
        bundle.putInt("collectionbitrate_vc", Integer.parseInt(collectionbitrate_vc.getText().toString()) * 1024);
        bundle.putInt("publishbitrate_vc", Integer.parseInt(publishbitrate_vc.getText().toString()) * 1024);
        bundle.putInt("pu_width", Integer.parseInt(pu_width.getText().toString()));
        bundle.putInt("pu_height", Integer.parseInt(pu_height.getText().toString()));
        bundle.putInt("pr_width", Integer.parseInt(pr_width.getText().toString()));
        bundle.putInt("pr_height", Integer.parseInt(pr_height.getText().toString()));

        if (videoCode.getCheckedRadioButtonId() == R.id.sh264) {
            bundle.putString("videoCode", VDEncoder.H264);
        } else {
            bundle.putString("videoCode", VDEncoder.H265);
        }
        if (preview.getCheckedRadioButtonId() == R.id.haspreview) {
            bundle.putBoolean("ispreview", true);
        } else {
            bundle.putBoolean("ispreview", false);
        }
        if (rotate.getCheckedRadioButtonId() == R.id.front) {
            bundle.putBoolean("rotate", true);
        } else {
            bundle.putBoolean("rotate", false);
        }

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        begin.setEnabled(true);
    }

    //
//    /**
//     * =========通过ip ping 来判断ip是否通
//     *
//     * @param ip
//     */
//    private void judgeTheConnect(String ip) {
//
//        try {
//
//            if (ip != null) {
//
//                //代表ping 3 次 超时时间为10秒
//                Process p = Runtime.getRuntime().exec("ping -c 3 -w 10 " + ip); //  ping3次
//
//                int status = p.waitFor();
//
//                if (status == 0) {
//                    //代表成功
//                    Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    //代表失败
//                    Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                //代表失败
//                Toast.makeText(this, "IP为空", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            LogUtils.e(e.getMessage());
//        }
//
//    }
    /**
     * =========通过ip ping 来判断ip是否通
     *
     * @param ip
     */
    private boolean judgeTheConnect(String ip) {

        try {

            if (ip != null) {

                //代表ping 3 次 超时时间为10秒
                Process p = Runtime.getRuntime().exec("ping -c 3 -w 10 " + ip); //  ping3次

                int status = p.waitFor();

                if (status == 0) {
                    //代表成功
                    Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
                    return true ;

                } else {
                    //代表失败
                    Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
                    return false ;
                }
            } else {
                //代表失败
                Toast.makeText(this, "IP为空", Toast.LENGTH_SHORT).show();
                return false ;
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
        return false ;
    }
}
