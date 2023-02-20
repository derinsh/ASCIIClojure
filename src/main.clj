(ns main
  (:gen-class :main true)
  (:require
   [clojure.string :as s]
   [media.play :as media :refer [play]]))

(def help-line
  "Run the program followed with a filename and optional arguments to produce an ASCII-art image.
  Supported file formats: PNG, JPG, BMP and GIF\n
  Arguments:\n
  --color : will output the ASCII characters with ANSI rgb color codes matching the source image\n
  --bt709 : will resample luminance according to the BT-709 color standard\n
  --scale <number> : will scale the image to the specified number (default 1.0)\n
  --out <filename> : will produce a text file containing the ASCII image (GIF not supported)\n
  --help : show this help screen\n")

(def default-options
  {:color false :scale false :out false :help false :bt709 false})

(defn command-parser [args]

  (loop [args args
         parsed default-options
         errors []]

    ;; When no args remain, return parsed options and errors if any
    (if (empty? args)
      [parsed errors]

      ;; Parser
      (if-let [arg (second (s/split (first args) #"\--"))]
        (cond
          (not (contains? parsed (keyword arg)))
          (recur (rest args) parsed (conj errors (str "Argument not recognized: " (first args))))

          (= arg "out")
          (if (and (< 0 (count (second args))) (not (= "-" (first (second args)))))
            (recur (rest (rest args)) (assoc parsed :out (second args)) errors)
            (recur (rest args) parsed (conj errors "No valid output file provided.")))

          (= arg "scale")
          (let [scale (second args)
                float (try (Float/parseFloat scale) (catch Exception _e 0))]

            (if (> float 0)
              (recur (rest (rest args)) (assoc parsed :scale float) errors)
              (recur (rest args) parsed (conj errors "Not a valid scale."))))

          :else
          (recur (rest args) (assoc parsed (keyword arg) true) errors))
        (recur (rest args) parsed (conj errors (str "Argument not recognized: " (first args))))))))

(defn -main
  "Generate and render an ASCII image of the provided file."
  [& args]
  (let [filename (first args)
        commands (command-parser (rest args))
        parsed (first commands)
        errors (second commands)]

    ;; If parser returned errors, print errors and quit

    (if (seq errors)
      (apply println errors)

      ;; If no errors, start the program or show help if requested by user or filename is absent

      (if (or (get parsed :help) (< (count filename) 1))
        (println help-line)
        (play filename (:color parsed) (:bt709 parsed) (:scale parsed) (:out parsed))))))
