package cn.syc.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.Notification.Action;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import cn.syc.R;
import cn.syc.R.id;
import cn.syc.R.layout;
import cn.syc.R.menu;
import cn.syc.listener.OnSelectedListener;
import cn.syc.view.MyClockView;



public class MainActivity extends Activity {
	private MyClockView mMyclockView=null;
	private TextView tx=null;	
	String TAG="MainActivity";
	private static String remoteServerIp="10.32.0.66";
	private static String remoteServerPort="50000";
	private MyTask mTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		mMyclockView = (MyClockView)findViewById(R.id.clock);
		tx = (TextView)findViewById(R.id.tx);
		mMyclockView.setOnSelectedListener(new OnSelectedListener() {
			
			@Override
			public void OnSelected(int v,String door){
				tx.setText("正在打开"+door);
				Log.i(TAG, "OnSelectedCallBackListener.OnSelected is running"+v);
				String dnum="";
				if (v ==1) {				
					dnum="d1";          
				} else if (v == 2) {
					dnum="d2";
				} else if (v == 3) {
					dnum="d3"; 
				} else if (v == 4) {
					dnum="d4";  				
				}
				Log.i(TAG, dnum);
				//此次必须检测mtask是否已经执行完毕，确认没有运行的任务，才可以重新发起任务		
				if(mTask!=null && mTask.getStatus()!=AsyncTask.Status.FINISHED){
					Toast.makeText(MainActivity.this, "系统检测到已经有个开门请求在执行中，请稍等", Toast.LENGTH_LONG).show();
				}else{			
					mTask = new MyTask();	
					mTask.execute(remoteServerIp,remoteServerPort,dnum);
				}
				
			}
			
			@Override
			public void OnfingerDown(float x, float y) {
				// TODO Auto-generated method stub
				//tx.setText("x："+x+" y:"+y);
				Log.i(TAG, "OnSelectedCallBackListener.OnfingerDown is running");
			}
			
		});
	
	}

	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();		
		if (id == R.id.action_settings) {
			Toast.makeText(MainActivity.this, "设置", Toast.LENGTH_LONG).show();
			return true;
		}
		if (id == R.id.action_selectpic) {
			Intent it=new Intent();
			it.setClass( MainActivity.this,ListGridPicActivity.class);
			startActivity(it);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED)
            mTask.cancel(true);
	}



	class MyTask extends AsyncTask<String, Integer, String>{	
		private static final String TAG="MyTask";
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.i(TAG, "onPreExecute called");    
			tx.setText("服务器通信中");
		}	
		

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.i(TAG, "onPostExecute called");	
			if (result.indexOf("ok")!=-1){
				tx.setText("开门成功");
				Toast.makeText(MainActivity.this, "开门成功", Toast.LENGTH_LONG).show();
			}else{	
				tx.setText("开门失败");
				Toast.makeText(MainActivity.this, "开门失败，请联系管理员手动开门！", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.i(TAG, "doInBackground called");
			String ip=params[0];
			int port =Integer.parseInt(params[1]);
			String result="";		
			
			try {
				publishProgress(10);
				// 创建Socket连接对象 （ip地址，端口）
				Socket socket = new Socket(ip,port);
				socket.setSoTimeout(20000);
				// PrintWriter发送对象				
				publishProgress(30);				
				PrintWriter printWriter = new PrintWriter(new
				OutputStreamWriter(socket.getOutputStream()), true);
				// 向服务器发送信息
				Log.i(TAG, "param :"+params[2]);
				printWriter.print(params[2]);
				printWriter.flush();
				publishProgress(60);
				// Bufferedreader 接收服务器的数据对象
				BufferedReader bufferedReader = new BufferedReader(new
				InputStreamReader(socket.getInputStream()));
				// 接受服务器端数据
				result = bufferedReader.readLine();	
				publishProgress(90);
				// 关闭服务之间的连接
				printWriter.close();
				bufferedReader.close();
				socket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.i(TAG, "result :"+result);
			publishProgress(100);
			return result;
		}	

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			Log.i(TAG, "onProgressUpdate called");
			int progressValue=values[0];
			tx.setText("进度："+progressValue+"%");
			mMyclockView.setPoint(progressValue);
		}
			
		
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
		}
	}

	
}


