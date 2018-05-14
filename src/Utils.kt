import java.io.File
import java.util.regex.Pattern

object Utils {

    fun renameFile(oldName: File, newName: File): Boolean {
        var newName = newName
        var i = 0
        while (newName.exists()) {
            i++
            if (i > 0) {
                var regExPattern = "\\[\\d{4}\\]\$"
                val pattern = Pattern.compile(regExPattern)
                val matcher = pattern.matcher(newName.nameWithoutExtension)

                var yearOfFilm = ""
                while (matcher.find()) {
                    yearOfFilm = matcher.group()
                }
                var newNameWithOutYear = newName.nameWithoutExtension.replace(Regex(regExPattern), "")
                var newNameWithExtension = (newNameWithOutYear + " (" + ++i + ") " + yearOfFilm + "." + newName.extension).trim().replace("\\s+".toRegex(), " ")
                newName = File(newName.parentFile.path + "\\" + newNameWithExtension)
            }
        }

        return oldName.renameTo(newName)
    }

    fun renameFolder(oldName: File, newName: File): Boolean {
        var newName = newName
        if (newName.exists()) {
            mergeTwoDirectories(newName, oldName)
        } else
            return oldName.renameTo(newName)
        return false
    }

    fun mergeTwoDirectories(dir1: File, dir2: File) {
        val targetDirPath = dir1.absolutePath
        val files = dir2.listFiles()
        for (file in files!!) {
            renameFile(file, (File(targetDirPath + File.separator + file.name)))
            //file.renameTo(File(targetDirPath + File.separator + file.name))
        }
    }

    fun replaceLast(string: String, substring: String, replacement: String): String {
        val index = string.lastIndexOf(substring)
        return if (index == -1) string else string.substring(0, index) + replacement + string.substring(index + substring.length)
    }
}