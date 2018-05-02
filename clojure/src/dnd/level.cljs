(ns dnd.level
   (:require [cljs.nodejs :as nodejs]
             [dnd.character :as item :refer [base-character tree egg pickaxe]]
             [dnd.color :refer [COLOR color-name]]
             ))

(def block {
            :x 0
            :y 0
            :inhabitants []
            :solid false
            :visible true
            })

(def width  40)
(def height 30)

(defn make-block [x y]
  (let [solid (> (rand) 0.9)
        has-inhabitant (> (rand) .9)
        has-tree (> (rand) .1)
        items [egg pickaxe item/sword]
        inhabitants (if solid
                      [(item/rocks)]
                      (if has-inhabitant
                        (if has-tree
                          [(tree)]
                          [((rand-nth items))])
                        []))
        color (if solid
                (COLOR "#b1b7b7")
                (if (> (rand) 0.9)
                  (COLOR "#0078ff")
                  (COLOR "#0cce0c")))]
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
   (let [row-array (range width)
         col-array (range height)]
    (vec
      (for [x row-array]
        (vec (map #(make-block x %) col-array)))))))

