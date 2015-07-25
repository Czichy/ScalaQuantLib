package org.scalaquant.core.indexes

import org.joda.time.LocalDate
import org.scalaquant.common.TimeSeries

import scala.collection.concurrent._

object IndexManager {
  trait Storage {

    def hasHistory(name: String): Boolean
    //! returns the (possibly empty) history of the index fixings
    def getHistory(name: String): Option[TimeSeries[LocalDate, Double]]
    //! stores the historical fixings of the index
    def setHistory(name: String, history: TimeSeries[LocalDate, Double]): Unit
    //! observer notifying of changes in the index fixings
    // boost::shared_ptr<Observable> notifier(const std::string& name) const;
    //! returns all names of the indexes for which fixings were stored
    def histories: List[String]
    //! clears the historical fixings of the index
    def clearHistory(name: String): Unit
    //! clears all stored fixings
    def clearHistories(): Unit
  }
  object MemoryStorage extends Storage {
    private val data: Map[String,TimeSeries[LocalDate, Double]] = TrieMap.empty[String, TimeSeries[LocalDate, Double]]
    def hasHistory(name: String): Boolean = data.contains(name.toUpperCase)
    def getHistory(name: String): Option[TimeSeries[LocalDate, Double]] = data.get(name.toUpperCase)
    def setHistory(name: String, history: TimeSeries[LocalDate, Double]): Unit = {
      data.+=(name.toUpperCase -> history)
    }
    def histories: List[String] = data.keySet.toList
    def clearHistory(name: String): Unit = {
      data.remove(name.toUpperCase)
    }
    def clearHistories(): Unit = {
      data.clear()
    }
  }
  //case class SQLStorage() extends Storage

  val emptyIndex = TimeSeries[LocalDate, Double](Nil, Nil)
  lazy val  storage: Storage = MemoryStorage

  def hasHistory(name: String): Boolean = storage.hasHistory(name)
  def getHistory(name: String): Option[TimeSeries[LocalDate, Double]] = storage.getHistory(name)

  def setHistory(name: String, history: TimeSeries[LocalDate, Double]): Unit = {
      storage.setHistory(name, history)
  }
  def histories: List[String] = storage.histories

  def clearHistory(name: String): Unit = {
    storage.clearHistory(name)
  }
  def clearHistories(): Unit = {
    storage.clearHistories()
  }
}
