import java.io.File
import java.util.*

object Main {
    private const val SEPARATOR = "\\"

    @JvmStatic
    fun main(args: Array<String>) {
        startProcess("C:\\Users\\amalg\\Desktop\\Christener\\SRC")
    }

    private fun startProcess(path: String) {
        YearProcessor.start(File(path))
    }


    private fun filterFromDictionary(fileName: String): String {
        var fileName = fileName
        val dictionary = HashSet<String>()
        dictionary.add("[]")
        for (thisString in dictionary) {
            fileName = fileName.replace(thisString, " ")
        }
        return fileName.trim { it <= ' ' }
    }


}