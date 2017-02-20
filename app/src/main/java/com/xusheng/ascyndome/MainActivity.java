package com.xusheng.ascyndome;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.main_tv)
    ImageView show;
    @InjectView(R.id.main_btn)
    Button download;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }
    @OnClick(R.id.main_btn)
    public void onClick(View v)  {
        switch(v.getId()){
            case R.id.main_btn:
                DownloadTask task = new DownloadTask(this);
                try {
                    task.execute(new URL("http://pic.58pic.com/58pic/13/72/07/55Z58PICKka_1024.jpg"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    class DownloadTask extends AsyncTask<URL,Integer,Bitmap>{
        ProgressDialog prodialog;
        int hasRead = 0;
        Context context;
        Bitmap bitmap;
        public DownloadTask(Context context) {
            this.context = context;
        }
        @Override
        protected Bitmap doInBackground(URL... params) {
            StringBuffer sb=null;
            HttpURLConnection url = null;
            BufferedInputStream bis =null;
            int total = 0;
                try {
                    url = (HttpURLConnection) params[0].openConnection();
                    url.setReadTimeout(8000);
                    url.setConnectTimeout(8000);
                    bitmap = BitmapFactory.decodeStream(url.getInputStream());
                    if(url.getResponseCode()==200){
                        bis = new BufferedInputStream(url.getInputStream());
                        byte[] data = new byte[1024];
                        int b = 0;
                      while((b=bis.read(data))!=-1){
                          total +=b;
                          publishProgress((total*100)/(bitmap.getByteCount()));
                      }

                        return bitmap;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    if(url!=null){
                        url.disconnect();
                    }
                    if(bis!=null){
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            show.setImageBitmap(s);
            prodialog.dismiss();
        }
        @Override
        protected void onPreExecute() {
            prodialog = new ProgressDialog(context);
            prodialog.setMax(100);
            prodialog.setTitle("任务正在执行");
            prodialog.setMessage("任务正在执行中，请稍后");
            prodialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            prodialog.setCancelable(false);
            prodialog.setIndeterminate(true);
            prodialog.show();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            prodialog.setProgress(values[0]);
        }
    }
}
