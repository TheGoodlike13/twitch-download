## Configurations

To make the application do what you want it to do, you may want to fiddle
with the settings file and read up on the command line options

## Settings file

Settings file can be found [here](copy/twitch-download.properties)
I've taken my time to explain what each setting does there.
In general, anything you can enter with command line will override the
settings. Also, if any of the settings (or the whole file) is missing,
the application has hardcoded defaults anyway.

## Command line options

You can read up what each command line option does by using '-h', but
hey. Maybe you're here just for the reading, so I'll just write it down
here as well. The only required option is "vodId".

1) -dl, --download
This option disables ffmpeg usage, and just downloads the parts manually.
Uses output_format for the folder name (minus the file extension).

2) -fo OPTIONS, --ffmpeg_options OPTIONS
Adds additional options to the ffmpeg execution. For example:
-fo " -ss 00:00:03 -t 00:00:08"
This will only download 8 seconds of the VoD, start at 3rd second of the
VoD.
Notice how OPTIONS is in brackets (that's because of the spaces) and I
added an extra space there. It's because if no space is added, the
parser library I use thinks it's some kind of a flag and not the OPTIONS
variable. I could do something, but I think adding that space is not
much of an inconvenience compared to alternatives.

3) -fr, -ffmpeg_replace
This will make the -fo OPTIONS override the setting in settings file,
instead of appending. Not sure if it's useful, but here it is!

4) -o FORMAT, --output FORMAT
If you want a different output format than the one in settings, use this.
Same rules apply as in the settings file.

5) -q {audio_only,mobile,low,medium,high,source}, --quality {audio_only,mobile,low,medium,high,source}
CHOOSE YOUR QUALITY! (source is default)

6) -smq, --skip_missing_quality
Sometimes you just want specific quality and won't take no for an answer.
This options lets you do this by NOT defaulting to source quality whenever
your selection is missing, and skip the VoD instead.

7) vodId
VoD id refers to the number 73595705 in the link
https://www.twitch.tv/davidangel64/v/73595705
You can also specify it with 'v' prefix, like this: v73595705
Then, if you're feeling particularly fancy, you can create a text file
with the line "73595705" in it and specify that file's name. App checks:
link -> file -> prefix -> no prefix before ignoring. And yes, all duplicate
ids are ignored, regardless of where or how you specify them.

8) -tm N, --threads_max N
By default, this application limits concurrent ffmpeg processes/downloads
to either your processor core count as reported by the JVM, or 4, whichever
is higher. Override this option here if you want for some reason.

9) -hd, --hide_debug
This will stop printing debug into the command line. Debug includes
stuff like http requests, errors, etc.

10) -hpo, --hide_process_output
This will stop printing ffmpeg output into command line. Generally not
recommended, because ffmpeg output shows progress.

11) -l FILE, --log_file FILE
Logs ALL output into given FILE. Even if you disabled it on command line.
By default no file logging is done.

12) -npc, --no_playlist_clean
Basically, a playlist file is created for ffmpeg, and deleted afterwards.
If you use this, it will not be deleted.

13) -npo, --no_playlist_optimization
Some VoDs are stored in larger parts than the playlist reports (i.e.
stored in 60s segments, but playlist shows 4s parts). These will be
automatically combined when possible. Unless you use this option, of
course.
