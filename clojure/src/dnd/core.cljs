(ns dnd.core
  (:require
    [ cljs.nodejs :as nodejs ]
    [ cljs.core.async :refer [timeout] ]
    [ promesa.core :as p
     :refer [await]
     :refer-macros [alet]]
    [ promesa.async-cljs :refer-macros [async] ]
    [ dnd.level ]
    [ dnd.db :as db ]
    [ dnd.character :as character]
    [ dnd.character.random-name :refer [generate-name] ]
    [ dnd.action-handlers :refer [attack-handler move-handler get-handler drop-handler] ]
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
           :inventory (repeatedly 2 character/rocks)
           :description "hero")))

(def level (atom (dnd.level/make-level)))

(def select-values (comp vals select-keys))
(defn character-offset [character dx dy]
  (map #(reduce + %)
       (map vector [dx dy] (select-values character [:x :y]))))
(defn look [character dx dy]
  (get-in @level (character-offset character dx dy)))

(defn show-screen [term character] (show/show-screen term @level @character))

(defn show-inventory [term character]
  (show/show-inventory term character))

(def queue (atom #queue []))

(defn pusher! [& arguments]
  (swap! queue conj arguments))

(defonce clients (atom []))

(defn remove-client [client]
  (swap! clients (fn [fucks]
                   (remove #(= client (:client %)) fucks))))

(defn setup [term character client]
  (doto term
    (.clear)
    (.applicationKeypad)
    (.grabInput #js { :mouse "button" :focus true })
    )

  (when client
    (.on client "end" #(let [character-name (:name @character)
                             string (str "Character '" character-name "' has left the building")]
                        (remove-client client)
                        (println string)
                        )))

  (keyboard/HandleCharacterKeys term level character)
  (.on keyboard/emitter "move" (partial pusher! "move"))
  (.on keyboard/emitter "get"  (partial pusher! "get"))
  (.on keyboard/emitter "drop" (partial pusher! "drop"))
  (.on keyboard/emitter "attack" (partial pusher! "attack"))
  (.on keyboard/emitter "inventory" (partial pusher! "inventory")))

(defn teardown [term]
  (.removeAllListeners term "key")
  (.removeAllListeners keyboard/emitter))

(defn popper! [queue]
  (go-loop [q queue]
    (when-let [next-action (peek @queue)]
      (let [action-name (first next-action)
            args        (rest next-action)
            character   (nth args 2)
            function    (case action-name
                          "move" move-handler
                          "get"  get-handler
                          "drop" drop-handler
                          "attack" attack-handler
                          "inventory" show-inventory
                          )
            [new-level new-character] (apply function args)]

        (if new-level     (reset! level new-level))
        (if new-character (reset! character new-character))
        (swap! queue pop)
        (run! #(let [{:keys [term character]} %] (show-screen term character)) @clients)))

    (<! (timeout 100))
    (recur queue))
  )

(defn kick-it [client]
  (let [{:keys [term character client]} client]
    (teardown term)
    (setup term character client)
    (show-screen term character)
    (popper! queue)))

(defn create-server []
  (doto (.createServer net
                       (fn [client]
                         (let [term (doto (.createTerminal tkit
                                                      #js {:stdin   client
                                                           :stdout  client
                                                           :generic "xterm-truecolor"
                                                           :appId   "xterm-truecolor" })
                                      (aset "width" 120)
                                      (aset "height" 45))
                               character (make-character)
                               new-client {:client    client
                                           :term      term
                                           :character character}]
                           (swap! clients conj new-client)
                           (async
                             (let [dat-name (await (generate-name))]
                               (prn (str dat-name " has joined the fray."))
                               (swap! (:character new-client) assoc :name dat-name)))
                           (kick-it new-client))))
    (.listen 2323)))

(defn create-local []
  (let [term (.-terminal tkit)
        character (make-character)
        new-client {:term      term
                    :character character } ]
    (swap! clients conj new-client)
    (async
      (let [dat-name (await (generate-name))]
        (prn (str dat-name " has joined the fray."))
        (swap! (:character new-client) assoc :name dat-name)))
    (kick-it new-client)))

(defn -main []
  (if (-> (last process.argv)
          (= "server"))
    (create-server)
    (create-local)
    ))


(set! *main-cli-fn* -main)

(run! #(kick-it %) @clients)

;(when term (kick-it term))
