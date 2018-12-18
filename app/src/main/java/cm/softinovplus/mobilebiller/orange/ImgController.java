package cm.softinovplus.mobilebiller.orange;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by nkalla on 11/12/18.
 */

public class ImgController extends MultiDexApplication {
    public static final String TAG = ImgController.class.getSimpleName();
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private static ImgController instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Log.e("APPLICATION CREATED", instance.toString());

    }

    public static synchronized ImgController getInstance(){
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return this.requestQueue;
    }
    public ImageLoader getImageLoader(){
        getRequestQueue();
        if(imageLoader ==  null){
            imageLoader = new ImageLoader(this.requestQueue, new LruBitmapCache());
        }

        return this.imageLoader;
    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

