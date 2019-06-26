# TmiK
[![Download](https://api.bintray.com/packages/wooodenleg/maven/TmiK/images/download.svg)](https://bintray.com/wooodenleg/maven/TmiK/_latestVersion)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/03cd61c9bd1f40a2baf416ae1c84ade6)](https://www.codacy.com/app/wooodenleg/TmiK?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=wooodenleg/TmiK&amp;utm_campaign=Badge_Grade)
[![GitHub](https://img.shields.io/github/license/wooodenleg/TmiK.svg?color=blue)](https://github.com/wooodenleg/TmiK/blob/master/LICENSE)  
**T**witch **m**essaging **i**n **K**otlin  
Simple DSL for interacting with Twitch chat

**See the [Documentation](https://github.com/wooodenleg/TmiK/wiki) for more information**

## State
Only **JVM** and **JS** are targeted but I hope I will be able to add **Native** in the future.  
 

## Example
With help of some Kotlin features I was able to create simple DSL:
```kotlin
tmi("oauth:token") {
    + Reconnect(5) // Tries to reconnect for five times if network fails (and re-joins all channels)
 
    onRoomState { println("Joined ${it.channel}") }

    channel("mychannel") {

        broadcaster { // Filters only events by/for broadcaster
            onMessage {
                if (it.message == "hello")
                    this@channel.action("Hello ${it.displayName}")
            }
        }

        filterUserState {
            // Filters events that are not by/for moderators
            withPredicate { !it.isMod }

            onMessage {
                if (it.message.contains("bannedword")) {
                    this@channel.sendMessage("Hey, you can't use that word @${it.displayName}!")
                    this@channel.timeout(it.username)
                }
            }
        }
    }

    onConnected { join("mychannel") }
}
``` 
 
## Installation
You can download this library from bintray
```groovy
repositories {
    maven { url "https://dl.bintray.com/wooodenleg/maven" }
}

dependencies {
    implementation "com.tmik:TmiK-jvm:0.0.7" // For JVM
    // OR
    implementation "com.tmik:TmiK-js:0.0.7" // For Kotlin/JS
}
```
