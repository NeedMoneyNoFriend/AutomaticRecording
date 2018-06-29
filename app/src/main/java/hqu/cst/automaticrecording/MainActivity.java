package hqu.cst.automaticrecording;

import android.app.Activity;
import java.io.File;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener{

    //sensor管理器
    private SensorManager sensorManager;
    //震动
    private Vibrator mVibrator;
    private TextView start;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private File file;

    private Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


    public void stop(View view){
        mediaRecorder.stop();
        start.setVisibility(View.GONE);
        mediaRecorder.release();//释放资源
        Toast.makeText(MainActivity.this, "录制完成", Toast.LENGTH_LONG).show();
    }

    public void play(View view){
        try {
            //这个是录音的存储位置和名字
            String path= Environment.getExternalStorageDirectory().
                    getAbsolutePath()+"/bb.amr";
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start=(TextView) findViewById(R.id.start);
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        mVibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
        mediaPlayer=new MediaPlayer();
        //通过MediaRecorder录制音频
        //1.创建
        mediaRecorder=new MediaRecorder();
        //2.调用MediaRecorder对象的方法来设置声音来源
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //3.设置录制的音频格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //4.设置编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //5.设置保存路径
        mediaRecorder.setOutputFile
                (Environment.getExternalStorageDirectory().
                        getAbsolutePath()+"/bb.amr");
        //6.进入准备录制的状态
        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    private final class PhoneListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING: //来电
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: //接通电话
                        mediaRecorder.setOutputFile
                                (Environment.getExternalStorageDirectory().
                                        getAbsolutePath() + "/cc.amr");
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        Toast.makeText(getApplicationContext(), "电话已接通，开始录音。", Toast.LENGTH_SHORT).show();
                        break;
                    case TelephonyManager.CALL_STATE_IDLE: //挂断电话
                        if (mediaRecorder != null) {
                            mediaRecorder.stop();
                            mediaRecorder.release();
                            mediaRecorder = null;
                            Toast.makeText(getApplicationContext(), "电话已挂断，录音停止。", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }




    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        //传感器的绑定
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        //传感器的解除绑定
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                if(Math.abs(event.values[0])>15||Math.abs(event.values[1])>15||Math.abs(event.values[2])>15){
                    mVibrator.vibrate(100);
                    System.out.println("111111111111111111111");
                    start.setText("开始录制中...");
                    System.out.println("222222222222222");
                    Toast.makeText(MainActivity.this, "开始录制", Toast.LENGTH_LONG).show();
                    mediaRecorder.start();
                }
                break;
            default:
                break;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }




}
