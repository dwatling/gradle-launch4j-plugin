# Gradle Launch4j Plugin

This is a simple Gradle plugin that wraps downloading, extracting, and running [Launch4j](https://launch4j.sourceforge.net/).

It uses the standard XML configuration file that you can read more about [here](https://launch4j.sourceforge.net/docs.html).

## How to use

In your `plugins` section of your `build.gradle` file, add the following:

```gradle
plugins {
    ...
    id("io.watling.gradle.launch4j")
    ...
}
```

You don't need to specify a configuration block if you don't want to. These defaults will be used:

| Property | Default value                                                                                              |
|-|------------------------------------------------------------------------------------------------------------|
|`version`| `3.50`                                                                                                     |
|`downloadUrl`| `https://sourceforge.net/projects/launch4j/files/launch4j-3/$version/launch4j-$version-win32.zip/download` |
|`executable`| `launch4j/launch4jc.exe`                                                                                   |
|`configFile`| `launch4j.xml`                                                                                             |

If you need to override any of these, you can do so in a config block in your `build.gradle` file. Example:

```gradle
...

launch4j {
    version = "3.14"
    configFile = "launch.xml"
}
...
```

Note that `downloadUrl` is a static string and will negate any `version` supplied. In other words, if you just need a specific version of Launch4j and don't want to specify a URL, just override `version`. If you have a preferred place to download Launch4j, then specify it using `downloadUrl`.
