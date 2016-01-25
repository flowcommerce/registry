package io.flow.registry.api.lib

import db.{ApplicationsDao, PortsDao}
import io.flow.postgresql.Authorization

/**
 * Parses the name of the application and allocates the default port
 * to that application. Basic approach is to recognize a few suffixes
 * (e.g. -postgresql) and allocate ports consistently to those
 * suffixes, while reserving blocks of 10 ports for each prefix.
 *
 * Some basic rules we implemented to minimize probability of port
 * collissions with external software
 * 
 *  - start at 6000 ( > postgresql port )
 *  - blacklist any number ending in 00 (things people randomly are
 *    more likely to use like 7000, 8000, 9000)
 *  - create a blacklist of well known ports that we encounter
 *    (e.g. 8080)
 * 
 * @param name The name of the application (e.g. splashpage, splashpage-postgresql) 
 * @param largestPortAllocated The value of the largest port allocated to
 *        date (or None if no ports have been allocated)
 */
case class DefaultPortAllocator(
  name: String,
  largestPortAllocated: Option[Long]
) {

  private[this] val Blacklist = Seq(8080)

  private[this] val MinPortNumber = 6000

  private[this] val Blocksize = 10

  private[this] val defaults = Map(
    "postgresql" -> 9
  )

  private[this] val suffix = name.trim.split("-").last
  private[this] val prefix = {
    val idx = name.trim.lastIndexOf("-")
    if (idx < 0) {
      name.trim
    } else {
      name.trim.substring(0, idx)
    }
  }

  /**
    * The port offset for this type of application (based on its
    * name). Will be a number >= 0 and <= 9. If specified, we will try
    * to make sure a port is allocated that ends with this
    * number. Otherwise we just generated the next sequential port
    * number.
    */
  val offset: Option[Int] = defaults.get(suffix)

  /**
    * The base port number (not taking into account the offset)
    */
  def number: Long = {
    val existingBasePorts = ApplicationsDao.findAll(Authorization.All, prefix = Some(prefix)).
      flatMap(_.ports).
      map(_.number).
      map(toBase(_)).
      sorted

    val block = existingBasePorts.headOption.getOrElse {
      nextAvailableBlock(
        largestPortAllocated match {
          case None => MinPortNumber
          case Some(num) => toBase(num) + Blocksize
        }
      )
    }

    offset match {
      case None => nextAvailablePort(block)
      case Some(offset) => nextAvailablePort(block + offset, Blocksize)
    }
  }

  /**
    * Given a port number (e.g. 8201) returns the base port number
    * (e.g. 8200)
    */
  def toBase(number: Long): Long = {
    number - (number % Blocksize)
  }

  @scala.annotation.tailrec
  private[this] def nextAvailableBlock(block: Long): Long = {
    assert(block % Blocksize == 0, s"Invalid block[$block]")
    assert(block >= MinPortNumber, s"Block[$block] must be greater than MinPortNumber[$MinPortNumber]")
    if (Blacklist.contains(block)) {
      nextAvailableBlock(block + Blocksize)
    } else if (block % 100 == 0) {
      nextAvailableBlock(block + Blocksize)
    } else {
      block
    }
  }

  @scala.annotation.tailrec
  private[this] def nextAvailablePort(port: Long, increment: Integer = 1): Long = {
    isPortAvailable(port) match {
      case true => port
      case false => nextAvailablePort(port + increment)
    }
  }

  private[this] def isPortAvailable(number: Long): Boolean = {
    PortsDao.findByNumber(Authorization.All, number).isEmpty
  }

}
