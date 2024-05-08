package util

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

object IPInfoUtil {
    private fun getIPInfoWindows() : String{
        val powerShellScript = "curl ipinfo.io | Select-Object -Expand Content"
        val processBuilder = ProcessBuilder("powershell.exe", "-Command", powerShellScript)
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        var jsonString = ""

        while (reader.readLine().also { line = it } != null) {
            jsonString += line
        }

        val exitCode = process.waitFor()
        println("Exited with error code $exitCode")
        if(exitCode != 0){
            throw RuntimeException("Error retrieving the location")
        }
        return jsonString
    }

    private fun getIPInfoLinux(): String{
        return ""
        //TODO get ip info linux
    }

    fun getProvider() : String{
        if(System.getProperty("os.name").startsWith("Windows")){
            val jsonString = getIPInfoWindows()
            val jsonObject = JSONObject(jsonString)
            val provider = jsonObject.get("org")
            return provider.toString()
        }
        if(System.getProperty("os.name").startsWith("Linux")){
            val jsonString = getIPInfoLinux()
        }
        return ""
        //TODO handle linux
    }

}