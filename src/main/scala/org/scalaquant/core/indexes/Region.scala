package org.scalaquant.core.indexes


abstract class Region {
  protected val data: Region.Data
  def name: String = data.name
  def code: String = data.code

  def ==(other: Region): Boolean = this.name == other.name
  def !=(other: Region): Boolean = this.name != other.name
}

class CustomRegion(name: String, code: String) extends Region{
  protected val data = Region.Data(name,code)
}
class AustraliaRegion extends Region{
  protected val data = Region.AUData
}

class EURegion extends Region{
  protected val data = Region.EUData
}

class FranceRegion extends Region{
  protected val data = Region.FRData
}

class UKRegion extends Region{
  protected val data = Region.UKData
}


class USRegion extends Region{
  protected val data = Region.USData
}

class ZARegion extends Region{
  protected val data = Region.ZAData
}

object Region{

  private[pacakge] case class Data(name :String, code: String)

  private[pacakge] val AUData = Data("Australia","AU")
  private[pacakge] val EUData = Data("EU","EU")
  private[pacakge] val FRData = Data("France","FR")
  private[pacakge] val UKData = Data("UK","UK")
  private[pacakge] val USData = Data("USA","US")
  private[pacakge] val ZAData = Data("South Africa","ZA")
}