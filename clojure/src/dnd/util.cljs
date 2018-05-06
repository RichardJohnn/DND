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

(defn contain [n limit]
  (if (>= n limit)
    (rem n limit)
    (if (< n 0) (dec limit) n)))

(defn get-block-with-wrap [level x y]
  (let [width (count level)
        height (-> level first count)
        x (contain x width)
        y (contain y height)]
    (get-in level [x y])))
