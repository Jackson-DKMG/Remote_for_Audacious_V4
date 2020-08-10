
package com.remote;


import android.Manifest;
//import android.annotation.TargetApi;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.media.MediaCas;
//import android.os.Build;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.os.Vibrator;
//import androidx.annotation.RequiresApi;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.jcraft.jsch.Session;
import com.remote.SshCommands.SshConnect;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

//import static com.remote.SshCommands.myMainActivity;
import static com.remote.SshCommands.session;
import static java.lang.System.exit;
//import static java.lang.System.load;


public class MainActivity extends Activity {

    public ImageButton audacious;
    public ImageButton play;
    public ImageButton stop;
    public ImageButton previous;
    public ImageButton next;
    public ImageButton volume_down;
    public ImageButton volume_up;
    public Button shuffleButton;
    public Button playlist;
    public Button menu;
    public ListView Listview;
    public EditText search;
    public Button volume;
    private ArrayAdapter arrayAdapter;
    public List<String> pl;
    int o;
    public int i = 0;
    public String ls;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        final Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        switch (item.getItemId()) {

            case R.id.Settings:
                vib.vibrate(50);
                Intent i = new Intent(this, Settings.class);
                startActivity(i);
                break;

            case R.id.Help:
                vib.vibrate(50);
                HelpWindow();
                break;

            case R.id.Quit:
                vib.vibrate(50);
                SshCommands.quit quit = new SshCommands.quit();
                quit.execute();
                Process.killProcess(Process.myPid());
                exit(0);

            case R.id.refreshPlaylist:

                vib.vibrate(50);

                if (session != null && session.isConnected()) {
                    //closeOptionsMenu();
                    SshCommands.ToastHandler message = new SshCommands.ToastHandler(getApplicationContext());
                    message.showToast("Downloading playlist. It could take a while if the playlist is large. Please wait.");

                    Runnable r = new Runnable() {
                        public void run() {
                            refreshPlaylist();
                        }
                    };
                    new Thread(r).start();

                } else {
                    SshCommands.ToastHandler message = new SshCommands.ToastHandler(getApplicationContext());
                    message.showToast("Not connected.");
                }
                break;
        }
        return true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_main);

        SshCommands.myMainActivity = this;

        final Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        audacious = (ImageButton) findViewById(R.id.audacious);
        play = (ImageButton) findViewById(R.id.play);
        stop = (ImageButton) findViewById(R.id.stop);
        next = (ImageButton) findViewById(R.id.next);
        previous = (ImageButton) findViewById(R.id.previous);
        volume_down = (ImageButton) findViewById(R.id.volume_down);
        volume_up = (ImageButton) findViewById(R.id.volume_up);
        shuffleButton = (Button) findViewById(R.id.shuffleButton);
        playlist = (Button) findViewById(R.id.playlist);
        menu = (Button) findViewById(R.id.menu);
        Listview = (ListView) findViewById(R.id.Listview);
        volume = (Button) findViewById(R.id.volume);
        search = (EditText) findViewById(R.id.search);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }


        audacious.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                vib.vibrate(50);

         //       Log.d("TIMERDEBUG", "STARTING CONNECTION : CURRENT TIME: " + LocalDateTime.now());

                //if (ContextCompat.checkSelfPermission(getApplicationContext(),
                 //       Manifest.permission.WRITE_EXTERNAL_STORAGE)
                 //       != PackageManager.PERMISSION_GRANTED) {

