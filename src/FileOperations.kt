import java.io.File
import com.sun.deploy.cache.Cache.copyFile
import org.codehaus.plexus.util.FileUtils
import java.util.HashMap


object FileOperations {
    interface FileT {
        fun onFile(file: File): File
        fun onFolder(folder: File): File
    }


    fun traverseAll(parent: File, listener: FileT) {
        traverseAll(parent, listener, -1)
    }

    //depth -1 ALL
    //depth 0 NIL
    //depth 1 files
    //depth 2 files, folders 1
    fun traverseAll(parent: File, listener: FileT, depth: Int) {
        if (depth == 0) return

        val children = parent.listFiles()
        if (children != null) {
            for (thisChild in children) {
                if (thisChild.isDirectory) {
                    traverseAll(listener.onFolder(thisChild), listener, depth - 1)
                } else {
                    listener.onFile(thisChild)
                }
            }
        }
    }

    fun copyDifferentFolderFilesIntoOne(mergedFolderStr: String,
                                        vararg foldersStr: String) {
        val mergedFolder = File(mergedFolderStr)
        val filesMap = HashMap<String, File>()
        for (folder in foldersStr) {
            updateFilesMap(File(folder), filesMap, null)
        }

        for ((relativeName, srcFile) in filesMap) {
            FileUtils.copyFile(srcFile, File(mergedFolder, relativeName))
        }
    }

    private fun updateFilesMap(baseFolder: File, filesMap: MutableMap<String, File>,
                               relativeName: String?) {
        for (file in baseFolder.listFiles()!!) {
            val fileRelativeName = getFileRelativeName(relativeName, file.name)

            if (file.isDirectory) {
                updateFilesMap(file, filesMap, fileRelativeName)
            } else {
                val existingFile = filesMap[fileRelativeName]
                if (existingFile == null) {
                    filesMap[fileRelativeName] = file
                } else {
                    //filesMap[]=file
                }
            }
        }
    }

    private fun getFileRelativeName(baseName: String?, fileName: String): String {
        return if (baseName == null) fileName else "$baseName/$fileName"
    }

}