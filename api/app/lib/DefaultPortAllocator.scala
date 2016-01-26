package io.flow.registry.api.lib

import db.{ApplicationsDao, PortsDao}
import io.flow.postgresql.Authorization
import io.flow.registry.v0.models.PortType

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
 *  - blacklist any num ending in 00 (things people randomly are
 *    more likely to use like 7000, 8000, 9000)
 *  - create a blacklist of well known ports that we encounter
 *    (e.g. 8080)
 * 
 * The basic algorithm is to grab the prefix of an application and
 * then to iterate through existing blocks of ports to find an
 * available one. If not found, allocate another block of ports
 * and repeat the process.
 * 
 * @param name The name of the application (e.g. splashpage, splashpage-postgresql) 
 */
case class DefaultPortAllocator(
  name: String,
  applicationType: PortType
) {

  private[this] val Blacklist = Seq(8080)

  private[this] val MinPortNumber = 6000

  private[this] val Blocksize = 10

  private[this] val defaults = Map[PortType, Int](
    PortType.Api -> 1,
    PortType.Ui -> 0,
    PortType.Database -> 9
  )

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
    * name). Will be a num >= 0 and <= 9. If specified, we will try
    * to make sure a port is allocated that ends with this
    * num. Otherwise we just generated the next sequential port
    * num.
    */
  val offset: Option[Int] = defaults.get(applicationType)

  private[this] val applicationBasePorts = ApplicationsDao.findAll(Authorization.All, prefix = Some(prefix)).
    flatMap(_.ports).
    map(_.num).
    map(toBase(_)).
    sorted.
    distinct

  private[this] var i = 0
  private[this] var last: Long = toBase(PortsDao.maxPortNumber().getOrElse(MinPortNumber - Blocksize))

  @scala.annotation.tailrec
  private[this] def nextBlock(): Long = {
    val block = applicationBasePorts.lift(i) match {
      case Some(value) => {
        i += 1
        value
      }
      case None => {
        last = last + Blocksize
        last
      }
    }
    isBlockAvailable(block) match {
      case true => block
      case false => nextBlock()
    }
  }

  /**
    * The base port num (not taking into account the offset)
    */
  @scala.annotation.tailrec
  final def num(): Long = {
    firstAvailablePort(nextBlock(), offset) match {
      case None => num()
      case Some(v) => v
    }
  }

  def firstAvailablePort(min: Long, offset: Option[Int]): Option[Long] = {
    assert(min == toBase(min), s"Min[$min] must be start of block")

    offset match {
      case None => {
        min.until(min + Blocksize + 1).
          filter { v => !defaults.values.toSeq.contains( (v % Blocksize).toInt ) }.
          find { isPortAvailable(_) }
      }
      case Some(value) => {
        Seq(min + value).find { isPortAvailable(_) }
      }
    }
  }

  /**
    * Given a port num (e.g. 8201) returns the base port num
    * (e.g. 8200)
    */
  def toBase(num: Long): Long = {
    num - (num % Blocksize)
  }

  def isPortAvailable(num: Long): Boolean = {
    if (Blacklist.contains(num)) {
      false
    } else {
      PortsDao.findByNumber(Authorization.All, num).isEmpty
    }
  }

  def isBlockAvailable(num: Long): Boolean = {
    if (Blacklist.contains(num)) {
      false
    } else if (num % 100 == 0) {
      false
    } else {
      true
    }
  }

}
