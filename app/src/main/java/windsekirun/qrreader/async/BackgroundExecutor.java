package windsekirun.qrreader.async;

import java.util.concurrent.Executor;

/**
 * palette
 * Class: BackgroundExecutor
 * Created by WindSekirun on 15. 3. 10..
 */
public interface BackgroundExecutor extends Executor {

    BackgroundExecutor setTaskType(String taskType);

    BackgroundExecutor setThreadPoolSize(int poolSize);
}