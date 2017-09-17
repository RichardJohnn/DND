(ns dnd.level
   (:require [cljs.nodejs :as nodejs]
             [ dnd.character :refer [base-character]]))

(def COLOR
  (nodejs/require "color"))

(def block {
            :x 0
            :y 0
            :inhabitants []
            :solid false
            })

(def width  20)
(def height 10)

(defn make-block [x y]
  (let [solid (> (rand) 0.9)
        walkable (not solid)
        has-inhabitant (> (rand) .8)
        inhabitants (if (and (not solid) has-inhabitant)
                      [(assoc base-character :char "e" :color (rand-int 256))]
                      [])
        color (if (and walkable (> (rand) 0.9))
                (COLOR "blue")
                (COLOR "green")
                )]
    (assoc block
           :x x
           :y y
           :solid solid
           :inhabitants inhabitants
           :color color
           )))

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

