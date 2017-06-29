(ns hello-world.core
  (:require
    [ cljs.nodejs :as nodejs ]
    [ hello-world.show-screen :as show]
    )
  )

(nodejs/enable-util-print!)

(defn -main [] nil)

(set! *main-cli-fn* -main)
