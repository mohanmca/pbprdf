package com.stellmangreene.pbprdf.plays.test

import org.openrdf.repository.sail.SailRepository
import org.openrdf.sail.memory.MemoryStore
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.stellmangreene.pbprdf.plays.TechnicalFoulPlay
import com.stellmangreene.pbprdf.util.RdfOperations
import com.stellmangreene.pbprdf.test.TestUri

/**
 * Test the TechnicalFoulPlay class
 *
 * @author andrewstellman
 */
class TechnicalFoulPlaySpec extends FlatSpec with Matchers with RdfOperations {

  behavior of "TechnicalFoulPlay"

  // As long as each event has unique game and event IDs, they can all go into the same repository
  val rep = new SailRepository(new MemoryStore)
  rep.initialize

  it should "parse a technical foul" in {
    new TechnicalFoulPlay(TestUri.create("400496779"), 93, 2, "7:37", "Mercury", "Diana Taurasi technical foul(1st technical foul)", "21-28").addRdf(rep)

    rep.executeQuery("SELECT * { <http://www.stellman-greene.com/pbprdf/400496779/93> ?p ?o }")
      .map(statement => (s"${statement.getValue("p").stringValue} -> ${statement.getValue("o").stringValue}"))
      .toSet should be(
        Set(
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://www.stellman-greene.com/pbprdf#Event",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://www.stellman-greene.com/pbprdf#Play",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://www.stellman-greene.com/pbprdf#TechnicalFoul",
          "http://www.stellman-greene.com/pbprdf#period -> 2",
          "http://www.stellman-greene.com/pbprdf#time -> 7:37",
          "http://www.stellman-greene.com/pbprdf#secondsIntoGame -> 743",
          "http://www.stellman-greene.com/pbprdf#team -> http://www.stellman-greene.com/pbprdf/teams/Mercury",
          "http://www.stellman-greene.com/pbprdf#foulCommittedBy -> http://www.stellman-greene.com/pbprdf/players/Diana_Taurasi",
          "http://www.stellman-greene.com/pbprdf#technicalFoulNumber -> 1",
          "http://www.w3.org/2000/01/rdf-schema#label -> Mercury: Diana Taurasi technical foul(1st technical foul)"))

  }

  it should "parse a technical foul with no player specified" in {
    new TechnicalFoulPlay(TestUri.create("400496779"), 152, 2, "1:03", "Mercury", "technical foul(2nd technical foul)", "37-32").addRdf(rep)

    rep.executeQuery("SELECT * { <http://www.stellman-greene.com/pbprdf/400496779/152> ?p ?o }")
      .map(statement => (s"${statement.getValue("p").stringValue} -> ${statement.getValue("o").stringValue}"))
      .toSet should be(
        Set(
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://www.stellman-greene.com/pbprdf#Event",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://www.stellman-greene.com/pbprdf#Play",
          "http://www.w3.org/1999/02/22-rdf-syntax-ns#type -> http://www.stellman-greene.com/pbprdf#TechnicalFoul",
          "http://www.stellman-greene.com/pbprdf#period -> 2",
          "http://www.stellman-greene.com/pbprdf#time -> 1:03",
          "http://www.stellman-greene.com/pbprdf#secondsIntoGame -> 1137",
          "http://www.stellman-greene.com/pbprdf#team -> http://www.stellman-greene.com/pbprdf/teams/Mercury",
          "http://www.stellman-greene.com/pbprdf#technicalFoulNumber -> 2",
          "http://www.w3.org/2000/01/rdf-schema#label -> Mercury: technical foul(2nd technical foul)"))

  }

}
