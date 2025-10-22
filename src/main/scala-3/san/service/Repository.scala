package san.service

// Generic repository trait defining data access contract
trait Repository[ID, T]:
  def all: Seq[T]
  def add(id: ID, t: T): Unit
  def upsert(id: ID, t: T): Unit
  def get(id: ID): Option[T]
  def remove(id: ID): Unit
  def clear(): Unit

import scala.collection.mutable

final class InMemoryRepository[ID, T] extends Repository[ID, T]:
  private val store = mutable.LinkedHashMap.empty[ID, T]
  // Get all stored entities as a sequence
  def all: Seq[T] = store.values.toSeq
  // Add entity to storage
  def add(id: ID, t: T): Unit = store.update(id, t)
  // Insert or update entity in storage
  def upsert(id: ID, t: T): Unit = store.update(id, t)
  // Retrieve entity by ID
  def get(id: ID): Option[T] = store.get(id)
  // Remove entity by ID
  def remove(id: ID): Unit = store.remove(id)
  // Remove all entities from storage
  def clear(): Unit = store.clear()
