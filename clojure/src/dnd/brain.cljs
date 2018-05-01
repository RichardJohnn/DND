(ns dnd.brain
  (:require [cljs.nodejs :as nodejs]
            ;[dnd.level :refer [width height]]
            [dnd.showScreen :refer [viewable-coords]]
            ))

(defn think [inhabitant level]
  (if-let [{brain :brain} inhabitant]
    (-> inhabitant
         (viewable-coords level)
         (.log js/console viewable-coords)
         ;(run! #(draw-block term %))
         )))
