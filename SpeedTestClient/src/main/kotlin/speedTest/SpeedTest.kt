package speedTest

import java.net.URI
import java.time.Duration
import java.time.LocalTime
import kotlin.random.Random

const val MIN_VALUE = 0
const val MAX_VALUE = 100

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

    private fun showResults() {
        duration = Duration.between(startTime, endTime).toMillis()
        val bitsLoaded = downloadSize * 8
        val speedBps = bitsLoaded / duration!!
        println(speedBps)
    }

    fun measure(imgAddress: String, downloadSize: Long) {
        this.imgAddress = imgAddress
        this.downloadSize = downloadSize
        downloadImg()
        showResults()
    }

    fun measure() {
        imgAddress =
            "https://wallpaperswide.com/download/shadow_of_the_tomb_raider_2018_puzzle_video_game-wallpaper-7680x4800.jpg?n=${
                Random.nextInt(
                    MIN_VALUE,
                    MAX_VALUE
                )
            }"
        downloadImg()
        showResults()
    }

    fun measureCycle(): Long {
        //Doing 10 cycles to account for tcp ramp up
        for (i in 1..10) {
            measure()
        }
        return 0
    }
}