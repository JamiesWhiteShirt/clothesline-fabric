# Clothesline Fabric
[![CurseForge Downloads](http://cf.way2muchnoise.eu/clothesline-fabric.svg)](https://minecraft.curseforge.com/projects/clothesline-fabric)
[![Maven Repository](https://img.shields.io/maven-metadata/v/https/maven.jamieswhiteshirt.com/libs-release/com/jamieswhiteshirt/clothesline-fabric/maven-metadata.xml.svg)](https://maven.jamieswhiteshirt.com/libs-release/com/jamieswhiteshirt/clothesline-fabric/)

A seamless laundry experience that is definitely not an item transport mod for [Fabric](https://fabricmc.net/).

- [rtree-3i-lite-fabric](https://github.com/JamiesWhiteShirt/rtree-3i-lite-fabric): Provides a spatial indexing library used by Clothesline Fabric.

## Developing Clothesline Fabric

To get started, refer to the [Fabric documentation](https://fabricmc.net/wiki/setup).

## Usage

To use this mod in your workspace, add the following to your `build.gradle`:

```groovy
repositories {
    maven {url "https://maven.jamieswhiteshirt.com/libs-release/"}
}

dependencies {
    modCompile "com.jamieswhiteshirt:clothesline-fabric:<CLOTHESLINE_FABRIC_VERSION>"
}
```

Clothesline has an API, but it is currently unstable and with limited functionality.
The API is located in the `com.jamieswhiteshirt.clothesline.api` package.

To get started, get the network manager of a World by casting to the `NetworkManagerProvider` interface and call `getNetworkManager`.
Example:

```java
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.api.NetworkManager;

class Example {
    void example(World world) {
        NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
        /* ... */
    }
}
```
