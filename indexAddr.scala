object MemoryIndexing {
  def main(args: Array[String]): Unit = {
    val totalWrites = 64
    val writesBeforeSkip = 8
    val skipAddresses = 4

    // This will hold the final memory addresses to write to
    val memoryAddresses = new scala.collection.mutable.ListBuffer[Int]()
    println(s"memoryAddresses is ${memoryAddresses} \n")

    // Index to keep track of the current address
    var currentAddress = 0

    for (_ <- 1 to totalWrites) {
      // Write to the next memory address
      memoryAddresses += currentAddress
      currentAddress += 1

      // Check if we need to skip some addresses
      if (memoryAddresses.length % writesBeforeSkip == 0) {
        currentAddress += skipAddresses
      }
    }

    // Print the memory addresses
    println(memoryAddresses.mkString(", "))
  }
}

