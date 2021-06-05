package main

import Graph.SegmentMapper.doSegmentMapping
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import main.CommandLineArguments
import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger()

fun main(args: Array<String>) {
    val cla = mainBody { ArgParser(args).parseInto(::CommandLineArguments) }

    logger.info(cla.toString())

    cla.toMetaFile()

    doSegmentMapping(cla)
}