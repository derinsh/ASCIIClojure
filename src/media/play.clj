(ns media.play
  (:require
   [media.media :refer
    [get-frame get-format file-in decode-image gif-decoder]]
   [char.characters :as char]
   [clojure.pprint])
  (:import
   [java.awt.image BufferedImage]))

(defn render-image
  "Renders an ASCII image by printing a matrix row-wise."
  [image color]
  (let [frame (get-frame image)
        rendered-frame (if-not color
                         (char/render frame)
                         (char/render-with-color frame))]
    (doseq [row rendered-frame]
      (run! print row)
      (newline))))

(defn render-gif
  "Renders ASCII images from gif by printing "
  [iterator color]
  (while (.hasNext iterator)
    (let [^BufferedImage image (.next iterator)
          frame (get-frame image)
          rendered-frame (if-not color
                           (char/render frame)
                           (char/render-with-color frame))]
      (doseq [row rendered-frame]
        (run! print row)
        (newline)
        (Thread/sleep 100)))))


(defn play
  "Takes filename and an optional argument color, checks the file-format and calls functions to render the image with or without ANSI colors."
  [filename & [color]]

  (if-let [^java.io.File file (file-in filename)]
    (let [format (get-format filename)]
      (cond
        (or (= format "png") (= format "jpg") (= format "bmp"))
        (let
            [^BufferedImage image (decode-image file)]
          (render-image image (or color false)))
        (= format "gif")
        (let
            [^java.util.Iterator iterator (gif-decoder file)]
          (render-gif iterator (or color false)))
        :else (println "File not supported.")))
      "File could not be read."))
