(ns media.media
  (:require
   [clojure.string :refer [split]]
   [clojure.java.io :as io])
  (:import
   [java.awt.image BufferedImage]
   [java.awt Color]
   [javax.imageio ImageIO]
   [java.io File]))


(defn file-in [filename]
  (try
  ;; (File. filename)
  (^File io/file filename)
   (catch Exception e
     (println "File could not be read: " (.getMessage e)))))


(defn decode-image [^File file]
  (^BufferedImage ImageIO/read file))


(defn get-format [file]
  (last (split file #"\.")))


(defn rec-frame [w h ^BufferedImage image]
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


(defn get-frame [image]
  (let [width (.getWidth image)
        height (.getHeight image)]
    (rec-frame width height image)))
