package edu.stanford.cs193a.raagavi_hsieh64
import java.io.Serializable
import java.util.*

class channelNode:Serializable {
    val description = ""
    val messages = listOf<msgNode>()
    val name = ""
}

class userNode:Serializable {
    val username = ""
    val name = ""
    val email = ""
    val human = false
}
class msgNode:Serializable {
    val from = ""
    val text = ""
    val timestamp = ""
}
