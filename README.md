# Twitch VoD downloader

Downloads twitch VoDs (Video on Demand)

Couldn't have been done without the help of:
https://github.com/fonsleenaars/twitch-hls-vods

## How to?

1. Install Java 8u92 or later: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
2. Install ffmpeg: https://ffmpeg.org/
3. Put this code somewhere
4. Run cmd with "gradlew shadowJar" in that folder
5. Take all the files from "copy" folder and run cmd with "twitchVoD -h"

Without ffmpeg you can only download the VoD in small pieces
(depending on VoD, it can vary between 4-60s each)

You can read more [here](CONFIGURATIONS.md)

## Why?

I know this exists: http://docs.livestreamer.io/

But the file it downloads is weird (slightly larger and opens slowly)

Also, I just wanted to code something. So I coded this (real reason)

Also, how cool is that output file naming feature? :D
