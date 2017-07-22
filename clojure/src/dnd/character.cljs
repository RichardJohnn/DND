(ns dnd.character)

(defonce character {:char "@"
                    :x 3
                    :y 2
                    :inventory [] })

(defn move-character! [level character dx dy]
  (let [{ox :x oy :y} @character
        old-block (get-in @level [ox oy])
        old-inhabitants (:inhabitants old-block)
        is-character (fn [inhabitant] (= "@" (:char inhabitant)))
        remove-character #(assoc % :inhabitants (remove is-character old-inhabitants))
        level-without-character (swap! level update-in [ox oy] remove-character)
        target-block (get-in level-without-character [dx dy])
        target-inhabitants (:inhabitants target-block)
        updated-character (swap! character assoc :x dx :y dy)
        update-habs #(assoc % :inhabitants (conj target-inhabitants updated-character))
        updated-level (swap! level update-in [dx dy] update-habs)
        ]
    [level character]))

