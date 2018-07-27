(ns dnd.character
  (:require [dnd.color :refer [color-name]]
            [dnd.util :refer [coin-flip get-block-with-wrap has-some index-of]]))

(defonce base-character {:char "🐰"
                         :is-player false
                         :x 0
                         :y 0
                         :hp 20
                         :solid false
                         :inventory []
                         :description "shit"
                         :view-distance 10
                         :direction "n"
                         :can-move true })

(defn match-id [id] #(= (% :id) id))

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

(defn item-maker [char color description & key-values]
  (let [name-of-color (color-name color)
        item (assoc base-character
           :id (random-uuid)
           :char char
           :color color
           :color-name name-of-color
           :description description
           :colorful-description (str description ", " name-of-color))]
    (if (nil? key-values)
      item
      (apply assoc item key-values))))


(defn egg [] (item-maker "●" (rand-rgb-vec)  "a nice egg"))

(defn person [] (-> (item-maker
                    "☺"
                    (rand-rgb-vec)
                    "a friendly person"
                    :brain #{})))

(defn tree [] (-> (item-maker
                    (coin-flip "O" "o")
                    (rand-rgb-vec)
                    "a happy tree"
                    :solid true)))

(defn pickaxe [] (item-maker
                   "T"
                   [200 200 200]
                   "a trusty pickaxe"))

(defn sword [] (item-maker
                 "†"
                 [200 200 200]
                 "a pointy sword"))

(defn rocks []
  (item-maker "☷" [130 130 130] "rubble"))

(defn stone [] (item-maker
                 "▒"
                 [130 130 130]
                 "stone wall"
                 :solid true
                 :degrades-to (rocks)))

(defn redirect-character [character dx dy]
  (assoc character :direction (character-direction dx dy)))

(defn move-character [level character dx dy]
  (let [target-block (get-block-with-wrap level dx dy)]
    (if-let [not-solid (not (has-some target-block :solid ))]
      (let [{new-x :x new-y :y} target-block
            {ox :x oy :y id :id} character
            {old-inhabitants :inhabitants} (get-in level [ox oy])
            level-without-character (update-in level [ox oy]
                                               #(assoc % :inhabitants (remove (match-id id) old-inhabitants)))
            target-inhabitants (:inhabitants target-block)
            updated-character (assoc character :x new-x :y new-y)
            update-habs #(assoc % :inhabitants (conj target-inhabitants updated-character))
            updated-level (update-in level-without-character [new-x new-y] update-habs)]
        [updated-level updated-character])
      [level character]))) ; can't walk through solid blocks

(defn replace-inhabitant [inhabitants target new-hab]
  (if-let [index (index-of inhabitants target)]
    (assoc inhabitants index new-hab)
    inhabitants))

(defn push-inhabitant-to-block [item block]
  (assoc block :inhabitants (conj (:inhabitants block) item)))

(defn push-inhabitant [level x y item]
  (let [block (get-in level [x y])
        update-habs (partial push-inhabitant-to-block item)]
    (update-in level [x y] update-habs)))

(defn give-item
  ([character item]
   (assoc character :inventory
          (conj (:inventory character) item)))

  ;([level character item]
   ;(let [{:keys [x y]} character
         ;updated-character (give-item character item)]
     ;updated-level (update-in level [x y])))
  )

(defn get-item [level character]
  (let [{:keys [id x y inventory]} character
        {old-inhabitants :inhabitants} (get-in level [x y])
        gotten-inhabitant (first (remove (match-id id) old-inhabitants))
        updated-character (if (nil? gotten-inhabitant)
                            character
                            (give-item character gotten-inhabitant))
        matcher #(= gotten-inhabitant %)
        remove-inhabitant #(assoc % :inhabitants (remove matcher old-inhabitants))
        updated-level (update-in level [x y] remove-inhabitant)]
    [updated-level updated-character]))

(defn drop-item
  ([character item]
   (let [{:keys [inventory]} character
         matcher #(= item %)
         updated-character (assoc character :inventory (remove matcher inventory))]
     updated-character))

  ([level character item]
   (let [{:keys [x y]} character
         updated-character (drop-item character item)
         updated-level (push-inhabitant level x y item)]
     [updated-level updated-character])))

