package windsekirun.qrreader.test.async;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * palette
 * Class: MainThreadExecutor
 * Created by WindSekirun on 15. 3. 10..
 */
class MainThreadExecutor implements Executor {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable runnable) {
        mHandler.post(runnable);
    }
}
