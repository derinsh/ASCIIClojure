(ns media.play
  (:require
   [media.media :refer
    [get-frame get-format file-in decode-image gif-decoder]]
   [char.characters :as char])
  (:import
   [java.awt.image BufferedImage]))

(defn render-image
  "Renders an ASCII image from a `BufferdImage` by calling `render` and prints the returned matrix row-wise."
  [^BufferedImage image color]

  (let [frame (get-frame image)
        rendered-frame (if-not color
                         (char/render frame)
                         (char/render-with-color frame))]

    (doseq [row rendered-frame]
      (run! print row)
      (newline))))

(defn render-gif
  "Returns ASCII images by looping through an ImageFileReader and letting images by index."
  ;; There is a bug in GIFImageReader: Index 4096 out of bounds for length 4096
  ;; This affects some gifs, alternative reader or patch is needed
  [^com.sun.imageio.plugins.gif.GIFImageReader gif-reader color]

  (for [i (range (dec (.getNumImages gif-reader true)))

    :let [^BufferedImage image (.read gif-reader i)
          frame (get-frame image)
          rendered-frame (if-not color
                           (char/render frame)
                           (char/render-with-color frame))]]
      rendered-frame))

(defn play-gif
  "Plays a GIFImageReader by extracting and rendering images with function `render-gif` and looping through the vectors.
  Sleeps between frames and sends an ANSI terminal clear command to simulate video."
  [^com.sun.imageio.plugins.gif.GIFImageReader reader color]

  (let [images (render-gif reader color)]

    ;; Main loop
    (while true

      ;; Loop #1 vector of multiple images
      (doseq [i (range (dec (count images)))]

        (let [image (nth images i)]

          ;; Loop #2 vector of rows of an image
          (doseq [row image]
            (run! print row)
            (newline)))

        ;; Pause and clear outpu
        (Thread/sleep 100)
        (print (str \u001b \c))))))


(defn play
  "Takes filename and an optional argument color, checks the file-format and calls functions to render the image with or without ANSI colors."
  [filename & [color]]

  (if-let [^java.io.File file (file-in filename)]

    (let [format (get-format filename)]

      (cond
        (or (= format "png") (= format "jpg") (= format "bmp"))
        (let [^BufferedImage image (decode-image file)]
          (render-image image (or color false)))
        (= format "gif")
        (let [^com.sun.imageio.plugins.gif.GIFImageReader reader (gif-decoder file)]
          (play-gif reader (or color false)))
        :else (println "File not supported.")))

      "File could not be read."))
