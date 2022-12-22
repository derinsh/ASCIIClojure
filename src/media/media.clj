(ns media.media
  (:require
   [clojure.string :refer [split]]
   [clojure.java.io :as io])
  (:import
   [java.awt.image BufferedImage]
   [java.awt Color]
   [javax.imageio ImageIO]
   [javax.imageio.stream FileImageInputStream]
   [java.io File]))

;; IO

(defn file-in
  "Attempts to load a file from disk and return a `File` object."
  [filename]
  (try
  (^File io/file filename)
   (catch Exception e
     (println "File could not be read: " (.getMessage e)))))

(defn get-format
  "Identifies a file extension from a path or filename string."
  [file]
  (last (split file #"\.")))

;; Image

(defn decode-image
  "Reads a file and returns a `BufferedImage`."
  [^File file]
  (^BufferedImage ImageIO/read file))

;; Gif

(defn new-stream
  "Constructs and returns a new `FileImageInputStream` of a file."
  [file]
  (new FileImageInputStream file))

(defn gif-reader
  "Takes a `FileImageInputStream` and returns a `GIFImageReader` containing the stream."
  [^FileImageInputStream stream]
  (let [^com.sun.imageio.plugins.gif.GIFImageReader reader (-> (ImageIO/getImageReaders stream) (.next))]
    (.setInput reader stream)
    reader))

(defn gif-decoder
  "Reads a gif file and returns a `GIFImageReader`."
  [^File file]
  (let [^FileImageInputStream stream (new-stream file)]
    ^com.sun.imageio.plugins.gif.GIFImageReader (gif-reader stream)))

;; RGB frame

(defn rgb-vector
  "Takes a 24-bit RGBA integer and returns a vector of R, G and B values."
  [rgba]
  (let [^Color color (new Color rgba)]
    (vector (.getRed color) (.getGreen color) (.getBlue color))))

(defn get-frame
  "Recursively reads 24-bit RGBA values of a `BufferedImage` and returns a frame as a matrix of each pixel as a vector containing 8-bit R, G and B channels."
  [^BufferedImage image]

  (let [width (.getWidth image)
        height (.getHeight image)]

   (loop [x 0
         y 0
         frame [[]]]

    (if (and (= x (dec width)) (= y (dec height)))
      frame

      ;; .getRGB method of BufferedImage returns a 24-bit integer
      ;; add-pixel function decides where to add the pixel vector
      (let [rgba (.getRGB image x y)
            pixel (rgb-vector rgba)
            add-pixel (if (= x (dec width))
                              #(conj frame [%])
                              #(conj (pop frame) (conj (peek frame) %)))]

          (recur 0 (inc y) (add-pixel pixel)))))))
