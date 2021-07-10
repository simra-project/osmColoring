package main

import Graph.SegmentMapper.doSegmentMapping
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger()

fun main(args: Array<String>) {
    val cla = mainBody { ArgParser(args).parseInto(::CommandLineArguments) }

    logger.info(cla.toString())

    cla.toMetaFile()

    val start = System.currentTimeMillis();

    doSegmentMapping(cla)

    val finish = System.currentTimeMillis();
    val timeElapsed = finish - start

    logger.info("Mapping took ${timeElapsed} milliseconds.")
}