package windsekirun.qrreader.async;

import java.util.concurrent.Executor;

/**
 * QRReader
 * Class: BackgroundExecutor
 * Created by WindSekirun on 15. 3. 10..
 */
@SuppressWarnings("ALL")
public interface BackgroundExecutor extends Executor {

    public BackgroundExecutor setTaskType(String taskType);

    public BackgroundExecutor setThreadPoolSize(int poolSize);
}