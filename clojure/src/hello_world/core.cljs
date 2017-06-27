(ns hello-world.core
  (:require
    [ cljs.nodejs :as nodejs ]
    [ hello-world.show-screen :as show]
    )
  )

(def mongoose (nodejs/require "mongoose"))
(.connect mongoose "mongodb://localhost/test")
(defonce Cat (.model mongoose "cat" #js {:name js/String}))

(defn crap []
  (let [kitty (Cat. #js {:name "Interesting" :size 200 })
        promise (.save kitty)]
    (.then promise (fn [& args]
                     (println args)))))

(nodejs/enable-util-print!)

(defn -main [] (show/demo))

(set! *main-cli-fn* -main)
