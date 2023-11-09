package uz.gita.puzzle15_Mehriddin_S;

import static uz.gita.puzzle15_Mehriddin_S.Puzzle_15_GAME.hasSound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

public class MyRecord extends AppCompatActivity {
    AppCompatButton exit;
    MediaPlayer back;
    MediaPlayer musicRecord;
    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_record);

        getWindow().setNavigationBarColor(Color.parseColor("#FECF03"));

        TextView textView = findViewById(R.id.recordStep);
        int counter = LocalStorage.getInstance().getCount();
        String s = String.valueOf(counter);
        textView.setText(s);

        back = MediaPlayer.create(this,R.raw.bg_exit);
        musicRecord = MediaPlayer.create(this,R.raw.tom_record);
        musicRecord.setLooping(true);

        findViewById(R.id.btnExit).setOnClickListener(v->{
            if(hasSound)
                back.start();
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        musicRecord.start();
        TextView textView = findViewById(R.id.recordStep);
        textView.setText(String.valueOf(LocalStorage.getInstance().getCount()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        musicRecord.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
