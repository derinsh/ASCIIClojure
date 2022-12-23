(ns media.media
  (:require
   [clojure.string :refer [split]]
   [clojure.java.io :as io])
  (:import
   [java.awt.image BufferedImage]
   [java.awt Color]
   [javax.imageio ImageIO]
   [javax.imageio.stream FileImageInputStream]
   [javax.imageio ImageReader]
   [javax.imageio IIOImage]
   [java.util Iterator]
   [java.io File]))

;; IO

(defn file-in
  "Attempts to load a file from disk and return a File object."
  [filename]
  (try
  (^File io/file filename)
   (catch Exception e
     (println "File could not be read: " (.getMessage e)))))

(defn get-format
  "Identifies a file extension from string."
  [file]
  (last (split file #"\.")))

;; Image

(defn decode-image
  "Reads a File and returns a BufferdImage."
  [^File file]
  (^BufferedImage ImageIO/read file))

;; Gif

(defn new-stream
  "Constructs and returns a new FileImageInputStream"
  [file]
  (new FileImageInputStream file))

(defn gif-reader
  "Returns a .gif ImageReader."
  [stream]
  (let [^com.sun.imageio.plugins.gif.GIFImageReader reader (-> (ImageIO/getImageReaders stream) (.next))]
    (.setInput reader stream)
    reader))

(defn gif-decoder
  "Returns a GIFImageReader."
  [^File file]
  (let [^FileImageInputStream stream (new-stream file)]
    ^com.sun.imageio.plugins.gif.GIFImageReader (gif-reader stream)))

;; RGB frame

(defn recursive--frame
  "Reads 24-bit RGBA values of an image and returns a matrix of 8-bit R, G and B channels as vectors."
  [w h ^BufferedImage image]
  (loop [x 0
         y 0
         matrix [[]]]
    (if (and (= x (dec w)) (= y (dec h)))
      matrix
      (let [pixel (^Integer .getRGB image x y)
            color (new Color pixel)
            rgb (vector (.getRed color) (.getGreen color) (.getBlue color))]
        (if (= x (dec w))
          (recur 0 (inc y) (conj matrix (vector rgb)))
          (recur (inc x) y (conj (pop matrix) (conj (peek matrix) rgb))))))))

(defn get-frame
  "Takes a BufferedImage and returns a matrix of R, G and B vectors."
  [image]
  (let [width (.getWidth image)
        height (.getHeight image)]
    (recursive--frame width height image)))
