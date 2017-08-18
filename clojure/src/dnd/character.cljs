(ns dnd.character)

(defonce character {:char "@"
                    :x 3
                    :y 2
                    :inventory [] })

(defn is-character [inhabitant] (= "@" (:char inhabitant)))

(defn move-character! [level character dx dy]
  (let [{ox :x oy :y} @character
        old-block (get-in @level [ox oy])
        old-inhabitants (:inhabitants old-block)
        remove-character #(assoc % :inhabitants (remove is-character old-inhabitants))
        level-without-character (swap! level update-in [ox oy] remove-character)
        target-block (get-in level-without-character [dx dy])
        target-inhabitants (:inhabitants target-block)
        updated-character (swap! character assoc :x dx :y dy)
        update-habs #(assoc % :inhabitants (conj target-inhabitants updated-character))
        updated-level (swap! level update-in [dx dy] update-habs)
        ]
    [level character]))

(defn get-item! [level character]
  (let [{:keys [x y]} @character
        block (get-in @level [x y])
        old-inhabitants (:inhabitants block)
        gotten-inhabitant (first (remove is-character old-inhabitants))

        inventory (:inventory @character)
        inventory-length (count inventory)
        new-inventory (assoc inventory inventory-length gotten-inhabitant)
        updated-character (swap! character assoc :inventory new-inventory)

        matcher #(= gotten-inhabitant %)
        remove-inhabitant #(assoc % :inhabitants (remove matcher old-inhabitants))
        updated-level (swap! level update-in [x y] remove-inhabitant)]
    [level character]))

(defn pushInhabitant [level x y item]
  (let [{target-inhabitants :inhabitants} (get-in @level [x y])
        update-habs #(assoc % :inhabitants (conj target-inhabitants item))
        updated-level (swap! level update-in [x y] update-habs)
        ]
    level))

(defn drop-item! [level character item]
  (let [{:keys [x y inventory]} @character
        matcher #(= item %)
        updated-character (swap! character assoc :inventory (remove matcher inventory))
        updated-level (pushInhabitant level x y item)
        ])
  )


