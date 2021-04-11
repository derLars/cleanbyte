package com.derlars.moneyflow.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class DatabaseTime extends TimerTask {
    private static DatabaseTime INSTANCE;
    private static final int PERIOD = 600;

    private boolean running = false;

    private static int seconds;
    private Timer timer;

    private long lastTime = 0;

    public interface Callback {
        void timeUpdate(long time);
    }

    List<Callback> callbacks = new ArrayList<>();

    private DatabaseTime() {
        timer = new Timer();

        //Run once at the beginning to get current time.
        run();
    }

    public static DatabaseTime getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DatabaseTime();
        }
        return INSTANCE;
    }

    public static DatabaseTime getInstance(Callback callback) {
        DatabaseTime.getInstance();

        DatabaseTime.seconds = PERIOD;

        INSTANCE.subscribe(callback);

        return INSTANCE;
    }

    public static void releaseInstance() {
        if(INSTANCE != null) {
            INSTANCE.stop();
        }
        INSTANCE = null;
    }

    public static DatabaseTime getInstance(int seconds, Callback callback) {
        DatabaseTime.getInstance();

        DatabaseTime.seconds = seconds;

        INSTANCE.subscribe(callback);

        return INSTANCE;
    }

    public long getLastTime() {
        return this.lastTime;
    }

    public void subscribe(Callback callback) {
        if(!this.callbacks.contains(callback)) {
            this.callbacks.add(callback);
        }

        if(!running) {
            running = true;
            timer.schedule(this, 0, seconds *1000);
        }
    }

    public void stop() {
        this.timer.cancel();
    }

    public void unsubscribe(Callback callback) {
        if(this.callbacks.contains(callback)) {
            this.callbacks.remove(callback);
        }
    }

    private void update(long currentTime) {
        for(Callback c : callbacks) {
            if(c != null) {
                c.timeUpdate(currentTime);
            }
        }
        //TODO:Remove element when it is null
    }

    private long get() throws Exception {
        String url = "https://time.is/Unix_time_now";
        Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
        String[] tags = new String[] {
                "div[id=time_section]",
                "div[id=clock0_bg]"
        };
        Elements elements= doc.select(tags[0]);
        for (String tag : tags) {
            elements = elements.select(tag);
        }

        return Long.parseLong(elements.text() + "000");
    }

    @Override
    public void run() {
        try {
            this.lastTime = get();
            update(this.lastTime);
        } catch (Exception e) {
            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("UTC"));
            this.lastTime = c.getTimeInMillis();
        }
    }
}
