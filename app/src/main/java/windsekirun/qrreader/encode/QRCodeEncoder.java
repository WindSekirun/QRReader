package windsekirun.qrreader.encode;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

/**
 * QRReader
 * Class: QRCodeEncoder
 * Created by WindSekirun on 2015. 4. 6..
 */
@SuppressWarnings("ALL")
public class QRCodeEncoder {

    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;

    public String contents;
    public String displayContents;
    public String title;
    public BarcodeFormat format;
    public boolean encoded;

    public QRCodeEncoder(String data, String format) {
        encoded = encodeContents(data, format);
    }

    public boolean encodeContents(String data, String formatString) {
        format = null;
        if (formatString != null) {
            try {
                format = BarcodeFormat.valueOf(formatString);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            }
        }
        if (format == null || format == BarcodeFormat.QR_CODE) {
            this.format = BarcodeFormat.QR_CODE;
            encodeQRCodeContents(data, Contents.Type.TEXT);
        } else if (data != null && data.length() > 0) {
            contents = data;
            displayContents = data;
            title = "Text";
        }
        return contents != null && contents.length() > 0;
    }

    public void encodeQRCodeContents(String data, String type) {
        switch (type) {
            case Contents.Type.TEXT:
                if (data != null && data.length() > 0) {
                    contents = data;
                    displayContents = data;
                    title = "Text";
                }
                break;
        }
    }

    public Bitmap encodeAsBitmap() throws WriterException {
        if (!encoded) return null;

        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = writer.encode(contents, format, 1536, 1536, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];

        for (int i = 0; i < height; i++) {
            int offset = i * width;
            for (int j = 0; j < width; j++)
                pixels[offset + j] = result.get(j, i) ? BLACK : WHITE;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}
