(ns dnd.character
  (:require [dnd.color :refer [color-name]]
            [dnd.util :refer [coin-flip]]))

(defonce base-character {:char "🐰"
                         :isPlayer false
                         :x 3
                         :y 2
                         :hp 20
                         :inventory []
                         :description "shit"
                         :view-distance 10
                         :direction "n"
                         :can-move true })

(defn is-character [inhabitant] (:isPlayer inhabitant))

(defn character-direction [dx dy]
  (if (= dx 0)
    (if (= dy 1) "s" "n")
    (if (= dx 1) "e" "w")))

(defn direction->offset [direction]
  (case direction
    "n" [ 0 -1]
    "s" [ 0  1]
    "w" [-1  0]
    "e" [ 1  0]))

(defn character->offset [character]
  (-> character
      :direction
      direction->offset))

(defn rand-rgb-vec [] (vec (repeatedly 3 #(rand-int 256))))

(defn item-maker [char color description]
  (let [name-of-color (color-name color)]
    (assoc base-character
           :id (random-uuid)
           :char char
           :color color
           :color-name name-of-color
           :description description
           :colorful-description (str description ", " name-of-color)
           )))


(defn egg [] (item-maker "●" (rand-rgb-vec)  "a nice egg"))

(defn tree [] (item-maker
                (coin-flip "🎄" "🌲 ")
                (rand-rgb-vec)
                "a happy tree"))

(defn pickaxe [] (item-maker
                   "⛏️"
                   [200 200 200]
                   "a trusty pickaxe"))

(defn sword [] (item-maker
                 "🗡"
                 [200 200 200]
                 "a pointy sword"))

(def rocks-han "䂟")
(defn rocks []
  (item-maker rocks-han [130 130 130] "rubble"))


(defn redirect-character [character dx dy]
  (assoc character :direction (character-direction dx dy)))

(defn move-character [level character dx dy]
  (if-let [target-block (get-in level [dx dy])]
    (if-let [not-solid (not (:solid target-block))]
      (let [{ox :x oy :y} character
            {old-inhabitants :inhabitants} (get-in level [ox oy])
            level-without-character (update-in level [ox oy]
                                               #(assoc % :inhabitants (remove is-character old-inhabitants)))
            target-inhabitants (:inhabitants target-block)
            updated-character (assoc character :x dx :y dy)
            update-habs #(assoc % :inhabitants (conj target-inhabitants updated-character))
            updated-level (update-in level-without-character [dx dy] update-habs)]
        [updated-level updated-character])
      [level character]) ; can't walk through solid blocks
    [level character])) ; can't walk off the board

(defn makes-solid [item]
  (-> item :char (= rocks-han)))

(defn push-inhabitant-to-block [item block]
  (let [block (if (makes-solid item) (assoc block :solid true :color "#b1b7b7") block)]
    (assoc block :inhabitants (conj (:inhabitants block) item))))

(defn push-inhabitant! [level x y item]
  (let [block (get-in @level [x y])
        update-habs (partial push-inhabitant-to-block item)
        updated-level (swap! level update-in [x y] update-habs)]
    level))

(defn get-item! [level character]
  (let [{:keys [x y inventory]} @character
        {old-inhabitants :inhabitants} (get-in @level [x y])
        gotten-inhabitant (first (remove is-character old-inhabitants))
        updated-character (when-not (nil? gotten-inhabitant)
                            (swap! character assoc :inventory
                                   (conj inventory gotten-inhabitant)))
        matcher #(= gotten-inhabitant %)
        remove-inhabitant #(assoc % :inhabitants (remove matcher old-inhabitants))
        updated-level (swap! level update-in [x y] remove-inhabitant)]
    [level character]))

(defn drop-item! [level character item]
  (let [{:keys [x y inventory]} @character
        matcher #(= item %)
        updated-character (swap! character assoc :inventory (remove matcher inventory))
        updated-level (push-inhabitant! level x y item)
        ]))


