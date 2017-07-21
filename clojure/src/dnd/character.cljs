(ns dnd.character)

(def character {:char "@"
                :x 1
                :y 2
                :inventory [] })

(defn move-character [level character dx dy]
  (let [{ox :x oy :y} character
        get-old-block [level [ox oy]]
        old-block (apply get-in get-old-block)
        old-inhabitants (:inhabitants old-block)
        remove-character (fn [old-block] (assoc old-block :inhabitants (vec (remove #(= "@" (:char %)) old-inhabitants))))
        level-without-char (apply update-in (conj get-old-block remove-character))

        level level-without-char
        get-new-block [level [dx dy]]
        target-block (apply get-in get-new-block)
        target-inhabitants (:inhabitants target-block)
        updated-character (assoc character :x dx :y dy)
        update-habs #(assoc % :inhabitants (conj target-inhabitants character))
        updated-level (apply update-in (conj get-new-block update-habs))
        ]
    [updated-level updated-character]
    ))


