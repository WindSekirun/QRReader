package windsekirun.qrreader.async;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * QRReader
 * Class: NaraeAsync
 * Created by WindSekirun on 15. 3. 10..
 */
@SuppressWarnings("ALL")
public class NaraeAsync implements NaraeTask {
    public static final int DEFAULT_POOL_SIZE = getCoresCount() + 1;
    public static final String DEFAULT_TASK_TYPE = "DEFAULT";
    public boolean isMainThread;
    public Runnable runnable;
    public int poolsize;
    public String tasktype;

    public NaraeAsync(boolean isMainThread, Runnable runnable, int poolsize, String tasktype) {
        this.isMainThread = isMainThread;
        this.runnable = runnable;
        this.poolsize = poolsize;
        this.tasktype = tasktype;
    }

    public NaraeAsync(boolean isMainThread, Runnable runnable, int poolsize) {
        this.isMainThread = isMainThread;
        this.runnable = runnable;
        this.poolsize = poolsize;
        this.tasktype = DEFAULT_TASK_TYPE;
    }

    public NaraeAsync(boolean isMainThread, Runnable runnable, String tasktype) {
        this.isMainThread = isMainThread;
        this.runnable = runnable;
        this.poolsize = DEFAULT_POOL_SIZE;
        this.tasktype = tasktype;
    }

    public NaraeAsync(boolean isMainThread, Runnable runnable) {
        this.isMainThread = isMainThread;
        this.runnable = runnable;
        this.poolsize = DEFAULT_POOL_SIZE;
        this.tasktype = DEFAULT_TASK_TYPE;
    }

    public static int getCoresCount() {
        try {
            final File dir = new File("/sys/devices/system/cpu/");
            final File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (final Exception e) {
            return Math.max(1, Runtime.getRuntime().availableProcessors());
        }
    }

    public void execute() {
        if (isMainThread) {
            MainThreadExecutor mte = new MainThreadExecutor();
            mte.execute(runnable);
        } else {
            BackgroundThreadExecutor bte = new BackgroundThreadExecutor();
            bte.setTaskType(tasktype);
            bte.setThreadPoolSize(poolsize);
            bte.execute(runnable);
        }
    }

    @Override
    public void onStartExecute(Runnable runnable) {

    }

    @Override
    public void doInBackground(Runnable runnable) {

    }

    @Override
    public void onPostExecute(Runnable runnable) {

    }

    // TODO: Implement Default Interface : NaraeTask.
    public static class BackgroundThreadExecutor implements BackgroundExecutor {
        private static Map<ExecutorId, Executor> sCachedExecutors = new HashMap<>();

        private int mDesiredThreadPoolSize = DEFAULT_POOL_SIZE;
        private String mDesiredTaskType = DEFAULT_TASK_TYPE;


        @Override
        public BackgroundExecutor setTaskType(String taskType) {
            mDesiredTaskType = taskType;
            return null;
        }

        @Override
        public BackgroundExecutor setThreadPoolSize(int poolSize) {
            mDesiredThreadPoolSize = poolSize;
            return null;
        }

        @Override
        public void execute(@NonNull Runnable command) {
            getExecutor().execute(command);
        }

        Executor getExecutor() {
            final ExecutorId executorId = new ExecutorId(mDesiredThreadPoolSize, mDesiredTaskType);
            synchronized (BackgroundThreadExecutor.class) {
                Executor executor = sCachedExecutors.get(executorId);
                if (executor == null) {
                    executor = Executors.newFixedThreadPool(mDesiredThreadPoolSize);
                    sCachedExecutors.put(executorId, executor);
                }
                return executor;
            }
        }
    }

    public static class ExecutorId {

        private final int mThreadPoolSize;
        private final String mTaskType;

        private ExecutorId(int threadPoolSize, String taskType) {
            mThreadPoolSize = threadPoolSize;
            mTaskType = taskType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExecutorId executorId = (ExecutorId) o;
            if (mThreadPoolSize != executorId.mThreadPoolSize) return false;
            if (!mTaskType.equals(executorId.mTaskType)) return false;
            return true;
        }

        @Override
        public int hashCode() {
            return 31 * mThreadPoolSize + mTaskType.hashCode();
        }
    }

    public static class CpuFilter implements FileFilter {
        @Override
        public boolean accept(final File pathname) {
            if (Pattern.matches("cpu[0-9]+", pathname.getName()))
                return true;
            return false;
        }
    }
}

