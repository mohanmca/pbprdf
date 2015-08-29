package com.stellmangreene.pbprdf.plays.test

import org.openrdf.repository.sail.SailRepository
import org.openrdf.sail.memory.MemoryStore
import org.scalatest.FlatSpec
import org.scalatest.Matchers

import com.stellmangreene.pbprdf.plays.EnterPlay
import com.stellmangreene.pbprdf.util.RdfOperations

/**
 * Test the EnterPlay class
 *
 * @author andrewstellman
 */
class EnterPlaySpec extends FlatSpec with Matchers with RdfOperations {

  behavior of "EnterPlay"

  // As long as each event has unique game and event IDs, they can all go into the same repository
  val rep = new SailRepository(new MemoryStore)
  rep.initialize

  it should "parse enter triples" in {

    var rep = new SailRepository(new MemoryStore)
    rep.initialize

    new EnterPlay("400610636", 101, 1, "8:00", "Sun", "Kelly Faris enters the game for Alyssa Thomas", "21-26").addRdf(rep)

    rep.executeQuery("SELECT * { <http://www.stellman-greene.com/pbprdf/400610636/101> ?p ?o }")
      .map(statement => (s"${statement.getValue("p").stringValue} -> ${statement.getValue("o").stringValue}"))
      .toSet should be(
        Set(
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://www.stellman-greene.com/pbprdf#Event",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://www.stellman-greene.com/pbprdf#Play",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://www.stellman-greene.com/pbprdf#Enters",
          "http://www.stellman-greene.com/pbprdf#period -> 1",
          "http://www.stellman-greene.com/pbprdf#time -> 8:00",
          "http://www.stellman-greene.com/pbprdf#secondsIntoGame -> 120",
          "http://www.stellman-greene.com/pbprdf#team -> Sun",
          "http://www.stellman-greene.com/pbprdf#playerEntering -> Kelly Faris",
          "http://www.stellman-greene.com/pbprdf#playerExiting -> Alyssa Thomas",
          "http://www.w3.org/2000/01/rdf-schema#label -> Sun: Kelly Faris enters the game for Alyssa Thomas"))
  }

}
