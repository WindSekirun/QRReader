package windsekirun.qrreader.util;

/**
 * QRReader
 * Class: SendListItem
 * Created by WindSekirun on 15. 4. 8..
 */
@SuppressWarnings("ALL")
public class SendListItem {

    final String name;
    final String number;
    final int check;

    public SendListItem(String name, String number, int check) {
        this.name = name;
        this.number = number;
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public int getCheck() {
        return check;
    }
}
