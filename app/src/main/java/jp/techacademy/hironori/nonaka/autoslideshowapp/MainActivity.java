package jp.techacademy.hironori.nonaka.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //わからない
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    //タイマーが動いているかいないかを判断
    Timer mTimer;
    //スレッドで使う
    Handler mHandler = new Handler();
    //ボタンフィールド
    Button mBackButton;
    Button mPauseButton;
    Button mGoButton;
    // 画像の情報を取得するためのフィールド(以下2行)
    ContentResolver resolver;
    Cursor cursor;
    //パーミッションチェック
    int check = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //パーミッションの許可を問う
        allow();

        mBackButton = (Button) findViewById(R.id.back_button);
        mPauseButton = (Button) findViewById(R.id.pause_button);
        mGoButton = (Button) findViewById(R.id.go_button);

        //再生・停止ボタンの処理
        mPauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (mTimer == null) {


                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //ここに写真を次へ送る記述をする
                            Log.d("ANDROID", "再生/停止ボタンで再生しました．(2秒ごとに)");

                            //スレッドの部分
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    getContentsNextInfo();
                                }
                            });
                        }
                    }, 2000, 2000);//最初に始動されまで2000ミリ秒，のちに2000ミリ秒間隔,run()を呼び出す
                } else if (mTimer != null) {

                    mTimer.cancel();
                    mTimer = null;
                    Log.d("ANDROID", "再生/停止ボタンで停止しました．");
                }


            }
        });


        //戻るボタンの処理
        mBackButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                //自動スライドショーが動いてる時にはこのボタンは無効
                if (mTimer == null) {
                    getContentsBackInfo();
                }

            }
        });


        //進むボタンの処理
        mGoButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {


                //自動スライドショーが動いてる時にはこのボタンは無効
                if (mTimer == null) {

                    getContentsNextInfo();
                }

            }
        });
    }


    //パーミションの許可を表示するメソッド
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                    Log.d("ANDROID", "許可された勝手に");
                }
                else {
                    Log.d("ANDROID", "許可されなかった勝手に");
                    finish();
                }
                break;
            default:
                break;
        }
    }


    //エミュレータ内の一番はじめの画像を取得するメソッド
    private void getContentsInfo() {

        Log.d("ANDROID", "getContentsInfoの実行");

        // 画像の情報を取得する
        resolver = getContentResolver();
        //
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

    }

    //エミュレータ内の次画像を取得するメソッド
    private void getContentsNextInfo() {


        if (cursor.moveToNext()) {
            Log.d("ANDROID", "次へ移動");

            // indexからIDを取得し、そのIDから画像のURIを取得する
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            Log.d("ANDROID", "URI : " + imageUri.toString());

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
        } else if (cursor.moveToNext() == false) {
            cursor.moveToFirst();
            // indexからIDを取得し、そのIDから画像のURIを取得する
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            Log.d("ANDROID", "URI : " + imageUri.toString());

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
        }
    }

    //エミュレータ内の前の画像を取得するメソッド
    private void getContentsBackInfo() {

        Log.d("ANDROID", "前の画像に戻る");

        if (cursor.moveToPrevious()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            Log.d("ANDROID", "URI : " + imageUri.toString());

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
        } else if (cursor.moveToPrevious() == false) {
            cursor.moveToLast();
            // indexからIDを取得し、そのIDから画像のURIを取得する
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            Log.d("ANDROID", "URI : " + imageUri.toString());

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //パーミッションの許可を問うメソッド
    protected void allow(){
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
                check++;
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
            check++;
        }
    }

    protected void permissionCheck(){
        if(PackageManager.PERMISSION_GRANTED == 0){
            check++;

        }
        else if(check==0){
            finish();
        }
    }




}
