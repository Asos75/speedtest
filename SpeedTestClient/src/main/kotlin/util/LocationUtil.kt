package util

import Location
import java.io.BufferedReader
import java.io.InputStreamReader

object LocationUtil {
    private fun getLocationWindows() : Location {
        val geoWatcher = "$"+"GeoWatcher"

        val powerShellScript = """
        Add-Type -AssemblyName System.Device 
        $geoWatcher = New-Object System.Device.Location.GeoCoordinateWatcher
        $geoWatcher.Start()
        
        while (($geoWatcher.Status -ne 'Ready') -and ($geoWatcher.Permission -ne 'Denied')) {
            Start-Sleep -Milliseconds 100 
        }  
        
        if ($geoWatcher.Permission -eq 'Denied'){
            Write-Error 'Access Denied for Location Information'
        } else {
            $geoWatcher.Position.Location | Select-Object -ExpandProperty Latitude 
            $geoWatcher.Position.Location | Select-Object -ExpandProperty Longitude
        }
    """.trimIndent()

        val processBuilder = ProcessBuilder("powershell.exe", "-Command", powerShellScript)
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        var count = 0
        var lat: Double? = null
        var lon: Double? = null
        while (reader.readLine().also { line = it } != null) {
            println(line)
            count++
            when(count){
                1 -> lat = line?.toDouble()
                2 -> lon = line?.toDouble()
            }
        }

        val exitCode = process.waitFor()
        println("Exited with error code $exitCode")
        if(exitCode != 0 || count != 2){
            throw RuntimeException("Error retrieving the location")
        }
        if(lat == null || lon == null ) {
            throw RuntimeException("Error retrieving the location")

        }
        return Location(coordinates = listOf(lat, lon))
    }

    private fun getLocationLinux(): Location {
        //TODO retrieve linux location
        return Location(coordinates = listOf(0.0, 0.0))
    }

    fun getLocation(): Location {
        if(System.getProperty("os.name").startsWith("Windows")){
            return getLocationWindows()
        }

        if(System.getProperty("os.name").startsWith("Linux")){
            return getLocationWindows()
        }
        return Location(coordinates = listOf(0.0, 0.0))
    }
}