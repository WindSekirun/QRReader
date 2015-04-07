package windsekirun.qrreader.async;

/**
 * QRReader
 * Class: NaraeTask
 * Created by WindSekirun on 15. 3. 10..
 */
@SuppressWarnings("ALL")
public interface NaraeTask {

    /**
     * Starting at UI Thread
     */
    public void onStartExecute(Runnable runnable);

    /**
     * Main Work in Background Thread
     */
    public void doInBackground(Runnable runnable);

    /**
     * Ending at UI Thread
     */
    public void onPostExecute(Runnable runnable);
}
