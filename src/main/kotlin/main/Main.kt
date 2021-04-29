import Graph.SegmentMapper.doSegmentMapping
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import main.CommandLineArguments

fun main(args: Array<String>) {
    val cla = mainBody { ArgParser(args).parseInto(::CommandLineArguments) }

    println("Hello World!")
    doSegmentMapping(cla)
    print("Bye!")
}