import com.anandbose.BundlePath
import com.anandbose.ProgramArguments
import com.anandbose.TRACK_PRODUCTION
import com.anandbose.runWithArguments
import java.io.File
import kotlin.test.Test

class BundleUploadTest {
    @Test
    fun uploadBundle() {
        runWithArguments(
            ProgramArguments(
                serviceAccountFile = File("/home/anandbose/Downloads/pc-api-7435836948364980037-995-7caf1aa2f08e.json"),
                applicationName = "com.anandbose.blogapp",
                appPath = BundlePath(File("/home/anandbose/AndroidStudioProjects/blog-android-app/app/build/outputs/bundle/release/app-release.aab")),
                track = TRACK_PRODUCTION,
            )
        )
    }
}