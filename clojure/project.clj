(defproject dnd "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [funcool/promesa "1.9.0"]
                 [com.rpl/specter "1.0.3"]
                 ]

  ;:jvm-opts ["-Dclojure.server.repl={:port 5555 :accept clojure.core.server/repl}"]
  :jvm-opts ["--add-modules" "java.xml.bind"]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.13"]]

  :source-paths ["src" "test"]

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.13"]
                                  [com.cemerick/piggieback "0.2.2"]]
                   :source-paths ["src" "dev" "test"]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   }}


  :clean-targets ["server.js"
                  "target"]

  :hooks  [leiningen.cljsbuild]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src" "test"]
              :figwheel true
              :compiler {
                         :main dnd.core
                         :output-to "target/server_dev/dnd.js"
                         :output-dir "target/server_dev"
                         :asset-path "target/server_dev"
                         :optimizations :none
                         :target :nodejs
                         :source-map true}}
             {:id "test"
              :source-paths ["src" "test"]
              :compiler {
                         :main dnd.test-runner
                         :output-to "target/server_dev/test.js"
                         :output-dir "target/server_dev/test"
                         :asset-path "target/server_dev/test"
                         :optimizations :none
                         :target :nodejs
                         :source-map true}}
             {:id "prod"
              :source-paths ["src"]
              :compiler {
                         :output-to "server.js"
                         :output-dir "target/server_prod"
                         :target :nodejs
                         :optimizations :simple}}]})
