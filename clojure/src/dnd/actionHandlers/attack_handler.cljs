(ns dnd.actionHandlers
  (:require
    [dnd.character :as character]
    ))

(defn attack-block [block]
  (let [{solid :solid} block]
  (if solid
    (assoc block :solid false))))

(defn attack-handler [term level character]
  (if-let [can-move (:can-move @character)]
    (let [{:keys [x y direction]} @character
          [dx dy] (character/direction->offset direction)
          new-x (+ x dx)
          new-y (+ y dy)
          new-level (update-in @level [new-x new-y] attack-block)]
      [term new-level character])))

