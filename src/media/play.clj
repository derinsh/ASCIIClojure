(ns media.play
  (:require
   [media.media :as media]
   [char.characters :as char]
   [clojure.pprint]))

(defn render-image
  "Renders an ASCII image by printing a matrix row-wise."
  [image]
  (let [frame (media/get-frame image)
        rendered-frame (char/render frame)]
    (doseq [row rendered-frame]
      (run! print row)
      (newline))
    ))

(defn play [filename]
  (if-let [file (media/file-in filename)]
    (let [format (media/get-format filename)]
      (cond
        (or (= format "png") (= format "jpg"))
        (let [image (media/decode-image file)]
          (render-image image))
        (= format "gif")
        nil
        :else (println "File not supported.")))))
