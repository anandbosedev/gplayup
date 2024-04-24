package com.anandbose

import java.io.File

sealed interface AppPath
@JvmInline value class ApkPath(val file: File): AppPath
@JvmInline value class BundlePath(val file: File): AppPath

sealed interface ArgParseResult

data class ProgramArguments(
    val serviceAccountFile: File,
    val applicationName: String,
    val appPath: AppPath,
    val track: String,
    val releaseNotes: String? = null,
): ArgParseResult

@JvmInline
value class ArgumentError(val error: String): ArgParseResult

private const val STATE_EXPECT_ARG_KEY = 0
private const val STATE_EXPECT_SERVICE_ACCOUNT_PATH = 1
private const val STATE_EXPECT_APPLICATION_NAME = 2
private const val STATE_EXPECT_BUNDLE_PATH = 3
private const val STATE_EXPECT_APK_PATH = 4
private const val STATE_EXPECT_TRACK = 5
private const val STATE_EXPECT_RELEASE_NOTES = 6

fun parseArgs(args: Array<String>): ArgParseResult {
    var state = STATE_EXPECT_ARG_KEY
    var serviceAccountFilePath: String? = null
    var applicationName: String? = null
    var bundlePath: String? = null
    var apkPath: String? = null
    var track: String? = null
    var releaseNotes: String? = null

    for (arg in args) {
        when (state) {
            STATE_EXPECT_ARG_KEY -> {
                state = when (arg) {
                    ARG_SERVICE_ACCOUNT -> STATE_EXPECT_SERVICE_ACCOUNT_PATH
                    ARG_APPLICATION_NAME -> STATE_EXPECT_APPLICATION_NAME
                    ARG_APK_PATH -> STATE_EXPECT_APK_PATH
                    ARG_BUNDLE_PATH -> STATE_EXPECT_BUNDLE_PATH
                    ARG_TRACK -> STATE_EXPECT_TRACK
                    ARG_RELEASE_NOTES -> STATE_EXPECT_RELEASE_NOTES
                    else -> return ArgumentError("Unknown switch: $arg")
                }
                continue
            }
            STATE_EXPECT_SERVICE_ACCOUNT_PATH -> {
                serviceAccountFilePath = arg
                state = STATE_EXPECT_ARG_KEY
                continue
            }
            STATE_EXPECT_APPLICATION_NAME -> {
                applicationName = arg
                state = STATE_EXPECT_ARG_KEY
                continue
            }
            STATE_EXPECT_BUNDLE_PATH -> {
                bundlePath = arg
                state = STATE_EXPECT_ARG_KEY
                continue
            }
            STATE_EXPECT_APK_PATH -> {
                apkPath = arg
                state = STATE_EXPECT_ARG_KEY
                continue
            }
            STATE_EXPECT_TRACK -> {
                track = arg
                state = STATE_EXPECT_ARG_KEY
                continue
            }
            STATE_EXPECT_RELEASE_NOTES -> {
                releaseNotes = arg
                state = STATE_EXPECT_ARG_KEY
                continue
            }
        }
    }
    val serviceAccountFile = if (serviceAccountFilePath != null) {
        File(serviceAccountFilePath)
    } else {
        return ArgumentError("Service account path should not be empty.")
    }
    if (!serviceAccountFile.exists()) {
        return ArgumentError("Service account file $serviceAccountFilePath does not exist.")
    }
    if (!serviceAccountFile.canRead()) {
        return ArgumentError("Service account file $serviceAccountFilePath cannot be read.")
    }
    if (applicationName.isNullOrBlank()) {
        return ArgumentError("Application name cannot be empty.")
    }
    val appPath = when {
        bundlePath != null -> {
            val file = File(bundlePath)
            if (!file.exists()) {
                return ArgumentError("Bundle path $bundlePath does not exist.")
            }
            if (!file.canRead()) {
                return ArgumentError("Bundle path $bundlePath cannot be read.")
            }
            BundlePath(file)
        }
        apkPath != null -> {
            val file = File(apkPath)
            if (!file.exists()) {
                return ArgumentError("APK path $bundlePath does not exist.")
            }
            if (!file.canRead()) {
                return ArgumentError("APK path $bundlePath cannot be read.")
            }
            ApkPath(file)
        }
        else -> return ArgumentError("Either $ARG_BUNDLE_PATH or $ARG_APK_PATH must be provided.")
    }
    if (track == null) {
        return ArgumentError("Track cannot be empty")
    }
    return ProgramArguments(
        serviceAccountFile = serviceAccountFile,
        applicationName = applicationName,
        appPath = appPath,
        track = track,
        releaseNotes = releaseNotes,
    )
}