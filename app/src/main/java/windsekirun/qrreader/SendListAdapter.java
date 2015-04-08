package windsekirun.qrreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import windsekirun.qrreader.util.ListHolder;
import windsekirun.qrreader.util.SendListItem;

/**
 * QRReader
 * Class: SendListAdapter
 * Created by WindSekirun on 15. 4. 8..
 */
@SuppressWarnings("ALL")
public class SendListAdapter extends ArrayAdapter<SendListItem> {

    public final LayoutInflater inflater;
    public Context c;

    public SendListAdapter(Context c, ArrayList<SendListItem> o) {
        super(c, 0, o);
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = inflater.inflate(R.layout.row_send, parent, false);
        TextView name = ListHolder.get(convertView, R.id.send_name);
        TextView number = ListHolder.get(convertView, R.id.send_number);
        ImageView check = ListHolder.get(convertView, R.id.checkMark);
        SendListItem data = this.getItem(position);
        name.setText("이름: " + data.getName());
        number.setText("학번: " + data.getNumber());
        if (data.getCheck() == 1) {
            check.setVisibility(View.VISIBLE);
        } else {
            check.setVisibility(View.GONE);
        }
        return convertView;
    }
}