//                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
  //                          1);
    //            }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {

                     File datadir = new File(Environment.getExternalStorageDirectory() + File.separator + "remote");
                       if (!datadir.exists()) {
                           datadir.mkdir();
                       }

                    if (session == null) {

                        SshCommands.ToastHandler message = new SshCommands.ToastHandler(getApplicationContext());
                        message.showToast("Connecting and starting up. Please wait.");

                        Runnable r = new Runnable() {
                            public void run() {
                                try {
                                    SshConnect sshConnect = new SshConnect(getApplicationContext());
                                    sshConnect.execute();

                                    while (sshConnect.status == null) {
                                        try {
                                            sshConnect.wait();
                                        } catch (Exception ignored) {
                                        }
                                    }


                                    if (sshConnect.status.equals("connected")) {

                                           //Log.d("TIMERDEBUG", "CONNECTED : CURRENT TIME: " + LocalDateTime.now());

                                        startup();
                                    }

                                    if (sshConnect.status.equals("missing")) {
                                        session = null;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SshCommands.ToastHandler message1 = new SshCommands.ToastHandler(getApplicationContext());
                                                message1.showToast("Settings items are missing. Please update.");
                                            }}
                                            );
                                    } else if (!sshConnect.status.equals("connected")) {
                                        session = null;
                                        runOnUiThread(new Runnable() {
                                                          @Override
                                                          public void run() {
                                                              SshCommands.ToastHandler message1 = new SshCommands.ToastHandler(getApplicationContext());
                                                              message1.showToast("Couldn't establish a connection. Please check your network connection or SSH credentials.");
                                                          }
                                                      }
                                        );
                                    }

                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          SshCommands.ToastHandler message1 = new SshCommands.ToastHandler(getApplicationContext());
                                                          message1.showToast("Couldn't establish a connection. Please check your network connection or SSH credentials.");
                                                      }
                                                  }
                                    );
                                    if (session.isConnected()) {
                                        session.disconnect();
                                    }
                                    session = null;
                                    // e.printStackTrace();
                                }

                            }
                        };
                              new Thread(r).start();

                        } else {
                            try {
                                session.connect();
                            } catch (Exception e) {
                                if (e.toString().contains("already connected")) {

                                    SshCommands.ToastHandler message = new SshCommands.ToastHandler(getApplicationContext());
                                    message.showToast("Connected already. Refreshing data.");

                                    Runnable r = new Runnable() {
                                        public void run() {
                                            startup();
                                        }
                                    };
                                    new Thread(r).start();

                                } else {
                                    toast();

                                    if (session.isConnected()) {
                                        session.disconnect();
                                    }
                                    session = null;
                                    //e.printStackTrace();

                                }
                            }
                        }
                    } else {
                        SshCommands.ToastHandler message1 = new SshCommands.ToastHandler(getApplicationContext());
                        message1.showToast("Application will *not* work without storage read/write permission. Exiting.");
                        exit(0);
                    }
                }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vib.vibrate(50);
                if (session != null) {
                    try {
                        session.connect();
                    } catch (Exception e) {
                        if (e.toString().contains("already connected")) {

                            Runnable r = new Runnable() {
                                public void run() {
                            SshCommands.PlayPause playPause = new SshCommands.PlayPause();
                            playPause.execute();
                            SshCommands.TrackName song = new SshCommands.TrackName();
                            song.execute();
                            while (song.trackName == null) {
                                try {
                                    song.wait();
                                } catch (Exception e1) {
                                    //e1.printStackTrace();
                                }
                            }
                            o = song.trackName;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    scrollToPosition();
                                }
                            });
                        }
                    };
                            new Thread(r).start();

                        } else {
                            toast();

                            if (session.isConnected()) {
                                session.disconnect();
                            }
                            session = null;
                            //e.printStackTrace();
                        }
                    }
                } }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vib.vibrate(50);

                if (session != null) {
                try {
                    session.connect();
                } catch (Exception e) {
                    if (e.toString().contains("already connected")) {

                        Runnable r = new Runnable() {
                            public void run() {
                        SshCommands.StopPlayback stopPlayback = new SshCommands.StopPlayback();
                        stopPlayback.execute();
                    }
                };
                    new Thread(r).start();


                    } else {
                        toast();
                        if (session.isConnected()) {
                            session.disconnect();
                        }
                        session = null;
                        //e.printStackTrace();
                    }
                }}
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vib.vibrate(50);
                    if (session != null) {
                    try {
                        session.connect();
                    } catch (Exception e) {
                        if (e.toString().contains("already connected")) {

                            Runnable r = new Runnable() {
                                public void run() {
                                    SshCommands.Next Next = new SshCommands.Next();
                                    Next.execute();

                                    while (Next.trackName == null) {
                                        try {
                                            Next.wait();
                                        } catch (Exception e1) {
                                            //e1.printStackTrace();
                                        }
                                    } o = Next.trackName;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            scrollToPosition();
                                        }
                                    });
                                }
                            };
                            new Thread(r).start();

                        } else {
                            toast();
                            if (session.isConnected()) {
                                session.disconnect();
                            }
                            session = null;
                           // e.printStackTrace();
                        }
                    }
                }}
    });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vib.vibrate(50);
                //String track;
                if (session != null) {
                try {
                    session.connect();
                } catch (Exception e) {
                    if (e.toString().contains("already connected")) {
                        Runnable r = new Runnable() {
                            public void run() {
                                SshCommands.Previous Previous = new SshCommands.Previous();
                                Previous.execute();

                                while (Previous.trackName == null) {
                                    try {
                                        Previous.wait();
                                    } catch (Exception e1) {
                                        //e1.printStackTrace();
                                    }
                                } o = Previous.trackName;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        scrollToPosition();
                                    }
                                });
                            }
                        };
                        new Thread(r).start();

            } else {
                        toast();
                        if (session.isConnected()) {
                            session.disconnect();
                        }
                        session = null;
                       //e.printStackTrace();
                    }
                }
            }}
        });

        volume.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vib.vibrate(50);
                if (session != null) {
                    try {
                        session.connect();
                    } catch (Exception e) {
                        if (e.toString().contains("already connected")) {

                            Runnable r = new Runnable() {
                                public void run() {
                            final String volumeLevel;
                            SshCommands.getVolume getvolume = new SshCommands.getVolume();
                            getvolume.execute();
                            while (getvolume.volLevel == null) {
                                    try {getvolume.wait();
                                } catch (Exception e1) {
                               // e1.printStackTrace();
                            }
                        }
                            volumeLevel = getvolume.volLevel.replace("\n", "");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        volume.setText(volumeLevel);                                    }
                                });
                            }
                        };
                        new Thread(r).start();

                        } else {
                            toast();
                            if (session.isConnected()) {
                                session.disconnect();
                            }
                            session = null;
                          //  e.printStackTrace();
                        }
                    }
                }}
        });


        volume_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vib.vibrate(50);
                if (session != null) {
                    try {
                        session.connect();
                    } catch (Exception e) {
                        if (e.toString().contains("already connected")) {
                            Runnable r = new Runnable() {
                                public void run() {
                                    final String volumeLevel;
                                    SshCommands.VolumeUp volumeUp = new SshCommands.VolumeUp();
                                    volumeUp.execute();
                                    while (volumeUp.volLevel == null) {
                                        try {
                                            volumeUp.wait();
                                        } catch (Exception e1) {
                                            // e1.printStackTrace();
                                        }
                                    }
                                    volumeLevel = volumeUp.volLevel.replace("\n", "");;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            volume.setText(volumeLevel);                                    }
                                    });
                                }
                            };
                            new Thread(r).start();
                        } else {
                            toast();
                            if (session.isConnected()) {
                                session.disconnect();
                            }
                            session = null;
                        //    e.printStackTrace();
                        }
                    }
                }}
        });

        volume_down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vib.vibrate(50);
                if (session != null) {
                    try {
                        session.connect();
                    } catch (Exception e) {
                        if (e.toString().contains("already connected")) {
                            Runnable r = new Runnable() {
                                public void run() {
                                    final String volumeLevel;
                                    SshCommands.VolumeDown volumeDown = new SshCommands.VolumeDown();
                                    volumeDown.execute();
                                    while (volumeDown.volLevel == null) {
                                        try {volumeDown.wait();
                                        } catch (Exception e1) {
                                            // e1.printStackTrace();
                                        }
                                    }
                                    volumeLevel = volumeDown.volLevel.replace("\n", "");;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            volume.setText(volumeLevel);                                    }
                                    });
                                }
                            };
                            new Thread(r).start();
                        } else {
                            toast();
                            if (session.isConnected()) {
                                session.disconnect();
                            }
                            session = null;
                         //   e.printStackTrace();
                        }
                    }
                }}
        });

        playlist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vib.vibrate(50);
                if (session != null) {
                    try {
                        session.connect();
                    } catch (Exception e) {
                        if (e.toString().contains("already connected")) {

                            Toast.makeText(getApplicationContext(), "Switching playlist.", Toast.LENGTH_SHORT).show();

                            Runnable r = new Runnable() {
                                public void run() {

                                    final String cur_playlist;
                                    SshCommands.loadPlaylist loadplaylist = new SshCommands.loadPlaylist();
                                    SshCommands.switchPlaylist switchPlaylist = new SshCommands.switchPlaylist();
                                    SshCommands.TrackName song = new SshCommands.TrackName();


                                    switchPlaylist.execute();
                                    while (switchPlaylist.playlist_name == null) {
                                        try {
                                            switchPlaylist.wait();
                                        } catch (Exception e1) {
                                            //    e1.printStackTrace();
                                        }
                                    }

                                    cur_playlist = switchPlaylist.playlist_name;


                                    loadplaylist.execute();
                                    pl = loadplaylist.songs;
                                    while (pl.isEmpty()) {
                                        try {
                                            loadplaylist.wait();
                                        } catch (Exception e1) {
                                            //      e1.printStackTrace();
                                        }
                                    }

                                    song.execute();
                                    while (song.trackName == null) {
                                        try {
                                            song.wait();
                                        } catch (Exception e1) {
                                            //  e1.printStackTrace();
                                        }
                                    }
                                    o = song.trackName;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            search.setText("");
                                            hideKeyboard();
                                            playlist.setText("Playlist: " + cur_playlist);
                                            createListview();
                                            scrollToPosition();
                                        }
                                    });
                                }
                            };
                            new Thread(r).start();

                        } else {
                            toast();
                            if (session.isConnected()) {
                                session.disconnect();
                            }
                            session = null;
                          // e.printStackTrace();
                        }
                    }
                } }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vib.vibrate(50);
                if (session != null) {
                    try {
                        session.connect();
                    } catch (Exception e) {
                        if (e.toString().contains("already connected")) {

                            Runnable r = new Runnable() {
                                public void run() {
                                    SshCommands.Shuffle shuffle = new SshCommands.Shuffle();
                                    final String shufflestatus;
                                    shuffle.execute();

                                    while (shuffle.shuffleStatus == null) {
                                        try {
                                            shuffle.wait();
                                        } catch (Exception e1) {
                                            //  e1.printStackTrace();
                                        }
                                    }
                                    shufflestatus = shuffle.shuffleStatus.replace("\n", "");
                                    runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        shuffleButton.setText("Shuffle: " + shufflestatus);
                                    }
                                });
                            }
                        };
                        new Thread(r).start();

                        } else {
                            toast();
                            if (session.isConnected()) {
                                session.disconnect();
                            }
                            session = null;
                          //  e.printStackTrace();
                        }
                    }
                }}
        });

        menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            vib.vibrate(50);
                openOptionsMenu();
            }
        });

        Listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //public int songId;

            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vib.vibrate(50);
                Object song = Listview.getItemAtPosition(position);

                SshCommands.selectTrack set = new SshCommands.selectTrack();

                createListview();

                o = pl.indexOf(song);

                scrollToPosition();

                String songId = Integer.toString(o + 1);
                set.execute(songId);
                search.setText("");
                hideKeyboard();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            session.connect();
        } catch (Exception e) {
        }
          //  if (e.toString().contains("already connected")) {
            if ( session != null && session.isConnected()) {
                SshCommands.TrackName song = new SshCommands.TrackName();
                song.execute();

                while (song.trackName == null) {
                    try {
                        song.wait();//Thread.sleep(100);
                    } catch (Exception e1) {
                        //  e1.printStackTrace();
                    }
                }

                o = song.trackName;

                scrollToPosition();
            } else if (session != null && !session.isConnected()) {
                toast();
            }
        }

    public void hideKeyboard() {

        View view = getCurrentFocus();

        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void scrollToPosition() {

        //View view = getCurrentFocus();
        Listview.setAdapter(arrayAdapter);
        Listview.setItemChecked(o, true);
        Listview.setSelection(o - 5);
        //view.setSelected(true);
        arrayAdapter.notifyDataSetChanged();
       }

    public void toast() {
        Toast.makeText(getApplicationContext(), "Not connected.", Toast.LENGTH_LONG).show();
    }

    /* Handler handler = new Handler();
    public Runnable refreshTrack = new Runnable() {

        @Override
        public void run() {

            try {
                session.connect();
            } catch (Exception e) {
                if (e.toString().contains("already connected")) {

                  // PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

                   //if (pm.isInteractive()) {

                    SshCommands.TrackName song = new SshCommands.TrackName();

                        song.execute();
                        //o = 0;
                        int i = 0;
                        try {
                            while (i < 30 && song.trackName == null) {
                                Thread.sleep(100);
                                i = i + 1;
                            }

                        if (song.trackName != o) {
                            o = song.trackName;
                        }

                        } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                        if (!(o == 0)) {
                           // o = o - 1;
                            //createListview();
                            scrollToPosition();
                        }
                    }
                //handler.postDelayed(this, 10000); //check the current track every 10 seconds. If it changed, scroll to the new position. Might also act as a keepalive for the connection ?
                // DISABLED. This is really too freaking annoying as the playlist keeps scrolling back to the current song, can't navigate and search for another track.
            }
        }
    };

 */
public void createListview() {

    try {
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, pl);
        //Thread.sleep(1000);
        while (arrayAdapter.isEmpty()) {
            try {
                Thread.sleep(100);
                arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_activated_1, pl);
            } catch (Exception e) {
               // e.printStackTrace();
            }
            if (!session.isConnected()) {
                toast();
                break;
            }
        }
        Listview.setAdapter(arrayAdapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                (MainActivity.this).arrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    } catch (Exception e) {
       // e.printStackTrace();
    }
    arrayAdapter.notifyDataSetChanged();
}


