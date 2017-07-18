package cn.syc.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import cn.syc.R;
import cn.syc.adapter.MyGridViewAdapter;
import cn.syc.bean.PictureLog;
import cn.syc.bean.Result;
import cn.syc.view.LoadingDialog;
import net.HttpUtils;
import net.UploadFile;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ListGridPicActivity extends Activity {

	private long lastTime = 0; // 记录上次点击的时间
	private PullToRefreshGridView mPullRefreshListView;
	private MyGridViewAdapter mMyGridViewAdapter;
	private ArrayList<PictureLog> mPictures = null;
	// private GridView myGridView;
	private Context ctx;
	private static final int RESULT_LOAD_IMAGE = 0;
	private static final int RESULT_CAMERA_IMAGE = 1;
	public Dialog mLoadingDialog;
	private String photoPath = null;
	private ImageLoader mImageLoader;

	private MyTask mTask;
	private int currentPicCount;
	private int p = 1;
	private int pageCount = 12;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_grid_pic_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_gallery) {
			// myGridView.setVisibility(View.GONE);
			showPopueWindow();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private ArrayList<PictureLog> getData(int p) {
		ArrayList<PictureLog> mPictures = new ArrayList<PictureLog>();
		for (int i = 0; i <= this.pageCount - 1; i++) {
			String url = "http://10.32.0.66/app/upload/";
			// Log.e("TAG", "url:"+ url + "p" + ((p-1)*this.pageCount+i) +
			// ".png" );
			PictureLog tempPicture = new PictureLog("照片" + ((p - 1) * this.pageCount + i),
					url + "p" + ((p - 1) * this.pageCount + i) + ".png");
			mPictures.add(tempPicture);
		}
		return mPictures;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.ctx = getBaseContext();
		setContentView(R.layout.activity_list_grid_pic);
		mLoadingDialog = LoadingDialog.createLoadingDialog(ListGridPicActivity.this, "加载中...");
		mLoadingDialog.setCanceledOnTouchOutside(false);
		mPullRefreshListView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
		this.mImageLoader = ImageLoader.getInstance();

		this.mPictures = getData(p);
		this.mMyGridViewAdapter = new MyGridViewAdapter(ctx, mPictures);
		mPullRefreshListView.setAdapter(mMyGridViewAdapter);
		mMyGridViewAdapter.notifyDataSetChanged();
		mPullRefreshListView.onRefreshComplete();
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				// Toast.makeText(ListGridPicActivity.this, "Pull Down!",
				// Toast.LENGTH_SHORT).show();
				ListGridPicActivity.this.p = ListGridPicActivity.this.p - 1;
				if (ListGridPicActivity.this.p == 0) {
					ListGridPicActivity.this.p = 1;
				}
				// Log.e("TAG", "onPullDownToRefresh:"+
				// ListGridPicActivity.this.p);
				new GetDataTask().execute(ListGridPicActivity.this.p);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				// Toast.makeText(ListGridPicActivity.this, "Pull Up!",
				// Toast.LENGTH_SHORT).show();
				ListGridPicActivity.this.p = ListGridPicActivity.this.p + 1;
				if (ListGridPicActivity.this.p > 15) {
					ListGridPicActivity.this.p = 15;
				}
				// Log.e("TAG", "onPullDownToRefresh:"+
				// ListGridPicActivity.this.p);
				new GetDataTask().execute(ListGridPicActivity.this.p);
			}

		});
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				PictureLog tPicture = (PictureLog) parent.getItemAtPosition(position);
				String url = tPicture.getUrl();
				showSelectPicWindow(url);

				// Toast.makeText(ctx, url, Toast.LENGTH_SHORT).show();
				DownloadImageTask mTask = new DownloadImageTask();
				mTask.execute(url);

			}
		});
		/*
		 * MyGridViewAdapter mMyGridViewAdapter = new MyGridViewAdapter(ctx,
		 * mPictures); myGridView.setAdapter(mMyGridViewAdapter);
		 * myGridView.setOnScrollListener(new
		 * PauseOnScrollListener(mImageLoader, true, true));
		 * myGridView.setOnItemClickListener(new OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> parent, View view,
		 * int position, long id) { // TODO Auto-generated method stub
		 * PictureLog tPicture = (PictureLog)
		 * parent.getItemAtPosition(position); String url = tPicture.getUrl();
		 * showSelectPicWindow(url);
		 * 
		 * //Toast.makeText(ctx, url, Toast.LENGTH_SHORT).show();
		 * DownloadImageTask mTask = new DownloadImageTask();
		 * mTask.execute(url);
		 * 
		 * } });
		 */

	}

	// 下拉刷新加载数据
	private class GetDataTask extends AsyncTask<Integer, Integer, ArrayList<PictureLog>> {

		@Override
		protected void onPostExecute(ArrayList<PictureLog> result) {

			ListGridPicActivity.this.mMyGridViewAdapter = new MyGridViewAdapter(ctx, result);
			mPullRefreshListView.setAdapter(mMyGridViewAdapter);
			mMyGridViewAdapter.notifyDataSetChanged();
			mPullRefreshListView.onRefreshComplete();

		}

		@Override
		protected ArrayList<PictureLog> doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			ArrayList<PictureLog> mArrayList = ListGridPicActivity.this.getData(params[0]);

			return mArrayList;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String picturePath = null;
		if (resultCode == RESULT_OK) {
			Uri uriImage = null;
			if (requestCode == RESULT_LOAD_IMAGE && null != data) {
				uriImage = data.getData();
				Log.i("uriImage", "data:" + uriImage);
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(uriImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				picturePath = cursor.getString(columnIndex);
				cursor.close();
				Log.i("LOAD_IMAGE", "uriImage:" + uriImage + " picturePath:" + picturePath);
			} else if (requestCode == RESULT_CAMERA_IMAGE) {
				// uriImage = data.getData();
				picturePath = photoPath;
				Log.i("CAMERA", "uriImage:" + uriImage + " picturePath:" + picturePath);
			}

			this.mTask = new MyTask();
			mTask.execute("http://10.32.0.66/app/savetofile.php", picturePath);
		}
	}

	private void selPIc(int num) {
		Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, num);
	}

	private void takeCamera(int num) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			photoFile = createImageFile();
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
			}
		}

		startActivityForResult(takePictureIntent, num);// 跳转界面传回拍照所得数据
	}

	private File createImageFile() {
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File image = null;
		try {
			image = File.createTempFile(generateFileName(), ".jpg", storageDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		photoPath = image.getAbsolutePath();
		return image;
	}

	public static String generateFileName() {
		// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
		// Date());
		// String imageFileName = "JPEG_" + timeStamp + "_";
		String imageFileName = "TEMP";
		return imageFileName;
	}

	private void showSelectPicWindow(String url) {
		View popView = View.inflate(this, R.layout.popupwindow_pic, null);
		ImageView mImageView = (ImageView) popView.findViewById(R.id.selpic);
		final TextView mTextView = (TextView) popView.findViewById(R.id.progressText);
		final ProgressBar mBar = (ProgressBar) popView.findViewById(R.id.progress_horizontal);
		// 获取屏幕宽高
		int weight = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;

		final PopupWindow popupWindow = new PopupWindow(popView, weight, height);
		// popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.setFocusable(true);
		// 点击外部popueWindow消失
		popupWindow.setOutsideTouchable(true);

		// mImageView.setImageBitmap(bm);

		ListGridPicActivity.this.mImageLoader.displayImage(url, mImageView, MyGridViewAdapter.getImageLoaderOptions(),
				new SimpleImageLoadingListener(), new ImageLoadingProgressListener() {

					@Override
					public void onProgressUpdate(String arg0, View arg1, int arg2, int arg3) {
						// TODO Auto-generated method stub
						Log.i("mImageLoader-onProgressUpdate", "current:" + arg2 + "total:" + arg3);
						mBar.setProgress(100 * arg2 / arg3);
						mTextView.setText(100 * arg2 / arg3 + "%");
					}
				});
		// popupWindow消失屏幕变为不透明
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// popupWindow出现屏幕变为半透明
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
		popupWindow.showAtLocation(popView, Gravity.TOP, 0, 0);

	}

	private void showPopueWindow() {
		View popView = View.inflate(this, R.layout.popupwindow_camera_need, null);
		Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
		Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
		Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);
		// 获取屏幕宽高
		int weight = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels - 20;

		final PopupWindow popupWindow = new PopupWindow(popView, weight, height);
		// popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.setFocusable(true);
		// 点击外部popueWindow消失
		popupWindow.setOutsideTouchable(true);

		bt_album.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selPIc(RESULT_LOAD_IMAGE);
				popupWindow.dismiss();

			}
		});
		bt_camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takeCamera(RESULT_CAMERA_IMAGE);
				popupWindow.dismiss();

			}
		});
		bt_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();

			}
		});
		// popupWindow消失屏幕变为不透明
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// popupWindow出现屏幕变为半透明
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.7f;
		getWindow().setAttributes(lp);
		popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 50);

	}

	class MyTask extends AsyncTask<String, Integer, String> {
		private static final String TAG = "MyTask";

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.i(TAG, "onPreExecute called");
			mLoadingDialog.show();

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.i(TAG, "onPostExecute called" + result);
			/*
			 * if (result.indexOf("success") != -1) {
			 * Toast.makeText(ListGridPicActivity.this, "上传成功",
			 * Toast.LENGTH_LONG).show(); } else {
			 * Toast.makeText(ListGridPicActivity.this, "上传失败",
			 * Toast.LENGTH_LONG).show(); }
			 */
			Gson mGson = new Gson();
			Result mResult = mGson.fromJson(result, Result.class);			
			String msg=mResult.getResultStr();
			int newFileCount=mResult.getFileCount();
			if ( "success".equals(msg ) ){
				Toast.makeText(ListGridPicActivity.this, "上传成功", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(ListGridPicActivity.this, "上传失败", Toast.LENGTH_LONG).show();
			}

			mLoadingDialog.dismiss();
			// myGridView.setVisibility(View.VISIBLE);
			if (photoPath != null) {
				File tempFile = new File(photoPath);
				if (tempFile.isFile() && tempFile.exists()) {
					// 如果拍照生产的临时图片存在，在上传完成时删除临时文件
					tempFile.delete();
				}
			}
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.i(TAG, "doInBackground called");
			UploadFile mUploadFile = new UploadFile(params[0], params[1]);
			String result = mUploadFile.submit();
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			Log.i(TAG, "onProgressUpdate called");
			// int progressValue=values[0];
			// mMyclockView.setPoint(progressValue);
		}

		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			mLoadingDialog.dismiss();
			// myGridView.setVisibility(View.VISIBLE);
		}
	}

	class DownloadImageTask extends AsyncTask<String, Integer, Boolean> {
		private static final String TAG = "DownloadImageTask";
		private Bitmap pic = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.i(TAG, "onPreExecute called");
			// mLoadingDialog.show();
			// myGridView.setVisibility(View.GONE);

		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub

			if (result) {
				// ListGridPicActivity.this.bm=this.pic;
				// showSelectPicWindow(bm);
				// myGridView.setVisibility(View.VISIBLE);
				// mLoadingDialog.dismiss();

			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.i(TAG, "doInBackground called");
			Bitmap img = HttpUtils.getNetWorkBitmap(params[0]);
			int imgW = img.getWidth();
			int imgH = img.getHeight();
			this.pic = Bitmap.createScaledBitmap(img, imgW / 4, imgH / 4, false);

			System.out.println(pic.getByteCount());
			/*
			 * if(pic!=null){ return true; }
			 */
			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			Log.i(TAG, "onProgressUpdate called");
			// int progressValue=values[0];
			// mMyclockView.setPoint(progressValue);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (System.currentTimeMillis() - lastTime > 2000) {
				Toast.makeText(ctx, "再按一次退出程序", Toast.LENGTH_LONG).show();
				lastTime = System.currentTimeMillis();
			} else {
				if (this.mTask != null && this.mTask.getStatus() == AsyncTask.Status.RUNNING) {
					this.mTask.cancel(true);
				}
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getSDFiles() {
		// uri:MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		// MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
		// MediaStore.Files.getContentUri("external")
		Uri uri = Uri.parse("content://browser/markbook");
		String[] filePathColumn = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.MIME_TYPE };
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		String[] colnames = cursor.getColumnNames();
		for (int i = 0; i < colnames.length - 1; i++) {
			System.out.println(i + ": " + colnames[i]);
		}
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				System.out.println(
						"id:" + cursor.getInt(0) + "  path:" + cursor.getString(1) + "  mime: " + cursor.getString(12));

			}
		} else {
			System.out.println(cursor.getCount());
		}
	}
}
