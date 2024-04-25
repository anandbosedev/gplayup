# `gplayup` 📦🚀
`gplayup` is a command-line utility built with Kotlin/JVM to publish app (and updates)
in Google Play. This uses [Google APIs Client Library for Java](https://developers.google.com/api-client-library/java/apis/androidpublisher/v3)
under the hood, and makes use of Android Publisher APIs. It supports both APK and 
app bundles. The typical usage of this tool will be like this:

```shell
user:~$ ./gplayup-1.0-SNAPSHOT/bin/gplayup \
  --service-account ~/Downloads/service-account.json \
  --application-name com.anandbose.blogapp \
  --bundle-path ~/Downloads/app-release.aab \
  --track production \
  --release-notes 'Bug fixes'
  
✅ Authenticated with Google Play
✍ Created Edit ID: 15183043767374173813
📦 Uploading App Bundle
🚀 Uploaded App Bundle (version code: 7)
✅ Track production has been updated.
✨ App (Edit ID15183043767374173813) has been committed to production track.
```
## Requirements
JDK 17 is required. Tested with Eclipse Temurin JDK 17.

## Prerequisites
To use `gplayup`, make sure you have the following requirements met:

* You need to create a project in Google Cloud.
* Create a service account, download the JSON file and keep it safe.
* Link the service account to the Google Play console, providing proper permissions
to the application and associated tracks

[Reference: Google Pla Developer API - Get Started](https://developers.google.com/android-publisher/getting_started)