(ns main
  (:gen-class :main true)
  (:require [media.play :refer [play]]))

(def help-line "Run the program with a filename and the argument 'color' to print in color.")

(defn -main
  "Generate and render an ASCII image of the provided file."
  [& args]
  (let [first-arg (first args)
        second-arg (first (rest args))
        help? (= "help" first-arg)
        color? (= "color" second-arg)]
    (if help?
      (println help-line)
      (if (>= 0 (count first-arg))
        (throw (AssertionError. "No filename or argument provided"))
        (play first-arg color?)))))
