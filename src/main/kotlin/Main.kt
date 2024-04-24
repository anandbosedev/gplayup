package com.anandbose

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import com.google.api.services.androidpublisher.model.LocalizedText
import com.google.api.services.androidpublisher.model.Track
import com.google.api.services.androidpublisher.model.TrackRelease
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials

fun main(args: Array<String>) {
    when (val argParseResult = parseArgs(args)) {
        is ArgumentError -> {
            println(argParseResult.error)
            printHelp()
        }
        is ProgramArguments -> runWithArguments(argParseResult)
    }
}

fun printHelp() {
    println("""
        Usage:
        $APP_NAME $ARG_SERVICE_ACCOUNT ./path/to/service-account.json
            $ARG_APPLICATION_NAME com.example.app
            [$ARG_BUNDLE_PATH ./path/to/bundle.aab | $ARG_APK_PATH ./path/to/apkfile.apk]
            $ARG_TRACK 'production'
            $ARG_RELEASE_NOTES 'release notes'
    """.trimIndent())
}

fun runWithArguments(args: ProgramArguments) {
    val credential = GoogleCredentials.fromStream(args.serviceAccountFile.inputStream())
        .createScoped(AndroidPublisherScopes.ANDROIDPUBLISHER)
    val token = credential.refreshAccessToken()?.tokenValue
    if (token != null) {
        if (LOG_ACCESS_TOKEN) {
            logDecorated("Access Token", token)
        }
        uploadToGooglePlay(args, credential)
    } else {
        println("Failed to obtain access token")
    }
}

fun uploadToGooglePlay(args: ProgramArguments, credentials: GoogleCredentials) {
    val transport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory = GsonFactory.getDefaultInstance()
    val requestInitializer = HttpCredentialsAdapter(credentials)
    val publisher = AndroidPublisher.Builder(transport, jsonFactory, requestInitializer)
        .setApplicationName(args.applicationName)
        .build()

    val edits = publisher.edits()
    val insert = edits.insert(args.applicationName, null)
    val appEdit = insert.execute()
    val editId = appEdit.id
    logDecorated("Created Edit ID: $editId")
    val versionCode = when (val app = args.appPath) {
        is ApkPath -> {
            val apkFile = FileContent(MIME_TYPE_APK, app.file)
            val upload = edits.apks()
                .upload(args.applicationName, editId, apkFile)
            val apk = upload.execute()
            logDecorated("Uploaded APK version code: ${apk.versionCode}")
            apk.versionCode
        }
        is BundlePath -> {
            val bundleFile = FileContent(MIME_TYPE_BUNDLE, app.file)
            val upload = edits.bundles()
                .upload(args.applicationName, editId, bundleFile)
            val bundle = upload.execute()
            logDecorated("Uploaded App Bundle version code: ${bundle.versionCode}")
            bundle.versionCode
        }
    }

    val update = edits.tracks()
        .update(
            args.applicationName,
            editId,
            args.track,
            Track().setReleases(
                mutableListOf(
                    TrackRelease()
                        .setVersionCodes(mutableListOf(versionCode.toLong()))
                        .setStatus("completed")
                        .setReleaseNotes(
                            mutableListOf(
                                LocalizedText()
                                    .setLanguage("en-US")
                                    .setText(args.releaseNotes ?: "")
                            )
                        )
                )
            )
        )
    val track = update.execute()
    logDecorated("Track ${track.track} has been updated.")

    val commit = edits.commit(args.applicationName, editId)
    val appEditFinal = commit.execute()
    logDecorated("App edit ${appEditFinal.id} has been committed.")
}