//@RequiresApi(api = Build.VERSION_CODES.O)

public void startup() {

//    Log.d("TIMERDEBUG", "STARTUP : CURRENT TIME: " + LocalDateTime.now());

    final String shuffle;
    final String play_list;
    final String volumeLevel;

    SshCommands.ShuffleStatus shufflestatus = new SshCommands.ShuffleStatus();
    SshCommands.AudaciousStart audaciousStart = new SshCommands.AudaciousStart();
    SshCommands.currentPlaylist curplaylist = new SshCommands.currentPlaylist();
    SshCommands.getVolume getvolume = new SshCommands.getVolume();
    SshCommands.loadPlaylist loadplaylist = new SshCommands.loadPlaylist();
    SshCommands.TrackName song = new SshCommands.TrackName();


    //Log.d("TIMERDEBUG", "INIT SSH COMMANDS : CURRENT TIME: " + LocalDateTime.now());

    audaciousStart.execute();
    shufflestatus.execute();
    curplaylist.execute();
    getvolume.execute();
    song.execute();
    loadplaylist.execute();

   // Log.d("TIMERDEBUG", "SSH COMMANDS EXECUTED : CURRENT TIME: " + LocalDateTime.now());



   // Log.d("TIMERDEBUG", "PLAYLIST LOADED : CURRENT TIME: " + LocalDateTime.now());

    while (song.trackName == null) {
       try {
            song.wait(1000);//Thread.sleep(100);
        } catch (Exception e) {
         //  e.printStackTrace();
        }
   }

    o = song.trackName;

    if (o == -1) {                    //if no song is queued, try to play/stop to initialize the playlist.
                                      //if still fails, likely the queue is empty.
        SshCommands.PlayPause playPause = new SshCommands.PlayPause();
        SshCommands.StopPlayback stopPlayback = new SshCommands.StopPlayback();

        try {
            playPause.execute();
            Thread.sleep(200);
            stopPlayback.execute();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        song.execute();
        while (song.trackName == null) {
            try {
                song.wait(1000);//Thread.sleep(100);
            } catch (Exception e) {
              //  e.printStackTrace();
            }
        }
        o = song.trackName;
    }

   // Log.d("TIMERDEBUG", "TRACK RETRIEVED : CURRENT TIME: " + LocalDateTime.now());

     if (!(o < -1)) {
        //o = o - 1;

    //     Log.d("TIMERDEBUG", "LISTVIEW + SCROLLED : CURRENT TIME: " + LocalDateTime.now());

        //i = 0;
        while (curplaylist.currentplaylist == null) { //i <= 20 && play_list == null) {
            try {
                curplaylist.wait(1000);
              } catch (Exception e) {
               // e.printStackTrace();
            }
        }
         play_list = curplaylist.currentplaylist;


  //       Log.d("TIMERDEBUG", "PLAYLIST NAME SET : CURRENT TIME: " + LocalDateTime.now());


        while (shufflestatus.shuffleStatus == null) {
            try {
                shufflestatus.wait(1000);
            } catch (Exception e) {
              //  e.printStackTrace();
            }
        }
         shuffle = shufflestatus.shuffleStatus;


//         Log.d("TIMERDEBUG", "SHUFFLED STATUS : CURRENT TIME: " + LocalDateTime.now());

        try {
            while (getvolume.volLevel == null) {
               // getvolume.wait(1000);
                Thread.sleep(50);
            }
        }
        catch (Exception e) {
           // e.printStackTrace();
        }
         volumeLevel = getvolume.volLevel;

         while (loadplaylist.songs.isEmpty()) {
             try {
                 loadplaylist.wait(1000);
             } catch (Exception e) {
                 // e.printStackTrace();
             }
         }

         pl = loadplaylist.songs;

        // Log.d("TIMERDEBUG", "VOLUME SET : CURRENT TIME: " + LocalDateTime.now());

         runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 hideKeyboard();
                 search.setText("");
                 createListview();
                 scrollToPosition();
                 playlist.setText("Playlist: " + play_list);
                 shuffleButton.setText("Shuffle: " + shuffle);
                 volume.setText(volumeLevel);
                 Toast.makeText(getApplicationContext(), "Ready.", Toast.LENGTH_SHORT).show();
             }
         });


      //handler.post(refreshTrack);  //refresh track and move playlist to position every X seconds : annoying as hell when screen is on. Useless when screen is off.
                                       // onResume() does it.

    } else {

        SshCommands.ToastHandler message = new SshCommands.ToastHandler(getApplicationContext());
        message.showToast("The playqueue on Audacious seems to be empty. Maybe it was cleared, or the connection was lost. Aborting.");
        if (session.isConnected()) {
            session.disconnect();
        }
        session = null;
    }
}


