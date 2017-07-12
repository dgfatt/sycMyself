package cn.syc.adapter;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.syc.R;
import cn.syc.bean.PictureLog;


public class MyGridViewAdapter extends BaseAdapter {
	private LayoutInflater mLayoutInflater;
	private ArrayList<PictureLog> mPictures=new ArrayList<PictureLog>();
	private Context ctx;
	private RequestQueue mQueue; // volley请求

	
	public MyGridViewAdapter(Context ctx, ArrayList<PictureLog> mList) {
		// TODO Auto-generated constructor stub
		super();
		this.ctx = ctx;
		this.mPictures = mList;
		this.mLayoutInflater = LayoutInflater.from(this.ctx);
		this.mQueue = Volley.newRequestQueue(this.ctx);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (null != mPictures) {
			return mPictures.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mPictures.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder mViewHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.grid_item, null);
			mViewHolder = new ViewHolder();
			mViewHolder.title = (TextView) convertView.findViewById(R.id.title);
			mViewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		mViewHolder.title.setText(mPictures.get(position).getTitle());
		loadImageByImageLoader(mViewHolder.image, mPictures.get(position).getUrl());
		return convertView;
	}

	/**
	 * 使用volley的ImageLoader加载图片（自带缓存）
	 * 
	 * @param imageview
	 * @param imgUrl
	 */
	private void loadImageByImageLoader(ImageView image, String imageUrl) {

		// 创建ImageLoader对象，参数（加入请求队列，自定义缓存机制）
		ImageLoader imageLoader = new ImageLoader(this.mQueue, new BitmapCache());

		// 获取图片监听器 参数（要显示的ImageView控件，默认显示的图片，加载错误显示的图片）
		ImageLoader.ImageListener listener = ImageLoader.getImageListener(image, android.R.drawable.gallery_thumb,
				android.R.drawable.ic_dialog_alert);

		imageLoader.get(imageUrl, listener, 100, 120);
	}
}

class BitmapCache implements ImageCache {
	private LruCache<String, Bitmap> mCache;

	public BitmapCache() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}

		};
	}

	@Override
	public Bitmap getBitmap(String url) {
		return mCache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		mCache.put(url, bitmap);
	}

}

class ViewHolder {
	public TextView title;
	public ImageView image;
}

