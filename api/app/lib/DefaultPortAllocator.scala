package io.flow.registry.api.lib

import javax.inject.{Inject, Singleton}

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
 * The basic algorithm is to grab the prefix of an application and
 * then to iterate through existing blocks of ports to find an
 * available one. If not found, allocate another block of ports
 * and repeat the process.
 *
 */
@Singleton
class DefaultPortAllocator @Inject() (
  applicationsDao: ApplicationsDao,
  portsDao: PortsDao
) {

  private[this] val Blacklist = Seq(8080L)

  private[this] val MinPortNumber = 6000

  private[this] val BlockSize = 10L

  private[this] val defaults = Map[String, Int](
    "nodejs" -> 0,
    "play" -> 1,
    "postgresql" -> 9
  )

  private[this] def prefix(name: String) = {
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
  def offset(serviceName: String): Option[Int] = defaults.get(serviceName)

  private[this] def applicationBasePorts(name: String): Seq[Long] = applicationsDao.findAll(Authorization.All, prefix = Some(prefix(name))).
    flatMap(_.ports).
    map(_.external).
    map(toBase(_)).
    sorted.
    distinct

  private[this] var i = 0
  private[this] var last: Long = toBase(portsDao.maxExternalPortNumber().getOrElse(MinPortNumber - BlockSize))

  @scala.annotation.tailrec
  private[this] def nextBlock(name: String): Long = {
    val block = applicationBasePorts(name)lift(i) match {
      case Some(value) => {
        i += 1
        value
      }
      case None => {
        last = last + BlockSize
        last
      }
    }
    if (isBlockAvailable(block)) {
      block
    } else {
      nextBlock(name)
    }
  }

  /**
    * The base port number (not taking into account the offset)
    *
    * @param name The name of the application (e.g. splashpage, splashpage-postgresql)
    * @param serviceName The name of the service (e.g. postgresql, nodejs, etc.)
    */
  @scala.annotation.tailrec
  final def number(name: String,
                   serviceName: String): Long = {
    firstAvailablePort(nextBlock(name), offset(serviceName)) match {
      case None => number(name, serviceName)
      case Some(v) => v
    }
  }

  private def firstAvailablePort(min: Long, offset: Option[Int]): Option[Long] = {
    assert(min == toBase(min), s"Min[$min] must be start of block")

    offset match {
      case None => {
        min.until(min + BlockSize + 1).
          filter { v => !defaults.values.toSeq.contains( (v % BlockSize).toInt ) }.
          find { isPortAvailable }
      }
      case Some(value) => {
        Seq(min + value).find { isPortAvailable }
      }
    }
  }

  /**
    * Given a port number (e.g. 8201) returns the base port number
    * (e.g. 8200)
    */
  protected[lib] def toBase(number: Long): Long = {
    number - (number % BlockSize)
  }

  protected[lib] def isPortAvailable(number: Long): Boolean = {
    if (Blacklist.contains(number)) {
      false
    } else {
      portsDao.findByExternal(Authorization.All, number).isEmpty
    }
  }

  protected[lib] def isBlockAvailable(number: Long): Boolean = {
    if (Blacklist.contains(number)) {
      false
    } else if (number % 100 == 0) {
      false
    } else {
      true
    }
  }

}
