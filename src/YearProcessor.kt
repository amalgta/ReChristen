import java.io.File
import java.time.LocalDateTime
import java.util.regex.Pattern

object YearProcessor {
    fun start(parent: File) {
        FileOperations.traverseAll(
                parent,
                object : FileOperations.FileT {
                    override fun onFile(file: File): File {
                        /*
                        if (Constants.ignoredFiles.contains(Utils.getExtension(file.name))) return file
                        val newFile = processFile(file)
                        Utils.renameFile(file, newFile)
                        */
                        return file
                    }

                    override fun onFolder(folder: File): File {
                        return processFolder(folder)
                    }

                }
        )
    }

    private fun stripYearFromFolderAndChildren(folder: File): String {
        val yearsFromFolderName = stripYear(folder.name)
        val filesAndYears: HashMap<File, HashMap<String, MutableList<Int>>> = hashMapOf()

        FileOperations.traverseAll(
                folder,
                object : FileOperations.FileT {
                    override fun onFile(file: File): File {
                        if (!isGoodFileExtension(file.extension)) return file

                        val yearsFromFile = stripYear(file.name)
                        if (yearsFromFile.isNotEmpty()) {
                            filesAndYears[file] = yearsFromFile
                        }
                        return file
                    }

                    override fun onFolder(folder: File): File {
                        return folder
                    }

                }
                , 1)

        val years: HashSet<String> = hashSetOf()

        for (thisElement in yearsFromFolderName) {
            years.add(thisElement.key)
        }
        for (thisFileAndYear in filesAndYears) {
            //print(thisFileAndYear.key.name + " ")
            for (thisYearAndPositions in thisFileAndYear.value) {
                //print(thisYearAndPositions.key + " ")
                years.add(thisYearAndPositions.key)
            }
            //println()
        }

        for (thisYear in years) {
            if (!isGoodYear(thisYear))
                years.remove(thisYear)
        }

        if (years.isNotEmpty()) {
            if (years.size == 1) {
                return years.elementAt(0)
            } else {

                var userInput: Int
                do {
                    var options = ""
                    for (i in 0 until years.size step 1) {
                        options += ("${i + 1}.${years.elementAt(i)} ")
                    }
                    print(folder.name + "? " + options)

                    userInput = Integer.parseInt(readLine().toString()) - 1

                } while (userInput !in 0 until years.size)

                return years.elementAt(userInput)
            }
        } else {
            return ""
        }
    }

    private fun processFolder(folder: File): File {
        if (!isFilmLeaf(folder)) return folder
        if (!containsRequiredFiles(folder)) return folder

        var target = folder.name

        val yearOfFilm = stripYearFromFolderAndChildren(folder)

        if (yearOfFilm.isNotEmpty()) {
            target.replace(Regex("\\[$yearOfFilm]\$"), "")
            target.replace(Regex(yearOfFilm), "")
            target = target.plus("[$yearOfFilm]")
        }

        val targetFolder = File(folder.parentFile.path + "\\" + target)
        Utils.renameFolder(folder, targetFolder)

        return folder
    }

    private fun isFilmLeaf(folderName: File): Boolean {
        var count = 0

        FileOperations.traverseAll(folderName, object : FileOperations.FileT {
            override fun onFile(file: File): File {
                return file
            }

            override fun onFolder(folder: File): File {
                if (containsRequiredFiles(folder))
                    count++
                return folder
            }

        }, 2)
        return (count <= 0)
    }

    private fun containsRequiredFiles(folder: File): Boolean {
        var containsFiles = false
        FileOperations.traverseAll(folder, object : FileOperations.FileT {
            override fun onFile(file: File): File {
                if (!containsFiles) {
                    if (isGoodFileExtension(file.extension))
                        containsFiles = true
                }
                return file
            }

            override fun onFolder(folder: File): File {
                return folder
            }

        }, 1)
        return containsFiles;
    }

    private fun isGoodFileExtension(extension: String): Boolean {
        if (!Constants.ignoredFiles.contains(extension) && !Constants.goodFiles.contains(extension)) {
            var userInput: String
            do {
                print("Is this a good file extension $extension? y/n : ")
                userInput = readLine().toString()
                when (userInput) {
                    "y" -> Constants.goodFiles.add(extension)
                    "n" -> Constants.ignoredFiles.add(extension)
                }
            } while (userInput != "y" && userInput != "n")
        }

        if (Constants.ignoredFiles.contains(extension)) return false
        if (Constants.goodFiles.contains(extension)) return true
        return false
    }

    private fun stripYear(fileName: String, pattern: String): HashMap<String, MutableList<Int>> {
        val pattern = Pattern.compile(pattern)
        val matcher = pattern.matcher(fileName)

        var result: HashMap<String, MutableList<Int>> = hashMapOf()
        while (matcher.find()) {
            if (result.containsKey(matcher.group())) {
                result[matcher.group()]?.add(matcher.start())
            } else {
                result[matcher.group()] = mutableListOf(matcher.start())
            }
        }
        return result
    }

    private fun stripYear(fileName: String): HashMap<String, MutableList<Int>> {
        return stripYear(fileName, "\\d{4}")
    }

    private fun isGoodYear(substring: String): Boolean {
        val startYear = 1900
        val endYear = LocalDateTime.now().year + 1
        return Integer.parseInt(substring) in startYear..endYear
    }
}
