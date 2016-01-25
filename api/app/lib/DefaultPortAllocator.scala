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
 */
case class DefaultPortAllocator(
  name: String
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

  private[this] val applicationBasePorts = ApplicationsDao.findAll(Authorization.All, prefix = Some(prefix)).
    flatMap(_.ports).
    map(_.number).
    map(toBase(_)).
    sorted.
    distinct

  private[this] var i = 0
  private[this] var last: Long = toBase(PortsDao.maxPortNumber().getOrElse(MinPortNumber - Blocksize))

  private[this] def nextBlock(): Long = {
    applicationBasePorts.lift(i) match {
      case Some(value) => {
        i += 1
        value
      }
      case None => {
        last = last + Blocksize
        last
      }
    }
  }

  /**
    * The base port number (not taking into account the offset)
    */
  def number(): Long = {
    val start = nextBlock()
    val port: Option[Long] = offset match {
      case None => firstAvailablePort(start, start + Blocksize - 1)
      case Some(offset) => firstAvailablePort(start + offset, start + offset)
    }
    port.getOrElse {
      number()
    }
  }

  def firstAvailablePort(min: Long, max: Long): Option[Long] = {
    min.until(max + 1).find { isPortAvailable(_) }
  }

  /**
    * Given a port number (e.g. 8201) returns the base port number
    * (e.g. 8200)
    */
  def toBase(number: Long): Long = {
    number - (number % Blocksize)
  }

  def isPortAvailable(number: Long): Boolean = {
    if (Blacklist.contains(number)) {
      false
    } else if (number % 100 == 0) {
      false
    } else {
      PortsDao.findByNumber(Authorization.All, number).isEmpty
    }
  }

}
