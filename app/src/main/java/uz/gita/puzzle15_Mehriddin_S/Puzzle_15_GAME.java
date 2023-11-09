package uz.gita.puzzle15_Mehriddin_S;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Puzzle_15_GAME extends AppCompatActivity {

    private AppCompatButton[][] buttons;
    private TextView stepCounter;
    private Coordinate emptyCoordinate;
    private List<Integer> numbers;
    int counter = 0;
    private MediaPlayer playerButton;
    private MediaPlayer playerShuffle;
    private MediaPlayer musicPlay;
    private MediaPlayer playerWon;
    MediaPlayer back;
    ArrayList<String> savedNum;
    Chronometer chronometer;
    private long pauseTime;
    ArrayList<String> nums;

    public static boolean isWin;
    boolean logic;
    public static boolean hasSound = true;
    private AppCompatImageView mute;
    private AppCompatImageView unMute;

    boolean isResume;
    LocalStorage pref;
    CustomDialog customDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_game);

        pref = LocalStorage.getInstance();
        getWindow().setNavigationBarColor(Color.parseColor("#FECF03"));

        stepCounter = findViewById(R.id.textStepCounter);

        chronometer = findViewById(R.id.chhronometer);

        mute = findViewById(R.id.mute);
        unMute = findViewById(R.id.unMute);
        Intent intent1 = getIntent();
        boolean logic1 = intent1.getBooleanExtra("LOGIC", true);
        if (logic1) {
            mute.setVisibility(View.VISIBLE);
            unMute.setVisibility(View.INVISIBLE);
        } else {
            mute.setVisibility(View.INVISIBLE);
            unMute.setVisibility(View.VISIBLE);
        }

        setAnimation();
        initViews();


        Intent j = getIntent();
        isResume = j.getBooleanExtra("isResume", false);
        if (isResume) {
            RelativeLayout containerButtons = findViewById(R.id.containerButtons);
            int count = containerButtons.getChildCount();
            numbers = new ArrayList<>(count - 1);
            for (int i = 1; i < count; i++) {
                numbers.add(i);
            }

            if (chronometer != null) {
                chronometer.setBase(SystemClock.elapsedRealtime() - pref.getResumeTime());
                chronometer.start();
            }

            counter = pref.getCount();
            stepCounter.setText(counter + "");
            String[] number = pref.getNumbers().split(" ");

            nums = new ArrayList<>(16);
            for (int i = 0; i < 16; i++) {
                if (number[i].equals("16")) {
                    emptyCoordinate.setX(i / 4);
                    emptyCoordinate.setY(i % 4);
                }
                nums.add(number[i]);
            }


            if (pref.getIsWinForResume()) {
                Collections.shuffle(nums);
            }

            setNumberButton(nums);
        } else {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();

            initNumbers();
            shuffleNumbers();
            setNumberButton();
        }
        playerButton = MediaPlayer.create(this, R.raw.bg_btn);
        playerShuffle = MediaPlayer.create(this, R.raw.bg1);
        back = MediaPlayer.create(this, R.raw.bg_exit);
        playerWon = MediaPlayer.create(this, R.raw.win_music);
        playerWon.setLooping(true);
        musicPlay = MediaPlayer.create(this, R.raw.tom_play);
        musicPlay.setLooping(true);


        findViewById(R.id.shuffleBtn).setOnClickListener(view -> {
            chronometer.setBase(SystemClock.elapsedRealtime());
            if (hasSound)
                playerShuffle.start();
            counter = 0;
            stepCounter.setText(counter + "");
            shuffleNumbers();
            setNumberButton();
            chronometer.start();

            pref.saveIsWin(false);
            pref.saveIsPause(false);

            pref.saveTimeForWin(0);
            pref.saveTimeForSimple(0);
            pref.saveTimeForPause(0);
        });

        findViewById(R.id.btnExit).setOnClickListener(v -> {
            if (hasSound)
                back.start();
            finish();
        });

        unMute.setOnClickListener(v -> {
            back.start();
            hasSound = true;
            findViewById(R.id.unMute).setVisibility(View.INVISIBLE);
            mute.setVisibility(View.VISIBLE);
        });

        mute.setOnClickListener(v -> {
            hasSound = false;
            mute.setVisibility(View.INVISIBLE);
            findViewById(R.id.unMute).setVisibility(View.VISIBLE);
        });


        View view = LayoutInflater.from(this).inflate(R.layout.resume_dialog, null);
        ImageView btnPause = findViewById(R.id.btnPause);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        view.findViewById(R.id.btnRestartD).setOnClickListener(v -> {
            counter = 0;
            stepCounter.setText(counter + "");
            shuffleNumbers();
            setNumberButton();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();

            pref.saveIsWin(false);
            pref.saveIsPause(false);

            pref.saveTimeForWin(0);
            pref.saveTimeForSimple(0);
            pref.saveTimeForPause(0);
            dialog.dismiss();
        });

        view.findViewById(R.id.btnResumeD).setOnClickListener(v -> {
            chronometer.setBase(SystemClock.elapsedRealtime() - pref.getTimePause());
            chronometer.start();
            dialog.dismiss();
        });

        view.findViewById(R.id.btnMenuD).setOnClickListener(v -> {
            onBackPressed();
            pref.saveIsPause(false);
            finish();
        });


        btnPause.setOnClickListener(v -> {
            dialog.setCancelable(false);
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(null);

            long time = SystemClock.elapsedRealtime() - chronometer.getBase();
            pref.saveTimeForPause(time);
            chronometer.stop();

            pref.saveIsPause(true);
            Log.d("UUU", pauseTime + "");
        });

        //isWin();

    }

    private ArrayList<String> ordered(String[] num) {
        Log.d("ISWIN", Arrays.toString(num));
        ArrayList<String> s = new ArrayList<>();
        boolean logicc = false;

        for (int i = 1; i < 16; i++) {
            if (num[i] == i + "") {
                logicc = true;
            } else {
                logicc = false;
                i = 17;
            }
        }
        if (logicc) {
            Log.d("ISWIN", logicc + " = logic");
            for (int i = 1; i <= 16; i++) {
                s.add(i + "");
            }
            Collections.shuffle(s);
            Log.d("ISWIN", "s = " + String.valueOf(s));
            return s;
        } else {
            return nums;
        }

    }

    private void initViews() {

        RelativeLayout containerButtons = findViewById(R.id.containerButtons);
        int count = containerButtons.getChildCount(); // 16
        int size = (int) Math.sqrt(count);
        buttons = new AppCompatButton[size][size];
        emptyCoordinate = new Coordinate(size - 1, size - 1);

        for (int i = 0; i < count; i++) { // m = x  :  n = y
            int x = i / size;
            int y = i % size;
            AppCompatButton button = (AppCompatButton) containerButtons.getChildAt(i);


            buttons[x][y] = button;
            button.setTag(new Coordinate(x, y));
            button.setOnClickListener(view -> {
                Coordinate clickedCoordinate = (Coordinate) button.getTag();

                int clickX = clickedCoordinate.getX();
                int clickY = clickedCoordinate.getY();

                //  if (!pref.getWin())
                move(clickX, clickY);
            });
        }

    }

    private void move(int clickX, int clickY) {
        int empX = emptyCoordinate.getX();
        int empY = emptyCoordinate.getY();

        if (Math.abs(clickX - empX) + Math.abs(clickY - empY) == 1) {
            if (isResume) isResume = false;
            if (hasSound)
                playerButton.start();
            buttons[empX][empY].setText(buttons[clickX][clickY].getText());
            buttons[clickX][clickY].setText("16");
            buttons[clickX][clickY].setVisibility(View.INVISIBLE);
            buttons[empX][empY].setVisibility(View.VISIBLE);

            emptyCoordinate.setX(clickX);
            emptyCoordinate.setY(clickY);

            stepCounter.setText(++counter + "");

            pref.saveIsWinForResume(false);

            isWin();
        }

    }

    String pauseTi;

    private void isWin() {
        RelativeLayout containerButtons = findViewById(R.id.containerButtons);
        int sum = containerButtons.getChildCount();
        logic = false;
        if (emptyCoordinate.getX() + emptyCoordinate.getY() == 6)
            for (int i = 0; i < sum - 1; i++) {
                if (buttons[i / 4][i % 4].getText().equals(String.valueOf(i + 1))) {
                    logic = true;
                } else {
                    logic = false;
                    break;
                }
            }
        if (logic) {
            logic = true;
            isWin = true;
            pref.saveIsWin(true);
            pref.saveIsWinForResume(true);
            musicPlay.pause();
            playerWon.start();

            customDialog = new CustomDialog(this);
            customDialog.show();
            customDialog.setWinScore(String.valueOf(counter));
            pauseTi = (String) chronometer.getText();
            customDialog.setWinTime((String) chronometer.getText());
            customDialog.setOnClickListener(new CustomDialog.Listener() {
                @Override
                public void OnClick() {
                    musicPlay.start();
                    playerWon.pause();
                    counter = 0;
                    stepCounter.setText(counter + "");
                    musicPlay.start();
                    shuffleNumbers();
                    setNumberButton();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    pref.saveTimeForWin(0);
                    pref.saveIsWin(false);
                    isWin = false;
                }
            });
            customDialog.setOnClickListenerBtnMenu(new Listener2() {
                @Override
                public void OnClick2() {
                    Intent i = new Intent(Puzzle_15_GAME.this, PuzzleMain.class);
                    startActivity(i);
                    finish();
                }
            });


            if (counter <= LocalStorage.getInstance().getCount() || LocalStorage.getInstance().getCount() == 0) {
                LocalStorage.getInstance().setSaveCount(counter);
            }


            chronometer.stop();
        }
    }

    private void initNumbers() {
        int empX = emptyCoordinate.getX();
        int empY = emptyCoordinate.getY();
        buttons[empX][empY].setVisibility(View.INVISIBLE);
        RelativeLayout containerButtons = findViewById(R.id.containerButtons);
        int count = containerButtons.getChildCount();
        numbers = new ArrayList<>(count - 1);
        for (int i = 1; i < count; i++) {
            numbers.add(i);
        }
    }

    private void setNumberButton() {

        stepCounter.setText(counter + "");

        buttons[emptyCoordinate.getX()][emptyCoordinate.getY()].setVisibility(View.VISIBLE);
        emptyCoordinate.setX(3);
        emptyCoordinate.setY(3);
        buttons[emptyCoordinate.getX()][emptyCoordinate.getY()].setVisibility(View.INVISIBLE);
        buttons[emptyCoordinate.getX()][emptyCoordinate.getY()].setText("16");

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                int index = i * buttons.length + j;
                if (index < 15)
                    buttons[i][j].setText(numbers.get(index) + "");
                buttons[3][3].setText("16");
            }
        }
    }

    private void setNumberButton(ArrayList<String> number) {
        stepCounter = findViewById(R.id.textStepCounter);
        stepCounter.setText(counter + "");

        for (int i = 0; i < 16; i++) {
            buttons[i / 4][i % 4].setVisibility(View.VISIBLE);
            buttons[i / 4][i % 4].setText(number.get(i));
        }

        buttons[emptyCoordinate.getX()][emptyCoordinate.getY()].setVisibility(View.INVISIBLE);

       /* for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                int index = i * buttons.length + j;
                if (index < 15)
                    buttons[i][j].setText(number.get(index));
            }
        }*/
    }

    private void shuffleNumbers() {
        Collections.shuffle(numbers);
        if (!isSolvable(numbers))
            shuffleNumbers();
    }

    private boolean isSolvable(List<Integer> numbers) {
        RelativeLayout containerButtons = findViewById(R.id.containerButtons);
        int count = containerButtons.getChildCount();
        int size = (int) Math.sqrt(count);

        int countInversions = 0;
        for (int i = 0; i < count - 1; i++) {
            for (int j = i; j < count - 1; j++) {
                if (numbers.get(i) > numbers.get(j))
                    countInversions++;
            }
        }
        return countInversions % 2 == 0;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("UUU", "onSaveInstanceState");

        int visibilMute = mute.getVisibility();
        int visibilUnMute = unMute.getVisibility();

        String snum = "", s = "";
        savedNum = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            snum = (String) buttons[i / 4][i % 4].getText();
            savedNum.add(snum);
        }

        Log.d("TTT", String.valueOf(savedNum));
        long pauseT = SystemClock.elapsedRealtime() - chronometer.getBase();

        outState.putLong("time", pauseT);
        outState.putStringArrayList("BUTTONS", savedNum);
        outState.putInt("COUNT", counter);
        outState.putInt("VISIBILMUTE", visibilMute);
        outState.putInt("VISIBILUNMUTE", visibilUnMute);
        outState.putBoolean("LOGIC", logic);
        outState.putString("PauseTime", pauseTi);
    }

    long pauseT;


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("UUU", "onRestoreInstanceState");
        savedNum = savedInstanceState.getStringArrayList("BUTTONS");
        buttons[emptyCoordinate.getX()][emptyCoordinate.getY()].setVisibility(View.VISIBLE);
        for (int i = 0; i < 16; i++) {
            if (savedNum.get(i).equals("16")) {
                buttons[i / 4][i % 4].setVisibility(View.INVISIBLE);
                buttons[i / 4][i % 4].setText("16");
                emptyCoordinate.setX(i / 4);
                emptyCoordinate.setY(i % 4);
            } else buttons[i / 4][i % 4].setText(savedNum.get(i));
        }

        counter = savedInstanceState.getInt("COUNT");
        stepCounter.setText(counter + "");

        /*chronometer.setText((String.valueOf(SystemClock.elapsedRealtime() - pauseTime)));*/
        mute.setVisibility(savedInstanceState.getInt("VISIBILMUTE"));
        unMute.setVisibility(savedInstanceState.getInt("VISIBILUNMUTE"));

        pauseT = savedInstanceState.getLong("time");
        logic = savedInstanceState.getBoolean("LOGIC");
        Log.d("UUU", "time  " + savedInstanceState.getLong("time"));

        pauseTi = savedInstanceState.getString("PauseTime");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("UUU", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("UUU", "onResume");
        musicPlay.start();
        Log.d("UUU", "onResumeda logic" + logic);
        if (logic) {
            musicPlay.pause();
            playerWon.start();
            CustomDialog customDialog = new CustomDialog(this);
            customDialog.show();
            customDialog.setWinScore(String.valueOf(counter));
            customDialog.setWinTime(pauseTi);
            customDialog.setOnClickListener(new CustomDialog.Listener() {
                @Override
                public void OnClick() {
                    musicPlay.start();
                    playerWon.pause();
                    counter = 0;
                    stepCounter.setText(counter + "");
                    musicPlay.start();
                    shuffleNumbers();
                    setNumberButton();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    pref.saveTimeForWin(0);
                    pref.saveIsWin(false);
                    logic = false;
                }
            });
            customDialog.setOnClickListenerBtnMenu(new Listener2() {
                @Override
                public void OnClick2() {
                    Intent i = new Intent(Puzzle_15_GAME.this, PuzzleMain.class);
                    startActivity(i);
                    finish();
                }
            });

            chronometer.stop();
            return;
        }

        if (pref.getWin()) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pref.getTimeWin());
            Log.d("UUU", "getWin");
        } else if (pref.getPause()) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pref.getTimePause());
            Log.d("UUU", "getPause");
        } else if (pauseT != 0) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseT);
            chronometer.start();
        } else if (!isResume) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        }
        /*chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();*/

        if (pref.getWin()) {
            playerWon.start();
            musicPlay.pause();
        } else {
            musicPlay.start();
        }


        if (pref.getPause()) {

            View view = LayoutInflater.from(this).inflate(R.layout.resume_dialog, null);
            ImageView btnPause = findViewById(R.id.btnPause);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .create();

            view.findViewById(R.id.btnRestartD).setOnClickListener(v -> {
                counter = 0;
                stepCounter.setText(counter + "");
                shuffleNumbers();
                setNumberButton();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();

                pref.saveIsWin(false);
                pref.saveIsPause(false);

                pref.saveTimeForWin(0);
                pref.saveTimeForSimple(0);
                pref.saveTimeForPause(0);
                dialog.dismiss();
            });

            view.findViewById(R.id.btnResumeD).setOnClickListener(v -> {
                chronometer.setBase(SystemClock.elapsedRealtime() - pref.getTimePause());
                chronometer.start();
                dialog.dismiss();
            });

            view.findViewById(R.id.btnMenuD).setOnClickListener(v -> {
                onBackPressed();
                pref.saveIsPause(false);
                finish();
            });

            dialog.setCancelable(false);
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(null);

            long time = SystemClock.elapsedRealtime() - chronometer.getBase();
            pref.saveTimeForPause(time);
            chronometer.stop();

            pref.saveIsPause(true);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        musicPlay.pause();
        Log.d("UUU", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("UUU", "onStop");
        playerWon.pause();

        String s = "", ss = "";
        for (int i = 0; i < 16; i++) {
            ss = (String) buttons[i / 4][i % 4].getText();
            s += ss + " ";
        }
        pref.saveNumbers(s);

        pref.setSaveCount(counter);

        long time = SystemClock.elapsedRealtime() - chronometer.getBase();
        pref.saveResumeTime(time);
        if (pref.getWin()) {
            pref.saveTimeForWin(time);
            pref.saveTimeForPause(0);
            pref.saveTimeForSimple(0);

            pref.saveIsPause(false);
            pref.saveNumbers("1 10 2 11 3 12 4 5 13 7 14 6 15 9 8 12 16");
        } else if (pref.getPause()) {
            //pref.saveTimeForPause(time);
            pref.saveTimeForSimple(0);
            pref.saveTimeForWin(0);

            pref.saveIsWin(false);
        } else {
            pref.saveTimeForSimple(time);
            pref.saveTimeForPause(0);
            pref.saveTimeForWin(0);

            //pref.saveIsPause(false);
        }

        chronometer.stop();

        ///saved pref
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        pref.saveTimeForSimple(0);
        //pref.saveIsPause(false);

        if (customDialog != null) customDialog.dismiss();
    }

    private void setAnimation() {
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn1));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn2));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn3));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn4));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn5));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn6));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn7));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn8));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn9));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn10));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn11));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn12));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn13));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn14));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn15));
        //animation
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(0)
                .playOn(findViewById(R.id.btn16));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
