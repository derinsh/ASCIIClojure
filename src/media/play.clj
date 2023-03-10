(ns media.play
  (:require
   [media.media :refer
    [get-frame get-format file-in scale-image decode-image gif-decoder image-from-array]]
   [char.characters :as char]
   [clojure.string :as s]
   [clojure.java.io])
  (:import
   [java.awt.image BufferedImage]))

(defn render-image
  "Renders an ASCII image from a `BufferedImage` by calling `render` and concatenating the rows into one string."
  [^BufferedImage image color bt709]

  (let [frame (get-frame image)
        rendered (if-not color
                   (char/render frame bt709)
                   (char/render-with-color frame bt709))]
    (apply str
           (for [row rendered]
             (str (apply str row) "\n")))))

(defn write-image
  "Writes and saves a rendered ASCII image to a text file.
  Asks for user confirmation to overwrite if file already exists."
  [rendered-image ^java.io.File out]

  (let [write-file? (atom true)]

    (when (.exists out)
        (println "Overwrite existing file? y/n")
        (let [resp (read-line)] (when-not (or (= (s/lower-case resp) "y") (= (s/lower-case resp) "yes")) (reset! write-file? false))))

    (if write-file?
      (with-open [writer (clojure.java.io/writer out)]
        (.write writer rendered-image))
      (println "Will not write to existing file."))))

(defn print-image
  "Prints a rendered ASCII image."
  [rendered-image]
  (println rendered-image))


(defn render-gif
  "Returns a collection of ASCII images by looping through a `GifImageReader` and rendering the frames."
  [^com.ibasco.image.gif.GifImageReader gif-reader scale color bt709]

  (for [_i (range (.getTotalFrames gif-reader))

        :let [^com.ibasco.image.gif.GifFrame gif-frame (.read gif-reader)
          gif-data (.getData gif-frame)
          width (.getWidth gif-frame)
          height (.getHeight gif-frame)
          ^BufferedImage image (image-from-array gif-data width height)
          ^BufferedImage image (if-not scale image (scale-image image scale))
          frame (get-frame image)
          rendered-frame (if-not color
                           (char/render frame bt709)
                           (char/render-with-color frame bt709))]]

    (apply str
      (for [row rendered-frame]
        (str (apply str row) "\n")))))

(defn play-gif
  "Plays a collection of rendered images."
  [images]
    ;; Main loop
    (while true

      ;; Looping through vector of multiple images
      (doseq [i (range (dec (count images)))]
         (println (nth images i))

        ;; Pause and clear output
        (Thread/sleep 100)
        (print (str \u001b \c)))))


(defn play
  "Takes a filename and optional arguments, checks the file-format and calls functions to render the image with or without ANSI colors, scaling or writing to an output file."
  [filename & [color? bt709? scale out]]

  (if-let [^java.io.File file (file-in filename)]

    (let [format (get-format filename)]

      (cond

        (or (= format "png") (= format "jpg") (= format "bmp"))
        (let [^BufferedImage image (decode-image file scale)
              rendered-image (render-image image color? bt709?)]
          (if out
            (if-let [^java.io.File file-out (file-in out)]
              (write-image rendered-image file-out)
              (println "Failed to write to output file " out "."))
            (print-image rendered-image)))


        (= format "gif")
        (let [^com.ibasco.image.gif.GifImageReader reader (gif-decoder file)]
          (if out
            "Output file cannot be used with GIF."
            (play-gif (render-gif reader scale color? bt709?))))


        :else
        (println "File format not recognized or not supported.")))

    "File could not be read."))
