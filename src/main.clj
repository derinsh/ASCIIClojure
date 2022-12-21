(ns main
  (:gen-class :main true)
  (:require [media.play :refer [play]]))

(defn -main
  "Generate and render an ASCII image of the provided file."
  [& args]
  (let [filename (first args)]
    (if (>= 0 (count filename))
      (throw (AssertionError. "No filename provided"))
      (play filename))))
