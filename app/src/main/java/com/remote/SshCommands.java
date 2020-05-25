package com.remote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.*;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.jcraft.jsch.*;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SshCommands extends MainActivity {

    public static Session session = null;
    @SuppressLint("StaticFieldLeak")
    public static MainActivity myMainActivity = null;


    protected static class SshConnect extends AsyncTask<Void, Void, String> {

        @SuppressLint("StaticFieldLeak")
        Context context;
        String status = null;

        SshConnect(Context context) {
            this.context = context;
        }



        @Override
        public String doInBackground(Void... params) {

            //Integer defaultport = 22;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            final String hostip = prefs.getString("host_ip", null);
            String port = prefs.getString("host_port", "22");
            if (port == "") {
                port = "22";
                            }
            final String username = prefs.getString("username", null);
            final String password = prefs.getString("password", null);

            if (hostip != "" && hostip != null && username != "" && username != null && password != "" && password != null) {

                if (session == null || !session.isConnected()) {
                    try {
                        JSch sshChannel = new JSch();
                        session = sshChannel.getSession(username, hostip, Integer.valueOf(port));
                        session.setPassword(password);
                        session.setConfig("StrictHostKeyChecking", "no");
                        session.connect(1000);

                    } catch (Exception e) {
                        status = e.toString();
                        return status;
                    }
                } }
                else {
                status = "missing";
                return status;
            }
                status = "connected";
                return status;
             }
    }


    protected static class AudaciousStart extends AsyncTask<Void, String, Void> {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myMainActivity);
        String command; //= prefs.getBoolean("headless", false);

        @Override
        protected Void doInBackground(Void... params) {
            try {

                if (prefs.getBoolean("headless", false)) {
                    command = "nohup audacious -H &";
                } else {
                    command = "DISPLAY=:0 nohup audacious &";
                }

                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(command);
                channel.connect();
                channel.disconnect();
            } catch (JSchException e) {

              //  e.printStackTrace();
            }
            return null;
        }
    }

    protected static class PlayPause extends AsyncTask<Void, String, Void> {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myMainActivity);
        String command;

        @Override
        protected Void doInBackground(Void... params) {

            if (session.isConnected()) {
                try {

                    if (prefs.getBoolean("headless", false)) {
                        command = "audacious -t";
                    } else {
                        command = "DISPLAY=:0 audacious -t";
                    }


                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand(command);
                    channel.connect();
                    channel.disconnect();
                } catch (JSchException e) {

                //    e.printStackTrace();
                }

            }     return null;
        }
    }

    protected static class StopPlayback extends AsyncTask<Void, String, Void> {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myMainActivity);
        String command;

        @Override
        protected Void doInBackground(Void... params) {

            if (session.isConnected()) {

                try {

                    if (prefs.getBoolean("headless", false)) {
                        command = "audacious -s";
                    } else {
                        command = "DISPLAY=:0 audacious -s";
                    }

                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand(command);
                    channel.connect();
                    channel.disconnect();
                } catch (JSchException e) {

                  //  e.printStackTrace();
                }
            }       return null;
        }
    }

    protected static class Previous extends AsyncTask<Void, String, Integer> {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myMainActivity);
        String command;
        Integer trackName;

        @Override
        protected Integer doInBackground(Void... params) {
            if (session.isConnected()) {
                try {

                    if (prefs.getBoolean("headless", false)) {
                        command = "audacious -r && audtool --playlist-position";
                    } else {
                        command = "DISPLAY=:0 audacious -r && audtool --playlist-position";
                    }

                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand(command);
                    channel.setOutputStream(null);
                    final ByteArrayOutputStream in = new ByteArrayOutputStream();
                    channel.setOutputStream(in);
                    channel.connect();
                    String tempstring = new String(in.toByteArray(), "UTF-8");
                    while (tempstring.equals("")){
                        Thread.sleep(50);
                        tempstring = new String(in.toByteArray(), "UTF-8");
                    }
                    tempstring = tempstring.replace("\n", "");
                    trackName = Integer.parseInt(tempstring) -1;

                    channel.disconnect();
                } catch (Exception e) {
                    //e.printStackTrace();
                    return trackName = -1;
                }
            }   return trackName;
        }
    }

    protected static class Next extends AsyncTask<Void, String, Integer> {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myMainActivity);
        String command;
        Integer trackName;

        @Override
        protected Integer doInBackground(Void... params) {
            if (session.isConnected()) {
                try {

                    if (prefs.getBoolean("headless", false)) {
                        command = "audacious -f && audtool --playlist-position";
                    } else {
                        command = "DISPLAY=:0 audacious -f && audtool --playlist-position";
                    }

                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand(command);
                    channel.setOutputStream(null);
                    final ByteArrayOutputStream in = new ByteArrayOutputStream();
                    channel.setOutputStream(in);
                    channel.connect();
                    String tempstring = new String(in.toByteArray(), "UTF-8");
                    while (tempstring.equals("")){
                        Thread.sleep(50);
                        tempstring = new String(in.toByteArray(), "UTF-8");
                    }
                    tempstring = tempstring.replace("\n", "");
                    trackName = Integer.parseInt(tempstring) -1;

                    channel.disconnect();
                } catch (Exception e) {
                    //e.printStackTrace();
                    return trackName = -1;
                }
            }   return trackName;
        }
    }

    protected static class getVolume extends AsyncTask<Void, String, String> {
        protected String volLevel;
        private int i = 0;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myMainActivity);
        final String GetVolume = prefs.getString("customGetVolume", "amixer -D default sget Master | grep % | head -1 | awk -F '[' '{print $2}' | awk -F ']' '{print $1}'" );
        @Override
        protected String doInBackground(Void... params) {
            if (session.isConnected()) {
                try {
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand(GetVolume);//("amixer -D default sget Master | grep % | head -1 | awk -F '[' '{print $2}' | awk -F ']' '{print $1}'");
                    //("pacmd dump-volumes | grep \"Sink 0\" | awk -F \" \" '{print $8}'");
                    channel.setOutputStream(null);
                    final ByteArrayOutputStream vl = new ByteArrayOutputStream();
                    channel.setOutputStream(vl);
                    channel.connect();
                    Thread.sleep(200);
                   // volLevel = new String(vl.toByteArray());
                   // while (volLevel == "" && i <= 5) {
                    //    Thread.sleep(100);
                     volLevel = new String(vl.toByteArray());
                     //   i = i + 1;
                     //  }

                    channel.disconnect();
                    if (!volLevel.equals("")) {
                        volLevel = volLevel.replace("\n", "");
                    }
                    else {
                        volLevel = "n/a";
                    }

                } catch (Exception e) {
                   // e.printStackTrace();
                   return volLevel = "n/a";
                }

            } return volLevel;
        }
    }

    protected static class VolumeUp extends AsyncTask<Void, String, String> {
        protected String volLevel;
        private int i = 0;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myMainActivity);
        final String VolumeUp = prefs.getString("customVolumeUp", "amixer -D default sset Master 5%+ | grep % | head -1 | awk -F '[' '{print $2}' | awk -F ']' '{print $1}'");

        @Override
        protected String doInBackground(Void... params) {
            //if (session.isConnected()) {
                try {
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand(VolumeUp);//("amixer -D default sset Master 5%+ | grep % | head -1 | awk -F '[' '{print $2}' | awk -F ']' '{print $1}'");
                    //("pactl set-sink-volume 0 +5% && pacmd dump-volumes | grep \"Sink 0\" | awk -F \" \" '{print $8}'");//"amixer -c 0 sset PCM 3dB+ | grep Mono: | awk -F '[' '{print $2}' | awk -F ']' '{print $1}'");
                    channel.setOutputStream(null);
                    final ByteArrayOutputStream vl = new ByteArrayOutputStream();
                    channel.setOutputStream(vl);
                    channel.connect();
                    while (i <= 5 && vl.toByteArray() != null) {
                        Thread.sleep(50);
                        i = i + 1;
                        }
                    volLevel = new String(vl.toByteArray());
                    channel.disconnect();
                    if (!volLevel.equals("")) {
                        volLevel = volLevel.replace("\n", "");
                    }
                    else {
                        volLevel = "n/a";
                    }

                } catch (Exception e) {
                    // e.printStackTrace();
                    return volLevel = "n/a";
                }
            return volLevel;
        }
    }
    protected static class VolumeDown extends AsyncTask<Void, String, String> {
        protected String volLevel;
        private int i = 0;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myMainActivity);
        final String VolumeDown = prefs.getString("customVolumeDown", "amixer -D default sset Master 5%+ | grep % | head -1 | awk -F '[' '{print $2}' | awk -F ']' '{print $1}'");


        @Override
        protected String doInBackground(Void... params) {
         try {
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand(VolumeDown);//("amixer -D default sset Master 5%- | grep % | head -1 | awk -F '[' '{print $2}' | awk -F ']' '{print $1}'");
         //("pactl -- set-sink-volume 0 -5% && pacmd dump-volumes | grep \"Sink 0\" | awk -F \" \" '{print $8}'"); //"amixer -c 0 sset PCM 3dB- | grep Mono: | awk -F '[' '{print $2}' | awk -F ']' '{print $1}'");
                    channel.setOutputStream(null);
                    final ByteArrayOutputStream vl = new ByteArrayOutputStream();
                    channel.setOutputStream(vl);
                    channel.connect();
                    while (i <= 5 && vl.toByteArray() != null) {
                        Thread.sleep(50);
                        i = i + 1;
                    }
                    volLevel = new String(vl.toByteArray());
                    channel.disconnect();
                    if (!volLevel.equals("")) {
                        volLevel = volLevel.replace("\n", "");
                    }
                    else {
                        volLevel = "n/a";
                    }

                } catch (Exception e) {
                   // e.printStackTrace();
                    return volLevel = "n/a";
                }
                return volLevel;
            }
        }

    protected static class TrackName extends AsyncTask<Void, Void, Integer> {

        Integer trackName;

        @Override
        public Integer doInBackground(Void... params) {

            if (session.isConnected()) {
                try {
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("audtool --playlist-position");
                    channel.setOutputStream(null);
                    final ByteArrayOutputStream in = new ByteArrayOutputStream();
                    channel.setOutputStream(in);
                    channel.connect();
                    String tempstring = new String(in.toByteArray(), "UTF-8");
                    while (tempstring.equals("")){
                        Thread.sleep(50);
                        tempstring = new String(in.toByteArray(), "UTF-8");
                    }
                    tempstring = tempstring.replace("\n", "");
                    trackName = Integer.parseInt(tempstring) -1;

                    channel.disconnect();

                } catch (Exception e) {
                   // e.printStackTrace();
                    return trackName = -1;
                }
            }     return trackName;
        }
    }

    protected static class Shuffle extends AsyncTask<Void, Void, String> {

        protected String shuffleStatus = null;

        @Override
        public String doInBackground(Void... params) {
            if (session.isConnected()) {
                try {
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("audtool --playlist-shuffle-toggle && audtool --playlist-shuffle-status");
                    channel.setOutputStream(null);
                    final ByteArrayOutputStream st = new ByteArrayOutputStream();
                    channel.setOutputStream(st);
                    channel.connect();
                    Thread.sleep(200);
                    shuffleStatus = new String(st.toByteArray());

                    channel.disconnect();
                    } catch (Exception e1) {
                  //  e1.printStackTrace();
                    return shuffleStatus = "n/a";

                }
            }    return shuffleStatus;
        }
    }

    protected static class ShuffleStatus extends AsyncTask<Void, Void, String> {

        protected String shuffleStatus = null;

        @Override
        public String doInBackground(Void... params) {
            if (session.isConnected()) {
                try {
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("audtool --playlist-shuffle-status");
                    channel.setOutputStream(null);
                    final ByteArrayOutputStream st = new ByteArrayOutputStream();
                    channel.setOutputStream(st);
                    channel.connect();
                    Thread.sleep(200);
                    shuffleStatus = new String(st.toByteArray());

                    channel.disconnect();
                    shuffleStatus = shuffleStatus.replace("\n", "");
                } catch (Exception e1) {
                    //e1.printStackTrace();
                    return shuffleStatus = "n/a";

                }
            }    return shuffleStatus;
        }
    }

    protected static class switchPlaylist extends AsyncTask<Void, Void, String> {

        protected String playlist_name;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myMainActivity);
        final String playlist1 = prefs.getString("playlist1", "1");
        final String playlist2 = prefs.getString("playlist2", "2");

        @Override
        public String doInBackground(Void... params) {
            if (session.isConnected()) {
                try {
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("audtool --current-playlist");
                    String plname = null;
                    byte plnumber = 0;
                    channel.setOutputStream(null);
                    ByteArrayOutputStream pl = new ByteArrayOutputStream();
                    channel.setOutputStream(pl);
                    channel.connect();
                   while (plname == null) {
                       Thread.sleep(200);
                       plname = new String(pl.toByteArray()); //, "UTF-8");
                   }
                    channel.disconnect();

                    if (plname.replace("\n", "").equals("1")) {
                        plnumber = 2;
                        playlist_name = playlist2;
                    } else if (plname.replace("\n", "").equals("2")) {
                        plnumber = 1;
                        playlist_name = playlist1;
                    }

                    try {
                        channel.setCommand("audtool --set-current-playlist " + plnumber); // + " && DISPLAY=:0 audtool --current-playlist-name");
                        channel.connect();
                    } catch (Exception e) {
                      //  e.printStackTrace();
                    }
                    channel.disconnect();

                } catch (Exception e) {
                   // e.printStackTrace();
                    return playlist_name = "n/a";
                }
                playlist_name = playlist_name.replace("\n", "");
            }    return playlist_name;
        }
    }


    protected static class currentPlaylist extends AsyncTask<Void, Void, String> {
        protected String currentplaylist;
        private String plTemp;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myMainActivity);
        final String playlist1 = prefs.getString("playlist1", "1");
        final String playlist2 = prefs.getString("playlist2", "2");

        @Override
        public String doInBackground(Void... params) {
            if (session.isConnected()) {
                try {
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("audtool --current-playlist");
                    channel.setOutputStream(null);
                    final ByteArrayOutputStream pl = new ByteArrayOutputStream();
                    channel.setOutputStream(pl);
                    channel.connect();
                    while (plTemp == null) {
                        Thread.sleep(200);
                        plTemp = new String(pl.toByteArray(), "UTF-8");
                    }
                    plTemp = plTemp.replace("\n", "");
                    channel.disconnect();

                } catch (Exception e) {
                    //e.printStackTrace();
                    currentplaylist = "n/a";
                    return currentplaylist;
                }

                if (plTemp.equals("1")) {
                    currentplaylist = playlist1;
                } else if (plTemp.equals("2")) {
                    currentplaylist = playlist2;
                } else {
                    currentplaylist = "n/a";
                }
            }    return currentplaylist;
        }
    }


    protected static class getPlaylist extends AsyncTask<Void, Void, String> {

        protected String currentplaylist;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myMainActivity);
        final String username = prefs.getString("username", null);
        final String home = prefs.getString("home", "/home/"+username+"/");

        @Override
        public String doInBackground(Void... params) {

            if (session.isConnected()) {

                try {

                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("for playlist in $(audtool --current-playlist); do audtool --playlist-display | grep -v 'Total length' | grep -v ' tracks.' | awk -F '|' '{print $2}' > $playlist && echo $playlist; done");
                    channel.setOutputStream(null);
                    final ByteArrayOutputStream pl = new ByteArrayOutputStream();
                    channel.setOutputStream(pl);
                    channel.connect();
                    String currentpl = new String(pl.toByteArray());
                    try {
                        while (currentpl.equals("")) {
                            Thread.sleep(50);
                            currentpl = new String(pl.toByteArray(), "UTF-8");
                        }
                        currentplaylist = currentpl.trim();
                    } catch (Exception e1) {
                      //  e1.printStackTrace();

                        channel.disconnect();
                    }
                } catch (JSchException e) {
                   // e.printStackTrace();

                }

                try {
                    ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
                    channel.connect();
                    String destPath = Environment.getExternalStorageDirectory() + "/remote/" + currentplaylist;//"/storage/emulated/0/Download/" + currentplaylist;
                    String target = home + "/" + currentplaylist;
                    channel.get(target, destPath);
                    channel.exit();

                } catch (Exception e) {
                 //   e.printStackTrace();

                }
            }    return currentplaylist;
        }
    }

    protected static class loadPlaylist extends AsyncTask<String, Void, List<String>> {

        protected String currentplaylist;
        protected List<String> songs = new ArrayList<String>();

        public List<String> doInBackground(String... params) {
            if (session.isConnected()) {
                try {
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("audtool --current-playlist");
                    channel.setOutputStream(null);
                    final ByteArrayOutputStream pl = new ByteArrayOutputStream();
                    channel.setOutputStream(pl);
                    channel.connect();
                    try {
                        Thread.sleep(250);
                        currentplaylist = new String(pl.toByteArray(), "UTF-8");
                        currentplaylist = currentplaylist.trim();
                    } catch (Exception e1) {
                      //  e1.printStackTrace();
                        channel.disconnect();
                    }
                } catch (JSchException e) {
                   // e.printStackTrace();
                }

                try {
                    BufferedReader br = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + "/remote/" + currentplaylist));
                    String line = br.readLine();
                    while (line != null) {
                        songs.add(line);
                        line = br.readLine();
                    }
                    br.close();
                } catch (Exception e) {
                   // e.printStackTrace();
                }

                if (songs.isEmpty()) {
                    songs.add("\nNo playlist file found.\n\nPlease download an updated list : Menu -> Refresh Playlist.");
                }
            }    return songs;
        }
    }

    protected static class selectTrack extends AsyncTask<String, Void, Void> {

        @Override
        public Void doInBackground(String... params) {
            if (session.isConnected()) {
                try {
                    String songId = params[0];
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("audtool --playlist-jump " + songId + " && audtool --playback-play");
                    channel.connect();
                    channel.disconnect();
                } catch (JSchException e) {
                   // e.printStackTrace();

                }
            }     return null;
        }
    }

    protected static class quit extends AsyncTask<String, Void, Void> {
        @Override
        public Void doInBackground(String... params) {
           try {
                    ChannelExec channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("exit");
                    channel.connect();
                    channel.disconnect();
                    session.disconnect();
                } catch (JSchException e) {
                   // e.printStackTrace();
                }
          return null;
        }
    }

    public static class ToastHandler {
        // General attributes
        private Context mContext;
        private Handler mHandler;

        /**
         * Class constructor.
         *
         * @param _context The <code>Context</code> for showing the <code>Toast</code>
         */
        public ToastHandler(Context _context) {
            this.mContext = _context;
            this.mHandler = new Handler();
        }

        /**
         * Runs the <code>Runnable</code> in a separate <code>Thread</code>.
         *
         * @param _runnable The <code>Runnable</code> containing the <code>Toast</code>
         */
        private void runRunnable(final Runnable _runnable) {
            Thread thread = new Thread() {
                public void run() {
                    mHandler.post(_runnable);
                }
            };

            thread.start();
            thread.interrupt();
            thread = null;
        }

        /**
         * Shows a <code>Toast</code> using a <code>Handler</code>. Can be used in
         * background processes.
         *
         * @param text    The resource id of the string resource to use. Can be
         *                  formatted text.
         * @param _duration How long to display the message. Only use LENGTH_LONG or
         *                  LENGTH_SHORT from <code>Toast</code>.
         */
        public void showToast(final String text, final int _duration) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // Get the text for the given resource ID
                    //String text = mContext.getResources().getString(_resID);

                    Toast.makeText(mContext, text, _duration).show();
                }
            };

            runRunnable(runnable);
        }

        /**
         * Shows a <code>Toast</code> using a <code>Handler</code>. Can be used in
         * background processes.
         *
         * @param text The text to show. Can be formatted text.
         *              // * @param _duration
         *              How long to display the message. Only use LENGTH_LONG or
         *              LENGTH_SHORT from <code>Toast</code>.
         */
        public void showToast(final CharSequence text) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(myMainActivity, text, Toast.LENGTH_LONG).show();
                }
            };

            runRunnable(runnable);
        }
    }

}
