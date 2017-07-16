(ns dnd.character)

(def character {:char "@"
                :inventory [] })

(defn move-character [level character dx dy]
  (let [get-params [level [dx dy]]
        firstOne (apply get-in get-params)
        updated-character (assoc character :x dx :y dy)
        update-habs #(assoc firstOne :inhabitants [character])
        updated-level (apply update-in (conj get-params update-habs))]
    [updated-level updated-character]))


