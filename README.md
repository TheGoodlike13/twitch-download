## Twitch VoD downloader

Downloads twitch VoDs (Video on Demand)
Couldn't have been done without the help of:
https://github.com/fonsleenaars/twitch-hls-vods

## How to?

1) Put this code somewhere
2) Run cmd with "gradlew shadowJar" in that folder
3) Take all the files from "copy" folder and run cmd with "twitchVoD -h"
4) RECOMMENDED! Install ffmpeg: https://ffmpeg.org/
Without ffmpeg you can only download small pieces (depending on VoD, it
can vary between 4-60s each)

You can read more [here](CONFIGURATIONS.md)

## Why?

I know this exists: http://docs.livestreamer.io/
But the file it downloads is weird (slightly larger and opens slowly)
Also, I just wanted to code something. So I coded this (real reason)
Also, how cool is that output file naming feature? :D

## FAQ:

Nobody has asked me anything yet.
