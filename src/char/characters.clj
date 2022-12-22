(ns char.characters
  (:require
   [char.ansi :as ansi]))

(def characters
  "A 32-slot vector of ASCII characters."
  [" " "`" "." "-" "~" "^" "+" "=" "*" "<" "c" "!" "/" "¤" "#" "x" "e" "d" "?" "2" "L" "E" "{" "€" "£" "X" "&" "§" "%" "M" "$" "@"])

(def luminance
  "A vector of multiples for percieved luminance according to BT-709."
  [0.33 0.5 0.16])

(defn convert-rgb
  "Converts an 8-bit R, G and B vector to luminance. Optionally BT-709."
  [rgb lum?]
  (let [red (nth rgb 0)
        green (nth rgb 1)
        blue (nth rgb 2)]
    (if lum?
      (/ (reduce + (map * luminance [red green blue])) 3)
      (/ (+ red green blue) 3))))

(defn get-character
  "Returns a character based on luminance."
  [rgb]
  (let [average (convert-rgb rgb false)
        index (/ average 8.0)]
    (nth characters index)))

(defn get-ansi
  "Returns an ANSI escape code string for RGB."
  [rgb]
  (let [r (nth rgb 0)
        g (nth rgb 1)
        b (nth rgb 2)]
    (ansi/from-color r g b)))

(defn render-with-color
  "Creates a matrix of ANSI color codes and ASCII characters from a matrix of R, G and B vectors."
  [frame]
  (for [row frame]
    (map
     (fn [rgb]
           (str " " (get-ansi rgb) (get-character rgb) " "))
         row)))

(defn render
  "Creates a matrix of ASCII characters from a matrix of R, G and B vectors."
  [frame]
  (for [row frame]
    (map
     (fn [rgb] (str " " (get-character rgb) " "))
         row)))