public void refreshPlaylist() {

    if (session != null) {

        try {
            session.connect();
        } catch (Exception e) {
            if (e.toString().contains("already connected")) {

             //   SshCommands.TrackName song = new SshCommands.TrackName();
                SshCommands.getPlaylist getplaylist = new SshCommands.getPlaylist();
                SshCommands.loadPlaylist loadplaylist = new SshCommands.loadPlaylist();
                SshCommands.currentPlaylist curplaylist = new SshCommands.currentPlaylist();

                try {

                        getplaylist.execute();


                        while (getplaylist.currentplaylist == null) {
                            try {
                                getplaylist.wait();
                            } catch (Exception e1) {
                               // e1.printStackTrace();
                            }
                        }

                     final String currentpl;
                     curplaylist.execute();

                    while (curplaylist.currentplaylist == null) {
                        try {
                            curplaylist.wait();
                        } catch (Exception e1) {
                            // e.printStackTrace();
                        }
                    }
                    currentpl = curplaylist.currentplaylist;

                       // playlist.setText("Playlist: " + currentpl);

                        loadplaylist.execute();

                        //pl = loadplaylist.songs;
                        int length = -1;
                        while (length < loadplaylist.songs.size()) {
                            try {
                                length = loadplaylist.songs.size();
                                Thread.sleep(50);
                                } catch (Exception e1) {
                              //  e1.printStackTrace();
                            }
                        }
                        pl = loadplaylist.songs;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createListview();
                            scrollToPosition();
                            playlist.setText("Playlist: " + currentpl);
                            Toast.makeText(getApplicationContext(), "Playlist downloaded.", Toast.LENGTH_LONG).show();
                        }
                    });


                    //SshCommands.ToastHandler message1 = new SshCommands.ToastHandler(this);
                    //message1.showToast("Playlist downloaded.");

                } catch (Exception e1) {
                 //   e1.printStackTrace();
                }
            }
        }
    }
    else{
        toast();
        if (session.isConnected()) {
            session.disconnect();
        }
        session = null;
         }
}

public void HelpWindow() {

    Dialog help = new Dialog(MainActivity.this);
    help.setContentView(R.layout.helpview);
    TextView Help = help.findViewById(R.id.helpView);
    Help.setText("Audacious needs to be installed.\nYou can have two playlists.\n\n" +
            "If you have an external audio card, you may need to adjust the ALSA configuration and/or customize the volume control commands.\n\n" +
            "If using Pulse Audio : edit /etc/pulse/daemon.conf, uncomment the line 'default-sample-rate' and set the value to the max bitrate of your " +
            "card/DAC, e.g. 192000.\n" +
            "Pulse Audio will be useful if the DAC/card has no volume control capabilities.\n" +
            "If so, adjust the audio output settings in Audacious.\n\n");
    Help.setTextSize(18);
    help.show();
    }
}