import java.io.File
import java.util.regex.Pattern
import java.security.DigestInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest


object Utils {

    fun renameFile(oldName: File, newName: File): Boolean {
        if (areBothFilesSame(oldName, newName)) return false
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
                var newNameWithOutYear = newName.nameWithoutExtension.replace(Regex(regExPattern), "").trim().replace("\\s+".toRegex(), " ")
                var newNameWithExtension = (newNameWithOutYear + " (" + ++i + ") " + yearOfFilm + "." + newName.extension).trim()
                newName = File(newName.parentFile.path + "\\" + newNameWithExtension)
            }
        }

        return oldName.renameTo(newName)
    }

    private fun areBothFilesSame(oldName: File, newName: File): Boolean {
        if (!oldName.exists()) return false
        if (!newName.exists()) return false
        if (newName.isDirectory) return false
        if (oldName.isDirectory) return false

        val messageDigest1 = MessageDigest.getInstance("MD5")
        var inputStream1: InputStream = FileInputStream(oldName)
        val messageDigest2 = MessageDigest.getInstance("MD5")
        var inputStream2: InputStream = FileInputStream(newName)

        try {
            inputStream1 = DigestInputStream(inputStream1, messageDigest1)
            inputStream2 = DigestInputStream(inputStream2, messageDigest2)
        } finally {
            inputStream1.close()
            inputStream2.close()
        }

        return messageDigest1.digest()!!.contentEquals(messageDigest2.digest())
    }

    fun renameFolder(oldName: File, newName: File): Boolean {
        var newName = newName
        if (newName.exists()) {
            mergeTwoDirectories(newName, oldName)
        } else
            return oldName.renameTo(newName)
        return false
    }

    fun mergeTwoDirectories(target: File, source: File) {
        val targetDirPath = target.absolutePath
        val sourceFiles = source.listFiles()
        for (file in sourceFiles!!) {
            renameFile(file, (File(targetDirPath + File.separator + file.name)))
            //file.renameTo(File(targetDirPath + File.separator + file.name))
        }
    }

    fun replaceLast(string: String, substring: String, replacement: String): String {
        val index = string.lastIndexOf(substring)
        return if (index == -1) string else string.substring(0, index) + replacement + string.substring(index + substring.length)
    }
}
