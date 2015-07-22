# Fim

## Table of contents

  * [File Integrity Manager](#file-integrity-manager)
  * [Fim workflow](#fim-workflow)
  * [Usage](#usage)
  * [Simple example](#simple-example)
  * [Real life example](#real-life-example)
  * [License](https://github.com/evrignaud/fim/blob/master/LICENSE.md)

## File Integrity Manager

Fim manage the integrity of a complete file tree.

Ensuring the integrity of a files can be done in several ways. The two classical ways are:

-  manage your files using a VCS or a DVCS.
-  put all your file into an archive that will ensure the integrity of all the files.

When you have gigabytes of videos or photos or other kind of binary files you cannot put all of them into a VCS or into an archive.

The solution provided by Fim is to manage an index of the files that contains for each file:

- filename
- file length
- modification date
- hash of the first four kilobytes of the file content
- hash of the first megabyte of the file content
- hash of the full file content

The hash algorithm that is used is SHA-512.

This index is called a `State` and acts like the Central Directory does for a Zip file.
All the data generated by Fim is stored into the `.fim` root directory.

You can use Fim to ensure the integrity of a big amount of data files.
This means having a clear status on files that have been removed, corrupted, modified, copied, renamed, duplicated.
You can also easily detect duplicates and remove them.

Fim does not keep track of the different contents of the files that are managed,
this is why we can call it an __Unversioned Control System (UVCS)__.
Fim keeps only each version of the different State that have been committed.
You cannot use them to retrieve the content of one file that you may have lost.

> __Fim does not replace a backup software__

[\[^ TOC\]](#table-of-contents)

## Fim workflow

First you need to initialize the Fim repository using the init command.
This will record the first State of your file tree.

	$ fim init

Then you can compare the differences between the recorded State and the current file tree using the diff command.
You can do a full diff that will compare the hash of all the files. It can be very slow as all the files content will be read.

	$ fim diff

You can compare quickly asking to hash only the first megabyte of the files. **Using this option can produce an inaccurate result.**

	$ fim diff -m

You can compare quicker asking to hash only the first four kilobytes of the files. **Using this option can produce an inaccurate result.**

	$ fim diff -k

Otherwise you can request to not hash file content using the fast mode (-f option). It will compare only the filenames and modification dates.
You will not be able to detect files that have been renamed or duplicated.

	$ fim diff -f

Each time you want to record the State of the current file tree you can use the commit command.
It's a time consuming operation that will compute all the hash of each files content.

	$ fim ci

[\[^ TOC\]](#table-of-contents)

## Usage

	$ fim help

	usage: fim <command> [-a <arg>] [-c <arg>] [-f] [-h] [-k] [-l] [-m] [-q] [-t <arg>] [-v] [-y]

	File Integrity Checker

	Available commands:
		 init                      Initialize a Fim repository
		 ci / commit               Commit the current directory State
		 diff                      Compare the current directory State with the previous one
		 rdates / reset-dates      Reset the file modification dates like it's stored in the last committed State
		 fdup / find-duplicates    Find duplicated files
		 rdup / remove-duplicates  Remove duplicated files from local directory based on a master Fim repository
		 log                       Display States log
		 help                      Prints the Fim help
		 version                   Prints the Fim version

	Available options:
		 -a,--master-fim-repository <arg>   Fim repository directory that you want to use as master. Only for the remove
											duplicates command
		 -c,--comment <arg>                 Sets that State comment during commit
		 -f,--fast-mode                     Do not hash file content. Use only filenames and modification dates
		 -h,--help                          Prints the Fim help
		 -k,--hash-only-first-four-kilo     Hash only the first four kilo of the files
		 -l,--use-last-state                Use the last committed State
		 -m,--hash-only-first-mega          Hash only the first mega of the files
		 -q,--quiet                         Do not display details
		 -t,--thread-count <arg>            Number of thread to use to hash files content in parallel
		 -v,--version                       Prints the Fim version
		 -y,--always-yes                    Always yes to every questions

[\[^ TOC\]](#table-of-contents)

## Simple example

Here is a step by step example of Fim usage.
For the purpose of this example we use small files.

##### Create a set of files

```shell
~$ mkdir sample

~$ cd sample/

# Creates 10 files
~/sample$ for i in {01..10} ; do echo "New File $i" > file$i ; done

~/sample$ ls -la
total 48
drwxrwxr-x 2 4096 juil. 21 22:24 .
drwxrwxr-x 7 4096 juil. 20 22:00 ..
-rw-rw-r-- 1   12 juil. 21 22:24 file01
-rw-rw-r-- 1   12 juil. 21 22:24 file02
-rw-rw-r-- 1   12 juil. 21 22:24 file03
-rw-rw-r-- 1   12 juil. 21 22:24 file04
-rw-rw-r-- 1   12 juil. 21 22:24 file05
-rw-rw-r-- 1   12 juil. 21 22:24 file06
-rw-rw-r-- 1   12 juil. 21 22:24 file07
-rw-rw-r-- 1   12 juil. 21 22:24 file08
-rw-rw-r-- 1   12 juil. 21 22:24 file09
-rw-rw-r-- 1   12 juil. 21 22:24 file10
```

##### Initialize the Fim repository

```shell
~/sample$ fim init
2015/07/21 22:25:02 - Info  - Scanning recursively local files, computing all hash, using 2 thread
    (Hash progress legend for files grouped 10 by 10: # > 200 MB, @ > 100 MB, O > 50 MB, o > 20 MB, . otherwise)
.2015/07/21 22:25:05 - Info  - Scanned 10 files (120 bytes), hashed 120 bytes bytes, during 00:00:03, using 2 thread

Added:            file01
Added:            file02
Added:            file03
Added:            file04
Added:            file05
Added:            file06
Added:            file07
Added:            file08
Added:            file09
Added:            file10

10 added
```

##### A new `.fim` directory have been created

```shell
~/sample$ ls -la
total 52
drwxrwxr-x 3 4096 juil. 21 22:25 .
drwxrwxr-x 7 4096 juil. 20 22:00 ..
-rw-rw-r-- 1   12 juil. 21 22:24 file01
-rw-rw-r-- 1   12 juil. 21 22:24 file02
-rw-rw-r-- 1   12 juil. 21 22:24 file03
-rw-rw-r-- 1   12 juil. 21 22:24 file04
-rw-rw-r-- 1   12 juil. 21 22:24 file05
-rw-rw-r-- 1   12 juil. 21 22:24 file06
-rw-rw-r-- 1   12 juil. 21 22:24 file07
-rw-rw-r-- 1   12 juil. 21 22:24 file08
-rw-rw-r-- 1   12 juil. 21 22:24 file09
-rw-rw-r-- 1   12 juil. 21 22:24 file10
drwxrwxr-x 3 4096 juil. 21 22:25 .fim
```

##### Do some modifications in the files

```shell
~/sample$ mkdir dir1

~/sample$ mv file01 dir1

# Change the file02 modification date
~/sample$ touch file02

~/sample$ cp file03 file11

# Add content to file04
~/sample$ echo foo >> file04

~/sample$ cp file05 file12

# Add content to file05
~/sample$ echo bar >> file05

~/sample$ rm file06

# Create the new file13
~/sample$ echo "New File 13" > file13

~/sample$ ls -la
total 60
drwxrwxr-x 4 4096 juil. 21 22:26 .
drwxrwxr-x 7 4096 juil. 20 22:00 ..
drwxrwxr-x 2 4096 juil. 21 22:25 dir1
-rw-rw-r-- 1   12 juil. 21 22:25 file02
-rw-rw-r-- 1   12 juil. 21 22:24 file03
-rw-rw-r-- 1   16 juil. 21 22:26 file04
-rw-rw-r-- 1   16 juil. 21 22:26 file05
-rw-rw-r-- 1   12 juil. 21 22:24 file07
-rw-rw-r-- 1   12 juil. 21 22:24 file08
-rw-rw-r-- 1   12 juil. 21 22:24 file09
-rw-rw-r-- 1   12 juil. 21 22:24 file10
-rw-rw-r-- 1   12 juil. 21 22:25 file11
-rw-rw-r-- 1   12 juil. 21 22:26 file12
-rw-rw-r-- 1   12 juil. 21 22:26 file13
drwxrwxr-x 3 4096 juil. 21 22:25 .fim

~/sample$ ls -la dir1/
total 12
drwxrwxr-x 2 4096 juil. 21 22:25 .
drwxrwxr-x 4 4096 juil. 21 22:26 ..
-rw-rw-r-- 1   12 juil. 21 22:24 file01
```

##### Fim is able to detect all the modifications that have been done

```shell
~/sample$ fim diff
2015/07/21 22:27:41 - Info  - Scanning recursively local files, computing all hash, using 2 thread
    (Hash progress legend for files grouped 10 by 10: # > 200 MB, @ > 100 MB, O > 50 MB, o > 20 MB, . otherwise)
.
2015/07/21 22:27:44 - Info  - Scanned 12 files (152 bytes), hashed 152 bytes bytes, during 00:00:03, using 2 thread

Comparing with the last committed state from 2015/07/21 22:25:02
Comment: Initial State

Added:            file13
Copied:           file12 	(was file05)
Duplicated:       file11 = file03
Date modified:    file02 	2015/07/21 22:24:08 -> 2015/07/21 22:25:55
Content modified: file04 	2015/07/21 22:24:08 -> 2015/07/21 22:26:07
Content modified: file05 	2015/07/21 22:24:08 -> 2015/07/21 22:26:15
Renamed:          file01 -> dir1/file01
Deleted:          file06

1 added, 1 copied, 1 duplicated, 1 date modified, 2 content modified, 1 renamed, 1 deleted
```

##### Search for duplicated files

```shell
~/sample$ fim fdup
Searching for duplicated files

2015/07/21 22:28:07 - Info  - Scanning recursively local files, computing all hash, using 2 thread
    (Hash progress legend for files grouped 10 by 10: # > 200 MB, @ > 100 MB, O > 50 MB, o > 20 MB, . otherwise)
.
2015/07/21 22:28:10 - Info  - Scanned 12 files (152 bytes), hashed 152 bytes bytes, during 00:00:03, using 2 thread

1 duplicated files

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
- Duplicate set #1
  file03 duplicated 1 times
      file11

```

##### Commit the modifications

```shell
~/sample$ fim ci -c "After modifications"
2015/07/21 22:28:38 - Info  - Scanning recursively local files, computing all hash, using 2 thread
    (Hash progress legend for files grouped 10 by 10: # > 200 MB, @ > 100 MB, O > 50 MB, o > 20 MB, . otherwise)
.
2015/07/21 22:28:41 - Info  - Scanned 12 files (152 bytes), hashed 152 bytes bytes, during 00:00:03, using 2 thread

Comparing with the last committed state from 2015/07/21 22:25:02
Comment: Initial State

Added:            file13
Copied:           file12 	(was file05)
Duplicated:       file11 = file03
Date modified:    file02 	2015/07/21 22:24:08 -> 2015/07/21 22:25:55
Content modified: file04 	2015/07/21 22:24:08 -> 2015/07/21 22:26:07
Content modified: file05 	2015/07/21 22:24:08 -> 2015/07/21 22:26:15
Renamed:          file01 -> dir1/file01
Deleted:          file06

1 added, 1 copied, 1 duplicated, 1 date modified, 2 content modified, 1 renamed, 1 deleted

Do you really want to commit (y/n/A)? y
```

##### Now everything is ok

```shell
~/sample$ fim diff
2015/07/21 22:30:32 - Info  - Scanning recursively local files, computing all hash, using 2 thread
    (Hash progress legend for files grouped 10 by 10: # > 200 MB, @ > 100 MB, O > 50 MB, o > 20 MB, . otherwise)
.
2015/07/21 22:30:35 - Info  - Scanned 12 files (152 bytes), hashed 152 bytes bytes, during 00:00:03, using 2 thread

Comparing with the last committed state from 2015/07/21 22:28:38
Comment: After modifications

Nothing modified
```

##### Display the Fim log

```shell
~/sample$ fim log
State #1: 2015/07/21 22:25:02
	Comment: Initial State
	Contains 10 files

State #2: 2015/07/21 22:28:38
	Comment: After modifications
	Contains 12 files

```

[\[^ TOC\]](#table-of-contents)

## Real life example

Here is the output of the initialization of a big Fim repository that contains 297 GB of photos and videos.
**It takes 2 hours** to hash all the files content.

```shell
~/Photos_Videos$ fim init
2015/07/20 23:07:28 - Info  - Scanning recursively local files, computing all hash, using 2 thread
    (Hash progress legend for files grouped 10 by 10: # > 200 MB, @ > 100 MB, O > 50 MB, o > 20 MB, . otherwise)
.o#...........................................o@o...............OO.oO.......O.......................
@....o.......oo......................o..................Oo..........................................
......................................#.............................................................
oO........O........................@@...............................................................
............................O...o........o.oo...o.o..o.................o........o...................
....o.o................................Oo..oo..o@...........................o.......................
.................................O...@..............................................................
....................................................................................................
...........o.......O@..............O@@.................O.o........@.........Oo........O@O..o......OO
..@...O#o..o......o................@.........OO.....................................................
...............o.........oo.o................................o...............o......................
.........................oooooooo.ooo@..........................oooo................O...............
...oo....OoOo..o....oO@@o@..O..........oO...o@oOO#@@@oo.@@o.o@O.....@@@@@@OO@@@@OO..@@#....OooO.....
.........................Oo#oOOoo.O#O.....oo.....o.......oo........@oOO.@o....o................O....
...@o#OoOO..@Oo.O.o........@o.............ooo@.o...............oO...................................
....O..O.....Oo#oooooooo..oo@oo.ooo.oooooooooO@ooo#Ooo@Ooo.o.o.##@@ooooo@OoOOOo...o.O.@Ooooooooooooo
oooooOooo.ooooooOooooOOoo.######O@####oo#####oo...oooOoo...ooooooooooooo....o.@#@#@OOoooOo#ooooOOoo@
ooo#@#####ooo..#.##o@o.@@o##oooooooO.oooooo@##o#@....#oooo@oo#######o...o.ooooooo#oo@#ooO@###o#oooo#
OooooooooooooooOoooooo....ooooooooo.#oooooo.oOOOooo....................o..ooo.........o.ooo.........
oo.o...........@o@o@oooO@##o.oooo#@##oo@ooo@##O##oOooooooooo###@##@............###OOoO#o###ooOoooo#o
oOoo@oo..o.oo...................o@#..o@ooo@@....................................................O...
............o............ooooo..o.....................ooo.o........................ooo.....o.oo.....
...............ooo....................oooooooooooooo.oo.oooooooooooo#oo#oooooooooooooooooooo........
..oooo......o......o......o..................o...#.#####ooooooooOoOooooooooOooooooOOOOooOOOoOOooo###
#@OooooooO#####oOoooOOOOOOOoOOOoooooooOooooooooooooo###ooooooooooo#@OoOOOOOOOOoooooooooooooooOOoo#oO
ooooooooooOOOoooooOooOOoOOOOooOOoOOOooOooOoooOoOOoooOoOoooOOOoOo##..................................
..........................@oo......ooo.oooo@oooooooooOooooooooO##OOOOOoOOOOOOOOOOOOOO@###OOOoOOOOOO#
@@OOOo...........................................................####@.ooo.o...o#ooooo@ooo..ooooo...
.ooo.......................Oo........................oo..@.......O@......O..........................
.....................................................@................@O............................
..................................................##@.....................................#o#oooOooo
.......#oO############################################################@##########o....ooooooooooo.oo
ooooo.oooo.o.oooooooooooo......ooooooooooooooooooooooo.oooooooooo.oooooooooooooooooooooooooooooooooo
o......oooooooooooooo....oooooooooooooooooooooooooooooooooooooo.oooooooooooooooooooooooooooooooooooo
oooooooo.o.ooo..ooo.ooooooooooo...oo.o....o..ooooo.ooo...o.ooooooooooooooooooo.ooooooooooooooooooooo
oooooooooo....oooo.ooooooooooooooooo....o....ooo...o.....oo............o.......o.oo.ooooooooooooo.oo
oooooooooooooooooooooooooooooooooo.oooooooooooooooo.ooooooooooooooooooooo.....oooooooooooooooooooooo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo..oo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
oooooooooooooooooo#ooooooooooo..oo@...ooooo#o
2015/07/21 01:11:54 - Info  - Scanned 41453 files (297 GB), hashed 297 GB bytes, during 02:04:25, using 2 thread

... # File list is skipped as it is too long

41453 added
```

I am able to check the differences very quickly using the `-k` option that will check only the first four kilobytes and **it take 4 minutes** :=).

```shell
~/Photos_Videos$ fim diff -k
2015/07/21 08:49:21 - Info  - Scanning recursively local files, hashing only first four kilo, using 2 thread
    (Hash progress legend for files grouped 10 by 10: # > 200 MB, @ > 100 MB, O > 50 MB, o > 20 MB, . otherwise)
.o#...........................................o@o...............OO.oO.......O.......................
@....o.......oo......................o..................Oo..........................................
......................................#.............................................................
oO........O........................@@...............................................................
............................O...o........o.oo...o.o..o.................o........o...................
....o.o................................Oo..oo..o@...........................o.......................
.................................O...@..............................................................
....................................................................................................
...........o.......O@..............O@@.................O.o........@.........Oo........O@O..o......OO
..@...O#o..o......o................@.........OO.....................................................
...............o.........oo.o................................o...............o......................
.........................oooooooo.ooo@..........................oooo................O...............
...oo....OoOo..o....oO@@o@..O..........oO...o@oOO#@@@oo.@@o.o@O.....@@@@@@OO@@@@OO..@@#....OooO.....
.........................Oo#oOOoo.O#O.....oo.....o.......oo........@oOO.@o....o................O....
...@o#OoOO..@Oo.O.o........@o.............ooo@.o...............oO...................................
....O..O.....Oo#oooooooo..oo@oo.ooo.oooooooooO@ooo#Ooo@Ooo.o.o.##@@ooooo@OoOOOo...o.O.@Ooooooooooooo
oooooOooo.ooooooOooooOOoo.######O@####oo#####oo...oooOoo...ooooooooooooo....o.@#@#@OOoooOo#ooooOOoo@
ooo#@#####ooo..#.##o@o.@@o##oooooooO.oooooo@##o#@....#oooo@oo#######o...o.ooooooo#oo@#ooO@###o#oooo#
OooooooooooooooOoooooo....ooooooooo.#oooooo.oOOOooo....................o..ooo.........o.ooo.........
oo.o...........@o@o@oooO@##o.oooo#@##oo@ooo@##O##oOooooooooo###@##@............###OOoO#o###ooOoooo#o
oOoo@oo..o.oo...................o@#..o@ooo@@....................................................O...
............o............ooooo..o.....................ooo.o........................ooo.....o.oo.....
...............ooo....................oooooooooooooo.oo.oooooooooooo#oo#oooooooooooooooooooo........
..oooo......o......o......o..................o...#.#####ooooooooOoOooooooooOooooooOOOOooOOOoOOooo###
#@OooooooO#####oOoooOOOOOOOoOOOoooooooOooooooooooooo###ooooooooooo#@OoOOOOOOOOoooooooooooooooOOoo#oO
ooooooooooOOOoooooOooOOoOOOOooOOoOOOooOooOoooOoOOoooOoOoooOOOoOo##..................................
..........................@oo......ooo.oooo@oooooooooOooooooooO##OOOOOoOOOOOOOOOOOOOO@###OOOoOOOOOO#
@@OOOo...........................................................####@.ooo.o...o#ooooo@ooo..ooooo...
.ooo.......................Oo........................oo..@.......O@......O..........................
.....................................................@................@O............................
..................................................##@.....................................#o#oooOooo
.......#oO############################################################@##########o....ooooooooooo.oo
ooooo.oooo.o.oooooooooooo......ooooooooooooooooooooooo.oooooooooo.oooooooooooooooooooooooooooooooooo
o......oooooooooooooo....oooooooooooooooooooooooooooooooooooooo.oooooooooooooooooooooooooooooooooooo
oooooooo.o.ooo..ooo.ooooooooooo...oo.o....o..ooooo.ooo...o.ooooooooooooooooooo.ooooooooooooooooooooo
oooooooooo....oooo.ooooooooooooooooo....o....ooo...o.....oo............o.......o.oo.ooooooooooooo.oo
oooooooooooooooooooooooooooooooooo.oooooooooooooooo.ooooooooooooooooooooo.....oooooooooooooooooooooo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo..oo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
oooooooooooooooooo#ooooooooooo..oo@...ooooo#o
2015/07/21 08:53:47 - Info  - Scanned 41453 files (297 GB), hashed 161 MB bytes, during 00:04:25, using 2 thread

Comparing with the last committed state from 2015/07/20 23:07:28
Comment: Initial State

Nothing modified
```

This quick diff can be inaccurate.
To increase accuracy, you can use the `-m` option. This result is more accurate,
but it cannot be completely accurate as only the hash of the first megabyte of the files are computed.
This time in my case, **it takes 20 minutes** to check the differences.

```shell
~/Photos_Videos$ fim diff -m
2015/07/21 12:45:24 - Info  - Scanning recursively local files, hashing only first mega, using 2 thread
    (Hash progress legend for files grouped 10 by 10: # > 200 MB, @ > 100 MB, O > 50 MB, o > 20 MB, . otherwise)
.o#...........................................o@o...............OO.oO.......O.......................
@....o.......oo......................o..................Oo..........................................
......................................#.............................................................
oO........O........................@@...............................................................
............................O...o........o.oo...o.o..o.................o........o...................
....o.o................................Oo..oo..o@...........................o.......................
.................................O...@..............................................................
....................................................................................................
...........o.......O@..............O@@.................O.o........@.........Oo........O@O..o......OO
..@...O#o..o......o................@.........OO.....................................................
...............o.........oo.o................................o...............o......................
.........................oooooooo.ooo@..........................oooo................O...............
...oo....OoOo..o....oO@@o@..O..........oO...o@oOO#@@@oo.@@o.o@O.....@@@@@@OO@@@@OO..@@#....OooO.....
.........................Oo#oOOoo.O#O.....oo.....o.......oo........@oOO.@o....o................O....
...@o#OoOO..@Oo.O.o........@o.............ooo@.o...............oO...................................
....O..O.....Oo#oooooooo..oo@oo.ooo.oooooooooO@ooo#Ooo@Ooo.o.o.##@@ooooo@OoOOOo...o.O.@Ooooooooooooo
oooooOooo.ooooooOooooOOoo.######O@####oo#####oo...oooOoo...ooooooooooooo....o.@#@#@OOoooOo#ooooOOoo@
ooo#@#####ooo..#.##o@o.@@o##oooooooO.oooooo@##o#@....#oooo@oo#######o...o.ooooooo#oo@#ooO@###o#oooo#
OooooooooooooooOoooooo....ooooooooo.#oooooo.oOOOooo....................o..ooo.........o.ooo.........
oo.o...........@o@o@oooO@##o.oooo#@##oo@ooo@##O##oOooooooooo###@##@............###OOoO#o###ooOoooo#o
oOoo@oo..o.oo...................o@#..o@ooo@@....................................................O...
............o............ooooo..o.....................ooo.o........................ooo.....o.oo.....
...............ooo....................oooooooooooooo.oo.oooooooooooo#oo#oooooooooooooooooooo........
..oooo......o......o......o..................o...#.#####ooooooooOoOooooooooOooooooOOOOooOOOoOOooo###
#@OooooooO#####oOoooOOOOOOOoOOOoooooooOooooooooooooo###ooooooooooo#@OoOOOOOOOOoooooooooooooooOOoo#oO
ooooooooooOOOoooooOooOOoOOOOooOOoOOOooOooOoooOoOOoooOoOoooOOOoOo##..................................
..........................@oo......ooo.oooo@oooooooooOooooooooO##OOOOOoOOOOOOOOOOOOOO@###OOOoOOOOOO#
@@OOOo...........................................................####@.ooo.o...o#ooooo@ooo..ooooo...
.ooo.......................Oo........................oo..@.......O@......O..........................
.....................................................@................@O............................
..................................................##@.....................................#o#oooOooo
.......#oO############################################################@##########o....ooooooooooo.oo
ooooo.oooo.o.oooooooooooo......ooooooooooooooooooooooo.oooooooooo.oooooooooooooooooooooooooooooooooo
o......oooooooooooooo....oooooooooooooooooooooooooooooooooooooo.oooooooooooooooooooooooooooooooooooo
oooooooo.o.ooo..ooo.ooooooooooo...oo.o....o..ooooo.ooo...o.ooooooooooooooooooo.ooooooooooooooooooooo
oooooooooo....oooo.ooooooooooooooooo....o....ooo...o.....oo............o.......o.oo.ooooooooooooo.oo
oooooooooooooooooooooooooooooooooo.oooooooooooooooo.ooooooooooooooooooooo.....oooooooooooooooooooooo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo..oo
oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
oooooooooooooooooo#ooooooooooo..oo@...ooooo#o
2015/07/21 13:05:04 - Info  - Scanned 41453 files (297 GB), hashed 35 GB bytes, during 00:19:39, using 2 thread

Comparing with the last committed state from 2015/07/20 23:07:28
Comment: Initial State

Nothing modified
```

If you want to be completely sure of the `diff` result you need to run a full hash of all the files content using the `fim diff` command without any option.
This time in my case, **it takes 2 hours** as for the `init` command.

There is also the fast mode using the `-f` option that do not hash file content. It helps to detect faster changes but only file names that have changed.
This time in my case, **it takes 3 seconds**.

[\[^ TOC\]](#table-of-contents)
