# ASCII-Clojure

An ASCII art image generator written in Clojure. You can run the program with an image or GIF file as the first argument and the terminal outputs a scaled image of ASCII characters. The program supports generating the characters in ANSI true color.

## Installation

Download release or build from source.

## Build

Clone the project and build using leiningen with `lein bin`.

## Usage

Simple example:

    ascii-clojure my-image.png

Scale image:

    ascii-clojure my-image.png --scale 0.25

Resample luminance and color output:

    ascii-clojure my-image.png --bt709 --color

Save output file:

    ascii-clojure my-image.png --out out-file.txt

## Options

    --color
    --bt709
    --scale <float>
    --out <file>
    --help

### Improvements

Output image and gifs in a new terminal window, scaled accordingly

Review performance

### Background

This is my first application in Clojure. It's a personal proof-of-concept to get acquainted with not only Clojure and its rich functionality for recursion but also Java's OO and classes.

Only Clojure and Oracle docs have been used for preparation and learning.
