package speedTest

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.net.URI
import java.time.Duration
import java.time.LocalTime
import kotlin.random.Random

const val MIN_VALUE = 0
const val MAX_VALUE = 100

@RequiresApi(Build.VERSION_CODES.O)
class SpeedTest(
    private var imgAddress: String = "https://wallpaperswide.com/download/shadow_of_the_tomb_raider_2018_puzzle_video_game-wallpaper-7680x4800.jpg?n=${
        Random.nextInt(
            MIN_VALUE,
            MAX_VALUE
        )
    }",
    private var downloadSize: Long = 5616998,
    private var duration: Long? = null,
    private var startTime: LocalTime? = null,
    private var endTime: LocalTime? = null
) {
    private fun downloadImg() {
        val url = URI(imgAddress).toURL()
        startTime = LocalTime.now()
        url.readBytes()
        endTime = LocalTime.now()
    }

    private fun showResults(): Long {
        duration = Duration.between(startTime, endTime).toMillis()
        val bitsLoaded = downloadSize * 8
        val speedBps = bitsLoaded / duration!!
        return speedBps * 1000 // TO account for milliseconds
    }

    fun measure(imgAddress: String, downloadSize: Long) {
        this.imgAddress = imgAddress
        this.downloadSize = downloadSize
        downloadImg()
        showResults()
    }

    fun measure(): Long {
        imgAddress =
            "https://wallpaperswide.com/download/shadow_of_the_tomb_raider_2018_puzzle_video_game-wallpaper-7680x4800.jpg?n=${
                Random.nextInt(
                    MIN_VALUE,
                    MAX_VALUE
                )
            }"
        downloadImg()
        return showResults()
    }

    fun measureCycle(
        updateSpeed: (Double) -> Unit,
        ): Long {
        val results = Array<Long>(10) { 0 }
        //Doing 10 cycles to account for tcp ramp up
        for (i in 0..<10) {
            Log.d("SpeedTestRun", "Cycle $i")
            results[i] = measure()
            updateSpeed(convertToMbps(results[i]))
        }
        var sum = 0L
        for(i in 3..<10){
            sum += results[i]
        }
        return sum/7
    }

    fun convertToMbps(res: Long): Double {
        return res / 1000000.0
    }

}