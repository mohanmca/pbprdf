package com.stellmangreene.pbprdf

import org.openrdf.repository.Repository
import org.openrdf.repository.sail.SailRepository
import org.openrdf.sail.memory.MemoryStore
import com.typesafe.scalalogging.LazyLogging
import java.io.FileInputStream
import java.io.File
import org.xml.sax.InputSource
import com.stellmangreene.pbprdf.model._
import com.stellmangreene.pbprdf.util.XmlHelper
import scala.util.Try
import scala.util.Success
import java.io.FileOutputStream
import com.stellmangreene.pbprdf.util.RdfOperations

object PbpRdfApp extends App with LazyLogging with RdfOperations {

  def printUsageAndExit(message: Option[String] = None) = {
    if (message.isDefined)
      println(message.get)
    println("""usage: pbprdf folder [filename.ttl]
  Read all of the files in the folder and attempt to process them
  Write all plays for each game to stdout, or a file if specified
  
pbprdf --ontology [filename.ttl]
  Write the ontology to stout, or a file if specified""")
    System.exit(1)
  }

  if (args.size != 1 && args.size != 2) {
    printUsageAndExit()
  } else {

    val outputFile =
      if (args.size >= 2) {
        if (new File(args(1)).exists)
          printUsageAndExit(Some(s"File already exists, will not overwrite: ${args(1)}"))
        Some(args(1))

      } else {
        None

      }

    if (args(0) == "--ontology") {

      logger.info("Writing ontology statements")
      OntologyRdfRepository.rep.writeAllStatements(outputFile)
      
    } else {

      var folder: File = null
      val inputFolderPath = args(0)
      Try(new File(inputFolderPath)) match {
        case Success(f) => { folder = f }
        case _          => printUsageAndExit(Some(s"Unable to open folder: ${inputFolderPath}"))
      }

      if (!folder.exists || !folder.isDirectory)
        printUsageAndExit(Some(s"Invalid folder: ${inputFolderPath}"))

      val files = folder.listFiles
      if (files.isEmpty)
        printUsageAndExit(Some(s"No files found in folder: ${inputFolderPath}"))

      logger.info(s"Reading ${files.size} files from folder ${inputFolderPath}")

      var rep = new SailRepository(new MemoryStore)
      rep.initialize

      var i = 0
      files.foreach(file => {
        i += 1
        logger.debug(s"Reading plays from ${file.getCanonicalPath} (file ${i} of ${files.size})")
        val xmlStream = new FileInputStream(file)
        val rootElem = XmlHelper.parseXml(xmlStream)
        try {
          val playByPlay: PlayByPlay = new EspnPlayByPlay(rootElem, file.getCanonicalPath)
          playByPlay.addRdf(rep)
        } catch {
          case e: InvalidPlayByPlayException => {
            logger.error(s"Error reading play-by-play: ${e.getMessage}")
          }
        }
      })

      logger.info("Finished reading files")

      rep.writeAllStatements(outputFile)

      logger.info(s"Finished writing Turtle")
    }
  }

}
