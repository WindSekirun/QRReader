package windsekirun.qrreader.util;

import android.util.SparseArray;
import android.view.View;

/**
 * QRReader
 * Class: ListHolder
 * Created by WindSekirun on 15. 4. 8..
 */
@SuppressWarnings("ALL")
public class ListHolder {

    public static SparseArray<View> viewHolder;
    public static View childView;

    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<>();
            view.setTag(viewHolder);
        }

        childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

    public static void clean() {
        viewHolder.clear();
        childView.clearFocus();
    }
}
