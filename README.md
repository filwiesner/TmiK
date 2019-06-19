# TmiK
Twitch messaging in Kotlin - Simple DSL for interacting with Twitch chat

## State
This *library* is **very** unstable. On JVM it's better than JS but still unreliable and buggy. 
WebSocket communication depends on [Ktor](https://github.com/ktorio/ktor) 
(and that's mostly the reason why it's unstable)  
*Only* **JVM** and **JS** are targeted but I hope I will be able to add **Native** in the future.

## Example
With help of some Kotlin features I was able to create simple DSL:
```kotlin
tmi("oauth:token") {

    onConnected {
        join("someChannel")
    }

    channel("someChannel") {
        owner {
            onMessage {
                this@channel.sendMessage("Hello master")
            }
        }

        user("naughtyUser69") {
            onClearMessage {
                this@channel.sendMessage("Hey, this is the last straw!")
                this@channel.ban(user)
            }
        }
    }
}
``` 
 
## Installation
I will *release* the library to maven when there will be something to release and when I learn
how to do it :)  
For now the only way is downloading the code
