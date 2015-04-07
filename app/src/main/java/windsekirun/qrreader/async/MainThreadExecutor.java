package windsekirun.qrreader.async;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * QRReader
 * Class: MainThreadExecutor
 * Created by WindSekirun on 15. 3. 10..
 */
@SuppressWarnings("ALL")
class MainThreadExecutor implements Executor {

    public final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable runnable) {
        mHandler.post(runnable);
    }
}
