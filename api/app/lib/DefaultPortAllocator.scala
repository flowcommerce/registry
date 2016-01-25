package io.flow.registry.api.lib

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

  val number: Long = {
    val nextBlock = largestPortAllocated match {
      case None => MinPortNumber
      case Some(num) => num - (num % Blocksize) + Blocksize
    }
    nextAvailableBlock(nextBlock) + offset()
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

  /**
    * Returns the port offset - a number between 0 and 9
    */
  def offset(): Int = {
    val suffix = name.trim.split("-").last
    defaults.get(suffix).getOrElse(0)
  }

}
