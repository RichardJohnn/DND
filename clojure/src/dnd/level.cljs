(ns dnd.level
   (:require [cljs.nodejs :as nodejs]
             [dnd.character :as item :refer [base-character tree egg pickaxe]]
             [dnd.color :refer [COLOR color-name color-to-array]]
             [dnd.util :refer [coin-flip]]))

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
  (let [is-solid (> (rand) .4)
        has-tree (coin-flip)
        has-inhabitant (> (rand) .99)
        items [egg pickaxe item/sword]
        inhabitant (if is-solid
                      (if has-tree (tree) (item/stone))
                      (when has-inhabitant
                        ((rand-nth items))))
        inhabitants (if inhabitant [inhabitant] [])
        color (color-to-array
                (if-not (nil? (:bg-color inhabitant))
                  (COLOR "#b1b7b7")
                  (if (> (rand) 0.9)
                    (COLOR "#0078ff")
                    (COLOR "#0cce0c"))))]
    (assoc block
           :x x
           :y y
           :solid is-solid
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

