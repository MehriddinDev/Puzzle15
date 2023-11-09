package uz.gita.puzzle15_Mehriddin_S;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LocalStorage.init(this);
    }
}
