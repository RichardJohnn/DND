(ns dnd.level)

(def block {
            :x 0
            :y 0
            :inhabitants []
            :solid false
            })

(def width  40)
(def height 20)

(defn make-block [x y]
  (let [solid (> (rand) 0.9)]
    (assoc block :x x :y y :solid solid)))

(defn make-level []
  (vec
    (for [x (map inc (range width))
          y (map inc (range height))]
    (make-block x y))))

