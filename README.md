# TmiK
[![Download](https://api.bintray.com/packages/wooodenleg/maven/TmiK/images/download.svg?version=latest)](https://bintray.com/wooodenleg/maven/TmiK/_latestVersion)
[![Download](https://api.bintray.com/packages/wooodenleg/maven/TmiK-experimental/images/download.svg?version=latest)](https://bintray.com/wooodenleg/maven/TmiK-experimental/_latestVersion)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/03cd61c9bd1f40a2baf416ae1c84ade6)](https://www.codacy.com/app/wooodenleg/TmiK?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=wooodenleg/TmiK&amp;utm_campaign=Badge_Grade)
[![GitHub](https://img.shields.io/github/license/wooodenleg/TmiK.svg?color=blue)](https://github.com/wooodenleg/TmiK/blob/master/LICENSE)  
**T**witch **m**essaging **i**n **K**otlin  
Simple DSL for interacting with Twitch chat

**See the [Documentation](https://github.com/wooodenleg/TmiK/wiki) for more information**

## State
Only **JVM** and **JS** are targeted but I hope I will be able to add **Native** in the future.  
 
## Example
Example of simple bot
```kotlin
tmi(token) {
    + Reconnect(5) // Tries to reconnect for five times if network fails (and re-joins all channels)

    channel("MyChannel") {

        // Convenient way of listening to commands
        commands('!') {
            moderators { 
                "uptime" receive { // on "!uptime" command from moderator
                    sendMessage("Stream has been running for ${getUptime()} minutes")
                }
            }
            
            "|h,help|" {
                onReceive { // on "!h" or "!help" 
                    // Whisper back to user who sent the command using context 
                    whisper("Psst, ask me about shedule using \"!schedule {day}\"")
                }
                "schedule {day}" receive { paramaters -> // e.g. "!h schedule monday
                    val day = paramaters["day"]
                    sendMessage("Stream starts in ${getShedule(day)} on $day")
                }
            }
        }

        // Or use just plain old listeners

        onMessage {
            println("Message from channel $channel received: $text")
        }

        subscribers {
            onSubGift {
                sendMessage("Awwww, subs giving subs <3")
            }
        }

        onRaid {
            sendMessage("Hello ${message.sourceDisplayName}! Thanks for the raid!")
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
    implementation "com.tmik:TmiK-jvm:$version" // For JVM
    // OR
    implementation "com.tmik:TmiK-js:$version" // For JS
}
```
