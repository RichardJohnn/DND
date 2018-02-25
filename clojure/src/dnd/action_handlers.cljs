(ns dnd.action-handlers
  (:require
    [dnd.character :as character :refer [direction->offset]]))

(defn attack-block [block]
  (let [{solid :solid} block]
  (if solid
    (assoc block :solid false)
    block)))

(defn attack-handler [term level character]
  (if-let [can-move (:can-move @character)]
    (let [{:keys [x y direction]} @character
          [dx dy] (direction->offset direction)
          new-x (+ x dx)
          new-y (+ y dy)
          new-level (update-in @level [new-x new-y] attack-block)]
      [new-level character])))

(defn move-handler [term level character dx dy]
  (if-let [can-move (:can-move @character)]
    (let [level @level
          character @character
          {:keys [x y]} character
          new-x (+ x dx)
          new-y (+ y dy)
          new-character (character/redirect-character character dx dy)
          [new-level new-character] (character/move-character level new-character new-x new-y)]
      [new-level new-character]
      )))

(defn get-handler [term level character]
  (let [[new-level new-character] (character/get-item! level character)]
    [new-level new-character]))

(defn drop-handler [term level character]
  (let [lastItem (last (:inventory @character))]
    (when-not (nil? lastItem)
      (character/drop-item! level character lastItem))))

