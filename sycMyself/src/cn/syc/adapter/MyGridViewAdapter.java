package cn.syc.adapter;

import java.util.ArrayList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.syc.R;
import cn.syc.activity.ListGridPicActivity;
import cn.syc.bean.PictureLog;

public class MyGridViewAdapter extends BaseAdapter {
	private LayoutInflater mLayoutInflater;
	private ArrayList<PictureLog> mPictures = new ArrayList<PictureLog>();
	private Context ctx;
	private ImageLoader mImageLoader;
	// private RequestQueue mQueue; // volley请求

	public MyGridViewAdapter(Context ctx, ArrayList<PictureLog> mList) {
		// TODO Auto-generated constructor stub
		super();
		this.ctx = ctx;
		this.mPictures = mList;
		this.mLayoutInflater = LayoutInflater.from(this.ctx);
		this.mImageLoader = ImageLoader.getInstance();
		// this.mQueue = Volley.newRequestQueue(this.ctx);

	}

	public static DisplayImageOptions getImageLoaderOptions(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()  
				 .showImageOnLoading(R.drawable.ic_launcher) //设置图片在下载期间显示的图片  
				 .showImageForEmptyUri(android.R.drawable.stat_notify_error)//设置图片Uri为空或是错误的时候显示的图片  
				.showImageOnFail(R.drawable.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)//设置下载的图片是否缓存在内存中  
				.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中  
				.considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示  
				.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
				//.decodingOptions(android.graphics.BitmapFactory.Options)//设置图片的解码配置  
				//.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
				//设置图片加入缓存前，对bitmap进行设置  
				//.preProcessor(BitmapProcessor preProcessor)  
				.resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
				//.displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少  
				//.displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
				.build();//构建完成  
		return options;
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
		final ViewHolder mViewHolder;
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
		// loadImageByImageLoader(mViewHolder.image,
		// mPictures.get(position).getUrl());
		//mImageLoader.displayImage(mPictures.get(position).getUrl(), mViewHolder.image,this.getImageLoaderOptions());		
		mImageLoader.displayImage(mPictures.get(position).getUrl(), mViewHolder.image,this.getImageLoaderOptions(), new SimpleImageLoadingListener(), new ImageLoadingProgressListener() {
			
			@Override
			public void onProgressUpdate(String arg0, View arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				Log.i("mImageLoader-onProgressUpdate", "current:"+arg2+"total:"+arg3);
				mViewHolder.title.setText(100 * arg2 / arg3+"%");
			}
		});
		
		
		return convertView;
	}
}

class ViewHolder {
	public TextView title;
	public ImageView image;
}

/*	*//**
		 * 使用volley的ImageLoader加载图片（自带缓存）
		 * 
		 * @param imageview
		 * @param imgUrl
		 *//*
		 * private void loadImageByImageLoader(ImageView image, String imageUrl)
		 * {
		 * 
		 * // 创建ImageLoader对象，参数（加入请求队列，自定义缓存机制） ImageLoader imageLoader = new
		 * ImageLoader(this.mQueue, new BitmapCache());
		 * 
		 * // 获取图片监听器 参数（要显示的ImageView控件，默认显示的图片，加载错误显示的图片）
		 * ImageLoader.ImageListener listener =
		 * ImageLoader.getImageListener(image, android.R.drawable.gallery_thumb,
		 * android.R.drawable.ic_dialog_alert);
		 * 
		 * imageLoader.get(imageUrl, listener, 100, 120); } }
		 * 
		 * class BitmapCache implements ImageCache { private LruCache<String,
		 * Bitmap> mCache;
		 * 
		 * public BitmapCache() { int maxMemory = (int)
		 * Runtime.getRuntime().maxMemory(); int cacheSize = maxMemory / 8;
		 * mCache = new LruCache<String, Bitmap>(cacheSize) {
		 * 
		 * @Override protected int sizeOf(String key, Bitmap value) { return
		 * value.getRowBytes() * value.getHeight(); }
		 * 
		 * }; }
		 * 
		 * @Override public Bitmap getBitmap(String url) { return
		 * mCache.get(url); }
		 * 
		 * @Override public void putBitmap(String url, Bitmap bitmap) {
		 * mCache.put(url, bitmap); }
		 * 
		 * }
		 */
