(ns dnd.util)

(defn index-of
  "return the index of the supplied item, or nil"
  [v item]
  (let [len (count v)]
    (loop [i 0]
      (cond
        (<= len i)         nil,
        (= item (get v i)) i,
        :else              (recur (inc i))))))

(defn coin-flip
  ([] (> (rand) .5))
  ([heads-expr tails-expr]
   (if (coin-flip)
     heads-expr
     tails-expr)))

(defn get-width-and-height [level]
  [(count level) (-> level first count)])

(defn contain [n limit]
  (if (>= n limit)
    (rem n limit)
    (if (< n 0) (dec limit) n)))

(defn contain-x-and-y [level x y]
  (let [[ width height ] (get-width-and-height level)
        x (contain x width)
        y (contain y height)]
    [x y]))

(defn get-block-with-wrap [level x y]
  (let [[ x y ] (contain-x-and-y level x y)]
    (get-in level [x y])))

(defn give [character item])

(defn has-some [block func]
  (->> block :inhabitants (some func)))

