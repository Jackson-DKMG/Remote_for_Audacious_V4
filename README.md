# Remote_for_Audacious_V4

An Android application for controlling Audacious on linux through SSH.

- Should work on any distro, only needs an SSH account and bash.
- Audacious (and audtool) needs to be installed and set up : playlists (up to 2 to cycle through), audio output. It can run in desktop or headless mode.
- a SSH account is needed.
- Volume control default commands may not work depending on the audio configuration. They can be customized if necessary, based on the ALSA/Pulse configuration.

KNOWN ISSUES :

- The application in Android must be configured to disable battery optimization and allow background usage. Otherwise, the SSH connection is killed shortly after the screen goes off. As a result, the battery usage isn't exactly light.
- This is essentially a remote control with extra features : the playlist is displayed and searchable/selectable. The current playing track is queried every 10s, and if it changed since the last check, it is highlighted and the playlist scrolls to its position. Result is that the screen will never go off automatically. Another option is to only do this check once when the screen is lit back on. I will try to make this an option later on.
- The playlists are exported on the server and a copy stored on the device. Whenever the playlists are modified, they need to be refreshed on the device. This can take a while with large playlists : obviously it depends on the server, network and device performances, but with a Rasperry Pi 3 and a recent phone, it takes roughly 15s for a 3k+ items playlist.
