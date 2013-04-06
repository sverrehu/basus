Basus
=====

A simple environment for teaching/learning programming.  Intended to
fill the gap between graphical languages, like Scratch, and the real
thing.

If you just want the program, not the source code, go to
http://basus.no

Written by Sverre H Huseby, Norway.  Project started in 2008.

----

I'm not really ready to make the code public yet, but decided to do so
anyway due to several requests.

A few notes to whoever happens to pass by:

  * Maven-based Java project.

  * I started restructuring stuff about a year ago, without ever
    coming around to finish it.  Some stuff may be in illogical
    locations.

  * Uses other jar files from your's truly.  All of them should be
    pulled automatically from my maven repo.  If not, please let me
    know.

  * I really never intended this to be a collaboration project, so
    there is no developer documentation.

  * Staring point for launching the IDE: no.shhsoft.basus.Main

  * Build and run from command line:

        mvn clean package
        java -jar full/target/basus-1.3-SNAPSHOT.jar

  * No, I'm not using Proguard to obfuscate the code, I'm using it to
    keep the executable small in order to speed up download times.


Sverre.
