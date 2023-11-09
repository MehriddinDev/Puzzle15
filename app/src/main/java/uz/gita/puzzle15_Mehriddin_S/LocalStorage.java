package uz.gita.puzzle15_Mehriddin_S;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    private static LocalStorage localStorage;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private LocalStorage(Context context) {
        preferences = context.getSharedPreferences("PUZZLE", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static LocalStorage getInstance() {
        return localStorage;
    }

    public static void init(Context context) {
        if (localStorage == null)
            localStorage = new LocalStorage(context);
    }

    /*  public void setSaveString(String s) {
          editor.putString("LANDNUMBERS", s);
      }

      public String getString() {
          return preferences.getString("LANDNUMBERS", "0");
      }

      public void setSaveStep(int step) {
          editor.putInt("StepCurrent", step).apply();
      }

      public int getStep() {
          return preferences.getInt("StepCurrent", 0);
      }
  */
    public void setSaveCount(int count) {
        editor.putInt("COUNT", count).apply();
    }


    public int getCount() {
        return preferences.getInt("COUNT", 0);
    }

    public void saveIsWin(boolean isWin){
        editor.putBoolean("iswin",isWin).apply();
    }

    public boolean getWin(){
        return preferences.getBoolean("iswin",true);
    }

    public void saveIsPause(boolean pause){
        editor.putBoolean("pause",pause).apply();
    }

    public boolean getPause(){
        return preferences.getBoolean("pause",false);
    }

    public void saveResumeTime(long time){
        editor.putLong("TimeResume",time).apply();
    }

    public long getResumeTime(){
        return preferences.getLong("TimeResume",0);
    }
    public void saveTimeForSimple(long n){
        editor.putLong("TimeSimple",n).apply();
    }
    public long getTimeSimple() {
        return preferences.getLong("TimeSimple", 0);
    }

    public void saveIsWinForResume(boolean l){
        editor.putBoolean("h",l).apply();
    }
    public boolean getIsWinForResume(){
        return preferences.getBoolean("l",false);
    }

    public void saveTimeForWin(long n){
        editor.putLong("TimeWin",n).apply();
    }
    public long getTimeWin() {
        return preferences.getLong("TimeWin", 0);
    }

    public void saveTimeForPause(long n){
        editor.putLong("TimePause",n).apply();
    }
    public long getTimePause() {
        return preferences.getLong("TimePause", 0);
    }

    public void saveNumbers(String s){
        editor.putString("Number",s).apply();
    }

    public String getNumbers(){
        return preferences.getString("Number","-1");
    }


}
