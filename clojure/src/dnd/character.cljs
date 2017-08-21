(ns dnd.character)

(defonce base-character {:char "@"
                         :x 3
                         :y 2
                         :inventory []})

(defn is-character [inhabitant] (= "@" (:char inhabitant)))

(defn move-character! [level character dx dy]
  (let [target-block (get-in @level [dx dy])]
    (if-let [target-block (not (:solid target-block))]
      (let [{ox :x oy :y} @character
            {old-inhabitants :inhabitants} (get-in @level [ox oy])
            remove-character #(assoc % :inhabitants (remove is-character old-inhabitants))
            level-without-character (swap! level update-in [ox oy] remove-character)
            target-inhabitants (:inhabitants target-block)
            updated-character (swap! character assoc :x dx :y dy)
            update-habs #(assoc % :inhabitants (conj target-inhabitants updated-character))
            updated-level (swap! level update-in [dx dy] update-habs)])
      "can't walk there")
    [level character]))


(defn push-inhabitant! [level x y item]
  (let [{target-inhabitants :inhabitants} (get-in @level [x y])
        update-habs #(assoc % :inhabitants (conj target-inhabitants item))
        updated-level (swap! level update-in [x y] update-habs)]
    level))

(defn get-item! [level character]
  (let [{:keys [x y]} @character
        {old-inhabitants :inhabitants} (get-in @level [x y])
        gotten-inhabitant (first (remove is-character old-inhabitants))

        inventory (:inventory @character)
        new-inventory (conj inventory gotten-inhabitant)
        updated-character (swap! character assoc :inventory new-inventory)

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


