(ns dnd.level
   (:require [cljs.nodejs :as nodejs]
             [dnd.character :refer [base-character tree egg]]
             [dnd.color :refer [COLOR color-to-array color-name]]
             ))

(def block {
            :x 0
            :y 0
            :inhabitants []
            :solid false
            :visible true
            })

(def width  100)
(def height 40)

(defn make-block [x y]
  (let [solid (> (rand) 0.9)
        walkable (not solid)
        has-inhabitant (> (rand) .9)
        has-tree (> (rand) .5)
        inhabitants (if (and (not solid) has-inhabitant)
                      (if has-tree
                        [(tree "🌳" "happy")]
                        [(egg)])
                      [])
        color (color-to-array
                (if-not walkable
                  (COLOR "#b1b7b7")
                  (if (> (rand) 0.9)
                    (COLOR "#0078ff")
                    (COLOR "#0cce0c"))))]
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
         col-array (one-indexed-range height)]
    (vec
      (for [x row-array]
        (vec (map #(make-block x %) col-array)))))))

