package newreader.playo.com.newreader;

import android.app.Application;

public class MyApplication extends Application {

    public static final String TAG = MyApplication.class.getSimpleName();

    public static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

}
