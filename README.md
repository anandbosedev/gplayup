## `gplayup`
`gplayup` is a command-line utility built with Kotlin/JVM to publish app (and updates)
in Google Play. It supports both APK and app bundles. The typical usage of this tool 
will be like this:

```shell
./gplayup-1.0-SNAPSHOT/bin/gplayup --service-account ./path/to/service-account.json
    --application-name com.example.app
    [--bundle-path ./path/to/bundle.aab | --apk-path ./path/to/apkfile.apk]
    --track 'production'
    --release-notes 'release notes'
```
The track names will be `production`, `alpha`, `beta`, `qa` or form factor tracks
such as `wear:production`, `automotive:beta` as mentioned in the official
Google Play Developer API [documentation](https://developers.google.com/android-publisher/tracks#ff-track-name).

*Note: This utility internally uses `google-api-services-androidpublisher` Java library,
so make sure you installed Java 17 or later in your host system to run this tool.*