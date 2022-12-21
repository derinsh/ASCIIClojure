(ns char.characters)

(def characters
  "A 16-slot vector of ASCII characters."
  [" " "`" "." "-" "~" "^" "+" "=" "*" "<" "c" "!" "/" "¤" "#" "x" "e" "d" "?" "2" "L" "E" "{" "€" "£" "X" "&" "§" "%" "M" "$" "@"])

(defn convert-rgb
  "Converts an 8-bit RGB vector to perceived luminance according to BT-709."
  [rgb]
  (let [red (nth rgb 0)
        green (nth rgb 1)
        blue (nth rgb 2)]
    (+ (* 0.33 red) (* 0.5 green) (* 0.16 blue))))

(defn get-character
  "Returns a character based on luminance."
  [rgb]
  (let [average (/ (convert-rgb rgb) 3.0)
        index (/ average 16.0)]
    (nth characters (- 16 index))))

(defn render [frame]
  (for [row frame]
    (map get-character row)))
