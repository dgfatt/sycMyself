package cn.syc;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
    }

    private void initImageLoader(Context context){
        /**定义缓存文件的目录**/
        File cacheDir= StorageUtils.getOwnCacheDirectory(getApplicationContext(),context.getPackageName());
        /**ImageLoader的配置**/
        ImageLoaderConfiguration config=new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY-2) //设置同时运行的线程
                .denyCacheImageMultipleSizesInMemory()  //缓存显示不同大小的同一张图片
                .diskCacheSize(50*1024*1024)  //50MB SD卡本地缓存的最大值
                .diskCache(new UnlimitedDiskCache(cacheDir)) //SD卡缓存
                .memoryCache(new WeakMemoryCache()) //内存缓存
                .writeDebugLogs()//print log
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        //全局初始化配置
        ImageLoader.getInstance().init(config);
    }
}
