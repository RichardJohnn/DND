(ns dnd.util)

(defn index-of
  "return the index of the supplied item, or nil"
  [v item]
  (let [len (count v)]
    (loop [i 0]
      (cond
        (<= len i)         nil,
        (= item (get v i)) i,
        :else              (recur (inc i ))))))

(defn coin-flip [heads-expr tails-expr]
  (if (> (rand) .5)
    heads-expr
    tails-expr))

