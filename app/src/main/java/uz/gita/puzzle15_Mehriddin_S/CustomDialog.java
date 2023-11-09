package uz.gita.puzzle15_Mehriddin_S;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

public class CustomDialog extends AlertDialog {

    Listener listener;
    Listener2 listenerBtnMenu;
    TextView winScore;
    TextView winTime;
    AppCompatButton btnMenu;

    LocalStorage pref;
    Context contex;

    public void setOnClickListener(Listener listener){
        this.listener = listener;
    }

    public void setOnClickListenerBtnMenu(Listener2 listener2){
        listenerBtnMenu = listener2;
    }

    protected CustomDialog(@NonNull Context context) {
        super(context);
        contex = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_temp);

        pref = LocalStorage.getInstance();
        getWindow().setBackgroundDrawable(null);
        setCancelable(false);
        AppCompatButton restart = findViewById(R.id.btnDialogRestart);
        btnMenu = findViewById(R.id.btnDialogMenu);

        restart.setOnClickListener(v->{
            this.dismiss();
            listener.OnClick();
            dismiss();
        });

        btnMenu.setOnClickListener(v->{
            listenerBtnMenu.OnClick2();
        });

        winScore = findViewById(R.id.win_Score);
        winTime = findViewById(R.id.win_Time);



    }

    interface Listener{
        void OnClick();
    }



    public void setWinTime(String time){
        winTime.setText(time);
    }

    public void setWinScore(String score){
        if(score != null)
            winScore.setText(score);
    }
}
