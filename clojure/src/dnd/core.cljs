(ns dnd.core
  (:require
    [ cljs.nodejs :as nodejs ]
    [ cljs.core.async :refer [timeout] ]
    [ promesa.core :as p
     :refer [await]
     :refer-macros [alet]]
    [ promesa.async-cljs :refer-macros [async] ]
    [ dnd.level ]
    [ dnd.character :as character]
    [ dnd.character.random-name :refer [generate-name] ]
    [ dnd.showScreen :as show ]
    [ dnd.keyboard :as keyboard ]
    [ com.rpl.specter :as s
     :refer [FIRST]
     :refer-macros [select transform setval] ])
  (:require-macros
   [cljs.core.async.macros :as m :refer [go-loop]]))

(nodejs/enable-util-print!)

(defonce tkit (nodejs/require "terminal-kit"))
(defonce net (nodejs/require "net"))

(defonce clients (atom []))

(defn make-character []
  (atom
    (assoc character/base-character
           :char "@"
           :isPlayer true
           :description "hero")))

(def level (atom (dnd.level/make-level)))

(def select-values (comp vals select-keys))
(defn character-offset [character dx dy]
  (map #(reduce + %)
       (map vector [dx dy] (select-values character [:x :y]))))
(defn look [character dx dy]
  (get-in @level (character-offset character dx dy)))

(defn show-screen [term character] (show/show-screen term @level @character))

(defn show-inventory [term character] (show/show-inventory term @character))

(defn move-handler [term level character dx dy]
  (let [{:keys [x y]} @character
        new-x (+ x dx)
        new-y (+ y dy)
        _character (character/redirect-character! character dx dy)
        [_level _character] (character/move-character! level character new-x new-y)]
    ;; (show-screen term character)
    ))

(defn get-handler [term level character]
  (let [[_level _character] (character/get-item! level character)]
    (show-screen term character)))

(defn drop-handler [term level character]
  (let [lastItem (last (:inventory @character))]
    (when-not (nil? lastItem)
      (character/drop-item! level character lastItem)
      (show-screen term character))))

(def queue (atom #queue []))

(defn pusher! [& arguments]
  (swap! queue conj arguments))

(defn setup [term character]
  (doto term
    (.clear)
    (.applicationKeypad)
    (.hideCursor)
    (.grabInput #js { :mouse "button" :focus true }))

  (keyboard/HandleCharacterKeys term level character)
  (.on keyboard/emitter "move" (partial pusher! "move"))
  (.on keyboard/emitter "get"  (partial pusher! "get"))
  (.on keyboard/emitter "drop" (partial pusher! "drop"))
  (.on keyboard/emitter "inventory" (partial pusher! "inventory")))

(defn teardown [term]
  (.removeAllListeners term "key")
  (.removeAllListeners keyboard/emitter))

(defn popper! [queue]
  (go-loop [q queue]
    (when-let [next-action (peek @queue)]
      (let [action-name (first next-action)
            args        (rest next-action)
            function    (case action-name
                          "move" move-handler
                          "get"  get-handler
                          "drop" drop-handler
                          "inventory" show-inventory
                          )]

        (apply function args)
        (swap! queue pop)
        (run! #(let [{term :term character :character} %] (show-screen term character)) @clients)))

    (<! (timeout 100))
    (recur queue))
  )

;(defn herp [term client]
  ;(.on term "key"  #((let [string (str "key was " %)]
                     ;(println string)
                     ;(.send client string)
                     ;)))
  ;(.on term "data" #((let [string (str "data was " %)]
                     ;(println string)
                     ;(.send client string)
                     ;))))


(defn kick-it [client]
  (let [{:keys [term character]} client]
    (prn term)
    (teardown term)
    (setup term character)
    (show-screen term character)
    (popper! queue)))

(defn create-server []
  (doto (.createServer net
                       (fn [client]
                         (let [term (.createTerminal tkit
                                                     #js {:stdin client :stdout client})
                               new-client {:client client
                                           :term   term
                                           :character (make-character) } ]
                           (swap! clients conj new-client)
                           ;(async (swap! character assoc :name (await (generate-name))))
                           (kick-it new-client))))
    (.listen 2323)))

(defn -main []
  (if (-> (last process.argv)
          (= "server"))
    (create-server)
    (do
      (def term (.-terminal tkit))
      (kick-it term))))


(set! *main-cli-fn* -main)

(run! #(kick-it %) @clients)

;(when term (kick-it term))
