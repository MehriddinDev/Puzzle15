package uz.gita.puzzle15_Mehriddin_S;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

public class Info extends AppCompatActivity {

    AppCompatImageView quite;
    MediaPlayer playerQuite;
    MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        getWindow().setNavigationBarColor(Color.parseColor("#FECF03"));

        playerQuite = MediaPlayer.create(this,R.raw.bg_exit);
        music = MediaPlayer.create(this,R.raw.info_music);
        quite = findViewById(R.id.btnQuite);

        quite.setOnClickListener(v->{
            if(Puzzle_15_GAME.hasSound)
                playerQuite.start();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        music.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        music.stop();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}