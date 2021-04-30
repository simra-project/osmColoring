package main

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.InvalidArgumentException
import com.xenomachina.argparser.default
import java.io.File

class CommandLineArguments(parser: ArgParser) {

    val simraRoot by parser
        .storing("-s", "--simraRoot", help = "path to the SimRa dataset root") { File(this) }
        .default(File("./simra_data"))
        .addValidator {
            if (!value.exists()) {
                throw InvalidArgumentException("${value.absolutePath} does not exist")
            }
            if (!value.isDirectory) {
                throw InvalidArgumentException("${value.absolutePath} is not a directory")
            }
        }

    val region by parser
        .storing("-r", "--region", help = "SimRa region to parse")
        .default("Leipzig")
        .addValidator {
            require(simraRoot.listFiles()!!.toList().map { it.nameWithoutExtension }.contains(value)) {
                "SimRa root folder ${simraRoot.absolutePath} does not contain region $value"
            }
        }

    /*****************************************************************
     * Generated methods
     ****************************************************************/

    override fun toString(): String {
        return "CommandLineArguments(simraRoot=${simraRoot.absolutePath}, region='$region')"
    }

}