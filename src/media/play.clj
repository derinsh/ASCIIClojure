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
  "Renders ASCII images from gif by looping through an ImageFileReader and letting images by index.
  Images are rendered as a matrix and printed row-wise and calls to sleep and ansi-clear are sent."
  ;; There is a bug in GIFImageReader: Index 4096 out of bounds for length 4096
  ;; This affects some gifs, alternative reader or patch is needed
  [^com.sun.imageio.plugins.gif.GIFImageReader reader color]
  (for [i (range (dec (.getNumImages reader true)))
    :let [^BufferedImage image (.read reader i)
          frame (get-frame image)
          rendered-frame (if-not color
                           (char/render frame)
                           (char/render-with-color frame))]]
      rendered-frame))

(defn play-gif
  "Plays a gif by saving the rendered images with function `render-gif` in a vector and looping."
  [^com.sun.imageio.plugins.gif.GIFImageReader reader color]
  (let [images (render-gif reader color)]
    (while true
      (doseq [i (range (dec (count images)))]
        (let [image (nth images i)]
          (doseq [row image]
            (run! print row)
            (newline)))
        (Thread/sleep 100)
        (print (str \u001b \c))))))


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
            [^com.sun.imageio.plugins.gif.GIFImageReader reader (gif-decoder file)]
          (play-gif reader (or color false)))
        :else (println "File not supported.")))
      "File could not be read."))
