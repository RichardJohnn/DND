(ns dnd.level)

(def block {
            :x 0
            :y 0
            :inhabitants []
            :solid false
            })

(def width  20)
(def height 5)

(defn make-block [x y]
  (let [solid (> (rand) 0.9)]
    (assoc block :x x :y y :solid solid)))

(defn make-level
  ([]
   (make-level width height))

  ([width height]
   (let [one-indexed-range #(map inc (range %))
         row-array (one-indexed-range width)
         col-array (one-indexed-range height)
         ]
    (vec
      (for [x row-array]
        (vec (map #(make-block x %) col-array)))))))

