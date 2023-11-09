package uz.gita.puzzle15_Mehriddin_S;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class PuzzleMain extends AppCompatActivity {
    MediaPlayer playerOpen;
    MediaPlayer musicChose;
    AlertDialog dialog;
    AppCompatButton resume;

    LocalStorage pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_main);

        pref = LocalStorage.getInstance();

        getWindow().setFlags((WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS), WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        playerOpen = MediaPlayer.create(this, R.raw.bg_exit);
        musicChose = MediaPlayer.create(this, R.raw.choose_activity);
        musicChose.setLooping(true);
        musicChose.start();


        AppCompatButton play = findViewById(R.id.btnPlay);
        resume = findViewById(R.id.btnResume);
        AppCompatButton record = findViewById(R.id.btnMyRecord);
        AppCompatButton exit = findViewById(R.id.btnLeft);
        AppCompatButton info = findViewById(R.id.btnInfo);


        resume.setOnClickListener(v -> {
            Intent i = new Intent(this, Puzzle_15_GAME.class);
            i.putExtra("isResume", true);
            startActivity(i);
        });

        play.setOnClickListener(v -> {
            boolean logic = Puzzle_15_GAME.hasSound;
            playerOpen.start();
            Intent intent = new Intent(this, Puzzle_15_GAME.class);
            intent.putExtra("LOGIC", logic);
            pref.saveIsWin(false);
            startActivity(intent);
        });

        record.setOnClickListener(v -> {
            playerOpen.start();
            Intent intent = new Intent(this, MyRecord.class);
            startActivity(intent);
        });

        View view = LayoutInflater.from(this).inflate(R.layout.quit_item, null);
        dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();


        view.findViewById(R.id.txtNo).setOnClickListener(v -> {
            playerOpen.start();
            dialog.dismiss();
        });

        view.findViewById(R.id.txtYes).setOnClickListener(v -> {
            playerOpen.start();
            finish();

        });


        exit.setOnClickListener(v -> {
            playerOpen.start();
            dialog.show();
        });

        info.setOnClickListener(v -> {
            playerOpen.start();
            Intent intent = new Intent(this, Info.class);
            startActivity(intent);

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        musicChose.start();

        if(pref.getWin()){

            resume.setVisibility(View.GONE);
        }else {
            resume.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        musicChose.pause();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
