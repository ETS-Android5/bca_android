package cc.mudev.bca_alter.activity.card;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import cc.mudev.bca_alter.R;
import cc.mudev.bca_alter.activity.main.MainActivity;
import cc.mudev.bca_alter.network.NetworkSupport;
import cc.mudev.bca_alter.util.SwipeDismissBaseActivity;

public class CardShareQRcodeActivity extends SwipeDismissBaseActivity {
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;

    private Button shareWithAnotherWayBtn;
    private ImageView cardShareQRcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_share_qrcode);

        Toolbar toolbar = findViewById(R.id.ac_cardShareQRcode_toolbar);
        toolbar.setTitle("내 명함 공유하기");
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        shareWithAnotherWayBtn = findViewById(R.id.ac_cardShareQRcode_shareWithBtn);
        cardShareQRcode = findViewById(R.id.ac_cardShareQRcode_qrImgView);

        // This activity will be used multiple times
        Intent profileListActivity = new Intent(CardShareQRcodeActivity.this, MainActivity.class);
        profileListActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent activityStartIntent = getIntent();
        int cardId = activityStartIntent.getIntExtra("cardId", -1);
        if (cardId < 0) {
            AlertDialog.Builder errorDialogBuilder = new AlertDialog.Builder(this)
                    .setTitle("내 명함의 QR코드를 표시할 수 없어요.")
                    .setMessage("내 명함의 정보를 가져오는 도중 문제가 발생했습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            startActivity(profileListActivity);
                            finish();
                        }
                    });
            errorDialogBuilder.create().show();
            return;
        }

        NetworkSupport api = NetworkSupport.getInstance();
        // We don't need to initialize api as we just get baseUrl on object
        String cardSubscriptionUrl = String.format(NetworkSupport.baseUrl + "cards/%d/subscribe", cardId);

        try {
            Bitmap qrCodeBitmap = this.encodeAsBitmap(cardSubscriptionUrl);
            cardShareQRcode.setImageBitmap(qrCodeBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            AlertDialog.Builder errorDialogBuilder = new AlertDialog.Builder(this)
                    .setTitle("내 명함의 QR코드를 표시할 수 없어요.")
                    .setMessage("내 명함 고유의 QR코드를 만드는데 오류가 발생했어요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            startActivity(profileListActivity);
                            finish();
                        }
                    });
            errorDialogBuilder.create().show();
            return;
        }

        shareWithAnotherWayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, cardSubscriptionUrl);
                sendIntent.putExtra(Intent.EXTRA_TITLE, "이 링크를 누르면 다른 유저가 내 명함을 구독할 수 있습니다.");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }
    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
