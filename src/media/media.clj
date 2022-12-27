(ns media.media
  (:require
   [clojure.string :refer [split]]
   [clojure.java.io :as io])
  (:import
   [java.awt.image BufferedImage AffineTransformOp]
   [java.awt Color]
   [javax.imageio ImageIO]
   [javax.imageio.stream FileImageInputStream]
   [java.io File]
   [java.awt.geom AffineTransform]
   [com.ibasco.image.gif GifImageReader]))

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
  [filename]
  (last (split filename #"\.")))

;; Image

(defn scale-image
  "Scales a `BufferedImage` by a float."
  [^BufferedImage image scale]
  (let [width (Math/round (* scale (.getWidth image)))
        height (Math/round (* scale (.getHeight image)))
        ^BufferedImage new (new BufferedImage width height (.getType image))
        ^AffineTransform scale-instance (AffineTransform/getScaleInstance scale scale)
        ^AffineTransformOp scale-op (new AffineTransformOp scale-instance AffineTransformOp/TYPE_BILINEAR)]
    (.filter scale-op image new)
    new))

(defn decode-image
  "Reads a file and returns a `BufferedImage`.
  If scale is provided, return a scaled image."
  [^File file scale]
  (let [^BufferedImage image (ImageIO/read file)]
    (if-not scale
      image
      (scale-image image scale))))

;; Gif

(defn new-stream
  "Constructs and returns a new `FileImageInputStream` of a file."
  [^File file]
  (new FileImageInputStream file))

;; (defn gif-reader
;;   "Takes a `FileImageInputStream` and returns a `GIFImageReader` containing the stream."
;;   [^FileImageInputStream stream]
;;   (let [^com.ibasco.image.gif.GifImageReader reader (new GifImageReader stream)]
;;     reader))

(defn gif-decoder
  "Reads a gif file and returns a `GIFImageReader`."
  [^File file]
  (let [^FileImageInputStream stream (new-stream file)]
    (^com.ibasco.image.gif.GifImageReader new GifImageReader file)))

(defn image-from-array
  "Constructs a `BufferedImage` and initializes it with RGBA values from an IntBuffer array"
  [data width height]
  (let [^BufferedImage image (new BufferedImage width height BufferedImage/TYPE_INT_ARGB)]
    (.setRGB image 0 0 width height data 0 width)
    image))

;; RGB frame

(defn rgb-vector
  "Takes a 24-bit RGBA integer and returns a vector of R, G and B values."
  [rgba]
  (let [^Color color (new Color rgba)]
    (vector (.getRed color) (.getGreen color) (.getBlue color))))

(defn get-frame
  "Recursively reads 24-bit RGBA values of a `BufferedImage` and returns a frame as a matrix of each pixel as a vector containing 8-bit R, G and B channels."
  [^BufferedImage image]

  (let [width (dec (.getWidth image))
        height (dec (.getHeight image))]

   (loop [x 0
         y 0
         frame []]

    (if (> y height)
      frame

      ;; .getRGB method of BufferedImage returns a 24-bit integer
      ;; add-pixel function decides where to add the pixel vector
      (let [rgba (.getRGB image x y)
            pixel (rgb-vector rgba)
            add-pixel (if (= x 0)
                              #(conj %1 [%2])
                              #(conj (pop %1) (conj (peek %1) %2)))]

        (if (= x width)
          (recur 0 (inc y) (add-pixel frame pixel))
          (recur (inc x) y (add-pixel frame pixel))))))))
